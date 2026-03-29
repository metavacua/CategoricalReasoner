package org.catty.categorical.core;

import java.util.*;

/**
 * Minimal Logic (LM) - Initial object in the category of logics.
 * Correct logical signature: ∧, ∨, ⊤, ⊥, ⊢ (no negation, no implication)
 * This is the minimal base logic that can be extended to other logics.
 */
public final class MinimalLogic extends AbstractLogic {
    
    public MinimalLogic() {
        super("http://localhost/categorical-logics#", 
              "LM", 
              "Minimal Logic (Initial Object)", 
              new HashSet<>(Arrays.asList("∧", "∨", "⊤", "⊥", "⊢")));
        
        CategoricalProperties props = new CategoricalProperties(
            true,   // isInitial - LM is initial
            false,  // isTerminal
            true,   // isMonotonic
            false,  // isClassical
            true,   // isConstructive
            false,  // isParaconsistent
            false,  // isParacomplete
            false   // isResourceSensitive
        );
        
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
    
    /**
     * Verify that this logic has the minimal constructive signature
     */
    public boolean hasMinimalConstructiveSignature() {
        return hasConnective("∧") && hasConnective("∨") && 
               hasConnective("⊤") && hasConnective("⊥") && hasConnective("⊢") &&
               !hasConnective("¬") && !hasConnective("→") && !hasConnective("↔");
    }
}