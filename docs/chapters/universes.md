---
title: "Universes"
abbrev-title: "Universes"
msc-primary: "03B70"
msc-secondary: ["68N18", "03B40", "03G30"]
keywords: [computational universes, logic programming, Rust, type systems]
---

# Universes {#sec-universes}

## Logics as Computational Substrates {#subsec-computational}

Each logic in the Catty framework corresponds to a computational universe—a 
substrate for programming and reasoning.

## Type Systems and Logic {#subsec-type-systems}

The Curry-Howard correspondence provides the bridge between logic and computation:

| Logic | Type System | Computational Interpretation |
|-------|-------------|------------------------------|
| LJ | Simply-typed lambda calculus | Functional programming |
| LL | Linear types | Resource-sensitive computation |
| LK | Classical logic | Control operators |

## Implementation in Systems Programming {#subsec-systems}

This has practical implications for the interpretation, design, and development of programming languages. Rust for example has relationships with certain substructural logics with at least partial resource-sensitivity. There exists logics with consistency-as-resources or completeness-as-resources semantics related to subclassical logics of formal inconsistency or subclassical logics of formal undeterminedness; LJ extended by a completeness operation or LDJ extended by a consistent operation are examples.
