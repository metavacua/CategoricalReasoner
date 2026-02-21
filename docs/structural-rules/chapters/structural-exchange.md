---
title: "Structural Rules: Exchange"
abbrev-title: "Exchange"
msc-primary: "03F07"
msc-secondary: ["03B47", "03G30"]
keywords: [exchange, structural rules, substructural logic, non-commutative]
---

# Structural Rules: Exchange {#sec-exchange}

## Introduction to Exchange {#subsec-exchange-intro}

Exchange is a structural rule that permits the reordering of formulas within 
a sequent context. In its classical form, exchange allows the inference from 
$\Gamma, A, B, \Delta \vdash C$ to $\Gamma, B, A, \Delta \vdash C$, thereby 
treating contexts as multisets rather than sequences.

This part examines exchange as the most fundamental structural rule: unlike 
weakening and contraction, exchange does not change the number of formula 
occurrences, only their arrangement. The rejection of exchange leads to 
non-commutative logics with rich algebraic structure.

## Permutation of Antecedents {#subsec-exchange-antecedent}

Antecedent exchange governs the reordering of premises in the left-hand side 
of a sequent. In classical and intuitionistic logics, antecedents are treated 
as unordered collections, making exchange implicit.

## Permutation of Succedents {#subsec-exchange-succedent}

Succedent exchange governs the reordering of conclusions in the right-hand 
side of a sequent. In classical logic with multiple conclusions, exchange 
applies symmetrically to both sides.

## Exchange-Free Logics {#subsec-exchange-free}

Logics that reject exchange include:

- **Lambek calculus**: Ordered type logic for natural language
- **Bunched implications**: Separation logic and resource models
- **Non-commutative linear logic**: Ordered resource management

## Categorical Semantics {#subsec-exchange-categorical}

Categorically, exchange corresponds to the existence of *symmetry isomorphisms* 
or *braidings*. In a symmetric monoidal category, the natural isomorphism 
$\sigma_{A,B} : A \otimes B \to B \otimes A$ witnesses exchange, while braided 
monoidal categories permit controlled forms of exchange.
