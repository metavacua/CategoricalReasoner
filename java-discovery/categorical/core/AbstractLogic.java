package org.catty.categorical.core;

import java.util.*;
import java.util.function.Function;

/**
 * Standard abstract base class for logic systems in categorical structures.
 * Provides common interface for all logic types with categorical properties.
 */
public abstract class AbstractLogic {
    protected final String name;
    protected final String description;
    protected final Set<String> connectives;
    protected final Map<String, Object> properties;
    
    public AbstractLogic(String name, String description, Set<String> connectives) {
        this.name = Objects.requireNonNull(name, "Logic name cannot be null");
        this.description = Objects.requireNonNull(description, "Logic description cannot be null");
        this.connectives = new HashSet<>(Objects.requireNonNull(connectives, "Connectives cannot be null"));
        this.properties = new HashMap<>();
    }
    
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
        return Objects.equals(name, logic.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * Check if this logic is initial in a category
     */
    public abstract boolean isInitial();
    
    /**
     * Check if this logic is terminal in a category
     */
    public abstract boolean isTerminal();
    
    /**
     * Get the categorical properties of this logic
     */
    public abstract CategoricalProperties getCategoricalProperties();
    
    /**
     * Categorical properties for logic systems
     */
    public static class CategoricalProperties {
        private boolean isInitial = false;
        private boolean isTerminal = false;
        private boolean isMonotonic = false;
        private boolean isClassical = false;
        private boolean isConstructive = false;
        private boolean isParaconsistent = false;
        private boolean isParacomplete = false;
        private boolean isResourceSensitive = false;
        
        public CategoricalProperties setInitial(boolean isInitial) {
            this.isInitial = isInitial;
            return this;
        }
        
        public CategoricalProperties setTerminal(boolean isTerminal) {
            this.isTerminal = isTerminal;
            return this;
        }
        
        public CategoricalProperties setMonotonic(boolean isMonotonic) {
            this.isMonotonic = isMonotonic;
            return this;
        }
        
        public CategoricalProperties setClassical(boolean isClassical) {
            this.isClassical = isClassical;
            return this;
        }
        
        public CategoricalProperties setConstructive(boolean isConstructive) {
            this.isConstructive = isConstructive;
            return this;
        }
        
        public CategoricalProperties setParaconsistent(boolean isParaconsistent) {
            this.isParaconsistent = isParaconsistent;
            return this;
        }
        
        public CategoricalProperties setParacomplete(boolean isParacomplete) {
            this.isParacomplete = isParacomplete;
            return this;
        }
        
        public CategoricalProperties setResourceSensitive(boolean isResourceSensitive) {
            this.isResourceSensitive = isResourceSensitive;
            return this;
        }
        
        public boolean isInitial() { return isInitial; }
        public boolean isTerminal() { return isTerminal; }
        public boolean isMonotonic() { return isMonotonic; }
        public boolean isClassical() { return isClassical; }
        public boolean isConstructive() { return isConstructive; }
        public boolean isParaconsistent() { return isParaconsistent; }
        public boolean isParacomplete() { return isParacomplete; }
        public boolean isResourceSensitive() { return isResourceSensitive; }
    }
}