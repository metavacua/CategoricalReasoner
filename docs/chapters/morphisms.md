---
title: "Morphisms"
abbrev-title: "Morphisms"
msc-primary: "18D15"
msc-secondary: ["03G30", "03B22"]
keywords: [logical morphisms, categorical logic, translations, embeddings]
---

# Morphisms {#sec-morphisms}

## Logical Morphisms {#subsec-logical-morphisms}

Morphisms between logics in the Catty framework capture formal relationships 
of extension and interpretation.

### Extension Morphisms

An extension morphism $L_1 \to L_2$ indicates that $L_2$ extends $L_1$ with 
additional connectives or rules while preserving the base logic's theorems.

### Interpretation Morphisms

Interpretation morphisms translate formulas from one logic to another, 
preserving provability or refutability as appropriate.

## Categorical Structure {#subsec-categorical-structure}

The category of logics has:

- **Objects**: Logics (as sequent calculi)
- **Morphisms**: Structure-preserving translations
- **Composition**: Sequential translation
- **Identity**: Trivial translation

## Galois Connections {#subsec-galois}

The relationship between intuitionistic and co-intuitionistic logics forms 
a Galois connection, providing a categorical account of duality.
