#!/bin/bash
# SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean
# SPDX-License-Identifier: AGPL-3.0-or-later
#
# test-all-validation.sh
# Master script to run all validation procedures
#
# Usage: ./scripts/test-all-validation.sh

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Root directory
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Test counter
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

echo "========================================="
echo "  Document Policy Validation Tests"
echo "========================================="
echo ""

# Test 1: File Placement Validation
echo "Test 1: File Placement Validation"
echo "-----------------------------------"
if ./scripts/validate-file-placement.sh; then
    echo -e "${GREEN}✓ File placement validation: PASSED${NC}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ File placement validation: FAILED${NC}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo ""

# Test 2: License Header Validation
echo "Test 2: License Header Validation"
echo "-----------------------------------"
if ./scripts/validate-license-headers.sh; then
    echo -e "${GREEN}✓ License header validation: PASSED${NC}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ License header validation: FAILED${NC}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo ""

# Test 3: AGENTS.md Coverage
echo "Test 3: AGENTS.md Coverage"
echo "-----------------------------------"
AGENTS_MISSING=0
for dir in docs/* src/*; do
    if [ -d "$dir" ] && [ "$(basename "$dir")" != "src" ] && [ "$(basename "$dir")" != "docs" ]; then
        if [ ! -f "$dir/AGENTS.md" ]; then
            echo -e "${YELLOW}  ✗ Missing AGENTS.md in $(basename "$dir")${NC}"
            AGENTS_MISSING=$((AGENTS_MISSING + 1))
        fi
    fi
done

if [ $AGENTS_MISSING -eq 0 ]; then
    echo -e "${GREEN}✓ AGENTS.md coverage: PASSED${NC}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ AGENTS.md coverage: FAILED ($AGENTS_MISSING missing)${NC}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo ""

# Test 4: REUSE Compliance
echo "Test 4: REUSE Compliance Check"
echo "-----------------------------------"
if command -v reuse &> /dev/null; then
    if reuse lint &> /dev/null; then
        echo -e "${GREEN}✓ REUSE compliance: PASSED${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ REUSE compliance: FAILED${NC}"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    echo -e "${YELLOW}  ⚠ REUSE tool not installed (skipped)${NC}"
    echo -e "  Install with: pip install reuse${NC}"
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo ""

# Test 5: Formal Model References
echo "Test 5: Formal Model References"
echo "-----------------------------------"
FORMAL_FILES=(
    "AGENTS.md"
    "docs/AGENTS.md"
    "src/AGENTS.md"
    "docs/formal-document-policy.html"
    "docs/document-policy-implementation.html"
    "docs/validation-procedures.html"
)

ALL_REFERENCES=0
for file in "${FORMAL_FILES[@]}"; do
    if [ -f "$ROOT_DIR/$file" ]; then
        if grep -q "formal-document-policy.html\|document-policy-implementation.html\|validation-procedures.html" "$ROOT_DIR/$file"; then
            echo -e "${GREEN}  ✓ $file references formal policy${NC}"
            ALL_REFERENCES=$((ALL_REFERENCES + 1))
        else
            echo -e "${YELLOW}  ⚠ $file may not reference formal policy${NC}"
        fi
    fi
done

if [ $ALL_REFERENCES -eq ${#FORMAL_FILES[@]} ]; then
    echo -e "${GREEN}✓ Formal model references: PASSED${NC}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${YELLOW}✗ Formal model references: PARTIAL ($ALL_REFERENCES/${#FORMAL_FILES[@]})${NC}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo ""

# Test 6: License Consistency
echo "Test 6: License Consistency"
echo "-----------------------------------"
LICENSE_MISMATCH=0

# Check docs files for CC BY-SA v4.0
DOCS_FILES=$(find "$ROOT_DIR/docs" -type f -name "AGENTS.md")
for file in $DOCS_FILES; do
    if grep -q "SPDX-License-Identifier:" "$file" && ! grep -q "SPDX-License-Identifier:.*CC-BY-SA-4\.0" "$file"; then
        echo -e "${YELLOW}  ⚠ $(realpath --relative-to="$ROOT_DIR" "$file") has wrong license${NC}"
        LICENSE_MISMATCH=$((LICENSE_MISMATCH + 1))
    fi
done

# Check src files for AGPL v3.0
SRC_FILES=$(find "$ROOT_DIR/src" -type f -name "AGENTS.md")
for file in $SRC_FILES; do
    if grep -q "SPDX-License-Identifier:" "$file" && ! grep -q "SPDX-License-Identifier:.*AGPL-3\.0" "$file"; then
        echo -e "${YELLOW}  ⚠ $(realpath --relative-to="$ROOT_DIR" "$file") has wrong license${NC}"
        LICENSE_MISMATCH=$((LICENSE_MISMATCH + 1))
    fi
done

if [ $LICENSE_MISMATCH -eq 0 ]; then
    echo -e "${GREEN}✓ License consistency: PASSED${NC}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ License consistency: FAILED ($LICENSE_MISMATCH mismatches)${NC}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo ""

# Summary
echo "========================================="
echo "  Validation Summary"
echo "========================================="
echo -e "  Total Tests: $TOTAL_TESTS${NC}"
echo -e "  ${GREEN}Passed:$NC}   $PASSED_TESTS"
echo -e "  ${RED}Failed:$NC}   $FAILED_TESTS"
echo ""

# Calculate percentage
if [ $TOTAL_TESTS -gt 0 ]; then
    PERCENT=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    echo -e "  Success Rate: ${PERCENT}%${NC}"
fi
echo ""

# Final result
if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}  ALL VALIDATION TESTS PASSED!${NC}"
    echo -e "${GREEN}=========================================${NC}"
    exit 0
else
    echo -e "${RED}=========================================${NC}"
    echo -e "${RED}  $FAILED_TESTS VALIDATION TEST(S) FAILED${NC}"
    echo -e "${RED}=========================================${NC}"
    exit 1
fi
