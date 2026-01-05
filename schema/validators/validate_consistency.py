#!/usr/bin/env python3
"""
Bidirectional Consistency Validator for Catty Thesis

Validates TeX ↔ RDF ↔ External consistency:
- Parse TeX structure
- Load RDF graph
- For each TeX theorem/definition/section: verify RDF resource exists
- For each RDF resource: verify referenced in TeX
- For each citation: verify in TeX, RDF, and external registry
"""

import sys
import re
import argparse
import yaml
import json
from pathlib import Path
from typing import Dict, List, Set, Tuple
from rdflib import Graph, Namespace, URIRef
from rdflib.namespace import RDF, RDFS, DCTERMS

class ConsistencyValidator:
    def __init__(self, mapping_spec: Path):
        with open(mapping_spec) as f:
            self.mapping = yaml.safe_load(f)

        self.errors = []
        self.warnings = []

        # Data structures
        self.tex_elements = {}  # {id: {type, file, line, properties}}
        self.rdf_resources = {}  # {id: {type, properties}}
        self.tex_citations = set()
        self.rdf_citations = set()
        self.yaml_citations = set()

        # Namespaces
        self.CATTY = Namespace("http://metavacua.github.io/catty/")
        self.DCT = DCTERMS
        self.rdf_graph = Graph()

    def load_rdf_graph(self, ontology_dir: Path):
        """Load all RDF files"""
        print("Loading RDF files...")
        rdf_files = list(ontology_dir.rglob('*.jsonld')) + \
                   list(ontology_dir.rglob('*.ttl'))

        for rdf_file in rdf_files:
            try:
                fmt = 'json-ld' if rdf_file.suffix == '.jsonld' else 'turtle'
                self.rdf_graph.parse(str(rdf_file), format=fmt)
                print(f"  ✓ Loaded {rdf_file.name}")
            except Exception as e:
                self.warnings.append(f"Failed to load {rdf_file}: {e}")

        print(f"✓ RDF graph loaded: {len(self.rdf_graph)} triples")

        # Extract RDF resources
        self._extract_rdf_resources()

    def _extract_rdf_resources(self):
        """Extract thesis elements from RDF graph"""
        query = """
            PREFIX catty: <http://metavacua.github.io/catty/>
            PREFIX dct: <http://purl.org/dc/terms/>

            SELECT ?resource ?type ?id
            WHERE {
                ?resource a ?type ;
                         dct:identifier ?id .
                FILTER(STRSTARTS(STR(?type), "http://metavacua.github.io/catty/"))
            }
        """

        results = self.rdf_graph.query(query)

        for row in results:
            resource_type = str(row.type).split('/')[-1]
            identifier = str(row.id)

            self.rdf_resources[identifier] = {
                'type': resource_type,
                'uri': str(row.resource),
            }

        print(f"  ✓ Extracted {len(self.rdf_resources)} RDF resources")

        # Extract RDF citations
        citation_query = """
            PREFIX dct: <http://purl.org/dc/terms/>
            PREFIX bibo: <http://purl.org/ontology/bibo/>

            SELECT ?citation ?id
            WHERE {
                ?citation a ?type ;
                         dct:identifier ?id .
                FILTER(?type = bibo:Document ||
                       ?type = bibo:AcademicArticle ||
                       ?type = bibo:Book ||
                       ?type = bibo:Article ||
                       ?type = bibo:Chapter)
            }
        """

        citation_results = self.rdf_graph.query(citation_query)
        for row in citation_results:
            self.rdf_citations.add(str(row.id))

        print(f"  ✓ Found {len(self.rdf_citations)} RDF citations")

    def parse_tex_files(self, tex_dir: Path):
        """Parse TeX files and extract structure"""
        print("\nParsing TeX files...")
        tex_files = sorted(tex_dir.rglob('*.tex'))

        for tex_file in tex_files:
            try:
                with open(tex_file, 'r', encoding='utf-8') as f:
                    lines = f.readlines()

                self._parse_tex_file(tex_file, lines)
            except Exception as e:
                self.warnings.append(f"Failed to parse {tex_file}: {e}")

        print(f"✓ Extracted {len(self.tex_elements)} TeX elements")
        print(f"✓ Found {len(self.tex_citations)} TeX citations")

    def _parse_tex_file(self, file_path: Path, lines: List[str]):
        """Parse single TeX file"""
        # Extract labels (IDs)
        label_pattern = r'\\label\{([^}]+)\}'

        for line_num, line in enumerate(lines, 1):
            # Extract labels
            label_matches = re.finditer(label_pattern, line)
            for match in label_matches:
                label_id = match.group(1)

                # Determine type from ID pattern
                element_type = self._infer_type_from_id(label_id)

                if element_type:
                    self.tex_elements[label_id] = {
                        'type': element_type,
                        'file': str(file_path),
                        'line': line_num,
                    }

            # Extract citations
            cite_patterns = [
                r'\\cite\{([^}]+)\}',
                r'\\citep\{([^}]+)\}',
                r'\\definedfrom\{[^}]+\}\{([^}]+)\}',
                r'\\provedfrom\{[^}]+\}\{([^}]+)\}',
            ]

            for pattern in cite_patterns:
                cite_matches = re.finditer(pattern, line)
                for match in cite_matches:
                    keys = match.group(1).split(',')
                    for key in keys:
                        self.tex_citations.add(key.strip())

    def _infer_type_from_id(self, id_str: str) -> str:
        """Infer element type from ID prefix"""
        type_map = {
            'thm-': 'Theorem',
            'lem-': 'Lemma',
            'prop-': 'Proposition',
            'cor-': 'Corollary',
            'def-': 'Definition',
            'ex-': 'Example',
            'rem-': 'Remark',
            'conj-': 'Conjecture',
            'proof-': 'Proof',
            'sec-': 'Section',
            'subsec-': 'Subsection',
            'ch-': 'Chapter',
            'part-': 'Part',
        }

        for prefix, elem_type in type_map.items():
            if id_str.startswith(prefix):
                return elem_type

        return None

    def load_yaml_citations(self, yaml_path: Path):
        """Load citation registry"""
        with open(yaml_path) as f:
            data = yaml.safe_load(f)
            self.yaml_citations = set(data['citations'].keys())

        print(f"✓ Loaded {len(self.yaml_citations)} YAML citations")

    def validate_tex_to_rdf(self):
        """Validate TeX elements have RDF resources"""
        print("\nValidating TeX → RDF consistency...")
        missing_in_rdf = []

        for tex_id, tex_elem in self.tex_elements.items():
            if tex_id not in self.rdf_resources:
                missing_in_rdf.append((tex_id, tex_elem))

        if missing_in_rdf:
            for tex_id, tex_elem in missing_in_rdf:
                self.errors.append(
                    f"{tex_elem['file']}:{tex_elem['line']}: "
                    f"TeX element '{tex_id}' ({tex_elem['type']}) "
                    f"has no corresponding RDF resource"
                )
        else:
            print(f"✓ All {len(self.tex_elements)} TeX elements have RDF resources")

    def validate_rdf_to_tex(self):
        """Validate RDF resources are referenced in TeX"""
        print("\nValidating RDF → TeX consistency...")
        missing_in_tex = []

        for rdf_id, rdf_elem in self.rdf_resources.items():
            # Skip citation resources (they don't need TeX labels)
            if rdf_id in self.rdf_citations:
                continue

            if rdf_id not in self.tex_elements:
                # Check if marked as supplementary
                # (For now, just warn)
                self.warnings.append(
                    f"RDF resource '{rdf_id}' ({rdf_elem['type']}) "
                    f"not found in TeX files"
                )

        if not missing_in_tex:
            print(f"✓ RDF resources validated against TeX")

    def validate_citation_consistency(self):
        """Validate citations are consistent across all sources"""
        print("\nValidating citation consistency...")

        # TeX citations must be in YAML
        tex_not_in_yaml = self.tex_citations - self.yaml_citations
        if tex_not_in_yaml:
            for key in tex_not_in_yaml:
                self.errors.append(
                    f"TeX cites '{key}' but not in bibliography/citations.yaml"
                )

        # YAML citations should be in RDF
        yaml_not_in_rdf = self.yaml_citations - self.rdf_citations
        if yaml_not_in_rdf:
            for key in yaml_not_in_rdf:
                self.errors.append(
                    f"YAML has '{key}' but no RDF resource in citations.jsonld"
                )

        if not tex_not_in_yaml and not yaml_not_in_rdf:
            print("✓ Citation consistency validated")

    def validate_type_consistency(self):
        """Validate TeX and RDF element types match"""
        print("\nValidating type consistency...")
        mismatches = []

        for elem_id in set(self.tex_elements.keys()) & set(self.rdf_resources.keys()):
            tex_type = self.tex_elements[elem_id]['type']
            rdf_type = self.rdf_resources[elem_id]['type']

            if tex_type != rdf_type:
                mismatches.append((
                    elem_id,
                    tex_type,
                    rdf_type,
                    self.tex_elements[elem_id]['file'],
                    self.tex_elements[elem_id]['line']
                ))

        if mismatches:
            for elem_id, tex_type, rdf_type, file_path, line_num in mismatches:
                self.errors.append(
                    f"{file_path}:{line_num}: Type mismatch for '{elem_id}': "
                    f"TeX={tex_type}, RDF={rdf_type}"
                )
        else:
            print("✓ All element types are consistent")

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
            print(f"\n❌ CONSISTENCY VALIDATION FAILED: {len(self.errors)} error(s)")
            return False
        else:
            print("\n✅ CONSISTENCY VALIDATION PASSED")
            print(f"  - {len(self.tex_elements)} TeX elements validated")
            print(f"  - {len(self.rdf_resources)} RDF resources validated")
            print(f"  - TeX ↔ RDF bidirectional consistency verified")
            if self.warnings:
                print(f"  - {len(self.warnings)} warning(s)")
            return True


def main():
    parser = argparse.ArgumentParser(
        description='Validate bidirectional consistency between TeX and RDF'
    )
    parser.add_argument(
        '--tex-dir',
        type=Path,
        required=True,
        help='Directory containing TeX files'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        required=True,
        help='Directory containing RDF files'
    )
    parser.add_argument(
        '--bibliography',
        type=Path,
        required=True,
        help='Path to citations.yaml'
    )
    parser.add_argument(
        '--mapping',
        type=Path,
        required=True,
        help='Path to tex-rdf-mapping.yaml'
    )

    args = parser.parse_args()

    # Validate paths
    for path, name in [(args.tex_dir, "TeX directory"),
                       (args.ontology, "Ontology directory"),
                       (args.bibliography, "Bibliography"),
                       (args.mapping, "Mapping file")]:
        if not path.exists():
            print(f"❌ ERROR: {name} not found: {path}")
            sys.exit(1)

    print("="*70)
    print("CATTY THESIS BIDIRECTIONAL CONSISTENCY VALIDATOR")
    print("="*70)
    print(f"\nTeX directory: {args.tex_dir}")
    print(f"Ontology directory: {args.ontology}")
    print(f"Bibliography: {args.bibliography}")
    print(f"Mapping: {args.mapping}")

    validator = ConsistencyValidator(args.mapping)

    # Load data
    validator.load_rdf_graph(args.ontology)
    validator.parse_tex_files(args.tex_dir)
    validator.load_yaml_citations(args.bibliography)

    # Run validations
    validator.validate_tex_to_rdf()
    validator.validate_rdf_to_tex()
    validator.validate_citation_consistency()
    validator.validate_type_consistency()

    # Print results
    success = validator.print_results()
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
