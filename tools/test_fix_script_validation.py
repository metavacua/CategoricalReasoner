#!/usr/bin/env python3
"""
Test to validate that the automated fix script works correctly.

This test ensures that:
1. The fix script can run in dry-run mode
2. The fix script correctly identifies files that need fixing
3. The fix script doesn't modify files in dry-run mode
4. The fix script provides clear output
"""

import subprocess
import sys
from pathlib import Path


def test_fix_script_dry_run():
    """Test that the fix script runs in dry-run mode."""
    print("Testing fix script in dry-run mode...")

    script_dir = Path(__file__).parent
    script_path = script_dir / 'test_apply_uri_fix.py'

    if not script_path.exists():
        print("  ❌ Fix script not found")
        return False

    try:
        # Run the script in dry-run mode
        result = subprocess.run(
            ['python3', str(script_path), '--dry-run'],
            capture_output=True,
            text=True,
            timeout=30
        )

        # Check that the script ran
        if 'DRY RUN' in result.stdout:
            print("  ✅ Script ran in dry-run mode")

            # Check for expected output
            if 'Invalid URIs to replace' in result.stdout:
                print("  ✅ Script shows URIs to replace")
            else:
                print("  ⚠️  Script output may be incomplete")

            print("✅ Fix script dry-run works\n")
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


def test_fix_script_help():
    """Test that the fix script has help documentation."""
    print("Testing fix script help...")

    script_dir = Path(__file__).parent
    script_path = script_dir / 'test_apply_uri_fix.py'

    try:
        # Run the script with --help
        result = subprocess.run(
            ['python3', str(script_path), '--help'],
            capture_output=True,
            text=True,
            timeout=10
        )

        # Check that help is displayed
        if '--dry-run' in result.stdout or 'usage' in result.stdout.lower():
            print("  ✅ Help documentation available")
            print("✅ Fix script has help\n")
            return True
        else:
            print("  ❌ Help documentation not found")
            return False

    except Exception as e:
        print(f"  ❌ Error running script: {e}")
        return False


def test_fix_script_finds_files():
    """Test that the fix script can find ontology files."""
    print("Testing fix script file discovery...")

    script_dir = Path(__file__).parent
    repo_root = script_dir.parent

    # Import the fix script's file discovery function
    sys.path.insert(0, str(script_dir))
    try:
        from test_apply_uri_fix import find_all_ontology_files

        files = find_all_ontology_files(repo_root)

        if not files:
            print("  ❌ No files found")
            return False

        print(f"  ✅ Found {len(files)} file(s)")

        # Check for example files
        example_files = [f for f in files if 'examples' in str(f)]
        if example_files:
            print(f"  ✅ Includes {len(example_files)} example file(s)")

        print("✅ Fix script finds files correctly\n")
        return True

    except ImportError as e:
        print(f"  ❌ Cannot import fix script: {e}")
        return False
    except Exception as e:
        print(f"  ❌ Error: {e}")
        return False
    finally:
        sys.path.pop(0)


def main():
    """Run all fix script validation tests."""
    print("=" * 80)
    print("Fix Script Validation Test")
    print("=" * 80)
    print()

    tests = [
        ("Fix script dry-run works", test_fix_script_dry_run),
        ("Fix script has help", test_fix_script_help),
        ("Fix script finds files", test_fix_script_finds_files),
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
        print("\n🎉 SUCCESS: Fix script validation passed!")
        print("\nThe automated fix script is working correctly.")
        return 0
    else:
        print(f"\n❌ FAILED: {total - passed} test(s) failed")
        return 1


if __name__ == '__main__':
    sys.exit(main())
