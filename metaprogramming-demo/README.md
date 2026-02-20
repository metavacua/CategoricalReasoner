# Java Metaprogramming Demonstration

A minimal Java metaprogramming demonstration for categorical logic processing, showcasing reflection, dynamic method invocation, code generation, and type introspection.

## Prerequisites

### Java Development Kit (JDK) 21

**macOS (Homebrew):**
```bash
brew install openjdk@21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install openjdk-21-jdk
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

**Windows:**
Download and install from [Adoptium](https://adoptium.net/temurin/releases/?version=21).

Verify installation:
```bash
java -version
# Should output: openjdk version "21.x.x"
```

### Maven 3.8+

**macOS (Homebrew):**
```bash
brew install maven
```

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install maven
```

**Windows:**
Download from [Apache Maven](https://maven.apache.org/download.cgi) and extract to a local directory, then add `apache-maven-X.X.X/bin` to your PATH.

Verify installation:
```bash
mvn -version
# Should output: Apache Maven 3.8+
```

## Project Structure

```
metaprogramming-demo/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── org/
    │           └── catty/
    │               └── metaprogramming/
    │                   ├── MetaprogrammingDemo.java   # Core demonstration
    │                   └── Main.java                  # Entry point
    └── test/
        └── java/
            └── org/
                └── catty/
                    └── metaprogramming/
                        └── MetaprogrammingDemoTest.java # Unit tests
```

## Build Instructions

### Full Build (Compile + Test + Package)

```bash
cd metaprogramming-demo
mvn clean package
```

This will:
1. Clean previous build artifacts
2. Compile source code
3. Run unit tests
4. Package the application into a JAR

### Compile Only

```bash
mvn compile
```

### Run Tests Only

```bash
mvn test
```

### Package Without Tests

```bash
mvn package -DskipTests
```

## Running the Application

### Using Maven Exec Plugin

```bash
mvn exec:java -Dexec.mainClass="org.catty.metaprogramming.Main"
```

### Using the Compiled JAR

```bash
java -jar target/metaprogramming-demo-1.0.0.jar
```

### Run Specific Test Class

```bash
mvn test -Dtest=MetaprogrammingDemoTest
```

### Run Specific Test Method

```bash
mvn test -Dtest=MetaprogrammingDemoTest#testDynamicPropertyAccess
```

## Features Demonstrated

### 1. Runtime Reflection

Dynamic property access and modification using Java Reflection API:

```java
// Get property value dynamically
Object value = DynamicPropertyAccessor.getProperty(target, "propertyName");

// Set property value dynamically
DynamicPropertyAccessor.setProperty(target, "propertyName", newValue);

// Get all properties
Map<String, Object> allProps = DynamicPropertyAccessor.getAllProperties(target);
```

### 2. Dynamic Method Invocation

Runtime method calling without compile-time binding:

```java
// Invoke method with arguments
Object result = DynamicMethodInvoker.invokeMethod(target, "methodName", arg1, arg2);

// Find methods by naming pattern
List<Method> getters = DynamicMethodInvoker.findMethodsByPattern(target, "get.*");
```

### 3. Code Generation

Generate standard methods for data classes:

```java
// Generate equals(), hashCode(), and toString() methods
String equalsMethod = CodeGenerator.generateEqualsMethod("ClassName", fields);
String hashCodeMethod = CodeGenerator.generateHashCodeMethod(fields);
String toStringMethod = CodeGenerator.generateToStringMethod("ClassName", fields);
```

### 4. Type Introspection

Inspect class structure and validate categorical structures:

```java
// Validate logic objects
boolean isValid = CategoricalValidator.isValidLogicObject(obj);
List<String> errors = CategoricalValidator.validateStructure(obj);
String report = CategoricalValidator.generateValidationReport(obj);
```

### 5. JSON Serialization

Using Jackson for dynamic object serialization:

```java
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(obj);
LogicObject deserialized = mapper.readValue(json, LogicObject.class);
```

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Jackson Databind | 2.17.2 | JSON serialization |
| JUnit Jupiter | 5.11.3 | Unit testing |
| Commons Lang3 | 3.17.0 | Utility methods |

## Troubleshooting

### "java: invalid source release: 21"

Ensure JAVA_HOME points to JDK 21:
```bash
echo $JAVA_HOME
# Should show path to JDK 21
```

### "mvn: command not found"

Ensure Maven is installed and in your PATH:
```bash
which mvn
# Should show Maven path
```

### Compilation Errors

Ensure you have the correct Java version:
```bash
java -version  # Should show 21
mvn -version   # Should show Maven 3.8+
```

## License

This project is part of the Catty thesis repository, licensed under AGPL-3.0. See the root LICENSE file for details.
