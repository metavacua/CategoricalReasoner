# Changelog

All notable changes to the CategoricalReasoner repository will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Removed

- **BREAKING**: Eliminated `docs/dissertation/bibliography/citations.yaml` citation registry system
- **BREAKING**: Removed all references to citations.yaml from documentation
- Deleted `DOCUMENTATION_REORGANIZATION_SUMMARY.md` (root-level policy violation)

### Changed

- Added CI quality gates with changelog enforcement, TeX validation, LaTeX build optimizations, SPARQL benchmarks, CodeQL scanning, workflow validation hooks, and self-review automation
- Added SPARQL benchmark timeouts and non-empty result enforcement
- Added inclusion tracing and preamble-only command checks to the TeX structure validator
- Updated GitHub Pages deployment to build from docs/dissertation, run build-and-deploy and deploy-pages on pull requests, and deploy on main/manual or candidate-* tags
- Added fallback definition for linear logic `\parr`, moved `xcolor` to the dissertation preamble, corrected a dissertation table column count, escaped `content\_spec`, replaced emoji checkmarks in the development cycle chapter, and added bookshelf HTML output for dissertation and structural rules
- Added `docs/quality-gate-investigation.tex` documenting quality-gate decisions
- Updated `AGENTS.md` (root) to reflect citation system under development
- Updated `docs/dissertation/AGENTS.md` to remove citations.yaml references
- Updated `docs/dissertation/README.md` to remove citations.yaml validation commands
- Updated `docs/dissertation/bibliography/AGENTS.md` to reflect pending Java implementation
- Updated `docs/dissertation/macros/citations.tex` to remove registry enforcement comments
- Updated `src/schema/AGENTS.md` to remove citations.yaml constraints
- Updated `src/schema/README.md` to document citation system as under development
- Updated `src/tests/README.md` to mark citation tests as temporarily disabled
- Disabled `validate_citations.py` with early exit and documentation of missing components
- Disabled `validate_consistency.py` with early exit and documentation of missing components

### Added

- Created `docs/dissertation/bibliography/README.md` documenting missing Java/RO-Crate implementation requirements
- Created `CHANGELOG.md` following Keep a Changelog + SemVer standards
- Documented Javadoc annotation requirements for Java citation records
- Documented OpenJDK 21+ setup issues (February 2026)

### Documentation

The YAML-based citation system was non-functional:
- Required manual YAML editing without validation
- No enforcement of citation key uniqueness
- No external link validation
- No integration with TeX compilation workflow
- No support for agent query interfaces (ARATU)
- Violated project architecture (YAML vs Java ecosystem preference)

### Implementation Requirements

The citation system requires Java/RO-Crate implementation with the following components:

**Missing Components:**
- Maven `pom.xml` build configuration with required dependencies
- Java source files in `src/main/java/org/metavacua/categoricalreasoner/citation/`
- RO-Crate 1.1 JSON-LD generation (`RoCrateGenerator.java`)
- BibLaTeX export functionality (`BiblatexExporter.java`)
- SPARQL federation support (`SparqlQueryService.java`)
- Javadoc annotations for Citation, Person, and related records

**Dependencies:**
- edu.kit.datamanager:ro-crate-java:2.1.0
- io.github.xmlobjects:edtf-model:2.0.0
- org.apache.jena:jena-arq:4.9.0
- org.junit.jupiter:junit-jupiter:5.10.0 (testing)

See `docs/dissertation/bibliography/README.md` for detailed implementation requirements.

### Test Results

**Validator Test Results (February 2026):**

The old `validate_citations.py` script had the following issues:
- Failed to load `src/ontology/citations.jsonld` (file never existed)
- Attempted to validate against non-existent RDF citation graph
- No integration with actual TeX compilation workflow
- Python dependencies required but not documented (PyYAML, rdflib)

The validator has been disabled and will be rewritten when the Java/RO-Crate system is implemented.

## [0.1.0] - 2024-02-XX

### Added

- Initial repository structure for Catty thesis project
- LaTeX thesis framework in `docs/dissertation/`
- Schema validation infrastructure in `src/schema/`
- Citation registry (YAML-based - later removed)
- AGENTS.md files for various directories
- Python validation scripts (temporary, to be replaced with Java)

### Security Notes

- Domain restriction enforced: No use of `http://catty.org/`
- Only associated webpage: `https://github.com/metavacua/CategoricalReasoner`
- Semantic web data consumed from external sources only (SPARQL endpoints, linked data, GGG)

### Architecture Notes

- Primary artifact: LaTeX thesis (TeX primary, RDF secondary for provenance only)
- Technology stack: Java (Jena, OpenLlet, JavaPoet, JUnit) for validation/transformation
- Python scripts: Auxiliary CI/CD orchestration only (pragmatic approach)
- No bidirectional TeX ↔ RDF consistency required
- RDF is for metadata/provenance extraction only (unidirectional: TeX → RDF)

---

## Versioning Guidelines

This project uses [Semantic Versioning](https://semver.org/):

- **MAJOR**: Incompatible API changes
- **MINOR**: Backwards-compatible functionality additions
- **PATCH**: Backwards-compatible bug fixes

### Breaking Changes

Breaking changes will be noted in the changelog with `[BREAKING]` prefix.

### Examples

**Breaking change example:**
> **[BREAKING]** Eliminated `docs/dissertation/bibliography/citations.yaml` citation registry system

**Feature addition example:**
> Added RO-Crate 1.1 JSON-LD generation for citation management

**Bug fix example:**
> Fixed validator to correctly handle non-ASCII characters in citation titles

---

## Links

- [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
- [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
- [How to Unrelease](https://keepachangelog.com/en/1.0.0/#how)
- [Catty Thesis Repository](https://github.com/metavacua/CategoricalReasoner)
