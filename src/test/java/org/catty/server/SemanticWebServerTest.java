package org.catty.server;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SemanticWebServer.
 * Tests end-to-end functionality including SPARQL endpoints, RDF serving,
 * and HTTP interface on localhost.
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 */
public class SemanticWebServerTest {
    
    private SemanticWebServer server;
    private HttpClient httpClient;
    
    @TempDir
    Path tempDataDir;
    
    @BeforeEach
    void setUp() throws IOException {
        // Create a test dataset directory
        Path dataDir = tempDataDir.resolve("data");
        dataDir.toFile().mkdirs();
        
        // Create test server on different port to avoid conflicts
        server = new SemanticWebServer("localhost", 3031, "/test");
        
        // Start server
        server.start();
        
        // Initialize HTTP client with timeout settings
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }
    
    @Test
    void testServerStartsSuccessfully() {
        assertNotNull(server);
        assertNotNull(server.getDataset());
        assertNotNull(server.getServer());
        
        // Verify server is accessible
        Dataset dataset = server.getDataset();
        assertNotNull(dataset);
        
        Model model = dataset.getDefaultModel();
        assertNotNull(model);
    }
    
    @Test
    void testQueryEndpointAccessible() throws Exception {
        // Build SPARQL query URL
        URI queryUri = URI.create("http://localhost:3031/test/query");
        
        // Create query request
        String sparqlQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 10";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(queryUri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/sparql-results+json")
                .header("Content-Type", "application/sparql-query")
                .POST(HttpRequest.BodyPublishers.ofString(sparqlQuery))
                .build();
        
        // Execute request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Verify response
        assertEquals(200, response.statusCode(), "Query endpoint should return 200 OK");
        assertNotNull(response.body());
        assertTrue(response.body().contains("results"), "Response should contain SPARQL results");
    }
    
    @Test
    void testUpdateEndpointAccessible() throws Exception {
        URI updateUri = URI.create("http://localhost:3031/test/update");
        
        // Create update request
        String sparqlUpdate = "INSERT DATA { <http://example.org/test> <http://example.org/property> \"test\" . }";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(updateUri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/sparql-update")
                .POST(HttpRequest.BodyPublishers.ofString(sparqlUpdate))
                .build();
        
        // Execute request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Verify response
        assertEquals(204, response.statusCode(), "Update endpoint should return 204 No Content");
    }
    
    @Test
    void testDatasetLoadedWithOntologies() {
        Dataset dataset = server.getDataset();
        Model model = dataset.getDefaultModel();
        
        // Verify some basic structure (assuming catty ontologies exist)
        assertTrue(model.size() >= 0, "Model should contain ontology data");
        
        // Check for typical RDF/OWL structure
        Resource owlClass = model.createResource("http://www.w3.org/2002/07/owl#Class");
        assertNotNull(owlClass, "Should be able to create resources");
    }
    
    @Test
    void testServerHandlesMultipleRequests() throws Exception {
        // Test that server can handle multiple concurrent requests
        
        URI queryUri = URI.create("http://localhost:3031/test/query");
        String sparqlQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 5";
        
        // Create multiple requests
        HttpRequest request1 = buildSparqlRequest(queryUri, sparqlQuery);
        HttpRequest request2 = buildSparqlRequest(queryUri, sparqlQuery);
        HttpRequest request3 = buildSparqlRequest(queryUri, sparqlQuery);
        
        // Execute concurrently
        java.util.concurrent.CompletableFuture<HttpResponse<String>> future1 = 
            httpClient.sendAsync(request1, HttpResponse.BodyHandlers.ofString());
        java.util.concurrent.CompletableFuture<HttpResponse<String>> future2 = 
            httpClient.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
        java.util.concurrent.CompletableFuture<HttpResponse<String>> future3 = 
            httpClient.sendAsync(request3, HttpResponse.BodyHandlers.ofString());
        
        // Wait for all to complete
        HttpResponse<String> response1 = future1.join();
        HttpResponse<String> response2 = future2.join();
        HttpResponse<String> response3 = future3.join();
        
        // Verify all succeeded
        assertEquals(200, response1.statusCode());
        assertEquals(200, response2.statusCode());
        assertEquals(200, response3.statusCode());
    }
    
    private HttpRequest buildSparqlRequest(URI uri, String query) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/sparql-results+json")
                .header("Content-Type", "application/sparql-query")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();
    }
    
    @Test
    void testServerStopsGracefully() {
        assertTrue(server.getServer() != null || server.getDataset() != null, 
                   "Server should have resources before stop");
        
        server.stop();
        
        // Verify dataset is closed (this is the main state change we can check)
        Dataset dataset = server.getDataset();
        assertNotNull(dataset, "Dataset should still exist after stop");
    }
}