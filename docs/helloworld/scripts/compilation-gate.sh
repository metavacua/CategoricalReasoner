#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
# SPDX-License-Identifier: CC-BY-SA-4.0
#
# Pre-commit compilation gate implementing Curry-Howard proof-theoretic classification
# Exit codes:
# 0 - Proceed (closed proof or open branch)
# 1 - Reject (ill-typed code, GIGO)
# 2 - Warning (compilation failure but documentation-only)

set -euo pipefail

# Colors for output
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly NC='\033[0m' # No Color

# Log functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Detect if Java files changed
has_java_changes() {
    # Get list of staged files
    local staged_files
    staged_files=$(git diff --cached --name-only --diff-filter=ACM)

    # Check if any .java files are staged
    if echo "$staged_files" | grep -q '\.java$'; then
        return 0
    else
        return 1
    fi
}

# Detect if documentation files changed
has_doc_changes() {
    local staged_files
    staged_files=$(git diff --cached --name-only --diff-filter=ACM)

    # Check for documentation files
    if echo "$staged_files" | grep -E '\.(md|yaml|yml|toml)$' | grep -q .; then
        return 0
    else
        return 1
    fi
}

# Classify the proof-theoretic status
classify_proof() {
    local has_java
    local has_doc

    has_java=0
    has_doc=0

    if has_java_changes; then
        has_java=1
    fi

    if has_doc_changes; then
        has_doc=1
    fi

    if [ $has_java -eq 1 ]; then
        if [ $has_doc -eq 1 ]; then
            echo "MIXED"
        else
            echo "JAVA_ONLY"
        fi
    elif [ $has_doc -eq 1 ]; then
        echo "DOC_ONLY"
    else
        echo "NONE"
    fi
}

# Run Maven compilation
run_compilation() {
    log_info "Running Maven compilation as proof normalization..."

    # Use offline mode if possible for speed
    if mvn compile -q -o 2>/dev/null; then
        log_success "Compilation succeeded (proof normalized)"
        return 0
    else
        # Try with online mode if offline fails
        if mvn compile -q 2>&1; then
            log_success "Compilation succeeded (proof normalized)"
            return 0
        else
            log_error "Compilation failed (ill-typed term)"
            return 1
        fi
    fi
}

# Generate proof-theoretic metadata
generate_metadata() {
    local proof_status
    local license
    local normalization

    proof_status="$1"
    license="$2"
    normalization="$3"

    local metadata_file
    metadata_file=".git/proof-theoretic-metadata.json"

    # Create directory if it doesn't exist
    mkdir -p "$(dirname "$metadata_file")"

    # Generate metadata
    cat > "$metadata_file" << EOF
{
  "proof-theoretic": {
    "classification": "$proof_status",
    "license": "$license",
    "normalization": "$normalization",
    "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")",
    "git-branch": "$(git rev-parse --abbrev-ref HEAD)",
    "git-commit": "$(git rev-parse HEAD)"
  },
  "curry-howard": {
    "correspondence": "proof-term",
    "normalization-strategy": "maven-compile",
    "type-system": "java-25"
  }
}
EOF

    log_info "Proof-theoretic metadata written to $metadata_file"
}

# Main execution
main() {
    log_info "=== Curry-Howard Pre-commit Compilation Gate ==="

    local classification
    classification=$(classify_proof)

    log_info "Change classification: $classification"

    case "$classification" in
        JAVA_ONLY)
            log_info "Java changes detected - requires compilation (proof normalization)"
            if run_compilation; then
                generate_metadata "CLOSED" "AGPL-3.0-or-later" "SUCCESS"
                log_success "Closed proof verified - AGPL-3.0-or-later applies"
                exit 0
            else
                generate_metadata "INVALID" "NONE" "FAILURE"
                log_error "Ill-typed code detected - commit rejected (GIGO)"
                log_error "Please fix compilation errors before committing"
                exit 1
            fi
            ;;

        DOC_ONLY)
            log_info "Documentation-only changes - open branch (CC BY-SA 4.0)"
            generate_metadata "OPEN" "CC-BY-SA-4.0" "N/A"
            log_success "Open branch verified - CC-BY-SA-4.0 applies"
            exit 0
            ;;

        MIXED)
            log_info "Mixed changes detected - requires compilation (proof normalization)"
            if run_compilation; then
                generate_metadata "CLOSED" "AGPL-3.0-or-later" "SUCCESS"
                log_success "Closed proof verified - AGPL-3.0-or-later applies to compiled code"
                log_warning "Documentation remains under CC-BY-SA-4.0"
                exit 0
            else
                generate_metadata "INVALID" "NONE" "FAILURE"
                log_error "Ill-typed code detected - commit rejected (GIGO)"
                log_error "Please fix compilation errors before committing"
                exit 1
            fi
            ;;

        NONE)
            log_warning "No files staged - nothing to classify"
            exit 0
            ;;

        *)
            log_error "Unknown classification: $classification"
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
