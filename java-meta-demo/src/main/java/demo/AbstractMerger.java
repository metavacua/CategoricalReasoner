package demo;

import java.util.Set;

/**
 * Generic functional interface demonstrating type-safe merge contract.
 * This abstraction layer declares the merge operation without binding
 * to a specific type, allowing implementations to provide type constraints.
 *
 * @param <T> the type of elements in the sets to merge
 */
@FunctionalInterface
public interface AbstractMerger<T> {
    
    /**
     * Merges two sets into a single combined set.
     *
     * @param set1 the first set
     * @param set2 the second set
     * @return a new set containing all elements from both input sets
     */
    Set<T> merge(Set<T> set1, Set<T> set2);
}
