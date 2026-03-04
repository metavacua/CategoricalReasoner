package org.catty.sparql.federation;

import java.util.Map;

/**
 * Abstract base for SELECT query execution with timeout and polite request handling.
 * 
 * Concrete implementations handle:
 * - Jena QueryExecution wrapping
 * - Timeout enforcement
 * - Rate limiting (from Query D results)
 * - User-Agent headers
 * - Result collection and validation
 * 
 * Specification (from Query B + Query C results):
 * - Each method is test-driven (JUnit test exists first)
 * - Full Javadoc specification before implementation
 * - Timeout pattern from Query D recommendations
 */
public abstract class SelectQueryExecutor {
  
  /**
   * Execute SELECT query with enforced timeout.
   * 
   * Pseudocode:
   * 1. Create Jena QueryExecution from endpoint + query string
   * 2. Set timeout to {@code timeoutSeconds} (default 30)
   * 3. Set User-Agent header for politeness
   * 4. Execute query
   * 5. Collect ResultSet into memory
   * 6. Close execution
   * 7. Return structured result or empty if timeout
   * 
   * @param endpoint SPARQL endpoint URI
   * @param query SPARQL SELECT query
   * @param timeoutSeconds timeout in seconds
   * @return SelectQueryResult with rows or error status
   * 
   * Test case: SelectQueryExecutorTest.testTimeoutEnforcement()
   */
  public abstract SelectQueryResult executeWithTimeout(String endpoint, String query, int timeoutSeconds);
  
  /**
   * Rate-limit requests to endpoint per Query D recommendations.
   * 
   * Pseudocode (from Query D results):
   * 1. Retrieve recommended rate limit from query results
   * 2. Track request timestamps per endpoint
   * 3. Sleep if necessary to respect rate limit
   * 4. Execute request
   * 
   * Test case: SelectQueryExecutorTest.testRateLimiting()
   */
  protected abstract void applyRateLimit(String endpoint);
  
  /**
   * Polite request headers per Query D recommendations.
   * 
   * Pseudocode:
   * 1. Set User-Agent: "CattySelectQueryFederation/1.0"
   * 2. Set Accept: "application/sparql-results+json"
   * 3. Set custom headers from endpoint whitelist
   * 
   * Test case: SelectQueryExecutorTest.testPoliteHeaders()
   */
  protected abstract Map<String, String> createPoliteHeaders();
}
