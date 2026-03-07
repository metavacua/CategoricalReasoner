<!--
SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
SPDX-License-Identifier: CC-BY-SA-4.0
-->

# AGENTS.md - Catty HelloWorld Source

## Scope

Source directory for the HelloWorld infrastructure smoke test.

## Core Constraints

Inherits from parent `docs/helloworld/AGENTS.md` with the following:

- **AGPL-3.0-or-later**: All source code in this directory is licensed under AGPL-3.0-or-later
- **Minimal Java**: Uses only Java 25 standard library, no external dependencies
- **Proof Completion**: This directory represents closed proof branches (executable programs)

## Validation

- Maven compilation with Java 25
- REUSE.software compliance with AGPL headers
- Pre-commit hooks (reuse, trailing-whitespace, end-of-file-fixer)

## See Also

- `../../AGENTS.md` - Parent crate
- `../../../AGENTS.md` - Root crate
