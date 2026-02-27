# Standards Directory

## Overview

This directory contains canonical specifications for repository governance.

## Canonical Specification

### File: `repository-constraints.nt`

**Format**: N-Triples (canonical form)

**Characteristics**:
- One triple per line
- All URIs absolute (no prefixes)
- Space-separated subject, predicate, object
- Each line ends with " ."
- Lines sorted lexicographically
- 387 triples

## Validation Protocol

The specification was validated at 4 levels:

### Level 1: Syntactic Validation (N-Triples 1.1 Grammar)
- Parser: rdflib 7.6.0
- Result: **PASSED**
- Triple count: 387
- Line count matches triple count

### Level 2: Structural Validation (SHACL)
- Engine: pyshacl 0.31.0
- Result: **PASSED**
- All RepositoryNode instances conform to shape constraints

### Level 3: Semantic Validation (RDFS Consistency)
- Result: **PASSED**
- 30 RepositoryNode instances found
- No semantic contradictions detected

### Level 4: Domain Validation (Path Correspondence)
- Result: **PASSED**
- 29 path definitions found
- All paths exist on disk

## Derivation

"Derive" means: A file F2 derives from file F1 if and only if:
1. F1 is the authoritative source
2. Every statement in F2 is either copied from F1 or logically entailed by F1
3. F2 can be regenerated deterministically from F1

In this repository:
- `AGENTS.md` (root) is the authoritative source for governance constraints
- All other `AGENTS.md` and `README.md` files derive from root `AGENTS.md`
- The derivation relationship is formally defined using PROV-O vocabulary in `repository-constraints.nt`

## License

CC BY-SA 4.0 International
