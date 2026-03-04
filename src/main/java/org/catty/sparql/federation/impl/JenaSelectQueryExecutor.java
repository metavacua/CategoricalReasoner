package org.catty.sparql.federation.impl;

import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.catty.sparql.federation.SelectQueryExecutor;
import org.catty.sparql.federation.SelectQueryResult;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class JenaSelectQueryExecutor extends SelectQueryExecutor {

    private static final String USER_AGENT = "CattySelectQueryFederation/1.0";
    private final Map<String, Long> lastRequestTime = new HashMap<>();
    private static final long MIN_DELAY_MS = 2000; // 2 seconds between requests to the same endpoint

    @Override
    public SelectQueryResult executeWithTimeout(String endpoint, String query, int timeoutSeconds) {
        applyRateLimit(endpoint);
        
        long startTime = System.currentTimeMillis();
        SelectQueryResult.Builder builder = new SelectQueryResult.Builder()
                .endpoint(endpoint)
                .query(query);

        // In Jena 4.x, we use QueryExecution.service() for fluent API
        try (QueryExecution qExec = QueryExecution.service(endpoint)
                .query(query)
                .timeout(timeoutSeconds, TimeUnit.SECONDS)
                .build()) {
            
            // Set User-Agent via System properties or ARQ context if needed, 
            // but Wikidata really wants it.
            // For Jena 4.10.0, we can use the custom httpClient but it's more complex.
            // Let's try setting it in the context for this execution.
            qExec.getContext().set(org.apache.jena.sparql.util.Symbol.create("httpHeader:User-Agent"), USER_AGENT);
            
            ResultSet rs = qExec.execSelect();
            List<String> vars = rs.getResultVars();
            builder.variables(vars);
            
            List<Map<String, String>> rows = new ArrayList<>();
            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                Map<String, String> row = new HashMap<>();
                for (String var : vars) {
                    if (soln.contains(var)) {
                        row.put(var, soln.get(var).toString());
                    }
                }
                rows.add(row);
            }
            builder.rows(rows);
            builder.timedOut(false);
        } catch (QueryExceptionHTTP e) {
            if (e.getStatusCode() == 408 || e.getMessage().contains("timeout")) {
                builder.timedOut(true);
            }
            builder.error(e);
        } catch (Exception e) {
            builder.error(e);
            if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                builder.timedOut(true);
            }
        } finally {
            builder.executionTimeMs(System.currentTimeMillis() - startTime);
        }

        return builder.build();
    }

    @Override
    protected void applyRateLimit(String endpoint) {
        synchronized (lastRequestTime) {
            Long lastTime = lastRequestTime.get(endpoint);
            if (lastTime != null) {
                long elapsed = System.currentTimeMillis() - lastTime;
                if (elapsed < MIN_DELAY_MS) {
                    try {
                        Thread.sleep(MIN_DELAY_MS - elapsed);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            lastRequestTime.put(endpoint, System.currentTimeMillis());
        }
    }

    @Override
    protected Map<String, String> createPoliteHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", USER_AGENT);
        headers.put("Accept", "application/sparql-results+json");
        return headers;
    }
}
