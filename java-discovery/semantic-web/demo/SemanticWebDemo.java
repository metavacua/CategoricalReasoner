package org.catty.semanticweb.demo;

import org.catty.categorical.core.*;

/**
 * Semantic Web Integration Demo
 * 
 * Demonstrates Java categorical objects to RDF/OWL mapping using AbstractLogic.
 */
public class SemanticWebDemo {
    
    public static void main(String[] args) {
        try {
            var logicCategory = createLogicCategory();
            String rdfContent = generateRDF(logicCategory);
            saveRDF(rdfContent);
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
        }
    }
    
    private static LogicCategory createLogicCategory() {
        LogicCategory category = new LogicCategory("LogicCategory");
        
        // Use abstract logic implementations
        AbstractLogic ppsc = new ParaconsistentLogic();
        AbstractLogic cpl = new ClassicalLogic();
        
        category.addObject(ppsc);
        category.addObject(cpl);
        
        category.addMorphism(new Morphism("ppsc_to_cpl", ppsc, cpl, "EXTENSION", "Classical extension"));
        
        category.initialObject = ppsc;
        category.terminalObject = cpl;
        
        return category;
    }
    
    private static String generateRDF(LogicCategory category) {
        var rdf = new StringBuilder();
        rdf.append("@prefix catty: <https://catty.org/categorical#> .\n");
        rdf.append("@prefix owl: <http://www.w3.org/2002/07/owl#> .\n\n");
        
        for (var obj : category.objects) {
            rdf.append("catty:").append(obj.name).append(" rdf:type catty:LogicObject .\n");
        }
        
        return rdf.toString();
    }
    
    private static void saveRDF(String content) throws java.io.IOException {
        var dir = new java.io.File("/tmp/catty-output");
        dir.mkdirs();
        java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/catty-output/catty.ttl"), content.getBytes());
    }
}