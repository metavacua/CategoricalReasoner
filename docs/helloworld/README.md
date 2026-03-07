<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->

# HelloWorld - Proof-of-Concept: docs → src Transformation

## Overview

This directory demonstrates the foundational proof-theoretic model of the Categorical Reasoner "Catty" thesis, where documentation (open branch) can be transformed into executable code (closed branch).

## Proof-Theoretic Model

According to the AGENTS.md specification, file system structure corresponds to proof trees/proof nets:

| Branch Type | Proof-Theoretic Meaning | License | Content |
|-------------|-------------------------|---------|---------|
| **Open** | Incomplete proof, no program term | CC BY-SA v4 | Documentation, media, specifications |
| **Closed** | Complete proof, normalizing term | AGPL v3 | Executable code, software |

## Transformation Pattern

The `docs/helloworld/src/` directory contains the "witness" - the Java class that proves documentation is transformable into executable code:

- `docs/helloworld/` = **Open branch** (incomplete proof) → CC BY-SA 4.0
- `docs/helloworld/src/` = **Closed branch** (complete proof) → AGPL 3.0

This transformation is **proof completion**: open branches (CC BY-SA) are closed into programs (AGPL). The subformula property guarantees constraints are preserved, not weakened, under this transformation.

## Purpose

This minimal example serves as the template pattern for all other documentation sections (dissertation, structural-rules) to follow. Each documentation section should have:

1. A README.md explaining the proof-theoretic model
2. A src/ subdirectory containing the executable "witness"
3. An AGENTS.md inheriting constraints from root
4. A src/AGENTS.md specifying AGPL 3.0 license

## See Also

- `/AGENTS.md` - Root configuration
- `/docs/AGENTS.md` - Documentation directory constraints
- `/docs/helloworld/AGENTS.md` - This child crate's constraints
- `/docs/helloworld/src/AGENTS.md` - Source directory constraints
