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

Catty's DSL implementation connects to systems programming paradigms, 
including Rust's ownership system which shares affinities with linear logic.
