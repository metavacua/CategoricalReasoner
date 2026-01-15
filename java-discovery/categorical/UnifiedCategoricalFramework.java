package org.catty.categorical;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Unified Categorical Framework Demo
 * 
 * Demonstrates the relationships between:
 * - Static Finite Categories
 * - Commutative N-Polytopes  
 * - Directed Acyclic Graph Structures
 * 
 * Shows how these abstract structures form a hierarchy of categorical data structures.
 */
public class UnifiedCategoricalFramework {
    
    /**
     * Logic types for the demonstration
     */
    public enum LogicType {
        PPSC("Paraconsistent Paracomplete Subclassical", 
             Set.of("AND", "OR"), 
             "Initial logic with only monotonic connectives"),
        CPL("Classical Propositional Logic", 
            Set.of("AND", "OR", "NOT", "IMPLIES", "IFF"), 
            "Terminal logic with complete Boolean algebra"),
        INT("Intuitionistic Logic", 
            Set.of("AND", "OR", "NOT"), 
            "Constructive logic without excluded middle"),
        LL("Linear Logic", 
            Set.of("AND", "OR", "⊗", "⅋"), 
            "Resource-sensitive logic"),
        MODAL("Modal Logic", 
              Set.of("AND", "OR", "NOT", "□", "◇"), 
              "Logic with necessity and possibility");
        
        private final String description;
        private final Set<String> connectives;
        private final String characteristics;
        
        LogicType(String description, Set<String> connectives, String characteristics) {
            this.description = description;
            this.connectives = connectives;
            this.characteristics = characteristics;
        }
        
        public String getDescription() { return description; }
        public Set<String> getConnectives() { return connectives; }
        public String getCharacteristics() { return characteristics; }
        
        @Override
        public String toString() {
            return name() + "(" + description + ")";
        }
    }
    
    /**
     * Morphism types between logics
     */
    public enum MorphismType {
        EXTENSION("Extension", "Adds logical features"),
        INTERPRETATION("Interpretation", "Semantic translation"),
        RESTRICTION("Restriction", "Removes features"),
        EMBEDDING("Embedding", "Embeds logic in larger framework");
    }
    
    public static void main(String[] args) {
        System.out.println("=== UNIFIED CATEGORICAL FRAMEWORK DEMO ===");
        System.out.println("Static Categories, N-Polytopes, and DAG Structures\n");
        
        // 1. Demonstrate Static Finite Category
        testStaticFiniteCategory();
        
        // 2. Demonstrate Commutative N-Polytope
        testCommutativeNPolytope();
        
        // 3. Demonstrate DAG Categorical Structure
        testDAGCategoricalStructure();
        
        // 4. Show relationships and transformations
        testFrameworkRelationships();
        
        // 5. Integration with Catty's Logic Categories
        testLogicCategoryIntegration();
    }
    
    private static void testStaticFiniteCategory() {
        System.out.println("1. STATIC FINITE CATEGORY");
        System.out.println("==========================");
        
        // Build a logic category
        StaticFiniteCategory<LogicType> logicCategory = new StaticFiniteCategory.Builder<LogicType>("LogicCategory")
            .addObject(LogicType.PPSC)
            .addObject(LogicType.CPL)
            .addObject(LogicType.INT)
            .addObject(LogicType.LL)
            .addObject(LogicType.MODAL)
            .addMorphism("ppsc_to_cpl", LogicType.PPSC, LogicType.CPL,
                logic -> LogicType.CPL, "Extension", "Add classical connectives")
            .addMorphism("ppsc_to_int", LogicType.PPSC, LogicType.INT,
                logic -> LogicType.INT, "Extension", "Add intuitionistic negation")
            .addMorphism("int_to_cpl", LogicType.INT, LogicType.CPL,
                logic -> LogicType.CPL, "Extension", "Add excluded middle")
            .addMorphism("cpl_to_modal", LogicType.CPL, LogicType.MODAL,
                logic -> LogicType.MODAL, "Extension", "Add modal operators")
            .build();
        
        System.out.println("Category: " + logicCategory);
        System.out.println("Objects: " + logicCategory.getObjects());
        System.out.println("Initial Object: " + logicCategory.getInitialObject());
        System.out.println("Terminal Object: " + logicCategory.getTerminalObject());
        System.out.println("Category Dimension: " + logicCategory.getDimension());
        
        // Test hom-sets
        System.out.println("Hom(PPSC, CPL): " + logicCategory.getHomSet(LogicType.PPSC, LogicType.CPL));
        System.out.println("Hom(PPSC, INT): " + logicCategory.getHomSet(LogicType.PPSC, LogicType.INT));
        System.out.println();
    }
    
    private static void testCommutativeNPolytope() {
        System.out.println("2. COMMUTATIVE N-POLYTOPE");
        System.out.println("=========================");
        
        // Build a 2D polytope representing logic extensions
        CommutativeNPolytope<LogicType> polytope = new CommutativeNPolytope.Builder<LogicType>(
            "LogicExtensionPolytope", 
            2, 
            CommutativeNPolytope.PolytopeType.SQUARE
        )
        .addNode("ppsc", LogicType.PPSC)
        .addNode("int", LogicType.INT)
        .addNode("cpl", LogicType.CPL)
        .addNode("modal", LogicType.MODAL)
        .addMorphism("ppsc_to_int", "ppsc", "int",
            logic -> LogicType.INT, "Add intuitionistic features")
        .addMorphism("int_to_cpl", "int", "cpl",
            logic -> LogicType.CPL, "Add classical features")
        .addMorphism("ppsc_to_cpl", "ppsc", "cpl",
            logic -> LogicType.CPL, "Direct classical extension")
        .addMorphism("cpl_to_modal", "cpl", "modal",
            logic -> LogicType.MODAL, "Add modal operators")
        .addFace("face1", 1, Arrays.asList("ppsc", "int", "cpl"), 
            CommutativeNPolytope.FaceType.EDGE)
        .addFace("face2", 1, Arrays.asList("cpl", "modal"), 
            CommutativeNPolytope.FaceType.EDGE)
        .addCommutativityConstraint("comm1", Arrays.asList("ppsc", "int", "cpl"),
            "Two paths from PPSC to CPL should commute", 
            CommutativeNPolytope.CommutativityType.LOCAL)
        .build();
        
        System.out.println("Polytope: " + polytope);
        System.out.println("Dimension: " + polytope.getDimension());
        System.out.println("Polytope Type: " + polytope.getPolytopeType());
        System.out.println("Faces: " + polytope.getFaces().size());
        
        // Test commutativity
        boolean isCommutative = polytope.isCommutative("ppsc", "cpl");
        System.out.println("Commutative (PPSC → CPL): " + isCommutative);
        
        // Test paths
        List<CommutativeNPolytope.Path<LogicType>> paths = polytope.findAllPaths("ppsc", "cpl");
        System.out.println("Paths from PPSC to CPL: " + paths.size());
        for (CommutativeNPolytope.Path<LogicType> path : paths) {
            System.out.println("  " + path);
        }
        System.out.println();
    }
    
    private static void testDAGCategoricalStructure() {
        System.out.println("3. DAG CATEGORICAL STRUCTURE");
        System.out.println("============================");
        
        // Build a DAG for logic dependency structure
        DAGCategoricalStructure<LogicType> dag = new DAGCategoricalStructure.Builder<LogicType>("LogicDependencyDAG")
            .addSourceNode("minimal", LogicType.PPSC)
            .addNode("intuitionistic", LogicType.INT)
            .addNode("classical", LogicType.CPL)
            .addNode("linear", LogicType.LL)
            .addSinkNode("modal", LogicType.MODAL)
            .addDependency("minimal", "intuitionistic", 
                logic -> LogicType.INT, "Add constructive negation")
            .addDependency("minimal", "linear", 
                logic -> LogicType.LL, "Add resource sensitivity")
            .addDependency("intuitionistic", "classical", 
                logic -> LogicType.CPL, "Add excluded middle")
            .addDependency("linear", "classical", 
                logic -> LogicType.CPL, "Classical embedding")
            .addDependency("classical", "modal", 
                logic -> LogicType.MODAL, "Add modal operators")
            .addCommutativityPattern("comm_pattern1", "minimal", "classical",
                Arrays.asList("path1", "path2"), "Multiple extension paths")
            .build();
        
        System.out.println("DAG: " + dag);
        System.out.println("Dimension: " + dag.getDimension());
        System.out.println("Source Nodes: " + dag.getSourceNodes());
        System.out.println("Sink Nodes: " + dag.getSinkNodes());
        System.out.println("Topological Order: " + dag.getTopologicalOrder());
        
        // Test path finding
        List<DAGCategoricalStructure.DAGPath<LogicType>> sourceToSinkPaths = dag.getAllSourceToSinkPaths();
        System.out.println("Source-to-Sink Paths: " + sourceToSinkPaths.size());
        for (DAGCategoricalStructure.DAGPath<LogicType> path : sourceToSinkPaths) {
            System.out.println("  " + path);
        }
        
        // Test ancestry
        boolean hasPath = dag.hasPath("minimal", "modal");
        System.out.println("Path from minimal to modal: " + hasPath);
        System.out.println();
    }
    
    private static void testFrameworkRelationships() {
        System.out.println("4. FRAMEWORK RELATIONSHIPS");
        System.out.println("==========================");
        
        System.out.println("Structural Relationships:");
        System.out.println("• StaticFiniteCategory: Base categorical structure");
        System.out.println("  ├── Identity morphisms for all objects");
        System.out.println("  ├── Composition laws (associativity, identity)");
        System.out.println("  └── Hom-sets and categorical operations");
        System.out.println();
        
        System.out.println("• CommutativeNPolytope: Geometric extension");
        System.out.println("  ├── Inherits from AbstractCategoricalStructure");
        System.out.println("  ├── Adds geometric structure (faces, polytope types)");
        System.out.println("  ├── Supports commutativity constraints on faces");
        System.out.println("  └── Generalizes diagrams to n-dimensional spaces");
        System.out.println();
        
        System.out.println("• DAGCategoricalStructure: Hierarchical extension");
        System.out.println("  ├── Inherits from AbstractCategoricalStructure");
        System.out.println("  ├── Adds topological ordering and acyclicity");
        System.out.println("  ├── Supports dependency relationships");
        System.out.println("  └── Enables efficient path algorithms");
        System.out.println();
        
        // Demonstrate transformations between structures
        demonstrateStructureTransformations();
    }
    
    private static void demonstrateStructureTransformations() {
        System.out.println("Structure Transformations:");
        
        // 1. Category → DAG transformation
        StaticFiniteCategory<LogicType> category = new StaticFiniteCategory.Builder<LogicType>("LogicCategory")
            .addObject(LogicType.PPSC)
            .addObject(LogicType.CPL)
            .addObject(LogicType.MODAL)
            .addMorphism("ppsc_to_cpl", LogicType.PPSC, LogicType.CPL,
                logic -> LogicType.CPL, "Extension", "Add classical features")
            .addMorphism("cpl_to_modal", LogicType.CPL, LogicType.MODAL,
                logic -> LogicType.MODAL, "Extension", "Add modal operators")
            .build();
        
        // Transform to DAG (acyclic subset)
        DAGCategoricalStructure<LogicType> dag = categoryToDAG(category);
        System.out.println("✓ Category → DAG: " + dag);
        
        // 2. Category → Polytope transformation
        CommutativeNPolytope<LogicType> polytope = categoryToPolytope(category);
        System.out.println("✓ Category → Polytope: " + polytope);
        
        // 3. DAG → Polytope transformation
        DAGCategoricalStructure<LogicType> dag2 = new DAGCategoricalStructure.Builder<LogicType>("TestDAG")
            .addSourceNode("a", LogicType.PPSC)
            .addNode("b", LogicType.INT)
            .addSinkNode("c", LogicType.CPL)
            .addDependency("a", "b", logic -> LogicType.INT, "Step 1")
            .addDependency("b", "c", logic -> LogicType.CPL, "Step 2")
            .build();
        
        CommutativeNPolytope<LogicType> polytope2 = dagToPolytope(dag2);
        System.out.println("✓ DAG → Polytope: " + polytope2);
        System.out.println();
    }
    
    private static DAGCategoricalStructure<LogicType> categoryToDAG(StaticFiniteCategory<LogicType> category) {
        DAGCategoricalStructure<LogicType> dag = new DAGCategoricalStructure.Builder<LogicType>("FromCategory_" + category.name)
            .build();
        
        // Add nodes
        for (LogicType logic : category.getObjects()) {
            dag.addDAGNode(logic.toString(), logic);
        }
        
        // Add morphisms as dependencies (assuming no cycles)
        for (StaticFiniteCategory.CategoryMorphism<LogicType> morphism : category.getAllMorphisms()) {
            dag.addDAGMorphism(morphism.sourceNodeId, morphism.targetNodeId,
                morphism.transformation, morphism.label);
        }
        
        return dag;
    }
    
    private static CommutativeNPolytope<LogicType> categoryToPolytope(StaticFiniteCategory<LogicType> category) {
        CommutativeNPolytope<LogicType> polytope = new CommutativeNPolytope.Builder<LogicType>(
            "FromCategory_" + category.name, 
            category.getDimension(), 
            CommutativeNPolytope.PolytopeType.ARBITRARY
        ).build();
        
        // Add nodes
        for (LogicType logic : category.getObjects()) {
            polytope.addNode(logic.toString(), logic);
        }
        
        // Add morphisms
        for (StaticFiniteCategory.CategoryMorphism<LogicType> morphism : category.getAllMorphisms()) {
            polytope.addMorphism(morphism.id, morphism.sourceNodeId, morphism.targetNodeId,
                morphism.transformation, morphism.label);
        }
        
        return polytope;
    }
    
    private static CommutativeNPolytope<LogicType> dagToPolytope(DAGCategoricalStructure<LogicType> dag) {
        CommutativeNPolytope<LogicType> polytope = new CommutativeNPolytope.Builder<LogicType>(
            "FromDAG_" + dag.name, 
            dag.getDimension(), 
            CommutativeNPolytope.PolytopeType.N_CUBE
        ).build();
        
        // Add nodes
        for (AbstractCategoricalStructure.Node<LogicType> node : dag.getAllNodes()) {
            polytope.addNode(node.id, node.data);
        }
        
        // Add morphisms
        for (AbstractCategoricalStructure.Morphism<LogicType> morphism : dag.getAllMorphisms()) {
            polytope.addMorphism(morphism.id, morphism.sourceNodeId, morphism.targetNodeId,
                morphism.transformation, morphism.label);
        }
        
        return polytope;
    }
    
    private static void testLogicCategoryIntegration() {
        System.out.println("5. INTEGRATION WITH CATTY LOGIC CATEGORY");
        System.out.println("=======================================");
        
        // Create enhanced logic category using the unified framework
        StaticFiniteCategory<LogicType> cattyLogicCategory = createCattyLogicCategory();
        
        System.out.println("Catty Logic Category: " + cattyLogicCategory);
        System.out.println("Initial Object (Paraconsistent Logic): " + cattyLogicCategory.getInitialObject());
        System.out.println("Terminal Object (Classical Logic): " + cattyLogicCategory.getTerminalObject());
        
        // Show morphism properties
        System.out.println("\nMorphism Types by Category:");
        for (MorphismType type : MorphismType.values()) {
            Set<StaticFiniteCategory.CategoryMorphism<LogicType>> morphisms = 
                cattyLogicCategory.getMorphismsByType(type.name());
            System.out.println("  " + type + ": " + morphisms.size() + " morphisms");
            morphisms.forEach(m -> System.out.println("    " + m));
        }
        
        // Create corresponding polytope for visualization
        CommutativeNPolytope<LogicType> logicPolytope = createLogicPolytope(cattyLogicCategory);
        System.out.println("\nCorresponding Polytope: " + logicPolytope);
        
        // Create DAG for dependency analysis
        DAGCategoricalStructure<LogicType> logicDAG = createLogicDAG(cattyLogicCategory);
        System.out.println("Corresponding DAG: " + logicDAG);
        System.out.println();
    }
    
    private static StaticFiniteCategory<LogicType> createCattyLogicCategory() {
        return new StaticFiniteCategory.Builder<LogicType>("CattyLogicCategory")
            .addObject(LogicType.PPSC)  // Initial object
            .addObject(LogicType.INT)   // Intermediate
            .addObject(LogicType.LL)    // Linear
            .addObject(LogicType.CPL)   // Terminal object
            .addObject(LogicType.MODAL) // Extension
            // Extension morphisms
            .addMorphism("ppsc_to_int", LogicType.PPSC, LogicType.INT,
                logic -> LogicType.INT, "EXTENSION", "Add constructive negation")
            .addMorphism("ppsc_to_ll", LogicType.PPSC, LogicType.LL,
                logic -> LogicType.LL, "EXTENSION", "Add resource sensitivity")
            .addMorphism("int_to_cpl", LogicType.INT, LogicType.CPL,
                logic -> LogicType.CPL, "EXTENSION", "Add excluded middle")
            .addMorphism("ll_to_cpl", LogicType.LL, LogicType.CPL,
                logic -> LogicType.CPL, "EXTENSION", "Classical embedding")
            .addMorphism("cpl_to_modal", LogicType.CPL, LogicType.MODAL,
                logic -> LogicType.MODAL, "EXTENSION", "Add modal operators")
            // Interpretation morphisms
            .addMorphism("cpl_to_int", LogicType.CPL, LogicType.INT,
                logic -> LogicType.INT, "INTERPRETATION", "Gödel-Gentzen translation")
            // Restriction morphisms
            .addMorphism("cpl_to_ppsc", LogicType.CPL, LogicType.PPSC,
                logic -> LogicType.PPSC, "RESTRICTION", "Monotonic subcalculus")
            .build();
    }
    
    private static CommutativeNPolytope<LogicType> createLogicPolytope(StaticFiniteCategory<LogicType> category) {
        return new CommutativeNPolytope.Builder<LogicType>("CattyLogicPolytope", 3, 
            CommutativeNPolytope.PolytopeType.TETRAHEDRON)
            .addNode("ppsc", LogicType.PPSC)
            .addNode("int", LogicType.INT)
            .addNode("ll", LogicType.LL)
            .addNode("cpl", LogicType.CPL)
            .addNode("modal", LogicType.MODAL)
            .addMorphism("ppsc_to_int", "ppsc", "int", logic -> LogicType.INT, "Extension")
            .addMorphism("ppsc_to_ll", "ppsc", "ll", logic -> LogicType.LL, "Extension")
            .addMorphism("int_to_cpl", "int", "cpl", logic -> LogicType.CPL, "Extension")
            .addMorphism("ll_to_cpl", "ll", "cpl", logic -> LogicType.CPL, "Extension")
            .addMorphism("cpl_to_modal", "cpl", "modal", logic -> LogicType.MODAL, "Extension")
            .addFace("base_face", 2, Arrays.asList("ppsc", "int", "ll"), 
                CommutativeNPolytope.FaceType.FACE)
            .addFace("extension_face", 2, Arrays.asList("int", "cpl", "modal"), 
                CommutativeNPolytope.FaceType.FACE)
            .addCommutativityConstraint("triangular_comm", Arrays.asList("ppsc", "int", "cpl"),
                "PPSC → INT → CPL = PPSC → CPL", 
                CommutativeNPolytope.CommutativityType.GLOBAL)
            .build();
    }
    
    private static DAGCategoricalStructure<LogicType> createLogicDAG(StaticFiniteCategory<LogicType> category) {
        return new DAGCategoricalStructure.Builder<LogicType>("CattyLogicDAG")
            .addSourceNode("ppsc", LogicType.PPSC)
            .addNode("int", LogicType.INT)
            .addNode("ll", LogicType.LL)
            .addNode("cpl", LogicType.CPL)
            .addSinkNode("modal", LogicType.MODAL)
            .addDependency("ppsc", "int", logic -> LogicType.INT, "Constructive extension")
            .addDependency("ppsc", "ll", logic -> LogicType.LL, "Linear extension")
            .addDependency("int", "cpl", logic -> LogicType.CPL, "Classical extension")
            .addDependency("ll", "cpl", logic -> LogicType.CPL, "Classical embedding")
            .addDependency("cpl", "modal", logic -> LogicType.MODAL, "Modal extension")
            .build();
    }
    
    // Utility method to convert set to array for Java compatibility
    private static <T> Set<T> SetOf(T... elements) {
        return Arrays.stream(elements).collect(Collectors.toSet());
    }
}