package org.catty.http;

import org.apache.jena.query.Dataset;
import org.catty.rdf.RDFService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * SemanticWebHttpServer - Standalone HTTP server for RDF serving and SPARQL endpoints.
 * 
 * This server provides a localhost-first development environment with:
 * - HTTP endpoints for RDF content serving (multiple formats via content negotiation)
 * - SPARQL query and update endpoints
 * - Named graph management
 * - CORS support for local development
 * - Integration with Jetty for robust HTTP handling
 * 
 * Primary use case: Local development and testing of semantic web applications
 * without requiring external deployment.
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 */
public class SemanticWebHttpServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SemanticWebHttpServer.class);
    
    /**
     * Default hostname for localhost deployment
     */
    public static final String DEFAULT_HOST = "localhost";
    
    /**
     * Default HTTP port for the semantic web server
     */
    public static final int DEFAULT_PORT = 8080;
    
    /**
     * Default context path for RDF endpoints
     */
    public static final String DEFAULT_CONTEXT = "/rdf";
    
    private final String host;
    private final int port;
    private final String contextPath;
    private final Dataset dataset;
    private final RDFService rdfService;
    private Server server;
    
    /**
     * Initializes HTTP server with default settings.
     * 
     * @param dataset the Jena dataset to serve
     */
    public SemanticWebHttpServer(Dataset dataset) {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_CONTEXT, dataset);
    }
    
    /**
     * Initializes HTTP server with custom settings.
     * 
     * @param host the hostname to bind to
     * @param port the port number to listen on
     * @param contextPath the context path for endpoints
     * @param dataset the Jena dataset to serve
     */
    public SemanticWebHttpServer(String host, int port, String contextPath, Dataset dataset) {
        this.host = host;
        this.port = port;
        this.contextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
        this.dataset = dataset;
        this.rdfService = new RDFService(dataset);
        LOG.info("SemanticWebHttpServer initialized for {}:{}{}", host, port, contextPath);
    }
    
    /**
     * Configures and starts the HTTP server.
     * 
     * @throws IOException if server cannot be started
     */
    public void start() throws IOException {
        LOG.info("Starting Semantic Web HTTP Server on http://{}:{}{}", host, port, contextPath);
        
        try {
            server = new Server();
            
            // Configure HTTP connector
            ServerConnector http = new ServerConnector(server, new HttpConnectionFactory());
            http.setHost(host);
            http.setPort(port);
            http.setIdleTimeout(30000);
            
            Connector[] connectors = new Connector[]{http};
            server.setConnectors(connectors);
            
            // Configure servlet context
            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath("/");
            server.setHandler(context);
            
            // Add RDF HTTP handler
            RDFHttpHandler rdfHandler = new RDFHttpHandler(rdfService, dataset);
            ServletHolder rdfServletHolder = new ServletHolder(rdfHandler);
            rdfServletHolder.setName("RDFHandler");
            
            context.addServlet(rdfServletHolder, contextPath + "/*");
            
            // Start server
            server.start();
            
            LOG.info("Semantic Web HTTP Server started successfully");
            LOG.info("RDF endpoint: http://{}:{}{}/", host, port, contextPath);
            LOG.info("Query endpoint: http://{}:{}{}/query", host, port, contextPath);
            LOG.info("Update endpoint: http://{}:{}{}/update", host, port, contextPath);
            
        } catch (Exception e) {
            LOG.error("Failed to start HTTP server", e);
            throw new IOException("Failed to start HTTP server: " + e.getMessage(), e);
        }
    }
    
    /**
     * Stops the HTTP server gracefully.
     * 
     * @throws IOException if server cannot be stopped
     */
    public void stop() throws IOException {
        LOG.info("Stopping Semantic Web HTTP Server");
        
        if (server != null) {
            try {
                server.stop();
                server.join();
                LOG.info("HTTP server stopped");
            } catch (Exception e) {
                LOG.error("Error stopping HTTP server", e);
                throw new IOException("Failed to stop HTTP server: " + e.getMessage(), e);
            }
        }
        
        if (rdfService != null) {
            rdfService.close();
        }
        
        if (dataset != null) {
            dataset.close();
        }
    }
    
    /**
     * Waits for the server to stop.
     * 
     * @throws InterruptedException if the thread is interrupted
     */
    public void join() throws InterruptedException {
        if (server != null) {
            server.join();
        }
    }
    
    /**
     * Checks if the server is currently running.
     * 
     * @return true if server is running, false otherwise
     */
    public boolean isRunning() {
        return server != null && server.isRunning();
    }
    
    /**
     * Gets the hostname the server is bound to.
     * 
     * @return the hostname
     */
    public String getHost() {
        return host;
    }
    
    /**
     * Gets the port number the server is listening on.
     * 
     * @return the port number
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Gets the context path for RDF endpoints.
     * 
     * @return the context path
     */
    public String getContextPath() {
        return contextPath;
    }
    
    /**
     * Gets the underlying Jetty server instance.
     * 
     * @return the Jetty Server instance or null if not started
     */
    public Server getServer() {
        return server;
    }
}