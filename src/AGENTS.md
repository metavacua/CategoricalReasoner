# AGENTS.md - Source Layout

## Scope

This file governs all materials under the `src/` directory, including ontology examples, schemas, scripts, tests, and benchmarks. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Presumption of inconsistency**: Code in `src/` must prove local finite consistency
- **Technology Stack**: Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit)
- **Validation**: Standard validation tools required; KeY for formal verification
- **SPARQL Execution**: All queries must run against external endpoints with evidence
- **Semantic Web Data**: Local ontologies handled via Java, Jena, and OpenLlet

## Licensing

All contents of the `src/` directory are licensed under the GNU Affero General Public License v3.0 (AGPLv3). Any contributions or modifications within `src/` must comply with AGPLv3 terms and preserve the applicable notices.

## Subdirectories

Each subdirectory has its own `AGENTS.md` that derives from root:

- `src/benchmarks/AGENTS.md` - Benchmarks and evaluation
- `src/scripts/AGENTS.md` - Scripts and utilities
- `src/tests/AGENTS.md` - Testing framework

## See Also

- `AGENTS.md` (root) - Core repository constraints
