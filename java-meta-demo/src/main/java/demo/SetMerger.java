package demo;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete implementation of AbstractMerger bound to String type only.
 * This provides a positive guarantee: the type system enforces that this
 * merger only accepts Set<String> instances.
 * 
 * This demonstrates Java's type erasure and reification concepts,
 * where the generic type parameter is enforced at compile time.
 */
public class SetMerger implements AbstractMerger<String> {
    
    /**
     * Merges two String sets into a single combined set.
     * 
     * @param set1 the first set of strings
     * @param set2 the second set of strings
     * @return a new set containing all elements from both input sets
     * @throws NullPointerException if either set is null
     */
    @Override
    public Set<String> merge(Set<String> set1, Set<String> set2) {
        if (set1 == null || set2 == null) {
            throw new NullPointerException("Sets cannot be null");
        }
        
        Set<String> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }
}