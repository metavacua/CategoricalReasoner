# Report: Semantic Web Extraction and RAG Implementation

## Overview
This report documents the actual process of extracting semantic data from external SPARQL endpoints (Wikidata and DBpedia) for the Catty thesis project. It highlights the difficulties encountered, solutions implemented, and provides recommendations for future RAG (Retrieval-Augmented Generation) and code generation architectures.

## Extraction Process Evidence
Successful extraction of RDF data was performed using `src/benchmarks/run.py` against the following endpoints:

1.  **Wikidata** (`https://query.wikidata.org/sparql`):
    *   **Query**: `wikidata-logics.rq` (CONSTRUCT pattern)
    *   **Result**: `results/wikidata-logics.ttl`
    *   **Outcome**: Successfully extracted 10+ logic-related entities with labels and class memberships.
2.  **DBpedia** (`https://dbpedia.org/sparql`):
    *   **Query**: `dbpedia-category-theory.rq` (CONSTRUCT pattern)
    *   **Result**: `results/dbpedia-category-theory.ttl`
    *   **Outcome**: Successfully extracted concepts related to Category Theory.

## Encountered Difficulties and Solutions

### 1. Endpoint Security and User-Agent Filtering
*   **Issue**: Initial attempts to query Wikidata resulted in 403 Forbidden errors.
*   **Cause**: Wikidata requires a non-generic `User-Agent` header that identifies the bot/script.
*   **Solution**: Implemented a custom header `CattyThesisAgent/1.0` in the `requests` call within `run.py`.

### 2. SPARQL Store Limitations in RDFLib
*   **Issue**: The default `rdflib.plugins.stores.sparqlstore.SPARQLStore` lacked sufficient control over HTTP headers.
*   **Solution**: Rewrote `run.py` to use the `requests` library directly for remote SPARQL execution, allowing for precise control over `Accept` headers and `User-Agent`.

### 3. GIGO (Garbage In, Garbage Out) from LLM-Generated Queries
*   **Issue**: LLMs frequently generate SPARQL queries using incorrect prefixes or hallucinated property IDs (e.g., using `dbo:Logic` instead of verifying the actual DBpedia ontology).
*   **Solution**: Adopting the **Discovery Pattern** described in `docs/WIKIDATA_DISCOVERY.md`, where an agent first queries the endpoint to find correct URIs/Properties before attempting complex data extraction.

## Recommendations for Semantic Web RAG

### 1. Multi-Step Semantic Resolution
Instead of a single-shot RAG query, agents should follow a 3-step process:
1.  **Entity Resolution**: Convert natural language terms to URIs using Search APIs (e.g., `wbsearchentities`).
2.  **Neighborhood Extraction**: Use `DESCRIBE` or `CONSTRUCT` queries to pull the immediate graph neighborhood of the resolved URIs.
3.  **Contextual Synthesis**: Provide the extracted TTL to the LLM. TTL is superior to JSON-LD for RAG context because it is more concise and its indentation clearly represents graph structure.

### 2. Real-time SPARQL Validation
Coding agents must be equipped with a local or remote SPARQL engine to validate queries before including them in documentation or codebases. This prevents the "hallucination loop" where an agent generates a query, never runs it, and assumes it works.

### 3. TTL-based Knowledge Injection
For code generation, especially in categorical logic, the formal constraints of the domain should be injected as SHACL shapes. This allows the agent to validate its own generated RDF artifacts against the project's schema before final submission.

## Future Research and Development
*   **SPARQL Query Optimization Agents**: Developing specialized agents that can take a naive "GIGO" query and optimize it for performance and accuracy against specific endpoints.
*   **Ontology-Driven Code Generation**: Using Jena JavaPoet integration to generate Java classes directly from extracted RDF schemas, ensuring type safety matches semantic definitions.
*   **Federated RAG**: Researching how to perform RAG across multiple federated SPARQL endpoints to synthesize knowledge that doesn't exist in a single repository.
