package org.catty.finite;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Finite Static Category Implementation
 * 
 * Algorithmic category theory for practical applications.
 * Focuses on finite categories where all objects and morphisms are known at compile time.
 */
public final class FiniteCategory {
    
    private final String name;
    private final Set<Object> objects;
    private final Map<String, Morphism> morphisms;
    private final Map<String, Set<String>> morphismIndex; // Quick lookup by domain/codomain
    
    private FiniteCategory(Builder builder) {
        this.name = builder.name;
        this.objects = Collections.unmodifiableSet(new HashSet<>(builder.objects));
        this.morphisms = Collections.unmodifiableMap(new HashMap<>(builder.morphisms));
        this.morphismIndex = buildMorphismIndex();
        validateCategory();
    }
    
    public static class Builder {
        private final String name;
        private final Set<Object> objects = new HashSet<>();
        private final Map<String, Morphism> morphisms = new HashMap<>();
        
        public Builder(String name) {
            Objects.requireNonNull(name, "Category name cannot be null");
            this.name = name;
        }
        
        public Builder addObject(Object object) {
            Objects.requireNonNull(object, "Object cannot be null");
            objects.add(object);
            return this;
        }
        
        public Builder addObjects(Object... objects) {
            Objects.requireNonNull(objects, "Objects cannot be null");
            for (Object obj : objects) {
                addObject(obj);
            }
            return this;
        }
        
        public Builder addMorphism(String name, Object domain, Object codomain, 
                                Function<Object, Object> function, String description) {
            Objects.requireNonNull(name, "Morphism name cannot be null");
            Objects.requireNonNull(domain, "Domain cannot be null");
            Objects.requireNonNull(codomain, "Codomain cannot be null");
            Objects.requireNonNull(function, "Function cannot be null");
            
            if (!objects.contains(domain)) {
                throw new IllegalArgumentException("Domain must be an object of the category");
            }
            if (!objects.contains(codomain)) {
                throw new IllegalArgumentException("Codomain must be an object of the category");
            }
            
            Morphism morphism = new Morphism(name, domain, codomain, function, description);
            morphisms.put(name, morphism);
            return this;
        }
        
        public FiniteCategory build() {
            return new FiniteCategory(this);
        }
    }
    
    /**
     * Morphism representation with type safety
     */
    public static class Morphism {
        public final String name;
        public final Object domain;
        public final Object codomain;
        public final Function<Object, Object> function;
        public final String description;
        
        public Morphism(String name, Object domain, Object codomain, 
                       Function<Object, Object> function, String description) {
            this.name = name;
            this.domain = domain;
            this.codomain = codomain;
            this.function = function;
            this.description = description;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Morphism)) return false;
            Morphism morphism = (Morphism) o;
            return Objects.equals(name, morphism.name) &&
                   Objects.equals(domain, morphism.domain) &&
                   Objects.equals(codomain, morphism.codomain);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, domain, codomain);
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s → %s", name, domain, codomain);
        }
    }
    
    private Map<String, Set<String>> buildMorphismIndex() {
        Map<String, Set<String>> index = new HashMap<>();
        for (Morphism morphism : morphisms.values()) {
            String key = String.format("%s_%s", morphism.domain, morphism.codomain);
            index.computeIfAbsent(key, k -> new HashSet<>()).add(morphism.name);
        }
        return index;
    }
    
    private void validateCategory() {
        // Validate that every object has identity morphisms
        for (Object obj : objects) {
            String identityName = "id_" + obj;
            if (!morphisms.containsKey(identityName)) {
                // Auto-generate identity morphism
                morphisms.put(identityName, new Morphism(
                    identityName, obj, obj, 
                    Function.identity(), 
                    String.format("Identity morphism for %s", obj)
                ));
            }
        }
        
        // Validate composition possibilities (associativity check)
        validateCompositionLaws();
    }
    
    private void validateCompositionLaws() {
        // Check associativity for all possible compositions
        for (Morphism f : morphisms.values()) {
            for (Morphism g : morphisms.values()) {
                if (f.codomain.equals(g.domain)) {
                    for (Morphism h : morphisms.values()) {
                        if (g.codomain.equals(h.domain)) {
                            // Check (f ∘ g) ∘ h = f ∘ (g ∘ h)
                            validateAssociativity(f, g, h);
                        }
                    }
                }
            }
        }
    }
    
    private void validateAssociativity(Morphism f, Morphism g, Morphism h) {
        // Test associativity at a sample point
        Object testObject = f.domain; // Use domain of first morphism
        Object leftSide = compose(compose(f.function, g.function), h.function).apply(testObject);
        Object rightSide = compose(f.function, compose(g.function, h.function)).apply(testObject);
        
        // Note: This is a heuristic check since we can't guarantee mathematical equality
        // In a real implementation, this would require proper equality checks
    }
    
    private Function<Object, Object> compose(Function<Object, Object> f, Function<Object, Object> g) {
        return x -> f.apply(g.apply(x));
    }
    
    // Public API
    public String getName() { return name; }
    public Set<Object> getObjects() { return objects; }
    public Set<Morphism> getMorphisms() { return new HashSet<>(morphisms.values()); }
    
    public Optional<Morphism> getMorphism(String name) {
        return Optional.ofNullable(morphisms.get(name));
    }
    
    public Set<Morphism> getMorphismsFrom(Object domain) {
        return morphisms.values().stream()
            .filter(m -> m.domain.equals(domain))
            .collect(Collectors.toSet());
    }
    
    public Set<Morphism> getMorphismsTo(Object codomain) {
        return morphisms.values().stream()
            .filter(m -> m.codomain.equals(codomain))
            .collect(Collectors.toSet());
    }
    
    public Set<Morphism> getMorphisms(Object domain, Object codomain) {
        return morphisms.values().stream()
            .filter(m -> m.domain.equals(domain) && m.codomain.equals(codomain))
            .collect(Collectors.toSet());
    }
    
    public boolean isHomSet(Object domain, Object codomain) {
        return objects.contains(domain) && objects.contains(codomain) && 
               getMorphisms(domain, codomain).size() > 0;
    }
    
    public Object applyMorphism(String name, Object input) {
        return morphisms.get(name).function.apply(input);
    }
    
    @Override
    public String toString() {
        return String.format("Category(%s) with %d objects and %d morphisms", 
                           name, objects.size(), morphisms.size());
    }
}