package org.metavacua.catty;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RoCrateHelloWorld {
    private static final String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";
    private static final long TIMEOUT_MS = 60000; // 60 seconds
    private static final String OUTPUT_FILE = "wikidata-rocrate-results.ttl";

    public static void main(String[] args) {
        try {
            System.out.println("=== Catty: RO-Crate SPARQL Query Execution ===");
            System.out.println("Endpoint: " + WIKIDATA_ENDPOINT);
            System.out.println("Timeout: " + TIMEOUT_MS + "ms");
            System.out.println();

            // Load SPARQL query from resources
            System.out.println("Loading SPARQL query...");
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

            System.out.println("Query loaded successfully.");
            System.out.println();

            // Create query and set timeout
            System.out.println("Executing SPARQL query...");
            long startTime = System.currentTimeMillis();

            QueryExecution qe = QueryExecutionFactory.sparqlService(
                WIKIDATA_ENDPOINT,
                QueryFactory.create(query),
                null,
                TIMEOUT_MS,
                TIMEOUT_MS
            );

            try {
                ResultSet results = qe.execSelect();

                long queryTime = System.currentTimeMillis() - startTime;
                System.out.println("Query executed in " + queryTime + "ms");

                // Check for non-empty results
                if (!results.hasNext()) {
                    System.err.println("ERROR: No results retrieved from Wikidata");
                    System.err.println("Query may need adjustment or Wikidata data may be incomplete");
                    qe.close();
                    System.exit(1);
                }

                // Create RDF model for output
                System.out.println();
                System.out.println("=== Processing Results ===");
                Model model = ModelFactory.createDefaultModel();
                model.setNsPrefix("wd", "http://www.wikidata.org/entity/");
                model.setNsPrefix("schema", "http://schema.org/");
                model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

                // Process results and add to model
                int count = 0;
                while (results.hasNext()) {
                    QuerySolution soln = results.next();
                    String itemUri = soln.getResource("item").getURI();
                    String label = soln.getLiteral("itemLabel").getString();
                    String desc = soln.contains("itemDescription")
                        ? soln.getLiteral("itemDescription").getString()
                        : null;

                    System.out.println();
                    System.out.println("Entity #" + (count + 1) + ":");
                    System.out.println("  URI: " + itemUri);
                    System.out.println("  Label: " + label);
                    if (desc != null) {
                        System.out.println("  Description: " + desc);
                    }

                    // Create RDF resource and add properties
                    Resource itemResource = model.createResource(itemUri);
                    itemResource.addProperty(RDF.type, model.createResource("http://schema.org/Thing"));
                    itemResource.addProperty(RDFS.label, label);
                    if (desc != null) {
                        itemResource.addProperty(model.createProperty("http://schema.org/description"), desc);
                    }

                    count++;
                }

                System.out.println();
                System.out.println("Total entities retrieved: " + count);

                // Write model to Turtle format
                System.out.println();
                System.out.println("Writing results to " + OUTPUT_FILE + "...");
                File outputFile = new File(OUTPUT_FILE);
                try (FileOutputStream out = new FileOutputStream(outputFile)) {
                    model.write(out, "TURTLE");
                }
                System.out.println("Results successfully written to " + outputFile.getAbsolutePath());

                // Output Turtle to console for verification
                System.out.println();
                System.out.println("=== Turtle Output ===");
                model.write(System.out, "TURTLE");

                System.out.println();
                System.out.println("=== Execution Complete ===");
                System.out.println("Query time: " + queryTime + "ms");
                System.out.println("Entities processed: " + count);
                System.out.println("Output file: " + outputFile.getAbsolutePath());

                qe.close();
                System.exit(0);

            } catch (QueryExceptionHTTP e) {
                System.err.println();
                System.err.println("ERROR: HTTP error querying Wikidata endpoint");
                System.err.println("Message: " + e.getMessage());
                System.err.println("Exception class: " + e.getClass().getName());
                qe.close();
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println();
            System.err.println("ERROR: SPARQL query failed");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
