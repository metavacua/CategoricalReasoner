# pylint: disable=redefined-outer-name, unused-argument
#!/usr/bin/env python3
"""
Script to apply the URI fix for Issue #8.

This script updates all Catty ontology files to use the correct GitHub Pages URI.

WARNING: This script will modify files in place. Make sure you have committed
any changes before running this script, or run with --dry-run first.

Usage:
    python3 tools/test_apply_uri_fix.py --dry-run  # Preview changes
    python3 tools/test_apply_uri_fix.py            # Apply changes

Issue #8: Catty specific ontologies have invalidate URI
"""

import argparse
import sys
from pathlib import Path
from typing import List, Tuple

# URI configuration
OLD_URI = "http://catty.org/ontology/"
NEW_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

# Files to update
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


def update_file(filepath: Path, dry_run: bool = False) -> Tuple[bool, int]:
    """
    Update a file to use the new URI.

    Args:
        filepath: Path to the file to update
        dry_run: If True, don't actually write changes

    Returns:
        (was_modified, num_replacements)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        if OLD_URI not in content:
            return False, 0

        # Count replacements
        num_replacements = content.count(OLD_URI)

        # Replace URI
        new_content = content.replace(OLD_URI, NEW_URI)

        # Write back if not dry run
        if not dry_run:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)

        return True, num_replacements

    except Exception as e:
        print(f"❌ Error processing {filepath}: {e}")
        return False, 0


def main():
    """Main function."""
    parser = argparse.ArgumentParser(
        description='Apply URI fix for Issue #8',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Preview changes without modifying files
  python3 tools/test_apply_uri_fix.py --dry-run

  # Apply changes
  python3 tools/test_apply_uri_fix.py

  # Verify changes
  python3 tools/test_validate_uri.py
"""
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be changed without modifying files'
    )
    args = parser.parse_args()

    print("=" * 80)
    print(f"{'DRY RUN: ' if args.dry_run else ''}Applying URI Fix for Issue #8")
    print("=" * 80)
    print(f"\nOld URI: {OLD_URI}")
    print(f"New URI: {NEW_URI}\n")

    if args.dry_run:
        print("⚠️  DRY RUN MODE: No files will be modified\n")
    else:
        print("⚠️  WARNING: This will modify files in place!")
        print("   Make sure you have committed any changes first.\n")
        response = input("Continue? [y/N]: ")
        if response.lower() != 'y':
            print("\nAborted.")
            return 0
        print()

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    modified_files = []
    total_replacements = 0

    for file_path_str in ONTOLOGY_FILES:
        filepath = repo_root / file_path_str

        if not filepath.exists():
            print(f"⚠️  {file_path_str}")
            print(f"    File not found\n")
            continue

        was_modified, num_replacements = update_file(filepath, args.dry_run)

        if was_modified:
            status = "Would update" if args.dry_run else "Updated"
            print(f"✅ {status}: {file_path_str}")
            print(f"    {num_replacements} replacement(s)\n")
            modified_files.append(filepath)
            total_replacements += num_replacements
        else:
            print(f"ℹ️  No changes: {file_path_str}\n")

    # Summary
    print("=" * 80)
    print("Summary")
    print("=" * 80)
    print(f"\nFiles {'that would be ' if args.dry_run else ''}modified: {len(modified_files)}")
    print(f"Total replacements: {total_replacements}\n")

    if modified_files:
        print("Modified files:")
        for filepath in modified_files:
            print(f"  • {filepath.relative_to(repo_root)}")
        print()

    if args.dry_run:
        print("To apply these changes, run without --dry-run:")
        print("  python3 tools/test_apply_uri_fix.py\n")
    else:
        print("✅ Changes applied successfully!\n")
        print("Next steps:")
        print("  1. Verify changes: python3 tools/test_validate_uri.py")
        print("  2. Validate RDF: python3 tools/validate-rdf.py --all")
        print("  3. Commit changes: git add ontology/ && git commit -m 'Fix: Update ontology URIs to GitHub Pages URL (Issue #8)'")
        print()

    return 0


if __name__ == '__main__':
    sys.exit(main())
