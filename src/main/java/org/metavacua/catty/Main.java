package org.metavacua.catty;

import org.metavacua.catty.core.CrateGenerator;
import org.metavacua.catty.core.DiscoveryEngine;

import java.util.List;
import java.util.Map;

public class Main {
    private static final String DEFAULT_ENDPOINT = "https://query.wikidata.org/sparql";
    private static final String DEFAULT_QUERY = "queries/discovery.rq";
    private static final String DEFAULT_OUTPUT = "ro-crate-metadata.json";

    public static void main(String[] args) {
        String endpoint = System.getenv("SPARQL_ENDPOINT");
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = DEFAULT_ENDPOINT;
        }
        
        String queryPath = args.length > 0 ? args[0] : DEFAULT_QUERY;
        String outputPath = args.length > 1 ? args[1] : DEFAULT_OUTPUT;
        
        System.out.println("=== Catty Semantic Web Discovery ===");
        System.out.println("SPARQL Endpoint: " + endpoint);
        System.out.println("Query: " + queryPath);
        System.out.println("Output: " + outputPath);
        System.out.println();
        
        DiscoveryEngine engine = new DiscoveryEngine(endpoint);
        System.out.println("Executing discovery query...");
        List<Map<String, String>> results = engine.executeQuery(queryPath);
        
        System.out.println("Found " + results.size() + " results");
        
        if (results.isEmpty()) {
            System.out.println("WARNING: Empty result set. Creating minimal RO-Crate.");
        }
        
        CrateGenerator generator = new CrateGenerator();
        generator.generateROCrate(results, outputPath);
        
        System.out.println("\n=== Discovery Complete ===");
        System.out.println("RO-Crate written to: " + outputPath);
    }
}
