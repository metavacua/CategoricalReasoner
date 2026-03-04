SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: CC-BY-SA-4.0

# AGENTS.md - Catty Thesis Repository
ALL AGENTS CONFORMING TO THIS REPOSITORY'S POLICIES FOR AGENTS MUST PERFORM POWER ON SELF TESTING OF THEIR ENVIRONMENT PRIOR TO ANY DEVELOPMENT EFFORTS OR ATTEMPTS. POWER ON SELF TESTING MEANS USING ANY AND ALL TERMINAL TOOLS TO DISCERN WHAT YOUR DEVELOPMENT ENVIRONMENT HAS IN THE WAY OF TOOLING AND OTHERWISE ORIENTING TO YOUR ENVIRONMENT; THIS FILE IS NOT FOR HUMANS. THE FOLLOWING RESTRICTIONS APPLY THE BEHAVIORS OF THE MACHINE AGENTS RATHER THAN ANY PARTICULAR TOOL OR HUMAN PR.

## Documentation

### Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE NOW FORMALLY DEFINED IN THE FOLLOWING DOCUMENTS:
- **Formal Model:** [`docs/formal-document-policy.html`](docs/formal-document-policy.html) - Mathematical definitions, category-theoretic model, and licensing algebra
- **Implementation Guide:** [`docs/document-policy-implementation.html`](docs/document-policy-implementation.html) - Operational rules, validation procedures, and examples

### Core Policy Principles
1. **Golden Rule:** With the sole exception of GitHub special files, ALL documentation must reside in the `docs/` directory
2. **Licensing Separation:** Documentation in `docs/` is CC BY-SA v4.0; software in `src/` is AGPL v3.0
3. **No Arbitrary LLM Files in Root:** All AI-generated content must be curated and placed in appropriate subdirectories
4. **Special Files Only at Root:** Only GitHub-defined special files and tooling configuration may exist at repository root

### Special Files (Permitted at Repository Root)
- `README.md` - GitHub repository landing page
- `LICENSE` - License text (AGPL v3.0)
- `AGENTS.md` - Machine agent policies (this file)
- `CONTRIBUTING.md` - Contribution guidelines
- `CHANGELOG.md` - Version history
- `.gitignore` - Git exclusion patterns
- `pom.xml` - Maven build descriptor (if applicable)
- `.reuse/dep5` - REUSE specification compliance

### Documentation Structure
- `docs/` - All documentation (CC BY-SA v4.0)
  - `standards/` - Collected standards and specifications
  - `dissertation/` - Thesis materials
  - `structural-rules/` - Sequent calculus documentation
  - `formal-document-policy.html` - Formal policy model
  - `document-policy-implementation.html` - Implementation guide

### AGENTS.md Standard
AGENTS.md is required to be in standard format with the exact filename; it is not an arbitrary file and must conform to the [AGENTS.md standards](https://github.com/agentmd/agent.md). Each directory and subdirectory can have an AGENTS.md, and the root for this repository should have an AGENTS.md.

### Forbidden in Repository Root
- Arbitrary LLM-generated markdown files
- Documentation not required by GitHub or tooling
- Source code files (.java, .class, .jar)
- Test files or benchmarks

## Scope

This repository attempts to implement the Categorical Reasoner "Catty" thesis: categorical foundations for morphisms between logics rather than morphisms within logics; there are expected reflections from the category of subclassical sequent calculi into sequent calculi as categories or internal logics of categories, but the repository fundamentally concerns the category of subclassical sequent calculi. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, bash scripts, or curl during development. Semantic HTML documentation collects the semantic web data and standard ontologies published online and other relevant information for software development, formal methods, engineering practices, category theory, Java development, meta-linguistics, meta-mathematical theory development, computational theories, sequent calculi, strong normalization, canonical methods, formal verification, W3C standards, OMG DOL standards, ISO COLORE standards, open science, open source software and hardware and firmware, and more.

## Core Constraints

- **Presumption of Inconsistency**: The repository's present and past state is assumed to be corrupt, non-functional, partially implemented, or otherwise formally incorrect such that strict preservation of previous conflicting information will predictably preserve broken or incorrect behaviors. Local finite consistency must be proven, and global consistency requires the highest standards of proof with relative consistency proofs being the expected norm. This shall conform to Light Linear Logic, Logics of Formal Inconsistency, and Logics of Formal Undeterminedness semantics for subclassical calculi and first order theories.
- **Standards Collection**: The docs directory should have and collect relevant standards in a standards subdirectory for the repository and the development of its projects and derivatives; e.g. CCSDS 652.0-M-1, [RDFC 1.0](https://www.w3.org/TR/rdf-canon/), RFC 2119.
- **Language Constraint**: W3C compliant HTML5 for documentation in the docs directory and Java (primary ecosystem for validation and transformation) for code with mathematically canonical forms prioritized; all agents are required to determine the list of special files allowable in the root based strictly on Github defined special files and special standard files for tooling such as pre-commit, AGENTS.md, CHANGELOG, Maven, and similar tooling; the primary restriction is that no arbitrary LLM generated files are permitted in the root. There is a strict separation between developer tooling and tools that should be committed to the repository; python scripts, bash scripts, curl calls, and similar can be used for development but should not be committed in general to the repository.
- **License Constraint**: The repository shall conform to and utilize https://reuse.software/dev/ for the dual licensing based on the strict transformation of CC BY-SA v4 international media into AGPL v3 media. The root license is creative commons BY-SA v4 international license; the CategoricalReasoner repository is the intellectual property of Ian Douglas Lawrence Norman McLean and classified as a schema.org Creative Work and specifically a Collection under that semantic ontology.
- **Report Constraint**: Reports must be returned in semantic XHTML conformant to C14N or whatever format any tools use, and all documentation is to be contained in the docs directory with the sole exception being special files permitted for Github (e.g. README.md) and for specific tools like Maven.
- **Citation Constraint**: The documentation system, citation system, and citations must conform to relevant academic and industrial standards for open science, open source, and international learned society practices; OAIS (ISO 14721:2025) and (ISO 16363:2025) are minimal required standards compliance.
- **Technology Stack**: Core validation and transformation shall use Java 25 ecosystem (Jena, OpenLlet, JavaPoet, JUnit, KeY).
- **Semantic Web Data**: Consumed from external sources. Any and all local authoring of semantic linked data should be handled via the docs to src transformation, and local semantic web ontologies should be handled strictly within Java, Jena, and Openllet.
- **Domain Restriction**: Do not use http://catty.org/. The only associated webpage is the MetaVacua GitHub repository (https://github.com/metavacua/CategoricalReasoner). Any other script or artifact using catty.org is invalid.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints using standard SPARQL query tools. Evidence must be returned as valid and relevant format; the non-empty return requirements constitute a formal relevance condition. No faking or internal generation of results.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures indicating refutation of some number of assumptions or hypotheses in the formation of the query within reasonable resource bounds.
- **Debugging Protocol**: Document all difficulties and issues encountered during development.

## Standards and Norms
Maven projects with Java 25 open JDK including the full Java API documentation.

## Workflows
Semantic Versioning 2.0.0 (SemVer)
Keep a Changelog 1.1.0
Conventional Commits 1.0.0
pre-commit framework
RDFC 1.0 (RDF Dataset Canonicalization)
REUSE Software Specification 3.0
SLSA (Supply-chain Levels for Software Artifacts)
CodeMeta
Argument Ontology (ARGO)

## Provenance (PROV-O) and Evidence Graph
Implementation: Every time an agent runs a SPARQL query or a KeY validation or any non-no-op development activity, it must generate a PROV-O record. This record should link the Activity (The Query/Validation) to the Agent (The LLM) and the Entity (The Resulting Documentation/Code). Without this, the "academic quality" is unverifiable.

## Research Object Crate
Oxford Common File Layout
Bagit

## Validation

- **Standard Tools**: Standard validation tools must always be used; critically, compilation processes for Java are considered restricted validators; minimization of DIY, roll your own, re-inventing the wheel, and non-standard implementations of validators is required.
- **KeY Validation**: KeY is the correct technology for Java; the first major morphism to define is the transformation from semantic HTML web pages to Java and specifically Java records via Java documentation annotations that conform to KeY, Openllet, and Jena.
- **Annotation Processing**: The automation of morphisms should leverage the Java Compiler API (javax.tools) and Annotation Processing (JSR 269). By treating Semantic HTML as the source of truth, the JDK can automate the construction of records where the proof terms are embedded during the compilation phase. The usage of abstract classes and interfaces as well as the canonical constructors for Java Records is expected to be a basic application of the technologies.

## See Also

- **Formal Policy Documents:**
  - [Formal Document Policy](docs/formal-document-policy.html) - Category-theoretic model for copyrightable works, documentation, and special files
  - [Implementation Guide](docs/document-policy-implementation.html) - Operational rules and validation procedures

- **Directory Policies:**
  - `docs/AGENTS.md` - Documentation Directory
  - `src/AGENTS.md` - Source Code Directory

- **Standards References:**
  - [REUSE Software Specification](https://reuse.software/)
  - [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)
  - [AGPL v3.0](https://www.gnu.org/licenses/agpl-3.0.html)
  - [AGENTS.md Standard](https://github.com/agentmd/agent.md)

