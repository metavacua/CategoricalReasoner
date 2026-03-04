# AGENTS.md - Tests Directory

## Scope

This file governs all materials under the `src/tests/` directory. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Presumption of Inconsistency**: The repository's present and past state is assumed to be corrupt, non-functional, partially implemented, or otherwise formally incorrect such that strict preservation of previous conflicting information will predictably preserve broken or incorrect behaviors. Local finite consistency must be proven, and global consistency requires the highest standards of proof with relative consistency proofs being the expected norm.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit, KeY).
- **Standard Tools**: Standard validation tools must always be used; critically, compilation processes for Java are considered restricted validators; minimization of DIY, roll your own, re-inventing the wheel, and non-standard implementations of validators is required.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints using standard SPARQL query tools. Evidence must be returned as valid and relevant format; the non-empty return requirements constitute a formal relevance condition. No faking or internal generation of results.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.

## Licensing

All contents of this directory are licensed under AGPL-3.0-or-later.html.

## See Also

- `AGENTS.md` (root) - Core repository constraints

