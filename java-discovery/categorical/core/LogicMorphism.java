package org.catty.categorical.core;

import java.util.*;

/**
 * Logic morphism that preserves logical structure and properties.
 * All morphisms in the category of logics must be structure-preserving.
 */
public final class LogicMorphism {
    private final String name;
    private final AbstractLogic domain;
    private final AbstractLogic codomain;
    private final Set<String> preservedConnectives;
    private final Set<String> addedConnectives;
    private final String description;
    
    private LogicMorphism(Builder builder) {
        this.name = builder.name;
        this.domain = builder.domain;
        this.codomain = builder.codomain;
        this.preservedConnectives = new HashSet<>(builder.preservedConnectives);
        this.addedConnectives = new HashSet<>(builder.addedConnectives);
        this.description = builder.description;
        validateMorphism();
    }
    
    public static class Builder {
        private final String name;
        private final AbstractLogic domain;
        private final AbstractLogic codomain;
        private final Set<String> preservedConnectives = new HashSet<>();
        private final Set<String> addedConnectives = new HashSet<>();
        private String description;
        
        public Builder(String name, AbstractLogic domain, AbstractLogic codomain) {
            this.name = name;
            this.domain = domain;
            this.codomain = codomain;
        }
        
        public Builder preserveConnectives(String... connectives) {
            preservedConnectives.addAll(Arrays.asList(connectives));
            return this;
        }
        
        public Builder addConnectives(String... connectives) {
            addedConnectives.addAll(Arrays.asList(connectives));
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public LogicMorphism build() {
            return new LogicMorphism(this);
        }
    }
    
    private void validateMorphism() {
        // Verify that preserved connectives are actually in the domain
        for (String connective : preservedConnectives) {
            if (!domain.hasConnective(connective)) {
                throw new IllegalArgumentException(
                    "Morphism " + name + " attempts to preserve non-existent connective: " + connective);
            }
        }
        
        // Verify that added connectives are actually in the codomain
        for (String connective : addedConnectives) {
            if (!codomain.hasConnective(connective)) {
                throw new IllegalArgumentException(
                    "Morphism " + name + " claims to add non-existent connective: " + connective);
            }
        }
        
        // Verify that all preserved connectives appear in the codomain
        for (String connective : preservedConnectives) {
            if (!codomain.hasConnective(connective)) {
                throw new IllegalArgumentException(
                    "Morphism " + name + " preserves connective " + connective + 
                    " but it's not in codomain " + codomain.getName());
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public AbstractLogic getDomain() {
        return domain;
    }
    
    public AbstractLogic getCodomain() {
        return codomain;
    }
    
    public Set<String> getPreservedConnectives() {
        return new HashSet<>(preservedConnectives);
    }
    
    public Set<String> getAddedConnectives() {
        return new HashSet<>(addedConnectives);
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this morphism preserves a specific connective
     */
    public boolean preservesConnective(String connective) {
        return preservedConnectives.contains(connective);
    }
    
    /**
     * Check if this morphism adds a specific connective
     */
    public boolean addsConnective(String connective) {
        return addedConnectives.contains(connective);
    }
    
    /**
     * Check if this morphism is an extension (adds connectives)
     */
    public boolean isExtension() {
        return !addedConnectives.isEmpty();
    }
    
    /**
     * Check if this morphism is a restriction (removes connectives)
     */
    public boolean isRestriction() {
        return codomain.getConnectives().size() < domain.getConnectives().size();
    }
    
    /**
     * Check if this morphism is an isomorphism
     */
    public boolean isIsomorphism() {
        return domain.getConnectives().equals(codomain.getConnectives()) &&
               domain.isIsomorphicTo(codomain);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicMorphism)) return false;
        LogicMorphism that = (LogicMorphism) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return String.format("LogicMorphism(%s: %s → %s)", name, domain.getName(), codomain.getName());
    }
}