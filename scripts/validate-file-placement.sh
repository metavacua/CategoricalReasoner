#!/bin/bash
# SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean
# SPDX-License-Identifier: AGPL-3.0-or-later
#
# validate-file-placement.sh
# Validates file placement against formal document policy
#
# Usage: ./scripts/validate-file-placement.sh

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Error counter
ERRORS=0

# Root directory
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define special files allowed at root
SPECIAL_ROOT_FILES=(
    "README.md"
    "LICENSE"
    "AGENTS.md"
    "CONTRIBUTING.md"
    "CHANGELOG.md"
    "CODE_OF_CONDUCT.md"
    "AUTHORS.md"
    ".gitignore"
    "pom.xml"
    "build.gradle"
    "build.gradle.kts"
    ".pre-commit-config.yaml"
    "pre-commit-config.yaml"
    "codemeta.json"
)

echo "=== File Placement Validation ==="
echo ""

# Check for arbitrary LLM-generated files in root
echo "Checking for arbitrary LLM-generated files in root..."
ARBITRARY_FILES=$(find "$ROOT_DIR" -maxdepth 1 -type f \( -name "*.md" -o -name "*.txt" -o -name "*.html" \) -not -name "README.md" -not -name "LICENSE" -not -name "AGENTS.md" -not -name "CONTRIBUTING.md" -not -name "CHANGELOG.md" -not -name "CODE_OF_CONDUCT.md" -not -name "AUTHORS.md" -not -name ".gitignore" 2>/dev/null || true)

if [ -n "$ARBITRARY_FILES" ]; then
    echo -e "${RED}ERROR: Found arbitrary LLM-generated files in root:${NC}"
    echo "$ARBITRARY_FILES"
    ERRORS=$((ERRORS + 1))
else
    echo -e "${GREEN}✓ No arbitrary LLM-generated files in root${NC}"
fi
echo ""

# Check documentation placement
echo "Checking documentation placement..."
DOCS_OUTSIDE=$(find "$ROOT_DIR/docs" -type f \( -name "*.md" -o -name "*.html" -o -name "*.tex" \) ! -path "*/.*" 2>/dev/null || true)
NON_DOC_IN_DOCS=$(find "$ROOT_DIR" -maxdepth 1 -type f \( -name "*.md" -o -name "*.html" \) ! -name "README.md" -not -name "LICENSE" -not -name "AGENTS.md" 2>/dev/null || true)

if [ -n "$NON_DOC_IN_DOCS" ]; then
    echo -e "${RED}ERROR: Found non-documentation files outside docs/:${NC}"
    echo "$NON_DOC_IN_DOCS"
    ERRORS=$((ERRORS + 1))
else
    echo -e "${GREEN}✓ All documentation files correctly placed in docs/${NC}"
fi
echo ""

# Check software placement
echo "Checking software placement..."
SOFTWARE_OUTSIDE=$(find "$ROOT_DIR" -maxdepth 1 -type f \( -name "*.java" -o -name "*.class" -o -name "*.jar" \) ! -path "*/src/*" 2>/dev/null || true)

if [ -n "$SOFTWARE_OUTSIDE" ]; then
    echo -e "${RED}ERROR: Found software files outside src/:${NC}"
    echo "$SOFTWARE_OUTSIDE"
    ERRORS=$((ERRORS + 1))
else
    echo -e "${GREEN}✓ All software files correctly placed in src/${NC}"
fi
echo ""

# Check for README.md in subdirectories
echo "Checking for AGENTS.md files in content directories..."
MISSING_AGENTS=0

for dir in "$ROOT_DIR/docs"/* "$ROOT_DIR/src"/*; do
    if [ -d "$dir" ]; then
        AGENTS_MD="$dir/AGENTS.md"
        if [ ! -f "$AGENTS_MD" ]; then
            echo -e "${YELLOW}WARNING: Missing AGENTS.md in $(basename "$dir")${NC}"
            MISSING_AGENTS=$((MISSING_AGENTS + 1))
        fi
    fi
done

if [ $MISSING_AGENTS -eq 0 ]; then
    echo -e "${GREEN}✓ All content directories have AGENTS.md${NC}"
else
    echo -e "${YELLOW}WARNING: $MISSING_AGENTS directories missing AGENTS.md${NC}"
fi
echo ""

# Summary
echo "=== Validation Summary ==="
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}All file placement checks passed!${NC}"
    exit 0
else
    echo -e "${RED}$ERRORS file placement validation error(s) found${NC}"
    exit 1
fi
