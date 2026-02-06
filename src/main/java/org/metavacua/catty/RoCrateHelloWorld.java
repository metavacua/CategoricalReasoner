package org.metavacua.catty;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.http.HttpAuth;
import org.apache.jena.http.HttpEnv;
import org.apache.jena.http.SysRIOT;

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

            // Create query and set timeout using HTTP builder pattern
            System.out.println("Executing SPARQL query...");
            long startTime = System.currentTimeMillis();

            // Use HTTP builder pattern for robust, explicit timeout configuration
            QueryExecution qe = QueryExecutionHTTP.newBuilder()
                .endpoint(WIKIDATA_ENDPOINT)
                .query(QueryFactory.create(query))
                .httpClient(HttpEnv.getDftClient())
                .timeout(TIMEOUT_MS, TIMEOUT_MS)
                .build();

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
                    // Preserve language tags from SPARQL results
                    itemResource.addProperty(RDFS.label, model.createLiteral(label, "en"));
                    if (desc != null) {
                        itemResource.addProperty(model.createProperty("http://schema.org/description"),
                            model.createLiteral(desc, "en"));
                    }

                    count++;
                }

                System.out.println();
                System.out.println("Total entities retrieved: " + count);

                // Write model to Turtle format with provenance comments
                System.out.println();
                System.out.println("Writing results to " + OUTPUT_FILE + "...");
                File outputFile = new File(OUTPUT_FILE);

                // Compute query hash for provenance
                String queryHash = Integer.toHexString(query.hashCode());

                try (FileOutputStream out = new FileOutputStream(outputFile)) {
                    // Write provenance header as Turtle comments
                    String provenance = String.format(
                        "# ============================================\n" +
                        "# Catty RO-Crate HelloWorld - SPARQL Results\n" +
                        "# ============================================\n" +
                        "# Generated: %tF %<tT %<tz\n" +
                        "# Endpoint: %s\n" +
                        "# Query Execution Time: %d ms\n" +
                        "# Query Hash: %s\n" +
                        "# Entities Retrieved: %d\n" +
                        "# ============================================\n" +
                        "# This is a generated artifact from actual Wikidata execution.\n" +
                        "# Wikidata content may change over time.\n" +
                        "# For reproducibility, this file pins the exact results from this execution.\n" +
                        "# ============================================\n\n",
                        new java.util.Date(),
                        WIKIDATA_ENDPOINT,
                        queryTime,
                        queryHash,
                        count
                    );
                    out.write(provenance.getBytes(StandardCharsets.UTF_8));

                    // Write RDF model
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
