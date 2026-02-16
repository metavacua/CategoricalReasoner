# Citation Registry

## Purpose

This directory implements the **ARATU (Agentic Retrieval-Augmented Tool Use)** tool registry for mathematical knowledge sources. The registry serves as the **grounding interface** between human epistemic exposure and agent reasoning context.

**Core architectural principle:** The citation registry is not primarily a bibliographic aid for human readers. It is a **typed knowledge graph** enabling automated agents to query, validate, and integrate mathematical sources into formal categorical constructions. The LaTeX output is a **view** of this graph—human-auditable but secondary to machine-actionable structure.

**Epistemic hierarchy:** This system implements a progression from **idiosyncratic** (individual) to **canonical** (universally necessary) representation:

1. **Canonical:** Java Record model—immutable, validated, unique representation
2. **Normalized:** RO-Crate 1.1 JSON-LD—single source of truth, no redundancy
3. **Standardized:** BibLaTeX 3.0, schema.org, OAIS—external specifications
4. **Conventional:** Citation key format, field ordering—coordination norms
5. **Ad hoc:** **Eliminated**—no provisional structures, no TODOs

---

## Canonical Model (Java 17 Records)

The authoritative representation is the Java Record model in `src/main/java/org/catty/citation/`. These types enforce **total structure** at construction—no nulls, no empty collections, no stringly-typed data.

### Core Records

```java
public record Citation(
    CitationKey key,                    // [familyName][year][disambiguator]
    NonEmptyList<Person> authors,       // At least one; structured names
    InternationalizedString title,      // With optional BCP-47 language tag
    PublicationDate date,               // EDTF/ISO8601-2 parsed
    WorkType type,                      // Enum: ARTICLE, BOOK, etc.
    Optional<Doi> doi,                  // Validated 10.xxxx/... format
    Optional<Qid> wikidata,             // Q123456 format
    Optional<ArxivId> arxiv,            // 2001.12345 format
    FormalizationStatus status,         // UNVERIFIED, AXIOMATIZED, PROVEN, DISCHARGED
    Optional<AgentContext> agentContext, // LLM-facing structured notes
    List<CitationKey> dependsOn         // Graph dependencies for ARATU traversal
) {}
```

```java
public record Person(
    String familyName,                  // Required
    Optional<String> givenName,
    Optional<String> particle,          // von, van, de, etc.
    Optional<String> suffix             // Jr., Sr., III, etc.
) {}
```

**Validation:** Compact constructors enforce invariants. No object can exist in an invalid state.

**Immutability:** All fields are final; defensive copies in constructors.

---

## Normalized Storage (RO-Crate 1.1)

The file `ro-crate-metadata.json` is the **single source of truth** for the citation registry. It is generated from the Java canonical model via `ro-crate-java` (edu.kit.datamanager:ro-crate-java:2.1.0) during the Maven `compile` phase.

**Generation:**
```bash
mvn compile
# Triggers: org.catty.citation.RoCrateGenerator
# Output: docs/dissertation/bibliography/ro-crate-metadata.json
```

**Structure:**
- `@context`: `https://w3id.org/ro/crate/1.1/context`
- Root entity: `Dataset` with `hasPart` linking to citations
- Citations: `schema:ScholarlyArticle` with structured properties
- ARATU extensions: `additionalProperty` for `formalizationStatus`, `agentContext`
- Identifiers: DOI and Wikidata QID as URI `@id` values

**Why RO-Crate:**
- **Normalized:** Single JSON-LD file, no redundancy, entity references by URI
- **Federation-ready:** `@id` uses global identifiers (DOI, QID)
- **OAIS-aligned:** Research Object packaging for long-term preservation
- **SPARQL-queriable:** JSON-LD expands to RDF triples

---

## Standardized Output (BibLaTeX 3.0)

BibLaTeX is a **view** of the normalized RO-Crate data, not a source. Generated during Maven `process-resources` phase.

**Generation:**
```bash
mvn process-resources
# Triggers: org.catty.citation.BiblatexExporter
# Output: docs/dissertation/references.bib
```

**Validation:**
```bash
biber --validate-datamodel docs/dissertation/references.bib
```

**Mapping:**
- `schema:ScholarlyArticle` → `@article`
- `schema:Book` → `@book`
- `schema:Thesis` → `@phdthesis` or `@mastersthesis`
- `Person` → BibLaTeX name list format: "Family, Given" or "Family, Particle, Given"

**Note:** BibLaTeX's heuristic name parsing is **not used**. Names are **total** in the Java model and **deterministically formatted** in the export.

---

## ARATU Integration

### For LLM Coding Agents

Citations are **tool schemas** in the ARATU paradigm. Agents query the registry before generating formalizations:

1. **In-Context Retrieval:** Load `ro-crate-metadata.json` as semantic artifact at context window start
2. **SPARQL Query:** Traverse `dependsOn` for dependency resolution
3. **JIT External RAG:** Verify DOI/QID against live endpoints (Crossref, Wikidata)
4. **Tool Invocation:** Retrieve `agentContext` for mathematical grounding

### SPARQL Federation

```sparql
PREFIX schema: <http://schema.org/>
PREFIX ex: <https://github.com/metavacua/CategoricalReasoner/ontology/>

SELECT ?citation ?status ?context
WHERE {
  ?citation a schema:ScholarlyArticle ;
            ex:formalizationStatus ?status ;
            ex:agentContext ?context .
  FILTER (?status = "axiomatized" || ?status = "proven")
}
```

**Endpoints:**
- Wikidata Query Service: `https://query.wikidata.org/sparql`
- Crossref SPARQL: `https://sparql.crossref.org/`
- Local RO-Crate: Apache Jena in-memory model

---

## Adding Citations

### Method 1: Java Record (Preferred)

Add to `src/main/java/org/catty/citation/CitationRepository.java`:

```java
Citation.of(
    "newauthor2024categorical",           // key
    List.of(Person.of("Author", "New")),    // authors
    "Categorical Approaches to Logic",      // title
    "2024",                                 // date
    WorkType.ARTICLE,                       // type
    "10.1000/xyz123",                       // doi (optional)
    "Q987654",                              // wikidata (optional)
    null,                                   // arxiv (optional)
    FormalizationStatus.UNVERIFIED,           // status
    "Encountered in survey; TODO: assess relevance to linear logic.", // agentContext
    List.of()                               // dependsOn
);
```

Run `mvn compile` to regenerate RO-Crate and BibLaTeX.

### Method 2: CI/CD (GitHub Actions)

For automated agent submissions:

```yaml
# .github/workflows/aratu-citation.yml
workflow_dispatch:
  inputs:
    citation_yaml:  # Validated against Java model
```

The workflow:
1. Parses input into Java Record (validation enforced)
2. Appends to `CitationRepository.java`
3. Runs `mvn compile` (RO-Crate generation)
4. Commits changes if validation passes

---

## Citation Key Format

**Convention:** `[familyName][year][disambiguator]`

- Lowercase alphanumeric
- Disambiguator: `a`, `b`, `c`... for same-author-same-year collisions
- Deterministic: Generated from first author's family name + year

**Examples:**
- `girard1987linear` → J.-Y. Girard, 1987, "Linear Logic"
- `trafford2016a` → J. Trafford, 2016, first of multiple works

**Collision handling:** If `trafford2016a` exists, next is `trafford2016b`.

---

## Formalization Status

| Status | Meaning | Agent Usage |
|--------|---------|-------------|
| `UNVERIFIED` | Recorded, not assessed | Cite for bibliography completeness only |
| `AXIOMATIZED` | Core definitions in project ontology | Safe for theorem statements |
| `PROVEN` | Theorem proved in proof assistant | Safe for proof dependencies |
| `DISCHARGED` | Fully verified | Safe for all uses |
| `DEPRECATED` | Superseded | Retained for historical provenance |
| `REFUTED` | Constructively demonstrated to be false | Resolution methods for first order logical reasoning and refutation complete |

---

## Dependencies

- **Java 17+** (Records, pattern matching, `Optional` stream methods)
- **Maven 3.9+** (build lifecycle)
- **edu.kit.datamanager:ro-crate-java:2.1.0** (RO-Crate generation)
- **io.github.xmlobjects:edtf-model:2.0.0** (EDTF date parsing)
- **org.apache.jena:jena-arq:4.9.0** (SPARQL processing)
- **BibLaTeX 3.0** (TeX output validation)

---

## Environment

- `RO_CRATE_OUTPUT`: Override default `docs/dissertation/bibliography` path
- `BIBLATEX_OUTPUT`: Override default `docs/dissertation/references.bib` path
- `SPARQL_TIMEOUT_MS`: Query timeout (default: 30000)

---

## See Also

- `src/main/java/org/catty/citation/` — Canonical Java Record model
- RO-Crate specification: https://w3id.org/ro/crate/1.1
- OAIS Reference Model: ISO 14721:2012
