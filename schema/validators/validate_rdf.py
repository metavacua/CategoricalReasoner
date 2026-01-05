#!/usr/bin/env python3
"""
RDF/SHACL Validator for Catty Thesis
Validates RDF graphs against SHACL shapes
"""

import argparse
import sys
from pathlib import Path
from typing import List, Tuple
from dataclasses import dataclass

# Try to import required libraries
try:
    from rdflib import Graph
    from pyshacl import validate
except ImportError:
    print("ERROR: rdflib and pyshacl are required. Install with:")
    print("  pip install rdflib pyshacl")
    sys.exit(1)


@dataclass
class ValidationError:
    """Represents a validation error"""
    file: str
    message: str
    severity: str = "ERROR"


class RDFValidator:
    """Validates RDF against SHACL shapes"""

    def __init__(self):
        self.errors: List[ValidationError] = []
        self.warnings: List[ValidationError] = []

    def load_ontology(self, ontology_dir: Path) -> Graph:
        """Load all RDF files from ontology directory"""
        g = Graph()

        # Supported formats
        formats = {
            '.jsonld': 'json-ld',
            '.ttl': 'turtle',
            '.rdf': 'xml',
            '.n3': 'n3',
            '.nt': 'nt',
        }

        # Load all RDF files
        for ext, fmt in formats.items():
            for rdf_file in ontology_dir.glob(f'*{ext}'):
                try:
                    g.parse(rdf_file, format=fmt)
                    print(f"Loaded {rdf_file} ({fmt})")
                except Exception as e:
                    self.errors.append(ValidationError(
                        file=str(rdf_file),
                        message=f"Error parsing RDF: {e}",
                        severity="ERROR"
                    ))

        print(f"Total triples loaded: {len(g)}")
        return g

    def load_shapes(self, shapes_file: Path) -> Graph:
        """Load SHACL shapes"""
        try:
            shapes_g = Graph()
            # Determine format
            if shapes_file.suffix == '.ttl':
                fmt = 'turtle'
            elif shapes_file.suffix == '.jsonld':
                fmt = 'json-ld'
            elif shapes_file.suffix == '.rdf':
                fmt = 'xml'
            else:
                fmt = 'turtle'  # Default

            shapes_g.parse(shapes_file, format=fmt)
            print(f"Loaded {len(shapes_g)} shape triples from {shapes_file}")
            return shapes_g
        except Exception as e:
            self.errors.append(ValidationError(
                file=str(shapes_file),
                message=f"Error parsing SHACL shapes: {e}",
                severity="FATAL"
            ))
            return None

    def validate(self, ontology_dir: Path, shapes_file: Path) -> bool:
        """Validate RDF against SHACL shapes"""
        print("=" * 60)
        print("RDF/SHACL Validator for Catty Thesis")
        print("=" * 60)

        # Load ontology
        print(f"\nLoading RDF from {ontology_dir}...")
        data_graph = self.load_ontology(ontology_dir)

        if len(data_graph) == 0:
            self.errors.append(ValidationError(
                file=str(ontology_dir),
                message="No RDF triples loaded from ontology directory",
                severity="ERROR"
            ))
            return False

        # Load shapes
        print(f"\nLoading SHACL shapes from {shapes_file}...")
        shapes_graph = self.load_shapes(shapes_file)

        if shapes_graph is None or len(shapes_graph) == 0:
            self.errors.append(ValidationError(
                file=str(shapes_file),
                message="Failed to load SHACL shapes",
                severity="FATAL"
            ))
            return False

        # Validate
        print("\nValidating RDF against SHACL shapes...")
        try:
            conforms, results_graph, results_text = validate(
                data_graph,
                shacl_graph=shapes_graph,
                ont_graph=None,
                inference='rdfs',
                abort_on_first=False,
                allow_infos=False,
                allow_warnings=False,
                meta_shacl=False,
                debug=False
            )

            if conforms:
                print("✓ RDF graph conforms to all SHACL shapes")
                return True
            else:
                print("✗ RDF graph does not conform to SHACL shapes")
                print("\nValidation results:")
                print(results_text)

                # Parse and extract specific violations
                for result in results_graph.subjects(None, None):
                    # Extract violation details
                    print(f"\nViolation: {result}")

                return False

        except Exception as e:
            self.errors.append(ValidationError(
                file="validation",
                message=f"SHACL validation error: {e}",
                severity="FATAL"
            ))
            return False

    def print_errors(self):
        """Print all validation errors"""
        if not self.errors:
            print("\n✓ No RDF errors found")
            return

        print(f"\n✗ Found {len(self.errors)} RDF errors:")
        for error in self.errors:
            print(f"\n  {error.severity}: {error.file}")
            print(f"    {error.message}")


def main():
    parser = argparse.ArgumentParser(
        description='Validate RDF against SHACL shapes'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        default=Path('ontology'),
        help='Directory containing RDF ontology files'
    )
    parser.add_argument(
        '--shapes',
        type=Path,
        default=Path('ontology/catty-thesis-shapes.shacl'),
        help='Path to SHACL shapes file'
    )

    args = parser.parse_args()

    # Check if directory exists
    if not args.ontology.exists():
        print(f"ERROR: Directory not found: {args.ontology}")
        sys.exit(1)

    # Check if shapes file exists
    if not args.shapes.exists():
        print(f"ERROR: SHACL shapes file not found: {args.shapes}")
        sys.exit(1)

    # Create validator
    validator = RDFValidator()

    # Validate
    success = validator.validate(args.ontology, args.shapes)

    # Print results
    validator.print_errors()

    # Exit with appropriate code
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
