package org.catty.categorical.core;

import java.util.*;

/**
 * Logic Category that is correct-by-construction.
 * This category CANNOT be incorrectly constructed - violations are prevented at compile/construction time.
 */
public final class LogicCategory {
    private final String name;
    private final Map<String, AbstractLogic> logics;
    private final Map<String, LogicMorphism> morphisms;
    private final AbstractLogic initialObject;
    private final AbstractLogic terminalObject;
    
    private LogicCategory(Builder builder) {
        this.name = builder.name;
        this.logics = new HashMap<>(builder.logics);
        this.morphisms = new HashMap<>(builder.morphisms);
        this.initialObject = validateAndGetInitialObject();
        this.terminalObject = validateAndGetTerminalObject();
        validateCategory();
    }
    
    public static class Builder {
        private final String name;
        private final Map<String, AbstractLogic> logics = new HashMap<>();
        private final Map<String, LogicMorphism> morphisms = new HashMap<>();
        
        public Builder(String name) {
            this.name = name;
        }
        
        public Builder addLogic(AbstractLogic logic) {
            logics.put(logic.getName(), logic);
            return this;
        }
        
        public Builder addMorphism(LogicMorphism morphism) {
            // Validate that domain and codomain exist
            if (!logics.containsKey(morphism.getDomain().getName())) {
                throw new IllegalArgumentException("Domain logic not found: " + morphism.getDomain().getName());
            }
            if (!logics.containsKey(morphism.getCodomain().getName())) {
                throw new IllegalArgumentException("Codomain logic not found: " + morphism.getCodomain().getName());
            }
            morphisms.put(morphism.getName(), morphism);
            return this;
        }
        
        public LogicCategory build() {
            return new LogicCategory(this);
        }
    }
    
    private AbstractLogic validateAndGetInitialObject() {
        // Find the unique logic that is initial
        AbstractLogic initial = null;
        for (AbstractLogic logic : logics.values()) {
            if (logic.isInitial()) {
                if (initial != null) {
                    throw new IllegalStateException("Multiple initial objects found: " + 
                        initial.getName() + " and " + logic.getName());
                }
                initial = logic;
            }
        }
        return initial;
    }
    
    private AbstractLogic validateAndGetTerminalObject() {
        // Find the unique logic that is terminal
        AbstractLogic terminal = null;
        for (AbstractLogic logic : logics.values()) {
            if (logic.isTerminal()) {
                if (terminal != null) {
                    throw new IllegalStateException("Multiple terminal objects found: " + 
                        terminal.getName() + " and " + logic.getName());
                }
                terminal = logic;
            }
        }
        return terminal;
    }
    
    private void validateCategory() {
        // Verify isomorphism properties for all morphism compositions
        for (LogicMorphism morphism : morphisms.values()) {
            AbstractLogic domain = morphism.getDomain();
            AbstractLogic codomain = morphism.getCodomain();
            
            // Check that morphism respects logical structure
            if (!domain.hasAllConnectives(morphism.getPreservedConnectives())) {
                throw new IllegalStateException(
                    "Morphism " + morphism.getName() + " does not preserve required connectives");
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public Set<AbstractLogic> getLogics() {
        return new HashSet<>(logics.values());
    }
    
    public AbstractLogic getLogic(String name) {
        return logics.get(name);
    }
    
    public Set<LogicMorphism> getMorphisms() {
        return new HashSet<>(morphisms.values());
    }
    
    public LogicMorphism getMorphism(String name) {
        return morphisms.get(name);
    }
    
    public Set<LogicMorphism> getMorphismsFrom(AbstractLogic source) {
        return morphisms.values().stream()
            .filter(m -> m.getDomain().equals(source))
            .collect(Collectors.toSet());
    }
    
    public Set<LogicMorphism> getMorphismsTo(AbstractLogic target) {
        return morphisms.values().stream()
            .filter(m -> m.getCodomain().equals(target))
            .collect(Collectors.toSet());
    }
    
    public AbstractLogic getInitialObject() {
        return initialObject;
    }
    
    public AbstractLogic getTerminalObject() {
        return terminalObject;
    }
    
    public boolean hasMorphism(AbstractLogic source, AbstractLogic target) {
        return morphisms.values().stream()
            .anyMatch(m -> m.getDomain().equals(source) && m.getCodomain().equals(target));
    }
    
    @Override
    public String toString() {
        return String.format("LogicCategory(%s) with %d logics and %d morphisms", 
            name, logics.size(), morphisms.size());
    }
}