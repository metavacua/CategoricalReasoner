package org.catty.categorical.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

/**
 * Abstract base class for logic systems in categorical structures.
 * All URIs use localhost with proper infrastructure for publishing.
 */
public abstract class AbstractLogic {
    protected final URI namespace;
    protected final String name;
    protected final String description;
    protected final Set<String> connectives;
    protected final Map<String, Object> properties;
    
    protected AbstractLogic(String namespace, String name, String description, Set<String> connectives) {
        this.namespace = createNamespace(namespace);
        this.name = Objects.requireNonNull(name, "Logic name cannot be null");
        this.description = Objects.requireNonNull(description, "Logic description cannot be null");
        this.connectives = new HashSet<>(Objects.requireNonNull(connectives, "Connectives cannot be null"));
        this.properties = new HashMap<>();
    }
    
    private URI createNamespace(String namespace) {
        try {
            // Default to localhost for development, with infrastructure to switch
            if (namespace == null || namespace.isEmpty()) {
                return new URI("http://localhost/categorical-logics#");
            }
            return new URI(namespace);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid namespace URI: " + namespace, e);
        }
    }
    
    public URI getNamespace() { return namespace; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<String> getConnectives() { return new HashSet<>(connectives); }
    public Map<String, Object> getProperties() { return new HashMap<>(properties); }
    
    public boolean hasConnective(String connective) {
        return connectives.contains(connective);
    }
    
    public boolean hasAllConnectives(Set<String> required) {
        return connectives.containsAll(required);
    }
    
    public boolean hasAnyConnective(Set<String> candidates) {
        return connectives.stream().anyMatch(candidates::contains);
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractLogic)) return false;
        AbstractLogic logic = (AbstractLogic) o;
        return Objects.equals(name, logic.name) && 
               Objects.equals(namespace, logic.namespace);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * Check if this logic is initial in a category - REQUIRED by category theory
     */
    public abstract boolean isInitial();
    
    /**
     * Check if this logic is terminal in a category - REQUIRED by category theory
     */
    public abstract boolean isTerminal();
    
    /**
     * Get the categorical properties of this logic - REQUIRED for isomorphism checking
     */
    public abstract CategoricalProperties getCategoricalProperties();
    
    /**
     * Check isomorphism with another logic - REQUIRED for category theory
     */
    public boolean isIsomorphicTo(AbstractLogic other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        
        return Objects.equals(connectives, other.connectives) &&
               Objects.equals(getCategoricalProperties(), other.getCategoricalProperties());
    }
    
    /**
     * Categorical properties for logic systems
     */
    public static class CategoricalProperties {
        private final boolean isInitial;
        private final boolean isTerminal;
        private final boolean isMonotonic;
        private final boolean isClassical;
        private final boolean isConstructive;
        private final boolean isParaconsistent;
        private final boolean isParacomplete;
        private final boolean isResourceSensitive;
        
        public CategoricalProperties(boolean isInitial, boolean isTerminal, boolean isMonotonic,
                                  boolean isClassical, boolean isConstructive, boolean isParaconsistent,
                                  boolean isParacomplete, boolean isResourceSensitive) {
            this.isInitial = isInitial;
            this.isTerminal = isTerminal;
            this.isMonotonic = isMonotonic;
            this.isClassical = isClassical;
            this.isConstructive = isConstructive;
            this.isParaconsistent = isParaconsistent;
            this.isParacomplete = isParacomplete;
            this.isResourceSensitive = isResourceSensitive;
        }
        
        public boolean isInitial() { return isInitial; }
        public boolean isTerminal() { return isTerminal; }
        public boolean isMonotonic() { return isMonotonic; }
        public boolean isClassical() { return isClassical; }
        public boolean isConstructive() { return isConstructive; }
        public boolean isParaconsistent() { return isParaconsistent; }
        public boolean isParacomplete() { return isParacomplete; }
        public boolean isResourceSensitive() { return isResourceSensitive; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CategoricalProperties)) return false;
            CategoricalProperties that = (CategoricalProperties) o;
            return isInitial == that.isInitial &&
                   isTerminal == that.isTerminal &&
                   isMonotonic == that.isMonotonic &&
                   isClassical == that.isClassical &&
                   isConstructive == that.isConstructive &&
                   isParaconsistent == that.isParaconsistent &&
                   isParacomplete == that.isParacomplete &&
                   isResourceSensitive == that.isResourceSensitive;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(isInitial, isTerminal, isMonotonic, isClassical, 
                             isConstructive, isParaconsistent, isParacomplete, isResourceSensitive);
        }
        
        @Override
        public String toString() {
            return String.format("CategoricalProperties{initial=%s, terminal=%s, monotonic=%s, classical=%s, constructive=%s, paraconsistent=%s, paracomplete=%s, resourceSensitive=%s}",
                isInitial, isTerminal, isMonotonic, isClassical, isConstructive, 
                isParaconsistent, isParacomplete, isResourceSensitive);
        }
    }
}