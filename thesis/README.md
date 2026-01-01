# Thesis: The Logic-Witness Formalism

This directory contains the LaTeX source for the white paper "The Logic-Witness Formalism: A Categorical Framework for Formal Logics."

## Structure

```
thesis/
├── main.tex                    # Main document (includes all chapters)
├── preamble.tex                # Document preamble (packages, commands)
├── Makefile                    # Build automation
├── README.md                   # This file
├── frontmatter/                # Front matter files
│   ├── titlepage.tex
│   ├── abstract.tex
│   └── acknowledgments.tex
├── chapters/                   # Chapter files
│   ├── introduction.tex
│   ├── chapter2-logical-formalism.tex
│   ├── chapter3-theoretical-formalism.tex
│   ├── chapter4-witness-formalism.tex
│   ├── chapter5-semantic-web.tex
│   ├── chapter6-roadmap.tex
│   └── conclusions.tex
└── backmatter/                 # Back matter files
    └── bibliography.bib        # BibTeX references
```

## Building the PDF

### Requirements

- A LaTeX distribution (TeX Live, MiKTeX, or MacTeX)
- `make` utility (optional, for automated builds)

### Build Commands

**Using Make (recommended):**

```bash
cd thesis
make
```

This will:
1. Run `pdflatex` to generate the PDF
2. Run `bibtex` to process citations
3. Run `pdflatex` twice more to resolve cross-references

**Manual build:**

```bash
cd thesis
pdflatex main.tex
bibtex main
pdflatex main.tex
pdflatex main.tex
```

**Clean build artifacts:**

```bash
make clean
```

### Output

The generated PDF is `main.pdf` in the `thesis/` directory.

## Content Overview

### Part I: Metamathematical Formalism

- **Chapter 1 (Introduction)**: Motivation, two-dimensional structure, physical grounding, scope
- **Chapter 2 (Logical Formalism)**: Sequent calculi, two-dimensional lattice, logical signatures, physical instantiation
- **Chapter 3 (Theoretical Formalism)**: Tarski-Mostowski-Robinson framework, theory transformations, Robinson arithmetic
- **Chapter 4 (Witness Formalism)**: Curry-Howard correspondence, witness functor, algorithmic equivalence vs. behavioral differences

### Part II: Technical Implementation

- **Chapter 5 (Semantic Web)**: RDF/OWL specifications, ontology architecture, SHACL validation, SPARQL queries, DBpedia alignment
- **Chapter 6 (Technical Roadmap)**: Development phases, implementation strategies, mechanized verification integration, long-term vision

### Appendices

- **Appendix A**: External ontologies and vocabularies (see `../ontology/external-ontologies.md`)
- **Appendix B**: SPARQL query examples (see `../ontology/queries/sparql-examples.md`)
- **Appendix C**: License and attribution

## Incremental Development

This thesis follows an **issue-driven development model**:

1. Core structure and abstract formalism are established in this initial version
2. Detailed proofs, examples, and formalizations are tracked as GitHub issues
3. Each issue addresses a specific vacancy (theorem proof, chapter expansion, ontology extension)
4. Contributions are validated (LaTeX builds, RDF parses, tests pass) before merging

See `CONTRIBUTING.md` in the repository root for contribution guidelines.

## Identified Vacancies

The following are explicitly marked as future work:

- Formal proofs of meta-theorems (fragment containment, diamond commutativity, etc.)
- Complete specification of all morphisms between logical families
- Detailed commutative diagrams for all structural rule configurations
- Natural transformations and categorical limits/colimits
- Decidability proofs for monotonic Robinson and other variants
- Mechanized verification in Isabelle, Coq, or Lean
- Witness functor implementation (code generation from RDF)
- Quantum circuit synthesis for linear logic specifications

## LaTeX Dependencies

The thesis uses the following LaTeX packages:

- `amsmath`, `amssymb` (mathematical notation)
- `tikz` (diagrams)
- `listings` (code listings)
- `hyperref` (hyperlinks, PDF metadata)
- `booktabs` (tables)

All packages are standard and included in modern LaTeX distributions.

## Deployment

The PDF is automatically built and deployed to GitHub Pages via GitHub Actions. See `.github/workflows/deploy.yml` for the deployment workflow.

**Public URL** (after deployment): `https://<owner>.github.io/Catty/`

## License

This work is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)** or later. See `../LICENSE` for full license text.

External resources (DBpedia, Wikidata, OpenMath, nLab, etc.) have varying licenses; all are compatible with AGPL-3.0. See Appendix C in the thesis for details.

## Contact

For questions, issues, or contributions, please:

- Open a GitHub issue in the repository
- Follow the contribution guidelines in `CONTRIBUTING.md`
- Consult the project README at `../README.md`
