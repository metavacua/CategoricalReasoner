<!--
SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
SPDX-License-Identifier: CC-BY-SA-4.0
-->

# AGENTS.md - Catty HelloWorld

## Scope

This is a minimal infrastructure smoke test subproject demonstrating the docs→src transformation pipeline with complete REUSE.software, pre-commit, and GitHub Actions integration.

## Core Constraints

Inherits all constraints from root AGENTS.md with the following specific clarifications:

- **Minimal Implementation**: This is NOT a formal methods project. The Java code is a trivial test payload.
- **Single Purpose**: Validate that REUSE, pre-commit, and GitHub Actions work together correctly.
- **No Complex Dependencies**: Uses only standard Java API with no external dependencies.
- **No Annotation Processing**: This does NOT use Java Compiler API or JSR 269 annotation processing.
- **No Semantic Web**: This does NOT use Jena, SPARQL, or semantic web technologies.

## License Transformation

- **docs/**: CC BY-SA 4.0 (open proof branches)
- **src/**: AGPL-3.0-or-later (closed proof branches with executable code)

## Validation

Standard tools only:
- `mvn clean compile` - Maven compilation
- `pre-commit run --all-files` - Pre-commit hooks
- `reuse lint` - REUSE.software compliance
- GitHub Actions CI (on push/PR)

## See Also

- Root `AGENTS.md` - Parent crate constraints
- Root `README.md` - Project overview
