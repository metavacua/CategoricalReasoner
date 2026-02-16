# Citation Registry - Implementation Requirements

## Status: IMPLEMENTATION REQUIRED

**February 2026**: The YAML-based citation registry (`citations.yaml`) has been eliminated from the CategoricalReasoner repository. The citation system described below is **NOT IMPLEMENTED** - it is a design specification that needs to be built.

## Purpose

The `docs/dissertation/bibliography/` directory should contain the citation registry system for all thesis artifacts. When implemented, this will be the **ARATU (Agentic Retrieval-Augmented Tool Use)** tool registry for mathematical knowledge sources.

**Core architectural principle:** The citation registry is not primarily a bibliographic aid for human readers. It should be a **typed knowledge graph** enabling automated agents to query, validate, and integrate mathematical sources into formal categorical constructions. The LaTeX output is a **view** of this graph—human-auditable but secondary to machine-actionable structure.

---

## Missing Implementation Components

The following components are **NOT PRESENT** in the repository and must be implemented:

### 1. Maven Build Configuration (Missing)

**File**: `pom.xml` (at repository root)

**Required dependencies**:
```xml
<dependencies>
  <!-- RO-Crate 1.1 support -->
  <dependency>
    <groupId>edu.kit.datamanager</groupId>
    <artifactId>ro-crate-java</artifactId>
    <version>2.1.0</version>
  </dependency>

  <!-- EDTF date parsing -->
  <dependency>
    <groupId>io.github.xmlobjects</groupId>
    <artifactId>edtf-model</artifactId>
    <version>2.0.0</version>
  </dependency>

  <!-- SPARQL processing -->
  <dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-arq</artifactId>
    <version>4.9.0</version>
  </dependency>

  <!-- Testing -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

**Missing build plugins**:
- `maven-compiler-plugin` (Java 21+ required)
- `maven-resources-plugin` (for BibLaTeX export generation)

### 2. Java Source Files (Missing)

**Directory**: `src/main/java/org/metavacua/categoricalreasoner/citation/`

**Required Java Records**:

#### `Citation.java`
```java
package org.metavacua.categoricalreasoner.citation;

import java.util.List;
import java.util.Optional;

/**
 * Canonical citation record with total structure validation.
 * Javadoc annotations required for RO-Crate generation.
 *
 * @param key                    Citation key format: [familyName][year][disambiguator]
 * @param authors                At least one author; structured names
 * @param title                  Internationalized title with optional BCP-47 language tag
 * @param date                   EDTF/ISO8601-2 parsed date
 * @param type                   Publication type enum
 * @param doi                    Optional validated DOI (10.xxxx/... format)
 * @param wikidata               Optional Wikidata QID (Q123456 format)
 * @param arxiv                  Optional arXiv ID (2001.12345 format)
 * @param status                 Formalization status tracking
 * @param agentContext           LLM-facing structured notes
 * @param dependsOn             Graph dependencies for ARATU traversal
 */
public record Citation(
    CitationKey key,
    List<Person> authors,
    InternationalizedString title,
    PublicationDate date,
    WorkType type,
    Optional<Doi> doi,
    Optional<Qid> wikidata,
    Optional<ArxivId> arxiv,
    FormalizationStatus status,
    Optional<AgentContext> agentContext,
    List<CitationKey> dependsOn
) {
    /**
     * Compact constructor enforcing invariants.
     * @throws IllegalArgumentException if authors list is empty
     */
    public Citation {
        if (authors == null || authors.isEmpty()) {
            throw new IllegalArgumentException("Citation must have at least one author");
        }
        // All other validation enforced by record component types
    }
}
```

#### `Person.java`
```java
package org.metavacua.categoricalreasoner.citation;

import java.util.Optional;

/**
 * Structured person record for citation authors.
 * All fields are immutable; defensive copies in constructors.
 *
 * @param familyName  Required family name
 * @param givenName   Optional given name(s)
 * @param particle    Optional particle (von, van, de, etc.)
 * @param suffix      Optional suffix (Jr., Sr., III, etc.)
 *
 * @see <a href="https://schema.org/Person">schema.org Person specification</a>
 */
public record Person(
    String familyName,
    Optional<String> givenName,
    Optional<String> particle,
    Optional<String> suffix
) {
    /**
     * Compact constructor enforcing invariants.
     * @throws IllegalArgumentException if familyName is null or blank
     */
    public Person {
        if (familyName == null || familyName.isBlank()) {
            throw new IllegalArgumentException("Family name is required");
        }
        // Normalize whitespace
        familyName = familyName.trim();
    }
}
```

**Additional required records** (to be implemented):
- `CitationKey.java` - Value object with validation
- `InternationalizedString.java` - Title with BCP-47 language tag
- `PublicationDate.java` - EDTF/ISO8601-2 parsed date
- `WorkType.java` - Enum: ARTICLE, BOOK, CONFERENCE, INCOLLECTION, THESIS, REPORT
- `Doi.java`, `Qid.java`, `ArxivId.java` - Typed identifier value objects
- `FormalizationStatus.java` - Enum: UNVERIFIED, AXIOMATIZED, PROVEN, DISCHARGED, DEPRECATED, REFUTED
- `AgentContext.java` - LLM-facing structured notes

### 3. Citation Repository (Missing)

**File**: `src/main/java/org/metavacua/categoricalreasoner/citation/CitationRepository.java`

```java
package org.metavacua.categoricalreasoner.citation;

import java.util.List;
import java.util.Optional;

/**
 * Repository for citation records.
 * Generates RO-Crate metadata during compilation.
 */
public final class CitationRepository {

    private static final List<Citation> CITATIONS = List.of(
        // Citations to be added here when implemented
    );

    private CitationRepository() {
        // Utility class
    }

    public static List<Citation> getAll() {
        return List.copyOf(CITATIONS);
    }

    public static Optional<Citation> findByKey(CitationKey key) {
        return CITATIONS.stream()
            .filter(c -> c.key().equals(key))
            .findFirst();
    }
}
```

### 4. RO-Crate Generator (Missing)

**File**: `src/main/java/org/metavacua/categoricalreasoner/citation/RoCrateGenerator.java`

**Required functionality**:
- Load all citations from `CitationRepository`
- Generate RO-Crate 1.1 JSON-LD structure
- Output to `docs/dissertation/bibliography/ro-crate-metadata.json`
- Integrate with Maven `compile` phase

**RO-Crate structure**:
```json
{
  "@context": "https://w3id.org/ro/crate/1.1/context",
  "@graph": [
    {
      "@id": "ro-crate-metadata.json",
      "@type": "CreativeWork",
      "conformsTo": "https://w3id.org/ro/crate/1.1",
      "about": {"@id": "./"}
    },
    {
      "@id": "./",
      "@type": "Dataset",
      "hasPart": [
        {"@id": "#girard1987linear"},
        {"@id": "#kripke1965semantical"}
      ]
    },
    {
      "@id": "#girard1987linear",
      "@type": "ScholarlyArticle",
      "name": "Linear Logic",
      "author": [{"@type": "Person", "familyName": "Girard"}],
      "datePublished": "1987"
    }
  ]
}
```

### 5. BibLaTeX Exporter (Missing)

**File**: `src/main/java/org/metavacua/categoricalreasoner/citation/BiblatexExporter.java`

**Required functionality**:
- Load RO-Crate metadata
- Generate BibLaTeX 3.0 format
- Output to `docs/dissertation/references.bib`
- Integrate with Maven `process-resources` phase

**BibLaTeX output example**:
```bibtex
@article{girard1987linear,
  author = {Girard, Jean-Yves},
  title = {Linear Logic},
  journal = {Theoretical Computer Science},
  year = {1987},
  volume = {50},
  number = {1},
  pages = {1--102}
}
```

### 6. SPARQL Federation Support (Missing)

**File**: `src/main/java/org/metavacua/categoricalreasoner/citation/SparqlQueryService.java`

**Required endpoints**:
- Wikidata Query Service: `https://query.wikidata.org/sparql`
- Crossref SPARQL: `https://sparql.crossref.org/`
- Local RO-Crate: Apache Jena in-memory model

**Example SPARQL query**:
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

---

## OpenJDK 21+ Setup Issues (February 2026)

As of February 2026, there are known issues with OpenJDK 21+ setup in certain environments:

1. **Maven plugin compatibility**: Some older Maven plugins may not be compatible with Java 21
2. **Build tool updates**: Ensure Maven 3.9+ is installed
3. **IDE support**: Verify IDE supports Java 21 features (records, pattern matching, `Optional` stream methods)

**Workaround**: If OpenJDK 21+ setup fails, consider using:
- Eclipse Temurin 21 from Adoptium
- Corretto 21 from Amazon
- Building with SDKMAN! for version management

---

## Current State

### What Exists
- TeX citation macros in `docs/dissertation/macros/citations.tex` (functional but unvalidated)
- Validation scripts `validate_citations.py` and `validate_consistency.py` (disabled pending Java implementation)

### What's Missing
- **ALL Java source files** in `src/main/java/org/metavacua/categoricalreasoner/citation/`
- Maven `pom.xml` configuration
- RO-Crate 1.1 JSON-LD output
- BibLaTeX export functionality
- SPARQL federation support

---

## Implementation Roadmap

1. **Create Maven `pom.xml`** with required dependencies and plugins
2. **Implement Java record types** with Javadoc annotations
3. **Create `CitationRepository`** with sample citations
4. **Implement `RoCrateGenerator`** for JSON-LD export
5. **Implement `BiblatexExporter`** for TeX bibliography
6. **Implement `SparqlQueryService`** for federation support
7. **Update validation scripts** to use Java/RO-Crate system
8. **Test CI/CD integration**

---

## For Developers

### Adding Citations (When Implemented)

Once the Java/RO-Crate system is implemented:

1. **Add to Java `CitationRepository.java`**:
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

2. **Run Maven build**:
```bash
mvn clean compile process-resources
```

This regenerates:
- `ro-crate-metadata.json`
- `references.bib`

3. **Use citation in TeX**:
```latex
This result follows from \cite{newauthor2024categorical}.
```

---

## Validation Status

### Currently Disabled

- `validate_citations.py` - TEMPORARILY DISABLED
- `validate_consistency.py` - TEMPORARILY DISABLED

These scripts exit with success status (0) and print a message directing developers to this README.

### Future Validation

When Java/RO-Crate system is implemented:

```bash
# Validate RO-Crate structure
mvn verify

# Validate BibLaTeX format
biber --validate-datamodel docs/dissertation/references.bib

# Test SPARQL queries
mvn test -Dtest=SparqlQueryServiceTest
```

---

## See Also

- `docs/dissertation/macros/citations.tex` - LaTeX citation macros
- `src/schema/validators/validate_citations.py` - Disabled validator (see file header)
- `src/schema/validators/validate_consistency.py` - Disabled validator (see file header)
- RO-Crate specification: https://w3id.org/ro/crate/1.1
- OAIS Reference Model: ISO 14721:2012
- schema.org: https://schema.org/
