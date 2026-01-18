# Catty Local Semantic Web Server (Java)

This Maven module provides an embedded localhost server (JDK `HttpServer`) backed by an in-memory Apache Jena dataset.

## Goals

- No external RDF services required (no Blazegraph, no federation)
- Load all ontologies registered in `../.catty/iri-config.yaml`
- Run SPARQL queries against an in-memory dataset
- Provide a small web UI for interactive querying
- Provide an API for serialization (Turtle/JSON-LD/RDF/XML) and JSON-LD IRI rebinding

## Run

```sh
cd java
mvn test
mvn exec:java
```

Open: `http://localhost:8080/`

## API

- `GET /api/ontologies`
- `POST /api/query`
  - JSON body: `{ "query": "...", "format": "turtle|jsonld|rdfxml" }`
- `GET /api/graph?format=turtle|jsonld|rdfxml`
- `POST /api/load`
  - Post RDF with content-type:
    - `application/ld+json`
    - `text/turtle`
    - `application/rdf+xml`
- `POST /api/rebind`
  - JSON body: `{ "target": "production|localhost", "content": "..." }`

## Notes

- The server names each ontology graph by its configured **localhost** base IRI (the `localhost_iri` from the registry).
- JSON-LD uploads are validated for IRI safety (checks `@base` and `@id` values).
