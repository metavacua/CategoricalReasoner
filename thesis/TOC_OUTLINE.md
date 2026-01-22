# Catty: Structured Table of Contents Outline

## A Thesis on Categorical Foundations for Logics and Their Morphisms

---

## Part I: Genesis — Where Catty Comes From

### I.A: The Resource-Sensitive Revolution (Linear Logic)

**Purpose**: Establish linear logic as Catty's computational substrate—where propositions become resources with consumption/replication constraints.

**Key Topics**:
1. Girard's 1987 discovery: Beyond intuitionistic and classical
2. The exponential modality: `!` (replication) and `?` (choice)
3. Multiplicative vs. additive connectives
4. Decidability boundaries: Light linear logic and tractable fragments
5. Linear logic as partial Rust ownership model (source code)

**Source Material**: Girard (1987), Urbas (1993), Restall (2000)

**Connection to Catty**: Linear logic is not a foundation to build upon but the *initial object* from which all other logics extend. Catty's lattice begins at LL.

---

### I.B: Structural Rules and the Anatomy of Sequents

**Purpose**: Reveal the hidden architecture of sequent calculi—how weakening, contraction, and exchange shape what can be proven.

**Key Topics**1. Gentzen's sequent calculi: LJ, LK, and LM
2. Structural rules as logical parameters
3. Left/Right (LHS/RHS) restrictions: Single-conclusion vs. multi-conclusion
4. Contraction: The cognitive cost of reuse
5. Weakening: The ecology of assumptions
6. Exchange: Commutativity and non-commutativity

**Source Material**: Gentzen (1935), Negri (2011), Restall (2000)

**Connection to Catty**: Structural rules are the *coordinates* of Catty's lattice. Each rule's presence/absence defines a point in logic-space.

---

### I.C: Constructive Foundations and the Witness Principle

**Purpose**: Establish intuitionistic logic's constructive commitment—proofs are computational objects, not abstract truths.

**Key Topics**:
1. Brouwer-Heyting-Kolmogorov (BHK) interpretation
2. Kripke semantics for intuitionistic logic
3. Proofs as constructions: Evidence vs. truth
4. Negation as refutation: Failure to construct
5. Intuitionistic logic as computational substrate (LJ → LK via double-negation)

**Source Material**: Kripke (1965), Sambin (2003)

**Connection to Catty**: Intuitionistic logic demonstrates that *syntactic minimality enables semantic power*. The initial logic LM sits at the intersection of LJ and LDJ.

---

### I.D: Refutation as Construction — Popper and Nelson

**Purpose**: Integrate the often-ignored negative side of logic—how refutation is as constructive as proof.

**Key Topics**:
1. Popperian refutation: Falsification as logical operation
2. Nelson's constructive negation: Strong negation and contrast
3. Dualization: From proof to refutation
4. The symmetry of proof and refutation in sequent calculi
5. LDJ (dual intuitionistic logic) as refutation-centric logic

**Source Material**: Nelson's work on constructive negation, Popperian methodology

**Connection to Catty**: Catty's lattice is *two-sided*—proofs extend upward, refutations extend downward. LNC (Law of Non-Contradiction) and LEM (Law of Excluded Middle) are independent coordinates.

---

### I.E: The Great Unification — Curry-Howard-Lambek

**Purpose**: Present the three-fold correspondence that makes Catty possible—logics, types, and categories are the same thing.

**Key Topics**:
1. Curry's combinatory logic → Howard's formulae-as-types
2. Lambek's categorical semantics: Categories of proofs
3. The triangle: Logic (deduction) ↔ Type Theory (computation) ↔ Category Theory (structure)
4. CCCs and *-autonomous categories
5. Proof normalization as categorical isomorphisms

**Source Material**: Curry (1934), Howard (1969), Lambek (1988), Mac Lane (1971)

**Connection to Catty**: Catty's DSL is not documentation—*it is the thesis*. The DSL is a computational witness proving that the lattice exists.

---

### I.F: Beyond Explosion — Paraconsistent Reasoning

**Purpose**: Show that contradiction need not collapse into triviality—logics where A ∧ ¬A ⊬ B.

**Key Topics**:
1. Priest's dialetheism and LP (Logic of Paradox)
2. Multiple truth values: True, False, Both, Neither
3. Explosion (Ex Falso Quodlibet) as optional axiom
4. The role of contraction in explosion
5. Paraconsistent logics in the Catty lattice

**Source Material**: Graham Priest's work, substructural logic treatments

**Connection to Catty**: Explosion is a *parameter*, not a universal law. Catty's lattice includes logics where contradiction is informative rather than destructive.

---

### I.G: Quantum Logic and Non-Distributivity

**Purpose**: Connect Catty to quantum foundations—where distributivity fails and measurement matters.

**Key Topics**:
1. Birkhoff-von Neumann quantum logic
2. Zizzi's work: Quantum logic as substructural
3. Sambin's Basic Logic: From quantum to general substructural
4. Non-distributive lattices as logic spaces
5. Relevance: When A ∧ B does not imply A

**Source Material**: Zizzi, Sambin (2003)

**Connection to Catty**: Quantum logic demonstrates that *classical assumptions are not universal*. Catty's lattice accommodates non-distributive structures.

---

### I.H: The Structural Unifying Theory — Category Theory

**Purpose**: Provide the meta-language that makes all connections explicit—functors, natural transformations, adjunctions.

**Key Topics**:
1. Categories, objects, morphisms: The basic ontology
2. Functors: Structure-preserving maps between categories
3. Natural transformations: Parametric polymorphism
4. Adjoint relationships: Universal properties and Galois connections
5. Limits and colimits: Terminal and initial objects
6. Monoidal categories: Linear logic's categorical home

**Source Material**: Mac Lane (1971), Pierce (1991), Lawvere (1963)

**Connection to Catty**: Category theory is Catty's *metalanguage*. The lattice of logics is literally a category where objects are logics and morphisms are extensions.

---

## Part II: Architecture — The Lattice of Logics

### II.A: The Two-Dimensional Lattice Structure

**Purpose**: Define Catty's central structure—the lattice where X-axis = sequent restrictions, Y-axis = structural rules.

**Key Topics**:
1. Horizontal axis: LHS/RHS restrictions (single vs. multi-conclusion)
2. Vertical axis: Structural rules (weakening, contraction, exchange)
3. Intersection points: LM, LJ, LDJ, LK, LL
4. The lattice as poset category
5. Compatibility and incompatibility zones

**Source Material**: Ontology file `two-d-lattice-category.jsonld`

**Connection to Catty**: The lattice is not an analogy—it is the thesis's *mathematical structure*. All claims about logics are claims about positions in this lattice.

---

### II.B: Initial Objects — The Minimal Logics

**Purpose**: Identify and characterize the minimal logics from which others extend.

**Key Topics**:
1. LM: Minimal common sublogic
2. LL: Linear logic as resource-minimal base
3. Minimality proofs: What cannot be removed
4. Initial objects in the category of logics
5. The inversion principle: Minimal syntax, maximal semantics

**Connection to Catty**: LM and LL are the *starting points*, not "incomplete" logics. Catty inverts the conventional hierarchy where classical logic is "complete."

---

### II.C: Terminal Objects — Classical Logic and Closure

**Purpose**: Characterize the terminal logic (LK) and what closure means.

**Key Topics**:
1. LK as terminal object: Every logic extends to classical
2. LEM: Law of Excluded Middle as terminal property
3. LNC: Law of Non-Contradiction and its dual
4. Closure under extension: What LK provides
5. Independence of LEM and LNC: Neither implies the other

**Connection to Catty**: LK is terminal, not superior. The thesis maintains *logical plurality*—no logic is "more correct" than others.

---

### II.D: Substructural Branches — Affine and Relevant Logics

**Purpose**: Explore the branches where structural rules are selectively restored.

**Key Topics**:
1. ALL (Affine Linear Logic): Weakening added, contraction absent
2. RLL (Relevant Linear Logic): Contraction added, weakening absent
3. Substructural hierarchy: LL → ALL/RLL → LK
4. Resource semantics: Erasure vs. duplication
5. Programming language analogues: Ownership patterns

**Source Material**: Restall (2000), Girard (1987)

**Connection to Catty**: These branches show *compatible extensions*—adding one structural rule while preserving another property.

---

## Part III: Morphisms — How Logics Relate

### III.A: Extension Morphisms

**Purpose**: Formalize when and how one logic extends another.

**Key Topics**:
1. Extension as inclusion: LJ ⊂ LK
2. Axiom addition: LEM, LNC, Explosion as optional
3. Rule addition: Weakening, contraction, exchange
4. Conservative extension: What new theorems appear
5. Extension diagrams: LM → LJ, LM → LDJ, LJ → LK

**Source Material**: Ontology file `morphism-catalog.jsonld`

**Connection to Catty**: Extensions are *morphisms* in the category. The extension LM → LJ is as formal as a function in typed λ-calculus.

---

### III.B: Interpretation Morphisms

**Purpose**: Formalize when one logic can be embedded in another (not just extended).

**Key Topics**:
1. Double-negation embedding: LJ → LK
2. Translation semantics: Meaning preservation
3. Galois connections between logics
4. Adjoint functors: LK ⊣ LJ
5. Interpretation vs. extension: What gets lost

**Connection to Catty**: Interpretations are *losing morphisms*—they preserve some structure while collapsing others. The thesis uses them to show where logics are compatible.

---

### III.C: Functors Between Logic Categories

**Purpose**: Map between different logical frameworks while preserving structure.

**Key Topics**:
1. The Curry-Howard functor: Proofs ↔ Programs
2. Translation functors: Between linear and classical
3. Natural transformations: Parametric translations
4. Functor composition: Chains of interpretations
5. Categorical equivalence: When logics are "the same"

**Source Material**: Ontology file `curry-howard-categorical-model.jsonld`

**Connection to Catty**: Functors are *higher-order morphisms*—they map entire logic categories, not just individual logics.

---

## Part IV: Universes — Logics as Computational Substrates

### IV.A: The Logic-Universe Correspondence

**Purpose**: Establish each logic as a distinct computational universe with its own computational character.

**Key Topics**:
1. LJ: Functional programming (constructive proofs)
2. LK: Classical computation (non-constructive proofs)
3. LL: Resource-constrained computation
4. Quantum logics: Post-classical computation
5. Paraconsistent logics: Inconsistent but non-trivial computation

**Connection to Catty**: The thesis does not say "logics are like programming languages"—it says *logics are programming languages*, each with different computational tradeoffs.

---

### IV.B: Rust as Partial Linear Logic Implementation

**Purpose**: Demonstrate that modern programming languages embody linear-logic principles.

**Key Topics**:
1. Ownership: The `!` modality in practice
2. Borrowing: Uniqueness without cloning
3. Lifetimes: Resource lifetime tracking
4. The affine gap: Rust allows weakening, not contraction
5. Future directions: Linear types in Rust type system

**Connection to Catty**: Rust is *empirical evidence* that linear logic principles are computationally relevant. Catty's lattice predicts Rust's behavior.

---

### IV.C: Decidability Boundaries and Tractable Fragments

**Purpose**: Show where computation becomes intractable and where it remains feasible.

**Key Topics**:
1. Light linear logic: Polynomial-time computation
2. Finite extensions: What can be decided efficiently
3. The LK closure problem: Classical logic is undecidable
4. Bounded linear logic: Controlled recursion
5. Tractable sublattices: Where Catty's DSL can run

**Source Material**: Girard's work on light linear logic

**Connection to Catty**: Catty's DSL must live in a *tractable fragment*. The lattice shows which logics admit decidable proof search.

---

## Part V: Witness — Proofs as Programs

### V.A: The Catty DSL — Syntax and Semantics

**Purpose**: Present the domain-specific language that *is* the thesis's computational witness.

**Key Topics**:
1. DSL syntax: Defining logics and morphisms
2. Semantic domains: What each construct means
3. Type checking: Well-formed logics
4. Evaluation: Running proofs as programs
5. Compilation: From DSL to categorical structures

**Source Material**: Ontology file `catty-categorical-schema.jsonld`

**Connection to Catty**: The DSL is not documentation of the thesis—it *is* the thesis. The code proves the lattice exists.

---

### V.B: Commutative Diagrams as Computation

**Purpose**: Show that commutative diagrams are not illustrations—they are computational proofs.

**Key Topics**:
1. Diagram syntax: Objects, morphisms, compositions
2. Commutativity constraints: Path equality
3. Diagram verification: Automated proof checking
4. Natural transformations as polymorphic programs
5. Adjunction diagrams: Universal properties computed

**Connection to Catty**: A commutative diagram that verifies is a *proof* that the categorical structure holds. Catty's diagrams are executable.

---

### V.C: The Logic Definition Language

**Purpose**: Provide a complete language for defining logics within Catty.

**Key Topics**:
1. Connectives: Multiplicative, additive, exponential
2. Structural rules: Weakening, contraction, exchange
3. Axioms: LEM, LNC, Explosion
4. Sequent restrictions: Single vs. multi-conclusion
5. Meta-theory: What the definition entails

**Connection to Catty**: Users define logics *in* Catty, not *about* Catty. The DSL is the universal logic definition interface.

---

### V.D: Morphism Construction and Composition

**Purpose**: Provide a language for constructing and composing morphisms between logics.

**Key Topics**:
1. Extension morphisms: Axiom and rule addition
2. Interpretation morphisms: Embeddings and translations
3. Composition: Sequential and parallel
4. Identity morphisms: Logic self-embedding
5. Morphism verification: Type checking and properties

**Connection to Catty**: Morphisms are first-class citizens. Users construct and verify morphisms programmatically.

---

## Part VI: Formalization — RDF/OWL Ontologies

### VI.A: The Catty Categorical Schema

**Purpose**: Present the RDF/OWL schema formalizing category theory for logics.

**Key Topics**:
1. Core classes: Category, Object, Morphism, Functor
2. Logic-specific classes: Logic, LogicalSignature, Axiom
3. Structural rule representation: LHS/RHS configurations
4. Property hierarchies: Extension, Interpretation, Adjunction
5. SHACL constraints: Validating the ontology

**Source Material**: `catty-categorical-schema.jsonld`, `catty-shapes.ttl`

**Connection to Catty**: The schema is machine-readable categorical logic. It enables automated reasoning about the lattice.

---

### VI.B: Logics as Objects — The Knowledge Graph

**Purpose**: Present the instance data for specific logics in the Catty lattice.

**Key Topics**:
1. LM instance: Minimal logic
2. LJ, LDJ instances: Intuitionistic and dual intuitionistic
3. LK instance: Classical logic
4. LL, ALL, RLL instances: Substructural logics
5. Morphism instances: Extension and interpretation links

**Source Material**: `logics-as-objects.jsonld`

**Connection to Catty**: The knowledge graph is a *database* of logics. SPARQL queries retrieve lattice properties.

---

### VI.C: The Morphism Catalog

**Purpose**: Catalog all morphisms between logics in the Catty lattice.

**Key Topics**:
1. Extension morphisms: LM → LJ, LM → LDJ, LJ → LK, LDJ → LK
2. Substructural morphisms: LL → ALL, LL → RLL, ALL/RLL → LK
3. Interpretation morphisms: LK → LJ via double-negation
4. Adjoint pairs: LK ↔ LJ
5. Morphism properties: Monomorphism, epimorphism, isomorphism

**Source Material**: `morphism-catalog.jsonld`

**Connection to Catty**: The morphism catalog is *complete*—all relationships in the lattice are enumerated and formalized.

---

### VI.D: Validation and Query Framework

**Purpose**: Present the SHACL validation and SPARQL query framework for Catty.

**Key Topics**1. SHACL shapes: Lattice order constraints
2. Morphism: Preservation of structure validity
3. SPARQL queries: Retrieving lattice properties
4. Consistency checking: No contradictions in the ontology
5. Benchmark queries: Performance evaluation

**Source Material**: `catty-thesis-shapes.shacl`, `benchmarks/`

**Connection to Catty**: Validation proves the ontology is *correct*. Queries prove the lattice is *queryable*.

---

## Part VII: Integration — Connecting to External Systems

### VII.A: Semantic Web Integration

**Purpose**: Show how Catty connects to broader knowledge systems.

**Key Topics**:
1. Wikidata integration: DBPedia and Wikidata entities
2. OpenMath: Mathematical notation standards
3. nLab: Community knowledge base
4. Cross-ontology links: Shared concepts
5. URI schemes and linked data principles

**Source Material**: `external-ontologies.md`, `citation-usage.jsonld`

**Connection to Catty**: Catty is not isolated—it participates in the semantic web of mathematical knowledge.

---

### VII.B: Proof Assistant Interoperability

**Purpose**: Connect Catty to Coq, Lean, and other proof assistants.

**Key Topics**:
1. Coq's Calculus of Constructions and categorical models
2. Lean's mathematics library and category theory
3. Translation from Catty DSL to proof assistant code
4. Importing proofs: From Catty to Coq/Lean
5. Exporting definitions: From proof assistants to Catty

**Source Material**: Lean MathLib (Apache 2.0), Coq (CeCILL-B)

**Connection to Catty**: Proof assistants are *implementations* of Curry-Howard. Catty provides the categorical framework for understanding them.

---

## Part VIII: Philosophy — What Catty Means

### VIII.A: Logical Pluralism

**Purpose**: Articulate the philosophical thesis that logics are coordinate, not hierarchical.

**Key Topics**:
1. Against logical imperialism: No "one true logic"
2. Tradeoffs, not errors: Each logic has different strengths
3. The collapse problem: When distinctions are lost
4. Pluralism vs. relativism: Objectivity across logics
5. Implications for AI: Which logic for which task?

**Connection to Catty**: The lattice *is* logical pluralism formalized. Each point is equally valid.

---

### VIII.B: The Inversion Principle

**Purpose**: Articulate the central insight: syntactic minimality enables semantic power.

**Key Topics**:
1. Initial objects are powerful, not primitive
2. Classical logic as terminal, not superior
3. Minimal axioms, maximal expressivity
4. The inversion: Extensions are not improvements
5. Practical implications: Choose the minimal logic for the task

**Connection to Catty**: The inversion principle is Catty's *central contribution*. It reorients how we think about logic relationships.

---

### VIII.C: Proof as Witness, Not Description

**Purpose**: Articulate that the thesis's proofs are computational, not merely descriptive.

**Key Topics**:
1. Beyond documentation: The DSL is the proof
2. Executable mathematics: Proofs that run
3. Witness vs. testimony: Constructive vs. assertoric proof
4. The Curry-Howard thesis revisited: Proofs are programs
5. Implications: Formal verification of the thesis itself

**Connection to Catty**: Catty's DSL, RDF ontologies, and commutative diagrams are *witnesses* that prove the thesis claims.

---

## Part IX: Appendices — Reference Material

### IX.A: Citation Registry

**Purpose**: Complete bibliography of all sources cited in Catty.

**Key Topics**:
1. Primary sources: Girard, Gentzen, Lambek, Howard
2. Secondary literature: Restall, Sambin, Negri
3. Philosophy: Popper, Nelson, Priest
4. Programming: Pierce, Rust documentation
5. Semantic web: W3C standards, Wikidata

**Source Material**: `bibliography/citations.yaml`

---

### IX.B: Complete DSL Reference

**Purpose**: Full reference manual for the Catty DSL.

**Key Topics**:
1. Syntax overview
2. Grammar specification
3. Built-in logics
4. User-defined logics
5. Morphism construction
6. Examples

**Connection to Catty**: The DSL reference enables users to extend Catty's lattice with new logics.

---

### IX.C: SPARQL Query Reference

**Purpose**: Complete SPARQL query manual for the Catty knowledge graph.

**Key Topics**:
1. Basic queries: Retrieving logics
2. Morphism queries: Finding extensions
3. Lattice queries: Navigating the structure
4. Validation queries: Checking consistency
5. Performance benchmarks

**Source Material**: `ontology/queries/`

---

### IX.D: SHACL Shape Reference

**Purpose**: Complete reference for SHACL shapes validating the Catty ontology.

**Key Topics**:
1. Node shapes: Logic instances
2. Property shapes: Morphism properties
3. Constraint components: Cardinality, logical operators
4. Validation reports: Interpreting results
5. Custom shapes: Extending validation

**Source Material**: `catty-shapes.ttl`, `catty-thesis-shapes.shacl`

---

## Balance Summary

| Part | Theory | Code/DSL | Purpose |
|------|--------|----------|---------|
| I: Genesis | 70% | 30% | Foundational influences |
| II: Architecture | 80% | 20% | Lattice structure |
| III: Morphisms | 75% | 25% | Relationships between logics |
| IV: Universes | 60% | 40% | Computational substrates |
| V: Witness | 40% | 60% | DSL and proofs-as-programs |
| VI: Formalization | 30% | 70% | RDF/OWL ontologies |
| VII: Integration | 50% | 50% | External systems |
| VIII: Philosophy | 90% | 10% | Conceptual contribution |
| IX: Appendices | 20% | 80% | Reference material |

**Overall Balance**: ~55% Theory, ~45% Code/DSL

The balance is intentionally distributed to ensure that the thesis's *computational witness*—the DSL, ontologies, and executable proofs—carries equal weight with the theoretical contribution. This reflects Catty's core conviction: proofs are programs, and the thesis exists as code, not merely as text.

---

## Connection Map: Foundations to Implementation

| Foundation | Catty Component | How It Connects |
|------------|-----------------|-----------------|
| Linear Logic | Part IV, V | LL is initial object; DSL implements linear types |
| Sequent Calculi | Part II | Structural rules are lattice coordinates |
| Intuitionistic Logic | Part II, IV | LJ as constructive universe; witnesses as proofs |
| Popper/Nelson | Part II, IV | Refutation as dual extension; LNC as coordinate |
| Curry-Howard-Lambek | Part V, VI | DSL proofs are programs; ontology formalizes correspondence |
| Paraconsistent | Part II, IV | Explosion as optional axiom; non-trivial contradiction |
| Quantum Logic | Part II | Non-distributive sublattices; Zizzi's influence |
| Category Theory | All parts | Metalanguage for entire thesis |

---

## Notes on Open-Ended Structure

This outline is designed to be **open-ended** rather than climactic:

1. **No Part I–IX hierarchy**: Each part contributes equally to the thesis's argument
2. **No "conclusion" that summarizes**: The final part (Philosophy) is a contribution, not a summary
3. **Appendices are substantial**: The DSL reference and SPARQL queries are primary artifacts
4. **Theory and code intermix**: No "methods then results" separation
5. **The lattice is everywhere**: The 2D structure recurs in every part, not just Part II

The thesis's structure *mirrors* its content: just as the lattice has no "best" logic, this outline has no "most important" part. Each part is a coordinate in the thesis-space, and together they compose into the full picture.
