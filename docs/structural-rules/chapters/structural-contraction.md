---
title: "Structural Rules: Contraction"
abbrev-title: "Contraction"
msc-primary: "03F07"
msc-secondary: ["03B47", "03G30"]
keywords: [contraction, structural rules, substructural logic, resource logic]
---

# Structural Rules: Contraction {#sec-contraction}

## Introduction to Contraction {#subsec-contraction-intro}

Contraction is a structural rule that permits the identification of duplicate 
formulas within a sequent context. In its classical form, contraction allows 
the inference from $\Gamma, A, A \vdash C$ to $\Gamma, A \vdash C$, thereby 
enabling the reuse of premises.

This part examines contraction as the dual of weakening: while weakening creates 
resources, contraction consumes them through identification. The interplay 
between weakening and contraction characterizes classical logic, while their 
restriction leads to substructural logics.

## Additive Contraction {#subsec-contraction-additive}

Additive contraction operates in contexts where duplicate formulas are 
implicitly merged. In additive sequent calculi, contraction reflects the 
implicit assumption that premises can be used multiple times without explicit 
accounting.

## Multiplicative Contraction {#subsec-contraction-multiplicative}

Multiplicative contraction, in contrast, operates in contexts where duplicates 
must be explicitly managed. In linear logic, contraction is available only for 
formulas marked with the exponential modality $\oc A$, indicating that the 
formula may be "copied" as needed.

## Contraction-Free Logics {#subsec-contraction-free}

Logics that reject contraction include:

- **Linear logic**: Resources cannot be duplicated freely
- **Affine logic**: Weakening admitted, contraction rejected
- **Relevant logic**: Both weakening and contraction rejected

## Categorical Semantics {#subsec-contraction-categorical}

Categorically, contraction corresponds to the existence of *diagonal maps*. In 
a Cartesian closed category, the natural transformation $\delta_A : A \to A \times A$ 
witnesses contraction, providing the computational interpretation of "copying" 
or "duplication."
