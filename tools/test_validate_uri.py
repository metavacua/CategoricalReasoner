# pylint: disable=redefined-outer-name, unused-argument
#!/usr/bin/env python3
"""
Test to validate and document the URI fix for Issue #8.

This test validates that all Catty ontology files use the correct GitHub Pages URI.
It also provides detailed instructions for applying the fix.

Issue #8: Catty specific ontologies have invalidate URI
https://github.com/metavacua/CategoricalReasoner/issues/8

PROBLEM:
--------
All Catty ontology files currently use invalid URIs that point to non-existent domains:
    http://catty.org/ontology/
    https://owner.github.io/Catty/ontology#

SOLUTION:
---------
Update all ontology files to use the valid GitHub Pages URI:
    https://metavacua.github.io/CategoricalReasoner/ontology/

This test will:
1. Check all ontology files for invalid URIs
2. Report which files need to be updated
3. Provide exact line-by-line changes needed
4. Offer automated fix commands
"""

import json
import sys
from pathlib import Path
from typing import List, Tuple

# URI configuration
INVALID_URIS = [
    "http://catty.org/ontology/",
    "https://owner.github.io/Catty/ontology#",
    "http://owner.github.io/Catty/ontology#",
]
VALID_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

# Ontology directory
ONTOLOGY_DIR = Path("ontology")


def find_all_ontology_files(repo_root: Path) -> List[Path]:
    """Find all ontology files in the repository."""
    ontology_dir = repo_root / ONTOLOGY_DIR
    if not ontology_dir.exists():
        return []

    files = []
    for pattern in ['*.jsonld', '*.ttl', '*.rdf', '*.owl']:
        files.extend(ontology_dir.rglob(pattern))

    # Also check markdown files in queries directory
    queries_dir = ontology_dir / 'queries'
    if queries_dir.exists():
        files.extend(queries_dir.glob('*.md'))

    return sorted(files)


def check_file_uri(filepath: Path) -> Tuple[bool, str, List[str]]:
    """
    Check if a file uses the correct URI.

    Returns:
        (is_valid, message, fix_instructions)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            lines = content.splitlines()

        # Check for any invalid URIs
        has_invalid = False
        for invalid_uri in INVALID_URIS:
            if invalid_uri in content:
                has_invalid = True
                break

        if not has_invalid:
            if VALID_URI in content:
                return True, "✅ Uses correct URI", []
            else:
                return True, "ℹ️  No catty URI found", []

        # Find all lines with invalid URIs
        invalid_lines = []
        for i, line in enumerate(lines, 1):
            for invalid_uri in INVALID_URIS:
                if invalid_uri in line:
                    invalid_lines.append((i, line.strip(), invalid_uri))
                    break  # Only report each line once

        fix_instructions = [
            f"Found {len(invalid_lines)} line(s) with invalid URI:",
        ]
        for line_num, line_text, invalid_uri in invalid_lines:
            fix_instructions.append(f"Line {line_num}: {line_text}")

        fix_instructions.append("")
        fix_instructions.append("Replace with:")
        for line_num, line_text, invalid_uri in invalid_lines:
            fixed_line = line_text.replace(invalid_uri, VALID_URI)
            fix_instructions.append(f"Line {line_num}: {fixed_line}")

        return False, f"❌ Uses invalid URI ({len(invalid_lines)} occurrence(s))", fix_instructions

    except Exception as e:
        return False, f"❌ Error reading file: {e}", []


def print_section(title: str, char: str = "="):
    """Print a section header."""
    print(f"\n{char * 80}")
    print(title)
    print(f"{char * 80}\n")


def main():
    """Main test function."""
    print_section("Catty Ontology URI Validation Test - Issue #8")

    print(f"Invalid URIs:")
    for uri in INVALID_URIS:
        print(f"  • {uri}")
    print(f"\nValid URI:    {VALID_URI}")

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    print_section("Checking Ontology Files", "-")

    # Find all ontology files dynamically
    ontology_files = find_all_ontology_files(repo_root)

    if not ontology_files:
        print("⚠️  No ontology files found!")
        return 1

    print(f"Found {len(ontology_files)} ontology file(s) to check\n")

    results = []
    files_to_fix = []

    for filepath in ontology_files:
        rel_path = filepath.relative_to(repo_root)

        is_valid, message, fix_instructions = check_file_uri(filepath)
        results.append((filepath, is_valid, message))

        print(f"{rel_path}")
        print(f"    {message}")

        if not is_valid:
            files_to_fix.append((filepath, fix_instructions))
            if fix_instructions:
                for instruction in fix_instructions[:5]:  # Show first 5 lines
                    print(f"    {instruction}")
                if len(fix_instructions) > 5:
                    print(f"    ... ({len(fix_instructions) - 5} more lines)")

        print()

    # Summary
    print_section("Test Summary")

    total = len(results)
    passed = sum(1 for _, is_valid, _ in results if is_valid)
    failed = total - passed

    print(f"Total files checked: {total}")
    print(f"✅ Valid: {passed}")
    print(f"❌ Need fixing: {failed}")

    if failed == 0:
        print("\n🎉 SUCCESS: All ontology files use the correct GitHub Pages URI!")
        print_section("", "-")
        return 0

    # Provide fix instructions
    print_section("Fix Instructions")

    print("The following files need to be updated:\n")
    for filepath, _ in files_to_fix:
        print(f"  • {filepath.relative_to(repo_root)}")

    print(f"\n\nMANUAL FIX:")
    print(f"{'=' * 80}")
    print(f"\nIn each file, replace any of these invalid URIs:")
    for uri in INVALID_URIS:
        print(f"  • {uri}")
    print(f"\nwith:")
    print(f"  {VALID_URI}")

    print(f"\n\nAUTOMATED FIX (Python script):")
    print(f"{'=' * 80}")
    print(f"\nRun the automated fix script:")
    print(f"  python3 tools/test_apply_uri_fix.py --dry-run  # Preview changes")
    print(f"  python3 tools/test_apply_uri_fix.py            # Apply changes")

    print(f"\n\nVERIFICATION:")
    print(f"{'=' * 80}")
    print(f"\nAfter applying the fix, run this test again to verify:")
    print(f"  python3 tools/test_validate_uri.py")

    print(f"\n\nADDITIONAL VALIDATION:")
    print(f"{'=' * 80}")
    print(f"\n1. Comprehensive URI validation:")
    print(f"     python3 tools/test_ontology_uris.py")
    print(f"\n2. Validate RDF syntax:")
    print(f"     python3 tools/validate-rdf.py --all")
    print(f"\n3. Check for any remaining references:")
    for uri in INVALID_URIS:
        print(f"     grep -r '{uri}' ontology/")
    print(f"\n4. Verify GitHub Pages deployment:")
    print(f"     curl -I {VALID_URI}")

    print_section("", "-")

    return 1


if __name__ == '__main__':
    sys.exit(main())
