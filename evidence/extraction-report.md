# SPARQL extraction report (broad exploratory CONSTRUCT)

## Overview

Goal: collect raw RDF evidence about logical signatures around **sequent calculus** and specifically **LK**, while deliberately overcollecting to expose additional semantic web infrastructure.

Primary sources:
- Wikidata Query Service: https://query.wikidata.org/sparql
- DBpedia SPARQL endpoint: https://dbpedia.org/sparql

## Artifacts produced

| Artifact | Description |
|---|---|
| `evidence/wikidata-lk-raw.ttl` | Raw Turtle extracted from Wikidata using a broad CONSTRUCT query |
| `evidence/dbpedia-lk-raw.ttl` | Raw Turtle extracted from DBpedia using a broad CONSTRUCT query |
| `evidence/discovered-endpoints.md` | Endpoints discovered (incl. tested secondary endpoints + missing endpoint signals) |
| `evidence/sparql-queries-used.txt` | Repro commands + query file references |
| `evidence/queries/*.sparql` | The SPARQL scripts themselves |

Sizes (for quick sanity):
- `wikidata-lk-raw.ttl`: 195046 lines (~15 MB)
- `dbpedia-lk-raw.ttl`: 847 lines (~36 KB)

## Queries executed

### Wikidata
Query file: `evidence/queries/wikidata-lk-raw.construct.sparql`

Strategy:
- Use `SERVICE wikibase:mwapi` (`EntitySearch`) over a term list (sequent calculus, LK sequent calculus, Gentzen, cut-elimination, structural rules, classical/intuitionistic/linear logic, etc.)
- For each matched entity, extract a curated set of truthy properties (`wdt:`) including typing/hierarchy/topicality/provenance and potential endpoint discovery (`wdt:P5305`)

### DBpedia
Query file: `evidence/queries/dbpedia-lk-raw.construct.sparql`

Strategy:
- Start from a seed set of high-precision DBpedia resources (Sequent calculus, Proof theory, Gentzen, Classical/Intuitionistic/Linear logic, Structural rule, Cut-elimination theorem)
- Expand by keyword matches (still constrained to domain-specific phrases such as “exchange rule” rather than plain “exchange”)
- Extract a bounded set of descriptive relations (rdf:type, dct:subject, owl:sameAs, dbo:wikiPageExternalLink, dbo:wikiPageWikiLink) plus label/comment/abstract

## Data found (positive signals)

### Wikidata
- An entity for **sequent calculus** is present (e.g. `wd:Q1771121`), with hierarchical and topical relations (instance/subclass/part-of/main-subject/studied-by) and descriptive metadata (labels/descriptions/altLabels).
- Entities for nearby topics (proof theory, Gentzen, cut-elimination theorem, classical logic, intuitionistic logic, linear logic, etc.) are present in the extracted graph.
- Many entities include external URLs (e.g., official websites, “full work available at”, “exact match”), enabling further linkage.

### DBpedia
- Seed resources such as `dbr:Sequent_calculus`, `dbr:Proof_theory`, `dbr:Gerhard_Gentzen`, `dbr:Classical_logic`, `dbr:Intuitionistic_logic`, `dbr:Linear_logic` are present.
- `owl:sameAs` links connect DBpedia resources to Wikidata QIDs, providing a bridge for cross-source normalization.

### Endpoint discovery
- Several secondary SPARQL endpoints were discovered through Wikidata’s `wdt:P5305` and tested (see `evidence/discovered-endpoints.md`).

## Data not found / gaps (negative signals)

These gaps are treated as informative.

- **No clearly identified, dedicated Wikidata item for “LK” (Gentzen’s classical sequent calculus)** was pinned by label alone in this run. The extraction includes many “LK”-adjacent strings (often unrelated abbreviations), so LK may require disambiguation via additional constraints (e.g., “sequent calculus” + “Gentzen” + subclass relations) rather than label search.
- **No SPARQL endpoint recorded** on Wikidata for at least one relevant formalization resource:
  - Archive of Formal Proofs (`wd:Q108305552`) has no `wdt:P5305` value (see `evidence/discovered-endpoints.md`).
- No explicit evidence (via SPARQL endpoint metadata) was obtained here for Isabelle/Coq/Lean/nLab endpoints. This may reflect:
  - Wikidata incompleteness (endpoint not modeled), or
  - those projects not providing SPARQL endpoints.

## Ambiguities / contradictions observed

- Some concept items appear associated with `wdt:P5305` in the extracted graph. This may represent modeling noise (misuse of P5305) or a non-obvious intended meaning in Wikidata; treat these triples as “raw evidence” pending normalization.

## Reproducibility

See:
- `evidence/sparql-queries-used.txt` for exact curl commands
- `evidence/queries/*.sparql` for query text

Note: Public endpoints (especially WDQS) may rate-limit. Setting an explicit `User-Agent` is recommended.
