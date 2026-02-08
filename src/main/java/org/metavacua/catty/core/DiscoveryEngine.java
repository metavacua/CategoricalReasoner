package org.metavacua.catty.core;

import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(queryPath)) {
            if (is == null) {
                System.err.println("Query file not found: " + queryPath);
                return results;
            }
            
            String queryString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Query query = QueryFactory.create(queryString);
            
            try (RDFConnection conn = RDFConnectionRemote.service(endpoint).build()) {
                try (QueryExecution qexec = conn.query(query)) {
                    ResultSet rs = qexec.execSelect();
                    
                    while (rs.hasNext()) {
                        QuerySolution soln = rs.nextSolution();
                        Map<String, String> row = new HashMap<>();
                        
                        soln.varNames().forEachRemaining(varName -> {
                            if (soln.get(varName) != null) {
                                row.put(varName, soln.get(varName).toString());
                            }
                        });
                        
                        results.add(row);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Query execution failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
}
