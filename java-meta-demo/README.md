# Java Metaprogramming Demo

Minimal demonstration of Java type safety and metaprogramming concepts through generic interfaces and compile-time verification.

## Prerequisites

```bash
# Install Java 21 (using SDKMAN as example)
sdk install java 21.0.2-tem

# Install Maven
sdk install maven
```

## Build & Run

```bash
# Compile and test
mvn clean compile test

# Run specific test
mvn test -Dtest=PositiveTest
mvn test -Dtest=NegativeTest
```

## Project Structure

- **AbstractMerger.java**: Generic functional interface declaring merge contract
- **SetMerger.java**: Concrete implementation binding to `Set<String>` only
- **PositiveTest.java**: Demonstrates correct usage (compiles and passes)
- **NegativeTest.java**: Shows type violations cause compile errors

## Type Safety Guarantees

### Positive Guarantee
The type system enforces that `SetMerger` only accepts `Set<String>`:
```java
Set<String> set1 = Set.of("a", "b");
Set<String> set2 = Set.of("c", "d");
SetMerger merger = new SetMerger();
Set<String> result = merger.merge(set1, set2); // ✅ Compiles
```

### Negative Guarantee
Attempting invalid type combinations fails at compile time:
```java
Set<Integer> intSet1 = Set.of(1, 2);
Set<Integer> intSet2 = Set.of(3, 4);
SetMerger merger = new SetMerger();
merger.merge(intSet1, intSet2); // ❌ Compile error
```

The Java compiler acts as a proof assistant, preventing runtime type errors through compile-time verification.