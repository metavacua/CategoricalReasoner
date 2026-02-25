# AGENTS.md - Catty Thesis Repository

## Scope
This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents must generate LaTeX thesis content and validation artifacts. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally.

## Core Constraints
- **Presumption of inconsistency**: the repository's present and past state is assumed to be corrupt, non-functional, partially implemented, or otherwise formally incorrect such that strict preservation of previous conflicting information will predictably preserve broken or incorrect behaviors. Local finite consistency must be proven, and global consistency requires the highest standards of proof with relative consistency proofs being the expected norm.
- **Formats**: Read/write `*.md`, `*.tex`, `*.yaml`, `*.py`. Create directories only when specified; the root directory in general should not be changed as it is reserved for special files and the docs and src layout; all subdirectories should conform to those layouts or special layouts defined by standard tools or Github repository features.
- **Languages**: W3C compliant HTML5 and Java (primary ecosystem for validation and transformation); tex and markup is to be derived; the docs directory generally and documentation specifically is under the Creative Commons BY-SA v4 international license which has a strict one way conversion to the AGLPv3 license that governs the source code and programs of the repository.
- **Reports**: Reports must be returned in semantic HTML, and all documentation is to be contained in the docs directory with the sole exception being special files permitted for Github (e.g. README.md) and for specific tools like Maven.
- **Citations**: the Citation system and citations must conform to relevant academic and industrial standards for open science, open source, and international learned society practices; OAIS is a minimal standards compliance. The OAIS model (ISO 14721:2025) requires that information remains understandable to the "Designated Community" over the long term.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit).
- **Semantic Web Data**: Consumed from external sources. Any and all local authoring of semantic linked data should be handled via the docs to src transformation, and local semantic web ontologies should be handled strictly within Java, Jena, and Openllet. 
- **Domain Restriction**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`). Any script or artifact using `catty.org` is invalid; for testing and development purposes any locally authored semantic web technologies that need a namespace should use URNs; local authoring of ontologies, schema, and similar technologies should ONLY be contemplated AFTER all canonical, normative, and standard solutions have been exhausted by formal search and inquiry.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints. Evidence must be returned as valid and relevant format. No faking or internal generation of results.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like `lstlisting`) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Extraction Protocol**: Follow the discovery and verification patterns for external QIDs and URIs. Document all difficulties and issues encountered during extraction.

## Validation
Standard validation tools must always be used; critically, compilation processes for Java are considered restricted validators; minimization of DIY, roll your own, re-inventing the wheel, and non-standard implementations of validators is required. 

## See Also
- `src/schema/AGENTS.md` - Citation and ID constraints
