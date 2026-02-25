# AGENTS.md - Testing Framework

## Scope

This file governs all materials under the `src/tests/` directory. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Presumption of inconsistency**: Tests must verify local finite consistency
- **Technology Stack**: Java ecosystem (JUnit) for testing
- **Validation**: Tests serve as validation; use standard testing frameworks
- **SPARQL Execution**: Test queries must return error-free non-empty results (timeouts over 60s are failures)
- **Query Quality**: Well-formed queries must return valid results

## Testing Standards

- Tests should be spec-driven and test-driven
- All tests must produce reproducible results
- Test evidence must be documented

## Licensing

All contents of the `src/tests/` directory are licensed under the GNU Affero General Public License v3.0 (AGPLv3).

## See Also

- `AGENTS.md` (root) - Core repository constraints
- `src/AGENTS.md` - Source code constraints
