# AGENTS.md - Scripts and Utilities

## Scope

This file governs all materials under the `src/scripts/` directory. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Presumption of inconsistency**: Scripts must handle corrupt or invalid input gracefully
- **Formats**: Read/write `*.md`, `*.tex`, `*.yaml`, `*.py` as needed
- **Technology Stack**: Shell scripts, Python for utility operations
- **SPARQL Execution**: All queries in scripts must run against external endpoints with evidence
- **Domain Restriction**: Do not use `http://catty.org/`; use URNs for local testing

## Licensing

All contents of the `src/scripts/` directory are licensed under the GNU Affero General Public License v3.0 (AGPLv3).

## See Also

- `AGENTS.md` (root) - Core repository constraints
- `src/AGENTS.md` - Source code constraints
