package org.catty.sparql.federation.discovery;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.*;
import org.catty.sparql.federation.SelectQueryResult;
import org.catty.sparql.federation.impl.JenaSelectQueryExecutor;

/**
 * Handles discovery of SPARQL endpoints using Wikidata.
 */
public class WikidataEndpointDiscovery {
    private static final String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";
    private static final String DISCOVERY_QUERY = 
        "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
        "SELECT (SAMPLE(?graph) AS ?representativeQid) ?endpoint\n" +
        "WHERE {\n" +
        "  ?graph wdt:P5305 ?endpoint .\n" +
        "  FILTER (STRSTARTS(STR(?endpoint), \"http\") && \n" +
        "          !STRSTARTS(STR(?endpoint), \"http://wikiba.se/ontology#\") && \n" +
        "          !CONTAINS(STR(?endpoint), \"/.well-known/genid/\"))\n" +
        "}\n" +
        "GROUP BY ?endpoint\n" +
        "ORDER BY ?endpoint";

    private final JenaSelectQueryExecutor executor;

    public WikidataEndpointDiscovery() {
        this.executor = new JenaSelectQueryExecutor();
    }

    /**
     * Discovers endpoints from Wikidata.
     * @return List of endpoint URIs
     */
    public List<String> discover() {
        SelectQueryResult result = executor.executeWithTimeout(WIKIDATA_ENDPOINT, DISCOVERY_QUERY, 30);
        List<String> endpoints = new ArrayList<>();
        if (result.getRows() != null) {
            for (java.util.Map<String, String> row : result.getRows()) {
                endpoints.add(row.get("endpoint"));
            }
        }
        return endpoints;
    }
}
