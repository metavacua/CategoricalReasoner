<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: AGPL-3.0-only -->
<!-- AGENTS.md is licensed as documentation (AGPL) but is semantically part of the configuration/codebase; agents must treat it as authoritative configuration. -->

# AGENTS.md - HelloWorld Source Directory

This file governs the source directory for the helloworld proof-of-concept crate.

## Inheritance

- **Parent**: `/AGENTS.md` (root)
- **Parent**: `/docs/helloworld/AGENTS.md` (child crate)
- **Proof-theoretic status**: Closed branch (complete proof, normalizing term)
- **License**: AGPL 3.0

## Licensing

All contents of this directory are licensed under the GNU Affero General Public License v3.0 (AGPLv3). This represents the "closed branch" of the proof-theoretic model - the transformation from documentation (open, CC BY-SA) to executable code (closed, AGPL).

As stated in root AGENTS.md:

> The `docs/ → src/` transformation is **proof completion**: open branches (CC BY-SA) are closed into programs (AGPL). Subformula property guarantees constraints are preserved, not weakened, under this transformation.

## Contents

- `HelloWorld.java` - Minimal Java class serving as the "witness" proof

## Validation

Standard Java compilation serves as the validator for this source directory. The successful compilation of HelloWorld.java demonstrates proof completion.

## See Also

- `/AGENTS.md` - Root configuration
- `/docs/helloworld/AGENTS.md` - Parent crate constraints
