---
name: Ontology Extension
about: Extend or modify RDF/OWL ontologies
title: '[ONTOLOGY] '
labels: ['ontology', 'rdf', 'semantic-web']
assignees: ''
---

## Ontology Extension Description

**Ontology File**: `ontology/[filename].ttl` or `.jsonld`

**Purpose**: Briefly describe what this extension adds or modifies.

## Changes

### New Classes

List new classes to be added:

- `catty:ClassName`: Description

### New Properties

List new properties to be added:

- `catty:propertyName`: Domain, Range, Description

### New Instances

List new instances (e.g., new logics, morphisms):

- `catty:InstanceName`: Type, Description

### Modifications

Describe any modifications to existing classes, properties, or instances:

-

## External Alignments

Will this extension link to external ontologies?

- [ ] DBpedia (`dbr:`)
- [ ] Wikidata (`wd:`)
- [ ] nLab
- [ ] OpenMath
- [ ] Other:

Specify URIs:

-

## SHACL Constraints

Do we need to add or modify SHACL validation constraints?

- [ ] Yes (describe below)
- [ ] No

If yes, specify constraints:

-

## Dependencies

List any existing ontology elements this depends on:

-
-

## Deliverables

- [ ] Updated or new `.ttl` or `.jsonld` file in `ontology/`
- [ ] SHACL validation passes
- [ ] RDF syntax is valid (parseable by RDFLib or Apache Jena)
- [ ] External URI references are valid (dereferenceable)
- [ ] Documentation in `ontology/README.md` or related file
- [ ] SPARQL query examples (if applicable)

## Validation Steps

How will this extension be validated?

- [ ] Parse with RDFLib (Python): `rdflib.Graph().parse(file)`
- [ ] SHACL validation: `pyshacl` or similar
- [ ] Manual inspection of triples
- [ ] SPARQL queries return expected results

## Acceptance Criteria

- [ ] RDF file parses without errors
- [ ] SHACL validation passes (if constraints exist)
- [ ] External URIs resolve (DBpedia, Wikidata, etc.)
- [ ] Integration with existing ontologies is correct
- [ ] Documentation is updated
- [ ] SPARQL examples work (if applicable)

## Additional Notes

(Any additional context, references, or considerations)
