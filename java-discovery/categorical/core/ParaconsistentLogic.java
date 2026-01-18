package org.catty.categorical.core;

import java.util.*;

/**
 * Concrete implementation of Paraconsistent Paracomplete Subclassical Logic.
 * Represents the initial object in the category of logics.
 */
public class ParaconsistentLogic extends AbstractLogic {
    
    public ParaconsistentLogic() {
        super("PPSC", 
              "Paraconsistent Paracomplete Subclassical Logic", 
              new HashSet<>(Arrays.asList("AND", "OR")));
        
        CategoricalProperties props = new CategoricalProperties()
            .setInitial(true)
            .setMonotonic(true)
            .setParaconsistent(true)
            .setParacomplete(true);
        
        setProperty("categoricalProperties", props);
    }
    
    @Override
    public boolean isInitial() {
        return true;
    }
    
    @Override
    public boolean isTerminal() {
        return false;
    }
    
    @Override
    public CategoricalProperties getCategoricalProperties() {
        return (CategoricalProperties) getProperty("categoricalProperties");
    }
    
    public boolean isMonotonic() {
        return getCategoricalProperties().isMonotonic();
    }
    
    public boolean isParaconsistent() {
        return getCategoricalProperties().isParaconsistent();
    }
    
    public boolean isParacomplete() {
        return getCategoricalProperties().isParacomplete();
    }
}