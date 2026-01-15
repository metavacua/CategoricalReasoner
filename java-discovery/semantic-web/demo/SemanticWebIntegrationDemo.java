package org.catty.semanticweb.demo;

import org.catty.semanticweb.JenaSemanticWebAdapter;
import org.catty.categorical.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Comprehensive Semantic Web Integration Demo
 * 
 * Tests the complete integration between Java categorical objects and RDF/OWL ontologies.
 * Demonstrates loading from semantic web, processing, and exporting back to semantic web formats.
 */
public class SemanticWebIntegrationDemo {
    
    private static final String NAMESPACE = "https://catty.org/categorical#";
    
    public static void main(String[] args) {
        System.out.println("=== CATTY SEMANTIC WEB INTEGRATION DEMO ===");
        System.out.println("Java Categorical Objects ↔ RDF/OWL Ontologies");
        System.out.println("Using Apache Jena TDB2 v4.10.0");
        System.out.println();
        
        try {
            // Initialize semantic web adapter
            JenaSemanticWebAdapter adapter = new JenaSemanticWebAdapter(NAMESPACE);
            
            // 1. Create Java categorical objects
            System.out.println("1. CREATING JAVA CATEGORICAL OBJECTS");
            System.out.println("=====================================");
            StaticFiniteCategory<String> logicCategory = createLogicCategory();
            CommutativeNPolytope<String> logicPolytope = createLogicPolytope();
            DAGCategoricalStructure<String> logicDAG = createLogicDAG();
            
            System.out.println("Created: " + logicCategory);
            System.out.println("Created: " + logicPolytope);
            System.out.println("Created: " + logicDAG);
            System.out.println();
            
            // 2. Export to RDF
            System.out.println("2. EXPORTING TO RDF/OWL");
            System.out.println("========================");
            exportCategoricalObjects(adapter, logicCategory, logicPolytope, logicDAG);
            
            // 3. Save to file
            System.out.println("3. SAVING TO FILES");
            System.out.println("==================");
            saveToFiles(adapter);
            
            // 4. Test SPARQL queries
            System.out.println("4. TESTING SPARQL QUERIES");
            System.out.println("==========================");
            testSPARQLQueries(adapter);
            
            // 5. Test loading from semantic web
            System.out.println("5. TESTING LOAD FROM SEMANTIC WEB");
            System.out.println("=================================");
            testLoadingFromSemanticWeb(adapter);
            
            // 6. Demonstrate TeX integration
            System.out.println("6. GENERATING SEMANTIC LATEX");
            System.out.println("===========================");
            generateSemanticLatex(logicCategory);
            
            System.out.println("✓ Semantic Web Integration Demo Complete!");
            
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static StaticFiniteCategory<String> createLogicCategory() {
        return new StaticFiniteCategory.Builder<String>("LogicCategory")
            .addObject("PPSC")  // Initial object
            .addObject("INT")   // Intermediate
            .addObject("LL")    // Linear
            .addObject("CPL")   // Terminal object
            .addObject("MODAL") // Extension
            // Extension morphisms
            .addMorphism("ppsc_to_int", "PPSC", "INT", 
                logic -> "INT", "EXTENSION", "Add constructive negation")
            .addMorphism("int_to_cpl", "INT", "CPL",
                logic -> "CPL", "EXTENSION", "Add excluded middle")
            .addMorphism("ppsc_to_cpl", "PPSC", "CPL",
                logic -> "CPL", "EXTENSION", "Direct classical extension")
            .addMorphism("cpl_to_modal", "CPL", "MODAL",
                logic -> "MODAL", "EXTENSION", "Add modal operators")
            // Interpretation morphism
            .addMorphism("cpl_to_int", "CPL", "INT",
                logic -> "INT", "INTERPRETATION", "Gödel-Gentzen translation")
            // Restriction morphism
            .addMorphism("cpl_to_ppsc", "CPL", "PPSC",
                logic -> "PPSC", "RESTRICTION", "Monotonic subcalculus")
            .build();
    }
    
    private static CommutativeNPolytope<String> createLogicPolytope() {
        return new CommutativeNPolytope.Builder<String>(
            "LogicExtensionPolytope", 
            2, 
            CommutativeNPolytope.PolytopeType.TRIANGLE
        )
        .addNode("ppsc", "PPSC")
        .addNode("int", "INT")
        .addNode("cpl", "CPL")
        .addMorphism("ppsc_to_int", "ppsc", "int", logic -> "INT", "Extension")
        .addMorphism("int_to_cpl", "int", "cpl", logic -> "CPL", "Extension")
        .addMorphism("ppsc_to_cpl", "ppsc", "cpl", logic -> "CPL", "Direct extension")
        .addFace("triangular_face", 2, Arrays.asList("ppsc", "int", "cpl"), 
            CommutativeNPolytope.FaceType.FACE)
        .addCommutativityConstraint("triangular_comm", Arrays.asList("ppsc", "int", "cpl"),
            "Two paths from PPSC to CPL should commute", 
            CommutativeNPolytope.CommutativityType.LOCAL)
        .build();
    }
    
    private static DAGCategoricalStructure<String> createLogicDAG() {
        return new DAGCategoricalStructure.Builder<String>("LogicDependencyDAG")
            .addSourceNode("ppsc", "PPSC")
            .addNode("int", "INT")
            .addNode("cpl", "CPL")
            .addSinkNode("modal", "MODAL")
            .addDependency("ppsc", "int", logic -> "INT", "Constructive extension")
            .addDependency("int", "cpl", logic -> "CPL", "Classical extension")
            .addDependency("ppsc", "cpl", logic -> "CPL", "Direct extension")
            .addDependency("cpl", "modal", logic -> "MODAL", "Modal extension")
            .build();
    }
    
    private static void exportCategoricalObjects(JenaSemanticWebAdapter adapter,
                                               StaticFiniteCategory<String> category,
                                               CommutativeNPolytope<String> polytope,
                                               DAGCategoricalStructure<String> dag) {
        
        System.out.println("Exporting StaticFiniteCategory...");
        adapter.exportCategoricalObject(category);
        
        System.out.println("Exporting CommutativeNPolytope...");
        adapter.exportCategoricalObject(polytope);
        
        System.out.println("Exporting DAGCategoricalStructure...");
        adapter.exportCategoricalObject(dag);
        
        System.out.println("✓ All categorical objects exported to RDF model");
    }
    
    private static void saveToFiles(JenaSemanticWebAdapter adapter) throws IOException {
        String outputDir = "/tmp/catty-semantic-output";
        new File(outputDir).mkdirs();
        
        // Save as Turtle
        try (PrintWriter writer = new PrintWriter(outputDir + "/catty-ontology.ttl")) {
            adapter.exportToFile(outputDir + "/catty-ontology.ttl", "ttl");
        }
        
        // Save as RDF/XML
        try (PrintWriter writer = new PrintWriter(outputDir + "/catty-ontology.rdf")) {
            adapter.exportToFile(outputDir + "/catty-ontology.rdf", "rdf");
        }
        
        // Save as JSON-LD
        try (PrintWriter writer = new PrintWriter(outputDir + "/catty-ontology.jsonld")) {
            adapter.exportToFile(outputDir + "/catty-ontology.jsonld", "jsonld");
        }
        
        System.out.println("✓ Saved to: " + outputDir);
        System.out.println("  - catty-ontology.ttl (Turtle format)");
        System.out.println("  - catty-ontology.rdf (RDF/XML format)");
        System.out.println("  - catty-ontology.jsonld (JSON-LD format)");
    }
    
    private static void testSPARQLQueries(JenaSemanticWebAdapter adapter) {
        // Test 1: Get all categories
        String query1 = "SELECT ?category WHERE { ?category rdf:type catty:Category . }";
        
        System.out.println("Query 1: All categories");
        try {
            var results1 = adapter.executeSPARQLQuery(query1);
            System.out.println("Found categories: " + results1);
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        
        // Test 2: Get initial and terminal objects
        String query2 = "SELECT ?object ?type WHERE { ?object rdf:type ?type . }";
        
        System.out.println("\nQuery 2: Initial and terminal objects");
        try {
            var results2 = adapter.executeSPARQLQuery(query2);
            System.out.println("Found objects: " + results2);
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        
        // Test 3: Get all morphisms
        String query3 = "SELECT ?morphism ?domain ?codomain WHERE { ?morphism rdf:type catty:Morphism . ?morphism catty:domain ?domain . ?morphism catty:codomain ?codomain . }";
        
        System.out.println("\nQuery 3: All morphisms");
        try {
            var results3 = adapter.executeSPARQLQuery(query3);
            System.out.println("Found morphisms: " + results3);
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        
        System.out.println("✓ SPARQL queries tested");
    }
    
    private static void testLoadingFromSemanticWeb(JenaSemanticWebAdapter adapter) {
        System.out.println("Testing loading from semantic web sources...");
        
        // Simulate loading from existing Catty ontology
        String cattyOntology = "@prefix catty: <https://catty.org/categorical#> .\n" +
            "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "\n" +
            "catty:LogicCategory rdf:type owl:Class .\n" +
            "catty:LogicCategory rdf:type catty:Category .\n" +
            "\n" +
            "catty:PPSC rdf:type catty:InitialObject .\n" +
            "catty:PPSC rdf:type catty:LogicObject .\n" +
            "\n" +
            "catty:CPL rdf:type catty:TerminalObject .\n" +
            "catty:CPL rdf:type catty:LogicObject .\n" +
            "\n" +
            "catty:Extension rdf:type catty:Morphism .\n" +
            "catty:Extension catty:domain catty:PPSC .\n" +
            "catty:Extension catty:codomain catty:CPL .";
        
        try {
            // Write to temporary file
            String tempFile = "/tmp/catty-test-ontology.ttl";
            Files.write(Paths.get(tempFile), cattyOntology.getBytes());
            
            // Load from file
            adapter.loadFromFile(tempFile, "ttl");
            
            System.out.println("✓ Loaded Catty ontology from semantic web");
            
            // Query loaded data
            String query = "SELECT ?object ?type WHERE { ?object rdf:type ?type . }";
            
            var results = adapter.executeSPARQLQuery(query);
            System.out.println("Loaded objects: " + results);
            
        } catch (Exception e) {
            System.out.println("Loading test failed: " + e.getMessage());
        }
    }
    
    private static void generateSemanticLatex(StaticFiniteCategory<String> category) throws IOException {
        String outputDir = "/tmp/catty-semantic-output";
        String texFile = outputDir + "/catty-thesis-chapter.tex";
        
        try (PrintWriter writer = new PrintWriter(texFile))) {
            writer.println("% Catty Categorical Reasoner - Semantic LaTeX Chapter");
            writer.println("% Generated from Java categorical objects");
            writer.println();
            writer.println("\\chapter{Categorical Logic Analysis}");
            writer.println();
            writer.println("\\section{Logic Category Structure}");
            writer.println();
            writer.println("The category of logics $\\textbf{LogicCat}$ has the following structure:");
            writer.println();
            writer.println("\\begin{definition}[Logic Category]");
            writer.println("The category $\\textbf{LogicCat}$ consists of:");
            writer.println("\\begin{itemize}");
            
            for (Object obj : category.getObjects()) {
                writer.println("\\item Objects: $" + obj + "$");
            }
            
            writer.println("\\end{itemize}");
            writer.println("\\end{definition}");
            writer.println();
            
            writer.println("\\section{Morphisms}");
            writer.println();
            writer.println("The morphisms in $\\textbf{LogicCat}$ include:");
            writer.println();
            
            for (StaticFiniteCategory.CategoryMorphism<String> morphism : category.getAllMorphisms()) {
                writer.println("\\begin{morphism}");
                writer.println("Name: " + morphism.name + "\\\\");
                writer.println("Domain: $" + morphism.domain + "$\\\\");
                writer.println("Codomain: $" + morphism.codomain + "$\\\\");
                writer.println("Type: " + morphism.type + "\\\\");
                writer.println("Description: " + morphism.description);
                writer.println("\\end{morphism}");
                writer.println();
            }
            
            writer.println("\\section{Categorical Properties}");
            writer.println();
            
            if (category.getInitialObject() != null) {
                writer.println("\\begin{theorem}[Initial Object]");
                writer.println("The logic " + category.getInitialObject() + " is the initial object of $\\textbf{LogicCat}$.");
                writer.println("\\end{theorem}");
                writer.println();
            }
            
            if (category.getTerminalObject() != null) {
                writer.println("\\begin{theorem}[Terminal Object]");
                writer.println("The logic " + category.getTerminalObject() + " is the terminal object of $\\textbf{LogicCat}$.");
                writer.println("\\end{theorem}");
                writer.println();
            }
            
            writer.println("\\section{Semantic Web Representation}");
            writer.println();
            writer.println("The categorical structure is represented in RDF/OWL as follows:");
            writer.println();
            writer.println("\\begin{verbatim}");
            writer.println("@prefix catty: <https://catty.org/categorical#> .");
            writer.println("@prefix owl: <http://www.w3.org/2002/07/owl#> .");
            writer.println();
            writer.println("catty:LogicCategory rdf:type owl:Class .");
            
            for (Object obj : category.getObjects()) {
                writer.println("catty:" + obj + " rdf:type catty:LogicObject .");
            }
            
            for (StaticFiniteCategory.CategoryMorphism<String> morphism : category.getAllMorphisms()) {
                writer.println("catty:" + morphism.name + " rdf:type catty:Morphism .");
                writer.println("catty:" + morphism.name + " catty:domain catty:" + morphism.domain + " .");
                writer.println("catty:" + morphism.name + " catty:codomain catty:" + morphism.codomain + " .");
            }
            
            writer.println("\\end{verbatim}");
        }
        
        System.out.println("✓ Generated semantic LaTeX: " + texFile);
        System.out.println("  Ready for compilation with pdfLaTeX");
    }
}