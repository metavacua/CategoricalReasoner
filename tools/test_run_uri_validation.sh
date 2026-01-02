#!/bin/bash
# Test script to validate Catty ontology URIs
# Issue #8: Catty specific ontologies have invalidate URI

set -e

echo "=========================================="
echo "Catty Ontology URI Validation"
echo "Issue #8"
echo "=========================================="
echo ""

# Run the Python validation test
python3 tools/test_validate_uri.py

exit_code=$?

if [ $exit_code -eq 0 ]; then
    echo ""
    echo "✅ All ontology files use the correct URI!"
else
    echo ""
    echo "❌ Some ontology files need to be updated."
    echo ""
    echo "See tools/test_ISSUE_8_SUMMARY.md for fix instructions."
fi

exit $exit_code
