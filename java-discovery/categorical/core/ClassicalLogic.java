package org.catty.categorical.core;

import java.util.*;

/**
 * Concrete implementation of Classical Propositional Logic.
 * Represents the terminal object in the category of logics.
 */
public class ClassicalLogic extends AbstractLogic {
    
    public ClassicalLogic() {
        super("CPL", 
              "Classical Propositional Logic", 
              new HashSet<>(Arrays.asList("AND", "OR", "NOT", "IMPLIES", "IFF", "XOR")));
        
        CategoricalProperties props = new CategoricalProperties()
            .setTerminal(true)
            .setClassical(true)
            .setMonotonic(true)
            .setConstructive(false);
        
        setProperty("categoricalProperties", props);
    }
    
    @Override
    public boolean isInitial() {
        return false;
    }
    
    @Override
    public boolean isTerminal() {
        return true;
    }
    
    @Override
    public CategoricalProperties getCategoricalProperties() {
        return (CategoricalProperties) getProperty("categoricalProperties");
    }
    
    public boolean isClassical() {
        return getCategoricalProperties().isClassical();
    }
    
    public boolean isConstructive() {
        return getCategoricalProperties().isConstructive();
    }
    
    public boolean hasBooleanAlgebra() {
        return hasConnective("NOT") && hasConnective("IMPLIES") && 
               hasConnective("AND") && hasConnective("OR");
    }
}