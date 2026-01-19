# Catty Java Semantic Web Server

## Overview

The `java/` module provides a self-contained localhost semantic web development server with:

- **In-memory Apache Jena dataset**: No external database required
- **IRI safety validation**: All JSON-LD uploads checked against the IRI registry
- **SPARQL endpoint**: Full SELECT/CONSTRUCT/DESCRIBE/ASK support
- **Browser UI**: Interactive query workbench
- **RDF serialization**: Export as Turtle, JSON-LD, or RDF/XML
- **IRI rebinding**: Convert between localhost and production IRIs

## Quick Start

```bash
cd java
mvn clean test      # Run all tests
mvn exec:java       # Start server on http://localhost:8080
```

## Architecture

### Core Components

1. **CattyIriConfig**: Loads `.catty/iri-config.yaml` registry
2. **InMemoryCattyDataset**: Loads all ontologies into named graphs
3. **HTTP Handlers**:
   - `ApiOntologiesHandler`: List registered ontologies
   - `ApiQueryHandler`: SPARQL query execution
   - `ApiGraphHandler`: Dataset export
   - `ApiLoadHandler`: RDF upload with validation
   - `ApiRebindHandler`: IRI transformation
   - `RepoOntologyHandler`: Serve ontology files for dereferencing

### Security Features

- **Path traversal prevention**: All file access validated
- **Request size limits**: 1MB max body size
- **IRI validation**: JSON-LD uploads checked for unauthorized IRIs
- **Localhost binding**: Server only accepts connections from 127.0.0.1

## API Reference

### GET /api/ontologies

List all registered ontologies:

```bash
curl http://localhost:8080/api/ontologies
```

Response:
```json
[
  {
    "key": "catty-categorical-schema",
    "localhost_iri": "http://localhost:8080/ontology/catty-categorical-schema#",
    "production_iri": "https://metavacua.github.io/.../catty-categorical-schema#",
    "context_url": "http://localhost:8080/ontology/context.jsonld",
    "file": "ontology/catty-categorical-schema.jsonld"
  }
]
```

### POST /api/query

Execute SPARQL query:

```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "PREFIX catty: <http://localhost:8080/ontology/catty-categorical-schema#> SELECT ?s WHERE { ?s a catty:Logic } LIMIT 10",
    "format": "turtle"
  }'
```

### GET /api/graph

Export entire dataset:

```bash
curl "http://localhost:8080/api/graph?format=turtle" > dataset.ttl
curl "http://localhost:8080/api/graph?format=jsonld" > dataset.jsonld
curl "http://localhost:8080/api/graph?format=rdfxml" > dataset.rdf
```

### POST /api/load

Upload RDF content:

```bash
curl -X POST http://localhost:8080/api/load \
  -H "Content-Type: application/ld+json" \
  -d @my-ontology.jsonld
```

Response (success):
```json
{
  "ok": true,
  "triples_loaded": 42
}
```

Response (IRI safety violation):
```json
{
  "ok": false,
  "base_iri": "http://example.com/unknown#",
  "errors": ["Unregistered @base IRI: http://example.com/unknown#"]
}
```

### POST /api/rebind

Transform IRIs between environments:

```bash
curl -X POST http://localhost:8080/api/rebind \
  -H "Content-Type: application/json" \
  -d '{
    "target": "production",
    "content": "{ \"@id\": \"http://localhost:8080/ontology/test#Thing\" }"
  }'
```

## Testing

Run all tests with coverage:

```bash
mvn clean test
```

Test coverage report: `target/site/jacoco/index.html`

### Test Classes

- `CattyIriConfigTest`: IRI registry loading
- `InMemoryCattyDatasetTest`: Dataset initialization and SPARQL
- `RepoOntologyHandlerTest`: File serving and security

## Development

### Adding a New Handler

1. Create handler class implementing `HttpHandler`
2. Add to `CattyServer.createContext(...)`
3. Write tests in `src/test/java/.../http/`

### Debugging

Run with debug logging:

```bash
mvn exec:java -Dexec.args="--port 8080 --repo /path/to/repo"
```

### Building a JAR

```bash
mvn clean package
java -jar target/catty-local-semweb-0.1.0.jar
```

## Deployment

**This server is for local development only.** For production:

- Use Apache Jena Fuseki or similar
- Add authentication/authorization
- Configure CORS appropriately
- Use HTTPS
- Add rate limiting
- Monitor resource usage

## Troubleshooting

### Server won't start

- Check Java version: `java -version` (need 17+)
- Check port availability: `lsof -i :8080`
- Verify `.catty/iri-config.yaml` exists

### Ontology not loading

- Check file path in `iri-config.yaml`
- Validate JSON-LD syntax
- Check context resolution (should use relative `context.jsonld`)

### IRI validation errors

- Ensure all IRIs are registered in `.catty/iri-config.yaml`
- Check `@base` matches localhost pattern
- Use relative IRIs or registered prefixes

## Performance Notes

- Dataset is fully in-memory (RAM usage proportional to ontology size)
- SPARQL queries use Jena ARQ optimizer
- No persistent storage between restarts
- Suitable for development/testing workloads (<100K triples)

## License

AGPL-3.0 (see LICENSE file)
