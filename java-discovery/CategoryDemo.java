package org.catty.finite;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Demonstration and Testing of Finite Categories and Logic Category
 * 
 * Shows algorithmic category theory in practice with:
 * 1. Basic finite category operations
 * 2. Logic category with terminal and initial objects
 * 3. Categorical operations and morphisms
 */
public class CategoryDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ALGORITHMIC CATEGORY THEORY DEMO ===");
        System.out.println();
        
        // Test 1: Basic Finite Category
        testBasicFiniteCategory();
        
        // Test 2: Logic Category with Terminal/Initial Objects
        testLogicCategory();
        
        // Test 3: Commutative Diagrams in Logic Category
        testLogicCommutativeDiagrams();
        
        // Test 4: Morphism Composition and Properties
        testMorphismComposition();
        
        // Test 5: Integration with Catty Ontologies
        testOntologyIntegration();
    }
    
    private static void testBasicFiniteCategory() {
        System.out.println("1. BASIC FINITE CATEGORY");
        System.out.println("=========================");
        
        // Build a simple category of sets
        FiniteCategory category = new FiniteCategory.Builder("Sets")
            .addObjects("EmptySet", "Singleton", "Pair")
            .addMorphism("empty_to_singleton", "EmptySet", "Singleton", 
                obj -> "∅ → {x}", "Empty set to singleton set")
            .addMorphism("singleton_to_pair", "Singleton", "Pair", 
                obj -> "{x} → {x,y}", "Singleton to pair")
            .addMorphism("empty_to_pair", "EmptySet", "Pair", 
                obj -> "∅ → {x,y}", "Empty set to pair")
            .addMorphism("pair_projection1", "Pair", "Singleton", 
                obj -> "{x,y} → {x}", "First projection")
            .addMorphism("pair_projection2", "Pair", "Singleton", 
                obj -> "{x,y} → {y}", "Second projection")
            .build();
        
        System.out.println("Created category: " + category);
        System.out.println("Objects: " + category.getObjects());
        System.out.println();
        
        // Test morphism operations
        System.out.println("Morphisms from Singleton:");
        category.getMorphismsFrom("Singleton").forEach(m -> 
            System.out.println("  " + m));
        
        System.out.println("Morphisms to Singleton:");
        category.getMorphismsTo("Singleton").forEach(m -> 
            System.out.println("  " + m));
        
        // Test hom-sets
        System.out.println("Hom(EmptySet, Singleton): " + 
            category.getMorphisms("EmptySet", "Singleton").size() + " morphisms");
        System.out.println("Hom(Singleton, Pair): " + 
            category.getMorphisms("Singleton", "Pair").size() + " morphisms");
        System.out.println();
    }
    
    private static void testLogicCategory() {
        System.out.println("2. LOGIC CATEGORY WITH TERMINAL/INITIAL OBJECTS");
        System.out.println("================================================");
        
        // Build the logic category
        LogicCategory logicCategory = new LogicCategory.LogicCategoryBuilder().build();
        
        System.out.println("Logic Category: " + logicCategory);
        System.out.println("Initial Object: " + logicCategory.getInitialObject());
        System.out.println("  Description: " + logicCategory.getInitialObject().getDescription());
        System.out.println("  Connectives: " + logicCategory.getInitialObject().getConnectives());
        System.out.println();
        
        System.out.println("Terminal Object: " + logicCategory.getTerminalObject());
        System.out.println("  Description: " + logicCategory.getTerminalObject().getDescription());
        System.out.println("  Connectives: " + logicCategory.getTerminalObject().getConnectives());
        System.out.println();
        
        // Test morphism types
        System.out.println("Morphisms from Initial Object (PPSC):");
        Set<LogicCategory.LogicMorphism> fromInitial = logicCategory.getMorphismsFromInitial();
        fromInitial.forEach(m -> System.out.println("  " + m));
        System.out.println();
        
        System.out.println("Morphisms to Terminal Object (CPL):");
        Set<LogicCategory.LogicMorphism> toTerminal = logicCategory.getMorphismsToTerminal();
        toTerminal.forEach(m -> System.out.println("  " + m));
        System.out.println();
        
        // Test specific morphisms
        LogicCategory.Logic ppsc = LogicCategory.Logic.PPSC;
        LogicCategory.Logic cpl = LogicCategory.Logic.CPL;
        
        if (logicCategory.getMorphismsFrom(ppsc).contains(cpl)) {
            System.out.println("Extension morphism PPSC → CPL found!");
            LogicCategory.LogicMorphism extension = logicCategory.getMorphismsFrom(ppsc).stream()
                .map(m -> (LogicCategory.LogicMorphism) m)
                .filter(m -> m.getCodomain() == cpl)
                .findFirst()
                .orElse(null);
            
            if (extension != null) {
                System.out.println("  Type: " + extension.getType());
                System.out.println("  Properties: " + extension.getProperties());
                System.out.println("  Mathematical form: " + extension.getMathematicalForm());
            }
        }
        System.out.println();
    }
    
    private static void testLogicCommutativeDiagrams() {
        System.out.println("3. COMMUTATIVE DIAGRAMS IN LOGIC CATEGORY");
        System.out.println("==========================================");
        
        LogicCategory logicCategory = new LogicCategory.LogicCategoryBuilder().build();
        
        // Create a simple commutative diagram: PPSC → CPL and PPSC → INT → CPL
        System.out.println("Commutative Diagram: Extension of PPSC to CPL");
        System.out.println("PPSC ──→ CPL");
        System.out.println("│         │");
        System.out.println("↓         ↓");
        System.out.println("INT ──────→ CPL");
        System.out.println();
        
        // Check if the diagram commutes
        LogicCategory.Logic ppsc = LogicCategory.Logic.PPSC;
        LogicCategory.Logic cpl = LogicCategory.Logic.CPL;
        LogicCategory.Logic intLogic = LogicCategory.Logic.INT;
        
        boolean directPath = !logicCategory.getMorphisms(ppsc, cpl).isEmpty();
        boolean viaIntPath = !logicCategory.getMorphisms(ppsc, intLogic).isEmpty() && 
                            !logicCategory.getMorphisms(intLogic, cpl).isEmpty();
        
        System.out.println("Direct path PPSC → CPL: " + (directPath ? "EXISTS" : "MISSING"));
        System.out.println("Path PPSC → INT → CPL: " + (viaIntPath ? "EXISTS" : "MISSING"));
        
        if (directPath && viaIntPath) {
            System.out.println("✓ Diagram has multiple paths - commutativity check would require morphism equality");
        } else {
            System.out.println("! Diagram incomplete - missing paths");
        }
        System.out.println();
    }
    
    private static void testMorphismComposition() {
        System.out.println("4. MORPHISM COMPOSITION AND PROPERTIES");
        System.out.println("====================================");
        
        LogicCategory logicCategory = new LogicCategory.LogicCategoryBuilder().build();
        
        LogicCategory.Logic ppsc = LogicCategory.Logic.PPSC;
        LogicCategory.Logic cpl = LogicCategory.Logic.CPL;
        LogicCategory.Logic s4 = LogicCategory.Logic.S4;
        
        // Check for composable morphisms
        Set<FiniteCategory.Morphism> ppscToCpl = logicCategory.getMorphisms(ppsc, cpl);
        Set<FiniteCategory.Morphism> cplToS4 = logicCategory.getMorphisms(cpl, s4);
        
        System.out.println("Morphisms PPSC → CPL: " + ppscToCpl.size());
        System.out.println("Morphisms CPL → S4: " + cplToS4.size());
        
        if (!ppscToCpl.isEmpty() && !cplToS4.isEmpty()) {
            System.out.println("✓ Composition PPSC → CPL → S4 is possible");
            // In a real implementation, we would compose the functions here
        } else {
            System.out.println("! Cannot compose - missing morphisms");
        }
        
        // Test preservation properties
        System.out.println("\nPreservation Properties:");
        Set<LogicCategory.LogicMorphism> allMorphisms = logicCategory.getMorphisms().stream()
            .map(m -> (LogicCategory.LogicMorphism) m)
            .collect(Collectors.toSet());
        
        allMorphisms.stream()
            .filter(m -> m.type == LogicCategory.MorphismType.EXTENSION)
            .forEach(m -> {
                System.out.println("  " + m.name + " preserves connectives: " + 
                    m.preservesConnectives());
            });
        System.out.println();
    }
    
    private static void testOntologyIntegration() {
        System.out.println("5. INTEGRATION WITH CATTY ONTOLOGIES");
        System.out.println("====================================");
        
        // Show how this maps to Catty's ontology structure
        System.out.println("Mapping to Catty Ontologies:");
        System.out.println();
        
        System.out.println("Catty Ontology → Java Implementation:");
        System.out.println("• catty-categorical-schema.jsonld → FiniteCategory interface");
        System.out.println("• logics-as-objects.jsonld → LogicCategory.Logic enum");
        System.out.println("• morphism-catalog.jsonld → LogicCategory.LogicMorphism");
        System.out.println("• curry-howard-categorical-model.jsonld → Semantic operations");
        System.out.println();
        
        // Example of how to integrate with existing ontology
        System.out.println("Integration Example:");
        LogicCategory logicCategory = new LogicCategory.LogicCategoryBuilder().build();
        
        // Convert to RDF-like structure (simplified)
        System.out.println("Logic Objects as RDF Triples:");
        logicCategory.getObjects().forEach(obj -> {
            LogicCategory.Logic logic = (LogicCategory.Logic) obj;
            System.out.println("  _:logic" + logic.name() + " rdf:type catty:Logic");
            System.out.println("  _:logic" + logic.name() + " catty:hasSignature \"" + 
                String.join(",", logic.getConnectives()) + "\"");
            System.out.println("  _:logic" + logic.name() + " catty:hasDescription \"" + 
                logic.getDescription() + "\"");
        });
        
        System.out.println();
        System.out.println("Morphisms as RDF Triples:");
        logicCategory.getMorphisms().forEach(morphism -> {
            LogicCategory.LogicMorphism logicMorphism = (LogicCategory.LogicMorphism) morphism;
            System.out.println("  _:morphism" + logicMorphism.hashCode() + " rdf:type catty:Morphism");
            System.out.println("  _:morphism" + logicMorphism.hashCode() + " catty:domain _:logic" + 
                logicMorphism.domain);
            System.out.println("  _:morphism" + logicMorphism.hashCode() + " catty:codomain _:logic" + 
                logicMorphism.codomain);
            System.out.println("  _:morphism" + logicMorphism.hashCode() + " catty:type \"" + 
                logicMorphism.type + "\"");
        });
        System.out.println();
    }
    
    /**
     * Utility method to create a more complex finite category for testing
     */
    public static FiniteCategory createMonoidCategory() {
        return new FiniteCategory.Builder("Monoid")
            .addObjects("Unit", "Element", "Product")
            .addMorphism("unit_to_element", "Unit", "Element", 
                obj -> "ε → a", "Unit to element")
            .addMorphism("element_to_product", "Element", "Product", 
                obj -> "a → a×b", "Element to product")
            .addMorphism("unit_to_product", "Unit", "Product", 
                obj -> "ε → a×b", "Unit to product")
            .addMorphism("product_left", "Product", "Element", 
                obj -> "a×b → a", "Left projection")
            .addMorphism("product_right", "Product", "Element", 
                obj -> "a×b → b", "Right projection")
            .build();
    }
    
    /**
     * Create the complete logic category with all morphisms
     */
    public static LogicCategory createCompleteLogicCategory() {
        return new LogicCategory.LogicCategoryBuilder().build();
    }
}