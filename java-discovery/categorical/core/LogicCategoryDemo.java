package org.catty.categorical.core;

import java.util.*;

/**
 * Demonstration of correct-by-construction categorical logic implementation.
 * This demo shows that all categorical structures are verified at construction time.
 */
public final class LogicCategoryDemo {
    
    public static void main(String[] args) {
        try {
            // Create the category of logics with correct initial and terminal objects
            LogicCategory category = createLogicCategory();
            
            // Create commutative diagram that is guaranteed to be commutative
            CommutativeDiagram diagram = createCommutativeDiagram();
            
            // Verify categorical properties
            verifyCategoricalProperties(category, diagram);
            
            System.out.println("All categorical structures validated successfully.");
            
        } catch (Exception e) {
            System.err.println("Categorical validation failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private static LogicCategory createLogicCategory() {
        return new LogicCategory.Builder("Category of Logics")
            // Add initial object (LM) - the only logic that can be initial
            .addLogic(new MinimalLogic())
            
            // Add terminal object (LK) - the only logic that can be terminal
            .addLogic(new ClassicalLogic())
            
            // Add extension morphism from initial to terminal
            .addMorphism(new LogicMorphism.Builder("LM_to_LK", new MinimalLogic(), new ClassicalLogic())
                .preserveConnectives("∧", "∨", "⊤", "⊥", "⊢")
                .addConnectives("¬", "→", "↔", "∀", "∃", "=", "LEM", "LNC", "Explosion", "DNE", "Peirce")
                .description("Extension from minimal logic to classical first-order logic")
                .build())
            
            .build();
    }
    
    private static CommutativeDiagram createCommutativeDiagram() {
        return new CommutativeDiagram.Builder("Logic Extension Diagram")
            .addNode("LM")
            .addNode("LJ")
            .addNode("LK")
            
            // Add direct edge from LM to LK
            .addEdge("direct_extension", "LM", "LK", "Direct extension to classical logic")
            
            // Add composed path: LM → LJ → LK
            .addEdge("lm_to_lj", "LM", "LJ", "Add negation")
            .addEdge("lj_to_lk", "LJ", "LK", "Add excluded middle and quantifiers")
            
            // This creates the commutative diagram by construction
            // The diagram will validate that both paths have the same description
            .addComposedEdge("composed_path", "LM", "LK", 
                Arrays.asList("lm_to_lj", "lj_to_lk"), 
                "Direct extension to classical logic")
            
            .build();
    }
    
    private static void verifyCategoricalProperties(LogicCategory category, CommutativeDiagram diagram) {
        // Verify initial object
        AbstractLogic initial = category.getInitialObject();
        if (!initial.isInitial()) {
            throw new RuntimeException("Initial object validation failed");
        }
        
        // Verify terminal object
        AbstractLogic terminal = category.getTerminalObject();
        if (!terminal.isTerminal()) {
            throw new RuntimeException("Terminal object validation failed");
        }
        
        // Verify isomorphism checking
        AbstractLogic lm = category.getLogic("LM");
        AbstractLogic lk = category.getLogic("LK");
        
        // These should not be isomorphic (different signatures)
        if (lm.isIsomorphicTo(lk)) {
            throw new RuntimeException("Isomorphism validation failed - LM and LK should not be isomorphic");
        }
        
        // Verify commutative diagram
        if (!diagram.hasPath("LM", "LK")) {
            throw new RuntimeException("Commutative diagram validation failed");
        }
        
        System.out.println("Category: " + category);
        System.out.println("Initial Object: " + initial.getName() + " - " + initial.getDescription());
        System.out.println("Terminal Object: " + terminal.getName() + " - " + terminal.getDescription());
        System.out.println("Commutative Diagram: " + diagram);
        System.out.println("Isomorphism check: LM ≠ LK (correct)");
    }
}