<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->

# HelloWorld - Proof-of-Concept: docs → src Transformation

## Overview

This directory demonstrates the foundational proof-theoretic model of the Categorical Reasoner "Catty" thesis, where documentation (open branch) can be transformed into executable code (closed branch).

## Proof-Theoretic Model

According to the AGENTS.md specification, file system structure corresponds to proof trees/proof nets:

| Branch Type | Proof-Theoretic Meaning | License | Content |
|-------------|-------------------------|---------|---------|
| **Open** | Incomplete proof, no program term | CC BY-SA v4 | Documentation, media, specifications |
| **Closed** | Complete proof, normalizing term | AGPL v3 | Executable code, software |

## Transformation Pattern

The `docs/helloworld/src/` directory contains the "witness" - the Java class that proves documentation is transformable into executable code:

- `docs/helloworld/` = **Open branch** (incomplete proof) → CC BY-SA 4.0
- `docs/helloworld/src/` = **Closed branch** (complete proof) → AGPL 3.0

This transformation is **proof completion**: open branches (CC BY-SA) are closed into programs (AGPL). The subformula property guarantees constraints are preserved, not weakened, under this transformation.

## Purpose

This minimal example serves as the template pattern for all other documentation sections (dissertation, structural-rules) to follow. Each documentation section should have:

1. A README.md explaining the proof-theoretic model
2. A src/ subdirectory containing the executable "witness"
3. An AGENTS.md inheriting constraints from root
4. A src/AGENTS.md specifying AGPL 3.0 license

## Technology Stack

This project demonstrates the full technology stack from root AGENTS.md:

| Technology | Purpose |
|------------|---------|
| **Java 25** | Primary ecosystem |
| **Maven** | Build and dependency management |
| **Jena** | RDF/OWL semantic web framework |
| **OpenLlet** | OWL 2 RL reasoner |
| **JavaPoet** | Code generation for annotation processing |
| **JUnit 5** | Testing framework |
| **PROV-O** | W3C Provenance Ontology for evidence graphs |
| **JSR 269** | Annotation processing for docs→src transformation |

## Project Structure

```
docs/helloworld/
├── README.md                    # This file (open branch, CC BY-SA)
├── AGENTS.md                    # Child crate constraints
└── src/                         # Closed branch (AGPL 3.0)
    ├── pom.xml                  # Maven build configuration
    ├── main/
    │   ├── java/catty/helloworld/
    │   │   ├── HelloWorld.java           # Main entry point
    │   │   ├── CattyFormula.java         # JSR 269 annotation
    │   │   ├── prov/                     # PROV-O implementation
    │   │   │   ├── PROVEntity.java
    │   │   │   ├── PROVActivity.java
    │   │   │   └── PROVAgent.java
    │   │   └── processor/                # Annotation processor
    │   │       └── CattyFormulaProcessor.java
    │   └── resources/
    │       └── helloworld.owl            # OWL 2 RL ontology
    └── test/
        └── java/catty/helloworld/
            └── HelloWorldTest.java       # JUnit 5 tests
```

## Key Demonstrations

1. **Jena RDF Model**: Creates RDF triples demonstrating semantic web data handling
2. **OpenLlet OWL Reasoning**: Shows OWL 2 RL ontology creation and reasoning API
3. **PROV-O Provenance**: Records agent activities as W3C-compliant evidence graphs
4. **JSR 269 Annotation Processing**: Automatic code generation from annotations
5. **JUnit 5 Testing**: Comprehensive test coverage of all components

## Validation

Run the following to validate:

```bash
cd docs/helloworld/src
mvn clean compile test
```

## See Also

- `/AGENTS.md` - Root configuration
- `/docs/AGENTS.md` - Documentation directory constraints
- `/docs/helloworld/AGENTS.md` - This child crate's constraints
- `/docs/helloworld/src/AGENTS.md` - Source directory constraints
