package org.catty.sparql.federation;

import java.util.List;
import java.util.Map;

/**
 * Result container for SELECT query execution.
 * 
 * Specification (from Query C documentation standards):
 * - Immutable result object
 * - Structured rows and variable names
 * - Metadata: endpoint, query, execution time, timeout status
 * 
 * Test case: SelectQueryResultTest.testStructure()
 */
public class SelectQueryResult {
  private final String endpoint;
  private final String query;
  private final List<String> variables;
  private final List<Map<String, String>> rows;
  private final long executionTimeMs;
  private final boolean timedOut;
  private final Throwable error;

  private SelectQueryResult(Builder builder) {
    this.endpoint = builder.endpoint;
    this.query = builder.query;
    this.variables = builder.variables;
    this.rows = builder.rows;
    this.executionTimeMs = builder.executionTimeMs;
    this.timedOut = builder.timedOut;
    this.error = builder.error;
  }

  public String getEndpoint() { return endpoint; }
  public String getQuery() { return query; }
  public List<String> getVariables() { return variables; }
  public List<Map<String, String>> getRows() { return rows; }
  public long getExecutionTimeMs() { return executionTimeMs; }
  public boolean isTimedOut() { return timedOut; }
  public Throwable getError() { return error; }

  public static class Builder {
    private String endpoint;
    private String query;
    private List<String> variables;
    private List<Map<String, String>> rows;
    private long executionTimeMs;
    private boolean timedOut;
    private Throwable error;

    public Builder endpoint(String endpoint) { this.endpoint = endpoint; return this; }
    public Builder query(String query) { this.query = query; return this; }
    public Builder variables(List<String> variables) { this.variables = variables; return this; }
    public Builder rows(List<Map<String, String>> rows) { this.rows = rows; return this; }
    public Builder executionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; return this; }
    public Builder timedOut(boolean timedOut) { this.timedOut = timedOut; return this; }
    public Builder error(Throwable error) { this.error = error; return this; }

    public SelectQueryResult build() {
      return new SelectQueryResult(this);
    }
  }
}
