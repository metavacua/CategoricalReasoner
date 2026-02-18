package demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Positive test demonstrating correct usage of SetMerger.
 * This test proves that the type system correctly allows valid type
 * combinations and the merge operation works as expected.
 */
class PositiveTest {
    
    @Test
    @DisplayName("SetMerger correctly merges two String sets")
    void testMergeStringSets() {
        // Given: Two valid String sets
        Set<String> set1 = Set.of("a", "b", "c");
        Set<String> set2 = Set.of("c", "d", "e");
        
        // When: Using SetMerger with String type
        SetMerger merger = new SetMerger();
        Set<String> result = merger.merge(set1, set2);
        
        // Then: Result contains all unique elements
        assertEquals(5, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
        assertTrue(result.contains("d"));
        assertTrue(result.contains("e"));
    }
    
    @Test
    @DisplayName("SetMerger handles empty sets")
    void testMergeWithEmptySets() {
        // Given: One empty set, one non-empty set
        Set<String> emptySet = Set.of();
        Set<String> nonEmptySet = Set.of("x", "y");
        
        // When: Merging
        SetMerger merger = new SetMerger();
        Set<String> result = merger.merge(emptySet, nonEmptySet);
        
        // Then: Result contains only non-empty set elements
        assertEquals(2, result.size());
        assertTrue(result.contains("x"));
        assertTrue(result.contains("y"));
    }
    
    @Test
    @DisplayName("SetMerger returns new set instance")
    void testReturnsNewSet() {
        // Given: Two sets
        Set<String> set1 = Set.of("a");
        Set<String> set2 = Set.of("b");
        
        // When: Merging
        SetMerger merger = new SetMerger();
        Set<String> result = merger.merge(set1, set2);
        
        // Then: Result is a new object, not the same as input
        assertNotSame(set1, result);
        assertNotSame(set2, result);
    }
}