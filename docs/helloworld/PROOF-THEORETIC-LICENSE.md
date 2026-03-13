<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->

# Proof-Theoretic License Classification

This document formalizes the Curry-Howard correspondence for the Catty HelloWorld project, defining how Maven compilation phases implement proof normalization to determine license classification.

## Curry-Howard Correspondence Overview

The Curry-Howard isomorphism establishes a correspondence between:
- **Logic** (propositions, proofs) and **Computation** (types, programs)
- **Proof normalization** (cut elimination) and **Program execution** (β-reduction)
- **Closed proof terms** and **Executable programs**

In this architecture:
- Java source files with valid type annotations = well-typed λ-terms
- Maven compilation phase = proof normalization procedure
- Successful compilation (exit 0) = β-η normalization succeeds → **Closed Branch**
- Compilation failure = ill-typed/non-terminating → **GIGO** (reject)
- Documentation-only changes = no compilation needed → **Open Branch**

## Sequent Calculus Formalization

### Proof Classification

We define a sequent calculus `Γ ⊢ A` where:
- `Γ` is the context (repository state, dependencies, constraints)
- `A` is the proposition (license classification artifact)

#### Closed Branch Theorem

```
Γ ⊢ Java : Type      (Type correctness)
─────────────────────
Γ ⊢ compile(Java) = 0  (Compilation succeeds)
─────────────────────────────────────────────────────
Γ ⊢ AGPL(Java)        (Closed Branch → AGPL v3)
```

**Interpretation**: If Java source type-checks and compiles successfully, it constitutes a closed proof term and is licensed under AGPL v3.

#### Open Branch Theorem

```
Γ ⊢ Doc : Documentation      (Documentation artifact)
────────────────────────────
Γ ⊢ ¬compile(Doc)           (No compilation performed)
───────────────────────────────────────────────────────
Γ ⊢ CC-BY-SA(Doc)           (Open Branch → CC BY-SA 4.0)
```

**Interpretation**: Documentation artifacts are open branches (incomplete proofs) and remain under CC BY-SA 4.0.

#### GIGO Rejection Theorem

```
Γ ⊢ Java : ¬Type      (Type error)
────────────────────
Γ ⊢ compile(Java) ≠ 0  (Compilation fails)
────────────────────────────────────────
⊥                        (Contradiction → Reject)
```

**Interpretation**: Ill-typed Java code is a contradiction in the proof system and must be rejected.

## Maven Implementation

### Phase Bindings

| Maven Phase | Proof-Theoretic Operation | License Action |
|-------------|--------------------------|----------------|
| `validate` | Context consistency check | Verify AGENTS.md constraints |
| `generate-sources` | Context extension | Generate code from semantic HTML |
| `process-sources` | License classification (open) | Apply CC-BY-SA headers |
| `compile` | **Proof normalization** | Apply AGPL headers on success |
| `test` | Proof verification | Run unit tests as lemmas |
| `package` | Proof closure | Generate SBOM with provenance |

### License Maven Plugin as Cut Rule

The `license-maven-plugin` bound to the `compile` phase implements the **cut rule**:

```
Γ ⊢ Java : Type        (1) Type correctness
Γ ⊢ compile(Java) = 0  (2) Compilation succeeds
────────────────────────────────────────────
Γ ⊢ AGPL-header(Java)  (Cut: Apply AGPL license)
```

The plugin configuration enforces:
- Only apply AGPL headers **after** successful compilation
- CC-BY-SA headers applied to documentation **before** compilation
- License mismatch causes build failure (proof invalidation)

## Pre-Commit Compilation Gate

The pre-commit hook implements proof-theoretic classification:

### Exit Code Semantics

```
Exit 0 + Java changes        → Closed Proof (AGPL)
Exit 0 + Documentation only  → Open Branch (CC-BY-SA)
Exit ≠ 0 + Java changes      → GIGO (Reject commit)
Exit ≠ 0 + Documentation only → Conditional (warn, allow)
```

### Algorithm

1. Detect file types in commit:
   - `*.java` files → Requires compilation
   - `*.md`, `*.yaml`, `*.toml` → Documentation

2. If Java files present:
   - Run `mvn compile`
   - Exit 0 → Proceed (closed proof)
   - Exit ≠ 0 → Reject (ill-typed)

3. If documentation only:
   - Skip compilation (open branch)
   - Verify REUSE compliance
   - Proceed (CC-BY-SA)

4. Generate classification metadata:
   - Proof status: `OPEN` | `CLOSED`
   - License: `CC-BY-SA-4.0` | `AGPL-3.0-or-later`
   - Normalization: `SUCCESS` | `FAILURE` | `N/A`

## SPDX SBOM Provenance

The `spdx-maven-plugin` generates Software Bill of Materials with:

### Proof-Theoretic Metadata

```json
{
  "spdxVersion": "SPDX-3.1",
  "documentNamespace": "https://github.com/metavacua/CategoricalReasoner/docs/helloworld/0.0.1-SNAPSHOT/spdx",
  "documentDescribes": ["helloworld-0.0.1-SNAPSHOT.jar"],
  "annotations": [
    {
      "annotationType": "REVIEW",
      "annotator": "Tool: maven-license-plugin",
      "annotationDate": "2025-03-13T00:00:00Z",
      "annotationComment": "Proof-Theoretic Classification: CLOSED BRANCH. Compilation successful (exit 0), AGPL-3.0-or-later applied via Curry-Howard license-by-compilation."
    }
  ],
  "packages": [
    {
      "SPDXID": "SPDXRef-Package-helloworld",
      "name": "helloworld",
      "downloadLocation": "NOASSERTION",
      "licenseConcluded": "AGPL-3.0-or-later",
      "licenseDeclared": "AGPL-3.0-or-later",
      "copyrightText": "2025-2026 Ian Douglas Lawrence Norman McLean",
      "externalRefs": [
        {
          "referenceCategory": "PROOF-THEORETIC",
          "referenceLocator": "urn:proof:curry-howard:closed-branch",
          "referenceType": "uri"
        }
      ]
    }
  ]
}
```

## KeY Integration (Future)

KeY formal verification will provide proof-theoretic strengthening:

### JML Annotations as Proof Obligations

```java
/*@
  @ requires true;
  @ ensures (\result != null);
  @ public static String hello() {
      return "Hello, World!";
  @ }
*/
```

### Verification Process

```
Java Source
    ↓
JML Annotations (Proof obligations)
    ↓
KeY Proof Search
    ↓
Proof Obligations Satisfied → Strengthened AGPL Artifact
Proof Obligations Failed → Debug Required
```

### Light Linear Logic Bounds

For future LLD integration, proof complexity bounds ensure polytime verification:
- Proof tree depth ≤ O(n)
- Proof branching factor ≤ O(1)
- Verification time ≤ O(n²)

## Subformula Property and Constraint Inheritance

Following the root AGENTS.md, subdirectories satisfy:

```
Parent: Γ ⊢ A
Child:  Γ, Δ ⊢ A ∧ B
```

Where:
- Child inherits parent constraints `Γ`
- Child adds strengthening constraints `Δ`
- Subformula property: `B` contains only subformulas of `A`

This ensures:
- License constraints are preserved, not weakened
- Proof structure remains locally finite
- Global consistency can be proven by induction

## Example Workflow

### Scenario 1: New Java Feature

```bash
# 1. Modify Java source
echo "public class Feature { }" > src/main/java/Feature.java

# 2. Pre-commit hook runs
git commit -m "feat: add feature"
→ mvn compile
→ Exit 0: SUCCESS

# 3. License headers applied
# Feature.java now has AGPL-3.0-or-later header

# 4. Proof classification metadata generated
# Status: CLOSED, License: AGPL-3.0-or-later
```

### Scenario 2: Documentation Update

```bash
# 1. Modify documentation
echo "# New Section" >> README.md

# 2. Pre-commit hook runs
git commit -m "docs: add section"
→ No Java files, skip compilation
→ REUSE check passes

# 3. CC-BY-SA header already present

# 4. Proof classification metadata generated
# Status: OPEN, License: CC-BY-SA-4.0
```

### Scenario 3: Type Error

```bash
# 1. Add ill-typed code
echo "public class Broken { String x = 123; }" > Broken.java

# 2. Pre-commit hook runs
git commit -m "feat: add feature"
→ mvn compile
→ Exit 1: TYPE ERROR

# 3. Commit REJECTED
# Error: Proof normalization failed (ill-typed term)
```

## References

- Curry-Howard Correspondence: Howard (1969)
- Sequent Calculus: Gentzen (1934)
- Cut Elimination: Gentzen's Hauptsatz
- Light Linear Logic: Girard (1993)
- KeY Theorem Prover: https://www.key-project.org/
- REUSE Specification 3.0: https://reuse.software/spec/
- SPDX 3.1: https://spdx.dev/

## See Also

- `/home/engine/project/AGENTS.md` - Root proof structure
- `/home/engine/project/docs/helloworld/AGENTS.md` - Project constraints
- `/home/engine/project/docs/helloworld/pom.xml` - Maven configuration
