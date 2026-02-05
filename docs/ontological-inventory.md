> [!IMPORTANT]
> This file has been consolidated into the "Architecture of Catty" TeX document and is marked for future removal.

# Ontological Inventory: Categorical Resources for Catty

## Executive Summary

This inventory documents concrete, publicly accessible RDF/OWL schemas and knowledge graphs that support category-theoretic modeling of formal logics. Each entry includes: name, URI, categorical constructs defined, logic-categorical relationships supported, license, and Catty compatibility.

---

## 1. DBPedia Category Theory Schema

### Identification
- **Name**: DBPedia Category and Logic Ontology
- **Primary URI**: http://dbpedia.org/src/ontology/
- **Category Theory Base**: http://dbpedia.org/page/Category_(mathematics)
- **Logic Base**: http://dbpedia.org/page/Sequent_calculus

### Categorical Constructs Defined

DBPedia provides RDF representations of the following categorical concepts:

| Concept | DBPedia URI | RDF Type | Key Properties |
|---------|-------------|----------|----------------|
| Category (mathematics) | dbo:Category | owl:Class | dbo:domain, dbo:codomain |
| Functor | dbo:Functor | owl:Class | dbo:domain, dbo:codomain |
| Natural Transformation | dbo:... | owl:Class | (limited explicit definition) |
| Monoid | dbo:Monoid | owl:Class | (algebraic structure) |
| Group | dbo:Group | owl:Class | (algebraic structure) |
| Limit | dbo:Limit_(category_theory) | owl:Class | (universal constructions) |
| Colimit | dbo:Colimit | owl:Class | (universal constructions) |
| Adjoint Functors | dbo:Adjoint_functors | owl:Class | (adjunction relationship) |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Logics as categorical objects | **Partial** | Individual logic pages exist but lack explicit `rdf:type` assertions |
| Morphisms between logics | **Minimal** | Some embeddings mentioned but not formalized as RDF morphisms |
| Sequent restrictions | **Reference Only** | Text descriptions, no RDF structure |
| Structural rules | **Reference Only** | Text descriptions in logic pages |
| Proof system embeddings | **Minimal** | Some translations documented textually |

### License and Catty Compatibility

- **License**: Creative Commons Attribution-ShareAlike 3.0 (CC BY-SA 3.0)
- **Catty Compatibility**: **Compatible (with attribution and share-alike)**
- **Integration Approach**: Cross-reference for external discovery; selective import of key concepts

### Example Usage

```turtle
@prefix dbo: <http://dbpedia.org/src/ontology/> .
@prefix dbr: <http://dbpedia.org/resource/> .

dbr:Category_(mathematics) a dbo:Concept ;
    dbo:wikiPageRedirects dbr:Category_theory .

dbr:Sequent_calculus a dbo:ScientificDiscipline ;
    dbo:field dbr:Mathematical_logic ;
    dbo:influencedBy dbr:Natural_deduction .
```

### Limitations for Catty

1. Logics are not explicitly modeled as objects in a category
2. No explicit morphism relationships between logics
3. Lattice structure (2D) not represented
4. Curry-Howard correspondence not formalized in RDF

---

## 2. Wikidata Mathematical Ontology

### Identification
- **Name**: Wikidata Mathematics and Logic
- **Primary URI**: http://www.wikidata.org/
- **SPARQL Endpoint**: https://query.wikidata.org/sparql
- **Category Theory Item**: Q719395
- **Logic Item**: Q8078

### Categorical Constructs Defined

| Concept | Wikidata QID | Properties |
|---------|--------------|------------|
| Category (mathematics) | Q719395 | P155 (domain), P156 (codomain) |
| Functor | Q864475 | P1552 (maps to), P279 (subclass) |
| Natural Transformation | Q1442189 | P1552, P279 |
| Adjoint Functors | Q357858 | P1552, P279 |
| Monoid | Q207448 | P279, P1552 |
| Group | Q83478 | P279, P1552 |
| Isomorphism | Q189112 | P1552 (inverse element via P154) |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Logics as categorical objects | **Good** | Each logic is a named item (e.g., the sequent calculus, LK = Q1771121, LJ = Q176786) |
| Morphisms between logics | **Partial** | Embeddings can be modeled via `P1552` (maps to) |
| Sequent restrictions | **Reference Only** | Described in descriptions, not RDF structure |
| Structural rules | **Reference Only** | Described in descriptions |
| Proof system embeddings | **Partial** | Can use `P1552` for translations |

### License and Catty Compatibility

- **License**: CC0 1.0 Universal (Public Domain)
- **Catty Compatibility**: **Fully Compatible** (no attribution required)
- **Integration Approach**: Direct import; can use `owl:sameAs` for linking

### Example SPARQL Query

```sparql
SELECT ?logic ?logicLabel ?sequentForm
WHERE {
  ?logic wdt:P31 wd:Q8078 .  # instance of logic
  ?logic rdfs:label ?logicLabel .
  FILTER(LANG(?logicLabel) = "en")
  OPTIONAL { ?logic wdt:P361 ?sequentForm . }
}
LIMIT 20
```

### Example Turtle

```turtle
@prefix wd: <http://www.wikidata.org/entity/> .
@prefix wdt: <http://www.wikidata.org/prop/direct/> .
@prefix schema: <http://schema.org/> .

wd:Q176786 a wdt:Q8078 ;  # LJ
    rdfs:label "intuitionistic logic"@en ;
    wdt:P361 wd:Q719395 ;  # part of category theory (example mapping)
    schema:description "Intuitionistic sequent calculus"@en .

wd:Q841728 a wdt:Q8078 ;  # Linear Logic
    rdfs:label "linear logic"@en ;
    wdt:P1552 wd:Q176786 ;  # maps to intuitionistic logic (translation)
    schema:description "Substructural logic without weakening/contraction"@en .
```

### Limitations for Catty

1. Limited explicit categorical properties on logic items
2. No native representation of the 2D lattice structure
3. Need to extend with custom properties for sequent forms and structural rules

---

## 3. OpenMath Content Dictionaries

### Identification
- **Name**: OpenMath Category Theory Content Dictionary
- **Primary URI**: http://www.openmath.org/cd/
- **Category Theory CD**: http://www.openmath.org/cd/category1.ocd
- **Logic CD**: http://www.openmath.org/cd/logic1.ocd

### Categorical Constructs Defined

OpenMath uses XML-based symbol definitions. Key symbols:

| Symbol | CD | Description |
|--------|-----|-------------|
| category | category1 | The category concept |
| object | category1 | Object in a category |
| morphism | category1 | Morphism in a category |
| composition | category1 | Composition of morphisms |
| domain | category1 | Domain of morphism |
| codomain | category1 | Codomain of morphism |
| functor | category1 | Functor between categories |
| natural_transformation | category1 | Natural transformation |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Logics as categorical objects | **Minimal** | Logic symbols exist in logic1.cd, not connected to category1.cd |
| Morphisms between logics | **Minimal** | Generic morphism symbols exist, no logic-specific ones |
| Sequent restrictions | **None** | Not represented |
| Structural rules | **None** | Not represented |

### License and Catty Compatibility

- **License**: BSD 3-Clause License
- **Catty Compatibility**: **Fully Compatible**
- **Integration Approach**: Transform XML CD to RDF/OWL via XSLT or custom parser

### Example OpenMath XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<CD xmlns="http://www.openmath.org/OpenMathCD">
  <CDName>category1</CDName>
  <CDURL>http://www.openmath.org/cd/category1.ocd</CDURL>

  <CDDefinition>
    <Name>category</Name>
    <Description>A mathematical category consisting of objects and morphisms</Description>
  </CDDefinition>

  <CDDefinition>
    <Name>morphism</Name>
    <Description>A morphism in a category, with domain and codomain</Description>
  </CDDefinition>

  <CDDefinition>
    <Name>functor</Name>
    <Description>A functor mapping between categories</Description>
  </CDDefinition>
</CD>
```

### RDF Transformation Example

```turtle
@prefix om: <http://www.openmath.org/cd/> .
@prefix cat: <http://www.openmath.org/cd/category1#> .

cat:category a owl:Class ;
    rdfs:label "Category" ;
    rdfs:comment "A mathematical category consisting of objects and morphisms" .

cat:morphism a owl:Class ;
    rdfs:label "Morphism" ;
    rdfs:subClassOf [ owl:onProperty cat:domain ; owl:cardinality 1 ] ;
    rdfs:subClassOf [ owl:onProperty cat:codomain ; owl:cardinality 1 ] .
```

### Limitations for Catty

1. Not RDF/OWL natively; requires transformation
2. No explicit connection between category theory and logic CDs
3. No lattice structure representation
4. Minimal categorical semantics

---

## 4. COLORE (Common Logic Ontology RepOsitory)

### Identification
- **Name**: COLORE - Common Logic Ontology Repository
- **Primary URI**: http://colore.oor.net/
- **Repository**: https://github.com/wfaller/colore

### Categorical Constructs Defined

COLORE focuses on foundational ontologies:

| Ontology | Categorical Content | Status |
|----------|--------------------|--------|
| DOLCE | None | Foundational ontology, no explicit category theory |
| BFO | None | Foundational ontology, no explicit category theory |
| Logic Modules | Minimal | First-order logic structures, no categorical semantics |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Logics as categorical objects | **None** | Logics modeled as flat classes |
| Morphisms between logics | **None** | No morphism concepts |
| Sequent restrictions | **None** | Not represented |

### License and Catty Compatibility

- **License**: Creative Commons Attribution 4.0 (CC BY 4.0)
- **Catty Compatibility**: **Compatible (with attribution)**
- **Integration Approach**: Use as foundational ontology; extend with categorical axioms

### Limitations for Catty

1. No category theory content
2. Logics modeled flat, not categorically
3. Requires significant extension for Catty's needs

---

## 5. nLab Semantic Linked Data

### Identification
- **Name**: nLab - Categorical Mathematics Wiki
- **Primary URI**: https://ncatlab.org/
- **Category Theory Section**: https://ncatlab.org/nlab/show/category+theory
- **Categorical Logic**: https://ncatlab.org/nlab/show/categorical+logic

### Categorical Constructs Defined

nLab provides extensive coverage of category theory, with **experimental RDF exports**:

| Concept | nLab Page | RDF Status |
|---------|-----------|------------|
| Category | /show/category | **Available** (limited structure) |
| Functor | /show/functor | **Available** |
| Natural Transformation | /show/natural+transformation | **Available** |
| Adjoint Functor | /show/adjoint+functor | **Available** |
| Topos | /show/topos | **Available** |
| *-Autonomous Category | /show/star-autonomous+category | **Available** |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Logics as categorical objects | **Good** | Extensive coverage of categorical logic |
| Curry-Howard correspondence | **Excellent** | Dedicated page with categorical treatment |
| Sequent calculus categorical semantics | **Good** | Coverage of categorical proof theory |

### License and Catty Compatibility

- **License**: Creative Commons Attribution-ShareAlike 3.0 (CC BY-SA 3.0)
- **Catty Compatibility**: **Compatible (with attribution and share-alike)**
- **Integration Approach**: Cross-reference for external discovery; partial import for categorical concepts

### Example nLab Structure (RDF-like representation)

```turtle
@prefix nlab: <https://ncatlab.org/nlab/show/> .
@prefix vocab: <https://ncatlab.org/nlab/vocab/> .

nlab:category a vocab:Concept ;
    rdfs:label "category" ;
    vocab:relatedConcept nlab:functor, nlab:topos ;
    vocab:category "higher category theory"@en .

nlab:categorical+logic a vocab:Concept ;
    rdfs:label "categorical logic" ;
    vocab:relatesLogic nlab:Curry-Howard+correspondence ;
    vocab:relatesCategory nlab:topos, nlab:star-autonomous+category .
```

### Limitations for Catty

1. RDF exports are experimental and limited in structure
2. Not designed as a knowledge graph for reasoning
3. Primary use case is human-readable documentation

---

## 6. HoTT (Homotopy Type Theory) Knowledge Graph

### Identification
- **Name**: HoTT Library - Homotopy Type Theory
- **Primary URI**: https://homotopytypetheory.org/
- **Formalization**: https://github.com/HoTT/HoTT

### Categorical Constructs Defined

HoTT provides categorical semantics via $\infty$-groupoids:

| Concept | HoTT Representation | Categorical Interpretation |
|---------|---------------------|----------------------------|
| Type | Type | Object in $\infty$-groupoid |
| Function | Type → Type | Morphism in $\infty$-groupoid |
| Identity Path | x = y | Path (higher morphism) |
| Univalence Axiom | UA | Equivalence of categories |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Curry-Howard correspondence | **Excellent** | Core of HoTT foundation |
| Type theory as category | **Excellent** | Types form $\infty$-groupoids |
| Logics as type theories | **Partial** | Specific logics can be encoded |

### License and Catty Compatibility

- **License**: BSD 2-Clause License
- **Catty Compatibility**: **Fully Compatible**
- **Integration Approach**: Extract categorical structures; use Curry-Howard insights

### Limitations for Catty

1. Specialized to homotopy type theory
2. Not in RDF/OWL format
3. Requires Coq/Agda parsing

---

## 7. Coq Categorical Logic Libraries

### Identification
- **Name**: Coq Mathematical Components - Category Theory
- **Primary URI**: https://math-comp.github.io/
- **Category Theory Library**: https://github.com/math-comp/math-comp

### Categorical Constructs Defined

Coq's MathComp provides formalized category theory:

| Coq Module | Categorical Concept | Export Format |
|------------|---------------------|---------------|
| Category | Category definition | Coq .v files |
| Functor | Functor definition | Coq .v files |
| Adjoint | Adjoint functors | Coq .v files |
| NatTrans | Natural transformations | Coq .v files |
| Presheaf | Presheaf categories | Coq .v files |

### Export Formats

- **Coq Native**: .v files (Gallina)
- **XML Export**: Via `coqdoc` or custom tools
- **JSON Export**: Via `serapi` or custom serialization

### License and Catty Compatibility

- **License**: CeCILL-B (BSD-compatible)
- **Catty Compatibility**: **Fully Compatible**
- **Integration Approach**: Custom parser to extract categorical structures to RDF/OWL

### Example Coq to RDF Mapping

```coq
(* Coq definition *)
Record Category := {
  Obj : Type;
  Hom : Obj -> Obj -> Type;
  id : forall X, Hom X X;
  comp : forall X Y Z, Hom Y Z -> Hom X Y -> Hom X Z;
  (* axioms omitted *)
}.
```

```turtle
@prefix coq: <http://coq.inria.fr/library/> .
@prefix catty: <https://github.com/metavacua/CategoricalReasoner/ontology/> .

coq:Category a owl:Class ;
    rdfs:label "Category (Coq)" ;
    owl:equivalentClass catty:Category .
```

### Limitations for Catty

1. Not in RDF/OWL natively
2. Requires Coq parser
3. Focused on mathematical foundations, not logic lattice

---

## 8. Lean MathLib Category Theory

### Identification
- **Name**: Lean 4 MathLib - Category Theory
- **Primary URI**: https://leanprover-community.github.io/mathlib4/
- **Category Theory**: https://leanprover-community.github.io/mathlib4/Mathlib/CategoryTheory/

### Categorical Constructs Defined

Lean's MathLib provides comprehensive category theory formalization:

| Lean Module | Categorical Concept | Status |
|-------------|---------------------|--------|
| Category | Category definition | **Complete** |
| Functor | Functor definition | **Complete** |
| NatTrans | Natural transformations | **Complete** |
| Adjoint | Adjoint functors | **Complete** |
| Limits | Limits/Colimits | **Complete** |
| Monoidal | Monoidal categories | **Complete** |
| Monoidal.Closed | Closed monoidal categories | **Complete** |

### Export Formats

- **Lean Native**: .lean files
- **Export to JSON**: Via `lake build` with custom extensions
- **AST Export**: Via Lean's internal compiler

### License and Catty Compatibility

- **License**: Apache 2.0 License
- **Catty Compatibility**: **Fully Compatible**
- **Integration Approach**: Export AST to JSON; transform to RDF/OWL

### Example Lean Structure

```lean
namespace CategoryTheory

class Category (obj : Type u) where
  Hom : obj → obj → Type v
  id : ∀ X, Hom X X
  comp : ∀ {X Y Z}, Hom Y Z → Hom X Y → Hom X Z
  id_comp : ∀ {X Y} (f : Hom X Y), comp (id Y) f = f
  comp_id : ∀ {X Y} (f : Hom X Y), comp f (id X) = f
  assoc : ∀ {W X Y Z} (f : Hom W X) (g : Hom X Y) (h : Hom Y Z),
    comp (comp h g) f = comp h (comp g f)

end CategoryTheory
```

### Lean AST to RDF Example

```turtle
@prefix lean: <http://leanprover.org/library/> .
@prefix catty: <https://github.com/metavacua/CategoricalReasoner/ontology/> .

lean:CategoryTheory.Category a owl:Class ;
    rdfs:label "Category (Lean)" ;
    owl:equivalentClass catty:Category ;
    rdfs:comment "Lean's definition of a category, with Hom-sets, identity, composition, and axioms" .
```

### Categorical Logic Support in Lean

| Lean Module | Logic Coverage |
|-------------|---------------|
| CategoryTheory.Sheaf | Categorical logic of sheaves |
| CategoryTheory.Topos | Topos theory, internal logic |
| CategoryTheory.Site | Grothendieck sites, geometric logic |

### Limitations for Catty

1. Not in RDF/OWL natively
2. Requires Lean parser/AST export
3. Can be computationally intensive to extract

---

## 9. Isabelle/HOL Categorical Exports

### Identification
- **Name**: Isabelle AFP (Archive of Formal Proofs) - Category Theory
- **Primary URI**: https://www.isa-afp.org/
- **Category Theory Entries**: https://www.isa-afp.org/topics.html#Category_theory

### Categorical Constructs Defined

Isabelle AFP includes categorical logic entries:

| Entry | Categorical Content | Status |
|-------|---------------------|--------|
| Category | Category definitions | **Complete** |
| Functor | Functor definitions | **Complete** |
| Adjoint | Adjoint functors | **Partial** |
| Presheaf | Presheaf categories | **Partial** |

### Export Formats

- **Isabelle Native**: .thy files
- **XML Export**: Via `isabelle build -o export_theory`
- **Proof General**: Interactive access

### License and Catty Compatibility

- **License**: BSD 3-Clause License
- **Catty Compatibility**: **Fully Compatible**
- **Integration Approach**: Export theories to XML; transform to RDF/OWL

### Limitations for Catty

1. Not in RDF/OWL natively
2. Requires Isabelle export pipeline
3. Less comprehensive than Coq/Lean for category theory

---

## 10. ProofWiki Categorical Sections

### Identification
- **Name**: ProofWiki - Mathematics Encyclopedia
- **Primary URI**: https://proofwiki.org/wiki/
- **Category Theory**: https://proofwiki.org/wiki/Category_Theory
- **Sequent Calculus**: https://proofwiki.org/wiki/Sequent_Calculus

### Categorical Constructs Defined

ProofWiki has structured content on:

| Topic | ProofWiki Page | Status |
|-------|----------------|--------|
| Category | /wiki/Category | **Text only** |
| Functor | /wiki/Functor | **Text only** |
| Natural Transformation | /wiki/Natural_Transformation | **Text only** |
| Adjoint Functor | /wiki/Adjoint_Functor | **Text only** |
| Sequent Calculus | /wiki/Sequent_Calculus | **Text only** |

### Logic-Categorical Relationships Supported

| Relationship | Support Level | Details |
|--------------|---------------|---------|
| Categorical semantics of logic | **Good** | Dedicated sections |
| Curry-Howard correspondence | **Partial** | Mentioned in type theory sections |

### License and Catty Compatibility

- **License**: Creative Commons Attribution-ShareAlike 3.0 (CC BY-SA 3.0)
- **Catty Compatibility**: **Compatible (reference only)**
- **Integration Approach**: Cross-reference for human-readable documentation

### Limitations for Catty

1. No RDF/OWL export
2. Text-only format
3. Cannot be directly imported

---

## 11. OMDoc with Category Theory Markup

### Identification
- **Name**: OMDoc - Open Mathematical Documents
- **Primary URI**: https://omdoc.org/
- **Category Theory Support**: Via OMDoc's mathematical markup

### Categorical Constructs Defined

OMDoc supports structured mathematical markup including:

| OMDoc Element | Categorical Content | Status |
|--------------|---------------------|--------|
| `<theory>` | Category definitions | **Supported** |
| `<definition>` | Functor, morphism definitions | **Supported** |
| `<morphism>` | Mathematical mappings | **Supported** |

### Export/Transformation

- **OMDoc XML**: Primary format
- **RDF Transformation**: Via OMDoc2RDF XSLT
- **OMDoc-OpenMath Integration**: Native

### License and Catty Compatibility

- **License**: BSD 2-Clause License
- **Catty Compatibility**: **Fully Compatible**
- **Integration Approach**: OMDoc → RDF transformation

### Limitations for Catty

1. Not RDF/OWL natively
2. Requires transformation pipeline
3. Generic mathematical markup, not logic-specific

---

## Summary Comparison Table

| Resource | Categorical Coverage | Logic Coverage | RDF/OWL Native | License | Catty Compatibility | Recommended Use |
|----------|---------------------|----------------|----------------|---------|---------------------|-----------------|
| DBPedia | Good | Partial | **Yes** | CC BY-SA 3.0 | Compatible | Cross-reference |
| Wikidata | Good | Partial | **Yes** | **CC0** | **Fully Compatible** | Direct import |
| OpenMath | Partial | Minimal | **No** (XML) | BSD 3-Clause | Fully Compatible | Transform to RDF |
| COLORE | None | Minimal | **Yes** | CC BY 4.0 | Compatible | Foundation only |
| nLab | Excellent | Excellent | **Partial** (exp.) | CC BY-SA 3.0 | Compatible | Cross-reference |
| HoTT | Excellent | Partial | **No** | BSD 2-Clause | Fully Compatible | Extract insights |
| Coq | Excellent | Partial | **No** | CeCILL-B | Fully Compatible | Parse & extract |
| Lean | Excellent | Partial | **No** | Apache 2.0 | Fully Compatible | Parse & extract |
| Isabelle | Good | Partial | **No** | BSD 3-Clause | Fully Compatible | Export & transform |
| ProofWiki | Good | Good | **No** | CC BY-SA 3.0 | Compatible | Reference only |
| OMDoc | Partial | Partial | **No** | BSD 2-Clause | Fully Compatible | Transform to RDF |

---

## Integration Roadmap for Catty

### Phase 1: Direct Import (Immediate)

1. **Wikidata** (CC0)
   - Import logic items (LK, LJ, LL, etc.)
   - Link via `owl:sameAs`
   - Extract basic categorical concepts

2. **DBPedia** (CC BY-SA)
   - Cross-reference category theory concepts
   - Attribution required

### Phase 2: Transform and Import (Short-term)

3. **OpenMath** (BSD)
   - Transform category1.cd to RDF/OWL
   - Create Catty-specific extensions

4. **OMDoc** (BSD)
   - Set up OMDoc → RDF pipeline
   - Import category theory markup

### Phase 3: Extraction and Parsing (Medium-term)

5. **Lean MathLib** (Apache 2.0)
   - Develop AST exporter for category theory
   - Parse to RDF/OWL

6. **Coq MathComp** (CeCILL-B)
   - Develop .v parser
   - Extract categorical structures

### Phase 4: Cross-Reference Only (Ongoing)

7. **nLab** (CC BY-SA)
   - Link to categorical concepts
   - Attribution required

8. **ProofWiki** (CC BY-SA)
   - Link to definitions and proofs
   - Attribution required

---

## License Summary for Catty

| License | Compatibility | Attribution Required? | Share-Alike Required? |
|---------|---------------|----------------------|----------------------|
| CC0 | **Fully Compatible** | No | No |
| BSD 2-Clause | **Fully Compatible** | Yes | No |
| BSD 3-Clause | **Fully Compatible** | Yes | No |
| Apache 2.0 | **Fully Compatible** | Yes | No |
| CeCILL-B | **Fully Compatible** | Yes | No |
| CC BY 4.0 | Compatible | Yes | No |
| CC BY-SA 3.0 | Compatible | Yes | Yes |

**Catty License**: GNU Affero General Public License v3.0 (AGPL-3.0)
- All compatible resources can be used
- Attribution required for non-PD licenses
- Share-alike restrictions for CC BY-SA resources
