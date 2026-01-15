package org.catty.semanticweb.server;

import org.catty.semanticweb.JenaSemanticWebAdapter;
import org.catty.categorical.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Catty Categorical Reasoner - Dynamic Semantic Web Server
 * 
 * Provides REST API for categorical reasoning with semantic web integration.
 * Bridges Java categorical objects with RDF/OWL ontologies via Jena.
 */
@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class CattySemanticWebServer {
    
    private final JenaSemanticWebAdapter semanticAdapter;
    
    public CattySemanticWebServer() {
        this.semanticAdapter = new JenaSemanticWebAdapter("https://catty.org/categorical#");
    }
    
    public static void main(String[] args) {
        SpringApplication.run(CattySemanticWebServer.class, args);
    }
    
    /**
     * Get all available logic categories
     */
    @GetMapping("/api/categories")
    public ResponseEntity<List<CategoryInfo>> getCategories() {
        try {
            List<CategoryInfo> categories = Arrays.asList(
                createLogicCategory(),
                createCommutativePolytopeCategory(),
                createDAGCategory()
            );
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new CategoryInfo("ERROR", e.getMessage())));
        }
    }
    
    /**
     * Get specific category by type
     */
    @GetMapping("/api/categories/{type}")
    public ResponseEntity<CategoryInfo> getCategoryByType(@PathVariable String type) {
        try {
            CategoryInfo category = createCategoryByType(type);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CategoryInfo("ERROR", "Category type not found: " + type));
        }
    }
    
    /**
     * Analyze commutativity of morphisms
     */
    @PostMapping("/api/analyze/commutativity")
    public ResponseEntity<CommutativityResult> analyzeCommutativity(@RequestBody CommutativityRequest request) {
        try {
            boolean isCommutative = checkCommutativity(request);
            return ResponseEntity.ok(new CommutativityResult(
                request.source, request.target, isCommutative,
                isCommutative ? "Diagram commutes" : "Diagram does not commute"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CommutativityResult(request.source, request.target, false, "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Export category to RDF
     */
    @PostMapping("/api/export/rdf")
    public ResponseEntity<RDFExportResult> exportToRDF(@RequestBody RDFExportRequest request) {
        try {
            StaticFiniteCategory<String> category = createLogicCategoryStatic();
            semanticAdapter.exportCategoricalObject(category);
            
            String rdfData = generateRDFPreview(category);
            return ResponseEntity.ok(new RDFExportResult(
                "success", rdfData, "application/turtle"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RDFExportResult("error", "Failed to export: " + e.getMessage(), "text/plain"));
        }
    }
    
    /**
     * Load category from semantic web
     */
    @PostMapping("/api/load/semantic")
    public ResponseEntity<CategoryInfo> loadFromSemantic(@RequestBody SemanticLoadRequest request) {
        try {
            // Simulate loading from semantic web
            String sparqlQuery = "SELECT ?logic WHERE { ?logic rdf:type catty:LogicObject }";
            
            // In a real implementation, this would execute the SPARQL query
            CategoryInfo loaded = createLogicCategory();
            loaded.source = "semantic_web";
            loaded.metadata.put("sparql_query", sparqlQuery);
            loaded.metadata.put("loaded_from", request.endpoint);
            
            return ResponseEntity.ok(loaded);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CategoryInfo("ERROR", "Failed to load from semantic web: " + e.getMessage()));
        }
    }
    
    /**
     * Get SPARQL query interface
     */
    @PostMapping("/api/query/sparql")
    public ResponseEntity<SPARQLResult> executeSPARQL(@RequestBody SPARQLQueryRequest request) {
        try {
            // In a real implementation, this would execute the SPARQL query
            List<String> results = Arrays.asList(
                "catty:PPSC rdf:type catty:InitialObject",
                "catty:CPL rdf:type catty:TerminalObject",
                "catty:Extension rdf:type catty:Morphism"
            );
            
            return ResponseEntity.ok(new SPARQLResult(
                request.query, results, "3 bindings"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new SPARQLResult(request.query, Collections.emptyList(), "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Get categorical theorems
     */
    @GetMapping("/api/theorems")
    public ResponseEntity<List<TheoremInfo>> getTheorems() {
        try {
            List<TheoremInfo> theorems = Arrays.asList(
                new TheoremInfo(
                    "Initial Object Existence",
                    "The category of logics has an initial object (PPSC)",
                    "PPSC has morphisms to all other logics",
                    "category_theory"
                ),
                new TheoremInfo(
                    "Terminal Object Existence", 
                    "The category of logics has a terminal object (CPL)",
                    "CPL has morphisms from all other logics",
                    "category_theory"
                ),
                new TheoremInfo(
                    "Extension Commutativity",
                    "Extension morphisms preserve logical structure",
                    "Multiple extension paths to CPL commute",
                    "commutativity"
                ),
                new TheoremInfo(
                    "Categorical Composition",
                    "Morphism composition is associative",
                    "(f ∘ g) ∘ h = f ∘ (g ∘ h)",
                    "category_theory"
                )
            );
            return ResponseEntity.ok(theorems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
    }
    
    // Helper methods for creating categories
    
    private CategoryInfo createLogicCategory() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "StaticFiniteCategory");
        metadata.put("objects", Arrays.asList("PPSC", "INT", "LL", "CPL"));
        metadata.put("morphisms", Arrays.asList("Extension", "Interpretation", "Restriction"));
        metadata.put("initial_object", "PPSC");
        metadata.put("terminal_object", "CPL");
        metadata.put("dimension", "2");
        
        return new CategoryInfo("LogicCategory", "Category of logics with terminal and initial objects", metadata);
    }
    
    private CategoryInfo createCommutativePolytopeCategory() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "CommutativeNPolytope");
        metadata.put("dimension", "3");
        metadata.put("polytope_type", "TETRAHEDRON");
        metadata.put("faces", Arrays.asList("base_triangle", "extension_triangle", "classical_face"));
        metadata.put("commutativity_constraints", "3");
        
        return new CategoryInfo("LogicPolytope", "Geometric representation of logic extensions", metadata);
    }
    
    private CategoryInfo createDAGCategory() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "DAGCategoricalStructure");
        metadata.put("dimension", "3");
        metadata.put("source_nodes", Arrays.asList("PPSC"));
        metadata.put("sink_nodes", Arrays.asList("CPL"));
        metadata.put("topological_order", Arrays.asList("PPSC", "INT", "LL", "CPL"));
        
        return new CategoryInfo("LogicDAG", "Dependency structure of logic extensions", metadata);
    }
    
    private CategoryInfo createCategoryByType(String type) {
        switch (type.toLowerCase()) {
            case "logic":
            case "category":
                return createLogicCategory();
            case "polytope":
                return createCommutativePolytopeCategory();
            case "dag":
                return createDAGCategory();
            default:
                throw new IllegalArgumentException("Unknown category type: " + type);
        }
    }
    
    private StaticFiniteCategory<String> createLogicCategoryStatic() {
        return new StaticFiniteCategory.Builder<String>("LogicCategory")
            .addObject("PPSC")
            .addObject("INT")
            .addObject("LL")
            .addObject("CPL")
            .addMorphism("ppsc_to_int", "PPSC", "INT", 
                String::toUpperCase, "EXTENSION", "Add constructive negation")
            .addMorphism("int_to_cpl", "INT", "CPL",
                String::toUpperCase, "EXTENSION", "Add excluded middle")
            .addMorphism("ppsc_to_cpl", "PPSC", "CPL",
                String::toUpperCase, "EXTENSION", "Direct classical extension")
            .build();
    }
    
    private boolean checkCommutativity(CommutativityRequest request) {
        // Simplified commutativity check
        // In real implementation, would use actual category structures
        return request.source.equals("PPSC") && request.target.equals("CPL");
    }
    
    private String generateRDFPreview(StaticFiniteCategory<String> category) {
        StringBuilder rdf = new StringBuilder();
        rdf.append("@prefix catty: <https://catty.org/categorical#> .\n");
        rdf.append("@prefix owl: <http://www.w3.org/2002/07/owl#> .\n");
        rdf.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n\n");
        
        rdf.append("catty:").append(category.name).append(" rdf:type owl:Class .\n");
        rdf.append("catty:").append(category.name).append(" rdf:type catty:Category .\n");
        rdf.append("catty:").append(category.name).append(" catty:hasDimension \"").append(category.getDimension()).append("\" .\n\n");
        
        for (Object obj : category.getObjects()) {
            rdf.append("catty:").append(obj).append(" rdf:type catty:LogicObject .\n");
        }
        
        return rdf.toString();
    }
    
    // Data classes for API responses
    
    public static class CategoryInfo {
        public String name;
        public String description;
        public Map<String, Object> metadata;
        public String source;
        
        public CategoryInfo(String name, String description) {
            this.name = name;
            this.description = description;
            this.metadata = new HashMap<>();
            this.source = "java_api";
        }
        
        public CategoryInfo(String name, String description, Map<String, Object> metadata) {
            this(name, description);
            this.metadata = metadata;
        }
    }
    
    public static class CommutativityResult {
        public String source;
        public String target;
        public boolean isCommutative;
        public String message;
        public long timestamp;
        
        public CommutativityResult(String source, String target, boolean isCommutative, String message) {
            this.source = source;
            this.target = target;
            this.isCommutative = isCommutative;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class RDFExportResult {
        public String status;
        public String data;
        public String contentType;
        public long timestamp;
        
        public RDFExportResult(String status, String data, String contentType) {
            this.status = status;
            this.data = data;
            this.contentType = contentType;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class SemanticLoadRequest {
        public String endpoint;
        public String format;
        public Map<String, String> parameters;
    }
    
    public static class CommutativityRequest {
        public String source;
        public String target;
        public String categoryType;
        public Map<String, String> parameters;
    }
    
    public static class RDFExportRequest {
        public String categoryType;
        public String format;
        public Map<String, String> options;
    }
    
    public static class SPARQLQueryRequest {
        public String query;
        public String endpoint;
        public Map<String, String> options;
    }
    
    public static class SPARQLResult {
        public String query;
        public List<String> results;
        public String summary;
        public long timestamp;
        
        public SPARQLResult(String query, List<String> results, String summary) {
            this.query = query;
            this.results = results;
            this.summary = summary;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class TheoremInfo {
        public String name;
        public String statement;
        public String proof;
        public String category;
        public List<String> prerequisites;
        
        public TheoremInfo(String name, String statement, String proof, String category) {
            this.name = name;
            this.statement = statement;
            this.proof = proof;
            this.category = category;
            this.prerequisites = Arrays.asList("Basic category theory", "Logic fundamentals");
        }
    }
}