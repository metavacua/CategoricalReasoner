# pylint: disable=redefined-outer-name, unused-argument
#!/usr/bin/env python3
"""
Script to apply the URI fix for Issue #8.

This script updates all Catty ontology files to use the correct GitHub Pages URI.
It handles multiple invalid URI patterns that may exist in the codebase.

IMPORTANT: This script ONLY replaces Catty-specific invalid URIs. It preserves
external semantic web references (DBPedia, Wikidata, schema.org, etc.) as these
are legitimate resources from the greater web.

WARNING: This script will modify files in place. Make sure you have committed
any changes before running this script, or run with --dry-run first.

Usage:
    python3 tools/test_apply_uri_fix.py --dry-run  # Preview changes
    python3 tools/test_apply_uri_fix.py            # Apply changes

Issue #8: Catty specific ontologies have invalid URI
"""

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Tuple

# URI configuration
# Multiple invalid URIs that need to be replaced (ONLY Catty-specific URIs)
INVALID_URIS = [
    "http://catty.org/ontology/",
    "https://owner.github.io/Catty/ontology#",
    "http://owner.github.io/Catty/ontology#",
]

VALID_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

# Well-known semantic web vocabularies that should NEVER be replaced
PROTECTED_DOMAINS = [
    "dbpedia.org",
    "wikidata.org",
    "schema.org",
    "w3.org",
    "purl.org",
    "xmlns.com",
    "ncatlab.org",
    "example.org",
    "example.com",
]

# Ontology directory
ONTOLOGY_DIR = Path("ontology")


def is_protected_uri(uri: str) -> bool:
    """
    Check if a URI is from a protected domain (external semantic web resource).

    Args:
        uri: The URI to check

    Returns:
        True if the URI should be protected from replacement
    """
    uri_lower = uri.lower()
    return any(domain in uri_lower for domain in PROTECTED_DOMAINS)


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


def update_file(filepath: Path, dry_run: bool = False) -> Tuple[bool, int, Dict[str, int]]:
    """
    Update a file to use the new URI.

    Args:
        filepath: Path to the file to update
        dry_run: If True, don't actually write changes

    Returns:
        (was_modified, total_replacements, replacements_by_uri)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        original_content = content
        replacements_by_uri = {}
        total_replacements = 0

        # Replace each invalid URI, but only if it's not a protected domain
        for invalid_uri in INVALID_URIS:
            if invalid_uri in content:
                # Check each occurrence to ensure we're not replacing protected URIs
                lines = content.split('\n')
                new_lines = []
                count = 0

                for line in lines:
                    if invalid_uri in line:
                        # Check if this line contains a protected domain
                        # If so, skip replacement for this line
                        if not is_protected_uri(line):
                            new_line = line.replace(invalid_uri, VALID_URI)
                            count += line.count(invalid_uri)
                            new_lines.append(new_line)
                        else:
                            new_lines.append(line)
                    else:
                        new_lines.append(line)

                if count > 0:
                    replacements_by_uri[invalid_uri] = count
                    total_replacements += count
                    content = '\n'.join(new_lines)

        # Check if any changes were made
        if content == original_content:
            return False, 0, {}

        # Write back if not dry run
        if not dry_run:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)

        return True, total_replacements, replacements_by_uri

    except Exception as e:
        print(f"❌ Error processing {filepath}: {e}")
        return False, 0, {}


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

Note:
  This script ONLY replaces Catty-specific invalid URIs.
  External semantic web references (DBPedia, Wikidata, etc.) are preserved.
"""
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be changed without modifying files'
    )
    parser.add_argument(
        '--yes', '-y',
        action='store_true',
        help='Skip confirmation prompt (useful for CI/CD)'
    )
    args = parser.parse_args()

    print("=" * 80)
    print(f"{'DRY RUN: ' if args.dry_run else ''}Applying URI Fix for Issue #8")
    print("=" * 80)
    print(f"\nInvalid URIs to replace (Catty-specific only):")
    for uri in INVALID_URIS:
        print(f"  • {uri}")
    print(f"\nValid URI: {VALID_URI}")
    print(f"\nProtected domains (will NOT be replaced):")
    for domain in PROTECTED_DOMAINS:
        print(f"  • {domain}")
    print()

    if args.dry_run:
        print("⚠️  DRY RUN MODE: No files will be modified\n")
    else:
        if not args.yes:
            print("⚠️  WARNING: This will modify files in place!")
            print("   Make sure you have committed any changes first.\n")
            response = input("Continue? [y/N]: ")
            if response.lower() != 'y':
                print("\nAborted.")
                return 0

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    # Find all ontology files dynamically
    ontology_files = find_all_ontology_files(repo_root)

    if not ontology_files:
        print("⚠️  No ontology files found!")
        return 1

    print(f"Found {len(ontology_files)} ontology file(s) to process\n")

    modified_files = []
    grand_total_replacements = 0

    for filepath in ontology_files:
        rel_path = filepath.relative_to(repo_root)

        was_modified, total_replacements, replacements_by_uri = update_file(filepath, args.dry_run)

        if was_modified:
            status = "Would update" if args.dry_run else "Updated"
            print(f"✅ {status}: {rel_path}")
            for uri, count in replacements_by_uri.items():
                print(f"    • {count}× {uri}")
            print(f"    Total: {total_replacements} replacement(s)\n")
            modified_files.append(filepath)
            grand_total_replacements += total_replacements
        else:
            print(f"ℹ️  No changes: {rel_path}\n")

    # Summary
    print("=" * 80)
    print("Summary")
    print("=" * 80)
    print(f"\nFiles {'that would be ' if args.dry_run else ''}modified: {len(modified_files)}")
    print(f"Total replacements: {grand_total_replacements}\n")

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
        print("  2. Comprehensive validation: python3 tools/test_ontology_uris.py")
        print("  3. Validate RDF: python3 tools/validate-rdf.py --all")
        print("  4. Commit changes: git add ontology/ && git commit -m 'Fix: Update ontology URIs to GitHub Pages URL (Issue #8)'")
        print()

    return 0


if __name__ == '__main__':
    sys.exit(main())
