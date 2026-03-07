<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->
<!-- AGENTS.md is licensed as documentation (CC BY-SA) but is semantically part of the configuration/codebase; agents must treat it as authoritative configuration, and derived code (src/, PRs) inherits AGPLv3 where applicable. -->

# AGENTS.md - HelloWorld Child Crate

This file defines the child crate for the helloworld proof-of-concept, inheriting constraints from the root AGENTS.md.

## Inheritance

- **Parent**: `/AGENTS.md` (root)
- **Proof-theoretic status**: Open branch (incomplete proof)
- **License**: CC BY-SA 4.0

## Crate Type

This is a **child crate** under the docs/ directory, demonstrating the docs→src transformation pattern. As per the root AGENTS.md:

> Subdirectory AGENTS.md derivatives may define child crates satisfying subformula property: child crates inherit parent fixity requirements, constraints strengthen only

## Scope

This crate contains:

- `README.md` - Documentation explaining the proof-theoretic model
- `src/` - Closed proof branch containing executable witness code

## Constraints

All constraints from root AGENTS.md apply, with the following clarifications:

1. **Technology Stack**: Java 25 ecosystem (witness code must compile)
2. **Validation**: Standard Java compilation serves as the validator
3. **Transformation**: docs → src is proof completion (open → closed)

## Subformula Property

The child crate must satisfy the subformula property: constraints inherited from parent are strengthened, not weakened.

## See Also

- `/AGENTS.md` - Root configuration
- `/docs/AGENTS.md` - Documentation directory
- `src/AGENTS.md` - Source directory (AGPL 3.0)
