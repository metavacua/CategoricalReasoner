<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->
<!-- AGENTS.md is licensed as documentation (CC BY-SA) but is semantically part of the configuration/codebase; agents must treat it as authoritative configuration, and derived code (src/, PRs) inherits AGPLv3 where applicable. -->

# AGENTS.md - HelloWorld Project

## Scope

This directory contains a minimal viable helloworld project demonstrating the AGENTS.md infrastructure requirements for the CategoricalReasoner repository. It serves as a proof-of-concept for the docs → src proof transformation.

## Constraints

This project inherits all constraints from:
- Root AGENTS.md
- docs/AGENTS.md

## Licensing

- Documentation files (this AGENTS.md, markdown, YAML configs): CC BY-SA 4.0
- Source code (src/main/java/): AGPLv3
- Configuration follows REUSE Specification 3.0+

## Standards

- Maven with Java 25
- REUSE headers on all files (REUSE.toml format, not deprecated dep5)
- pre-commit framework for validation
- GitHub dependency graphs via marketplace action `advanced-security/maven-dependency-submission-action@v5` and CodeQL (workflows in repository root .github/workflows/)

## Dependency Graph Submission

The helloworld project uses the official GitHub-authored marketplace action `advanced-security/maven-dependency-submission-action@v5` for automatic dependency graph submission. This action:
- Automatically runs `mvn dependency:tree` internally
- Submits dependency snapshots to the GitHub Dependency Graph
- Requires `dependency-graph: read` permission in the workflow
- Is maintained by GitHub's Advanced Security team (38k+ usage count)

## See Also

- Root AGENTS.md
- docs/AGENTS.md
