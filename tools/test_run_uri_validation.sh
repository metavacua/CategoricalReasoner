#!/bin/bash
# Comprehensive validation script for Catty ontology URIs
# Issue #8: Catty specific ontologies have invalid URI
#
# This script provides infrastructure to validate ontologies when they're
# changed or added, and reports untested/untestable ontologies.
#
# Usage:
#   ./tools/test_run_uri_validation.sh           # Run all validations
#   ./tools/test_run_uri_validation.sh --quick   # Run quick validation only
#   ./tools/test_run_uri_validation.sh --fix     # Apply fixes automatically

set -e

QUICK_MODE=false
FIX_MODE=false

# Parse arguments
for arg in "$@"; do
    case $arg in
        --quick)
            QUICK_MODE=true
            shift
            ;;
        --fix)
            FIX_MODE=true
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --quick    Run quick validation only"
            echo "  --fix      Apply fixes automatically"
            echo "  --help     Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                  # Run all validations"
            echo "  $0 --quick          # Quick validation"
            echo "  $0 --fix            # Apply fixes"
            exit 0
            ;;
    esac
done

echo "=========================================="
echo "Catty Ontology URI Validation Infrastructure"
echo "Issue #8"
echo "=========================================="
echo ""

# If fix mode, apply fixes first
if [ "$FIX_MODE" = true ]; then
    echo "🔧 Applying URI fixes..."
    echo ""
    python3 tools/test_apply_uri_fix.py
    echo ""
fi

# Run comprehensive validation
echo "🔍 Running comprehensive URI validation..."
echo ""
python3 tools/test_ontology_uris.py
validation_exit_code=$?

if [ "$QUICK_MODE" = false ]; then
    echo ""
    echo "=========================================="
    echo "Additional Validation Checks"
    echo "=========================================="
    echo ""

    # Check for any remaining invalid URIs using grep
    echo "🔍 Checking for remaining invalid URI patterns..."
    echo ""

    INVALID_FOUND=false

    if grep -r "http://catty.org/ontology/" ontology/ 2>/dev/null; then
        echo "❌ Found http://catty.org/ontology/"
        INVALID_FOUND=true
    fi

    if grep -r "owner.github.io" ontology/ 2>/dev/null; then
        echo "❌ Found owner.github.io"
        INVALID_FOUND=true
    fi

    if [ "$INVALID_FOUND" = false ]; then
        echo "✅ No invalid URI patterns found"
    fi

    echo ""
fi

# Summary
echo ""
echo "=========================================="
echo "Validation Summary"
echo "=========================================="
echo ""

if [ $validation_exit_code -eq 0 ]; then
    echo "✅ SUCCESS: All ontology files are valid!"
    echo ""
    echo "Infrastructure is in place to:"
    echo "  • Validate ontologies when changed or added"
    echo "  • Detect various problematic URI patterns"
    echo "  • Report untested/untestable ontologies"
    echo ""
    exit 0
else
    echo "❌ FAILED: Some ontology files need attention"
    echo ""
    echo "Remediation options:"
    echo "  1. Apply fixes automatically:"
    echo "     $0 --fix"
    echo ""
    echo "  2. Apply fixes manually:"
    echo "     python3 tools/test_apply_uri_fix.py"
    echo ""
    echo "  3. See detailed instructions:"
    echo "     cat tools/test_ISSUE_8_SUMMARY.md"
    echo ""
    exit 1
fi
