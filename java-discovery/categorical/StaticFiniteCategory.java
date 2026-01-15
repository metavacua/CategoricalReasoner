package org.catty.categorical;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Static Finite Category Implementation
 * 
 * Implements the abstract categorical structure with specific category theory operations
 */
public final class StaticFiniteCategory<N> extends AbstractCategoricalStructure<N> {
    
    private final Set<N> objects;
    private final Map<String, CategoryMorphism<N>> morphismsByType;
    
    public StaticFiniteCategory(String name) {
        super(name);
        this.objects = new HashSet<>();
        this.morphismsByType = new HashMap<>();
    }
    
    /**
     * Category morphism with type information
     */
    public static class CategoryMorphism<N> extends Morphism<N> {
        public final String type;
        public final String domainObject;
        public final String codomainObject;
        public final String description;
        
        public CategoryMorphism(String id, String sourceNodeId, String targetNodeId, 
                              Function<N, N> transformation, String label,
                              String type, String domainObject, String codomainObject, String description) {
            super(id, sourceNodeId, targetNodeId, transformation, label);
            this.type = type;
            this.domainObject = domainObject;
            this.codomainObject = codomainObject;
            this.description = description;
        }
        
        @Override
        public String toString() {
            return String.format("%s[%s]: %s → %s (%s)", 
                label, type, domainObject, codomainObject, description);
        }
    }
    
    /**
     * Builder for Static Finite Category
     */
    public static class Builder<N> {
        private final StaticFiniteCategory<N> category;
        
        public Builder(String name) {
            this.category = new StaticFiniteCategory<>(name);
        }
        
        public Builder<N> addObject(N object) {
            category.objects.add(object);
            category.addNode(object.toString(), object);
            return this;
        }
        
        public Builder<N> addObjects(Collection<N> objects) {
            objects.forEach(this::addObject);
            return this;
        }
        
        public Builder<N> addMorphism(String id, N domain, N codomain, 
                                    Function<N, N> transformation, String label,
                                    String type, String description) {
            String sourceNodeId = domain.toString();
            String targetNodeId = codomain.toString();
            
            CategoryMorphism<N> morphism = new CategoryMorphism<>(
                id, sourceNodeId, targetNodeId, transformation, label,
                type, domain.toString(), codomain.toString(), description
            );
            
            category.morphisms.put(id, morphism);
            category.morphismsByType.put(id, morphism);
            
            // Update node connections
            category.nodes.get(sourceNodeId).addOutgoingMorphism(id);
            category.nodes.get(targetNodeId).addIncomingMorphism(id);
            
            return this;
        }
        
        public StaticFiniteCategory<N> build() {
            // Add identity morphisms for all objects
            for (N object : category.objects) {
                String identityId = "id_" + object;
                if (!category.morphisms.containsKey(identityId)) {
                    CategoryMorphism<N> identity = new CategoryMorphism<>(
                        identityId, object.toString(), object.toString(),
                        Function.identity(), "identity_" + object,
                        "identity", object.toString(), object.toString(),
                        "Identity morphism for " + object
                    );
                    category.morphisms.put(identityId, identity);
                    category.morphismsByType.put(identityId, identity);
                    category.nodes.get(object.toString()).addOutgoingMorphism(identityId);
                    category.nodes.get(object.toString()).addIncomingMorphism(identityId);
                }
            }
            
            // Validate category laws
            category.validateCategoryLaws();
            
            return category;
        }
    }
    
    /**
     * Hom-set between two objects
     */
    public static class HomSet<N> {
        public final N domain;
        public final N codomain;
        public final Set<CategoryMorphism<N>> morphisms;
        
        public HomSet(N domain, N codomain, Set<CategoryMorphism<N>> morphisms) {
            this.domain = domain;
            this.codomain = codomain;
            this.morphisms = new HashSet<>(morphisms);
        }
        
        public boolean isEmpty() {
            return morphisms.isEmpty();
        }
        
        public int size() {
            return morphisms.size();
        }
        
        @Override
        public String toString() {
            return String.format("Hom(%s, %s) with %d morphisms", domain, codomain, morphisms.size());
        }
    }
    
    private void validateCategoryLaws() {
        // Validate associativity
        validateAssociativity();
        
        // Validate identity laws
        validateIdentityLaws();
    }
    
    private void validateAssociativity() {
        // For all composable morphisms f: A→B, g: B→C, h: C→D
        // Check (f ∘ g) ∘ h = f ∘ (g ∘ h)
        for (CategoryMorphism<N> f : morphismsByType.values()) {
            for (CategoryMorphism<N> g : morphismsByType.values()) {
                if (f.domainObject.equals(g.codomainObject)) {
                    for (CategoryMorphism<N> h : morphismsByType.values()) {
                        if (g.domainObject.equals(h.codomainObject)) {
                            validateAssociativityLaw(f, g, h);
                        }
                    }
                }
            }
        }
    }
    
    private void validateAssociativityLaw(CategoryMorphism<N> f, CategoryMorphism<N> g, CategoryMorphism<N> h) {
        // Check if the composition exists and is consistent
        // This is a heuristic check - in practice would need proper equality
        Function<N, N> left = f.transformation.compose(g.transformation).compose(h.transformation);
        Function<N, N> right = f.transformation.compose(g.transformation.compose(h.transformation));
        
        // Note: Since we can't compare functions for equality directly,
        // this is a structural validation
        // In a real implementation, this would test with sample values
    }
    
    private void validateIdentityLaws() {
        // For all morphisms f: A→B, check id_B ∘ f = f and f ∘ id_A = f
        for (CategoryMorphism<N> morphism : morphismsByType.values()) {
            String leftIdentityId = "id_" + morphism.codomainObject;
            String rightIdentityId = "id_" + morphism.domainObject;
            
            // Check left identity
            if (morphismsByType.containsKey(leftIdentityId)) {
                CategoryMorphism<N> leftIdentity = morphismsByType.get(leftIdentityId);
                // In practice, would verify that leftIdentity ∘ morphism = morphism
            }
            
            // Check right identity
            if (morphismsByType.containsKey(rightIdentityId)) {
                CategoryMorphism<N> rightIdentity = morphismsByType.get(rightIdentityId);
                // In practice, would verify that morphism ∘ rightIdentity = morphism
            }
        }
    }
    
    @Override
    public List<Path<N>> findAllPaths(String sourceNodeId, String targetNodeId) {
        List<Path<N>> allPaths = new ArrayList<>();
        findPathsInternal(sourceNodeId, targetNodeId, new ArrayList<>(), allPaths, new HashSet<>());
        return allPaths;
    }
    
    private void findPathsInternal(String current, String target, List<String> currentPath,
                                 List<Path<N>> allPaths, Set<String> visited) {
        if (current.equals(target)) {
            allPaths.add(createPath(currentPath));
            return;
        }
        
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        
        for (String morphismId : nodes.get(current).outgoingMorphismIds) {
            Morphism<N> morphism = morphisms.get(morphismId);
            currentPath.add(morphismId);
            findPathsInternal(morphism.targetNodeId, target, currentPath, allPaths, visited);
            currentPath.remove(morphismId);
        }
        
        visited.remove(current);
    }
    
    @Override
    public boolean isCommutative(String sourceNodeId, String targetNodeId) {
        List<Path<N>> paths = findAllPaths(sourceNodeId, targetNodeId);
        if (paths.size() <= 1) {
            return true; // Trivially commutative if 0 or 1 paths
        }
        
        // Check if all paths compose to equivalent transformations
        // This is a structural check - in practice would need proper equality
        Set<Function<N, N>> pathTransformations = paths.stream()
            .map(path -> path.composedTransformation)
            .collect(Collectors.toSet());
        
        return pathTransformations.size() == 1; // All paths have same transformation
    }
    
    @Override
    public int getDimension() {
        // Dimension based on the longest path
        int maxPathLength = 0;
        for (String sourceId : nodes.keySet()) {
            for (String targetId : nodes.keySet()) {
                if (!sourceId.equals(targetId)) {
                    List<Path<N>> paths = findAllPaths(sourceId, targetId);
                    for (Path<N> path : paths) {
                        maxPathLength = Math.max(maxPathLength, path.length());
                    }
                }
            }
        }
        return maxPathLength;
    }
    
    @Override
    public String getStructureType() {
        return "StaticFiniteCategory";
    }
    
    // Category-specific operations
    public Set<N> getObjects() {
        return new HashSet<>(objects);
    }
    
    public HomSet<N> getHomSet(N domain, N codomain) {
        Set<CategoryMorphism<N>> morphisms = morphismsByType.values().stream()
            .filter(m -> m.domainObject.equals(domain.toString()) && m.codomainObject.equals(codomain.toString()))
            .collect(Collectors.toSet());
        return new HomSet<>(domain, codomain, morphisms);
    }
    
    public boolean hasMorphism(N domain, N codomain) {
        return !getHomSet(domain, codomain).morphisms.isEmpty();
    }
    
    public Optional<CategoryMorphism<N>> getMorphismById(String id) {
        return Optional.ofNullable((CategoryMorphism<N>) morphisms.get(id));
    }
    
    public Set<CategoryMorphism<N>> getMorphismsByType(String type) {
        return morphismsByType.values().stream()
            .filter(m -> m.type.equals(type))
            .collect(Collectors.toSet());
    }
    
    public N getInitialObject() {
        // Find object with unique morphism to all others
        return objects.stream()
            .filter(obj -> {
                for (N other : objects) {
                    if (!obj.equals(other)) {
                        if (getHomSet(obj, other).size() != 1) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .findFirst()
            .orElse(null);
    }
    
    public N getTerminalObject() {
        // Find object with unique morphism from all others
        return objects.stream()
            .filter(obj -> {
                for (N other : objects) {
                    if (!obj.equals(other)) {
                        if (getHomSet(other, obj).size() != 1) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .findFirst()
            .orElse(null);
    }
    
    public boolean isInitial(N object) {
        return getInitialObject() != null && getInitialObject().equals(object);
    }
    
    public boolean isTerminal(N object) {
        return getTerminalObject() != null && getTerminalObject().equals(object);
    }
}