#!/bin/bash
# Run all Catty thesis validators
# Usage: ./schema/validate_all.sh

set -e  # Exit on first error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "======================================================================"
echo "CATTY THESIS - FULL VALIDATION SUITE"
echo "======================================================================"
echo ""

# Check Python dependencies
echo "Checking Python dependencies..."
python3 -c "import yaml, rdflib, pyshacl, requests, jsonschema" 2>/dev/null || {
    echo "❌ ERROR: Missing Python dependencies"
    echo "Install with: pip install -r schema/requirements.txt"
    exit 1
}
echo "✓ All dependencies installed"
echo ""

# Track overall success
VALIDATION_PASSED=true

# 1. TeX Structure Validation
echo "======================================================================"
echo "1/4: TeX STRUCTURE VALIDATION"
echo "======================================================================"
if python3 "$SCRIPT_DIR/validators/validate_tex_structure.py" \
    --tex-dir "$PROJECT_ROOT/thesis/chapters/"; then
    echo "✅ TeX structure validation PASSED"
else
    echo "❌ TeX structure validation FAILED"
    VALIDATION_PASSED=false
fi
echo ""

# 2. Citation Validation
echo "======================================================================"
echo "2/4: CITATION VALIDATION"
echo "======================================================================"
if python3 "$SCRIPT_DIR/validators/validate_citations.py" \
    --tex-dir "$PROJECT_ROOT/thesis/chapters/" \
    --bibliography "$PROJECT_ROOT/bibliography/citations.yaml" \
    --ontology "$PROJECT_ROOT/ontology/citations.jsonld"; then
    echo "✅ Citation validation PASSED"
else
    echo "❌ Citation validation FAILED"
    VALIDATION_PASSED=false
fi
echo ""

# 3. RDF/SHACL Validation
echo "======================================================================"
echo "3/4: RDF/SHACL VALIDATION"
echo "======================================================================"
if python3 "$SCRIPT_DIR/validators/validate_rdf.py" \
    --ontology "$PROJECT_ROOT/ontology/" \
    --shapes "$PROJECT_ROOT/ontology/catty-thesis-shapes.shacl"; then
    echo "✅ RDF/SHACL validation PASSED"
else
    echo "❌ RDF/SHACL validation FAILED"
    VALIDATION_PASSED=false
fi
echo ""

# 4. Bidirectional Consistency Validation
echo "======================================================================"
echo "4/4: BIDIRECTIONAL CONSISTENCY VALIDATION"
echo "======================================================================"
if python3 "$SCRIPT_DIR/validators/validate_consistency.py" \
    --tex-dir "$PROJECT_ROOT/thesis/chapters/" \
    --ontology "$PROJECT_ROOT/ontology/" \
    --bibliography "$PROJECT_ROOT/bibliography/citations.yaml" \
    --mapping "$SCRIPT_DIR/tex-rdf-mapping.yaml"; then
    echo "✅ Consistency validation PASSED"
else
    echo "❌ Consistency validation FAILED"
    VALIDATION_PASSED=false
fi
echo ""

# Summary
echo "======================================================================"
echo "VALIDATION SUMMARY"
echo "======================================================================"

if [ "$VALIDATION_PASSED" = true ]; then
    echo ""
    echo "🎉 ALL VALIDATIONS PASSED 🎉"
    echo ""
    echo "The thesis infrastructure is working correctly:"
    echo "  ✓ TeX structure is valid"
    echo "  ✓ Citations are consistent across TeX, YAML, and RDF"
    echo "  ✓ RDF conforms to SHACL shapes"
    echo "  ✓ TeX ↔ RDF bidirectional consistency verified"
    echo ""
    echo "Ready for thesis content development!"
    echo ""
    exit 0
else
    echo ""
    echo "❌ VALIDATION FAILED ❌"
    echo ""
    echo "One or more validators reported errors."
    echo "Please fix the issues above before proceeding."
    echo ""
    exit 1
fi
