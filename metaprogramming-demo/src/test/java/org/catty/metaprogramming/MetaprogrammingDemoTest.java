package org.catty.metaprogramming;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test suite for metaprogramming demonstration components.
 * 
 * Tests cover:
 * - Dynamic property access
 * - Method invocation
 * - Code generation validation
 * - Categorical structure validation
 */
public class MetaprogrammingDemoTest {
    
    private MetaprogrammingDemo.LogicObject testLogicObject;
    
    @BeforeEach
    void setUp() {
        testLogicObject = new MetaprogrammingDemo.LogicObject("TestLogic", "LJ");
        testLogicObject.setProperty("testProperty", "testValue");
        testLogicObject.setProperty("number", 42);
    }
    
    @Test
    void testLogicObjectCreation() {
        assertNotNull(testLogicObject.getName());
        assertEquals("TestLogic", testLogicObject.getName());
        assertEquals("LJ", testLogicObject.getType());
        assertNotNull(testLogicObject.getProperties());
    }
    
    @Test
    void testDynamicPropertyAccess() throws Exception {
        // Test getting properties via reflection
        Object name = MetaprogrammingDemo.DynamicPropertyAccessor.getProperty(testLogicObject, "name");
        assertEquals("TestLogic", name);
        
        Object type = MetaprogrammingDemo.DynamicPropertyAccessor.getProperty(testLogicObject, "type");
        assertEquals("LJ", type);
        
        // Test setting properties via reflection
        MetaprogrammingDemo.DynamicPropertyAccessor.setProperty(testLogicObject, "name", "UpdatedLogic");
        assertEquals("UpdatedLogic", testLogicObject.getName());
    }
    
    @Test
    void testDynamicPropertyAccessorAllProperties() {
        Map<String, Object> allProps = MetaprogrammingDemo.DynamicPropertyAccessor.getAllProperties(testLogicObject);
        
        assertTrue(allProps.containsKey("name"));
        assertTrue(allProps.containsKey("type"));
        assertTrue(allProps.containsKey("properties"));
        assertTrue(allProps.containsKey("testProperty"));
        assertEquals("TestLogic", allProps.get("name"));
        assertEquals("LJ", allProps.get("type"));
    }
    
    @Test
    void testDynamicMethodInvocation() throws Exception {
        Object name = MetaprogrammingDemo.DynamicMethodInvoker.invokeMethod(testLogicObject, "getName");
        assertEquals("TestLogic", name);
        
        Object type = MetaprogrammingDemo.DynamicMethodInvoker.invokeMethod(testLogicObject, "getType");
        assertEquals("LJ", type);
    }
    
    @Test
    void testCodeGeneration() {
        List<String> fields = List.of("field1", "field2", "field3");
        
        String equalsMethod = MetaprogrammingDemo.CodeGenerator.generateEqualsMethod("TestClass", fields);
        assertTrue(equalsMethod.contains("public boolean equals"));
        assertTrue(equalsMethod.contains("TestClass that = (TestClass) o"));
        
        String hashCodeMethod = MetaprogrammingDemo.CodeGenerator.generateHashCodeMethod(fields);
        assertTrue(hashCodeMethod.contains("public int hashCode"));
        assertTrue(hashCodeMethod.contains("Objects.hash"));
        
        String toStringMethod = MetaprogrammingDemo.CodeGenerator.generateToStringMethod("TestClass", fields);
        assertTrue(toStringMethod.contains("public String toString"));
        assertTrue(toStringMethod.contains("TestClass{"));
    }
    
    @Test
    void testCategoricalValidator() {
        // Test valid logic object
        assertTrue(MetaprogrammingDemo.CategoricalValidator.isValidLogicObject(testLogicObject));
        
        // Create invalid logic object
        MetaprogrammingDemo.LogicObject invalidLogic = new MetaprogrammingDemo.LogicObject("", "LJ");
        assertFalse(MetaprogrammingDemo.CategoricalValidator.isValidLogicObject(invalidLogic));
        
        // Test validation report
        String report = MetaprogrammingDemo.CategoricalValidator.generateValidationReport(testLogicObject);
        assertTrue(report.contains("Validation passed"));
        
        String invalidReport = MetaprogrammingDemo.CategoricalValidator.generateValidationReport(invalidLogic);
        assertTrue(invalidReport.contains("Validation failed"));
        assertTrue(invalidReport.contains("Logic object name cannot be null or empty"));
    }
    
    @Test
    void testPropertyAccessWithDynamicValues() throws Exception {
        // Add multiple properties dynamically
        testLogicObject.setProperty("intValue", 100);
        testLogicObject.setProperty("boolValue", true);
        testLogicObject.setProperty("listValue", List.of("item1", "item2"));
        
        // Access them dynamically
        assertEquals(100, MetaprogrammingDemo.DynamicPropertyAccessor.getProperty(testLogicObject, "intValue"));
        assertEquals(true, MetaprogrammingDemo.DynamicPropertyAccessor.getProperty(testLogicObject, "boolValue"));
        
        @SuppressWarnings("unchecked")
        List<String> listValue = (List<String>) MetaprogrammingDemo.DynamicPropertyAccessor.getProperty(testLogicObject, "listValue");
        assertEquals(2, listValue.size());
        assertEquals("item1", listValue.get(0));
    }
}