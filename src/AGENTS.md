# AGENTS.md - Source Code Directory

## Scope

This file governs all materials under the `src/` directory. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Presumption of Inconsistency**: The repository's present and past state is assumed to be corrupt, non-functional, partially implemented, or otherwise formally incorrect such that strict preservation of previous conflicting information will predictably preserve broken or incorrect behaviors. Local finite consistency must be proven, and global consistency requires the highest standards of proof with relative consistency proofs being the expected norm.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit, KeY).
- **Semantic Web Data**: Consumed from external sources. Any and all local authoring of semantic linked data should be handled via the docs to src transformation, and local semantic web ontologies should be handled strictly within Java, Jena, and Openllet.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints using standard SPARQL query tools. Evidence must be returned as valid and relevant format; the non-empty return requirements constitute a formal relevance condition. No faking or internal generation of results.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like lstlisting) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Standard Tools**: Standard validation tools must always be used; critically, compilation processes for Java are considered restricted validators; minimization of DIY, roll your own, re-inventing the wheel, and non-standard implementations of validators is required.
- **KeY Validation**: KeY is the correct technology for Java; the first major morphism to define is the transformation from semantic HTML web pages to Java and specifically Java records via Java documentation annotations that conform to KeY, Openllet, and Jena.
- **Annotation Processing**: The automation of morphisms should leverage the Java Compiler API (javax.tools) and Annotation Processing (JSR 269). By treating Semantic HTML as the source of truth, the JDK can automate the construction of records where the proof terms are embedded during the compilation phase. The usage of abstract classes and interfaces as well as the canonical constructors for Java Records is expected to be a basic application of the technologies.

## Subdirectories

- `src/benchmarks/` - Benchmarks Directory
- `src/scripts/` - Scripts Directory
- `src/tests/` - Tests Directory

## Licensing

All contents of this directory are licensed under AGPL-3.0-or-later.html.

## See Also

- `AGENTS.md` (root) - Core repository constraints

