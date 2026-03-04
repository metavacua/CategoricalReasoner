#!/bin/bash
# SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean
# SPDX-License-Identifier: AGPL-3.0-or-later
#
# validate-license-headers.sh
# Validates REUSE specification compliance and license header consistency
#
# Usage: ./scripts/validate-license-headers.sh

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

# Define file patterns and expected licenses
declare -A FILE_PATTERNS=(
    ["docs/*.html"]="CC-BY-SA-4.0"
    ["docs/*.md"]="CC-BY-SA-4.0"
    ["docs/*.tex"]="CC-BY-SA-4.0"
    ["src/**/*.java"]="AGPL-3.0-or-later"
    ["src/**/*.sh"]="AGPL-3.0-or-later"
    ["src/**/*.py"]="AGPL-3.0-or-later"
    ["src/AGENTS.md"]="AGPL-3.0-or-later"
    ["src/benchmarks/AGENTS.md"]="AGPL-3.0-or-later"
    ["src/scripts/AGENTS.md"]="AGPL-3.0-or-later"
    ["src/tests/AGENTS.md"]="AGPL-3.0-or-later"
    ["docs/AGENTS.md"]="CC-BY-SA-4.0"
    ["docs/dissertation/AGENTS.md"]="CC-BY-SA-4.0"
    ["docs/structural-rules/part/AGENTS.md"]="CC-BY-SA-4.0"
    ["docs/structural-rules/part/chap-*/AGENTS.md"]="CC-BY-SA-4.0"
    ["docs/structural-rules/part/chap-*/sec*/AGENTS.md"]="CC-BY-SA-4.0"
    ["AGENTS.md"]="CC-BY-SA-4.0"
)

echo "=== License Header Validation ==="
echo ""

# Function to check SPDX header
check_spdx_header() {
    local file="$1"
    local expected_license="$2"
    
    # Check for SPDX headers
    if grep -q "SPDX-FileCopyrightText:" "$file"; then
        echo -e "${GREEN}  ✓ SPDX copyright header present"
        SPDX_COPYRIGHT=$(grep "SPDX-FileCopyrightText:" "$file" | sed 's/SPDX-FileCopyrightText: *//')
        echo -e "     ${NC}Copyright: $SPDX_COPYRIGHT"
    else
        echo -e "${RED}  ✗ Missing SPDX copyright header${NC}"
        ERRORS=$((ERRORS + 1))
    fi
    
    if grep -q "SPDX-License-Identifier:" "$file"; then
        SPDX_LICENSE=$(grep "SPDX-License-Identifier:" "$file" | sed 's/SPDX-License-Identifier: *//')
        echo -e "     ${NC}License: $SPDX_LICENSE"
        
        # Validate license identifier
        case "$expected_license" in
            "CC-BY-SA-4.0")
                if [[ ! "$SPDX_LICENSE" =~ ^CC-BY-SA-4\.0$ ]]; then
                    echo -e "${YELLOW}  ⚠ Expected CC-BY-SA-4.0, got $SPDX_LICENSE${NC}"
                    ERRORS=$((ERRORS + 1))
                else
                    echo -e "${GREEN}  ✓ Correct license${NC}"
                fi
                ;;
            "AGPL-3.0-or-later")
                if [[ ! "$SPDX_LICENSE" =~ ^AGPL-3\.0(-or-later)?$ ]]; then
                    echo -e "${YELLOW}  ⚠ Expected AGPL-3.0-or-later, got $SPDX_LICENSE${NC}"
                    ERRORS=$((ERRORS + 1))
                else
                    echo -e "${GREEN}  ✓ Correct license${NC}"
                fi
                ;;
        esac
    else
        echo -e "${RED}  ✗ Missing SPDX license identifier${NC}"
        ERRORS=$((ERRORS + 1))
    fi
}

# Check files in patterns
echo "Checking license headers for policy files..."
echo ""

# Root AGENTS.md
if [ -f "$ROOT_DIR/AGENTS.md" ]; then
    echo "Checking: AGENTS.md"
    check_spdx_header "$ROOT_DIR/AGENTS.md" "CC-BY-SA-4.0"
    echo ""
fi

# docs/AGENTS.md
if [ -f "$ROOT_DIR/docs/AGENTS.md" ]; then
    echo "Checking: docs/AGENTS.md"
    check_spdx_header "$ROOT_DIR/docs/AGENTS.md" "CC-BY-SA-4.0"
    echo ""
fi

# src/AGENTS.md
if [ -f "$ROOT_DIR/src/AGENTS.md" ]; then
    echo "Checking: src/AGENTS.md"
    check_spdx_header "$ROOT_DIR/src/AGENTS.md" "AGPL-3.0-or-later"
    echo ""
fi

# docs AGENTS.md files
echo "Checking documentation AGENTS.md files..."
for dir in "$ROOT_DIR/docs"/*; do
    if [ -d "$dir" ]; then
        agents_md="$dir/AGENTS.md"
        if [ -f "$agents_md" ]; then
            echo "Checking: $(realpath --relative-to="$ROOT_DIR" "$agents_md")"
            check_spdx_header "$agents_md" "CC-BY-SA-4.0"
            echo ""
        fi
    fi
done

# src AGENTS.md files
echo "Checking software AGENTS.md files..."
for dir in "$ROOT_DIR/src"/*; do
    if [ -d "$dir" ]; then
        agents_md="$dir/AGENTS.md"
        if [ -f "$agents_md" ]; then
            echo "Checking: $(realpath --relative-to="$ROOT_DIR" "$agents_md")"
            check_spdx_header "$agents_md" "AGPL-3.0-or-later"
            echo ""
        fi
    fi
done

# Check for files without SPDX headers
echo "Checking for files without SPDX headers..."
echo ""

# Find text files that might need headers
MISSING_HEADERS=$(find "$ROOT_DIR/docs" "$ROOT_DIR/src" -type f \( -name "*.md" -o -name "*.html" -o -name "*.tex" -o -name "*.java" -o -name "*.sh" \) ! -exec grep -q "SPDX-License-Identifier:" {} \; 2>/dev/null || true)

if [ -n "$MISSING_HEADERS" ]; then
    echo -e "${GREEN}✓ All checked files have SPDX headers${NC}"
else
    echo -e "${YELLOW}⚠ Warning: Some files may be missing SPDX headers:${NC}"
    echo "$MISSING_HEADERS"
fi

# Summary
echo ""
echo "=== Validation Summary ==="
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}All license header checks passed!${NC}"
    exit 0
else
    echo -e "${RED}$ERRORS license header validation error(s) found${NC}"
    exit 1
fi
