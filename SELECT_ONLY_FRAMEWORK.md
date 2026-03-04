# SELECT-only Federated SPARQL Query Framework

## Overview
This framework provides a Java-based implementation for executing SELECT queries across federated SPARQL endpoints. It is designed to be documentation-driven and test-driven, establishing a solid foundation for semantic data retrieval in the Catty project.

## Core Components

### 1. SelectQueryFederation (Interface)
The main facade for the framework. It handles:
- Endpoint discovery from Wikidata.
- Sequential execution of SELECT queries.
- Result collection and structuring.

### 2. SelectQueryExecutor (Abstract Class)
Handles the low-level execution details:
- Jena `QueryExecution` management.
- Timeout enforcement (default 30 seconds).
- Rate limiting to respect endpoint politeness.
- Custom User-Agent headers.

### 3. WikidataEndpointDiscovery
Specialized component that uses Wikidata's `P5305` (SPARQL endpoint) property to discover other available SPARQL endpoints.

### 4. SelectQueryResult
An immutable data container for query results, including metadata like execution time and timeout status.

## SDLC Integration
The framework bootstraps its own SDLC specification by retrieving formalized practices from semantic web endpoints. These practices are documented in `generated/SDLC_SPECIFICATION.md`.

## Usage

```java
SelectQueryFederation federation = new JenaSelectQueryFederation();
List<String> endpoints = federation.discoverEndpoints();
String query = "... SPARQL SELECT ...";
SelectQueryResult result = federation.executeSelectQuery(endpoints.get(0), query, 30);
```

## Performance and Politeness
- **Timeout**: Enforced 30-second timeout per request.
- **Rate Limiting**: Minimum 2-second delay between requests to the same endpoint.
- **User-Agent**: Identifies as `CattySelectQueryFederation/1.0`.

## Testing
Comprehensive JUnit test suite covering:
- Endpoint discovery.
- SDLC query execution.
- Timeout enforcement.
- Rate limiting behavior.
