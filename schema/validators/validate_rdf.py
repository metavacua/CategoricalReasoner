#!/usr/bin/env python3
"""
RDF/SHACL Validator for Catty Thesis

Uses pyshacl to validate RDF against SHACL shapes:
- Load all RDF files in ontology/
- Validate against shapes in catty-thesis-shapes.shacl
- Check thesis-specific constraints
- Output: PASS/FAIL with violations
"""

import sys
import argparse
from pathlib import Path
from rdflib import Graph
try:
    from pyshacl import validate
    PYSHACL_AVAILABLE = True
except ImportError:
    PYSHACL_AVAILABLE = False
    print("WARNING: pyshacl not installed. Run: pip install pyshacl")

class RDFValidator:
    def __init__(self):
        self.errors = []
        self.warnings = []
        self.data_graph = Graph()
        self.shapes_graph = Graph()

    def load_ontology_files(self, ontology_dir: Path):
        """Load all RDF files from ontology directory"""
        rdf_extensions = ['.jsonld', '.ttl', '.rdf', '.n3']
        rdf_files = []

        for ext in rdf_extensions:
            rdf_files.extend(ontology_dir.rglob(f'*{ext}'))

        if not rdf_files:
            self.warnings.append(f"No RDF files found in {ontology_dir}")
            return

        print(f"Loading {len(rdf_files)} RDF file(s)...")

        for rdf_file in rdf_files:
            try:
                # Determine format
                if rdf_file.suffix == '.jsonld':
                    fmt = 'json-ld'
                elif rdf_file.suffix == '.ttl':
                    fmt = 'turtle'
                elif rdf_file.suffix == '.n3':
                    fmt = 'n3'
                else:
                    fmt = 'xml'

                self.data_graph.parse(str(rdf_file), format=fmt)
                print(f"  ✓ Loaded {rdf_file.name}")
            except Exception as e:
                self.errors.append(f"Failed to load {rdf_file}: {e}")

        print(f"✓ Data graph loaded: {len(self.data_graph)} triple(s)")

    def load_shapes(self, shapes_file: Path):
        """Load SHACL shapes"""
        try:
            self.shapes_graph.parse(str(shapes_file), format='turtle')
            print(f"✓ Shapes loaded: {len(self.shapes_graph)} triple(s)")
        except Exception as e:
            self.errors.append(f"Failed to load shapes: {e}")

    def validate_with_shacl(self):
        """Run SHACL validation"""
        if not PYSHACL_AVAILABLE:
            self.errors.append(
                "pyshacl library not available. "
                "Install with: pip install pyshacl"
            )
            return False

        if len(self.data_graph) == 0:
            self.errors.append("Data graph is empty")
            return False

        if len(self.shapes_graph) == 0:
            self.errors.append("Shapes graph is empty")
            return False

        print("\nRunning SHACL validation...")

        try:
            conforms, results_graph, results_text = validate(
                self.data_graph,
                shacl_graph=self.shapes_graph,
                inference='rdfs',
                abort_on_first=False,
                meta_shacl=False,
                advanced=True,
                js=False,
            )

            if conforms:
                print("✓ SHACL validation passed")
                return True
            else:
                print("✗ SHACL validation failed")
                print("\nViolation Report:")
                print(results_text)
                self.errors.append("SHACL constraint violations found (see report above)")
                return False

        except Exception as e:
            self.errors.append(f"SHACL validation error: {e}")
            return False

    def validate_id_uniqueness(self):
        """Check that all dct:identifier values are unique"""
        print("\nValidating ID uniqueness...")

        query = """
            PREFIX dct: <http://purl.org/dc/terms/>

            SELECT ?id (COUNT(?entity) AS ?count)
            WHERE {
                ?entity dct:identifier ?id .
            }
            GROUP BY ?id
            HAVING (?count > 1)
        """

        results = list(self.data_graph.query(query))

        if results:
            for row in results:
                self.errors.append(
                    f"Duplicate identifier '{row.id}' found {row.count} times"
                )
            return False
        else:
            print("✓ All identifiers are unique")
            return True

    def validate_citations_exist(self):
        """Check that all dct:references point to existing citations"""
        print("\nValidating citation references...")

        query = """
            PREFIX dct: <http://purl.org/dc/terms/>
            PREFIX bibo: <http://purl.org/ontology/bibo/>

            SELECT ?entity ?citation
            WHERE {
                ?entity dct:references ?citation .
                FILTER NOT EXISTS {
                    ?citation a ?type .
                    FILTER(?type = bibo:Document ||
                           ?type = bibo:AcademicArticle ||
                           ?type = bibo:Book)
                }
            }
        """

        results = list(self.data_graph.query(query))

        if results:
            for row in results:
                self.errors.append(
                    f"Entity {row.entity} references non-existent citation {row.citation}"
                )
            return False
        else:
            print("✓ All citation references are valid")
            return True

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
            print(f"\n❌ RDF VALIDATION FAILED: {len(self.errors)} error(s)")
            return False
        else:
            print("\n✅ RDF VALIDATION PASSED")
            print(f"  - {len(self.data_graph)} triples validated")
            if self.warnings:
                print(f"  - {len(self.warnings)} warning(s)")
            return True


def main():
    parser = argparse.ArgumentParser(
        description='Validate RDF files against SHACL shapes'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        required=True,
        help='Directory containing RDF files'
    )
    parser.add_argument(
        '--shapes',
        type=Path,
        required=True,
        help='Path to SHACL shapes file'
    )

    args = parser.parse_args()

    if not args.ontology.exists():
        print(f"❌ ERROR: Ontology directory not found: {args.ontology}")
        sys.exit(1)

    if not args.shapes.exists():
        print(f"❌ ERROR: Shapes file not found: {args.shapes}")
        sys.exit(1)

    print("="*70)
    print("CATTY THESIS RDF/SHACL VALIDATOR")
    print("="*70)
    print(f"\nOntology directory: {args.ontology}")
    print(f"Shapes file: {args.shapes}")

    validator = RDFValidator()

    # Load data
    validator.load_ontology_files(args.ontology)
    validator.load_shapes(args.shapes)

    if validator.errors:
        validator.print_results()
        sys.exit(1)

    # Run validations
    shacl_valid = validator.validate_with_shacl()
    ids_valid = validator.validate_id_uniqueness()
    citations_valid = validator.validate_citations_exist()

    # Print results
    success = validator.print_results()
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
