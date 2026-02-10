package org.catty.sparql.federation;

import java.util.List;
import org.apache.jena.query.QueryExecution;

/**
 * Main facade for SELECT-only federated SPARQL query execution.
 * 
 * Responsibilities:
 * - Coordinate endpoint discovery (from Wikidata)
 * - Execute SELECT queries across endpoints
 * - Enforce 30-second timeout per endpoint
 * - Collect and structure results
 * - Apply polite request patterns
 * 
 * SDLC Integration (from Query A results):
 * - Phase: Implementation
 * - Artifacts: JUnit tests, JavaDoc specifications, README
 * - Gates: All queries return non-empty, valid results within timeout
 * 
 * Testing (from Query B results):
 * - Unit test per method (TDD approach)
 * - Integration test for full federation workflow
 * - Test fixtures from actual endpoint responses
 * 
 * @see benchmarks/queries/
 */
public interface SelectQueryFederation {
  
  /**
   * Execute SELECT query against a single SPARQL endpoint with timeout.
   * 
   * @param endpoint SPARQL endpoint URI
   * @param selectQuery SPARQL SELECT query string
   * @param timeoutSeconds Maximum execution time (default 30)
   * @return QueryExecution result set or empty if timeout
   * 
   * Specification (from Query D results): 
   * - Timeout: 30 seconds per endpoint
   * - User-Agent header required for politeness
   * - Exponential backoff on connection failure
   */
  SelectQueryResult executeSelectQuery(String endpoint, String selectQuery, int timeoutSeconds);
  
  /**
   * Load and cache endpoint list from Wikidata discovery.
   * 
   * Specification (from discoveryTest):
   * - Returns ~192 endpoints from Wikidata P5305 registry
   * - Caches for 24 hours
   * - Filters malformed URIs
   */
  List<String> discoverEndpoints();
}
