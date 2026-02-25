# AGENTS.md - Scripts Directory

## Scope

This file governs all materials under the `src/scripts/` directory. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Presumption of Inconsistency**: The repository's present and past state is assumed to be corrupt, non-functional, partially implemented, or otherwise formally incorrect such that strict preservation of previous conflicting information will predictably preserve broken or incorrect behaviors. Local finite consistency must be proven, and global consistency requires the highest standards of proof with relative consistency proofs being the expected norm. (Scope: Applies to all repository activities.)
- **Semantic Web Data**: Consumed from external sources. Any and all local authoring of semantic linked data should be handled via the docs to src transformation, and local semantic web ontologies should be handled strictly within Java, Jena, and Openllet. (Scope: Use URNs for local namespaces.)
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints. Evidence must be returned as valid and relevant format. No faking or internal generation of results. (Scope: Applies to all SPARQL queries.)
- **Domain Restriction**: Do not use http://catty.org/. The only associated webpage is the MetaVacua GitHub repository (https://github.com/metavacua/CategoricalReasoner). Any script or artifact using catty.org is invalid; for testing and development purposes any locally authored semantic web technologies that need a namespace should use URNs. (Scope: Local authoring of ontologies ONLY AFTER all canonical solutions exhausted.)

## Licensing

All contents of this directory are licensed under AGPL-3.0-or-later.html.

## See Also

- `AGENTS.md` - Root Directory

## Specification

This file is derived from the canonical semantic web specification:
- `docs/standards/repository-constraints.ttl` - RDF/Turtle specification

