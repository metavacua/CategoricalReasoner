# Standards Directory

## Overview

This directory contains specifications for repository governance using **industry-standard vocabularies** only.

## Canonical Specification

### File: `repository-constraints.nt`

**Format**: N-Triples (canonical form)

**Vocabularies Used** (all industry standards, zero local ontology):

| Vocabulary | Purpose | Standard Body |
|------------|---------|---------------|
| schema.org | SoftwareSourceCode, CodeRepository, SoftwareApplication | W3C/Google/Microsoft/Yahoo |
| DOAP | Project, programming_language | W3C Community |
| DCTERMS | Standard, isPartOf, title, identifier | Dublin Core Metadata Initiative |
| SPDX | License identifiers | Linux Foundation |

## Conformance Mapping

The specification was refactored from proprietary URIs to industry standards:

| Previous (Non-Standard) | Current (Industry Standard) |
|-------------------------|------------------------------|
| `urn:repo:...#RepositoryNode` | `schema:SoftwareSourceCode` |
| `urn:repo:...#path` | `schema:filePath` |
| `urn:repo:...#AnnotationProcessing` | `dcterms:Standard` (JSR 269) |
| `urn:repo:...#ValidationStandard` | `dcterms:Standard` |

## Entities Defined

### Repository
- URI: `https://github.com/metavacua/CategoricalReasoner`
- Type: `schema:CodeRepository`
- License: AGPL-3.0-or-later (SPDX)

### Project
- URI: `https://github.com/metavacua/CategoricalReasoner#project`
- Type: `doap:Project`
- Programming Language: Java

### Source Directories
- `#docs` - Documentation (CC-BY-SA-4.0)
- `#src` - Source Code (AGPL-3.0-or-later)

### Standards
- ISO 14721:2025 (OAIS Reference Model)
- ISO 16363:2025 (Audit and Certification)
- JSR 269 (Pluggable Annotation Processing API)

### Technology Stack
- Apache Jena (Semantic Web Framework)
- OpenLlet (OWL Reasoner)
- KeY (Formal Verification)

## Derivation

"Derive" means: A file F2 derives from file F1 if and only if:
1. F1 is the authoritative source
2. Every statement in F2 is either copied from F1 or logically entailed by F1
3. F2 can be regenerated deterministically from F1

## Validation

- **Level 1 (Syntactic)**: N-Triples parse success
- **Level 2 (Structural)**: Schema.org vocabulary conformance
- **Level 3 (Semantic)**: Standard vocabulary usage
- **Level 4 (Domain)**: All filePath values verified

## License

CC BY-SA 4.0 International
