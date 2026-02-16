#!/usr/bin/env python3
"""
Citation Validator for Catty Thesis
Validates citations across TeX files, YAML registry, and RDF resources
"""

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Set, Tuple
from dataclasses import dataclass
from urllib.request import urlopen
from urllib.error import URLError, HTTPError
import json
from http.client import HTTPException

# Try to import required libraries
try:
    import yaml
except ImportError:
    print("ERROR: PyYAML is required. Install with: pip install pyyaml")
    sys.exit(1)

try:
    from rdflib import Graph, URIRef, Literal
    from rdflib.namespace import RDF, RDFS, DC
except ImportError:
    print("ERROR: rdflib is required. Install with: pip install rdflib")
    sys.exit(1)


@dataclass
class Citation:
    """Represents a citation from the registry"""
    key: str
    author: str
    title: str
    year: int
    citation_type: str
    doi: str = None
    arxiv: str = None
    url: str = None
    wikidata: str = None
    dbpedia: str = None
    local_ontology: bool = False
    notes: str = None


@dataclass
class ValidationError:
    """Represents a validation error"""
    file: str
    line: int
    message: str
    severity: str = "ERROR"


class CitationValidator:
    """Validates citations across TeX, YAML, and RDF"""

    def __init__(self, check_external: bool = False):
        self.errors: List[ValidationError] = []
        self.warnings: List[ValidationError] = []
        self.citations: Dict[str, Citation] = {}
        self.rdf_citations: Set[str] = set()
        self.tex_citations: Set[Tuple[str, int]] = []  # (key, line_number)
        self.check_external = check_external

    def load_citation_registry(self, registry_file: Path) -> bool:
        """Load the YAML citation registry"""
        try:
            with open(registry_file, 'r') as f:
                data = yaml.safe_load(f)

            for key, citation_data in data['citations'].items():
                self.citations[key] = Citation(
                    key=key,
                    author=citation_data['author'],
                    title=citation_data['title'],
                    year=citation_data['year'],
                    citation_type=citation_data['type'],
                    doi=citation_data.get('doi'),
                    arxiv=citation_data.get('arxiv'),
                    url=citation_data.get('url'),
                    wikidata=citation_data.get('wikidata'),
                    dbpedia=citation_data.get('dbpedia'),
                    local_ontology=citation_data.get('local_ontology', False),
                    notes=citation_data.get('notes')
                )

            print(f"Loaded {len(self.citations)} citations from registry")
            return True

        except FileNotFoundError:
            self.errors.append(ValidationError(
                file=str(registry_file), line=0,
                message=f"Citation registry not found: {registry_file}",
                severity="FATAL"
            ))
            return False
        except yaml.YAMLError as e:
            self.errors.append(ValidationError(
                file=str(registry_file), line=0,
                message=f"Invalid YAML in citation registry: {e}",
                severity="FATAL"
            ))
            return False

    def load_rdf_citations(self, rdf_file: Path) -> bool:
        """Load RDF citation resources"""
        try:
            g = Graph()
            g.parse(rdf_file, format='json-ld')

            # Extract all citation identifiers
            for s in g.subjects(None, None):
                # Look for dct:identifier property
                identifiers = list(g.objects(s, URIRef('http://purl.org/dc/terms/identifier')))
                if identifiers:
                    citation_id = str(identifiers[0])
                    self.rdf_citations.add(citation_id)

            print(f"Loaded {len(self.rdf_citations)} RDF citations")
            return True

        except FileNotFoundError:
            self.errors.append(ValidationError(
                file=str(rdf_file), line=0,
                message=f"RDF citation file not found: {rdf_file}",
                severity="FATAL"
            ))
            return False
        except Exception as e:
            self.errors.append(ValidationError(
                file=str(rdf_file), line=0,
                message=f"Error parsing RDF: {e}",
                severity="FATAL"
            ))
            return False

    def parse_tex_citations(self, tex_dir: Path):
        """Parse citation keys from TeX files"""
        tex_files = list(tex_dir.glob('*.tex'))

        for tex_file in tex_files:
            with open(tex_file, 'r', encoding='utf-8') as f:
                lines = f.readlines()

            for i, line in enumerate(lines, start=1):
                # Find \cite{key} patterns
                cite_matches = re.finditer(r'\\cite\{([^}]+)\}', line)
                for match in cite_matches:
                    keys = match.group(1).split(',')
                    for key in keys:
                        key = key.strip()
                        if key:
                            self.tex_citations.append((key, i))

                # Find \citepage{key}{page} patterns
                page_matches = re.finditer(r'\\citepage\{([^}]+)\}', line)
                for match in page_matches:
                    key = match.group(1).strip()
                    if key:
                        self.tex_citations.append((key, i))

                # Find \citefigure{key}{figure} patterns
                fig_matches = re.finditer(r'\\citefigure\{([^}]+)\}', line)
                for match in fig_matches:
                    key = match.group(1).strip()
                    if key:
                        self.tex_citations.append((key, i))

        print(f"Parsed {len(self.tex_citations)} citations from TeX files")

    def validate_tex_citations(self, tex_dir: Path):
        """Validate all TeX citations exist in registry"""
        print("\nValidating TeX citations against registry...")

        missing = set()
        for key, line in self.tex_citations:
            if key not in self.citations:
                missing.add(key)

        if missing:
            for key in sorted(missing):
                # Find first occurrence
                for cite_key, cite_line in self.tex_citations:
                    if cite_key == key:
                        self.errors.append(ValidationError(
                            file=str(tex_dir),
                            line=cite_line,
                            message=f"Citation '{key}' not found in docs/dissertation/bibliography/citations.yaml",
                            severity="ERROR"
                        ))
                        break
        else:
            print("  ✓ All TeX citations exist in registry")

    def validate_rdf_consistency(self):
        """Validate RDF citations match YAML registry"""
        print("\nValidating RDF citations against YAML registry...")

        yaml_keys = set(self.citations.keys())

        # Check for missing RDF entries
        missing_rdf = yaml_keys - self.rdf_citations
        if missing_rdf:
            for key in sorted(missing_rdf):
                self.errors.append(ValidationError(
                    file="src/ontology/citations.jsonld", line=0,
                    message=f"Citation '{key}' exists in YAML but not in RDF",
                    severity="ERROR"
                ))
        else:
            print("  ✓ All YAML citations have RDF entries")

        # Check for extra RDF entries
        extra_rdf = self.rdf_citations - yaml_keys
        if extra_rdf:
            for key in sorted(extra_rdf):
                self.errors.append(ValidationError(
                    file="docs/dissertation/bibliography/citations.yaml", line=0,
                    message=f"Citation '{key}' exists in RDF but not in YAML",
                    severity="ERROR"
                ))
        else:
            print("  ✓ All RDF citations have YAML entries")

    def check_external_links(self):
        """Check if external links (DOI, URLs, Wikidata) are resolvable"""
        if not self.check_external:
            print("\nSkipping external link checks (use --check-external to enable)")
            return

        print("\nChecking external links...")

        for key, citation in self.citations.items():
            # Check DOI
            if citation.doi:
                if not self.check_url(f"https://doi.org/{citation.doi}"):
                    self.errors.append(ValidationError(
                        file="docs/dissertation/bibliography/citations.yaml", line=0,
                        message=f"DOI for '{key}' ({citation.doi}) is not resolvable",
                        severity="ERROR"
                    ))

            # Check URL
            if citation.url and not citation.url.startswith('https://arxiv.org'):
                if not self.check_url(citation.url):
                    self.errors.append(ValidationError(
                        file="docs/dissertation/bibliography/citations.yaml", line=0,
                        message=f"URL for '{key}' ({citation.url}) is not resolvable",
                        severity="ERROR"
                    ))

            # Check arXiv
            if citation.arxiv:
                if not self.check_url(f"https://arxiv.org/abs/{citation.arxiv}"):
                    self.errors.append(ValidationError(
                        file="docs/dissertation/bibliography/citations.yaml", line=0,
                        message=f"arXiv ID for '{key}' ({citation.arxiv}) is not resolvable",
                        severity="ERROR"
                    ))

            # Check Wikidata
            if citation.wikidata:
                if not self.check_url(f"https://www.wikidata.org/wiki/{citation.wikidata}"):
                    self.errors.append(ValidationError(
                        file="docs/dissertation/bibliography/citations.yaml", line=0,
                        message=f"Wikidata ID for '{key}' ({citation.wikidata}) is not resolvable",
                        severity="ERROR"
                    ))

        print("  ✓ External link check complete")

    def check_url(self, url: str) -> bool:
        """Check if a URL is resolvable"""
        try:
            with urlopen(url, timeout=5) as response:
                return response.status == 200
        except (URLError, HTTPError, HTTPException, Exception):
            return False

    def validate_metadata(self):
        """Validate all citations have required metadata"""
        print("\nValidating citation metadata...")

        for key, citation in self.citations.items():
            # Check required fields
            if not citation.author:
                self.errors.append(ValidationError(
                    file="docs/dissertation/bibliography/citations.yaml", line=0,
                    message=f"Citation '{key}' missing author",
                    severity="ERROR"
                ))

            if not citation.title:
                self.errors.append(ValidationError(
                    file="docs/dissertation/bibliography/citations.yaml", line=0,
                    message=f"Citation '{key}' missing title",
                    severity="ERROR"
                ))

            if not citation.year:
                self.errors.append(ValidationError(
                    file="docs/dissertation/bibliography/citations.yaml", line=0,
                    message=f"Citation '{key}' missing year",
                    severity="ERROR"
                ))

            # Check at least one external identifier
            if not any([citation.doi, citation.arxiv, citation.url,
                       citation.wikidata, citation.dbpedia, citation.local_ontology]):
                self.errors.append(ValidationError(
                    file="docs/dissertation/bibliography/citations.yaml", line=0,
                    message=f"Citation '{key}' missing external identifier (DOI, arXiv, URL, Wikidata, DBpedia, or local_ontology)",
                    severity="ERROR"
                ))

            # Check citation type
            valid_types = ['journal', 'book', 'conference', 'preprint', 'website', 'other', 'chapter']
            if citation.citation_type not in valid_types:
                self.errors.append(ValidationError(
                    file="docs/dissertation/bibliography/citations.yaml", line=0,
                    message=f"Citation '{key}' has invalid type '{citation.citation_type}'",
                    severity="ERROR"
                ))

        print("  ✓ Metadata validation complete")

    def validate(self, tex_dir: Path, registry_file: Path, rdf_file: Path) -> bool:
        """Run full citation validation"""
        print("=" * 60)
        print("Citation Validator for Catty Thesis")
        print("=" * 60)

        # Load citations
        if not self.load_citation_registry(registry_file):
            return False

        if not self.load_rdf_citations(rdf_file):
            return False

        # Parse TeX citations
        self.parse_tex_citations(tex_dir)

        # Run validations
        self.validate_tex_citations(tex_dir)
        self.validate_rdf_consistency()
        self.validate_metadata()
        self.check_external_links()

        return len(self.errors) == 0

    def print_errors(self):
        """Print all validation errors"""
        if not self.errors:
            print("\n✓ No citation errors found")
            return

        print(f"\n✗ Found {len(self.errors)} citation errors:")

        for error in self.errors:
            print(f"\n  {error.severity}: {error.file}:{error.line}")
            print(f"    {error.message}")


def main():
    parser = argparse.ArgumentParser(
        description='Validate citations across TeX, YAML registry, and RDF'
    )
    parser.add_argument(
        '--tex-dir',
        type=Path,
        default=Path('docs/dissertation/chapters'),
        help='Directory containing TeX files'
    )
    parser.add_argument(
        '--bibliography',
        type=Path,
        default=Path('docs/dissertation/bibliography/citations.yaml'),
        help='Path to citation registry YAML file'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        default=Path('docs/dissertation/bibliography/citations.jsonld'),
        help='Path to RDF citations file'
    )
    parser.add_argument(
        '--check-external',
        action='store_true',
        help='Check if external links (DOI, URLs) are resolvable'
    )

    args = parser.parse_args()

    # Create validator
    validator = CitationValidator(check_external=args.check_external)

    # Validate
    success = validator.validate(args.tex_dir, args.bibliography, args.ontology)

    # Print results
    validator.print_errors()

    # Exit with appropriate code
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
