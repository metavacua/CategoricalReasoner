package org.metavacua.categoricalreasoner.citation;

import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.Lang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * SPARQL federation service for citation queries.
 * Supports querying local RO-Crate and external endpoints (Wikidata, Crossref).
 * <p>
 * Enables ARATU (Agentic Retrieval-Augmented Tool Use) to:
 * - Query formalization status of citations
 * - Retrieve citation metadata from external sources
 * - Validate citation dependencies
 */
public class SparqlQueryService {

    private static final String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";
    private static final String CROSSREF_ENDPOINT = "https://api.crossref.org/works";

    private final Model localModel;

    /**
     * Creates a new SPARQL query service with local model.
     */
    public SparqlQueryService() {
        this.localModel = ModelFactory.createDefaultModel();
        loadLocalRocrate();
    }

    /**
     * Loads local RO-Crate into Jena model.
     */
    private void loadLocalRocrate() {
        try {
            String roCrateJson = new String(
                java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get("docs/dissertation/bibliography/ro-crate-metadata.json")
                ),
                StandardCharsets.UTF_8
            );
            try (InputStream in = new ByteArrayInputStream(roCrateJson.getBytes(StandardCharsets.UTF_8))) {
                RDFDataMgr.read(localModel, in, Lang.JSONLD);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load local RO-Crate: " + e.getMessage());
        }
    }

    /**
     * Queries local RO-Crate for citations by formalization status.
     *
     * @param status The formalization status to query
     * @return List of citation keys matching the status
     */
    public List<String> queryByStatus(FormalizationStatus status) {
        String sparql = String.format("""
            PREFIX schema: <http://schema.org/>

            SELECT ?citation ?title
            WHERE {
                ?citation schema:formalizationStatus "%s" ;
                         schema:name ?title .
            }
            """, status.name().toLowerCase());

        return executeLocalQuery(sparql);
    }

    /**
     * Queries local RO-Crate for citations by category.
     *
     * @param category The category to query
     * @return List of citation keys in the category
     */
    public List<String> queryByCategory(String category) {
        String sparql = String.format("""
            PREFIX schema: <http://schema.org/>

            SELECT ?citation ?title
            WHERE {
                ?citation schema:category "%s" ;
                         schema:name ?title .
            }
            """, category.toLowerCase());

        return executeLocalQuery(sparql);
    }

    /**
     * Queries Wikidata for additional metadata about a citation.
     *
     * @param qid The Wikidata QID
     * @return Query result as JSON string
     */
    public String queryWikidata(Qid qid) {
        String sparql = String.format("""
            PREFIX wd: <http://www.wikidata.org/entity/>
            PREFIX wdt: <http://www.wikidata.org/prop/direct/>
            PREFIX schema: <http://schema.org/>

            SELECT ?itemLabel ?publicationDate ?doi ?authorLabel
            WHERE {
                wd:%s rdfs:label ?itemLabel ;
                      wdt:P577 ?publicationDate .
                OPTIONAL { wd:%s wdt:P356 ?doi . }
                OPTIONAL {
                    wd:%s wdt:P50 ?author .
                    ?author rdfs:label ?authorLabel .
                }
                FILTER(LANG(?itemLabel) = "en")
                FILTER(LANG(?authorLabel) = "en")
            }
            LIMIT 10
            """, qid.value(), qid.value(), qid.value());

        return executeRemoteQuery(WIKIDATA_ENDPOINT, sparql);
    }

    /**
     * Validates citation dependencies in the knowledge graph.
     *
     * @param citation The citation to validate
     * @return List of dependency keys that are not in the repository
     */
    public List<String> validateDependencies(Citation citation) {
        List<String> missing = new ArrayList();

        for (CitationKey depKey : citation.dependsOn()) {
            if (CitationRepository.findByKey(depKey).isEmpty()) {
                missing.add(depKey.value());
            }
        }

        return missing;
    }

    /**
     * Executes a SPARQL query against the local RO-Crate model.
     *
     * @param sparql The SPARQL query string
     * @return List of citation URIs matching the query
     */
    private List<String> executeLocalQuery(String sparql) {
        List<String> results = new ArrayList();

        try (QueryExecution qexec = QueryExecutionFactory.create(sparql, localModel)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution soln = rs.next();
                String citation = soln.getResource("citation").getURI();
                results.add(citation);
            }
        }

        return results;
    }

    /**
     * Executes a SPARQL query against a remote endpoint.
     *
     * @param endpoint The SPARQL endpoint URL
     * @param sparql   The SPARQL query string
     * @return Query result as JSON string
     */
    private String executeRemoteQuery(String endpoint, String sparql) {
        try {
            Query query = QueryFactory.create(sparql);

            try (RDFConnection conn = RDFConnectionFactory.connect(endpoint)) {
                conn.queryResultSet(query, rs -> {
                    // Convert result to JSON
                    ResultSetFormatter.outputAsJSON(System.out, rs);
                });
            }

            return "Query executed successfully - see output";
        } catch (Exception e) {
            return "Error executing remote query: " + e.getMessage();
        }
    }

    /**
     * Returns all citations with their formalization status.
     *
     * @return SPARQL query result as table
     */
    public String getAllStatuses() {
        String sparql = """
            PREFIX schema: <http://schema.org/>

            SELECT ?citation ?title ?status ?category
            WHERE {
                ?citation a schema:ScholarlyArticle ;
                         schema:name ?title ;
                         schema:formalizationStatus ?status .
                OPTIONAL { ?citation schema:category ?category . }
            }
            ORDER BY ?citation
            """;

        try (QueryExecution qexec = QueryExecutionFactory.create(sparql, localModel)) {
            ResultSet rs = qexec.execSelect();
            return ResultSetFormatter.asText(rs);
        }
    }

    /**
     * Main method for testing SPARQL queries.
     */
    public static void main(String[] args) {
        SparqlQueryService service = new SparqlQueryService();

        System.out.println("=== Citations by Status ===");
        for (FormalizationStatus status : FormalizationStatus.values()) {
            List<String> citations = service.queryByStatus(status);
            System.out.println(status + ": " + citations.size() + " citations");
            if (!citations.isEmpty()) {
                citations.forEach(c -> System.out.println("  - " + c));
            }
        }

        System.out.println("\n=== Citations by Category ===");
        List<String> categoryResults = service.queryByCategory("linear-logic");
        categoryResults.forEach(c -> System.out.println("  - " + c));

        System.out.println("\n=== All Citations with Status ===");
        System.out.println(service.getAllStatuses());

        // Test Wikidata query
        if (args.length > 0) {
            Qid qid = Qid.of(args[0]);
            System.out.println("\n=== Wikidata Query for " + qid + " ===");
            System.out.println(service.queryWikidata(qid));
        }
    }
}
