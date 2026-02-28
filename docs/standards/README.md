# Standards Directory

## Overview

This directory contains specifications for repository governance using **industry-standard vocabularies** only.

## Canonical Specification

### File: `repository-constraints.nt`

**Format**: N-Triples (canonical form)

**Vocabularies Used** (all industry standards, zero local ontology):

| Vocabulary | Purpose | Standard Body |
|------------|---------|---------------|
| schema.org | CreativeWork, Collection, ArchiveComponent, SoftwareSourceCode, Thesis, MathSolver | W3C/Google/Microsoft/Yahoo |
| DOAP | Project, programming_language | W3C Community |
| DCTERMS | Standard, isPartOf, title, identifier | Dublin Core Metadata Initiative |
| SPDX | License identifiers | Linux Foundation |

## schema.org Type Mapping

| Entity | schema.org Types |
|--------|------------------|
| Repository root | `schema:CreativeWork` + `schema:Collection` |
| docs/ directory | `schema:ArchiveComponent` + `schema:CreativeWork` |
| src/ directory | `schema:ArchiveComponent` + `schema:SoftwareSourceCode` |
| docs/dissertation/ | `schema:Thesis` + `schema:ArchiveComponent` |
| docs/standards/ | `schema:ArchiveComponent` |
| Categorical Reasoner | `schema:MathSolver` |

## Entities Defined

### Root Repository
- URI: `https://github.com/metavacua/CategoricalReasoner`
- Types: `schema:CreativeWork`, `schema:Collection`
- License: AGPL-3.0-or-later (SPDX)

### Project
- URI: `https://github.com/metavacua/CategoricalReasoner#project`
- Type: `doap:Project`
- Programming Language: Java

### MathSolver
- URI: `https://github.com/metavacua/CategoricalReasoner#solver`
- Type: `schema:MathSolver`
- Description: Categorical foundations for morphisms between logics

### Source Directories
- `#docs` - Documentation (`schema:ArchiveComponent` + `schema:CreativeWork`, CC-BY-SA-4.0)
- `#src` - Source Code (`schema:ArchiveComponent` + `schema:SoftwareSourceCode`, AGPL-3.0-or-later)
- `#dissertation` - Thesis (`schema:Thesis` + `schema:ArchiveComponent`)
- `#standards` - Standards (`schema:ArchiveComponent`)

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

## License

CC BY-SA 4.0 International
