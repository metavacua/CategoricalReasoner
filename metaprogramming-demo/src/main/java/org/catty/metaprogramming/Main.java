package org.catty.metaprogramming;

/**
 * Main entry point for the Java metaprogramming demonstration.
 * 
 * This program showcases minimal Java metaprogramming techniques
 * applicable to categorical logic processing, including:
 * - Runtime reflection
 * - Dynamic method invocation
 * - Code generation patterns
 * - Type introspection
 * 
 * Build instructions:
 *   mvn clean package
 * 
 * Run instructions:
 *   mvn exec:java -Dexec.mainClass="org.catty.metaprogramming.Main"
 * 
 * Or run the compiled JAR:
 *   java -jar target/metaprogramming-demo-1.0.0.jar
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  Catty - Java Metaprogramming Demonstration                  ║");
            System.out.println("║  Categorical Logic Processing with Reflection               ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            
            // Run the metaprogramming demonstration
            MetaprogrammingDemo.demonstrate();
            
            // Exit successfully
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("Error during demonstration: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}