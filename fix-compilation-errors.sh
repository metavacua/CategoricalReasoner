#!/bin/bash
# Fix compilation errors in SemanticWebServer.java

echo "Fixing SemanticWebServer compilation errors..."

# Create backup
cp src/main/java/org/catty/server/SemanticWebServer.java src/main/java/org/catty/server/SemanticWebServer.java.bak

# Fix missing import and compilation errors
cat > src/main/java/org/catty/server/SemanticWebServer.java << 'EOF'
package org.catty.server;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.system.FusekiLogging;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.shared.JenaException;
import org.apache.jena.tdb2.TDB2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Semantic Web Server - Main entry point for the Catty Categorical Reasoner.
 * 
 * This class initializes and manages a localhost-first semantic web development environment
 * using Apache Jena and Fuseki. The server provides:
 * - RDF serving capabilities (Turtle, JSON-LD, RDF/XML formats)
 * - SPARQL query endpoints
 * - Ontology management
 * - SHACL validation services
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 */
public class SemanticWebServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SemanticWebServer.class);
    
    /**
     * Default hostname for localhost deployment
     */
    public static final String DEFAULT_HOST = "localhost";
    
    /**
     * Default HTTP port for semantic web services
     */
    public static final int DEFAULT_PORT = 3030;
    
    /**
     * Default dataset name for Catty ontologies
     */
    public static final String DEFAULT_DATASET = "/catty";
    
    /**
     * Directory containing Catty ontology files
     */
    public static final String ONTOLOGY_DIR = "ontology";
    
    private final String host;
    private final int port;
    private final String datasetName;
    private final Dataset dataset;
    private FusekiServer server;
    
    /**
     * Initializes a new SemanticWebServer instance with default settings.
     * 
     * @throws IOException if the ontology directory cannot be accessed
     */
    public SemanticWebServer() throws IOException {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DATASET);
    }
    
    /**
     * Initializes a new SemanticWebServer instance with custom settings.
     * 
     * @param host the hostname to bind to
     * @param port the port number to listen on
     * @param datasetName the dataset path/name
     * @throws IOException if the ontology directory cannot be accessed
     */
    public SemanticWebServer(String host, int port, String datasetName) throws IOException {
        this.host = host;
        this.port = port;
        this.datasetName = datasetName.startsWith("/") ? datasetName : "/" + datasetName;
        
        // Initialize TDB2 persistent storage
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        
        this.dataset = TDB2Factory.createDataset(dataDir.toString());
        
        // Load Catty ontologies into the dataset
        loadCattyOntologies();
        
        LOG.info("SemanticWebServer initialized with dataset: {}", this.datasetName);
    }
    
    /**
     * Loads all Catty ontology files from the ontology directory into the dataset.
     * 
     * @throws IOException if ontology files cannot be read
     */
    private void loadCattyOntologies() throws IOException {
        File ontologyDir = new File(ONTOLOGY_DIR);
        if (!ontologyDir.exists() || !ontologyDir.isDirectory()) {
            LOG.warn("Ontology directory not found: {}", ONTOLOGY_DIR);
            return;
        }
        
        LOG.info("Loading Catty ontologies from directory: {}", ONTOLOGY_DIR);
        
        File[] files = ontologyDir.listFiles((dir, name) -> name.endsWith(".jsonld") || 
                                                               name.endsWith(".ttl") || 
                                                               name.endsWith(".rdf") || 
                                                               name.endsWith(".owl"));
        
        if (files != null) {
            dataset.begin(ReadWrite.WRITE);
            try {
                Model defaultModel = dataset.getDefaultModel();
                
                for (File file : files) {
                    LOG.info("Loading ontology file: {}", file.getName());
                    try (InputStream in = new FileInputStream(file)) {
                        RDFDataMgr.read(defaultModel, in, getLangFromFileName(file.getName()));
                        LOG.info("Successfully loaded: {}", file.getName());
                    } catch (JenaException e) {
                        LOG.error("Failed to load ontology file: {}", file.getName(), e);
                    }
                }
                
                dataset.commit();
                LOG.info("Loaded {} ontology files into dataset", files.length);
            } catch (Exception e) {
                LOG.error("Error loading ontologies", e);
                dataset.abort();
                throw new IOException("Failed to load ontologies", e);
            } finally {
                if (dataset.isInTransaction()) {
                    dataset.end();
                }
            }
        }
    }
    
    /**
     * Determines the RDF language from the file extension.
     * 
     * @param fileName the name of the ontology file
     * @return the appropriate RDF language for parsing
     */
    private String getLangFromFileName(String fileName) {
        if (fileName.endsWith(".jsonld")) return RDFLanguages.JSONLD.getName();
        if (fileName.endsWith(".ttl")) return RDFLanguages.TURTLE.getName();
        if (fileName.endsWith(".rdf") || fileName.endsWith(".owl")) return RDFLanguages.RDFXML.getName();
        return RDFLanguages.TURTLE.getName(); // Default fallback
    }
    
    /**