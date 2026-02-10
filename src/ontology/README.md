# Catty Ontology Directory

## ⚠️ IMPORTANT DISCLAIMER

**This directory contains REFERENCE MATERIALS ONLY.**

This project **does not author local ontologies**. All semantic web data is consumed from external sources via SPARQL endpoints and linked data services.

## Purpose

This directory contains:
- Example SPARQL queries against external endpoints
- Reference documentation for external ontology consumption
- SHACL shapes for validation (if applicable to external data)

## External Data Sources

The Catty project consumes semantic web data from:

| Source | URL | Usage |
|--------|-----|-------|
| Wikidata | https://www.wikidata.org | Entity linking, QID resolution |
| DBpedia | https://dbpedia.org/sparql | Linked data queries |
| GGG | Giant Global Graph | General knowledge graph |

## Technology Stack

**Primary: Java**
- Apache Jena for RDF processing
- OpenLlet for reasoning
- JUnit for testing

## Prohibited Activities

❌ **DO NOT**:
- Create new JSON-LD files defining local ontologies
- Author RDF schemas or OWL ontologies
- Instantiate local ontology classes
- Reference `http://catty.org/` (invalid domain)

✅ **DO**:
- Query external SPARQL endpoints
- Process external RDF data with Jena
- Document external data consumption patterns
- Verify external QIDs and URIs

## License

This directory is part of the Catty project, licensed under AGPL-3.0.

## References

- `docs/external-ontologies.md` - External ontology documentation
- `docs/sparql-examples.md` - SPARQL query examples
