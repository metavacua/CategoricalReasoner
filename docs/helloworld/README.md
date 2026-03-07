<!--
SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
SPDX-License-Identifier: CC-BY-SA-4.0
-->

# Catty HelloWorld - Infrastructure Smoke Test

## Purpose

This subproject is a **minimal infrastructure smoke test** that demonstrates the complete docs→src transformation pipeline for the Catty Thesis Repository. It is **NOT** a formal methods project, nor does it implement any categorical reasoning logic.

## What This Tests

This minimal project validates that the following infrastructure works correctly:

1. **REUSE.software** - Dual licensing with SPDX headers
2. **Pre-commit hooks** - Automated code quality checks
3. **GitHub Actions CI** - Build and test automation
4. **Maven build** - Java compilation with Java 25
5. **AGENTS.md inheritance** - Constraint propagation from root to child crates
6. **docs→src pattern** - Documentation directory containing buildable source

## Structure

```
docs/helloworld/
├── README.md                    # This file
├── AGENTS.md                    # Child crate inheriting root constraints
├── src/
│   ├── AGENTS.md                # Source directory (AGPL)
│   └── main/java/HelloWorld.java
├── LICENSES/                    # REUSE.software dual licensing
├── pom.xml                      # Minimal Maven configuration
├── reuse.toml                   # REUSE.software configuration
└── .github/workflows/ci.yml     # GitHub Actions CI
```

## Building and Running

```bash
# Build with Maven
mvn clean compile

# Run the HelloWorld program
mvn exec:java -Dexec.mainClass="HelloWorld"
```

## License

This demonstrates the CC BY-SA 4.0 → AGPL-3.0 transformation:
- **Documentation** (README.md, AGENTS.md): CC BY-SA 4.0
- **Source Code** (HelloWorld.java, pom.xml): AGPL-3.0-or-later

## What This Is NOT

- ❌ Not a formal methods verification tool
- ❌ Not a categorical reasoner implementation
- ❌ Not a semantic web data processor
- ❌ Not using Jena, OpenLlet, or KeY
- ❌ Not a complex Java application

The Java code is purely a **test payload** to prove the infrastructure works.
