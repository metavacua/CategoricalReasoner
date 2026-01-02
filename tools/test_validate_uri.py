#!/usr/bin/env python3
"""
Test to validate that ontology URIs are correctly configured for GitHub Pages deployment.

This test ensures that all Catty ontology files use the correct GitHub Pages URI
instead of the invalid http://catty.org/ontology/ URI.

Expected URI: https://metavacua.github.io/CategoricalReasoner/ontology/
Invalid URI: http://catty.org/ontology/

Issue: #8 - Catty specific ontologies have invalidate URI
"""

import json
import sys
from pathlib import Path
from typing import List, Tuple

# Expected valid URI for GitHub Pages
EXPECTED_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"
INVALID_URI = "http://catty.org/ontology/"


def check_jsonld_uri(filepath: Path) -> Tuple[bool, str]:
    """
    Check if a JSON-LD file uses the correct ontology URI.

    Args:
        filepath: Path to the JSON-LD file

    Returns:
        Tuple of (is_valid: bool, message: str)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)

        # Check @context for catty URI
        if '@context' in data:
            context = data['@context']
            if isinstance(context, dict) and 'catty' in context:
                catty_uri = context['catty']

                if catty_uri == INVALID_URI:
                    return False, f"❌ Uses invalid URI: {INVALID_URI}"
                elif catty_uri == EXPECTED_URI:
                    return True, f"✅ Uses correct URI: {EXPECTED_URI}"
                else:
                    return False, f"⚠️  Uses unexpected URI: {catty_uri}"

        return True, "ℹ️  No catty URI found in @context"

    except json.JSONDecodeError as e:
        return False, f"❌ JSON parse error: {e}"
    except Exception as e:
        return False, f"❌ Error: {e}"


def check_ttl_uri(filepath: Path) -> Tuple[bool, str]:
    """
    Check if a Turtle file uses the correct ontology URI.

    Args:
        filepath: Path to the Turtle file

    Returns:
        Tuple of (is_valid: bool, message: str)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        # Check for catty prefix declaration
        if INVALID_URI in content:
            return False, f"❌ Uses invalid URI: {INVALID_URI}"
        elif EXPECTED_URI in content:
            return True, f"✅ Uses correct URI: {EXPECTED_URI}"
        elif 'catty:' in content and '<http://catty.org/ontology/>' in content:
            return False, f"❌ Uses invalid URI in angle brackets"

        return True, "ℹ️  No catty URI found"

    except Exception as e:
        return False, f"❌ Error: {e}"


def find_ontology_files() -> List[Path]:
    """
    Find all ontology files in the repository.

    Returns:
        List of paths to ontology files
    """
    script_dir = Path(__file__).parent
    repo_root = script_dir.parent
    ontology_dir = repo_root / 'ontology'

    if not ontology_dir.exists():
        return []

    files = []
    files.extend(ontology_dir.glob('*.jsonld'))
    files.extend(ontology_dir.glob('*.ttl'))
    files.extend(ontology_dir.glob('**/*.jsonld'))
    files.extend(ontology_dir.glob('**/*.ttl'))

    return sorted(set(files))


def main():
    """Main test function."""
    print("=" * 70)
    print("Catty Ontology URI Validation Test")
    print("=" * 70)
    print(f"\nExpected URI: {EXPECTED_URI}")
    print(f"Invalid URI:  {INVALID_URI}\n")

    ontology_files = find_ontology_files()

    if not ontology_files:
        print("❌ No ontology files found!")
        return 1

    print(f"Found {len(ontology_files)} ontology file(s) to check:\n")

    results = []
    for filepath in ontology_files:
        if filepath.suffix == '.jsonld':
            is_valid, message = check_jsonld_uri(filepath)
        elif filepath.suffix == '.ttl':
            is_valid, message = check_ttl_uri(filepath)
        else:
            continue

        results.append((filepath, is_valid, message))

        # Print result
        rel_path = filepath.relative_to(filepath.parent.parent)
        print(f"{rel_path}")
        print(f"  {message}\n")

    # Summary
    print("=" * 70)
    total = len(results)
    passed = sum(1 for _, is_valid, _ in results if is_valid)
    failed = total - passed

    print(f"Summary: {passed}/{total} files valid, {failed} files need fixing\n")

    if failed > 0:
        print("Files that need URI updates:")
        for filepath, is_valid, message in results:
            if not is_valid:
                rel_path = filepath.relative_to(filepath.parent.parent)
                print(f"  - {rel_path}")

        print(f"\nTo fix: Replace '{INVALID_URI}' with '{EXPECTED_URI}'")
        print("in all ontology files.\n")
        return 1
    else:
        print("✅ All ontology files use the correct GitHub Pages URI!\n")
        return 0


if __name__ == '__main__':
    sys.exit(main())
