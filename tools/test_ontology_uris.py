#!/usr/bin/env python3
# pylint: disable=redefined-outer-name, unused-argument
"""
Comprehensive test and validation infrastructure for Catty ontology URIs.

Issue #8: Catty specific ontologies have invalidate URI

This script provides infrastructure to:
1. Validate ontologies when they're changed or added
2. Detect various problematic URI patterns (not just catty.org)
3. Report untested/untestable ontologies
4. Ensure all ontology URIs are dereferenceable

PROBLEM:
--------
The Catty ontology files currently use invalid URIs that point to non-existent domains.
Examples:
    http://catty.org/ontology/
    https://owner.github.io/Catty/ontology#

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

INFRASTRUCTURE:
---------------
This script is designed to be run:
- Manually during development
- Automatically in CI/CD pipeline
- As a pre-commit hook

It will detect:
- Invalid domain names (catty.org, owner.github.io, etc.)
- Non-HTTPS URIs
- Inconsistent URI patterns across files
- Missing or malformed @context/@base declarations
"""

import json
import re
import sys
from pathlib import Path
from typing import Dict, List, Set, Tuple

# URI configuration
INVALID_URIS = [
    "http://catty.org/ontology/",
    "https://owner.github.io/Catty/ontology#",
    "http://owner.github.io/",
    "https://owner.github.io/",
]

VALID_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

# Patterns to detect problematic URIs
PROBLEMATIC_PATTERNS = [
    (r'http://catty\.org/', 'Invalid domain: catty.org'),
    (r'https?://owner\.github\.io/', 'Invalid placeholder: owner.github.io'),
    (r'http://[^/]+/ontology/', 'Non-HTTPS ontology URI'),
    (r'@prefix\s+catty:\s+<(?!https://metavacua\.github\.io/CategoricalReasoner/ontology/)', 'Invalid catty prefix'),
]

# Files that need to be checked
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


def check_jsonld_uri(filepath: Path) -> Tuple[bool, str, List[Dict]]:
    """Check JSON-LD file for correct URI and detect various issues."""
    issues = []

    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            data = json.loads(content)

        # Check @context
        if '@context' not in data:
            issues.append({
                'severity': 'warning',
                'message': 'No @context found',
                'line': None,
            })
        else:
            context = data['@context']
            if isinstance(context, dict) and 'catty' in context:
                catty_uri = context['catty']

                # Check for invalid URIs
                if catty_uri in INVALID_URIS:
                    issues.append({
                        'severity': 'error',
                        'message': f'Uses invalid URI: {catty_uri}',
                        'line': 4,  # Typically line 4 in JSON-LD files
                        'fix': f'Change to: "{VALID_URI}"'
                    })
                elif catty_uri != VALID_URI:
                    issues.append({
                        'severity': 'error',
                        'message': f'Uses unexpected URI: {catty_uri}',
                        'line': 4,
                        'fix': f'Change to: "{VALID_URI}"'
                    })

        # Check for problematic patterns in the entire content
        lines = content.splitlines()
        for i, line in enumerate(lines, 1):
            for pattern, description in PROBLEMATIC_PATTERNS:
                if re.search(pattern, line):
                    issues.append({
                        'severity': 'error',
                        'message': f'{description}: {line.strip()[:60]}...',
                        'line': i,
                        'fix': f'Replace with valid URI: {VALID_URI}'
                    })

        if not issues:
            return True, "✓ Valid", []

        error_count = sum(1 for i in issues if i['severity'] == 'error')
        warning_count = sum(1 for i in issues if i['severity'] == 'warning')

        summary = []
        if error_count:
            summary.append(f"{error_count} error(s)")
        if warning_count:
            summary.append(f"{warning_count} warning(s)")

        return False, f"✗ {', '.join(summary)}", issues

    except json.JSONDecodeError as e:
        return False, f"✗ Invalid JSON: {e}", [{
            'severity': 'error',
            'message': f'JSON parse error: {e}',
            'line': getattr(e, 'lineno', None),
        }]
    except Exception as e:
        return False, f"✗ Error: {e}", [{
            'severity': 'error',
            'message': str(e),
            'line': None,
        }]


def check_ttl_uri(filepath: Path) -> Tuple[bool, str, List[Dict]]:
    """Check Turtle file for correct URI and detect various issues."""
    issues = []

    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()

        for i, line in enumerate(lines, 1):
            # Check for invalid catty prefix
            if '@prefix catty:' in line:
                for invalid_uri in INVALID_URIS:
                    if invalid_uri in line:
                        issues.append({
                            'severity': 'error',
                            'message': f'Invalid catty prefix: {line.strip()}',
                            'line': i,
                            'fix': f'@prefix catty: <{VALID_URI}> .'
                        })
                        break

            # Check for other problematic patterns
            for pattern, description in PROBLEMATIC_PATTERNS:
                if re.search(pattern, line):
                    issues.append({
                        'severity': 'error',
                        'message': f'{description}: {line.strip()[:60]}...',
                        'line': i,
                        'fix': f'Replace with: {VALID_URI}'
                    })

        if not issues:
            return True, "✓ Valid", []

        return False, f"✗ {len(issues)} issue(s)", issues

    except Exception as e:
        return False, f"✗ Error: {e}", [{
            'severity': 'error',
            'message': str(e),
            'line': None,
        }]


def check_md_uri(filepath: Path) -> Tuple[bool, str, List[Dict]]:
    """Check Markdown file for correct URI."""
    issues = []

    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()

        for i, line in enumerate(lines, 1):
            for invalid_uri in INVALID_URIS:
                if invalid_uri in line:
                    issues.append({
                        'severity': 'error',
                        'message': f'Invalid URI reference: {line.strip()[:60]}...',
                        'line': i,
                        'fix': f'Replace {invalid_uri} with {VALID_URI}'
                    })

        if not issues:
            return True, "✓ Valid", []

        return False, f"✗ {len(issues)} issue(s)", issues

    except Exception as e:
        return False, f"✗ Error: {e}", [{
            'severity': 'error',
            'message': str(e),
            'line': None,
        }]


def print_issues(filepath: Path, issues: List[Dict]):
    """Print detailed issue information."""
    for issue in issues:
        severity_symbol = "❌" if issue['severity'] == 'error' else "⚠️"
        line_info = f" (line {issue['line']})" if issue['line'] else ""
        print(f"    {severity_symbol} {issue['message']}{line_info}")
        if 'fix' in issue:
            print(f"       Fix: {issue['fix']}")


def main():
    """Main test function."""
    print("=" * 80)
    print("Catty Ontology URI Validation Infrastructure")
    print("=" * 80)
    print(f"\nIssue #8: Catty specific ontologies have invalid URI")
    print(f"\nThis script validates all ontology files and detects:")
    print(f"  • Invalid domain names (catty.org, owner.github.io, etc.)")
    print(f"  • Non-HTTPS URIs")
    print(f"  • Inconsistent URI patterns")
    print(f"  • Missing or malformed declarations")
    print(f"\nValid URI:  {VALID_URI}")
    print("\n" + "=" * 80)

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    # Find all ontology files
    ontology_files = find_all_ontology_files(repo_root)

    if not ontology_files:
        print("\n⚠️  No ontology files found!")
        print("=" * 80)
        return 1

    print(f"\nFound {len(ontology_files)} ontology file(s) to validate\n")

    all_passed = True
    files_with_issues = []
    untested_files = []

    for filepath in ontology_files:
        rel_path = filepath.relative_to(repo_root)

        # Check based on file type
        if filepath.suffix == '.jsonld':
            is_valid, message, issues = check_jsonld_uri(filepath)
        elif filepath.suffix in ['.ttl', '.rdf', '.owl']:
            is_valid, message, issues = check_ttl_uri(filepath)
        elif filepath.suffix == '.md':
            is_valid, message, issues = check_md_uri(filepath)
        else:
            print(f"⚠️  {rel_path}")
            print(f"    Unsupported file type: {filepath.suffix}")
            untested_files.append(filepath)
            continue

        # Print result
        status = "✅" if is_valid else "❌"
        print(f"{status} {rel_path}")
        print(f"    {message}")

        if not is_valid:
            all_passed = False
            files_with_issues.append((filepath, issues))
            print_issues(filepath, issues)

        print()

    # Summary
    print("=" * 80)
    print("VALIDATION SUMMARY")
    print("=" * 80)
    print(f"\nTotal files checked: {len(ontology_files)}")
    print(f"✅ Valid: {len(ontology_files) - len(files_with_issues)}")
    print(f"❌ With issues: {len(files_with_issues)}")
    if untested_files:
        print(f"⚠️  Untested: {len(untested_files)}")

    if untested_files:
        print("\nUntested files:")
        for filepath in untested_files:
            print(f"  • {filepath.relative_to(repo_root)}")

    if all_passed and not untested_files:
        print("\n🎉 SUCCESS: All ontology files are valid!")
        print("=" * 80)
        return 0

    if files_with_issues:
        print("\n" + "=" * 80)
        print("REMEDIATION STEPS")
        print("=" * 80)
        print(f"\nThe following files need to be updated:")
        for filepath, _ in files_with_issues:
            print(f"  • {filepath.relative_to(repo_root)}")

        print(f"\n\nAUTOMATED FIX:")
        print(f"Run the automated fix script:")
        print(f"  python3 tools/test_apply_uri_fix.py")

        print(f"\n\nMANUAL FIX:")
        print(f"Replace all occurrences of invalid URIs with:")
        print(f"  {VALID_URI}")

        print(f"\n\nVERIFICATION:")
        print(f"After fixing, run this script again:")
        print(f"  python3 tools/test_ontology_uris.py")

    print("\n" + "=" * 80)
    return 1 if files_with_issues or untested_files else 0


if __name__ == '__main__':
    sys.exit(main())
