# AGENTS.md - Ontology Development

## Scope
The `ontology/` directory contains RDF/OWL schemas and knowledge graph data for Catty's categorical logic model: JSON-LD schema definitions, instance data, morphisms, and SHACL shapes.

## Core Constraints
- **Formats**: JSON-LD and Turtle RDF only. All files must parse without errors.
- **Schema Compliance**: Instance data must conform to `catty-categorical-schema.jsonld` classes and properties.
- **SHACL Validation**: All RDF artifacts must validate against `catty-shapes.ttl` and `catty-thesis-shapes.shacl`.
- **External References**: Link to Wikidata (`owl:sameAs`), DBpedia (`skos:exactMatch`), and DBLP where applicable. External identifiers must be resolvable.
- **Categorical Consistency**: Logic instances require signatures and axioms. Morphisms require source, target, and type definitions. All categorical constructions must have functorial mappings.
- **Namespaces**: Use `catty:` prefix (http://catty.org/ontology/) for all custom entities.

## Validation
Execute `python .catty/validation/validate.py --artifact <ontology-id>` or run SHACL validation directly with pyshacl.

## See Also
- `schema/AGENTS.md` - ID and citation constraints for linked data
