# Standards Directory

## Overview

This directory contains canonical specifications for repository governance.

## Canonical Format

The repository constraints specification is provided in **N-Triples format**, which is one of the canonical forms recognized by this repository:

- **Format**: N-Triples (.nt)
- **Canonical Form**: Sorted lexicographically
- **Characteristics**:
  - One triple per line
  - All URIs absolute (no prefixes)
  - Space-separated subject, predicate, object
  - Each line ends with " ."

## Specification File

- `repository-constraints.nt` - Repository constraints in canonical N-Triples format

## Derivation

All AGENTS.md and README.md files in this repository derive from the root AGENTS.md, which encapsulates the constraints defined in this canonical specification.

## License

CC BY-SA 4.0 International
