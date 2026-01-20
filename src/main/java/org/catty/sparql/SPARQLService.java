package org.catty.sparql;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.ResultSetMgr;
import org.apache.jena.riot.resultset.ResultSetWriterRegistry;
import org.apache.jena.riot.resultset.rw.ResultsWriter;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * SPARQLService - Comprehensive SPARQL query and update service for Catty Categorical Reasoner.
 * 
 * This service provides:
 * - SPARQL 1.1 Query and Update execution
 * - Multiple result formats (JSON, XML, CSV, TSV)
 * - Parameterized queries with secure substitution
 * - Transaction support for update operations
 * - Query result processing and transformation
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 */
public class SPARQLService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SPARQLService.class);
    
    /**
     * Supported SPARQL result formats
     */
    public enum SPARQLResultFormat {
        JSON("application/sparql-results+json", "json"),
        XML("application/sparql-results+xml", "xml"),
        CSV("text/csv", "csv"),
        TSV("text/tab-separated-values", "tsv");
        
        private final String contentType;
        private final String formatName;
        
        SPARQLResultFormat(String contentType, String formatName) {
            this.contentType = contentType;
            this.formatName = formatName;
        }
        
        public String getContentType() { return contentType; }
        public String getFormatName() { return formatName; }
    }
    
    /**
     * Query result container
     */
    public static class QueryResult {
        private final ResultSet resultSet;
        private final Model model;
        private final boolean isConstruct;
        private final String format;
        
        public QueryResult(ResultSet resultSet) {
            this.resultSet = resultSet;
            this.model = null;
            this.isConstruct = false;
            this.format = "json";
        }
        
        public QueryResult(ResultSet resultSet, String format) {
            this.resultSet = resultSet;
            this.model = null;
            this.isConstruct = false;
            this.format = format;
        }
        
        public QueryResult(Model model) {
            this.resultSet = null;
            this.model = model;
            this.isConstruct = true;
            this.format = "turtle";
        }
        
        public boolean isConstruct() { return isConstruct; }
        public ResultSet getResultSet() { return resultSet; }
        public Model getModel() { return model; }
    }
    
    private final Dataset dataset;
    
    /**
     * Initializes SPARQLService with a dataset.
     * 
     * @param dataset the Jena dataset for query execution
     */
    public SPARQLService(Dataset dataset) {
        this.dataset = dataset;
        LOG.info("SPARQLService initialized");
    }
    
    /**
     * Executes a SPARQL SELECT or ASK query with secure parameter substitution.
     * 
     * @param queryString the SPARQL query string
     * @param parameters optional query parameters for secure variable substitution
     * @return QueryResult containing the result set
     * @throws SPARQLException if query execution fails
     */
    public QueryResult executeQuery(String queryString, Map<String, String> parameters) throws SPARQLException {
        if (queryString == null) {
            throw new IllegalArgumentException("Query string cannot be null");
        }
        
        LOG.debug("Executing SPARQL query: {}", 
                 queryString.substring(0, Math.min(100, queryString.length())));
        
        dataset.begin(ReadWrite.READ);
        try {
            // Substitute parameters using secure method
            String processedQuery = parameters != null ? 
                substituteParametersSecure(queryString, parameters) : queryString;
            
            Query query = QueryFactory.create(processedQuery);
            
            try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
                switch (query.getQueryType()) {
                    case Query.QueryTypeSelect:
                        ResultSet results = qexec.execSelect();
                        // Need to make results usable after transaction closes
                        results = ResultSetFactory.copyResults(results);
                        return new QueryResult(results);
                        
                    case Query.QueryTypeAsk:
                        boolean result = qexec.execAsk();
                        // Create result set from boolean result
                        ResultSet askResults = ResultSetFactory.makeResults(
                            query.getResultVars(), result);
                        return new QueryResult(askResults);
                        
                    case Query.QueryTypeConstruct:
                        Model constructResult = qexec.execConstruct();
                        return new QueryResult(constructResult);
                        
                    case Query.QueryTypeDescribe:
                        Model describeResult = qexec.execDescribe();
                        return new QueryResult(describeResult);
                        
                    default:
                        throw new SPARQLException("Unsupported query type");
                }
            }
        } catch (QueryParseException e) {
            LOG.error("Invalid SPARQL query syntax: {}", e.getMessage());
            throw new SPARQLException("Invalid SPARQL query syntax: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("SPARQL query execution failed", e);
            throw new SPARQLException("SPARQL query execution failed", e);
        } finally {
            dataset.end();
        }
    }
    
    /**
     * Convenience method for executing queries without parameters.
     * 
     * @param queryString the SPARQL query string
     * @return QueryResult containing the result set
     * @throws SPARQLException if query execution fails
     */
    public QueryResult executeQuery(String queryString) throws SPARQLException {
        return executeQuery(queryString, null);
    }
    
    /**
     * Securely substitutes parameters in SPARQL query string using ParameterizedSparqlString.
     * This prevents SPARQL injection attacks by properly escaping and parameterizing inputs.
     * 
     * @param queryString the SPARQL query template
     * @param parameters map of parameter names to values
     * @return processed query string with substituted parameters
     */
    private String substituteParametersSecure(String queryString, Map<String, String> parameters) {
        if (queryString == null) {
            throw new IllegalArgumentException("Query string cannot be null");
        }
        if (parameters == null || parameters.isEmpty()) {
            return queryString;
        }
        
        // Use ParameterizedSparqlString to prevent SPARQL injection
        ParameterizedSparqlString pss = new ParameterizedSparqlString(queryString);
        
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String paramKey = entry.getKey();
            String paramValue = entry.getValue();
            
            if (paramKey == null || paramValue == null) {
                LOG.warn("Skipping null parameter: {} = {}", paramKey, paramValue);
                continue;
            }
            
            // For literal values - escape and sanitize properly
            pss.setLiteral(paramKey, paramValue);
        }
        
        LOG.debug("Secure parameter substitution completed for {} parameters", parameters.size());
        return pss.toString();
    }
    
    /**
     * Executes a SPARQL UPDATE request.
     * 
     * @param updateString the SPARQL UPDATE query string
     * @throws SPARQLException if update execution fails
     */
    public void executeUpdate(String updateString) throws SPARQLException {
        if (updateString == null) {
            throw new IllegalArgumentException("Update string cannot be null");
        }
        
        LOG.debug("Executing SPARQL UPDATE: {}", 
                 updateString.substring(0, Math.min(100, updateString.length())));
        
        dataset.begin(ReadWrite.WRITE);
        try {
            UpdateRequest update = UpdateFactory.create(updateString);
            UpdateProcessor processor = UpdateExecutionFactory.create(update, dataset);
            processor.execute();
            dataset.commit();
            LOG.info("SPARQL UPDATE executed successfully");
        } catch (UpdateException e) {
            dataset.abort();
            LOG.error("SPARQL UPDATE failed: {}", e.getMessage());
            throw new SPARQLException("SPARQL UPDATE failed: " + e.getMessage(), e);
        } catch (Exception e) {
            dataset.abort();
            LOG.error("SPARQL UPDATE failed", e);
            throw new SPARQLException("SPARQL UPDATE failed", e);
        } finally {
            dataset.end();
        }
    }
    
    /**
     * Parses and validates SPARQL query syntax.
     * 
     * @param queryString the SPARQL query string
     * @return validation result with error details if invalid
     */
    public ValidationResult validateQuery(String queryString) {
        try {
            Query query = QueryFactory.create(queryString);
            return new ValidationResult(true, query.getQueryType(), null);
        } catch (QueryParseException e) {
            return new ValidationResult(false, -1, "Query parse error at line " + e.getLine() + ": " + e.getMessage());
        } catch (Exception e) {
            return new ValidationResult(false, -1, "Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Query validation result
     */
    public static class ValidationResult {
        public final boolean valid;
        public final int queryType;
        public final String errorMessage;
        
        public ValidationResult(boolean valid, int queryType, String errorMessage) {
            this.valid = valid;
            this.queryType = queryType;
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * Serializes query results to string in specified format.
     * 
     * @param queryResult the query result to serialize
     * @param format the output format
     * @return serialized result string
     * @throws IOException if serialization fails
     */
    public String serializeResults(QueryResult queryResult, SPARQLResultFormat format) throws IOException {
        if (queryResult.isConstruct()) {
            throw new IOException("Cannot serialize construct results as SPARQL results - use RDF serialization");
        }
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ResultsFormat rdfFormat = getResultsFormat(format);
            
            ResultsWriter.create()
                    .format(rdfFormat)
                    .write(out, queryResult.getResultSet());
            
            byte[] bytes = out.toByteArray();
            String result = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            LOG.debug("Serialized query results in format: {}", format);
            return result;
        } catch (Exception e) {
            LOG.error("Failed to serialize query results", e);
            throw new IOException("Failed to serialize query results", e);
        }
    }
    
    /**
     * Converts SPARQLResultFormat to Jena ResultsFormat.
     * 
     * @param format the SPARQL result format
     * @return corresponding Jena ResultsFormat
     */
    private ResultsFormat getResultsFormat(SPARQLResultFormat format) {
        switch (format) {
            case JSON:
                return ResultsFormat.FMT_RS_JSON;
            case XML:
                return ResultsFormat.FMT_RS_XML;
            case CSV:
                return ResultsFormat.FMT_RS_CSV;
            case TSV:
                return ResultsFormat.FMT_RS_TSV;
            default:
                return ResultsFormat.FMT_RS_JSON;
        }
    }
    
    /**
     * Executes a construct query and returns the constructed model.
     * 
     * @param queryString the CONSTRUCT query string
     * @param parameters optional parameters
     * @return constructed model
     * @throws SPARQLException if query execution fails
     */
    public Model executeConstruct(String queryString, Map<String, String> parameters) throws SPARQLException {
        dataset.begin(ReadWrite.READ);
        try {
            String processedQuery = parameters != null ? 
                substituteParametersSecure(queryString, parameters) : queryString;
            Query query = QueryFactory.create(processedQuery);
            
            if (query.getQueryType() != Query.QueryTypeConstruct) {
                throw new SPARQLException("Query is not a CONSTRUCT query");
            }
            
            try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
                return qexec.execConstruct();
            }
        } catch (Exception e) {
            LOG.error("CONSTRUCT query execution failed", e);
            throw new SPARQLException("CONSTRUCT query execution failed", e);
        } finally {
            dataset.end();
        }
    }
    
    /**
     * Gets dataset statistics.
     * 
     * @return map containing dataset statistics
     */
    public Map<String, Object> getDatasetStatistics() {
        Map<String, Object> stats = new HashMap<>();
        dataset.begin(ReadWrite.READ);
        try {
            Model defaultModel = dataset.getDefaultModel();
            stats.put("modelSize", defaultModel.size());
            stats.put("numberOfStatements", defaultModel.listStatements().toSet().size());
            stats.put("numberOfSubjects", defaultModel.listSubjects().toSet().size());
            stats.put("numberOfProperties", defaultModel.listStatements().toList()
                    .stream().map(s -> s.getPredicate().getURI()).distinct().count());
            LOG.debug("Retrieved dataset statistics: {}", stats);
        } catch (Exception e) {
            LOG.error("Failed to get dataset statistics", e);
        } finally {
            dataset.end();
        }
        return stats;
    }
    
    /**
     * Custom exception for SPARQL errors.
     */
    public static class SPARQLException extends Exception {
        public SPARQLException(String message) {
            super(message);
        }
        
        public SPARQLException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}