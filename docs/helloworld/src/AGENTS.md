<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: AGPL-3.0-only -->
<!-- AGENTS.md is licensed as documentation (AGPL) but is semantically part of the configuration/codebase; agents must treat it as authoritative configuration. -->

# AGENTS.md - HelloWorld Source Directory

This file governs the source directory for the helloworld proof-of-concept crate.

## Inheritance

- **Parent**: `/AGENTS.md` (root)
- **Parent**: `/docs/helloworld/AGENTS.md` (child crate)
- **Proof-theoretic status**: Closed branch (complete proof, normalizing term)
- **License**: AGPL 3.0

## Licensing

All contents of this directory are licensed under the GNU Affero General Public License v3.0 (AGPLv3). This represents the "closed branch" of the proof-theoretic model - the transformation from documentation (open, CC BY-SA) to executable code (closed, AGPL).

As stated in root AGENTS.md:

> The `docs/ → src/` transformation is **proof completion**: open branches (CC BY-SA) are closed into programs (AGPL). Subformula property guarantees constraints are preserved, not weakened, under this transformation.

## Contents

This directory contains a Maven project demonstrating the full technology stack:

- `pom.xml` - Maven build with Jena, OpenLlet, JavaPoet, JUnit 5
- `main/java/catty/helloworld/`
  - `HelloWorld.java` - Main entry point demonstrating Jena + OpenLlet + PROV-O
  - `CattyFormula.java` - JSR 269 annotation for semantic HTML transformation
  - `prov/` - PROV-O (W3C Provenance Ontology) implementation
  - `processor/` - Annotation processor using JavaPoet
- `main/resources/helloworld.owl` - OWL 2 RL ontology
- `test/java/catty/helloworld/HelloWorldTest.java` - JUnit 5 tests

## Technology Stack

Per root AGENTS.md, this project implements:

- **Java 25** - Primary ecosystem
- **Jena** - RDF/OWL semantic web framework
- **OpenLlet** - OWL 2 RL reasoner
- **JavaPoet** - Code generation for annotation processing
- **JUnit 5** - Testing framework
- **PROV-O** - W3C Provenance Ontology for evidence graphs
- **JSR 269** - Annotation processing for docs→src transformation

## Validation

- Maven compilation serves as the primary validator
- JUnit 5 tests verify PROV-O, Jena, OWL API, and JavaPoet functionality
- Annotation processing runs during compilation (SOURCE retention)

## See Also

- `/AGENTS.md` - Root configuration
- `/docs/helloworld/AGENTS.md` - Parent crate constraints
