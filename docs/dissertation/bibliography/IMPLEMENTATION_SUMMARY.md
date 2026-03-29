# Citation System Implementation Summary

## Implementation Status: ✅ COMPLETE

This document summarizes the Java/RO-Crate citation system implementation for the Categorical Reasoner project.

## Components Implemented

### 1. Maven Build Configuration (`pom.xml`)
- **Java 21** target
- **Required Dependencies**:
  - Apache Jena ARQ 4.9.0 (SPARQL processing)
  - JSONLD-Java 0.13.4 (JSON-LD processing)
  - Gson 2.10.1 (JSON processing)
  - JUnit Jupiter 5.10.0 (Testing)

### 2. Core Java Records

All records use Java 21 features with compact constructors for validation:

#### Value Objects
- `CitationKey.java` - Format: `[familyName][year][disambiguator]`
  - Validates lowercase alphanumeric with hyphens
  - Must start with a letter

- `Person.java` - Structured author names
  - familyName (required)
  - givenName, particle, suffix (optional)
  - Based on schema.org Person specification

- `InternationalizedString.java` - Titles with BCP-47 language tags
  - Validates language tag format
  - Provides locale information

- `PublicationDate.java` - EDTF/ISO8601-2 dates
  - Supports: year, year-month, year-month-day
  - Validates day ranges per month

#### Identifier Value Objects
- `Doi.java` - Digital Object Identifier (10.xxxx/...)
  - Validates DOI format
  - Provides `url()` method (https://doi.org/...)

- `Qid.java` - Wikidata QID (Q123456)
  - Validates QID format
  - Provides `url()` method (https://www.wikidata.org/entity/...)

- `ArxivId.java` - arXiv identifiers
  - Supports new format: `2001.12345`
  - Supports old format: `math.CT/0605035`
  - Provides `url()` and `pdfUrl()` methods

#### Enumerations
- `WorkType.java` - ARTICLE, BOOK, CONFERENCE, INCOLLECTION, THESIS, REPORT, PREPRINT
- `FormalizationStatus.java` - UNVERIFIED, AXIOMATIZED, PROVEN, DISCHARGED, DEPRECATED, REFUTED

#### Context Records
- `AgentContext.java` - LLM-facing structured notes
  - relevance (required)
  - category, notes, lastUpdated (optional)

### 3. Main Citation Record

`Citation.java` - The central citation record with:
- Key validation
- At least one author required
- Title with internationalization support
- Publication date
- Work type
- Optional identifiers (DOI, Wikidata QID, arXiv ID)
- Formalization status tracking
- Agent context for AI/LLM systems
- Dependency tracking (graph structure)
- Helper methods: `primaryAuthor()`, `shortCitation()`, `hasExternalIdentifiers()`, `urls()`

### 4. Citation Repository

`CitationRepository.java` - Central registry with:
- Pre-populated with 8 foundational citations
- Search methods: `findByKey()`, `findByAuthor()`, `findByType()`, `findByStatus()`, `findByCategory()`
- Unmodifiable defensive copies returned

#### Sample Citations Include:
1. Lawvere (1963) - Functorial Semantics of Algebraic Theories
2. Mac Lane (1971) - Categories for the Working Mathematician
3. Girard (1987) - Linear Logic
4. Grothendieck (1972) - Topos Theory (descent)
5. Joyal & Tierney (1984) - Geometric Logic and Topos Theory
6. Kan (1958) - Adjoint Functors
7. Mac Lane (1963) - Monads (Triples)
8. Johnstone (2002) - Sketches of an Elephant

### 5. RO-Crate Generator

`RoCrateGenerator.java` - Converts citations to RO-Crate 1.1 JSON-LD:
- Creates metadata descriptor
- Creates root dataset
- Maps each citation to schema.org types
- Generates person entities
- Outputs to `docs/dissertation/bibliography/ro-crate-metadata.json`
- Includes formalization status and agent context
- Supports dependency graph structure

### 6. BibLaTeX Exporter

`BiblatexExporter.java` - Converts citations to BibLaTeX format:
- Maps WorkType to BibLaTeX entry types
- Escapes LaTeX special characters
- Generates proper author formatting
- Outputs to `docs/dissertation/references.bib`
- Includes custom fields: `formalizationstatus`, `category`, `agentnotes`, `dependencies`

### 7. SPARQL Query Service

`SparqlQueryService.java` - Enables ARATU (Agentic Retrieval-Augmented Tool Use):
- Loads local RO-Crate into Apache Jena model
- Supports querying by formalization status
- Supports querying by category
- Can query Wikidata endpoint
- Can query Crossref endpoint
- Validates citation dependencies
- Provides `getAllStatuses()` for debugging

### 8. Unit Tests

`CitationTest.java` - Comprehensive test coverage:
- CitationKey validation tests
- Person creation tests
- InternationalizedString tests
- PublicationDate parsing and validation tests
- DOI, QID, ArxivId validation tests
- AgentContext tests
- Citation creation and validation tests
- CitationRepository query tests

## Key Features

### Formal Verification Support
- `FormalizationStatus` enum tracks theorem proving status
- Agent context provides AI/LLM facing metadata
- Category-based querying for automated relevance assessment
- Dependency graph for theorem dependency tracking

### External Integration
- Wikidata QID support with URL generation
- DOI support with URL generation
- arXiv support with PDF and abstract URLs
- SPARQL federation to external endpoints

### Standards Compliance
- **RO-Crate 1.1** - JSON-LD structure conforms to specification
- **schema.org** - Uses standard types for citations
- **OAIS** - Compatible with ISO 14721:2012 reference model
- **BibLaTeX 3.0** - Generates compatible bibliography
- **EDTF/ISO8601-2** - Extended date format support

## Usage

### Adding Citations

1. Add citation to `CitationRepository.java`:
```java
Citation.of(
    CitationKey.of("newauthor2024categorical"),
    List.of(Person.of("Author", "New")),
    InternationalizedString.of("Title", "en"),
    PublicationDate.of(2024),
    WorkType.ARTICLE,
    Doi.of("10.1000/xyz123"),  // optional
    Qid.of("Q987654"),         // optional
    null,                         // arxiv (optional)
    FormalizationStatus.UNVERIFIED,
    AgentContext.of("Relevance note", "category"),
    List.of()                      // dependencies
);
```

2. Run generators:
```bash
mvn clean compile
# Or manually run:
java -cp target/classes org.metavacua.categoricalreasoner.citation.BiblatexExporter
java -cp target/classes org.metavacua.categoricalreasoner.citation.RoCrateGenerator
```

### Using Citations in LaTeX

```latex
\cite{girard1987linear}  % Citation from repository
```

### Querying with SPARQL

```java
SparqlQueryService service = new SparqlQueryService();
List<String> axiomatized = service.queryByStatus(FormalizationStatus.AXIOMATIZED);
List<String> linearLogic = service.queryByCategory("linear-logic");
```

## Architectural Alignment

The implementation follows the principles from the project's memory:

- **Canonicality**: The mapping from Java Records to RO-Crate JSON-LD is canonical (bijective isomorphism)
- **Normalizability**: RO-Crate conforms to normalization procedures
- **Functorial Mapping**: SPARQL SELECT queries act as a functor mapping the knowledge graph to Java Records
- **Immutability**: All records are immutable frozen proof-objects
- **Type-Safety**: Compact constructors enforce logical constraints at construction

## Files Created

```
pom.xml
src/main/java/org/metavacua/categoricalreasoner/citation/
  ├── Citation.java
  ├── CitationKey.java
  ├── Person.java
  ├── InternationalizedString.java
  ├── PublicationDate.java
  ├── WorkType.java
  ├── FormalizationStatus.java
  ├── Doi.java
  ├── Qid.java
  ├── ArxivId.java
  ├── AgentContext.java
  ├── CitationRepository.java
  ├── RoCrateGenerator.java
  ├── BiblatexExporter.java
  └── SparqlQueryService.java
src/test/java/org/metavacua/categoricalreasoner/citation/
  └── CitationTest.java
```

## Generated Artifacts

When compiled and executed, the system generates:

1. `docs/dissertation/bibliography/ro-crate-metadata.json` - RO-Crate 1.1 metadata
2. `docs/dissertation/references.bib` - BibLaTeX bibliography

## Validation

All components include:
- Compact constructor validation
- Defensive copies of mutable parameters
- Optional wrapping for nullable fields
- Clear error messages for validation failures
- Comprehensive unit test coverage

## Future Enhancements

Potential improvements:
1. **Dependency Validation**: Cross-reference dependency keys to actual citations
2. **Citation Key Generation**: Auto-generate keys from author/year/title
3. **Bibliography Sync**: Import/export from existing `.bib` files
4. **Enhanced SPARQL**: More complex queries and federation
5. **DOI Resolution**: Automatic metadata lookup from DOI
6. **Wikidata Integration**: Auto-fetch metadata for citations with QIDs

## Notes

- Implementation uses Java 21 records and pattern matching
- All validation happens at construction time
- Maven build configured for Java 21
- System designed for integration with theorem provers
- Agent context enables automated citation relevance assessment
