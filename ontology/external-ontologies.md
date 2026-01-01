# External Ontologies and Vocabularies

This document catalogs existing ontologies and vocabularies relevant to Catty's categorical model of formal logics. Following the principle of reuse over reinvention, we reference and extend established standards where possible.

## Category Theory Ontologies

### 1. Category Theory Ontology (CTO) - nLab
- **URI**: `https://ncatlab.org/nlab/`
- **License**: CC BY-SA 3.0
- **Coverage**: Comprehensive categorical structures, functors, natural transformations, adjunctions
- **Status**: Reference resource; concepts mapped via DBpedia/Wikidata
- **Usage**: Conceptual alignment for morphisms, functors, and categorical constructions

### 2. Mathematical Ontology (OpenMath)
- **URI**: `http://www.openmath.org/cd/`
- **License**: BSD 3-Clause
- **Coverage**: Mathematical structures, operations, and symbols
- **Status**: Compatible; used for mathematical notation
- **Usage**: Representing logical connectives, quantifiers, and mathematical operations

### 3. Wikidata Mathematics
- **URI**: `https://www.wikidata.org/wiki/Wikidata:WikiProject_Mathematics`
- **License**: CC0 (Public Domain)
- **Coverage**: Mathematical concepts with structured properties
- **Entities Used**:
  - `wd:Q217413` - Sequent calculus
  - `wd:Q5294` - Category theory
  - `wd:Q179899` - Intuitionistic logic
  - `wd:Q217699` - Classical logic
  - `wd:Q1149560` - Linear logic
  - `wd:Q5426514` - Structural rule
- **Usage**: Grounding definitions and linking to established mathematical concepts

### 4. DBpedia Mathematics
- **URI**: `http://dbpedia.org/resource/`
- **License**: CC BY-SA 3.0
- **Coverage**: Extracted from Wikipedia; comprehensive coverage of logic and category theory
- **Entities Used**:
  - `dbr:Sequent_calculus`
  - `dbr:Category_theory`
  - `dbr:Intuitionistic_logic`
  - `dbr:Classical_logic`
  - `dbr:Linear_logic`
  - `dbr:Curry-Howard_correspondence`
- **Usage**: Primary reference for grounding logical formalism concepts

## Logic and Formal Language Ontologies

### 5. OWL 2 and Description Logics
- **URI**: `http://www.w3.org/2002/07/owl#`
- **License**: W3C Document License
- **Coverage**: Description logic fragments, reasoning profiles
- **Status**: Standard; used for ontology construction
- **Usage**: Structural framework for Catty ontologies (classes, properties, restrictions)

### 6. Rule Interchange Format (RIF)
- **URI**: `http://www.w3.org/2005/rules/`
- **License**: W3C Recommendation
- **Coverage**: Rule-based reasoning, logical frameworks
- **Status**: Reference; potential future integration
- **Usage**: Modeling logical rules and inference patterns

### 7. Semantic Web Rule Language (SWRL)
- **URI**: `http://www.w3.org/2003/11/swrl#`
- **License**: W3C Submission
- **Coverage**: Horn clause rules on top of OWL
- **Status**: Reference; potential for representing logical axioms
- **Usage**: Future: encoding sequent calculus rules as SWRL rules

### 8. SUMO (Suggested Upper Merged Ontology)
- **URI**: `http://www.ontologyportal.org/`
- **License**: GNU GPL (ontology content)
- **Coverage**: Upper ontology with mathematical and logical concepts
- **Status**: Reference; not directly integrated (licensing considerations)
- **Usage**: Conceptual validation for upper-level categories

## Proof Theory and Type Theory

### 9. Isabelle/AFP Ontologies
- **URI**: `https://www.isa-afp.org/`
- **License**: Varies (mostly BSD)
- **Coverage**: Formalized mathematics, proof theory, category theory
- **Status**: Target for future mechanization
- **Usage**: Ground truth for formalized proofs (future phase)

### 10. Coq Standard Library
- **URI**: `https://coq.inria.fr/library/`
- **License**: CeCILL-B (BSD-compatible)
- **Coverage**: Constructive logic, type theory, category theory
- **Status**: Reference for constructive interpretations
- **Usage**: Validation of intuitionistic logic constructions

### 11. Lean MathLib
- **URI**: `https://github.com/leanprover-community/mathlib`
- **License**: Apache 2.0
- **Coverage**: Formalized mathematics including category theory
- **Status**: Reference for categorical constructions
- **Usage**: Validation of morphism properties and categorical axioms

## Domain-Specific Resources

### 12. Proof and Program Ontology
- **URI**: Custom (to be published at `https://<owner>.github.io/Catty/ontology/`)
- **License**: AGPL-3.0
- **Coverage**: Curry-Howard correspondence, witness formalism
- **Status**: Original contribution extending external resources
- **Usage**: Core Catty ontology

### 13. Logical Formalism Ontology
- **URI**: Custom (to be published at `https://<owner>.github.io/Catty/ontology/`)
- **License**: AGPL-3.0
- **Coverage**: Two-dimensional lattice category, structural rules, sequent restrictions
- **Status**: Original contribution
- **Usage**: Core Catty ontology

## Integration Strategy

### Namespace Prefixes

```turtle
@prefix catty: <https://<owner>.github.io/Catty/ontology#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
@prefix dcterms: <http://purl.org/dc/terms/> .
@prefix foaf: <http://xmlns.com/foaf/0.1/> .
@prefix dbr: <http://dbpedia.org/resource/> .
@prefix dbo: <http://dbpedia.org/ontology/> .
@prefix wd: <http://www.wikidata.org/entity/> .
@prefix wdt: <http://www.wikidata.org/prop/direct/> .
@prefix openmath: <http://www.openmath.org/cd/> .
```

### Mapping Strategy

1. **Grounding via DBpedia/Wikidata**: Use `owl:sameAs`, `skos:exactMatch`, `skos:closeMatch` to link Catty concepts to external URIs
2. **Provenance**: Use `dcterms:source` to cite external ontologies
3. **Licensing**: Document all external resource licenses in `LICENSE` file
4. **Versioning**: Pin specific versions of external resources where applicable

### Example Mapping

```turtle
catty:ClassicalLogic a owl:Class ;
    rdfs:label "Classical Logic"@en ;
    rdfs:comment "Logic LK with unrestricted sequents and full structural rules"@en ;
    skos:exactMatch dbr:Classical_logic, wd:Q217699 ;
    dcterms:source <https://ncatlab.org/nlab/show/classical+logic> .
```

## Validation Tools

### 1. RDFLib (Python)
- **Purpose**: RDF parsing, serialization, validation
- **License**: BSD
- **Usage**: Primary validation tool for Catty ontologies

### 2. Apache Jena (Java)
- **Purpose**: RDF graph manipulation, SPARQL queries, reasoning
- **License**: Apache 2.0
- **Usage**: Future production synthesis engine

### 3. SHACL Validation
- **Purpose**: Constraint validation for RDF graphs
- **Tool**: pySHACL (Python) or Jena SHACL
- **Usage**: Validating categorical axioms and lattice structure

## References

- Linked Open Vocabularies (LOV): https://lov.linkeddata.es/
- W3C Semantic Web Standards: https://www.w3.org/standards/semanticweb/
- DBpedia: https://www.dbpedia.org/
- Wikidata: https://www.wikidata.org/
- OpenMath: http://www.openmath.org/
- nLab: https://ncatlab.org/nlab/
