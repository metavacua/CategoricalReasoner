# AGENTS.md - Ontology Development (Examples Only)

## Scope
The `src/ontology/` directory contains **EXAMPLE and REFERENCE MATERIALS ONLY**. These files illustrate how RDF/OWL schemas and knowledge graph data might be structured for Catty's categorical logic model.

**IMPORTANT**: Do not use these files as authoritative sources. The actual architecture consumes external ontologies from SPARQL endpoints and linked data sources (DBPedia, Wikidata, GGG).

## Core Constraints
- **Formats**: JSON-LD and Turtle RDF only. All files must parse without errors.
- **Schema Compliance**: Instance data must conform to `catty-categorical-schema.jsonld` classes and properties.
- **SHACL Validation**: All RDF artifacts must validate against `catty-shapes.ttl` and `catty-thesis-shapes.shacl`.
- **External References**: Link to Wikidata (`owl:sameAs`), DBpedia (`skos:exactMatch`), and DBLP where applicable. External identifiers must be resolvable.
- **Categorical Consistency**: Logic instances require signatures and axioms. Morphisms require source, target, and type definitions. All categorical constructions must have functorial mappings.
- **Namespaces**: Use `catty:` prefix (http://example.org/src/ontology/) for all custom entities.

## Validation
These are example files only. No validation is required for the actual project, which uses external ontologies.

## Development Constraints
**DO NOT** create or modify files in this directory. All ontology development should:
1. Consume from external sources (SPARQL endpoints, linked data)
2. Use Jena to load and process external RDF
3. Use OpenLlet for reasoning
4. Not author local RDF schemas

## See Also
- `src/benchmarks/README.md` - SPARQL benchmarks against external endpoints
- `src/schema/README.md` - Validation schemas (note: does not use local ontology)
