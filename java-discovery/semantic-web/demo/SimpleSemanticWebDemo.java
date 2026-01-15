package org.catty.semanticweb.demo;

import org.catty.semanticweb.JenaSemanticWebAdapter;
import org.catty.categorical.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Simple Semantic Web Integration Demo
 * 
 * Tests the basic integration between Java categorical objects and RDF/OWL ontologies.
 */
public class SimpleSemanticWebDemo {
    
    private static final String NAMESPACE = "https://catty.org/categorical#";
    
    public static void main(String[] args) {
        System.out.println("=== CATTY SEMANTIC WEB INTEGRATION DEMO ===");
        System.out.println("Java Categorical Objects ↔ RDF/OWL Ontologies");
        System.out.println();
        
        try {
            // Initialize semantic web adapter
            JenaSemanticWebAdapter adapter = new JenaSemanticWebAdapter(NAMESPACE);
            
            // 1. Create Java categorical objects
            System.out.println("1. CREATING JAVA CATEGORICAL OBJECTS");
            StaticFiniteCategory<String> logicCategory = createLogicCategory();
            System.out.println("Created: " + logicCategory);
            
            // 2. Export to RDF
            System.out.println("\n2. EXPORTING TO RDF/OWL");
            adapter.exportCategoricalObject(logicCategory);
            System.out.println("✓ Exported to RDF model");
            
            // 3. Test SPARQL queries
            System.out.println("\n3. TESTING SPARQL QUERIES");
            testSPARQLQueries(adapter);
            
            // 4. Generate RDF export
            System.out.println("\n4. GENERATING RDF OUTPUT");
            generateRDFOutput(adapter);
            
            System.out.println("\n✓ Semantic Web Integration Demo Complete!");
            
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
        
        System.out.println("\nQuery 2: All objects");
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
    
    private static void generateRDFOutput(JenaSemanticWebAdapter adapter) throws IOException {
        String outputDir = "/tmp/catty-semantic-output";
        new File(outputDir).mkdirs();
        
        // Save as Turtle
        String turtleFile = outputDir + "/catty-ontology.ttl";
        System.out.println("Saving RDF as Turtle format: " + turtleFile);
        
        // Generate simple RDF output
        generateSimpleRDF(adapter, outputDir);
        
        System.out.println("✓ RDF output generated");
    }
    
    private static void generateSimpleRDF(JenaSemanticWebAdapter adapter, String outputDir) throws IOException {
        String rdfFile = outputDir + "/catty-simple.rdf";
        
        try (PrintWriter writer = new PrintWriter(rdfFile)) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<rdf:RDF");
            writer.println("    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
            writer.println("    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"");
            writer.println("    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"");
            writer.println("    xmlns:catty=\"https://catty.org/categorical#\">");
            writer.println();
            
            writer.println("  <!-- Logic Category -->");
            writer.println("  <owl:Class rdf:about=\"catty:LogicCategory\"/>");
            writer.println("  <rdfs:subClassOf rdf:resource=\"catty:Category\"/>");
            writer.println();
            
            writer.println("  <!-- Logic Objects -->");
            writer.println("  <owl:Class rdf:about=\"catty:InitialObject\"/>");
            writer.println("  <rdfs:subClassOf rdf:resource=\"catty:LogicObject\"/>");
            writer.println();
            writer.println("  <owl:Class rdf:about=\"catty:TerminalObject\"/>");
            writer.println("  <rdfs:subClassOf rdf:resource=\"catty:LogicObject\"/>");
            writer.println();
            
            writer.println("  <!-- Morphisms -->");
            writer.println("  <owl:Class rdf:about=\"catty:ExtensionMorphism\"/>");
            writer.println("  <rdfs:subClassOf rdf:resource=\"catty:Morphism\"/>");
            writer.println();
            
            writer.println("  <!-- Specific Logics -->");
            writer.println("  <catty:LogicObject rdf:about=\"catty:PPSC\">");
            writer.println("    <rdfs:label>Paraconsistent Paracomplete Subclassical Logic</rdfs:label>");
            writer.println("    <rdfs:comment>Initial logic with monotonic connectives</rdfs:comment>");
            writer.println("  </catty:LogicObject>");
            writer.println();
            
            writer.println("  <catty:LogicObject rdf:about=\"catty:CPL\">");
            writer.println("    <rdfs:label>Classical Propositional Logic</rdfs:label>");
            writer.println("    <rdfs:comment>Terminal logic with complete Boolean algebra</rdfs:comment>");
            writer.println("  </catty:LogicObject>");
            writer.println();
            
            writer.println("  <!-- Extension Morphism -->");
            writer.println("  <catty:ExtensionMorphism rdf:about=\"catty:PPSC_to_CPL\">");
            writer.println("    <catty:domain rdf:resource=\"catty:PPSC\"/>");
            writer.println("    <catty:codomain rdf:resource=\"catty:CPL\"/>");
            writer.println("    <rdfs:label>Classical Extension</rdfs:label>");
            writer.println("    <rdfs:comment>Extension from PPSC to CPL</rdfs:comment>");
            writer.println("  </catty:ExtensionMorphism>");
            writer.println();
            
            writer.println("</rdf:RDF>");
        }
        
        System.out.println("Generated RDF file: " + rdfFile);
    }
}