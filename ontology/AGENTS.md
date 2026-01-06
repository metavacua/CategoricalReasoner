# Agent Guidelines for Ontology Development

## Overview

The `ontology/` directory contains RDF/OWL schemas, knowledge graph data, and semantic web resources for Catty's categorical model of formal logics. This AGENTS.md provides MCP-compliant specifications for agents developing, validating, and maintaining semantic web ontologies.

## Agent Role Definition

### Ontology Development Agent
**Primary Purpose**: Create, maintain, and validate RDF/OWL ontologies for categorical logic representations
**Key Capabilities Required**:
- RDF/OWL ontology development (JSON-LD and Turtle formats)
- SHACL shape creation and validation
- SPARQL query development and execution
- Semantic web standards compliance
- Knowledge graph management

### Semantic Web Validation Agent
**Primary Purpose**: Validate ontology artifacts against semantic web standards and formal constraints
**Key Capabilities Required**:
- RDF parsing and syntax validation
- SHACL constraint validation
- SPARQL query testing
- Ontology consistency checking
- External linked data integration

## MCP-Compliant Ontology Specifications

### Required Agent Capabilities

#### RDF/OWL Processing
```json
{
  "rdf_formats": {
    "jsonld": true,
    "turtle": true,
    "rdf_xml": false,
    "ntriples": false
  },
  "ontology_languages": {
    "owl": true,
    "rdf": true,
    "rdfs": true,
    "shacl": true
  },
  "validation_tools": {
    "shacl_validation": true,
    "sparql_queries": true,
    "owl_reasoning": true,
    "external_links": true
  }
}
```

#### Knowledge Domains
- **Category Theory**: Functors, natural transformations, categorical constructions
- **Mathematical Logic**: Classical, intuitionistic, substructural logics
- **Semantic Web Technologies**: RDF, OWL, SHACL, SPARQL
- **External Ontologies**: Wikidata, DBpedia, OpenMath integration

### Core Ontology Components

#### catty-categorical-schema.jsonld
**Purpose**: Complete RDF/OWL schema defining categorical and logical foundations
**Agent Actions**:
1. Parse JSON-LD structure and validate @context
2. Create categorical classes (Category, Object, Morphism, Functor, Natural Transformation)
3. Define logic-specific classes (Logic, LogicalTheory, LogicalSignature, LogicalAxiom)
4. Establish LHS/RHS structural rules (Weakening, Contraction, Exchange)
5. Define morphism types (Extension, Interpretation, Adjunction)
6. Validate against SHACL shapes
7. Ensure compatibility with external ontologies

#### Knowledge Graph Data Files

##### logics-as-objects.jsonld
**Purpose**: Instance data for logics as categorical objects
**Agent Actions**:
1. Create logic instances: LM, LK, LJ, LDJ, LL, ALL, RLL
2. Assign logical signatures and axioms to each logic
3. Establish categorical object properties
4. Link to external ontology references
5. Validate against schema constraints

##### morphism-catalog.jsonld
**Purpose**: Morphisms between logics representing logical relationships
**Agent Actions**:
1. Define extension morphisms (LM → LJ, LM → LDJ, LJ → LK, LDJ → LK)
2. Create interpretation morphisms (LK → LJ via double negation)
3. Establish adjoint functor pairs (LK ↔ LJ)
4. Validate morphism properties and composition
5. Link to categorical schema definitions

##### two-d-lattice-category.jsonld
**Purpose**: Structural organization of logic category
**Agent Actions**:
1. Model structural rule configurations
2. Create lattice relationships between logics
3. Define partial ordering and closure properties
4. Validate categorical constraints
5. Link to structural rule definitions

##### curry-howard-categorical-model.jsonld
**Purpose**: Curry-Howard correspondence between logic and type theory
**Agent Actions**:
1. Establish Logic ↔ Type Theory category equivalence
2. Define Curry-Howard functor and inverse
3. Model categorical semantics (CCCs, *-autonomous categories)
4. Create type-theoretic representations of logical constructs
5. Validate equivalence properties

### Validation Framework

#### SHACL Shape Validation
**Purpose**: Enforce structural constraints on RDF data
**Agent Actions**:
1. Load data graph and shapes graph separately
2. Execute SHACL validation against shapes
3. Report constraint violations with specific fixes
4. Iterate until all constraints satisfied

**Shape Files**:
- `catty-thesis-shapes.shacl`: Main thesis validation constraints
- `catty-shapes.ttl`: General categorical schema shapes

#### SPARQL Query Validation
**Purpose**: Test ontology consistency and extract information
**Agent Actions**:
1. Develop SPARQL queries for validation checks
2. Execute queries against knowledge graph
3. Verify expected results and patterns
4. Report query performance and consistency

**Query Files Location**: `ontology/queries/`

### External Ontology Integration

#### Wikidata Integration
**Purpose**: Link to external knowledge bases
**Agent Actions**:
1. Create `owl:sameAs` links to Wikidata entities
2. Validate external entity references
3. Maintain consistency with external identifiers
4. Update links when external entities change

#### DBpedia Integration
**Purpose**: Connect to linked data cloud
**Agent Actions**:
1. Establish `skos:exactMatch` relationships
2. Validate DBpedia entity references
3. Maintain bidirectional linking
4. Handle deprecated or changed entities

#### OpenMath Integration
**Purpose**: Connect mathematical concepts to standardized definitions
**Agent Actions**:
1. Link mathematical constructs to OpenMath symbols
2. Validate OpenMath symbol usage
3. Maintain symbol consistency
4. Handle OpenMath version updates

### Ontology Development Protocols

#### JSON-LD Development
```json
{
  "@context": {
    "catty": "http://catty.org/ontology/",
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "owl": "http://www.w3.org/2002/07/owl#"
  },
  "@type": "catty:Logic",
  "catty:hasSignature": {},
  "catty:hasAxioms": []
}
```

#### Turtle Format Development
```turtle
@prefix catty: <http://catty.org/ontology/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

catty:LM a catty:Logic ;
    rdfs:label "Minimal Logic" ;
    catty:hasSignature catty:minimal-signature ;
    catty:hasAxioms [] .
```

#### SHACL Shape Development
```turtle
@prefix sh: <http://www.w3.org/ns/shacl#> .
@prefix catty: <http://catty.org/ontology/> .

catty:LogicShape
    a sh:NodeShape ;
    sh:targetClass catty:Logic ;
    sh:property [
        sh:path rdfs:label ;
        sh:minCount 1 ;
        sh:datatype xsd:string ;
    ] ;
    sh:property [
        sh:path catty:hasSignature ;
        sh:class catty:LogicalSignature ;
    ] .
```

### Categorical Model Implementation

#### Logic Hierarchy Implementation
```turtle
# Base logic relationships
catty:LM a catty:Logic ;
    catty:extends catty:null .

catty:LJ a catty:Logic ;
    catty:extends catty:LM ;
    catty:hasRules catty:single-conclusion .

catty:LDJ a catty:Logic ;
    catty:extends catty:LM ;
    catty:hasRules catty:dual-single-conclusion .

catty:LK a catty:Logic ;
    catty:extends catty:LJ ;
    catty:extends catty:LDJ ;
    catty:hasRules catty:full-structural .
```

#### Morphism Implementation
```turtle
# Extension morphisms
catty:ext-LM-LJ a catty:Extension ;
    catty:source catty:LM ;
    catty:target catty:LJ ;
    catty:morphismType catty:structural-extension .

# Interpretation morphisms  
catty:int-LK-LJ a catty:Interpretation ;
    catty:source catty:LK ;
    catty:target catty:LJ ;
    catty:interpretationType catty:double-negation .
```

### Validation Protocols

#### Automated Validation
```bash
# RDF syntax validation
python -c "import rdflib; g = rdflib.Graph(); g.parse('ontology/catty-categorical-schema.jsonld')"

# SHACL validation
python -c "from pyshacl import validate; validate('ontology/catty-categorical-schema.jsonld', 'ontology/catty-thesis-shapes.shacl')"

# SPARQL consistency check
python scripts/validate_rdf.py
```

#### Manual Validation Steps
1. **RDF Syntax Check**: Parse all JSON-LD/Turtle files
2. **OWL Consistency**: Verify class/property relationships
3. **SHACL Validation**: Test against all shapes
4. **SPARQL Queries**: Execute validation queries
5. **External Links**: Verify Wikidata/DBpedia connectivity

### Error Handling

#### Syntax Errors
1. **Identify error location** in RDF source
2. **Check JSON-LD/Turtle syntax** compliance
3. **Validate @context definitions** and prefixes
4. **Fix malformed triples** and syntax issues
5. **Re-validate** until parsing succeeds

#### Validation Failures
1. **Read SHACL violation messages** carefully
2. **Identify specific constraint violations**
3. **Apply corrective actions** to RDF data
4. **Re-run validation** to verify fixes
5. **Document resolved issues** for future reference

#### External Link Failures
1. **Check external service availability**
2. **Verify entity identifiers** are current
3. **Update links** if entities have moved
4. **Handle deprecated entities** appropriately
5. **Validate external relationships**

### Quality Assurance

#### Pre-Development Checklist
- [ ] RDF/OWL standards compliance verified
- [ ] JSON-LD/Turtle syntax validated
- [ ] SHACL shapes prepared and tested
- [ ] SPARQL queries designed and validated
- [ ] External ontology integration planned

#### Development Monitoring
- [ ] All classes and properties properly defined
- [ ] Instance data follows schema constraints
- [ ] External links are valid and current
- [ ] SHACL validation passes all constraints
- [ ] SPARQL queries return expected results

#### Post-Development Validation
- [ ] Complete ontology parses without errors
- [ ] All SHACL shapes validate successfully
- [ ] SPARQL queries perform as expected
- [ ] External links resolve correctly
- [ ] Documentation matches implementation

### Integration Protocols

#### Repository Integration
- Maintain backward compatibility with existing ontologies
- Preserve cross-references between ontology files
- Ensure consistency with thesis content
- Update documentation with ontology changes
- Follow semantic versioning for ontology updates

#### External Tool Integration
- Maintain compatibility with RDF processing tools
- Support standard SPARQL query engines
- Integrate with knowledge graph databases
- Preserve interoperability with semantic web standards
- Support ontology alignment tools

### Version Control

#### Ontology Versioning
- Increment minor versions for backward-compatible additions
- Increment major versions for breaking changes
- Document all changes in ontology changelog
- Maintain deprecated entity information
- Support migration paths between versions

#### Change Management
- Test all changes against validation framework
- Verify impact on dependent artifacts
- Update downstream consumers of ontology
- Maintain compatibility with existing data
- Document migration procedures

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: schema/, bibliography/, thesis/  
**Last Updated**: 2025-01-06