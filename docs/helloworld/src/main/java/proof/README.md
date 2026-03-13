<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->

# Proof-Theoretic Infrastructure

This package contains the core infrastructure for the Curry-Howard license-by-compilation architecture.

## Package Structure

### `proof.licensing`
- **LicenseClassification**: Enum representing proof-theoretic license classifications
  - `CLOSED_AGPL`: Successful compilation (closed proof)
  - `OPEN_CCBYSA`: Documentation only (open branch)
  - `INVALID`: Compilation failure (ill-typed)

### `proof.metadata`
- **ProofMetadata**: Record capturing proof normalization results
  - Classification, timestamp, git context
  - JSON generation for SBOM integration
  - Provenance tracking

## Curry-Howard Correspondence

This package implements the formal correspondence between:

- **Logic**: Propositions, proofs, proof normalization
- **Computation**: Types, programs, compilation

### Proof Classification Rules

```
Γ ⊢ Java : Type      Γ ⊢ compile(Java) = 0
──────────────────────────────────────────
          Γ ⊢ AGPL(Java)

Γ ⊢ Doc : Documentation  Γ ⊢ ¬compile(Doc)
────────────────────────────────────────────
          Γ ⊢ CC-BY-SA(Doc)

Γ ⊢ Java : ¬Type      Γ ⊢ compile(Java) ≠ 0
─────────────────────────────────────────────
              ⊥
```

## Usage

```java
// Classify based on compilation result
LicenseClassification classification = compilationSucceeded
    ? LicenseClassification.CLOSED_AGPL
    : LicenseClassification.INVALID;

// Create metadata for provenance tracking
ProofMetadata metadata = ProofMetadata.create(
    classification,
    "main",
    gitCommitHash
);

// Generate JSON for SBOM
String json = metadata.toJson();
```

## Integration

- **Maven Plugins**: license-maven-plugin, spdx-maven-plugin
- **Pre-commit**: scripts/compilation-gate.sh
- **GitHub Actions**: .github/workflows/formal-verification.yml

## Documentation

- `PROOF-THEORETIC-LICENSE.md`: Complete formal specification
- `../AGENTS.md`: Project constraints
- `/home/engine/project/AGENTS.md`: Repository-wide constraints
