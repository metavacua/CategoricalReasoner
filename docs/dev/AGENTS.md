# AGENTS.md - Development Artifacts

## Scope
The `docs/dev/` directory contains development artifacts: reports and handbook materials. These are derived artifacts, not primary thesis or monograph content.

## Separation Principle
This directory is strictly separated from:
- `docs/dissertation/` - Primary thesis content ("Theoretical Metalinguistics")
- `docs/structural-rules/` - Monograph content ("Structural Rules: A Categorical Investigation")

## Subdirectories

### docs/dev/report/
Contains validation and verification reports derived from SPARQL queries, QID verification, and semantic audits. These are operational artifacts documenting the development process.

### docs/dev/handbook/
Contains repository infrastructure documentation, architecture descriptions, and owner manuals.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`, `*.yaml`.
- **Content Type**: Derived artifacts, not primary content.
- **Validation**: Reports must pass SPARQL endpoint queries; handbook must compile.

## Validation
All artifacts must pass validation. Reports require SPARQL evidence; handbook requires successful compilation.

## See Also
- `docs/dev/report/AGENTS.md` - Report policies
- `docs/dev/handbook/AGENTS.md` - Handbook policies
