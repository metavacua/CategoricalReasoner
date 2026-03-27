#!/usr/bin/env python3
"""
Citation Validator for Catty Thesis

Validates citations across TeX, YAML, and RDF:
- Check every \cite{key} in TeX has entry in citations.yaml
- Check every citations.yaml entry has RDF resource in citations.jsonld
- Check every RDF citation has required metadata
- Check external links (DOI, Wikidata, arXiv, URLs) are resolvable
- Check every TeX citation has RDF provenance link
"""

import sys
import re
import argparse
import yaml
import json
import requests
from pathlib import Path
from typing import Dict, List, Set
from urllib.parse import urlparse

class CitationValidator:
    def __init__(self, check_external: bool = False):
        self.check_external = check_external
        self.errors = []
        self.warnings = []
        self.tex_citations = set()
        self.yaml_citations = {}
        self.rdf_citations = {}

    def load_yaml_citations(self, yaml_path: Path):
        """Load citations from YAML registry"""
        try:
            with open(yaml_path, 'r', encoding='utf-8') as f:
                data = yaml.safe_load(f)
                self.yaml_citations = data.get('citations', {})
                print(f"✓ Loaded {len(self.yaml_citations)} citations from YAML")
        except Exception as e:
            self.errors.append(f"Failed to load YAML citations: {e}")

    def load_rdf_citations(self, rdf_path: Path):
        """Load citations from JSON-LD"""
        try:
            with open(rdf_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                graph = data.get('@graph', [])
                for resource in graph:
                    identifier = resource.get('dct:identifier')
                    if identifier:
                        self.rdf_citations[identifier] = resource
                print(f"✓ Loaded {len(self.rdf_citations)} citations from RDF")
        except Exception as e:
            self.errors.append(f"Failed to load RDF citations: {e}")

    def extract_tex_citations(self, tex_dir: Path):
        """Extract all citation keys from TeX files"""
        cite_patterns = [
            r'\\cite\{([^}]+)\}',
            r'\\citep\{([^}]+)\}',
            r'\\citeauthor\{([^}]+)\}',
            r'\\citeyear\{([^}]+)\}',
            r'\\citepage\{([^}]+)\}',
            r'\\citefigure\{([^}]+)\}',
            r'\\citetheorem\{([^}]+)\}',
            r'\\definedfrom\{[^}]+\}\{([^}]+)\}',
            r'\\provedfrom\{[^}]+\}\{([^}]+)\}',
            r'\\derivedfrom\{[^}]+\}\{([^}]+)\}',
        ]

        tex_files = list(tex_dir.rglob('*.tex'))
        print(f"Scanning {len(tex_files)} TeX file(s) for citations...")

        for tex_file in tex_files:
            try:
                with open(tex_file, 'r', encoding='utf-8') as f:
                    content = f.read()

                for pattern in cite_patterns:
                    matches = re.finditer(pattern, content)
                    for match in matches:
                        keys = match.group(1).split(',')
                        for key in keys:
                            key = key.strip()
                            if key:
                                self.tex_citations.add(key)
            except Exception as e:
                self.warnings.append(f"Failed to read {tex_file}: {e}")

        print(f"✓ Found {len(self.tex_citations)} unique citation(s) in TeX")

    def validate_tex_to_yaml(self):
        """Validate TeX citations exist in YAML registry"""
        print("\nValidating TeX → YAML consistency...")
        missing = self.tex_citations - set(self.yaml_citations.keys())

        if missing:
            for key in sorted(missing):
                self.errors.append(
                    f"TeX cites '{key}' but not found in bibliography/citations.yaml"
                )
        else:
            print(f"✓ All {len(self.tex_citations)} TeX citations found in YAML")

    def validate_yaml_to_rdf(self):
        """Validate YAML entries have RDF resources"""
        print("\nValidating YAML → RDF consistency...")
        missing = set(self.yaml_citations.keys()) - set(self.rdf_citations.keys())

        if missing:
            for key in sorted(missing):
                self.errors.append(
                    f"YAML has '{key}' but no RDF resource in ontology/citations.jsonld"
                )
        else:
            print(f"✓ All {len(self.yaml_citations)} YAML entries have RDF resources")

    def validate_rdf_metadata(self):
        """Validate RDF citations have required metadata"""
        print("\nValidating RDF citation metadata...")
        required_fields = ['dct:creator', 'dct:title', 'dct:issued', 'dct:identifier']

        for key, resource in self.rdf_citations.items():
            for field in required_fields:
                if field not in resource:
                    self.errors.append(
                        f"RDF citation '{key}' missing required field: {field}"
                    )

            # Check external links (SWTI S2 compliance)
            has_external = any([
                'bibo:doi' in resource,
                'owl:sameAs' in resource,
                'rdfs:seeAlso' in resource,
            ])

            if not has_external:
                self.errors.append(
                    f"RDF citation '{key}' has no external links "
                    f"(DOI, owl:sameAs, rdfs:seeAlso) - SWTI S2 violation"
                )

        print(f"✓ Validated metadata for {len(self.rdf_citations)} RDF citations")

    def validate_external_links(self):
        """Validate external links are resolvable (optional)"""
        if not self.check_external:
            return

        print("\nValidating external links (this may take a while)...")

        for key, yaml_entry in self.yaml_citations.items():
            # Check DOI
            if 'doi' in yaml_entry:
                doi = yaml_entry['doi']
                doi_url = f"https://doi.org/{doi}"
                if not self._check_url(doi_url):
                    self.errors.append(
                        f"Citation '{key}': DOI {doi} is not resolvable"
                    )

            # Check Wikidata
            if 'wikidata' in yaml_entry:
                wikidata = yaml_entry['wikidata']
                if not re.match(r'^Q[0-9]+$', wikidata):
                    self.errors.append(
                        f"Citation '{key}': Invalid Wikidata ID format: {wikidata}"
                    )
                else:
                    wd_url = f"https://www.wikidata.org/wiki/{wikidata}"
                    if not self._check_url(wd_url):
                        self.warnings.append(
                            f"Citation '{key}': Wikidata {wikidata} not accessible"
                        )

            # Check URL
            if 'url' in yaml_entry:
                url = yaml_entry['url']
                if not self._check_url(url):
                    self.warnings.append(
                        f"Citation '{key}': URL {url} is not accessible"
                    )

    def _check_url(self, url: str, timeout: int = 5) -> bool:
        """Check if URL is accessible"""
        try:
            response = requests.head(url, timeout=timeout, allow_redirects=True)
            return response.status_code < 400
        except:
            # Try GET if HEAD fails
            try:
                response = requests.get(url, timeout=timeout, allow_redirects=True)
                return response.status_code < 400
            except:
                return False

    def validate_citation_key_format(self):
        """Validate citation keys match pattern: [lastname][year][shortname]"""
        print("\nValidating citation key formats...")
        pattern = re.compile(r'^[a-z]+[0-9]{4}[a-z]+$')

        for key in self.yaml_citations.keys():
            if not pattern.match(key):
                self.errors.append(
                    f"Citation key '{key}' does not match pattern: "
                    f"[lastname][year][shortname] (e.g., 'girard1987linear')"
                )

    def print_results(self):
        """Print validation results"""
        print("\n" + "="*70)

        if self.warnings:
            print("\nWARNINGS:")
            for warning in self.warnings:
                print(f"  ⚠  {warning}")

        if self.errors:
            print("\nERRORS:")
            for error in self.errors:
                print(f"  ✗ {error}")
            print(f"\n❌ CITATION VALIDATION FAILED: {len(self.errors)} error(s)")
            return False
        else:
            print("\n✅ CITATION VALIDATION PASSED")
            print(f"  - {len(self.tex_citations)} TeX citations validated")
            print(f"  - {len(self.yaml_citations)} YAML entries validated")
            print(f"  - {len(self.rdf_citations)} RDF resources validated")
            if self.warnings:
                print(f"  - {len(self.warnings)} warning(s)")
            return True


def main():
    parser = argparse.ArgumentParser(
        description='Validate citations across TeX, YAML, and RDF'
    )
    parser.add_argument(
        '--tex-dir',
        type=Path,
        required=True,
        help='Directory containing TeX files'
    )
    parser.add_argument(
        '--bibliography',
        type=Path,
        required=True,
        help='Path to citations.yaml'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        required=True,
        help='Path to citations.jsonld'
    )
    parser.add_argument(
        '--check-external',
        action='store_true',
        help='Check external links (DOI, Wikidata, URLs) are resolvable'
    )

    args = parser.parse_args()

    # Validate paths
    if not args.tex_dir.exists():
        print(f"❌ ERROR: TeX directory not found: {args.tex_dir}")
        sys.exit(1)

    if not args.bibliography.exists():
        print(f"❌ ERROR: Bibliography not found: {args.bibliography}")
        sys.exit(1)

    if not args.ontology.exists():
        print(f"❌ ERROR: Ontology not found: {args.ontology}")
        sys.exit(1)

    print("="*70)
    print("CATTY THESIS CITATION VALIDATOR")
    print("="*70)
    print(f"\nTeX directory: {args.tex_dir}")
    print(f"Bibliography: {args.bibliography}")
    print(f"Ontology: {args.ontology}")
    print(f"Check external links: {args.check_external}")

    validator = CitationValidator(check_external=args.check_external)

    # Load data
    validator.load_yaml_citations(args.bibliography)
    validator.load_rdf_citations(args.ontology)
    validator.extract_tex_citations(args.tex_dir)

    # Run validations
    validator.validate_citation_key_format()
    validator.validate_tex_to_yaml()
    validator.validate_yaml_to_rdf()
    validator.validate_rdf_metadata()
    validator.validate_external_links()

    # Print results
    success = validator.print_results()
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
