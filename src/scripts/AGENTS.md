SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: AGPL-3.0-or-later

# AGENTS.md - Scripts Directory

## Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE FORMALLY DEFINED IN:
- **[Formal Document Policy](../../docs/formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](../../docs/document-policy-implementation.html)** - Operational rules and validation

## Scope
This directory contains utility scripts and build tools for the Categorical Reasoner.
All content is classified as **Software** under the formal model.

## Classification
- **Type:** Software (A_soft)
- **License:** AGPL v3.0 or later
- **Format:** Shell scripts, Python scripts, Java utilities
- **Copyrightable:** ⊤

## Content Types
- Build and compilation scripts
- Validation and linting scripts
- Documentation generation tools
- Utility scripts for development tasks
- Deployment scripts

## Structural Requirements
- All scripts must include SPDX license headers
- Scripts must be documented with usage instructions
- Exit codes must follow standard conventions (0 = success, non-zero = failure)
- Scripts should be deterministic and idempotent where possible

## Constraints
- All files must include AGPL v3.0 license headers
- Scripts must handle errors gracefully
- No hardcoded paths; use relative paths or configuration
- Scripts should validate their environment before execution

## Validation
- Script syntax validation (bash -n, python -m py_compile)
- License header completeness check
- Executable permission where appropriate
- Environment validation logic present

## See Also
- [Formal Document Policy](../../docs/formal-document-policy.html) - Mathematical model
- [Implementation Guide](../../docs/document-policy-implementation.html) - Rules and procedures
- [Root AGENTS.md](../../AGENTS.md) - Repository-wide policies
- [Source AGENTS.md](../AGENTS.md) - Parent directory policy
