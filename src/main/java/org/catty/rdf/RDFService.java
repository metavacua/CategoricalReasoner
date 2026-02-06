package org.catty.rdf;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.shared.JenaException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.OWL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;

/**
 * RDFService - Core RDF operations service for Catty Categorical Reasoner.
 * 
 * This service provides comprehensive RDF processing capabilities including:
 * - RDF model creation and manipulation
 * - Multiple format serialization/deserialization (Turtle, JSON-LD, RDF/XML, N-Triples)
 * - Ontology management and reasoning
 * - Resource and property operations
 * - Namespace management
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 */
public class RDFService {
    
    private static final Logger LOG = LoggerFactory.getLogger(RDFService.class);
    
    /**
     * Supported RDF content types for HTTP serving
     */
    public enum RDFContentType {
        TURTLE("text/turtle", "ttl"),
        JSON_LD("application/ld+json", "jsonld"),
        RDF_XML("application/rdf+xml", "rdf"),
        N_TRIPLES("application/n-triples", "nt"),
        N3("text/n3", "n3"),
        NQUADS("application/n-quads", "nq");
        
        private final String contentType;
        private final String fileExtension;
        
        RDFContentType(String contentType, String fileExtension) {
            this.contentType = contentType;
            this.fileExtension = fileExtension;
        }
        
        public String getContentType() { return contentType; }
        public String getFileExtension() { return fileExtension; }
    }
    
    private final Dataset dataset;
    private final Map<String, String> prefixMapping;
    
    /**
     * Initializes RDFService with a dataset.
     * 
     * @param dataset the Jena dataset to operate on
     */
    public RDFService(Dataset dataset) {
        this.dataset = dataset;
        this.prefixMapping = createDefaultPrefixMapping();
        LOG.info("RDFService initialized with dataset");
    }
    
    /**
     * Creates default namespace prefixes for Catty ontologies.
     * 
     * @return map of prefix to namespace URI
     */
    private Map<String, String> createDefaultPrefixMapping() {
        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("rdf", RDF.getURI());
        prefixes.put("rdfs", RDFS.getURI());
        prefixes.put("owl", OWL.getURI());
        prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        prefixes.put("cat", "http://catty.example.org/cat#");
        prefixes.put("cato", "http://catty.example.org/ontology#");
        prefixes.put("catc", "http://catty.example.org/category#");
        prefixes.put("catl", "http://catty.example.org/logic#");
        return prefixes;
    }
    
    /**
     * Creates a new empty model with Catty prefixes.
     * 
     * @return a new Model instance
     */
    public Model createModel() {
        Model model = ModelFactory.createDefaultModel();
        prefixMapping.forEach(model::setNsPrefix);
        return model;
    }
    
    /**
     * Loads RDF data from an input stream.
     * 
     * @param inputStream the input stream containing RDF data
     * @param lang the RDF language format
     * @return a Model containing the loaded RDF data
     * @throws IOException if loading fails
     */
    public Model readRDF(InputStream inputStream, Lang lang) throws IOException {
        try {
            Model model = createModel();
            RDFDataMgr.read(model, inputStream, lang);
            LOG.debug("Loaded RDF data in format: {}", lang.getName());
            return model;
        } catch (JenaException e) {
            LOG.error("Failed to read RDF data", e);
            throw new IOException("Failed to read RDF data", e);
        }
    }
    
    /**
     * Serializes a model to a string in the specified format.
     * 
     * @param model the model to serialize
     * @param contentType the target content type
     * @return string representation of the model
     * @throws IOException if serialization fails
     */
    public String serializeModel(Model model, RDFContentType contentType) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Lang lang = getLangFromContentType(contentType);
            RDFFormat format = getFormatFromContentType(contentType, lang);
            
            RDFDataMgr.write(out, model, format);
            
            String result = out.toString("UTF-8");
            LOG.debug("Serialized model to format: {}", contentType);
            return result;
        } catch (Exception e) {
            LOG.error("Failed to serialize model", e);
            throw new IOException("Failed to serialize model", e);
        }
    }
    
    /**
     * Converts ContentType to Jena Lang.
     * 
     * @param contentType the RDF content type
     * @return corresponding Jena Lang
     */
    private Lang getLangFromContentType(RDFContentType contentType) {
        switch (contentType) {
            case TURTLE:
                return Lang.TURTLE;
            case JSON_LD:
                return Lang.JSONLD;
            case RDF_XML:
                return Lang.RDFXML;
            case N_TRIPLES:
                return Lang.NTRIPLES;
            case N3:
                return Lang.N3;
            case NQUADS:
                return Lang.NQUADS;
            default:
                return Lang.TURTLE;
        }
    }
    
    /**
     * Gets the appropriate RDFFormat for better formatting.
     * 
     * @param contentType the RDF content type
     * @param lang the base Lang
     * @return the formatted RDFFormat
     */
    private RDFFormat getFormatFromContentType(RDFContentType contentType, Lang lang) {
        switch (contentType) {
            case TURTLE:
                return RDFFormat.TURTLE_BLOCKS;
            case JSON_LD:
                return RDFFormat.JSONLD_FRAME_PRETTY;
            case RDF_XML:
                return RDFFormat.RDFXML_ABBREV;
            case N_TRIPLES:
                return RDFFormat.NTRIPLES_UTF8;
            case N3:
                return RDFFormat.N3;
            case NQUADS:
                return RDFFormat.NQUADS_UTF8;
            default:
                return RDFFormat.TURTLE_BLOCKS;
        }
    }
    
    /**
     * Creates a new resource in the given model.
     * 
     * @param model the target model
     * @param uri the URI of the resource
     * @param types the RDF types of the resource
     * @return the created Resource
     */
    public Resource createResource(Model model, String uri, Resource... types) {
        Resource resource = model.createResource(uri);
        for (Resource type : types) {
            resource.addProperty(RDF.type, type);
        }
        LOG.debug("Created resource: {} with types: {}", uri, types.length);
        return resource;
    }
    
    /**
     * Adds a property to a resource.
     * 
     * @param resource the resource to add property to
     * @param property the property predicate
     * @param value the property value (can be Resource or String)
     * @return the updated Resource
     */
    public Resource addProperty(Resource resource, Property property, Object value) {
        if (value instanceof Resource) {
            resource.addProperty(property, (Resource) value);
        } else {
            resource.addProperty(property, value.toString());
        }
        LOG.debug("Added property {} to resource {}", property.getURI(), resource.getURI());
        return resource;
    }
    
    /**
     * Creates a literal value with optional datatype or language.
     * 
     * @param model the target model
     * @param value the literal value
     * @param datatype optional datatype URI (null for plain string)
     * @param lang optional language tag (null for no language)
     * @return the created Literal
     */
    public Literal createTypedLiteral(Model model, String value, String datatype, String lang) {
        if (datatype != null) {
            return model.createTypedLiteral(value, datatype);
        } else if (lang != null) {
            return model.createLiteral(value, lang);
        } else {
            return model.createLiteral(value);
        }
    }
    
    /**
     * Lists all subjects of a given type in the model.
     * 
     * @param model the model to query
     * @param type the RDF type to filter by
     * @return iterable of Resources matching the type
     */
    public Iterable<Resource> listSubjectsOfType(Model model, Resource type) {
        return () -> model.listSubjectsWithProperty(RDF.type, type);
    }
    
    /**
     * Gets the default model from the dataset.
     * 
     * @return the default model
     */
    public Model getDefaultModel() {
        return dataset.getDefaultModel();
    }
    
    /**
     * Creates a named model in the dataset.
     * 
     * @param modelName the name of the model
     * @return the created or existing Model
     */
    public Model createNamedModel(String modelName) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getNamedModel(modelName);
            dataset.commit();
            LOG.info("Created/retrieved named model: {}", modelName);
            return model;
        } catch (Exception e) {
            dataset.abort();
            LOG.error("Failed to create named model: {}", modelName, e);
            throw new RuntimeException("Failed to create named model", e);
        } finally {
            dataset.end();
        }
    }
    
    /**
     * Gets all prefix mappings for namespace abbreviations.
     * 
     * @return map of prefix to namespace URI
     */
    public Map<String, String> getPrefixMapping() {
        return new HashMap<>(prefixMapping);
    }
    
    /**
     * Adds a custom namespace prefix.
     * 
     * @param prefix the namespace prefix
     * @param namespace the namespace URI
     */
    public void addNamespacePrefix(String prefix, String namespace) {
        prefixMapping.put(prefix, namespace);
        LOG.info("Added namespace prefix: {} -> {}", prefix, namespace);
    }
    
    /**
     * Closes the dataset and releases resources.
     */
    public void close() {
        if (dataset != null) {
            dataset.close();
            LOG.info("RDFService dataset closed");
        }
    }
}