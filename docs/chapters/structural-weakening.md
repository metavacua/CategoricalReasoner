---
title: "Structural Rules: Weakening"
abbrev-title: "Weakening"
msc-primary: "03F07"
msc-secondary: ["03B47", "03G30"]
keywords: [weakening, structural rules, substructural logic, resource logic]
---

# Structural Rules: Weakening {#sec-weakening}

## Introduction to Weakening {#subsec-weakening-intro}

Weakening, also known as *thinning* or *expansion*, is a structural rule that 
permits the introduction of arbitrary formulas into a sequent context without 
affecting derivability. In its classical form, weakening allows the inference 
from $\Gamma \vdash C$ to $\Gamma, A \vdash C$, thereby adding irrelevant 
premises.

This part examines weakening from multiple perspectives: proof-theoretic, 
model-theoretic, and categorical. We trace the historical development from 
Gentzen's original sequent calculi through to Girard's linear logic, where 
weakening is controlled through explicit resource management.

## Additive Weakening {#subsec-weakening-additive}

Additive weakening operates in contexts where formulas are implicitly shared 
across premises. In additive sequent calculi, weakening allows the introduction 
of formulas into contexts without cost, reflecting an implicit assumption of 
unlimited resources.

## Multiplicative Weakening {#subsec-weakening-multiplicative}

Multiplicative weakening, in contrast, operates in contexts where resources are 
explicitly tracked. In linear logic, weakening is available only for formulas 
marked with the exponential modality $\oc A$, indicating "unlimited" availability.

## Weakening-Free Logics {#subsec-weakening-free}

Logics that reject weakening include:

- **Relevant logic**: Premises must be relevant to conclusions
- **Linear logic**: Resources cannot be freely created
- **Affine logic**: Weakening is admitted but contraction is rejected

## Categorical Semantics {#subsec-weakening-categorical}

Categorically, weakening corresponds to the existence of *terminal objects* and 
*projection maps*. In a Cartesian closed category, the natural transformation 
$\pi_A : A \times B \to A$ witnesses weakening in the computational interpretation.
