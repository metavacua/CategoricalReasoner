---
title: "Appendices"
abbrev-title: "Appendices"
---

# Appendices {#sec-appendices}

## Citation Registry {#sec-citation-registry}

The master list of approved citations for the Catty framework is maintained 
in the bibliography directory.

## DSL Reference {#sec-dsl-reference}

Full specification for the Catty Domain-Specific Language.

### Syntax

```catty
logic L {
  axioms: [...]
  rules: [...]
}
```

### Commands

| Command | Description |
|---------|-------------|
| `define` | Define a new logic |
| `extend` | Extend an existing logic |
| `dual` | Create dual logic |
| `prove` | Construct a proof |
| `verify` | Check a derivation |

## SPARQL Query Reference {#sec-sparql-reference}

Reference for exploring the categorical knowledge graph.

### Example Queries

```sparql
# List all logics
SELECT ?logic ?name WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?name .
}
```

## SHACL Shape Reference {#sec-shacl-reference}

Validation constraints for the formal ontology.

### Logic Shape

```turtle
catty:LogicShape a sh:NodeShape ;
  sh:targetClass catty:Logic ;
  sh:property [
    sh:path catty:hasAxiom ;
    sh:minCount 1 ;
  ] .
```
