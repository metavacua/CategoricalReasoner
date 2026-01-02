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
All Catty ontology files currently use an invalid URI that points to a non-existent domain:
    http://catty.org/ontology/

SOLUTION:
---------
Update all ontology files to use the valid GitHub Pages URI:
    https://metavacua.github.io/CategoricalReasoner/ontology/

This test will:
1. Check all ontology files for the invalid URI
2. Report which files need to be updated
3. Provide exact line-by-line changes needed
4. Offer automated fix commands
"""

import json
import sys
from pathlib import Path
from typing import List, Tuple

# URI configuration
INVALID_URI = "http://catty.org/ontology/"
VALID_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

# Expected files to check
ONTOLOGY_FILES = [
    "ontology/catty-categorical-schema.jsonld",
    "ontology/catty-complete-example.jsonld",
    "ontology/curry-howard-categorical-model.jsonld",
    "ontology/logics-as-objects.jsonld",
    "ontology/morphism-catalog.jsonld",
    "ontology/two-d-lattice-category.jsonld",
    "ontology/catty-shapes.ttl",
    "ontology/queries/sparql-examples.md",
]


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

        if INVALID_URI not in content:
            if VALID_URI in content:
                return True, "✅ Uses correct URI", []
            else:
                return True, "ℹ️  No catty URI found", []

        # Find all lines with invalid URI
        invalid_lines = []
        for i, line in enumerate(lines, 1):
            if INVALID_URI in line:
                invalid_lines.append(f"Line {i}: {line.strip()}")

        fix_instructions = [
            f"Found {len(invalid_lines)} line(s) with invalid URI:",
            *invalid_lines,
            "",
            "Replace with:",
            *[line.replace(INVALID_URI, VALID_URI) for line in invalid_lines]
        ]

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

    print(f"Invalid URI:  {INVALID_URI}")
    print(f"Valid URI:    {VALID_URI}")

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    print_section("Checking Ontology Files", "-")

    results = []
    files_to_fix = []

    for file_path_str in ONTOLOGY_FILES:
        filepath = repo_root / file_path_str

        if not filepath.exists():
            print(f"⚠️  {file_path_str}")
            print(f"    File not found\n")
            continue

        is_valid, message, fix_instructions = check_file_uri(filepath)
        results.append((filepath, is_valid, message))

        print(f"{file_path_str}")
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
    print(f"\nIn each file, replace:")
    print(f"  {INVALID_URI}")
    print(f"with:")
    print(f"  {VALID_URI}")

    print(f"\n\nAUTOMATED FIX (Unix/Linux/Mac):")
    print(f"{'=' * 80}")
    print(f"\nRun this command from the repository root:")
    print(f"\n  find ontology/ -type f \\( -name '*.jsonld' -o -name '*.ttl' -o -name '*.md' \\) \\")
    print(f"    -exec sed -i.bak 's|{INVALID_URI}|{VALID_URI}|g' {{}} +")
    print(f"\n  # Remove backup files after verifying changes:")
    print(f"  find ontology/ -name '*.bak' -delete")

    print(f"\n\nAUTOMATED FIX (Python script):")
    print(f"{'=' * 80}")
    print(f"\nCreate and run this Python script:")
    print(f"""
```python
#!/usr/bin/env python3
from pathlib import Path

OLD_URI = "{INVALID_URI}"
NEW_URI = "{VALID_URI}"

ontology_dir = Path("ontology")
for filepath in ontology_dir.rglob("*"):
    if filepath.suffix in ['.jsonld', '.ttl', '.md'] and filepath.is_file():
        content = filepath.read_text()
        if OLD_URI in content:
            new_content = content.replace(OLD_URI, NEW_URI)
            filepath.write_text(new_content)
            print(f"Updated: {{filepath}}")
```
""")

    print(f"\n\nVERIFICATION:")
    print(f"{'=' * 80}")
    print(f"\nAfter applying the fix, run this test again to verify:")
    print(f"  python3 tools/test_validate_uri.py")

    print(f"\n\nADDITIONAL VALIDATION:")
    print(f"{'=' * 80}")
    print(f"\n1. Validate RDF syntax:")
    print(f"     python3 tools/validate-rdf.py --all")
    print(f"\n2. Check for any remaining references:")
    print(f"     grep -r '{INVALID_URI}' ontology/")
    print(f"\n3. Verify GitHub Pages deployment:")
    print(f"     curl -I {VALID_URI}")

    print_section("", "-")

    return 1


if __name__ == '__main__':
    sys.exit(main())
