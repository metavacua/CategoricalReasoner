package org.catty.sparql.federation;

import org.catty.sparql.federation.discovery.WikidataEndpointDiscovery;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class WikidataEndpointDiscoveryTest {

    @Test
    public void testDiscoveryQueryExecutes() {
        WikidataEndpointDiscovery discovery = new WikidataEndpointDiscovery();
        List<String> endpoints = discovery.discover();
        assertNotNull("Endpoints list should not be null", endpoints);
        assertFalse("Endpoints list should not be empty", endpoints.isEmpty());
    }

    @Test
    public void testEndpointCountApproximate() {
        WikidataEndpointDiscovery discovery = new WikidataEndpointDiscovery();
        List<String> endpoints = discovery.discover();
        // Wikidata documented ~192 endpoints. Let's allow some margin.
        assertTrue("Should return a significant number of endpoints. Found: " + endpoints.size(), 
                   endpoints.size() > 150);
    }

    @Test
    public void testEndpointFormatValidity() {
        WikidataEndpointDiscovery discovery = new WikidataEndpointDiscovery();
        List<String> endpoints = discovery.discover();
        for (String endpoint : endpoints) {
            assertTrue("Endpoint should be a valid HTTP(S) URI: " + endpoint, 
                       endpoint.startsWith("http://") || endpoint.startsWith("https://"));
        }
    }
}
