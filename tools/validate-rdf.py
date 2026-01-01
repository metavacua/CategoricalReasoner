#!/usr/bin/env python3
"""
RDF Validation Tool for Catty Ontologies

This script validates RDF/TTL files for syntactic correctness and basic semantic consistency.
It uses RDFLib to parse and validate ontology files.

Usage:
    python3 validate-rdf.py <file_or_directory>
    python3 validate-rdf.py --all  # Validate all ontology files

Requirements:
    pip install rdflib
"""

import sys
import os
from pathlib import Path
from typing import List, Tuple

try:
    from rdflib import Graph, URIRef, Namespace
    from rdflib.plugins.parsers.notation3 import BadSyntax
except ImportError:
    print("Error: rdflib is not installed. Install with: pip install rdflib")
    sys.exit(1)


def validate_rdf_file(filepath: Path) -> Tuple[bool, str]:
    """
    Validate a single RDF file.

    Args:
        filepath: Path to the RDF file

    Returns:
        Tuple of (success: bool, message: str)
    """
    try:
        g = Graph()

        # Determine format from file extension
        if filepath.suffix == '.ttl':
            fmt = 'turtle'
        elif filepath.suffix == '.jsonld':
            fmt = 'json-ld'
        elif filepath.suffix in ['.rdf', '.xml']:
            fmt = 'xml'
        elif filepath.suffix == '.nt':
            fmt = 'nt'
        else:
            return False, f"Unknown file format: {filepath.suffix}"

        # Parse the file
        g.parse(str(filepath), format=fmt)

        # Basic statistics
        num_triples = len(g)
        subjects = set(g.subjects())
        predicates = set(g.predicates())
        objects = set(g.objects())

        msg = f"✓ Valid ({num_triples} triples, {len(subjects)} subjects, {len(predicates)} predicates)"
        return True, msg

    except BadSyntax as e:
        return False, f"✗ Syntax Error: {e}"
    except Exception as e:
        return False, f"✗ Error: {type(e).__name__}: {e}"


def find_rdf_files(path: Path) -> List[Path]:
    """
    Find all RDF files in a directory (recursively) or return single file.

    Args:
        path: Path to file or directory

    Returns:
        List of RDF file paths
    """
    if path.is_file():
        return [path]

    rdf_extensions = ['.ttl', '.jsonld', '.rdf', '.xml', '.nt']
    rdf_files = []
    for ext in rdf_extensions:
        rdf_files.extend(path.rglob(f'*{ext}'))

    return sorted(rdf_files)


def main():
    if len(sys.argv) < 2:
        print("Usage: python3 validate-rdf.py <file_or_directory>")
        print("       python3 validate-rdf.py --all  # Validate all ontology files")
        sys.exit(1)

    # Determine path to validate
    if sys.argv[1] == '--all':
        # Find repository root (assuming script is in tools/)
        script_dir = Path(__file__).parent
        repo_root = script_dir.parent
        ontology_dir = repo_root / 'ontology'

        if not ontology_dir.exists():
            print(f"Error: Ontology directory not found: {ontology_dir}")
            sys.exit(1)

        target_path = ontology_dir
    else:
        target_path = Path(sys.argv[1])

    if not target_path.exists():
        print(f"Error: Path does not exist: {target_path}")
        sys.exit(1)

    # Find RDF files
    rdf_files = find_rdf_files(target_path)

    if not rdf_files:
        print(f"No RDF files found in: {target_path}")
        sys.exit(0)

    print(f"Validating {len(rdf_files)} RDF file(s)...\n")

    # Validate each file
    results = []
    for filepath in rdf_files:
        success, message = validate_rdf_file(filepath)
        results.append((filepath, success, message))

        # Print result
        status_icon = "✓" if success else "✗"
        try:
            display_path = filepath.relative_to(target_path if target_path.is_dir() else target_path.parent)
        except ValueError:
            display_path = filepath
        print(f"{status_icon} {display_path}")
        print(f"  {message}")

    # Summary
    print("\n" + "="*60)
    total = len(results)
    passed = sum(1 for _, success, _ in results if success)
    failed = total - passed

    print(f"Summary: {passed}/{total} files valid, {failed} failed")

    if failed > 0:
        print("\nFailed files:")
        for filepath, success, message in results:
            if not success:
                print(f"  - {filepath}")
        sys.exit(1)
    else:
        print("\n✓ All RDF files are valid!")
        sys.exit(0)


if __name__ == '__main__':
    main()
