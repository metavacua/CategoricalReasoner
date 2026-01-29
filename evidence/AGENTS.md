# Agent Guidelines for Evidence Artifacts

## Overview

The `evidence/` directory contains **raw, overcollected** data extracted from public SPARQL endpoints (e.g. Wikidata, DBpedia) to support an evidence-first methodology.

These artifacts are intentionally *not* normalized into the project ontology; they exist to:
- preserve provenance
- provide reproducible “ground truth” snapshots
- surface gaps/contradictions in public linked data sources

## Constraints

- Do not implement web crawlers or custom parsers here.
- Only collect data through SPARQL endpoints.
- Preserve provenance in extracted RDF files (endpoint URL + query file reference + retrieval timestamp).
- Keep queries reproducible: store SPARQL scripts under `evidence/queries/`.

## Directory structure

- `evidence/*.ttl` — extracted raw RDF/Turtle
- `evidence/queries/*.sparql` — SPARQL scripts used to produce the raw RDF
- `evidence/discovered-endpoints.md` — endpoints discovered during extraction (including failures)
- `evidence/extraction-report.md` — summary with clear separation of data found vs. not found
- `evidence/sparql-queries-used.txt` — execution commands for reproducibility
