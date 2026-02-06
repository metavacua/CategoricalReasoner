package org.metavacua.catty;

import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RoCrateHelloWorld {
    private static final String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";
    private static final long TIMEOUT_MS = 60000; // 60 seconds

    public static void main(String[] args) {
        try {
            System.out.println("Catty: RO-Crate HelloWorld");
            System.out.println("Retrieving Research Object Crate information from Wikidata...");

            // Load SPARQL query from resources
            InputStream queryStream = RoCrateHelloWorld.class
                .getResourceAsStream("/wikidata-rocrate-query.rq");
            if (queryStream == null) {
                System.err.println("ERROR: Could not load SPARQL query file");
                System.exit(1);
            }

            String query = new BufferedReader(
                new InputStreamReader(queryStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            System.out.println();
            System.out.println("Executing SPARQL query against Wikidata...");

            // Create RDF connection with timeout and User-Agent
            RDFConnection conn = RDFConnectionFactory.connect(
                RDFConnectionFactory
                    .setHTTPTimeout(TIMEOUT_MS)
                    .setHTTPUserAgent("CattyRoCrateHelloWorld/1.0 (https://github.com/metavacua/CategoricalReasoner)")
                    .build(WIKIDATA_ENDPOINT));

            try {
                // Execute query
                QueryExecution qe = conn.query(query);
                ResultSet results = qe.execSelect();

                // Check for non-empty results
                if (!results.hasNext()) {
                    System.err.println("ERROR: No results retrieved from Wikidata");
                    System.err.println("Query may need adjustment or Wikidata data may be incomplete");
                    qe.close();
                    conn.close();
                    System.exit(1);
                }

                // Output results
                System.out.println();
                System.out.println("=== Retrieved Wikidata Entities ===");
                int count = 0;
                while (results.hasNext()) {
                    QuerySolution soln = results.next();
                    String item = soln.getResource("item").getURI();
                    String label = soln.getLiteral("itemLabel").getString();
                    String desc = soln.contains("itemDescription")
                        ? soln.getLiteral("itemDescription").getString()
                        : "No description available";

                    System.out.println();
                    System.out.println("QID: " + item);
                    System.out.println("Label: " + label);
                    System.out.println("Description: " + desc);
                    count++;
                }

                System.out.println();
                System.out.println("Total entities retrieved: " + count);
                System.out.println();
                System.out.println("RO-Crate HelloWorld complete.");

                qe.close();
                conn.close();
                System.exit(0);

            } catch (QueryExceptionHTTP e) {
                System.err.println("ERROR: HTTP error querying Wikidata endpoint");
                System.err.println("Status code: " + e.getResponseCode());
                System.err.println("Message: " + e.getMessage());
                conn.close();
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("ERROR: SPARQL query failed");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
