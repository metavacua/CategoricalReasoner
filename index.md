---
title: "The Logic-Witness Formalism"
layout: default
---

# The Logic-Witness Formalism

A Categorical Framework for Formal Logics Grounded in Physical Realizability

---

## Overview

The **Logic-Witness Formalism** presents a rigorous metamathematical framework for modeling formal logics as objects in a **two-dimensional lattice category**. This categorical structure connects proof theory, quantum mechanics, and computational semantics.

### Core Contributions

1. **Two-Dimensional Lattice Structure**
   - **Horizontal dimension**: Sequent restrictions (classical ↔ intuitionistic ↔ dual-intuitionistic ↔ monotonic)
   - **Vertical dimension**: Structural rule presence/absence (full structural ↔ affine ↔ relevant ↔ linear ↔ non-structural)

2. **Physical Grounding**
   - Quantum-safe logics (W=0, C=0) embody **no-cloning** and **no-erasure** theorems
   - Structural rules in proof theory ↔ resource management in computation ↔ physical constraints in quantum mechanics

3. **Witness Functor**
   - Maps logic-theory pairings to executable programs or physical circuits
   - Extends Curry-Howard-Kleene-Lambek correspondence across all categorizable logics
   - Enables proof-carrying code and verified compilation

4. **Semantic Web Integration**
   - RDF/OWL ontologies for machine-readable specifications
   - SHACL validation of categorical axioms
   - DBpedia/Wikidata alignment for grounded definitions

---

## Download the White Paper

📄 **[Download PDF (main.pdf)](main.pdf)**

---

## Repository Structure

```
.
├── thesis/                      # LaTeX source for white paper
│   ├── main.tex                # Main document
│   ├── frontmatter/            # Abstract, acknowledgments
│   ├── chapters/               # 6 comprehensive chapters
│   └── backmatter/             # Bibliography
│
├── ontology/                    # RDF/OWL ontologies
│   ├── catty-categorical-schema.jsonld
│   ├── logics-as-objects.jsonld
│   ├── morphism-catalog.jsonld
│   ├── examples/               # Canonical logic instances
│   │   ├── classical-logic.ttl
│   │   ├── intuitionistic-logic.ttl
│   │   ├── dual-intuitionistic-logic.ttl
│   │   ├── monotonic-logic.ttl
│   │   └── linear-logic.ttl
│   └── external-ontologies.md  # Catalog of external resources
│
├── scripts/                     # Validation and utility scripts
└── .github/                     # CI/CD workflows and issue templates
```

---

## Key Concepts

### The Two-Dimensional Lattice

Formal logics are organized into a **lattice of lattices**:

#### Horizontal Dimension: Sequent Restrictions

For a fixed set of structural rules, logics vary by how sequents are restricted:

| Antecedent   | Succedent    | Logic                  |
|--------------|--------------|------------------------|
| Unrestricted | Unrestricted | Classical (LK)         |
| Unrestricted | Restricted   | Intuitionistic (LJ)    |
| Restricted   | Unrestricted | Dual-Intuitionistic (LDJ) |
| Restricted   | Restricted   | Monotonic              |

These four logics form a **horizontal diamond** parametrized by {LEM, LNC}.

#### Vertical Dimension: Structural Rules

Across families, structural rules vary:

- **Exchange (E)**: Order independence
- **Weakening (W)**: Add premises freely (corresponds to erasure)
- **Contraction (C)**: Merge duplicates (corresponds to cloning)

Five binary parameters: (E, W_L, W_R, C_L, C_R) ∈ {0,1}⁵ → 32 theoretical combinations, yielding logical families:

- **Full Structural**: E, W, C all present → classical family
- **Affine**: W present, C absent → allows erasure, no cloning
- **Relevant**: C present, W absent → allows cloning, no erasure
- **Linear**: E present, W=0, C=0 → **quantum-safe core**
- **Non-structural**: All absent → maximally resource-constrained

### Quantum-Safe Logics

**Linear logic** and **non-structural logics** are quantum-safe:

- **No-cloning theorem** (Wootters & Zurek, 1982): No contraction (C=0)
- **No-erasure theorem** (Pati & Braunstein, 2000): No weakening (W=0)

These logics can be directly implemented on quantum hardware.

### The Witness Functor

The witness functor W maps logic-theory pairings to executable programs:

```
W: (Logic, Theory) → ExecutableProgram
```

**Properties**:
- Constructive: witness *is* the implementation, not just a specification
- Functorial: morphisms between logics induce program transformations
- Physically realizable: quantum-safe logics → quantum circuits

---

## Case Study: Robinson Arithmetic

Robinson arithmetic (Q) serves as the canonical case study, formalized across all canonical logics:

- **Classical Robinson (LK)**: All seven axioms; essentially undecidable
- **Intuitionistic Robinson (LJ)**: Constructive; explicit witnesses required
- **Dual-Intuitionistic Robinson (LDJ)**: Co-constructive; refutations
- **Monotonic Robinson**: Axioms 2, 4, 5, 6, 7 only; positive operations
- **Linear Robinson (LL)**: Resource-aware; quantum-safe

Same theoretical signature, different logics → different computational behaviors.

---

## Applications

1. **Quantum-Safe Programming Languages**
   - Linear type systems enforce no-cloning and no-erasure
   - Verified compilation via witness functor

2. **Proof-Carrying Code**
   - Programs bundled with machine-checkable certificates
   - Critical systems: aerospace, finance, healthcare

3. **Automated Theorem Proving**
   - Morphisms induce proof translations
   - SPARQL queries identify applicable theorems

4. **Hardware Synthesis**
   - Quantum circuit generation from linear logic specifications
   - Resource-constrained system design

---

## Development Roadmap

### Phase 1: Formalize Robinson in RDF ✅
Complete RDF specifications for Robinson arithmetic across all canonical logics.

### Phase 2: Code Generator Prototype
Implement witness functor for Classical Robinson (Python + RDFLib).

### Phase 3: Extend to All Logics
Generalize code generator to intuitionistic, linear, monotonic, and dual-intuitionistic variants.

### Phase 4: Behavioral Analysis
Demonstrate algorithmic equivalence and behavioral differences.

### Phase 5: Quantum Circuit Synthesis
Generate quantum circuits from linear logic specifications.

### Phase 6: Generalization Beyond Robinson
Extend to Peano arithmetic, geometry, and arbitrary theories.

---

## Contributing

This project follows an **issue-driven development model**:

1. Identify vacancies (missing proofs, theorems, implementations)
2. Create GitHub issue with clear specification
3. Submit pull request addressing issue
4. Automated validation (LaTeX builds, RDF parses, tests pass)
5. Merge upon approval

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

### Contribution Pathways

- **Theorem Formalization**: Prove meta-theorems; mechanize in Isabelle/Coq/Lean
- **Ontology Extension**: Add logics, morphisms, or SHACL constraints
- **Code Generation**: Extend witness functor to new target languages
- **Documentation**: Expand chapters, write tutorials, create examples

---

## Resources

- **White Paper**: [main.pdf](main.pdf)
- **GitHub Repository**: [github.com/owner/Catty](https://github.com/owner/Catty)
- **Ontologies**: [ontology/](ontology/)
- **External Resources**: [ontology/external-ontologies.md](ontology/external-ontologies.md)
- **SPARQL Examples**: [ontology/queries/sparql-examples.md](ontology/queries/sparql-examples.md)

---

## External References

- **DBpedia**: [Classical Logic](http://dbpedia.org/resource/Classical_logic), [Intuitionistic Logic](http://dbpedia.org/resource/Intuitionistic_logic), [Linear Logic](http://dbpedia.org/resource/Linear_logic)
- **Wikidata**: [Q217699 (Classical logic)](https://www.wikidata.org/wiki/Q217699), [Q179899 (Intuitionistic logic)](https://www.wikidata.org/wiki/Q179899), [Q1149560 (Linear logic)](https://www.wikidata.org/wiki/Q1149560)
- **nLab**: [Category Theory](https://ncatlab.org/nlab/show/category+theory), [Linear Logic](https://ncatlab.org/nlab/show/linear+logic)

---

## License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)** or later. See [LICENSE](LICENSE) for details.

External resources (DBpedia, Wikidata, OpenMath, nLab) have compatible licenses (CC0, CC BY-SA 3.0, BSD, Apache 2.0).

---

## Citation

If you use this work in your research, please cite:

```bibtex
@misc{mclean2025logicwitness,
  author = {McLean, Ian Douglas Lawrence Norman},
  title = {The Logic-Witness Formalism: A Categorical Framework for Formal Logics},
  year = {2025},
  howpublished = {\url{https://owner.github.io/Catty/}},
  note = {White paper and RDF ontologies}
}
```

---

## Contact

For questions, feedback, or collaboration:

- **GitHub Issues**: [github.com/owner/Catty/issues](https://github.com/owner/Catty/issues)
- **Email**: (to be added)

---

<p align="center">
<em>"The map is not the territory, but a good map helps you navigate."</em><br>
— Adapted from Alfred Korzybski
</p>
