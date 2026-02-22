# AGENTS.md - Catty Thesis Repository

## Scope
This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents must generate thesis content and validation artifacts. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally.

## Core Constraints
- **Formats**: Read/write `*.md` (primary), `*.tex`, `*.yaml`, `*.py`. Create directories only when specified.
- **Languages**: LaTeX, Python ≥3.8 (auxiliary CI/CD only), Java (primary ecosystem for validation and transformation).
- **Reports**: Reports must be returned as `.tex` files (multi-part for non-trivial) or semantic HTML. `SEMANTIC_WEB_RAG_REPORT.md` is strictly forbidden and must be converted to TeX.
- **Citations**: Citation system is under development. Direct citation key validation is not currently enforced. See `docs/dissertation/bibliography/README.md` for Java/RO-Crate system requirements.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Validation**: All artifacts must pass automated validation. Acceptance criteria are boolean tests only.
- **Execution**: Execute task descriptions and validate outputs.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit). Python scripts are auxiliary for CI/CD orchestration only.
- **Semantic Web Data**: Consumed from external sources. Do not author local RDF schemas or instantiate ontology classes.
- **Domain Restriction**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`). Any script or artifact using `catty.org` is invalid.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints. Evidence must be returned as valid TTL. No faking or internal generation of results.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like `lstlisting`) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Extraction Protocol**: Follow the discovery and verification patterns for external QIDs and URIs. Document all difficulties and issues encountered during extraction.

## Directory Structure

### docs/dissertation/
**Thesis**: "Theoretical Metalinguistics"
- Primary scholarly work
- Contains chapters, bibliography, theorems
- Compiled via Quarto

### docs/structural-rules/
**Monograph**: "Structural Rules: A Categorical Investigation"
- Separate scholarly work on structural rules
- Parts: weakening, contraction, exchange
- Compiled via Quarto

### docs/dev/
**Development Artifacts**
- `report/` - Validation and verification reports
- `handbook/` - Repository infrastructure documentation
- Separate from thesis and monograph

### src/
**Source Code**
- Schema validators
- Scripts and utilities
- Tests and benchmarks

## Theorem Scoping by Directory Depth

The repository implements theorem scoping where theorems at higher directory levels apply more broadly:

```
docs/dissertation/theorems/     # Root: applies to ALL thesis chapters
docs/dissertation/chapters/*/   # Chapter: applies to that chapter only
```

```
docs/structural-rules/theorems/  # Root: applies to ALL parts
docs/structural-rules/part-*/   # Part: applies to all chapters in part
docs/structural-rules/part-*/chap-*/  # Chapter: applies to that chapter only
```

**Subformula Principle**: Deeper theorems are subformulas of shallower ones. A chapter theorem may restrict or strengthen a part theorem, which may restrict or strengthen a root theorem.

## Cæsar non Supra Grammaticos
*"The Emperor is not above the grammarians"*

All constraints must have rational basis. Arbitrary fiats are not permitted. Each AGENTS.md policy should be justifiable by:
- Technical necessity (e.g., compilation requirements)
- Logical coherence (e.g., theorem scoping)
- Operational clarity (e.g., validation criteria)

## LaTeX Package Requirements
Required packages for thesis/monograph compilation:
- `mathtools` - Enhanced mathematical typesetting
- `amsthm` - Theorem environments
- `ebproof` - Proof trees and sequent calculus
- `stmaryrd` - Additional mathematical symbols
- `tikz-cd` - Commutative diagrams
- `turnstile` - Turnstile notation for sequents

## Metadata Requirements
Thesis and monograph must include:
- MSC2020 classification
- Abstract
- Keywords
- Running head (short title)

## Quarto Workflow
- Primary format: `.md` (Markdown)
- Quarto processes `.md` to `.tex` and PDF
- `.md` files are GitHub-renderable
- Quarto configuration: `_quarto.yml` per book

## RO-Crates for Research Data
Research data (SPARQL results, verification evidence) should be packaged as RO-Crates for reproducibility and citation.

## Validation
All artifacts must pass validation criteria. All criteria must evaluate true.

## See Also
- `src/schema/AGENTS.md` - Citation and ID constraints
- `docs/dissertation/AGENTS.md` - Thesis policies
- `docs/structural-rules/AGENTS.md` - Monograph policies
- `docs/dev/AGENTS.md` - Development artifacts policies
