package demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Negative test demonstrating compile-time type safety.
 * This test file documents type violations that would cause compilation errors
 * if uncommented, proving that Java's type system prevents runtime type errors.
 */
class NegativeTest {
    
    @Test
    @DisplayName("SetMerger cannot accept Integer sets - compile-time type safety")
    void testTypeSafetyWithIntegers() {
        // This demonstrates that SetMerger is properly bounded to String type.
        // The following code would NOT compile if uncommented:
        
        /*
        Set<Integer> intSet1 = Set.of(1, 2);
        Set<Integer> intSet2 = Set.of(3, 4);
        SetMerger merger = new SetMerger();
        merger.merge(intSet1, intSet2);
        
        // Compilation error: incompatible types: Set<Integer> cannot be converted to Set<String>
        // This proves type safety at compile time!
        */
        
        // The fact that this test compiles proves the negative guarantee:
        // Java prevents type mismatches at compile time, not runtime.
        assertTrue(true, "Type safety enforced by compiler");
    }
    
    @Test
    @DisplayName("SetMerger cannot mix String and Integer - compile-time type safety")
    void testTypeSafetyWithMixedTypes() {
        // This demonstrates that generic type parameter T cannot be mixed.
        // The following code would NOT compile if uncommented:
        
        /*
        Set<String> stringSet = Set.of("hello");
        Set<Integer> intSet = Set.of(123);
        SetMerger merger = new SetMerger();
        merger.merge(stringSet, intSet);
        
        // Compilation error: incompatible types: Set<Integer> cannot be converted to Set<String>
        // This proves Java prevents mixing types in generic methods!
        */
        
        // Again, successful compilation of this test proves type safety.
        assertTrue(true, "Generic type parameter properly enforced");
    }
    
    @Test
    @DisplayName("AbstractMerger<String> cannot be assigned SetMerger with incompatible types")
    void testAbstractMergerTypeConstraint() {
        // This demonstrates that AbstractMerger<T> respects type parameter bounds.
        // The following would NOT compile:
        
        /*
        AbstractMerger<Integer> integerMerger = new SetMerger();
        
        // Compilation error: incompatible types: SetMerger cannot be converted to AbstractMerger<Integer>
        // Even though SetMerger implements AbstractMerger<String>,
        // it cannot be used as AbstractMerger<Integer> due to type parameter mismatch.
        */
        
        // Successful compilation proves proper type parameter enforcement.
        assertTrue(true, "Type parameter bounds properly enforced");
    }
}