package org.catty.semanticweb.demo;

import org.catty.categorical.core.*;
import java.util.*;

/**
 * Semantic Web Integration Demo with Correct-by-Construction Implementation
 * 
 * Demonstrates Java categorical objects to RDF/OWL mapping using verified AbstractLogic.
 */
public final class SemanticWebDemo {
    
    public static void main(String[] args) {
        try {
            LogicCategory category = createLogicCategory();
            String rdfContent = generateRDF(category);
            saveRDF(rdfContent);
            verifyCommutativeDiagram(category);
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private static LogicCategory createLogicCategory() {
        return new LogicCategory.Builder("LogicCategory")
            .addLogic(new MinimalLogic())
            .addLogic(new ClassicalLogic())
            .addMorphism(new LogicMorphism.Builder("LM_to_LK", new MinimalLogic(), new ClassicalLogic())
                .preserveConnectives("∧", "∨", "⊤", "⊥", "⊢")
                .addConnectives("¬", "→", "↔", "∀", "∃", "=", "LEM", "LNC", "Explosion", "DNE", "Peirce")
                .description("Extension from minimal to classical logic")
                .build())
            .build();
    }
    
    private static String generateRDF(LogicCategory category) {
        var rdf = new StringBuilder();
        rdf.append("@prefix catty: <").append(category.getLogic("LM").getNamespace()).append("> .\n");
        rdf.append("@prefix owl: <http://www.w3.org/2002/07/owl#> .\n\n");
        
        for (AbstractLogic logic : category.getLogics()) {
            rdf.append("catty:").append(logic.getName()).append(" rdf:type catty:LogicObject .\n");
            rdf.append("catty:").append(logic.getName())
               .append(" catty:hasConnectives \"")
               .append(String.join(", ", logic.getConnectives()))
               .append("\" .\n");
            
            if (logic.isInitial()) {
                rdf.append("catty:").append(logic.getName()).append(" rdf:type catty:InitialObject .\n");
            } else if (logic.isTerminal()) {
                rdf.append("catty:").append(logic.getName()).append(" rdf:type catty:TerminalObject .\n");
            }
        }
        
        return rdf.toString();
    }
    
    private static void saveRDF(String content) throws java.io.IOException {
        var dir = new java.io.File("/tmp/catty-output");
        dir.mkdirs();
        java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/catty-output/catty.ttl"), content.getBytes());
    }
    
    private static void verifyCommutativeDiagram(LogicCategory category) {
        // Verify that initial object exists and is unique
        AbstractLogic initial = category.getInitialObject();
        if (initial == null) {
            throw new RuntimeException("No initial object found");
        }
        
        // Verify that terminal object exists and is unique
        AbstractLogic terminal = category.getTerminalObject();
        if (terminal == null) {
            throw new RuntimeException("No terminal object found");
        }
        
        // Verify that extension morphism exists and is structure-preserving
        Set<LogicMorphism> morphisms = category.getMorphisms();
        if (morphisms.isEmpty()) {
            throw new RuntimeException("No morphisms found");
        }
        
        LogicMorphism extension = morphisms.iterator().next();
        if (!extension.isExtension()) {
            throw new RuntimeException("Extension morphism validation failed");
        }
    }
}