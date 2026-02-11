# Analysis: Queries Requiring Refactoring to External Standards

## Summary

The 13 SPARQL queries in `docs/sparql-examples.md` **violate AGENTS.md constraints** by using custom `catty:` vocabulary instead of consuming from existing W3C/OMG/ISO standards.

## Classification: PENDING REFACTORING

**Reason**: Uses custom vocabulary that duplicates existing international standards

**Action Required**: Refactor to import and consume from W3C OWL 2, OMG DOL, and ISO COLORE

## Violation Analysis

### Violation of Code Reuse Principle

All queries use the prefix:

```sparql
PREFIX catty: <https://github.com/metavacua/CategoricalReasoner/ontology/>
```

**This violates AGENTS.md sourcing priority**: W3C Standards → ISO/IEC Standards → Community Standards → Original Research

**Correct approach**: Replace `catty:` with existing standard prefixes:
- `owl:` for W3C OWL 2 vocabulary
- `dol:` for OMG DOL logic translations
- `colore:` / `theory:` for ISO Common Logic/COLORE category theory

### Redundant Vocabulary (Already Exists in External Standards)

The queries define vocabulary that **duplicates existing W3C/OMG/ISO standards**:

**Custom Vocabulary** → **Existing Standard**:
- `catty:Logic` → `owl:Ontology` (W3C OWL 2)
- `catty:hasLogicalAxiom` → `owl:Axiom` (W3C OWL 2)
- `catty:domain` → `theory:domain` (ISO Common Logic/COLORE)
- `catty:codomain` → `theory:codomain` (ISO Common Logic/COLORE)
- `catty:Extension` → COLORE Category Theory morphism structures
- `catty:AdjointFunctors` → COLORE Category Theory adjunction formalization
- `catty:CurryHowardFunctor` → `dol:LogicTranslation` (OMG DOL)
- `catty:correspondsToLogic` → DOL logic mapping predicates

**Evidence of Redundancy**:
1. **W3C OWL 2 Structural Specification**: Defines `Axiom` as a class and provides functional-style syntax for logic → RDF mappings
2. **ISO/IEC 24707 (Common Logic)**: Formalizes morphism, domain, codomain, and composition
3. **COLORE Repository**: Contains dedicated Category Theory module with formal definitions
4. **OMG DOL Standard**: Explicitly handles logic translations (Curry-Howard correspondences)

### Interoperability Obstruction

By using `catty:` namespace instead of standard prefixes, these queries:
1. **Cannot interoperate** with existing semantic web tools expecting OWL 2 / DOL / COLORE vocabulary
2. **Require custom parsers** instead of leveraging "off-the-shelf" RDF/OWL reasoners
3. **Create vendor lock-in** to Catty-specific implementations
4. **Violate open standards principles** stated in AGENTS.md sourcing hierarchy

## Query-by-Query Refactoring Requirements

### 1. all-logics.rq
**Type**: Basic query
**Purpose**: List all logics with their logical signatures and axioms
**Current Vocabulary**: `catty:Logic`, `catty:hasLogicalSignature`, `catty:hasLogicalAxiom`
**Required Refactoring**: Replace with W3C OWL 2 (`owl:Ontology`, `owl:Axiom`)
**Status**: PENDING REFACTORING

### 2. structural-rules.rq
**Type**: Basic query
**Purpose**: Get all structural rules for Linear Logic
**Current Vocabulary**: `catty:LL`, `catty:hasStructuralRule`
**Required Refactoring**: Model as OWL 2 ontology with axioms representing structural rules
**Status**: PENDING REFACTORING

### 3. morphisms-from-logic.rq
**Type**: Morphism query
**Purpose**: Find all morphisms from Intuitionistic Logic
**Current Vocabulary**: `catty:LJ`, `catty:domain`, `catty:codomain`
**Required Refactoring**: Replace with ISO COLORE Category Theory (`theory:domain`, `theory:codomain`, `theory:morphism`)
**Status**: PENDING REFACTORING

### 4. lattice-order.rq
**Type**: Morphism query
**Purpose**: Get all lattice order relations (Logic A extends Logic B)
**Current Vocabulary**: `catty:Extension`, `catty:domain`, `catty:codomain`
**Required Refactoring**: Model as COLORE partial order structures with morphism composition
**Status**: PENDING REFACTORING

### 5. adjoint-relationships.rq
**Type**: Adjoint query
**Purpose**: Find all adjoint functor pairs
**Current Vocabulary**: `catty:AdjointFunctors`, `catty:sourceCategory`, `catty:targetCategory`
**Required Refactoring**: Replace with COLORE Category Theory adjunction formalization
**Status**: PENDING REFACTORING

### 6. curry-howard-mapping.rq
**Type**: Curry-Howard query
**Purpose**: Get the type theory corresponding to a logic under Curry-Howard
**Current Vocabulary**: `catty:correspondsToLogic`
**Required Refactoring**: Replace with OMG DOL (`dol:LogicTranslation`, `dol:interprets`)
**Status**: PENDING REFACTORING

### 7. curry-howard-functor.rq
**Type**: Curry-Howard query
**Purpose**: Get the Curry-Howard functor mappings
**Current Vocabulary**: `catty:CurryHowardFunctor`, `catty:objectMapping`
**Required Refactoring**: Replace with OMG DOL logic morphism structures
**Status**: PENDING REFACTORING

### 8. extension-hierarchy.rq
**Type**: Lattice query
**Purpose**: Get the extension hierarchy of logics
**Current Vocabulary**: `catty:Extension`, `catty:domain`, `catty:codomain`
**Required Refactoring**: Model using COLORE partial order theory with morphism transitivity
**Status**: PENDING REFACTORING

### 9. lattice-neighbors.rq
**Type**: Lattice query
**Purpose**: Find all immediate neighbors of Linear Logic in the lattice
**Current Vocabulary**: `catty:LL`, `catty:domain`, `catty:codomain`
**Required Refactoring**: Query COLORE lattice structure with covering relation predicates
**Status**: PENDING REFACTORING

### 10. orphan-logics.rq
**Type**: Validation query
**Purpose**: Find logics that have no morphisms to/from them
**Current Vocabulary**: `catty:Logic`, `catty:domain`, `catty:codomain`
**Required Refactoring**: Query OWL 2 ontologies for isolated nodes in morphism graph
**Status**: PENDING REFACTORING

### 11. terminal-logics.rq
**Type**: Validation query
**Purpose**: Find terminal logics (logics that are not extended by any other logic)
**Current Vocabulary**: `catty:Logic`, `catty:Extension`, `catty:domain`
**Required Refactoring**: Query for maximal elements in COLORE partial order
**Status**: PENDING REFACTORING

### 12. find-all-paths-in-lattice.rq (Complex Query)
**Type**: Path-finding query
**Purpose**: Find all paths in the lattice
**Current Vocabulary**: `catty:*` (multiple)
**Required Refactoring**: Use COLORE morphism composition and reachability predicates
**Status**: PENDING REFACTORING

### 13. validate-curry-howard-correspondence.rq (Complex Query)
**Type**: Validation query
**Purpose**: Validate Curry-Howard correspondence
**Current Vocabulary**: `catty:Logic`, `catty:TypeTheory`, `catty:correspondsToLogic`
**Required Refactoring**: Use OMG DOL logic translation validation with bidirectional interpretation
**Status**: PENDING REFACTORING

## Violation of AGENTS.md Constraints

The AGENTS.md file states:

> "Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally."

> "SPARQL Execution: All documented queries must be actually ran against external endpoints."

**Analysis**:

1. These queries **violate the consumption requirement** by defining custom vocabulary instead of importing from W3C/OMG/ISO standards.

2. The **correct interpretation** of AGENTS.md constraints:
   - **Prioritize code reuse**: Use existing W3C OWL 2, OMG DOL, ISO COLORE vocabulary
   - **Import, don't author**: Consume from external standard repositories via SPARQL endpoints or OWL imports
   - **Off-the-shelf interoperability**: Leverage existing semantic web tools rather than creating custom parsers

3. **These queries must be refactored** to consume from external standards per the AGENTS.md sourcing priority hierarchy:
   - **W3C Standards** (OWL 2) → First priority
   - **ISO/IEC Standards** (Common Logic/COLORE) → Second priority  
   - **Community Standards** (Wikidata) → Third priority
   - **Original Research** (custom `catty:` vocabulary) → **Last resort only**

## Required Refactoring Actions

These queries must be refactored according to the following priority:

1. **High Priority: Replace Logic Vocabulary**
   - Remove all `catty:Logic`, `catty:hasLogicalAxiom` references
   - Import W3C OWL 2 vocabulary: `owl:Ontology`, `owl:Axiom`
   - Query existing OWL 2 ontologies for logic instances

2. **High Priority: Replace Category Theory Vocabulary**
   - Remove all `catty:domain`, `catty:codomain`, `catty:Extension` references
   - Import ISO COLORE Category Theory module
   - Query COLORE repository for morphism structures

3. **High Priority: Replace Logic Translation Vocabulary**
   - Remove all `catty:CurryHowardFunctor`, `catty:correspondsToLogic` references
   - Import OMG DOL Standard: `dol:LogicTranslation`, `dol:interprets`
   - Query DOL repositories for existing logic mappings

4. **Documentation Update**
   - Remove all references to "intended Catty ontology structure"
   - Document the refactoring process and external standard integration
   - Update AGENTS.md to reflect code reuse compliance

## Correct Implementation Path

To make these queries executable while complying with AGENTS.md constraints, the following steps are required:

1. **Import External Standards** (DO NOT author new vocabulary):
   - Import W3C OWL 2 vocabulary via standard OWL imports
   - Import ISO COLORE Category Theory module from Common Logic repository
   - Import OMG DOL Standard for logic translations
   - Configure SPARQL endpoints to query these external vocabularies

2. **Refactor Queries to Use External Standards**:
   - Rewrite all 13 queries to use `owl:`, `colore:`, `dol:` prefixes
   - Remove all `catty:` prefix references
   - Query existing W3C/OMG/ISO ontologies via SPARQL Federation

3. **Deploy SPARQL Federation Endpoint**:
   - Configure Apache Jena Fuseki with SPARQL Federation enabled
   - Connect to W3C OWL 2 endpoint, COLORE repository endpoint, OMG DOL endpoint
   - Execute federated queries that span multiple external standards

4. **Validate Against External Standards**:
   - Verify queries return results from W3C/OMG/ISO vocabularies
   - Confirm no custom `catty:` vocabulary is authored or instantiated
   - Document successful integration with "off-the-shelf" semantic web standards

## Conclusion

The 13 queries in `sparql-examples.md` are **syntactically valid SPARQL** but **violate AGENTS.md constraints** by using custom `catty:` vocabulary instead of importing from W3C/OMG/ISO standards.

**Classification**: PENDING REFACTORING (Violates code reuse principle)

**Required Action**: Refactor to consume from existing international standards:
- W3C OWL 2 for logic/axiom vocabulary
- ISO COLORE for category theory vocabulary
- OMG DOL for logic translation vocabulary

**Rationale**: The W3C, OMG, and ISO have already published formal standards for these concepts. Using custom `catty:` namespace creates redundant vocabulary that obstructs "off-the-shelf" interoperability.

**Recommendation**: DO NOT include in thesis as "specification documents" - instead document the refactoring requirements and external standard mappings.
