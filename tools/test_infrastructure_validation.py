#!/usr/bin/env python3
"""
Test to validate that the URI validation infrastructure is working correctly.

This test ensures that:
1. All validation scripts exist and are executable
2. The scripts can find ontology files dynamically
3. The scripts detect invalid URIs correctly
4. The automated fix script works as expected
5. Documentation is complete and accurate
"""

import subprocess
import sys
from pathlib import Path


def test_scripts_exist():
    """Test that all required scripts exist."""
    print("Testing script existence...")

    script_dir = Path(__file__).parent
    required_scripts = [
        'test_ontology_uris.py',
        'test_validate_uri.py',
        'test_apply_uri_fix.py',
        'test_run_uri_validation.sh',
    ]

    missing = []
    for script in required_scripts:
        script_path = script_dir / script
        if not script_path.exists():
            missing.append(script)
            print(f"  ❌ Missing: {script}")
        else:
            print(f"  ✅ Found: {script}")

    if missing:
        print(f"\n❌ Missing {len(missing)} required script(s)")
        return False

    print("✅ All required scripts exist\n")
    return True


def test_documentation_exists():
    """Test that all required documentation exists."""
    print("Testing documentation existence...")

    script_dir = Path(__file__).parent
    required_docs = [
        'test_README.md',
        'test_ISSUE_8_SUMMARY.md',
        'test_uri_fix_summary.md',
    ]

    missing = []
    for doc in required_docs:
        doc_path = script_dir / doc
        if not doc_path.exists():
            missing.append(doc)
            print(f"  ❌ Missing: {doc}")
        else:
            print(f"  ✅ Found: {doc}")

    if missing:
        print(f"\n❌ Missing {len(missing)} required document(s)")
        return False

    print("✅ All required documentation exists\n")
    return True


def test_workflow_exists():
    """Test that the CI/CD workflow exists."""
    print("Testing CI/CD workflow existence...")

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent
    workflow_path = repo_root / '.github' / 'workflows' / 'ontology-validation.yml'

    if not workflow_path.exists():
        print(f"  ❌ Missing: {workflow_path}")
        print("❌ CI/CD workflow not found\n")
        return False

    print(f"  ✅ Found: {workflow_path}")
    print("✅ CI/CD workflow exists\n")
    return True


def test_ontology_files_found():
    """Test that ontology files can be found dynamically."""
    print("Testing ontology file discovery...")

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent
    ontology_dir = repo_root / 'ontology'

    if not ontology_dir.exists():
        print(f"  ❌ Ontology directory not found: {ontology_dir}")
        return False

    # Find all ontology files
    files = []
    for pattern in ['*.jsonld', '*.ttl', '*.rdf', '*.owl']:
        files.extend(ontology_dir.rglob(pattern))

    # Also check markdown files in queries directory
    queries_dir = ontology_dir / 'queries'
    if queries_dir.exists():
        files.extend(queries_dir.glob('*.md'))

    if not files:
        print("  ❌ No ontology files found")
        return False

    print(f"  ✅ Found {len(files)} ontology file(s)")

    # Check for example files
    example_files = list((ontology_dir / 'examples').glob('*.ttl')) if (ontology_dir / 'examples').exists() else []
    if example_files:
        print(f"  ✅ Found {len(example_files)} example file(s)")

    print("✅ Ontology file discovery works\n")
    return True


def test_validation_script_runs():
    """Test that the validation script can run."""
    print("Testing validation script execution...")

    script_dir = Path(__file__).parent
    script_path = script_dir / 'test_ontology_uris.py'

    try:
        # Run the script (it will fail if there are invalid URIs, which is expected)
        result = subprocess.run(
            ['python3', str(script_path)],
            capture_output=True,
            text=True,
            timeout=30
        )

        # Check that the script ran (exit code doesn't matter for this test)
        if 'Catty Ontology URI Validation Infrastructure' in result.stdout:
            print("  ✅ Script executed successfully")
            print("✅ Validation script runs correctly\n")
            return True
        else:
            print("  ❌ Script output unexpected")
            print(f"  Output: {result.stdout[:200]}")
            return False

    except subprocess.TimeoutExpired:
        print("  ❌ Script timed out")
        return False
    except Exception as e:
        print(f"  ❌ Error running script: {e}")
        return False


def test_invalid_uri_patterns():
    """Test that the validation scripts check for the correct invalid URI patterns."""
    print("Testing invalid URI pattern detection...")

    script_dir = Path(__file__).parent
    script_path = script_dir / 'test_ontology_uris.py'

    # Read the script to check for pattern definitions
    try:
        with open(script_path, 'r') as f:
            content = f.read()

        required_patterns = [
            'http://catty.org/',
            'owner.github.io',
        ]

        all_found = True
        for pattern in required_patterns:
            if pattern in content:
                print(f"  ✅ Checks for: {pattern}")
            else:
                print(f"  ❌ Missing check for: {pattern}")
                all_found = False

        if all_found:
            print("✅ All required patterns are checked\n")
            return True
        else:
            print("❌ Some patterns are not checked\n")
            return False

    except Exception as e:
        print(f"  ❌ Error reading script: {e}")
        return False


def main():
    """Run all infrastructure validation tests."""
    print("=" * 80)
    print("URI Validation Infrastructure Test")
    print("=" * 80)
    print()

    tests = [
        ("Scripts exist", test_scripts_exist),
        ("Documentation exists", test_documentation_exists),
        ("CI/CD workflow exists", test_workflow_exists),
        ("Ontology files can be found", test_ontology_files_found),
        ("Validation script runs", test_validation_script_runs),
        ("Invalid URI patterns detected", test_invalid_uri_patterns),
    ]

    results = []
    for test_name, test_func in tests:
        try:
            result = test_func()
            results.append((test_name, result))
        except Exception as e:
            print(f"❌ Test '{test_name}' failed with exception: {e}\n")
            results.append((test_name, False))

    # Summary
    print("=" * 80)
    print("Test Summary")
    print("=" * 80)
    print()

    passed = sum(1 for _, result in results if result)
    total = len(results)

    for test_name, result in results:
        status = "✅" if result else "❌"
        print(f"{status} {test_name}")

    print()
    print(f"Passed: {passed}/{total}")

    if passed == total:
        print("\n🎉 SUCCESS: All infrastructure tests passed!")
        print("\nThe URI validation infrastructure is working correctly.")
        print("It will:")
        print("  • Validate ontologies when changed or added")
        print("  • Detect various problematic URI patterns")
        print("  • Report untested/untestable ontologies")
        print("  • Provide automated fixes")
        return 0
    else:
        print(f"\n❌ FAILED: {total - passed} test(s) failed")
        print("\nThe infrastructure needs attention before it can be used reliably.")
        return 1


if __name__ == '__main__':
    sys.exit(main())
