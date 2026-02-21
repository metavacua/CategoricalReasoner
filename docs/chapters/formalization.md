---
title: "Formalization"
abbrev-title: "Formalization"
msc-primary: "03B35"
msc-secondary: ["03G30", "68T15"]
keywords: [formalization, ontology, RDF, OWL, knowledge graph]
---

# Formalization {#sec-formalization}

## Machine-Readable Ontologies {#subsec-ontologies}

Catty includes formal RDF/OWL ontologies representing:

- Logic definitions and their properties
- Morphisms between logics
- Proof structures and derivations
- Categorical constructions

## Knowledge Graph Structure {#subsec-knowledge-graph}

The knowledge graph captures:

| Entity | Properties | Relations |
|--------|------------|-----------|
| Logic | Name, axioms, rules | Extends, dual-of, interprets |
| Connective | Arity, introduction rules | Belongs-to |
| Theorem | Statement, proof | Provable-in |

## SPARQL Endpoint {#subsec-sparql}

The formalization enables SPARQL queries for:

- Discovering relationships between logics
- Verifying categorical properties
- Extracting proof patterns
- Validating structural constraints
