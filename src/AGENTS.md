SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: AGPL-3.0-or-later

# AGENTS.md - Source Code Directory

## Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE FORMALLY DEFINED IN:
- **[Formal Document Policy](../docs/formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](../docs/document-policy-implementation.html)** - Operational rules and validation

## Scope
This file governs all materials under the `src/` directory, including Java source code, tests, benchmarks, and scripts. All content in `src/` is classified as "Software" under the formal model and is licensed under AGPL v3.0.

## Licensing
All contents of the `src/` directory are licensed under the GNU Affero General Public License v3.0 (AGPLv3).
- **Required License Header:** All copyrightable files must include REUSE-compliant SPDX headers
- **Transformation from docs/ to src/:** Software derived from CC BY-SA v4.0 documentation is formally transformed to AGPL v3.0 via the documented licensing algebra

## Software Classification Rules
From the formal model:
- `Location(f) = src/*` AND `Format(f) ∈ {Java, JAR, Class}` → `Software(f)`
- `Software(f)` → `Lic(f) = AGPL_3`

## Directory Structure
- `src/main/java/` - Primary Java source code
- `src/tests/` - Unit tests (JUnit)
- `src/benchmarks/` - Performance benchmarks
- `src/scripts/` - Build and utility scripts

## Code Quality Standards
- **Canonical Forms:** Use Java Records for immutable data structures
- **KeY Validation:** Code should be verifiable with KeY theorem prover
- **Annotation Processing:** Leverage JSR 269 for documentation-to-code morphisms
- **Jena and OpenLlet:** Use for RDF/OWL processing within Java
- **Java 25:** Target Java 25 ecosystem

## Prohibited in src/
- Documentation (prose, exposition, thesis materials) → belongs in `docs/`
- LLM-generated markdown → belongs in `docs/` (after curation)
- Build configuration → belongs at root (e.g., pom.xml)

## Agent-Specific Rules
- All agent-generated code must include PROV-O metadata in comments
- Generated code must pass compilation and validation before commit
- Follow Conventional Commits format for commit messages
- Generate SPDX headers for all new files

## See Also
- [Formal Document Policy](../docs/formal-document-policy.html) - Mathematical model
- [Implementation Guide](../docs/document-policy-implementation.html) - Rules and procedures
- [Root AGENTS.md](../AGENTS.md) - Repository-wide policies
- `src/benchmarks/AGENTS.md` - Benchmark policies
- `src/scripts/AGENTS.md` - Script policies
- `src/tests/AGENTS.md` - Test policies
