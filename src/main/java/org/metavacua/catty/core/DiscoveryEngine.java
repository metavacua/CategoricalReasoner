package org.metavacua.catty.core;

import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryEngine {
    private final String endpoint;

    public DiscoveryEngine(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Map<String, String>> executeQuery(String queryPath) {
        List<Map<String, String>> results = new ArrayList<>();
        
        System.out.println("[DiscoveryEngine] Loading query from: " + queryPath);
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(queryPath)) {
            if (is == null) {
                System.err.println("[ERROR] Query file not found: " + queryPath);
                return results;
            }
            
            String queryString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("[DiscoveryEngine] Query loaded successfully");
            System.out.println("=== SPARQL QUERY ===");
            System.out.println(queryString);
            System.out.println("=== END QUERY ===");
            System.out.println();
            
            Query query = QueryFactory.create(queryString);
            System.out.println("[DiscoveryEngine] Query parsed successfully");
            System.out.println("[DiscoveryEngine] Connecting to endpoint: " + endpoint);
            
            long startTime = System.currentTimeMillis();
            Instant startInstant = Instant.now();
            
            try (RDFConnection conn = RDFConnectionRemote.service(endpoint)
                    .acceptHeaderSelectQuery("application/sparql-results+json,application/sparql-results+xml;q=0.9,*/*;q=0.8")
                    .build()) {
                
                System.out.println("[DiscoveryEngine] Connection established");
                System.out.println("[DiscoveryEngine] Query start time: " + startInstant);
                
                try (QueryExecution qexec = conn.query(query)) {
                    System.out.println("[DiscoveryEngine] Executing query...");
                    
                    ResultSet rs = qexec.execSelect();
                    System.out.println("[DiscoveryEngine] Query executed, processing results...");
                    
                    int rowCount = 0;
                    while (rs.hasNext()) {
                        QuerySolution soln = rs.nextSolution();
                        Map<String, String> row = new HashMap<>();
                        final int currentRow = rowCount;
                        
                        soln.varNames().forEachRemaining(varName -> {
                            if (soln.get(varName) != null) {
                                String value = soln.get(varName).toString();
                                row.put(varName, value);
                                if (currentRow < 5) {
                                    System.out.println("  [Result " + currentRow + "] " + varName + " = " + value);
                                }
                            }
                        });
                        
                        results.add(row);
                        rowCount++;
                    }
                    
                    long endTime = System.currentTimeMillis();
                    Instant endInstant = Instant.now();
                    long duration = endTime - startTime;
                    
                    System.out.println("[DiscoveryEngine] Query completed");
                    System.out.println("[DiscoveryEngine] Query end time: " + endInstant);
                    System.out.println("[DiscoveryEngine] Duration: " + duration + " ms");
                    System.out.println("[DiscoveryEngine] Total results: " + results.size());
                    
                    if (results.isEmpty()) {
                        System.out.println("[WARNING] Empty result set returned from endpoint");
                        System.out.println("[WARNING] This may indicate:");
                        System.out.println("  - Query syntax is valid but semantically incorrect for this endpoint");
                        System.out.println("  - No data matches the query criteria");
                        System.out.println("  - Endpoint may not support the vocabularies used");
                        System.out.println("  - Network/rate limiting issues");
                    }
                }
            }
        } catch (QueryParseException e) {
            System.err.println("[ERROR] SPARQL Query Parse Error: " + e.getMessage());
            System.err.println("[ERROR] Line " + e.getLine() + ", Column " + e.getColumn());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Query execution failed: " + e.getMessage());
            System.err.println("[ERROR] Exception type: " + e.getClass().getName());
            e.printStackTrace();
            
            if (e.getCause() != null) {
                System.err.println("[ERROR] Caused by: " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
        }
        
        return results;
    }
}
