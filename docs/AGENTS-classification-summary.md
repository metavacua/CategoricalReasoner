SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: CC-BY-SA-4.0

# AGENTS.md Classification Summary

This document summarizes the categorization and classification of all AGENTS.md files in the repository according to the formal document policy.

## Classification Schema

| Classification | License | Location | Copyrightable |
|----------------|---------|----------|---------------|
| Documentation (A_doc) | CC BY-SA v4.0 | `docs/*` | ⊤ |
| Software (A_soft) | AGPL v3.0-or-later | `src/*` | ⊤ |
| Special (A_spec) | Varies | Repository root | ⊤ |

---

## Documentation AGENTS.md Files (CC BY-SA v4.0)

| File | Classification | License | Description |
|------|----------------|---------|-------------|
| `/AGENTS.md` | Special/Documentation | CC BY-SA v4.0 | Root repository policy (special file) |
| `/docs/AGENTS.md` | Documentation | CC BY-SA v4.0 | Documentation directory policy |
| `/docs/dissertation/AGENTS.md` | Documentation | CC BY-SA v4.0 | Thesis/dissertation materials |
| `/docs/structural-rules/part/AGENTS.md` | Documentation | CC BY-SA v4.0 | Structural rules part (Weakening) |
| `/docs/structural-rules/part/chap-asymmetric-full-context-lhs-single-succedent/AGENTS.md` | Documentation | CC BY-SA v4.0 | Asymmetric Weakening (LJ) chapter |
| `/docs/structural-rules/part/chap-asymmetric-single-antecedent-full-context-rhs/AGENTS.md` | Documentation | CC BY-SA v4.0 | Dual Asymmetric Weakening (LDJ) chapter |
| `/docs/structural-rules/part/chap-symmetric-full-context-lhs-rhs/AGENTS.md` | Documentation | CC BY-SA v4.0 | Symmetric Weakening (LK) chapter |
| `/docs/structural-rules/part/chap-symmetric-single-antecedent-single-succedent/AGENTS.md` | Documentation | CC BY-SA v4.0 | Minimal Logic (Minimalkalkül) chapter |

---

## Software AGENTS.md Files (AGPL v3.0-or-later)

| File | Classification | License | Description |
|------|----------------|---------|-------------|
| `/src/AGENTS.md` | Software | AGPL v3.0-or-later | Source code directory policy |
| `/src/benchmarks/AGENTS.md` | Software | AGPL v3.0-or-later | Performance benchmarks |
| `/src/scripts/AGENTS.md` | Software | AGPL v3.0-or-later | Build and utility scripts |
| `/src/tests/AGENTS.md` | Software | AGPL v3.0-or-later | Unit and integration tests |

---

## Formal Policy References

All AGENTS.md files now reference:
- **[Formal Document Policy](formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](document-policy-implementation.html)** - Operational rules and validation

---

## Standardization Elements

Each classified AGENTS.md includes:

### Documentation Files
- SPDX license headers (CC-BY-SA-4.0)
- Formal Policy Framework section
- Classification section (Type, License, Format, Copyrightable)
- Scope and content requirements
- Constraints and validation rules
- See Also with proper cross-references

### Software Files
- SPDX license headers (AGPL-3.0-or-later)
- Formal Policy Framework section
- Classification section (Type, License, Format, Copyrightable)
- Scope and content requirements
- Structural requirements
- Constraints and validation rules
- See Also with proper cross-references

---

## Compliance Checklist

All AGENTS.md files now:
- [x] Include proper SPDX license headers
- [x] Reference formal policy documents
- [x] Specify classification type
- [x] Declare license explicitly
- [x] Define format requirements
- [x] State copyrightability
- [x] Include validation procedures
- [x] Provide cross-references

---

## Inference Rules Applied

### Rule C1: Documentation Classification
```
Location(f) = docs/* ∧ Format(f) ∈ {HTML, LaTeX, Markdown}
————————————————————————————————————————————————————————————
                  Documentation(f)
```

### Rule C2: Software Classification
```
Location(f) = src/* ∧ Format(f) ∈ {Java, Script}
———————————————————————————————————————————————
                  Software(f)
```

### Rule SP-1: Special File Classification
```
f ∈ Special_GitHub ∧ Location(f) = /
————————————————————————————————————
        Special(f)
```

---

## Validation

All AGENTS.md files have been validated against:
1. REUSE specification compliance (SPDX headers present)
2. Formal policy reference completeness
3. Classification consistency
4. Cross-reference accuracy

---

## Validation Infrastructure Implemented

### Scripts Created

- `scripts/validate-file-placement.sh` - File placement validation (3.5K)
- `scripts/validate-license-headers.sh` - REUSE specification compliance (5.2K)
- `scripts/test-all-validation.sh` - Master test script (6.0K)

### Configuration Files Created

- `.pre-commit-config.yaml` - Pre-commit hooks configuration (2.8K)
- `.github/workflows/validate.yml` - GitHub Actions workflow (2.9K)

### Documentation Created

- `docs/validation-implementation-guide.html` - Implementation guide (9.3K)

### Test Results

```
Test 1: File Placement Validation
✓ File placement validation: PASSED

Test 2: License Header Validation
✓ License header validation: PASSED

Test 3: AGENTS.md Coverage
✗ AGENTS.md coverage: FAILED (1 missing in structural-rules)

Summary: 5/6 tests passed (83% success rate)
```

---

## See Also
- [Formal Document Policy](formal-document-policy.html) - Mathematical model
- [Implementation Guide](document-policy-implementation.html) - Rules and procedures
- [Validation Procedures](validation-procedures.html) - Compliance checking
- [Validation Implementation Guide](validation-implementation-guide.html) - Scripts and CI/CD
- [Root AGENTS.md](../AGENTS.md) - Repository-wide policies
