package org.catty.discovery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A generic commutative diagram implementation.
 * Tests whether Java's type system can naturally express categorical structures.
 * 
 * N = Node type (e.g., Logic)
 * A = Arrow type (e.g., Morphism)
 */
public class CommutativeDiagram<N, A> {
    
    private final Set<N> nodes;
    private final Map<String, Arrow<N, A>> arrows;
    
    public CommutativeDiagram() {
        this.nodes = new HashSet<>();
        this.arrows = new HashMap<>();
    }
    
    public void addNode(N node) {
        nodes.add(node);
    }
    
    public void addArrow(N from, N to, A morphism, String name) {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new IllegalArgumentException("Nodes must be added before arrows");
        }
        arrows.put(name, new Arrow<>(from, to, morphism, name));
    }
    
    public boolean isCommutative() {
        // Check all possible paths between any two nodes
        for (N start : nodes) {
            for (N end : nodes) {
                if (!start.equals(end)) {
                    List<List<Arrow<N, A>>> paths = findAllPaths(start, end);
                    if (paths.size() > 1) {
                        // Multiple paths exist, check if they're equal
                        Set<A> pathResults = new HashSet<>();
                        for (List<Arrow<N, A>> path : paths) {
                            A composed = composePath(path);
                            if (composed != null) {
                                pathResults.add(composed);
                            }
                        }
                        if (pathResults.size() > 1) {
                            return false; // Different results = not commutative
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private List<List<Arrow<N, A>>> findAllPaths(N start, N end) {
        List<List<Arrow<N, A>>> allPaths = new ArrayList<>();
        findPathsInternal(start, end, new ArrayList<>(), allPaths, new HashSet<>());
        return allPaths;
    }
    
    private void findPathsInternal(N current, N target, List<Arrow<N, A>> currentPath, 
                                 List<List<Arrow<N, A>>> allPaths, Set<N> visited) {
        if (current.equals(target)) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }
        
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        
        // Find all outgoing arrows from current node
        for (Arrow<N, A> arrow : arrows.values()) {
            if (arrow.from.equals(current)) {
                currentPath.add(arrow);
                findPathsInternal(arrow.to, target, currentPath, allPaths, visited);
                currentPath.remove(arrow);
            }
        }
        
        visited.remove(current);
    }
    
    private A composePath(List<Arrow<N, A>> path) {
        // This would need implementation based on the specific morphism type A
        // For now, return null to indicate composition not implemented
        return null;
    }
    
    public Set<N> getNodes() {
        return new HashSet<>(nodes);
    }
    
    public Collection<Arrow<N, A>> getArrows() {
        return arrows.values();
    }
    
    public Arrow<N, A> getArrow(String name) {
        return arrows.get(name);
    }
    
    // Helper class to represent arrows
    public static class Arrow<N, A> {
        public final N from;
        public final N to;
        public final A morphism;
        public final String name;
        
        public Arrow(N from, N to, A morphism, String name) {
            this.from = from;
            this.to = to;
            this.morphism = morphism;
            this.name = name;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Arrow)) return false;
            Arrow arrow = (Arrow) o;
            return Objects.equals(from, arrow.from) && 
                   Objects.equals(to, arrow.to) && 
                   Objects.equals(morphism, arrow.morphism) && 
                   Objects.equals(name, arrow.name);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(from, to, morphism, name);
        }
    }
}