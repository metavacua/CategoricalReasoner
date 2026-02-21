---
title: "Curry-Howard (Extracted)"
abbrev-title: "Curry-Howard"
msc-primary: "03B40"
msc-secondary: ["03B70", "68N18"]
keywords: [Curry-Howard, extracted content, proofs as programs]
---

# Curry-Howard (Extracted) {#sec-curry-howard}

## The Correspondence {#subsec-correspondence}

The Curry-Howard correspondence establishes a structural identity between:

| Logic | Computation |
|-------|-------------|
| Propositions | Types |
| Proofs | Programs |
| Normalization | Evaluation |

## Extracted Formal Content {#subsec-extracted-ch}

This chapter contains formally extracted instances of the correspondence:

### Intuitionistic Logic

```
Theorem (A → B) → (B → C) → (A → C)
Proof: λf. λg. λx. g (f x)
```

### Linear Logic

```
Theorem A ⊸ B ⊸ A ⊗ B
Proof: λx. λy. pair x y
```

## Dual Correspondence {#subsec-dual}

The dual Curry-Howard correspondence for co-intuitionistic logic relates:

- Co-proofs to co-programs
- Refutation to control flow
- Anti-normalization to co-evaluation
