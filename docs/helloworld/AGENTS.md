<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->
<!-- AGENTS.md is licensed as documentation (CC BY-SA) but is semantically part of the configuration/codebase; agents must treat it as authoritative configuration, and derived code (src/, PRs) inherits AGPLv3 where applicable. -->

# AGENTS.md - HelloWorld Project

## Scope

This directory contains a minimal viable helloworld project demonstrating the AGENTS.md infrastructure requirements for the CategoricalReasoner repository. It serves as a proof-of-concept for the docs → src proof transformation and implements the Curry-Howard license-by-compilation architecture.

## Constraints

This project inherits all constraints from:
- Root AGENTS.md
- docs/AGENTS.md

## License-by-Compilation (Curry-Howard Architecture)

This project implements a formally verifiable license classification system based on the Curry-Howard correspondence:

### Proof-Theoretic Classification

| Proof Status | Maven Outcome | License | File Types |
|--------------|--------------|---------|------------|
| CLOSED | `mvn compile` exit 0 | AGPL-3.0-or-later | Java source |
| OPEN | No compilation | CC-BY-SA-4.0 | Documentation, configs |
| INVALID | `mvn compile` exit ≠ 0 | Reject (GIGO) | Ill-typed Java |

### Implementation Components

1. **license-maven-plugin** (v5.0.0):
   - Bound to `compile` phase for AGPL header application
   - Bound to `process-sources` for CC-BY-SA header application
   - License templates in `src/license/`

2. **spdx-maven-plugin** (v1.0.3):
   - Generates SBOM with proof-theoretic provenance
   - Captures classification metadata in SPDX annotations

3. **Pre-commit Compilation Gate** (`scripts/compilation-gate.sh`):
   - Classifies changes by file type
   - Runs Maven compilation on Java changes
   - Rejects commits with compilation failures (GIGO)
   - Generates proof metadata in `.git/proof-theoretic-metadata.json`

4. **Proof-Theoretic Infrastructure** (`src/main/java/proof/`):
   - `LicenseClassification`: Enum for proof status and license mapping
   - `ProofMetadata`: Record for capturing normalization results
   - JML specifications in `src/main/jml/` for KeY integration

5. **Formal Verification Workflow** (`.github/workflows/formal-verification.yml`):
   - License-by-compilation verification
   - KeY integration placeholder (Java 25 support pending)
   - Proof-theoretic test suite
   - REUSE compliance validation

### Sequent Calculus Formalization

**Closed Branch (AGPL):**
```
Γ ⊢ Java : Type
Γ ⊢ compile(Java) = 0
─────────────────────
Γ ⊢ AGPL(Java)
```

**Open Branch (CC-BY-SA):**
```
Γ ⊢ Doc : Documentation
Γ ⊢ ¬compile(Doc)
─────────────────────
Γ ⊢ CC-BY-SA(Doc)
```

**GIGO Rejection:**
```
Γ ⊢ Java : ¬Type
Γ ⊢ compile(Java) ≠ 0
─────────────────────
⊥
```

### Dependency Infrastructure

The project includes dependencies for proof-theoretic infrastructure:

- **JavaPoet** (v1.13.0): Code generation for guaranteed-valid Java
- **Apache Jena** (v5.2.0): Semantic web and RDF processing
- **OpenLlet** (v3.1.0): OWL reasoning
- **KeY** (v2.13.0): Formal verification (provided scope, Java 25 pending)

## Licensing

- Documentation files (this AGENTS.md, markdown, YAML configs): CC BY-SA 4.0
- Source code (src/main/java/): AGPL-3.0-or-later
- Configuration follows REUSE Specification 3.0+
- License classification determined by compilation success (Curry-Howard)

## Standards

- Maven with Java 25
- REUSE headers on all files (REUSE.toml format, not deprecated dep5)
- pre-commit framework for validation
- SPDX 3.1 for SBOM generation
- GitHub dependency graphs and CodeQL (workflows in repository root .github/workflows/)

## Documentation

- `PROOF-THEORETIC-LICENSE.md`: Formal Curry-Howard documentation with sequent calculus notation

## See Also

- Root AGENTS.md
- docs/AGENTS.md
- PROOF-THEORETIC-LICENSE.md

