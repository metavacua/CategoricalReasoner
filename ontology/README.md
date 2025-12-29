# Catty Ontology: Categorical Semantic Model

This directory contains the RDF/OWL schemas and knowledge graph data for Catty's category-theoretic model of formal logics.

## Overview

Catty models logics as objects in a category with morphisms representing sequent restrictions and structural rules. The ontology provides:

1. **Categorical Schema**: Core classes for category theory (Category, Object, Morphism, Functor, Natural Transformation, etc.)
2. **Logics-as-Objects**: Formal logics (LK, LJ, LL, etc.) represented as categorical objects with properties
3. **Morphism Catalog**: Categorical relationships between logics (sequent restrictions, structural rule embeddings)
4. **Two-Dimensional Lattice**: The poset category of logics organized by sequent form and structural rules
5. **Curry-Howard Model**: The categorical equivalence between logic and type theory

## File Structure

### Core Schema

- **`catty-categorical-schema.jsonld`**: The complete RDF/OWL schema defining:
  - Category theory primitives (Category, Object, Morphism, Functor, Natural Transformation)
  - Logic-specific classes (Logic, LogicMorphism, SequentForm, StructuralRule)
  - Lattice structure (LogicLattice, LatticeAxis, LatticeCoordinate)
  - Curry-Howard equivalence classes

### Knowledge Graph Data

- **`logics-as-objects.jsonld`**: Instance data for logics as categorical objects:
  - LK (classical sequent calculus)
  - LJ (intuitionistic sequent calculus)
  - LDJ (dual intuitionistic logic)
  - LJW, LJC (intuitionistic without weakening/contraction)
  - LL, ALL, RLL (linear logic variants)
  - MLL, MALL (multiplicative fragments)

- **`morphism-catalog.jsonld`**: Morphisms between logics:
  - Sequent restriction morphisms (LK → LJ, LJ → LDJ)
  - Structural rule morphisms (LJ → LJW, LJ → LJC, LJ → LL)
  - Proof system embeddings
  - Adjoint functor pairs
  - Natural transformations (Gödel-Gentzen, CPS)

- **`two-d-lattice-category.jsonld`**: The two-dimensional lattice as a category:
  - Lattice axes (sequent restrictions, structural rules)
  - Lattice nodes with coordinates
  - Lattice order relations (≤)
  - Meet and join operations
  - Sublattices (intuitionistic, substructural)

- **`curry-howard-categorical-model.jsonld`**: The Curry-Howard correspondence:
  - Equivalence of categories (Logic ↔ Type Theory)
  - Curry-Howard functor and its inverse
  - Natural transformations
  - Type theory instances (STLC, System F, Linear Types)
  - Categorical semantics (CCCs, *-autonomous categories)

### Complete Example

- **`catty-complete-example.jsonld`**: A comprehensive, self-contained example demonstrating:
  - Core logics (LK, LJ, LL, ALL, RLL)
  - Structural rules (Weakening, Contraction, Exchange)
  - Morphisms and lattice order
  - Adjoint relationships
  - Curry-Howard correspondence

### Documentation

- **`ontological-inventory.md`**: Comprehensive inventory of external resources:
  - DBPedia category theory and logic schemas
  - Wikidata mathematical ontology
  - OpenMath content dictionaries
  - COLORE, nLab, HoTT, Coq, Lean, Isabelle
  - License compatibility assessment
  - Integration roadmap

## Usage

### Loading the Ontology

```bash
# Using Jena ARQ
arq --data catty-categorical-schema.jsonld --query example.rq

# Using RDF4J
curl -X POST -H "Content-Type: application/json-ld" \
  --data @catty-categorical-schema.jsonld \
  http://localhost:8080/rdf4j-server/repositories/catty/statements

# Using rdflib (Python)
from rdflib import Graph
g = Graph()
g.parse("catty-categorical-schema.jsonld", format="json-ld")
```

### Example SPARQL Queries

#### List all logics with their lattice coordinates

```sparql
PREFIX catty: <http://catty.org/ontology/>

SELECT ?logic ?label ?coordinate
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?label ;
         catty:latticeCoordinate ?coordinate .
}
ORDER BY ?coordinate
```

#### Find morphisms from LJ to other logics

```sparql
PREFIX catty: <http://catty.org/ontology/>

SELECT ?morphism ?target ?targetLabel
WHERE {
  ?morphism catty:domain catty:LJ ;
            catty:codomain ?target .
  ?target rdfs:label ?targetLabel .
}
```

#### Get all structural rules for linear logic

```sparql
PREFIX catty: <http://catty.org/ontology/>

SELECT ?rule ?ruleLabel
WHERE {
  catty:LL catty:hasStructuralRule ?rule .
  ?rule rdfs:label ?ruleLabel .
}
```

#### Find adjoint relationships

```sparql
PREFIX catty: <http://catty.org/ontology/>

SELECT ?adjoint ?label ?source ?target
WHERE {
  ?adjoint a catty:AdjointFunctors ;
           rdfs:label ?label ;
           catty:sourceCategory ?source ;
           catty:targetCategory ?target .
}
```

#### Get Curry-Howard type theory corresponding to LJ

```sparql
PREFIX catty: <http://catty.org/ontology/>

SELECT ?typeTheory ?label
WHERE {
  catty:LJ catty:correspondsToLogic ?logic .
  ?typeTheory catty:correspondsToLogic ?logic ;
              rdfs:label ?label .
}
```

## JSON-LD Context

The ontology uses the following prefixes:

```json
{
  "catty": "http://catty.org/ontology/",
  "owl": "http://www.w3.org/2002/07/owl#",
  "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
  "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
  "xsd": "http://www.w3.org/2001/XMLSchema#",
  "dct": "http://purl.org/dc/terms/",
  "prov": "http://www.w3.org/ns/prov#",
  "wd": "http://www.wikidata.org/entity/",
  "dbo": "http://dbpedia.org/ontology/"
}
```

## Categorical Model

### Objects (Logics)

Each logic is an object in the LogicCategory:

```
LK (Classical)         → LJ (Intuitionistic) → LDJ (Dual Intuitionistic)
Coordinate: (0,0)         Coordinate: (1,0)        Coordinate: (2,0)
```

### Vertical Dimension (Structural Rules)

```
LJ (Full) y=0
  ↓ (remove weakening)
LJW (No W) y=1
  ↓ (remove contraction)
LL (No W, No C) y=3 (linear logic)
  ↓ (add weakening)
ALL (Affine) y=4
  ↓ (add contraction)
RLL (Relevant) y=5
```

### Morphisms

A morphism exists from Logic A to Logic B iff A is more restrictive than B:

```
LL → ALL (add weakening)
LL → RLL (add contraction)
LL → LJ (add weakening and contraction)
ALL → LJ (add contraction)
RLL → LJ (add weakening)
```

### Adjoint Relationships

```
LJ ↔ LK (double negation)
- Inclusion: LJ → LK (intuitionistic sublogic of classical)
- Left Adjoint: LK → LJ (Gödel-Gentzen translation)
```

### Curry-Howard Correspondence

```
Logic Category                Type Theory Category
────────────────────────────────────────────────────────
LJ (Intuitionistic)    ↔      STLC (Simply Typed λ-calculus)
LL (Linear Logic)       ↔      Linear Types
ALL (Affine LL)         ↔      Affine Types
LK (Classical)          ↔      System F (with control)
```

## External Resources

The ontology is designed to integrate with external semantic resources:

- **Wikidata**: Direct import via `owl:sameAs` links
- **DBPedia**: Cross-reference for category theory concepts
- **OpenMath**: Transform category1.cd to RDF/OWL
- **nLab**: Reference for categorical insights
- **Proof Assistants**: Extract from Coq/Lean/Isabelle libraries

See `ontological-inventory.md` for detailed compatibility and integration guidelines.

## License

This ontology is part of the Catty thesis project (MIT License). External resources referenced have varying licenses:

- **Wikidata**: CC0 (public domain) - Fully compatible
- **DBPedia**: CC BY-SA 3.0 - Compatible with attribution
- **OpenMath**: BSD 3-Clause - Fully compatible
- **nLab**: CC BY-SA 3.0 - Compatible with attribution
- **Lean MathLib**: Apache 2.0 - Fully compatible
- **Coq**: CeCILL-B (BSD-compatible) - Fully compatible

## Contributing

When adding new logics or morphisms to the ontology:

1. Follow the existing schema structure
2. Include lattice coordinates for logics
3. Document the morphism direction (restriction → extension)
4. Add `owl:sameAs` links to external resources where applicable
5. Include `dct:description` for human readability
6. Include `prov:wasDerivedFrom` for sources

## References

- Curry, H. B., & Howard, W. A. (1980). "Formulae-as-Types Notion of Control"
- Gentzen, G. (1935). "Untersuchungen über das logische Schließen"
- Girard, J.-Y. (1987). "Linear Logic"
- Lambek, J., & Scott, P. J. (1986). "Introduction to Higher Order Categorical Logic"
- nLab: https://ncatlab.org/nlab/
