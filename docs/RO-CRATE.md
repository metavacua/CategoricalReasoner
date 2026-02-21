# RO-Crate Research Data Packaging

## Overview

This document describes the OAIS-aligned procedures for research data packaging 
in the Catty thesis repository. RO-Crates are generated to create Submission 
Information Packages (SIPs) for long-term preservation and publication.

## When to Generate RO-Crates

RO-Crates should be generated at the following milestones:

| Milestone | Trigger | Purpose |
|-----------|---------|---------|
| **Pre-submission** | Thesis completion | Archive thesis materials |
| **Publication** | Acceptance/Deposit | Create DOI-ready package |
| **Major Revision** | Significant content update | Versioned preservation |
| **Data Release** | Ontology/query publication | Independent data citation |

## RO-Crate Structure

```
ro-crate/
├── ro-crate-metadata.json    # RO-Crate metadata (JSON-LD)
├── ro-crate-preview.html     # Human-readable preview
├── data/
│   ├── thesis/
│   │   ├── source/           # Markdown source files
│   │   ├── rendered/
│   │   │   ├── html/         # Quarto HTML output
│   │   │   └── pdf/          # Quarto PDF output
│   │   └── figures/          # Generated diagrams
│   ├── ontologies/           # RDF/OWL files
│   ├── queries/              # SPARQL query collections
│   ├── benchmarks/           # Performance data
│   └── code/                 # Source code snapshots
└── README.md                 # Package documentation
```

## Metadata Requirements

### Required Fields

| Field | Description | Source |
|-------|-------------|--------|
| `@id` | Unique identifier | DOI or UUID |
| `@type` | Entity type | `Dataset` |
| `name` | Title | Quarto YAML `title` |
| `description` | Abstract | Quarto YAML `abstract` |
| `creator` | Author(s) | Quarto YAML `author` |
| `datePublished` | Publication date | Quarto YAML `date` |
| `license` | License | `LICENSE` file |

### MSC2020 Integration

Mathematics Subject Classification codes are embedded in RO-Crate metadata:

```json
{
  "@id": "#thesis",
  "@type": "Dataset",
  "about": [
    {
      "@id": "https://mathscinet.ams.org/mathscinet/msc/msc2020.html?t=03B22",
      "@type": "DefinedTerm",
      "name": "Abstract deductive systems",
      "inDefinedTermSet": "MSC2020"
    }
  ]
}
```

### Quarto Metadata Mapping

Quarto YAML frontmatter maps to RO-Crate properties:

| Quarto YAML | RO-Crate Property |
|-------------|-------------------|
| `title` | `name` |
| `author` | `creator` |
| `date` | `datePublished` |
| `abstract` | `description` |
| `msc-primary` | `about` (with MSC2020 link) |
| `msc-secondary` | `about` (additional terms) |
| `keywords` | `keywords` |

## Generation Procedure

### Prerequisites

- Java 17+ runtime
- Jena libraries for RDF processing
- Quarto CLI for document building

### Steps

1. **Build Documents**
   ```bash
   quarto render docs/
   ```

2. **Generate RO-Crate**
   ```bash
   cd src/rocrate-generator
   ./mvnw exec:java -Dexec.mainClass="org.catty.rocrate.Generator"
   ```

3. **Validate Package**
   ```bash
   rocrate validate ro-crate/
   ```

4. **Deposit (if applicable)**
   - Upload to institutional repository
   - Obtain DOI
   - Update `ro-crate-metadata.json` with permanent identifier

## Java Implementation

The RO-Crate generator is implemented in Java per the requirements in 
`docs/dissertation/bibliography/README.md`:

- **Library**: Uses `ro-crate-java` or similar RO-Crate library
- **Input**: Quarto `_quarto.yml` and chapter YAML frontmatter
- **Output**: Valid RO-Crate 1.1+ package
- **Validation**: Checks against RO-Crate specification

## OAIS Alignment

The RO-Crate serves as an OAIS Submission Information Package (SIP):

| OAIS Entity | RO-Crate Equivalent |
|-------------|---------------------|
| Content Information | Document source and rendered outputs |
| Preservation Description Information (PDI) | RO-Crate metadata, provenance |
| Package Description | `ro-crate-metadata.json` |
| Submission Package | Complete `ro-crate/` directory |

## Versioning

RO-Crates follow semantic versioning:

- `v1.0.0` — Initial submission
- `v1.1.0` — Minor revisions
- `v2.0.0` — Major structural changes

Version information is recorded in `ro-crate-metadata.json` using 
`schema:version`.

## Citation

When citing the RO-Crate package:

```bibtex
@dataset{catty2025thesis,
  title = {Theoretical Metalinguistics},
  author = {McLean, Ian Douglas Lawrence Norman},
  year = {2025},
  publisher = {GitHub},
  url = {https://github.com/metavacua/CategoricalReasoner},
  note = {RO-Crate package}
}
```

## See Also

- [RO-Crate Specification](https://www.researchobject.org/ro-crate/)
- [OAIS Reference Model](https://public.ccsds.org/Pubs/650x0m2.pdf)
- `docs/dissertation/bibliography/README.md` — Java implementation requirements
- `docs/dissertation/bibliography/AGENTS.md` — Citation integration
