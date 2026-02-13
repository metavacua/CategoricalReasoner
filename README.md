# Catty

An algorithmic thesis exploring categorical foundations for formal logics and their morphisms.

## Abstract

Catty develops algorithms and implementations for modeling formal logics as categorical structures. The project provides computational tools for representing logics as objects in categories, with morphisms capturing sequent restrictions and structural rules. The repository delivers executable algorithms that operate on semantic web data consumed from external sources.

## About This Project

### What is an Algorithmic Thesis?

An algorithmic thesis is a software repository that embodies research contributions through executable algorithms, not static documents. The primary deliverables are working implementations, validation frameworks, and computational tools that realize theoretical concepts in code.

Catty is not under any instutition, is not homework or any such instrument, is not subject to a thesis defense committee, or similar organizational commitments; it is a thesis in the sense of being held to formal standards for dissertation work at the level of the formation and operation of the instrument itself with voluntarily adopted academic standards for citations and scholarship. It is under the auspice of the Invisible College; the Invisible College is contemporaneously a socialogical concept related to the informal conduct of scholarship outside of established institutions, and it has direct roots into the common practice of science in the 19th and 20th century; it is in the tradition of Rutherford and Tesla.

The adoption of the structure of "algorithmic thesis" is not a dilution of the thesis but a deliberate restriction on it. Unlike a typical academic thesis that would be held to grading standards and the internal politics of an arbitrary tenured committee, this thesis must conform to academic and industrial standards, and the governing bodies that it must survive are the International standards bodies, private peers and entities, interdisciplinary communities, and the web of academic institutions that may or may not elect to participate in its development or judgement. If and when a committee or committees are formed to judge it then they too will be beholden to the highest intersectional standards, norms, and canonical methods of judgement.

### What Catty Delivers

This repository provides algorithms and implementations for:

- **Categorical logic modeling**: Computational representations of formal logics (LM, LK, LJ, LDJ, linear logic) as objects in categories
- **Morphism algorithms**: Tools for defining and reasoning about relationships between logics via extension and interpretation
- **Semantic web integration**: Consumption and reasoning over external RDF/OWL ontologies and knowledge graphs
- **Validation frameworks**: Automated testing infrastructure for verifying categorical structures and consistency

The project implements a Java-centric architecture with proven semantic web technologies, supported by auxiliary Python scripts for CI/CD orchestration.

### Supporting Documentation

The `thesis/` directory contains LaTeX whitepapers that document the research and development process. These files explain theoretical foundations, design decisions, and algorithmic approaches. They are supporting documentation for the algorithmic contributions, not the primary deliverables themselves.

## Project Structure

```
├── thesis/                      # LaTeX whitepapers documenting research and development
├── src/benchmarks/                  # Performance tests and evaluation
├── src/ontology/                    # Reference materials and examples
├── src/schema/                      # Validation schemas and constraints
├── docs/dissertation/bibliography/                # Citation registry
├── src/scripts/                     # Utility scripts
├── src/tests/                       # Test suites
├── .catty/                      # Operational model and validation framework
└── README.md                    # This file
```

Each directory contains its own README with detailed information about that component's purpose, structure, and usage.

## Technology Stack

**Primary ecosystem: Java**
- Semantic web processing and reasoning
- Code generation for validation and transformation
- Unit testing and validation frameworks

**Auxiliary: Python**
- CI/CD orchestration and helper scripts
- Utility functions supporting the Java architecture

See individual directory READMEs for specific technology details and build instructions.

## Semantic Web Data

This project consumes semantic web data from external sources rather than authoring local RDF schemas. External ontologies and knowledge graphs are accessed via SPARQL endpoints, linked data services, and the Giant Global Graph (GGG; https://en.wikipedia.org/wiki/Giant_Global_Graph).

See the `src/ontology/` and `src/benchmarks/` directories for information about consumed data sources and integration approaches.

## Development and Contribution

For information about contributing to this project, see `CONTRIBUTING.md`.

## Operational Model

The `.catty/` directory contains a formal operational model defining tasks, artifacts, dependencies, and validation criteria. This system governs the generation of algorithms, validation code, and documentation.

See `.catty/README.md` for complete documentation.

## License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0). See the `LICENSE` file for details.

## Special Files

- `AGENTS.md` - Machine-readable specifications for automated agents
- `CONTRIBUTING.md` - Contribution guidelines and workflows
- `LICENSE` - Full license text

## RO-Crate HelloWorld Example

This repository includes a minimal RO-Crate HelloWorld demonstrating Java + Jena SPARQL integration with reproducible builds. Key files:

- `pom.xml` - Maven build configuration with reproducible settings
- `src/main/java/org/metavacua/catty/RoCrateHelloWorld.java` - Main application
- `ro-crate-metadata.json` - RO-Crate 1.1 metadata (auto-generated from template)
- `ro-crate-metadata.json.template` - Template for RO-Crate metadata generation
- `Dockerfile` - Multi-stage container build
- `run.sh` - Automated build and run script

### Getting Started

**Quick start (default QID):**
```bash
./run.sh
```

**Query a specific QID:**
```bash
./run.sh Q1995545  # software package
./run.sh Q80       # software
./run.sh Q5        # human
```

This builds the JAR, executes a SPARQL query against Wikidata for the specified QID, and generates provenanced results.

**Direct Java execution:**
```bash
mvn clean package -DskipTests
java -jar rocrate-helloworld.jar Q1995545
```

**Container build:**
```bash
docker build -t catty-rocrate:0.0.0 .
docker run --rm -v "$PWD":/app catty-rocrate:0.0.0 Q1995545
```

### LLM-Assisted Semantic Mapping

The RO-Crate HelloWorld supports arbitrary QID input to enable LLM-assisted semantic mapping validation during development:

1. **Propose a mapping**: An LLM suggests a QID for a concept (e.g., "software package" → Q1995545)
2. **Validate the mapping**: Run the tool with the proposed QID to retrieve actual Wikidata entity data
3. **Verify semantics**: Compare retrieved labels and descriptions with the intended concept
4. **Iterate if needed**: Try alternative QIDs until the correct semantic match is found

This workflow allows LLMs to actively validate their semantic hypotheses against live Wikidata data, reducing errors in downstream development steps.

### Key Features

- ✅ Reproducible Maven builds (fixed timestamps, deterministic JARs)
- ✅ Modern Jena HTTP builder pattern for SPARQL execution
- ✅ W3C RDF language tags for literal normalization
- ✅ RO-Crate 1.1 compliant license modeling (SPDX)
- ✅ Multi-stage Docker builds with security hardening
- ✅ Comprehensive provenance tracking in generated artifacts
- ✅ Industry-standard OCI metadata labels
- ✅ Arbitrary QID input for semantic mapping validation
- ✅ Auto-generated RO-Crate metadata from Maven templates

### Generated Files

1. **rocrate-helloworld.jar** (at crate root)
   - Main executable artifact
   - Reproducibly built (fixed timestamp)
   - Part of RO-Crate distribution

2. **wikidata-rocrate-results.ttl**
   - SPARQL query results in Turtle format
   - Includes provenance header with:
     - Generation timestamp
     - Query endpoint and QID
     - Query execution time
     - Query hash
     - Entity count
   - Language-tagged literals (`@en`)

3. **ro-crate-metadata.json**
   - Auto-generated from template during Maven build
   - Contains complete RO-Crate 1.1 metadata
   - Version and other properties substituted from POM

### Troubleshooting

**Maven build fails:**
```bash
# Check Maven version
mvn -version

# Clean and retry
mvn clean package -DskipTests

# Check Java version
java -version
```

**SPARQL query fails:**
```bash
# Check internet connection
ping -c 3 query.wikidata.org

# Check endpoint availability
curl -I https://query.wikidata.org/sparql

# Try a different QID (some QIDs may not exist or have incomplete data)
```

**Invalid QID format:**
The tool validates QID format before querying. Valid format: `Q<digits>` (e.g., Q1995545).

**Docker build fails:**
```bash
# Check Docker version
docker --version

# Verify build context
docker build --no-cache -t catty-rocrate:0.0.0 .
```
