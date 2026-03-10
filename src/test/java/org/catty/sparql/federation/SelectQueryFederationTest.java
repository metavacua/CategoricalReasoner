package org.catty.sparql.federation;

import org.catty.sparql.federation.impl.JenaSelectQueryFederation;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class SelectQueryFederationTest {

    @Test
    public void testEndpointDiscoveryAndQueryExecution() {
        JenaSelectQueryFederation federation = new JenaSelectQueryFederation();
        List<String> endpoints = federation.discoverEndpoints();
        assertNotNull(endpoints);
        assertFalse(endpoints.isEmpty());
        
        // Test first endpoint with a simple query
        String endpoint = "https://query.wikidata.org/sparql"; 
        SelectQueryResult result = federation.executeSelectQuery(endpoint, "SELECT (count(*) as ?count) WHERE { ?s ?p ?o }", 30);
        assertNotNull(result);
        assertFalse(result.getRows().isEmpty());
    }
}
