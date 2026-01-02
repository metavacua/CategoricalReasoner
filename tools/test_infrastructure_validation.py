#!/usr/bin/env python3
"""
Test to validate the URI validation infrastructure itself.

This test ensures that:
1. All validation scripts can be imported and run
2. File discovery works correctly
3. Pattern detection is comprehensive
4. Documentation is consistent
"""

import sys
from pathlib import Path


def test_file_discovery():
    """Test that file discovery finds all ontology files."""
    print("Testing file discovery...")

    # Import the function from test_ontology_uris.py
    sys.path.insert(0, str(Path(__file__).parent))
    from test_ontology_uris import find_all_ontology_files

    repo_root = Path(__file__).parent.parent
    files = find_all_ontology_files(repo_root)

    # Check that we found files
    if not files:
        print("  ❌ No ontology files found!")
        return False

    print(f"  ✅ Found {len(files)} ontology files")

    # Check for expected files
    expected_patterns = [
        'catty-categorical-schema.jsonld',
        'catty-complete-example.jsonld',
        'catty-shapes.ttl',
        'curry-howard-categorical-model.jsonld',
        'logics-as-objects.jsonld',
        'morphism-catalog.jsonld',
        'two-d-lattice-category.jsonld',
        'sparql-examples.md',
    ]

    # Check for example files
    example_patterns = [
        'classical-logic.ttl',
        'intuitionistic-logic.ttl',
        'dual-intuitionistic-logic.ttl',
        'linear-logic.ttl',
        'monotonic-logic.ttl',
    ]

    found_files = [f.name for f in files]

    missing_expected = []
    for pattern in expected_patterns:
        if pattern not in found_files:
            missing_expected.append(pattern)

    missing_examples = []
    for pattern in example_patterns:
        if pattern not in found_files:
            missing_examples.append(pattern)

    if missing_expected:
        print(f"  ⚠️  Missing expected files: {', '.join(missing_expected)}")

    if missing_examples:
        print(f"  ⚠️  Missing example files: {', '.join(missing_examples)}")

    # Check that example files are found
    example_files = [f for f in files if 'examples' in str(f)]
    if example_files:
        print(f"  ✅ Found {len(example_files)} example files")
    else:
        print("  ⚠️  No example files found in ontology/examples/")

    return True


def test_pattern_detection():
    """Test that pattern detection is comprehensive."""
    print("\nTesting pattern detection...")

    from test_ontology_uris import INVALID_URIS, PROBLEMATIC_PATTERNS

    # Check that we have invalid URIs defined
    if not INVALID_URIS:
        print("  ❌ No invalid URIs defined!")
        return False

    print(f"  ✅ {len(INVALID_URIS)} invalid URI patterns defined")

    # Check that we have problematic patterns
    if not PROBLEMATIC_PATTERNS:
        print("  ❌ No problematic patterns defined!")
        return False

    print(f"  ✅ {len(PROBLEMATIC_PATTERNS)} problematic patterns defined")

    # Check for key patterns
    expected_patterns = [
        'catty.org',
        'owner.github.io',
    ]

    patterns_str = str(PROBLEMATIC_PATTERNS)
    for pattern in expected_patterns:
        if pattern in patterns_str:
            print(f"  ✅ Pattern '{pattern}' is detected")
        else:
            print(f"  ⚠️  Pattern '{pattern}' may not be detected")

    return True


def test_documentation_consistency():
    """Test that documentation files exist and are consistent."""
    print("\nTesting documentation consistency...")

    repo_root = Path(__file__).parent.parent
    tools_dir = repo_root / 'tools'

    expected_docs = [
        'test_README.md',
        'test_ISSUE_8_SUMMARY.md',
        'test_uri_fix_summary.md',
    ]

    for doc in expected_docs:
        doc_path = tools_dir / doc
        if doc_path.exists():
            print(f"  ✅ {doc} exists")
        else:
            print(f"  ❌ {doc} missing!")
            return False

    return True


def main():
    """Run all infrastructure tests."""
    print("=" * 80)
    print("URI Validation Infrastructure Test")
    print("=" * 80)

    all_passed = True
    all_passed &= test_file_discovery()
    all_passed &= test_pattern_detection()
    all_passed &= test_documentation_consistency()

    print("\n" + "=" * 80)
    if all_passed:
        print("✅ All infrastructure tests passed!")
        print("=" * 80)
        return 0
    else:
        print("❌ Some infrastructure tests failed!")
        print("=" * 80)
        return 1


if __name__ == '__main__':
    sys.exit(main())
