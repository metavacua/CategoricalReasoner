# AGENTS.md - Ontology Directory

## Scope
The `src/ontology/` directory contains **REFERENCE MATERIALS ONLY**. No local ontology authorship is permitted.

## Core Constraints
- **No Local Ontology Development**: This project consumes semantic web data from external sources (SPARQL endpoints, Wikidata, DBpedia, GGG), NOT from local RDF files.
- **Reference Only**: Any remaining files in this directory are for reference and example purposes only.
- **Prohibited**: Creating JSON-LD, TTL, or any RDF schema files that define local ontologies.

## External Data Consumption
The Catty project consumes external ontologies via:
- SPARQL endpoints (Wikidata Query Service, DBpedia)
- Linked data services
- Giant Global Graph (GGG)
- Jena-based loading and reasoning

## Validation
No local validation required. The Java-based infrastructure uses Jena for external RDF consumption.

## See Also
- `docs/external-ontologies.md` - Documentation on consumed external ontologies
- `docs/sparql-examples.md` - SPARQL query examples against external endpoints
