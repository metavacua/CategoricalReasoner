# Citation System Quick Start

## Overview

The Categorical Reasoner project now includes a complete Java/RO-Crate citation system with formal verification support.

## What's Included

- **Java 21** based immutable citation records
- **RO-Crate 1.1** metadata generation
- **BibLaTeX** export for LaTeX integration
- **SPARQL** federation for querying citations
- **Formal verification** status tracking (AXIOMATIZED, PROVEN, etc.)

## Quick Start

### 1. Build the Project

```bash
mvn clean compile package
```

This will compile the Java source files and generate:
- RO-Crate metadata (`docs/dissertation/bibliography/ro-crate-metadata.json`)
- BibLaTeX bibliography (`docs/dissertation/references.bib`)

### 2. Use Citations in LaTeX

The citation macros are defined in `docs/dissertation/macros/citations.tex`:

```latex
\cite{girard1987linear}
\cite{mac lane1971categories}
```

### 3. Add New Citations

Edit `src/main/java/org/metavacua/categoricalreasoner/citation/CitationRepository.java` and add your citation to the `CITATIONS` list:

```java
Citation.of(
    CitationKey.of("yourauthor2024new"),
    List.of(Person.of("Author", "New")),
    InternationalizedString.of("Your Title", "en"),
    PublicationDate.of(2024),
    WorkType.ARTICLE,
    Doi.of("10.1000/yourdoi"),           // optional
    Qid.of("Q987654"),                // optional
    null,                               // arxiv (optional)
    FormalizationStatus.UNVERIFIED,
    AgentContext.of("Why this is relevant", "category"),
    List.of()                           // dependencies
);
```

### 4. Query Citations Programmatically

```java
SparqlQueryService service = new SparqlQueryService();

// Find all axiomatized papers
List<String> axiomatized = service.queryByStatus(FormalizationStatus.AXIOMATIZED);

// Find papers in linear logic category
List<String> linearLogic = service.queryByCategory("linear-logic");

// Find by citation key
Optional<Citation> citation = CitationRepository.findByKey(
    CitationKey.of("girard1987linear")
);

// Find by author
List<Citation> girardWorks = CitationRepository.findByAuthor("Girard");
```

## Citation Key Format

Citation keys follow the pattern: `[familyName][year][disambiguator]`
- Lowercase letters, numbers, and hyphens only
- Must start with a letter
- Examples:
  - `girard1987linear`
  - `mac lane1971categories`
  - `lawvere1963functor`
  - `kan1958adjoint`

## Formalization Status

The system tracks the formal verification status of mathematical sources:

- **UNVERIFIED**: Not yet reviewed or formalized
- **AXIOMATIZED**: Axioms have been formalized in a theorem prover
- **PROVEN**: Theorems have been formally verified
- **DISCHARGED**: All dependencies have been verified
- **DEPRECATED**: Superseded by more recent results
- **REFUTED**: Found to be incorrect

## Architecture

The system follows category-theoretic principles:

1. **Immutability**: All citation records are immutable (frozen proof-objects)
2. **Type Safety**: Compact constructors validate invariants at construction time
3. **Canonicality**: Bijective mapping from Java Records to RO-Crate JSON-LD
4. **Functorial Mapping**: SPARQL queries act as a functor from knowledge graph to typed records

## For More Details

See the full implementation documentation:
- `docs/dissertation/bibliography/IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `docs/dissertation/bibliography/README.md` - Original requirements specification

## Sample Citations Included

The system includes 8 foundational category theory citations:

1. **Lawvere (1963)** - Functorial Semantics of Algebraic Theories
2. **Mac Lane (1971)** - Categories for the Working Mathematician
3. **Girard (1987)** - Linear Logic
4. **Grothendieck (1972)** - Topos Theory (descent)
5. **Joyal & Tierney (1984)** - Geometric Logic and Topos Theory
6. **Kan (1958)** - Adjoint Functors
7. **Mac Lane (1963)** - Monads (Triples)
8. **Johnstone (2002)** - Sketches of an Elephant
