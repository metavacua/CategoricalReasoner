package org.catty.metaprogramming;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Demonstrates minimal Java metaprogramming techniques
 * for categorical logic processing and dynamic code generation.
 * 
 * This class showcases:
 * 1. Runtime reflection for dynamic property access
 * 2. Dynamic method invocation
 * 3. Code generation patterns for data transformation
 * 4. Type introspection for categorical structures
 */
public class MetaprogrammingDemo {
    
    /**
     * Example of a "categorical structure" - represents a logic object
     */
    public static class LogicObject {
        private String name;
        private String type;
        private Map<String, Object> properties;
        
        public LogicObject(String name, String type) {
            this.name = name;
            this.type = type;
            this.properties = new HashMap<>();
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public Map<String, Object> getProperties() { return properties; }
        public void setProperty(String key, Object value) { properties.put(key, value); }
        public Object getProperty(String key) { return properties.get(key); }
    }
    
    /**
     * Dynamic property accessor using reflection
     */
    public static class DynamicPropertyAccessor {
        
        public static Object getProperty(Object target, String propertyName) throws Exception {
            // First try to find a direct field
            Field field = findField(target.getClass(), propertyName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(target);
            }
            
            // If no direct field found, check if the object has a properties map
            try {
                Field propertiesField = findField(target.getClass(), "properties");
                if (propertiesField != null) {
                    propertiesField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = (Map<String, Object>) propertiesField.get(target);
                    if (properties != null && properties.containsKey(propertyName)) {
                        return properties.get(propertyName);
                    }
                }
            } catch (IllegalAccessException e) {
                // Ignore access issues
            }
            
            throw new NoSuchFieldException("Property '" + propertyName + "' not found in " + target.getClass().getName());
        }
        
        public static void setProperty(Object target, String propertyName, Object value) throws Exception {
            // First try to set a direct field
            Field field = findField(target.getClass(), propertyName);
            if (field != null) {
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
            
            // If no direct field found, check if the object has a properties map
            try {
                Field propertiesField = findField(target.getClass(), "properties");
                if (propertiesField != null) {
                    propertiesField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = (Map<String, Object>) propertiesField.get(target);
                    if (properties != null) {
                        properties.put(propertyName, value);
                        return;
                    }
                }
            } catch (IllegalAccessException e) {
                // Ignore access issues
            }
            
            throw new NoSuchFieldException("Property '" + propertyName + "' not found in " + target.getClass().getName());
        }
        
        private static Field findField(Class<?> clazz, String fieldName) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null) {
                    return findField(superClass, fieldName);
                }
                return null;
            }
        }
        
        public static Map<String, Object> getAllProperties(Object target) {
            Map<String, Object> properties = new HashMap<>();
            Class<?> clazz = target.getClass();
            
            // Get direct fields
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        properties.put(field.getName(), field.get(target));
                    } catch (IllegalAccessException e) {
                        // Skip inaccessible fields
                    }
                }
                clazz = clazz.getSuperclass();
            }
            
            // Also get properties from the "properties" map if it exists
            try {
                Field propertiesField = findField(target.getClass(), "properties");
                if (propertiesField != null) {
                    propertiesField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> propsMap = (Map<String, Object>) propertiesField.get(target);
                    if (propsMap != null) {
                        // Add map properties directly, preferring map values over field values
                        for (Map.Entry<String, Object> entry : propsMap.entrySet()) {
                            // Only add if not already present as a direct field
                            if (!properties.containsKey(entry.getKey())) {
                                properties.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                // Ignore access issues
            }
            
            return properties;
        }
    }
    
    /**
     * Dynamic method invoker for runtime method calls
     */
    public static class DynamicMethodInvoker {
        
        public static Object invokeMethod(Object target, String methodName, Object... args) throws Exception {
            Class<?>[] paramTypes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
            
            Method method = target.getClass().getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        }
        
        public static List<Method> findMethodsByPattern(Object target, String pattern) {
            return Arrays.stream(target.getClass().getMethods())
                .filter(method -> method.getName().matches(pattern))
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Code generation helper for creating transformation pipelines
     */
    public static class CodeGenerator {
        
        public static String generateEqualsMethod(String className, List<String> fields) {
            StringBuilder sb = new StringBuilder();
            sb.append("@Override\n");
            sb.append("public boolean equals(Object o) {\n");
            sb.append("    if (this == o) return true;\n");
            sb.append("    if (o == null || getClass() != o.getClass()) return false;\n");
            sb.append("    ").append(className).append(" that = (").append(className).append(") o;\n");
            
            for (String field : fields) {
                sb.append("    return Objects.equals(").append(field).append(", that.").append(field).append(");\n");
            }
            sb.append("}\n");
            return sb.toString();
        }
        
        public static String generateHashCodeMethod(List<String> fields) {
            StringBuilder sb = new StringBuilder();
            sb.append("@Override\n");
            sb.append("public int hashCode() {\n");
            sb.append("    return Objects.hash(").append(String.join(", ", fields)).append(");\n");
            sb.append("}\n");
            return sb.toString();
        }
        
        public static String generateToStringMethod(String className, List<String> fields) {
            StringBuilder sb = new StringBuilder();
            sb.append("@Override\n");
            sb.append("public String toString() {\n");
            sb.append("    return \"").append(className).append("{");
            
            String fieldFormat = fields.stream()
                .map(field -> field + "=\" + " + field + " + '\"'")
                .collect(Collectors.joining(", "));
            
            sb.append(fieldFormat).append("}'\");\n");
            sb.append("}\n");
            return sb.toString();
        }
    }
    
    /**
     * Categorical structure validator
     */
    public static class CategoricalValidator {
        
        public static boolean isValidLogicObject(Object obj) {
            if (!(obj instanceof LogicObject)) {
                return false;
            }
            
            LogicObject logicObj = (LogicObject) obj;
            return logicObj.getName() != null && 
                   !logicObj.getName().trim().isEmpty() &&
                   logicObj.getType() != null && 
                   !logicObj.getType().trim().isEmpty();
        }
        
        public static List<String> validateStructure(Object obj) {
            List<String> errors = new ArrayList<>();
            
            if (!(obj instanceof LogicObject)) {
                errors.add("Object must be instance of LogicObject");
                return errors;
            }
            
            LogicObject logicObj = (LogicObject) obj;
            
            if (logicObj.getName() == null || logicObj.getName().trim().isEmpty()) {
                errors.add("Logic object name cannot be null or empty");
            }
            
            if (logicObj.getType() == null || logicObj.getType().trim().isEmpty()) {
                errors.add("Logic object type cannot be null or empty");
            }
            
            if (logicObj.getProperties() == null) {
                errors.add("Logic object properties cannot be null");
            }
            
            return errors;
        }
        
        public static String generateValidationReport(Object obj) {
            List<String> errors = validateStructure(obj);
            if (errors.isEmpty()) {
                return "✓ Validation passed: " + obj.getClass().getSimpleName() + " is a valid categorical structure";
            } else {
                return "✗ Validation failed:\n" + errors.stream()
                    .map(error -> "  - " + error)
                    .collect(Collectors.joining("\n"));
            }
        }
    }
    
    /**
     * Main demonstration method
     */
    public static void demonstrate() throws Exception {
        System.out.println("=== Java Metaprogramming Demonstration ===\n");
        
        // 1. Create a sample logic object
        System.out.println("1. Creating LogicObject...");
        LogicObject logicObj = new LogicObject("ClassicalLogic", "LM");
        logicObj.setProperty("axioms", List.of("Axiom1", "Axiom2"));
        logicObj.setProperty("rules", List.of("ModusPonens"));
        System.out.println("Created: " + logicObj.getName() + " of type " + logicObj.getType());
        
        // 2. Dynamic property access
        System.out.println("\n2. Dynamic Property Access via Reflection:");
        Object name = DynamicPropertyAccessor.getProperty(logicObj, "name");
        System.out.println("Dynamically accessed 'name': " + name);
        
        Map<String, Object> allProps = DynamicPropertyAccessor.getAllProperties(logicObj);
        System.out.println("All properties: " + allProps);
        
        // 3. Dynamic method invocation
        System.out.println("\n3. Dynamic Method Invocation:");
        Object type = DynamicMethodInvoker.invokeMethod(logicObj, "getType");
        System.out.println("Dynamically invoked 'getType()': " + type);
        
        // 4. Code generation
        System.out.println("\n4. Generated Code Snippets:");
        List<String> fields = Arrays.asList("name", "type", "properties");
        System.out.println("Generated equals() method:\n" + CodeGenerator.generateEqualsMethod("LogicObject", fields));
        System.out.println("Generated hashCode() method:\n" + CodeGenerator.generateHashCodeMethod(fields));
        System.out.println("Generated toString() method:\n" + CodeGenerator.generateToStringMethod("LogicObject", fields));
        
        // 5. Validation
        System.out.println("\n5. Categorical Structure Validation:");
        System.out.println(CategoricalValidator.generateValidationReport(logicObj));
        
        // 6. JSON serialization demonstration
        System.out.println("\n6. JSON Serialization:");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(logicObj);
        System.out.println("LogicObject as JSON: " + json);
        
        System.out.println("\n=== Demonstration Complete ===");
    }
}