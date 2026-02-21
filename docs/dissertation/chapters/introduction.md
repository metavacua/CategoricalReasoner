---
title: "Introduction"
abbrev-title: "Introduction"
msc-primary: "03B22"
msc-secondary: ["03A05", "03G30"]
keywords: [categorical logic, logical pluralism, introduction]
---

# Introduction {#sec-introduction}

This work presents Catty, a formal investigation of categorical foundations 
for logics and their morphisms. Rather than following a conventional linear 
progression, this thesis serves as a review of the literature and a foundational 
common reference for understanding logics as coordinate objects in a categorical 
structure.

## Motivation and Philosophical Stance {#subsec-motivation}

The proliferation of formal logics demands a framework that captures their 
structural relationships without imposing a rigid hierarchy. Catty adopts a 
position of **logical pluralism**, where different logics are seen as distinct 
computational universes, each with its own valid internal reasoning.

## Initial Logics and Logical Plurality {#subsec-initial-logics}

A core tenet of this work is the use of "initial logics" to describe the 
categorized bases from which others extend. By identifying these initial objects, 
we can span the space of categorizable logics through formal morphisms that 
preserve or restrict logical properties.

## Proofs as Witnesses {#subsec-proofs-witnesses}

Catty goes beyond descriptive documentation by providing **computational witnesses**. 
This is planned to be implemented via translation to valid Java bytecodes with the compiler as the validator.

In this framework, syntactic minimality is shown to enable greater semantic power; syntactically minimal logics admit more models, and this reflected in the variations of extensions with the initial logic having the greatest number and the terminal logic having the least.

## Structure of the Reference {#subsec-structure}

This reference is organized into themes that explore the genesis, architecture, 
and formalization of the logic category:

| Theme | Description |
|-------|-------------|
| **Genesis** | Foundational influences ranging from Linear Logic to Paraconsistent Reasoning |
| **Architecture** | The geometry of the lattice and its parameter space of logics; it is related to Post's lattices by the functional incompleteness theorem of proper subclassical logics|
| **Morphisms** | Formal relationships of extension and interpretation |
| **Universes** | Logics as substrates for computation, including links to Rust and systems programming |
| **Witness** | The implementation of Catty in standard programming languages and executable categorical reasoning; development towards a library, API, or SDK consisting of the constructive witnesses of the category of subclassical logics and their theories |
| **Philosophy** | The overarching implications of logical pluralism and the inversion principle |
