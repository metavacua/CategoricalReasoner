package org.catty.categorical;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract Categorical Structure Framework
 * 
 * Provides a fully abstract foundation for characteristic data structures
 * of categories, generalizing to commutative n-polytopes and directed acyclic graphs.
 */
public abstract class AbstractCategoricalStructure<N> {
    
    protected final String name;
    protected final Map<String, Node<N>> nodes;
    protected final Map<String, Morphism<N>> morphisms;
    
    public AbstractCategoricalStructure(String name) {
        this.name = name;
        this.nodes = new HashMap<>();
        this.morphisms = new HashMap<>();
    }
    
    /**
     * Node in the categorical structure
     */
    public static class Node<N> {
        public final String id;
        public final N data;
        public final Set<String> incomingMorphismIds;
        public final Set<String> outgoingMorphismIds;
        
        public Node(String id, N data) {
            this.id = id;
            this.data = data;
            this.incomingMorphismIds = new HashSet<>();
            this.outgoingMorphismIds = new HashSet<>();
        }
        
        public void addIncomingMorphism(String morphismId) {
            incomingMorphismIds.add(morphismId);
        }
        
        public void addOutgoingMorphism(String morphismId) {
            outgoingMorphismIds.add(morphismId);
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
            return String.format("Node(%s: %s)", id, data);
        }
    }
    
    /**
     * Morphism in the categorical structure
     */
    public static class Morphism<N> {
        public final String id;
        public final String sourceNodeId;
        public final String targetNodeId;
        public final Function<N, N> transformation;
        public final String label;
        
        public Morphism(String id, String sourceNodeId, String targetNodeId, 
                       Function<N, N> transformation, String label) {
            this.id = id;
            this.sourceNodeId = sourceNodeId;
            this.targetNodeId = targetNodeId;
            this.transformation = transformation;
            this.label = label;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Morphism)) return false;
            Morphism morphism = (Morphism) o;
            return Objects.equals(id, morphism.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s → %s", label, sourceNodeId, targetNodeId);
        }
    }
    
    /**
     * Path in the categorical structure
     */
    public static class Path<N> {
        public final List<String> morphismIds;
        public final List<String> nodeIds;
        public final Function<N, N> composedTransformation;
        
        public Path(List<String> morphismIds, List<String> nodeIds, Function<N, N> composedTransformation) {
            this.morphismIds = morphismIds;
            this.nodeIds = nodeIds;
            this.composedTransformation = composedTransformation;
        }
        
        public boolean isEmpty() {
            return morphismIds.isEmpty();
        }
        
        public int length() {
            return morphismIds.size();
        }
        
        @Override
        public String toString() {
            return String.format("Path(%s) with %d morphisms", 
                String.join(" → ", nodeIds), morphismIds.size());
        }
    }
    
    // Abstract methods to be implemented by specific structures
    public abstract List<Path<N>> findAllPaths(String sourceNodeId, String targetNodeId);
    public abstract boolean isCommutative(String sourceNodeId, String targetNodeId);
    public abstract int getDimension();
    public abstract String getStructureType();
    
    // Common methods
    public void addNode(String nodeId, N data) {
        nodes.put(nodeId, new Node<>(nodeId, data));
    }
    
    public void addMorphism(String morphismId, String sourceNodeId, String targetNodeId,
                           Function<N, N> transformation, String label) {
        Morphism<N> morphism = new Morphism<>(morphismId, sourceNodeId, targetNodeId, transformation, label);
        morphisms.put(morphismId, morphism);
        
        // Update node connections
        nodes.get(sourceNodeId).addOutgoingMorphism(morphismId);
        nodes.get(targetNodeId).addIncomingMorphism(morphismId);
    }
    
    public Optional<Node<N>> getNode(String nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }
    
    public Optional<Morphism<N>> getMorphism(String morphismId) {
        return Optional.ofNullable(morphisms.get(morphismId));
    }
    
    public List<Node<N>> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }
    
    public List<Morphism<N>> getAllMorphisms() {
        return new ArrayList<>(morphisms.values());
    }
    
    public List<Morphism<N>> getOutgoingMorphisms(String nodeId) {
        return nodes.get(nodeId).outgoingMorphismIds.stream()
            .map(morphisms::get)
            .collect(Collectors.toList());
    }
    
    public List<Morphism<N>> getIncomingMorphisms(String nodeId) {
        return nodes.get(nodeId).incomingMorphismIds.stream()
            .map(morphisms::get)
            .collect(Collectors.toList());
    }
    
    public Function<N, N> composePath(List<String> morphismIds) {
        if (morphismIds.isEmpty()) {
            return Function.identity();
        }
        
        Function<N, N> composed = morphisms.get(morphismIds.get(0)).transformation;
        for (int i = 1; i < morphismIds.size(); i++) {
            Function<N, N> next = morphisms.get(morphismIds.get(i)).transformation;
            composed = composed.andThen(next);
        }
        return composed;
    }
    
    public Path<N> createPath(List<String> morphismIds) {
        if (morphismIds.isEmpty()) {
            return new Path<>(morphismIds, new ArrayList<>(), Function.identity());
        }
        
        // Build node sequence
        List<String> nodeIds = new ArrayList<>();
        nodeIds.add(morphisms.get(morphismIds.get(0)).sourceNodeId);
        for (String morphismId : morphismIds) {
            nodeIds.add(morphisms.get(morphismId).targetNodeId);
        }
        
        return new Path<>(morphismIds, nodeIds, composePath(morphismIds));
    }
    
    public boolean hasPath(String sourceNodeId, String targetNodeId) {
        return !findAllPaths(sourceNodeId, targetNodeId).isEmpty();
    }
    
    public Set<Path<N>> getAllPaths(String sourceNodeId, String targetNodeId) {
        return new HashSet<>(findAllPaths(sourceNodeId, targetNodeId));
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s) with %d nodes and %d morphisms", 
            getStructureType(), name, nodes.size(), morphisms.size());
    }
}