package org.catty.categorical.core;

import java.util.*;

/**
 * Concrete implementation of Classical First-Order Predicate Logic.
 * Terminal object in the category of logics with complete first-order signature.
 */
public final class ClassicalLogic extends AbstractLogic {
    
    public ClassicalLogic() {
        super("http://localhost/categorical-logics#", 
              "LK", 
              "Classical First-Order Predicate Logic", 
              new HashSet<>(Arrays.asList(
                  "∧", "∨", "¬", "→", "↔", "⊤", "⊥", "⊢",  // Propositional
                  "∀", "∃", "=",  // First-order quantifiers and equality
                  "LEM", "LNC", "Explosion", "DNE", "Peirce"))); // Classical axioms
        
        CategoricalProperties props = new CategoricalProperties(
            false, // isInitial
            true,  // isTerminal - LK is terminal
            true,  // isMonotonic
            true,  // isClassical - classical logic
            false, // isConstructive - not constructive
            false, // isParaconsistent
            false, // isParacomplete
            false  // isResourceSensitive
        );
        
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
    
    /**
     * Check if this logic has first-order predicate structure
     */
    public boolean hasFirstOrderStructure() {
        return hasConnective("∀") && hasConnective("∃") && hasConnective("=");
    }
    
    /**
     * Check if this logic has classical propositional structure
     */
    public boolean hasClassicalPropositionalStructure() {
        return hasConnective("∧") && hasConnective("∨") && hasConnective("¬") && 
               hasConnective("→") && hasConnective("↔");
    }
    
    /**
     * Check if this logic has all classical axioms
     */
    public boolean hasAllClassicalAxioms() {
        return hasConnective("LEM") && hasConnective("LNC") && 
               hasConnective("Explosion") && hasConnective("DNE") && hasConnective("Peirce");
    }
}