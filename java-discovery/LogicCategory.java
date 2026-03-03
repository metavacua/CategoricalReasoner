package org.catty.finite;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Category of Logics - Specialized Finite Category
 * 
 * Models the categorical structure of logics where:
 * - Terminal object: Classical First-Order Predicate Logic (CPL)
 * - Initial object: Paraconsistent and Paracomplete Subclassical Logic (PPSC)
 * - Morphisms: Extensions, interpretations, and structural transformations
 * 
 * Focuses on algorithmic operations rather than full theoretical category theory.
 */
public final class LogicCategory extends FiniteCategory {
    
    /**
     * The specific logics in our category
     */
    public enum Logic {
        // Initial object: Paraconsistent & Paracomplete Subclassical Logic (PPSC)
        PPSC("Paraconsistent Paracomplete Subclassical", 
             Set.of("AND", "OR"), // Monotonic connectives only
             Set.of("idempotent", "commutative", "associative"),
             "Lacks: implication, negation, biconditional, XOR, non-implication"),
        
        // Classical propositional logic  
        CPL("Classical Propositional Logic",
            Set.of("AND", "OR", "NOT", "IMPLIES", "IFF", "XOR"),
            Set.of("all classical axioms"),
            "Full classical logic with all Boolean operations"),
        
        // Intuitionistic logic
        INT("Intuitionistic Logic",
            Set.of("AND", "OR", "NOT"), // Intuitionistic connectives
            Set.of("intuitionistic axioms"),
            "No excluded middle, constructive semantics"),
        
        // Linear logic
        LL("Linear Logic",
            Set.of("AND", "OR", "NOT", "⊗", "⅋"), // Linear connectives
            Set.of("resource-sensitive"),
            "Resource-aware logic without weakening/contraction"),
        
        // Modal logic S4
        S4("Modal Logic S4",
            Set.of("AND", "OR", "NOT", "IMPLIES", "□", "◇"),
            Set.of("modal axioms"),
            "Modal logic with necessity and possibility operators"),
        
        // Relevant logic
        REL("Relevant Logic", 
            Set.of("AND", "OR", "NOT", "IMPLIES"), // Relevant connectives
            Set.of("relevance principle"),
            "Logic where premises must be relevant to conclusions");
        
        private final String description;
        private final Set<String> connectives;
        private final Set<String> axioms;
        private final String characteristics;
        
        Logic(String description, Set<String> connectives, Set<String> axioms, String characteristics) {
            this.description = description;
            this.connectives = connectives;
            this.axioms = axioms;
            this.characteristics = characteristics;
        }
        
        public String getDescription() { return description; }
        public Set<String> getConnectives() { return connectives; }
        public Set<String> getAxioms() { return axioms; }
        public String getCharacteristics() { return characteristics; }
        
        public boolean hasConnective(String connective) {
            return connectives.contains(connective);
        }
        
        public boolean hasAllConnectives(Set<String> required) {
            return connectives.containsAll(required);
        }
        
        @Override
        public String toString() {
            return String.format("%s(%s)", name(), description);
        }
    }
    
    /**
     * Types of morphisms between logics
     */
    public enum MorphismType {
        EXTENSION("Extension", "Adds logical features while preserving existing ones"),
        INTERPRETATION("Interpretation", "Semantic interpretation or translation"),
        RESTRICTION("Restriction", "Removes features while preserving structure"),
        EMBEDDING("Embedding", "Embeds one logic in another"),
        REDUCTION("Reduction", "Reduces complex logic to simpler one"),
        COMPOSITION("Composition", "Composition of multiple transformations");
        
        private final String name;
        private final String description;
        
        MorphismType(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * Logic morphism with domain, codomain, and type information
     */
    public static class LogicMorphism extends Morphism {
        public final MorphismType type;
        public final Set<String> properties;
        public final String mathematicalForm;
        
        public LogicMorphism(String name, Logic domain, Logic codomain, 
                           Function<Object, Object> function, String description,
                           MorphismType type, Set<String> properties, String mathematicalForm) {
            super(name, domain, codomain, function, description);
            this.type = type;
            this.properties = properties;
            this.mathematicalForm = mathematicalForm;
        }
        
        public boolean preservesConnectives() {
            // Check if the morphism preserves the domain's connectives
            Logic domain = (Logic) this.domain;
            Logic codomain = (Logic) this.codomain;
            return domain.getConnectives().stream()
                .allMatch(conn -> codomain.hasConnective(conn));
        }
        
        public boolean extendsWithConnectives(Set<String> newConnectives) {
            Logic domain = (Logic) this.domain;
            Logic codomain = (Logic) this.codomain;
            return codomain.getConnectives().containsAll(
                Set.copyOf(domain.getConnectives()).stream()
                    .filter(conn -> !newConnectives.contains(conn))
                    .collect(Collectors.toSet())
            );
        }
        
        public String getMathematicalForm() { return mathematicalForm; }
        public MorphismType getType() { return type; }
        public Set<String> getProperties() { return properties; }
        
        @Override
        public String toString() {
            return String.format("%s: %s → %s [%s]", name, domain, codomain, type);
        }
    }
    
    /**
     * Build the category of logics
     */
    public static class LogicCategoryBuilder {
        private final Map<Logic, Map<Logic, List<LogicMorphism>>> morphisms = new HashMap<>();
        private final Set<Logic> objects = new HashSet<>();
        
        public LogicCategoryBuilder() {
            // Initialize with all logic objects
            objects.addAll(Arrays.asList(Logic.values()));
            
            // Build morphisms for each pair
            for (Logic source : Logic.values()) {
                morphisms.put(source, new HashMap<>());
                for (Logic target : Logic.values()) {
                    morphisms.get(source).put(target, new ArrayList<>());
                }
            }
            
            // Define specific morphisms
            defineMorphisms();
        }
        
        private void defineMorphisms() {
            // Extension: PPSC → CPL (add all classical features)
            addExtensionMorphism(Logic.PPSC, Logic.CPL, "Classical Extension", 
                "∅ ⊢ φ whenever ∅ ⊢ φ in PPSC (preserve all PPSC theorems)",
                Set.of("conservative", "sound", "complete"));
            
            // Extension: CPL → S4 (add modal operators)
            addExtensionMorphism(Logic.CPL, Logic.S4, "Modal Extension",
                "If ⊢ φ in CPL then ⊢ □φ in S4",
                SetOf("modal", "conservative"));
            
            // Interpretation: CPL → INT (double negation interpretation)
            addInterpretationMorphism(Logic.CPL, Logic.INT, "Gödel-Gentzen Translation",
                "¬¬φ ↔ φ",
                SetOf("constructive"));
                
            // Restriction: CPL → PPSC (remove non-monotonic connectives)
            addRestrictionMorphism(Logic.CPL, Logic.PPSC, "Monotonic Restriction",
                "Preserve only AND, OR (monotonic connectives)",
                SetOf("monotonic", "subcalculus"));
                
            // Linear logic embedding: CPL → LL
            addExtensionMorphism(Logic.CPL, Logic.LL, "Linearization",
                "Convert classical operators to linear equivalents",
                SetOf("resource-aware"));
        }
        
        private void addExtensionMorphism(Logic domain, Logic codomain, String name, 
                                       String description, Set<String> properties) {
            LogicMorphism morphism = new LogicMorphism(
                name, domain, codomain,
                createExtensionFunction(domain, codomain),
                description,
                MorphismType.EXTENSION,
                properties,
                generateExtensionForm(domain, codomain)
            );
            morphisms.get(domain).get(codomain).add(morphism);
        }
        
        private void addInterpretationMorphism(Logic domain, Logic codomain, String name,
                                             String description, Set<String> properties) {
            LogicMorphism morphism = new LogicMorphism(
                name, domain, codomain,
                createInterpretationFunction(domain, codomain),
                description,
                MorphismType.INTERPRETATION,
                properties,
                generateInterpretationForm(domain, codomain)
            );
            morphisms.get(domain).get(codomain).add(morphism);
        }
        
        private void addRestrictionMorphism(Logic domain, Logic codomain, String name,
                                         String description, Set<String> properties) {
            LogicMorphism morphism = new LogicMorphism(
                name, domain, codomain,
                createRestrictionFunction(domain, codomain),
                description,
                MorphismType.RESTRICTION,
                properties,
                generateRestrictionForm(domain, codomain)
            );
            morphisms.get(domain).get(codomain).add(morphism);
        }
        
        // Helper methods to create functions (simplified for demonstration)
        private Function<Object, Object> createExtensionFunction(Logic domain, Logic codomain) {
            return obj -> String.format("Extended[%s in %s → %s]", obj, domain, codomain);
        }
        
        private Function<Object, Object> createInterpretationFunction(Logic domain, Logic codomain) {
            return obj -> String.format("Interpreted[%s from %s to %s]", obj, domain, codomain);
        }
        
        private Function<Object, Object> createRestrictionFunction(Logic domain, Logic codomain) {
            return obj -> String.format("Restricted[%s from %s to %s]", obj, domain, codomain);
        }
        
        private String generateExtensionForm(Logic domain, Logic codomain) {
            return String.format("f: %s → %s where f preserves %s and adds %s", 
                domain, codomain, domain.getConnectives(), 
                codomain.getConnectives().stream()
                    .filter(conn -> !domain.hasConnective(conn))
                    .collect(Collectors.toList()));
        }
        
        private String generateInterpretationForm(Logic domain, Logic codomain) {
            return String.format("⟦_⟧: %s → %s (semantic interpretation)", domain, codomain);
        }
        
        private String generateRestrictionForm(Logic domain, Logic codomain) {
            return String.format("|_%s: %s → %s (preserving only %s)", 
                codomain, domain, codomain, codomain.getConnectives());
        }
        
        public LogicCategory build() {
            // Create builder for parent FiniteCategory
            FiniteCategory.Builder builder = new FiniteCategory.Builder("LogicCategory");
            
            // Add logic objects
            for (Logic logic : objects) {
                builder.addObject(logic);
            }
            
            // Add all morphisms
            for (Map.Entry<Logic, Map<Logic, List<LogicMorphism>>> entry : morphisms.entrySet()) {
                Logic source = entry.getKey();
                for (Map.Entry<Logic, List<LogicMorphism>> targetEntry : entry.getValue().entrySet()) {
                    Logic target = targetEntry.getKey();
                    for (LogicMorphism morphism : targetEntry.getValue()) {
                        builder.addMorphism(
                            morphism.name,
                            morphism.domain,
                            morphism.codomain,
                            morphism.function,
                            morphism.description
                        );
                    }
                }
            }
            
            return new LogicCategory(builder.build());
        }
        
        public Set<LogicMorphism> getMorphisms(Logic source, Logic target) {
            return new HashSet<>(morphisms.getOrDefault(source, new HashMap<>())
                .getOrDefault(target, new ArrayList<>()));
        }
        
        public boolean hasMorphism(Logic source, Logic target) {
            return !getMorphisms(source, target).isEmpty();
        }
    }
    
    // Helper to create Set.of (Java 9+) compatible version
    private static Set<String> SetOf(String... elements) {
        return Arrays.stream(elements).collect(Collectors.toSet());
    }
    
    /**
     * Get the initial object (paraconsistent paracomplete logic)
     */
    public Logic getInitialObject() {
        return Logic.PPSC;
    }
    
    /**
     * Get the terminal object (classical propositional logic)
     */
    public Logic getTerminalObject() {
        return Logic.CPL;
    }
    
    /**
     * Check if this is the category of logics
     */
    public boolean isTerminal(Logic logic) {
        return logic == Logic.CPL;
    }
    
    /**
     * Check if this is the initial object
     */
    public boolean isInitial(Logic logic) {
        return logic == Logic.PPSC;
    }
    
    /**
     * Get all morphisms from initial object
     */
    public Set<LogicMorphism> getMorphismsFromInitial() {
        return getMorphismsFrom(Logic.PPSC).stream()
            .map(m -> (LogicMorphism) m)
            .collect(Collectors.toSet());
    }
    
    /**
     * Get all morphisms to terminal object
     */
    public Set<LogicMorphism> getMorphismsToTerminal() {
        return getMorphismsTo(Logic.CPL).stream()
            .map(m -> (LogicMorphism) m)
            .collect(Collectors.toSet());
    }
}