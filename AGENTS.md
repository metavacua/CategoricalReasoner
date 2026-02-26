# AGENTS.md - Catty Thesis Repository

## Scope

This repository attempts to implement the Categorical Reasoner "Catty" thesis: categorical foundations for morphisms between logics rather than morphisms within logics; there are expected reflections from the category of subclassical sequent calculi into sequent calculi as categories or internal logics of categories, but the repository fundamentally concerns the category of subclassical sequent calculi. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, bash scripts, or curl during development. Semantic HTML documentation collects the semantic web data and standard ontologies published online and other relevant information for software development, formal methods, engineering practices, category theory, Java development, meta-linguistics, meta-mathematical theory development, computational theories, sequent calculi, strong normalization, canonical methods, formal verification, W3C standards, OMG DOL standards, ISO COLORE standards, open science, open source software and hardware and firmware, and more.

## Core Constraints

- **Presumption of Inconsistency**: The repository's present and past state is assumed to be corrupt, non-functional, partially implemented, or otherwise formally incorrect such that strict preservation of previous conflicting information will predictably preserve broken or incorrect behaviors. Local finite consistency must be proven, and global consistency requires the highest standards of proof with relative consistency proofs being the expected norm.
- **Standards Collection**: The docs directory should have and collect relevant standards in a standards subdirectory for the repository and the development of its projects and derivatives.
- **Language Constraint**: W3C compliant HTML5 for documentation and Java (primary ecosystem for validation and transformation) for code with mathematically canonical forms prioritized; tex and markup is to be derived by qualified deterministic and formally verified standard compliant tools. There is a strict separation between developer tooling and tools that should be committed to the repository; python scripts, bash scripts, curl calls, and similar can be used for development but should not be committed in general to the repository.
- **License Constraint**: The repository should conform to and utilize https://reuse.software/dev/ for the dual licensing; where AGPLv3 and CC BY-SA v4 international conflict and both have standing, AGPLv3 is the more restrictive license so it prevails at the intersection.
- **Report Constraint**: Reports must be returned in semantic XHTML conformant to C14N or whatever format any tools use, and all documentation is to be contained in the docs directory with the sole exception being special files permitted for Github (e.g. README.md) and for specific tools like Maven.
- **Citation Constraint**: The documentation system, citation system, and citations must conform to relevant academic and industrial standards for open science, open source, and international learned society practices; OAIS (ISO 14721:2025) is a minimal standards compliance.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit, KeY).
- **Semantic Web Data**: Consumed from external sources. Any and all local authoring of semantic linked data should be handled via the docs to src transformation, and local semantic web ontologies should be handled strictly within Java, Jena, and Openllet.
- **Domain Restriction**: Do not use http://catty.org/. The only associated webpage is the MetaVacua GitHub repository (https://github.com/metavacua/CategoricalReasoner). Any script or artifact using catty.org is invalid; for testing and development purposes any locally authored semantic web technologies that need a namespace should use URNs.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints. Evidence must be returned as valid and relevant format. No faking or internal generation of results.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like lstlisting) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Extraction Protocol**: Follow the discovery and verification patterns for external QIDs and URIs. Document all difficulties and issues encountered during extraction.

## Semantic Canonicalization
Canonical N-Quads	RDFC-1.0	Highest (Unique for isomorphic graphs)
Expanded JSON-LD (Sorted)	JSON-LD 1.1	High (If key/array sorting is enforced)
Canonical XML (C14N)	XML C14N	Absolute (For literals and XML-subtrees)

## Validation

- **Standard Tools**: Standard validation tools must always be used; critically, compilation processes for Java are considered restricted validators; minimization of DIY, roll your own, re-inventing the wheel, and non-standard implementations of validators is required.
- **KeY Validation**: KeY is the correct technology for Java; the first major morphism to define is the transformation from semantic HTML web pages to Java and specifically Java records via Java documentation annotations that conform to KeY, Openllet, and Jena.
- **Annotation Processing**: The automation of morphisms should leverage the Java Compiler API (javax.tools) and Annotation Processing (JSR 269). By treating Semantic HTML as the source of truth, the JDK can automate the construction of records where the proof terms are embedded during the compilation phase. The usage of abstract classes and interfaces as well as the canonical constructors for Java Records is expected to be a basic application of the technologies.

## See Also

- `docs/AGENTS.md` - Documentation Directory
- `src/AGENTS.md` - Source Code Directory

