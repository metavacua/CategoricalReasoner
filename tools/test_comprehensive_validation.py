#!/usr/bin/env python3
"""
Comprehensive validation test for the URI validation infrastructure.

This test runs all validation checks to ensure the infrastructure is working correctly
and that all ontology files will be properly validated.

This is the master test that should be run before merging any changes.
"""

import subprocess
import sys
from pathlib import Path


def run_test(test_name, script_path, args=None):
    """Run a test script and return the result."""
    print(f"\n{'=' * 80}")
    print(f"Running: {test_name}")
    print(f"{'=' * 80}\n")

    cmd = ['python3', str(script_path)]
    if args:
        cmd.extend(args)

    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            timeout=60
        )

        # Print output
        print(result.stdout)
        if result.stderr:
            print("STDERR:", result.stderr)

        # Return success/failure
        return result.returncode == 0

    except subprocess.TimeoutExpired:
        print(f"❌ Test timed out")
        return False
    except Exception as e:
        print(f"❌ Error running test: {e}")
        return False


def main():
    """Run all comprehensive validation tests."""
    print("=" * 80)
    print("COMPREHENSIVE URI VALIDATION INFRASTRUCTURE TEST")
    print("=" * 80)
    print("\nThis test validates that the entire URI validation infrastructure")
    print("is working correctly and ready for production use.")
    print()

    script_dir = Path(__file__).parent

    # Define all tests to run
    tests = [
        ("Infrastructure Validation", script_dir / 'test_infrastructure_validation.py', None),
        ("Fix Script Validation", script_dir / 'test_fix_script_validation.py', None),
        ("URI Validation (Comprehensive)", script_dir / 'test_ontology_uris.py', None),
        ("URI Validation (Quick)", script_dir / 'test_validate_uri.py', None),
    ]

    results = []

    for test_name, script_path, args in tests:
        if not script_path.exists():
            print(f"\n❌ Test script not found: {script_path}")
            results.append((test_name, False))
            continue

        result = run_test(test_name, script_path, args)
        results.append((test_name, result))

    # Final Summary
    print("\n" + "=" * 80)
    print("FINAL SUMMARY")
    print("=" * 80)
    print()

    passed = sum(1 for _, result in results if result)
    total = len(results)

    for test_name, result in results:
        status = "✅" if result else "❌"
        print(f"{status} {test_name}")

    print()
    print(f"Passed: {passed}/{total}")
    print()

    if passed == total:
        print("🎉 SUCCESS: All comprehensive validation tests passed!")
        print()
        print("The URI validation infrastructure is fully functional and ready for use.")
        print()
        print("Next steps:")
        print("  1. Review the changes in this PR")
        print("  2. Run the automated fix script:")
        print("     python3 tools/test_apply_uri_fix.py --dry-run")
        print("     python3 tools/test_apply_uri_fix.py")
        print("  3. Verify the fixes:")
        print("     python3 tools/test_ontology_uris.py")
        print("  4. Commit and push the changes")
        print()
        return 0
    else:
        print(f"❌ FAILED: {total - passed} test(s) failed")
        print()
        print("The infrastructure needs attention before it can be used.")
        print()
        print("Please review the failed tests above and fix any issues.")
        print()
        return 1


if __name__ == '__main__':
    sys.exit(main())
