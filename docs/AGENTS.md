# AGENTS.md - Documentation Directory

## Scope

This file governs all materials under the `docs/` directory. All contents derive from and must comply with the root `AGENTS.md` constraints.

## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

- **Standards Collection**: The docs directory should have and collect relevant standards in a standards subdirectory for the repository and the development of its projects and derivatives.
- **Language Constraint**: W3C compliant HTML5 for documentation and Java (primary ecosystem for validation and transformation) for code with mathematically canonical forms prioritized; tex and markup is to be derived by qualified deterministic and formally verified standard compliant tools. There is a strict separation between developer tooling and tools that should be committed to the repository; python scripts, bash scripts, curl calls, and similar can be used for development but should not be committed in general to the repository.
- **License Constraint**: The repository should conform to and utilize https://reuse.software/dev/ for the dual licensing; where AGPLv3 and CC BY-SA v4 international conflict and both have standing, AGPLv3 is the more restrictive license so it prevails at the intersection.
- **Report Constraint**: Reports must be returned in semantic XHTML conformant to C14N or whatever format any tools use, and all documentation is to be contained in the docs directory with the sole exception being special files permitted for Github (e.g. README.md) and for specific tools like Maven.
- **Citation Constraint**: The documentation system, citation system, and citations must conform to relevant academic and industrial standards for open science, open source, and international learned society practices; OAIS (ISO 14721:2025) and (ISO 16363:2025) are minimal required standards compliance.

## Subdirectories

- `docs/dissertation/` - Dissertation Directory
- `docs/structural-rules/` - Structural Rules Directory
- `docs/standards/` - Standards Directory

## Licensing

All contents of this directory are licensed under CC-BY-SA-4.0.html.

## See Also

- `AGENTS.md` (root) - Core repository constraints

