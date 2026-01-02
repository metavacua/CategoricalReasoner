#!/usr/bin/env python3
"""
Comprehensive test and migration guide for Catty ontology URI fix.

Issue #8: Catty specific ontologies have invalidate URI

PROBLEM:
--------
The Catty ontology files currently use an invalid URI:
    http://catty.org/ontology/

This domain does not exist and the ontology cannot be resolved.

SOLUTION:
---------
Update all ontology files to use the GitHub Pages URI:
    https://metavacua.github.io/CategoricalReasoner/ontology/

FILES TO UPDATE:
----------------
1. ontology/catty-categorical-schema.jsonld (line 4)
2. ontology/catty-complete-example.jsonld (line 4)
3. ontology/curry-howard-categorical-model.jsonld (line 4)
4. ontology/logics-as-objects.jsonld (line 4)
5. ontology/morphism-catalog.jsonld (line 4)
6. ontology/two-d-lattice-category.jsonld (line 4)
7. ontology/catty-shapes.ttl (line 6)
8. ontology/queries/sparql-examples.md (multiple lines)
9. ontology/ontological-inventory.md (documentation references)

CHANGES REQUIRED:
-----------------
In each JSON-LD file, change line 4 from:
    "catty": "http://catty.org/ontology/",
to:
    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",

In catty-shapes.ttl, change line 6 from:
    @prefix catty: <http://catty.org/ontology/> .
to:
    @prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology/> .

In SPARQL examples and documentation, update all references.

TESTING:
--------
After making changes, run this test to verify all URIs are correct.
"""

import json
import sys
from pathlib import Path
from typing import Dict, List, Tuple

# URI configuration
OLD_URI = "http://catty.org/ontology/"
NEW_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

# Files that need to be updated
EXPECTED_FILES = [
    "ontology/catty-categorical-schema.jsonld",
    "ontology/catty-complete-example.jsonld",
    "ontology/curry-howard-categorical-model.jsonld",
    "ontology/logics-as-objects.jsonld",
    "ontology/morphism-catalog.jsonld",
    "ontology/two-d-lattice-category.jsonld",
    "ontology/catty-shapes.ttl",
    "ontology/queries/sparql-examples.md",
]


def check_jsonld_uri(filepath: Path) -> Tuple[bool, str, Dict]:
    """Check JSON-LD file for correct URI."""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)

        if '@context' not in data:
            return True, "No @context found", {}

        context = data['@context']
        if not isinstance(context, dict) or 'catty' not in context:
            return True, "No catty URI in @context", {}

        catty_uri = context['catty']

        if catty_uri == OLD_URI:
            return False, f"Uses invalid URI: {OLD_URI}", {
                'line': 4,
                'old': f'    "catty": "{OLD_URI}",',
                'new': f'    "catty": "{NEW_URI}",'
            }
        elif catty_uri == NEW_URI:
            return True, f"✓ Uses correct URI", {}
        else:
            return False, f"Uses unexpected URI: {catty_uri}", {}

    except Exception as e:
        return False, f"Error: {e}", {}


def check_ttl_uri(filepath: Path) -> Tuple[bool, str, Dict]:
    """Check Turtle file for correct URI."""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()

        for i, line in enumerate(lines, 1):
            if 'catty:' in line and '<' in line:
                if OLD_URI in line:
                    return False, f"Uses invalid URI on line {i}", {
                        'line': i,
                        'old': line.strip(),
                        'new': line.replace(OLD_URI, NEW_URI).strip()
                    }
                elif NEW_URI in line:
                    return True, f"✓ Uses correct URI", {}

        return True, "No catty URI found", {}

    except Exception as e:
        return False, f"Error: {e}", {}


def check_md_uri(filepath: Path) -> Tuple[bool, str, List[Dict]]:
    """Check Markdown file for correct URI."""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()

        issues = []
        for i, line in enumerate(lines, 1):
            if OLD_URI in line:
                issues.append({
                    'line': i,
                    'old': line.strip(),
                    'new': line.replace(OLD_URI, NEW_URI).strip()
                })

        if issues:
            return False, f"Found {len(issues)} line(s) with invalid URI", issues
        elif NEW_URI in lines or any(NEW_URI in line for line in lines):
            return True, "✓ Uses correct URI", []
        else:
            return True, "No catty URI found", []

    except Exception as e:
        return False, f"Error: {e}", []


def print_fix_instructions(filepath: Path, fix_info):
    """Print instructions for fixing a file."""
    print(f"\n  Fix for {filepath.name}:")
    if isinstance(fix_info, dict) and 'line' in fix_info:
        print(f"    Line {fix_info['line']}:")
        print(f"    - {fix_info['old']}")
        print(f"    + {fix_info['new']}")
    elif isinstance(fix_info, list):
        for item in fix_info:
            print(f"    Line {item['line']}:")
            print(f"    - {item['old']}")
            print(f"    + {item['new']}")


def main():
    """Main test function."""
    print("=" * 80)
    print("Catty Ontology URI Validation Test")
    print("=" * 80)
    print(f"\nIssue #8: Catty specific ontologies have invalidate URI")
    print(f"\nInvalid URI:  {OLD_URI}")
    print(f"Correct URI:  {NEW_URI}")
    print("\n" + "=" * 80)

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    all_passed = True
    files_to_fix = []

    print("\nChecking ontology files...\n")

    for file_path_str in EXPECTED_FILES:
        filepath = repo_root / file_path_str

        if not filepath.exists():
            print(f"⚠️  {file_path_str}")
            print(f"    File not found\n")
            continue

        # Check based on file type
        if filepath.suffix == '.jsonld':
            is_valid, message, fix_info = check_jsonld_uri(filepath)
        elif filepath.suffix == '.ttl':
            is_valid, message, fix_info = check_ttl_uri(filepath)
        elif filepath.suffix == '.md':
            is_valid, message, fix_info = check_md_uri(filepath)
        else:
            continue

        # Print result
        status = "✅" if is_valid else "❌"
        print(f"{status} {file_path_str}")
        print(f"    {message}")

        if not is_valid:
            all_passed = False
            files_to_fix.append((filepath, fix_info))
            if fix_info:
                print_fix_instructions(filepath, fix_info)

        print()

    # Summary
    print("=" * 80)
    if all_passed:
        print("✅ SUCCESS: All ontology files use the correct GitHub Pages URI!")
        print("=" * 80)
        return 0
    else:
        print(f"❌ FAILED: {len(files_to_fix)} file(s) need to be updated")
        print("=" * 80)
        print("\nMANUAL FIX REQUIRED:")
        print(f"\nReplace all occurrences of:")
        print(f"  {OLD_URI}")
        print(f"with:")
        print(f"  {NEW_URI}")
        print(f"\nin the following files:")
        for filepath, _ in files_to_fix:
            print(f"  - {filepath.relative_to(repo_root)}")

        print("\nOR use find and replace:")
        print(f"  find ontology/ -type f \\( -name '*.jsonld' -o -name '*.ttl' -o -name '*.md' \\) \\")
        print(f"    -exec sed -i 's|{OLD_URI}|{NEW_URI}|g' {{}} +")

        print("\n" + "=" * 80)
        return 1


if __name__ == '__main__':
    sys.exit(main())
