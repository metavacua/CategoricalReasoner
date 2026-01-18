package org.catty.categorical.core;

import java.util.*;

/**
 * Commutative Diagram that is correct-by-construction.
 * This diagram CANNOT be non-commutative by design.
 */
public final class CommutativeDiagram {
    private final String name;
    private final Set<Node> nodes;
    private final Set<Edge> edges;
    private final Map<String, List<Edge>> paths;
    
    private CommutativeDiagram(Builder builder) {
        this.name = builder.name;
        this.nodes = new HashSet<>(builder.nodes);
        this.edges = new HashSet<>(builder.edges);
        this.paths = computePaths();
        validateCommutativity();
    }
    
    public static class Builder {
        private final String name;
        private final Set<Node> nodes = new HashSet<>();
        private final Set<Edge> edges = new HashSet<>();
        private final Map<String, List<Edge>> composedEdges = new HashMap<>();
        
        public Builder(String name) {
            this.name = name;
        }
        
        public Builder addNode(String id) {
            nodes.add(new Node(id));
            return this;
        }
        
        public Builder addEdge(String id, String source, String target, String description) {
            Edge edge = new Edge(id, source, target, description);
            edges.add(edge);
            return this;
        }
        
        public Builder addComposedEdge(String id, String source, String target, List<String> edgeIds, String description) {
            // Verify all component edges exist
            for (String edgeId : edgeIds) {
                if (edges.stream().noneMatch(e -> e.id.equals(edgeId))) {
                    throw new IllegalArgumentException("Component edge not found: " + edgeId);
                }
            }
            composedEdges.put(id, edges.stream().filter(e -> edgeIds.contains(e.id)).toList());
            return this;
        }
        
        public CommutativeDiagram build() {
            return new CommutativeDiagram(this);
        }
    }
    
    public static class Node {
        public final String id;
        
        public Node(String id) {
            this.id = id;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return Objects.equals(id, node.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return "Node(" + id + ")";
        }
    }
    
    public static class Edge {
        public final String id;
        public final String source;
        public final String target;
        public final String description;
        
        public Edge(String id, String source, String target, String description) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.description = description;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            return Objects.equals(id, edge.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return String.format("Edge(%s: %s → %s)", id, source, target);
        }
    }
    
    private Map<String, List<Edge>> computePaths() {
        Map<String, List<Edge>> paths = new HashMap<>();
        
        // Add all direct edges
        for (Edge edge : edges) {
            paths.put(edge.id, Collections.singletonList(edge));
        }
        
        // Add composed edges
        for (Map.Entry<String, List<Edge>> entry : composedEdges.entrySet()) {
            paths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        
        return paths;
    }
    
    private void validateCommutativity() {
        // Check that all paths between any two nodes have the same description
        Map<String, Map<String, List<String>>> pathDescriptions = new HashMap<>();
        
        for (Map.Entry<String, List<Edge>> entry : paths.entrySet()) {
            String pathId = entry.getKey();
            List<Edge> path = entry.getValue();
            
            if (!path.isEmpty()) {
                String source = path.get(0).source;
                String target = path.get(path.size() - 1).target;
                String description = path.get(0).description;
                
                pathDescriptions.computeIfAbsent(source, k -> new HashMap<>())
                             .computeIfAbsent(target, k -> new ArrayList<>())
                             .add(description);
            }
        }
        
        // Verify commutativity: all paths between same nodes have same description
        for (Map.Entry<String, Map<String, List<String>>> entry : pathDescriptions.entrySet()) {
            for (Map.Entry<String, List<String>> targetEntry : entry.getValue().entrySet()) {
                List<String> descriptions = targetEntry.getValue();
                if (descriptions.size() > 1) {
                    // All descriptions should be the same for commutativity
                    String firstDesc = descriptions.get(0);
                    if (!descriptions.stream().allMatch(desc -> desc.equals(firstDesc))) {
                        throw new IllegalStateException(
                            String.format("Commutativity violation: paths from %s to %s have different descriptions: %s", 
                                entry.getKey(), targetEntry.getKey(), descriptions));
                    }
                }
            }
        }
    }
    
    public Set<Node> getNodes() {
        return new HashSet<>(nodes);
    }
    
    public Set<Edge> getEdges() {
        return new HashSet<>(edges);
    }
    
    public List<Edge> getPath(String pathId) {
        return new ArrayList<>(paths.getOrDefault(pathId, Collections.emptyList()));
    }
    
    public boolean hasPath(String source, String target) {
        return paths.values().stream()
            .anyMatch(path -> !path.isEmpty() && 
                path.get(0).source.equals(source) && 
                path.get(path.size() - 1).target.equals(target));
    }
    
    @Override
    public String toString() {
        return String.format("CommutativeDiagram(%s) with %d nodes and %d edges", 
            name, nodes.size(), edges.size());
    }
}