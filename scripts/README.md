SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: AGPL-3.0-or-later

# Validation Scripts

This directory contains automated validation scripts for enforcing the formal document policy defined in the CategoricalReasoner repository.

## Scripts

### File Placement Validation

- **validate-file-placement.sh** - Validates that files are placed in correct directories
  - Checks for arbitrary LLM-generated files in repository root
  - Ensures all documentation is in `docs/`
  - Ensures all software is in `src/`
  - Validates AGENTS.md presence in content directories

### License Header Validation

- **validate-license-headers.sh** - Validates REUSE specification compliance
  - Checks SPDX copyright headers
  - Validates SPDX license identifiers
  - Ensures licenses match file classifications

### Master Test Script

- **test-all-validation.sh** - Runs all validation procedures
  - File placement validation
  - License header validation
  - AGENTS.md coverage check
  - Formal model references check
  - License consistency validation
  - REUSE compliance check

## Configuration

- **.pre-commit-config.yaml** - Pre-commit hooks configuration
  - Defines hooks for automatic validation on commit
  - Configured for REUSE linting, file placement, license headers

- **.github/workflows/validate.yml** - GitHub Actions workflow
  - Automated validation on push and pull requests
  - Runs all validation scripts
  - Validates HTML files
  - Checks for arbitrary files in root

## Documentation

- **../docs/validation-implementation-guide.html** - Complete validation implementation guide
  - Describes all validation scripts
  - Documents CI/CD integration
  - Provides testing procedures
  - Includes troubleshooting guide

## Usage

Run all validation:
```bash
./test-all-validation.sh
```

Run individual validation:
```bash
./validate-file-placement.sh
./validate-license-headers.sh
```

Install pre-commit hooks:
```bash
pre-commit install
```

## Classification

All scripts are classified as **Software** under the formal document policy and are licensed under AGPL v3.0-or-later.

## See Also

- [Formal Document Policy](../docs/formal-document-policy.html) - Mathematical model
- [Implementation Guide](../docs/document-policy-implementation.html) - Rules and procedures
- [Validation Procedures](../docs/validation-procedures.html) - Compliance documentation
- [Validation Implementation Guide](../docs/validation-implementation-guide.html) - Scripts and CI/CD
- [Root AGENTS.md](../../AGENTS.md) - Repository-wide policies
