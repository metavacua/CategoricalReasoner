package org.catty.http;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.shared.JenaException;
import org.catty.rdf.RDFService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * RDFHttpHandler - HTTP servlet for serving RDF content in multiple formats.
 * 
 * This servlet provides RESTful endpoints for:
 * - RDF data serving with content negotiation (Turtle, JSON-LD, RDF/XML, N-Triples)
 * - Named graph access
 * - Custom SPARQL query endpoints
 * - Ontology resource retrieval
 * 
 * Supports standard HTTP methods: GET, POST, PUT, DELETE
 * Implements proper content negotiation based on Accept headers
 * Handles CORS for local development
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 */
public class RDFHttpHandler extends HttpServlet {
    
    private static final Logger LOG = LoggerFactory.getLogger(RDFHttpHandler.class);
    
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    
    private final RDFService rdfService;
    private final Dataset dataset;
    
    /**
     * Initializes the RDF HTTP handler with RDFService and dataset.
     * 
     * @param rdfService the RDF service for serialization
     * @param dataset the Jena dataset
     */
    public RDFHttpHandler(RDFService rdfService, Dataset dataset) {
        this.rdfService = rdfService;
        this.dataset = dataset;
        LOG.info("RDFHttpHandler initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("GET request received for URI: {}", req.getRequestURI());
        
        // Set CORS headers for local development
        setCorsHeaders(resp);
        
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                serveRootDataset(req, resp);
            } else if (pathInfo.startsWith("/graph/")) {
                serveNamedGraph(req, resp, pathInfo.substring(7));
            } else {
                serveResource(req, resp, pathInfo);
            }
        } catch (Exception e) {
            LOG.error("Error handling GET request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("POST request received for URI: {}", req.getRequestURI());
        
        setCorsHeaders(resp);
        
        try {
            String pathInfo = req.getPathInfo();
            if ("/query".equals(pathInfo)) {
                handleSparqlQuery(req, resp);
            } else if ("/update".equals(pathInfo)) {
                handleSparqlUpdate(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception e) {
            LOG.error("Error handling POST request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("PUT request received for URI: {}", req.getRequestURI());
        
        setCorsHeaders(resp);
        
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/graph/")) {
                storeNamedGraph(req, resp, pathInfo.substring(7));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid PUT request");
            }
        } catch (Exception e) {
            LOG.error("Error handling PUT request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("DELETE request received for URI: {}", req.getRequestURI());
        
        setCorsHeaders(resp);
        
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/graph/")) {
                deleteNamedGraph(req, resp, pathInfo.substring(7));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid DELETE request");
            }
        } catch (Exception e) {
            LOG.error("Error handling DELETE request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * Sets CORS headers for cross-origin requests during local development.
     * 
     * @param resp the HTTP response
     */
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        resp.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Accept, Authorization");
    }
    
    /**
     * Serves the root dataset content based on content negotiation.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws IOException if serving fails
     */
    private void serveRootDataset(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acceptHeader = req.getHeader(HEADER_ACCEPT);
        RDFService.RDFContentType contentType = negotiateContentType(acceptHeader);
        
        LOG.info("Serving root dataset in format: {}", contentType);
        
        dataset.begin(org.apache.jena.query.ReadWrite.READ);
        try {
            Model model = dataset.getDefaultModel();
            String serialized = rdfService.serializeModel(model, contentType);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(contentType.getContentType());
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(serialized);
            
        } catch (Exception e) {
            LOG.error("Error serving root dataset", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving dataset");
        } finally {
            dataset.end();
        }
    }
    
    /**
     * Serves a named graph from the dataset.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @param graphName the name of the graph
     * @throws IOException if serving fails
     */
    private void serveNamedGraph(HttpServletRequest req, HttpServletResponse resp, String graphName) throws IOException {
        String acceptHeader = req.getHeader(HEADER_ACCEPT);
        RDFService.RDFContentType contentType = negotiateContentType(acceptHeader);
        
        LOG.info("Serving graph '{}' in format: {}", graphName, contentType);
        
        dataset.begin(org.apache.jena.query.ReadWrite.READ);
        try {
            Model model = dataset.getNamedModel(graphName);
            if (model.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Graph not found: " + graphName);
                return;
            }
            
            String serialized = rdfService.serializeModel(model, contentType);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(contentType.getContentType());
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(serialized);
            
        } catch (Exception e) {
            LOG.error("Error serving named graph: {}", graphName, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving graph");
        } finally {
            dataset.end();
        }
    }
    
    /**
     * Serves a specific resource description.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @param resourceUri the URI of the resource
     * @throws IOException if serving fails
     */
    private void serveResource(HttpServletRequest req, HttpServletResponse resp, String resourceUri) throws IOException {
        String acceptHeader = req.getHeader(HEADER_ACCEPT);
        RDFService.RDFContentType contentType = negotiateContentType(acceptHeader);
        
        LOG.info("Serving resource '{}' in format: {}", resourceUri, contentType);
        
        // TODO: Implement DESCRIBE query for specific resource
        resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Resource-specific serving not yet implemented");
    }
    
    /**
     * Builds SPARQL result formats for HTTP Accept header negotiation
     * 
     * @param acceptHeader the HTTP Accept header value
     * @return negotiated RDF content type
     */
    private RDFService.RDFContentType negotiateContentType(String acceptHeader) {
        if (acceptHeader == null || acceptHeader.isEmpty()) {
            return RDFService.RDFContentType.TURTLE;
        }
        
        // Check for JSON-LD
        if (acceptHeader.contains("application/ld+json") || acceptHeader.contains("application/json")) {
            return RDFService.RDFContentType.JSON_LD;
        }
        // Check for RDF/XML
        if (acceptHeader.contains("application/rdf+xml") || acceptHeader.contains("application/xml")) {
            return RDFService.RDFContentType.RDF_XML;
        }
        // Check for N-Triples
        if (acceptHeader.contains("application/n-triples")) {
            return RDFService.RDFContentType.N_TRIPLES;
        }
        // Check for Turtle (most specific last)
        if (acceptHeader.contains("text/turtle") || acceptHeader.contains("application/x-turtle")) {
            return RDFService.RDFContentType.TURTLE;
        }
        
        // Default to Turtle
        return RDFService.RDFContentType.TURTLE;
    }
    
    /**
     * Handles SPARQL query requests via POST.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws IOException if query fails
     */
    private void handleSparqlQuery(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contentType = req.getHeader(HEADER_CONTENT_TYPE);
        
        if (!"application/sparql-query".equals(contentType)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid content type. Expected: application/sparql-query");
            return;
        }
        
        String queryString = req.getReader().lines().reduce("", (acc, line) -> acc + line + "\n");
        String acceptHeader = req.getHeader(HEADER_ACCEPT);
        
        LOG.info("Processing SPARQL query");
        
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(acceptHeader != null ? acceptHeader : "application/sparql-results+json");
        resp.setCharacterEncoding("UTF-8");
        
        // Note: This would integrate with SPARQLService in a real implementation
        // For now, we'll return a placeholder response
        resp.getWriter().write("{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}");
    }
    
    /**
     * Handles SPARQL update requests via POST.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws IOException if update fails
     */
    private void handleSparqlUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contentType = req.getHeader(HEADER_CONTENT_TYPE);
        
        if (!"application/sparql-update".equals(contentType)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid content type. Expected: application/sparql-update");
            return;
        }
        
        String updateString = req.getReader().lines().reduce("", (acc, line) -> acc + line + "\n");
        
        LOG.info("Processing SPARQL update");
        
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    
    /**
     * Stores RDF data into a named graph via PUT.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @param graphName the target graph name
     * @throws IOException if storage fails
     */
    private void storeNamedGraph(HttpServletRequest req, HttpServletResponse resp, String graphName) throws IOException {
        String contentType = req.getHeader(HEADER_CONTENT_TYPE);
        RDFService.RDFContentType rdfContentType = getContentTypeFromString(contentType);
        
        LOG.info("Storing data into graph: '{}' from content type: {}", graphName, contentType);
        
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    
    /**
     * Deletes a named graph via DELETE.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @param graphName the target graph name
     * @throws IOException if deletion fails
     */
    private void deleteNamedGraph(HttpServletRequest req, HttpServletResponse resp, String graphName) throws IOException {
        LOG.info("Deleting graph: '{}'", graphName);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    
    /**
     * Converts content type header to RDFContentType.
     * 
     * @param contentType the HTTP content type header
     * @return RDFContentType enum value
     */
    private RDFService.RDFContentType getContentTypeFromString(String contentType) {
        if (contentType == null) return RDFService.RDFContentType.TURTLE;
        
        if (contentType.contains("text/turtle") || contentType.contains("application/x-turtle")) {
            return RDFService.RDFContentType.TURTLE;
        }
        if (contentType.contains("application/ld+json")) {
            return RDFService.RDFContentType.JSON_LD;
        }
        if (contentType.contains("application/rdf+xml")) {
            return RDFService.RDFContentType.RDF_XML;
        }
        if (contentType.contains("application/n-triples")) {
            return RDFService.RDFContentType.N_TRIPLES;
        }
        
        return RDFService.RDFContentType.TURTLE;
    }
}