package org.catty.categorical;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Directed Acyclic Graph (DAG) Categorical Structure
 * 
 * Provides general DAG support for categorical data structures.
 * Supports partial orders, hierarchical structures, and dependency graphs.
 */
public class DAGCategoricalStructure<N> extends AbstractCategoricalStructure<N> {
    
    private final Map<String, DAGNode<N>> dagNodes;
    private final Map<String, List<String>> adjacencyList;
    private final Map<String, Integer> topologicalOrder;
    private final Set<String> sourceNodes;
    private final Set<String> sinkNodes;
    
    /**
     * DAG-specific node with additional topological information
     */
    public static class DAGNode<N> extends Node<N> {
        public final int topologicalIndex;
        public final int depth;
        public final Set<String> ancestors;
        public final Set<String> descendants;
        public final List<String> directPredecessors;
        public final List<String> directSuccessors;
        
        public DAGNode(String id, N data, int topologicalIndex, int depth) {
            super(id, data);
            this.topologicalIndex = topologicalIndex;
            this.depth = depth;
            this.ancestors = new HashSet<>();
            this.descendants = new HashSet<>();
            this.directPredecessors = new ArrayList<>();
            this.directSuccessors = new ArrayList<>();
        }
        
        public boolean isSource() {
            return directPredecessors.isEmpty();
        }
        
        public boolean isSink() {
            return directSuccessors.isEmpty();
        }
        
        public boolean hasPathTo(String otherNodeId) {
            return descendants.contains(otherNodeId);
        }
        
        public boolean hasPathFrom(String otherNodeId) {
            return ancestors.contains(otherNodeId);
        }
        
        public boolean isAncestorOf(String otherNodeId) {
            return hasPathTo(otherNodeId);
        }
        
        public boolean isDescendantOf(String otherNodeId) {
            return hasPathFrom(otherNodeId);
        }
        
        @Override
        public String toString() {
            return String.format("DAGNode(%s, depth=%d, topological=%d)", id, depth, topologicalIndex);
        }
    }
    
    /**
     * Path in DAG with additional information
     */
    public static class DAGPath<N> extends Path<N> {
        public final int length;
        public final int minDepth;
        public final int maxDepth;
        public final boolean isSourceToSink;
        public final List<String> intermediateNodes;
        
        public DAGPath(List<String> morphismIds, List<String> nodeIds, Function<N, N> composedTransformation) {
            super(morphismIds, nodeIds, composedTransformation);
            this.length = morphismIds.size();
            this.minDepth = 0; // Would be calculated from DAG nodes
            this.maxDepth = 0;  // Would be calculated from DAG nodes
            this.isSourceToSink = false; // Would be determined from node types
            this.intermediateNodes = nodeIds.subList(1, nodeIds.size() - 1);
        }
        
        public boolean isDirectPath() {
            return length == 1;
        }
        
        public boolean hasIntermediateNodes() {
            return !intermediateNodes.isEmpty();
        }
        
        public boolean isMinimal() {
            // Path is minimal if no proper subsequence is also a path
            return true; // Simplified check
        }
        
        public boolean isMaximal() {
            // Path is maximal if it cannot be extended
            return true; // Simplified check
        }
        
        @Override
        public String toString() {
            String pathType = isSourceToSink ? "source-to-sink" : "intermediate";
            return String.format("DAGPath(%s, %s, length=%d)", 
                String.join(" → ", nodeIds), pathType, length);
        }
    }
    
    /**
     * Commutativity pattern in DAG
     */
    public static class CommutativityPattern {
        public final String id;
        public final List<String> sourceNodeIds;
        public final List<String> targetNodeIds;
        public final Map<String, List<String>> alternativePaths; // Multiple paths between same nodes
        public final String description;
        
        public CommutativityPattern(String id, List<String> sourceNodeIds, List<String> targetNodeIds,
                                  Map<String, List<String>> alternativePaths, String description) {
            this.id = id;
            this.sourceNodeIds = new ArrayList<>(sourceNodeIds);
            this.targetNodeIds = new ArrayList<>(targetNodeIds);
            this.alternativePaths = new HashMap<>(alternativePaths);
            this.description = description;
        }
        
        public boolean hasMultiplePaths(String source, String target) {
            String key = source + "_" + target;
            return alternativePaths.containsKey(key) && alternativePaths.get(key).size() > 1;
        }
        
        public List<String> getPaths(String source, String target) {
            String key = source + "_" + target;
            return alternativePaths.getOrDefault(key, new ArrayList<>());
        }
        
        @Override
        public String toString() {
            return String.format("CommutativityPattern_%s: %s → %s [%d alternative paths]", 
                id, sourceNodeIds, targetNodeIds, alternativePaths.size());
        }
    }
    
    private final List<CommutativityPattern> commutativityPatterns;
    
    public DAGCategoricalStructure(String name) {
        super(name);
        this.dagNodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.topologicalOrder = new HashMap<>();
        this.sourceNodes = new HashSet<>();
        this.sinkNodes = new HashSet<>();
        this.commutativityPatterns = new ArrayList<>();
    }
    
    /**
     * Builder for DAG Categorical Structure
     */
    public static class Builder<N> {
        private final DAGCategoricalStructure<N> dag;
        
        public Builder(String name) {
            this.dag = new DAGCategoricalStructure<>(name);
        }
        
        public Builder<N> addNode(String nodeId, N data) {
            dag.addDAGNode(nodeId, data);
            return this;
        }
        
        public Builder<N> addNodes(Map<String, N> nodes) {
            nodes.forEach(dag::addDAGNode);
            return this;
        }
        
        public Builder<N> addDependency(String sourceNodeId, String targetNodeId, 
                                     Function<N, N> transformation, String label) {
            dag.addDAGMorphism(sourceNodeId, targetNodeId, transformation, label);
            return this;
        }
        
        public Builder<N> addCommutativityPattern(String patternId, String sourceNodeId, String targetNodeId,
                                                List<String> pathIds, String description) {
            Map<String, List<String>> paths = new HashMap<>();
            paths.put(sourceNodeId + "_" + targetNodeId, pathIds);
            
            CommutativityPattern pattern = new CommutativityPattern(
                patternId, Arrays.asList(sourceNodeId), Arrays.asList(targetNodeId), 
                paths, description
            );
            dag.commutativityPatterns.add(pattern);
            return this;
        }
        
        public Builder<N> addSourceNode(String nodeId, N data) {
            dag.addDAGNode(nodeId, data);
            dag.sourceNodes.add(nodeId);
            return this;
        }
        
        public Builder<N> addSinkNode(String nodeId, N data) {
            dag.addDAGNode(nodeId, data);
            dag.sinkNodes.add(nodeId);
            return this;
        }
        
        public DAGCategoricalStructure<N> build() {
            dag.validateDAG();
            dag.computeTopologicalOrder();
            dag.computeAncestorsAndDescendants();
            return dag;
        }
    }
    
    private void addDAGNode(String nodeId, N data) {
        DAGNode<N> node = new DAGNode<>(nodeId, data, -1, -1);
        nodes.put(nodeId, node);
        dagNodes.put(nodeId, node);
        adjacencyList.putIfAbsent(nodeId, new ArrayList<>());
    }
    
    private void addDAGMorphism(String sourceNodeId, String targetNodeId, 
                              Function<N, N> transformation, String label) {
        // Check for cycles before adding
        if (wouldCreateCycle(sourceNodeId, targetNodeId)) {
            throw new IllegalArgumentException("Adding edge " + sourceNodeId + " → " + targetNodeId + 
                " would create a cycle");
        }
        
        String morphismId = sourceNodeId + "_to_" + targetNodeId;
        addMorphism(morphismId, sourceNodeId, targetNodeId, transformation, label);
        
        // Update DAG-specific structures
        adjacencyList.get(sourceNodeId).add(targetNodeId);
        dagNodes.get(sourceNodeId).directSuccessors.add(targetNodeId);
        dagNodes.get(targetNodeId).directPredecessors.add(sourceNodeId);
        
        // Update source/sink status
        sourceNodes.remove(targetNodeId);
        sinkNodes.remove(sourceNodeId);
        if (dagNodes.get(sourceNodeId).directPredecessors.isEmpty()) {
            sourceNodes.add(sourceNodeId);
        }
        if (dagNodes.get(targetNodeId).directSuccessors.isEmpty()) {
            sinkNodes.add(targetNodeId);
        }
    }
    
    private boolean wouldCreateCycle(String sourceNodeId, String targetNodeId) {
        // Check if there's already a path from target to source
        return hasPath(targetNodeId, sourceNodeId);
    }
    
    private void validateDAG() {
        // Check for cycles
        for (String nodeId : nodes.keySet()) {
            if (hasPath(nodeId, nodeId)) {
                throw new IllegalStateException("Cycle detected at node: " + nodeId);
            }
        }
        
        // Check that all morphisms respect topological order
        for (Morphism<N> morphism : morphisms.values()) {
            Integer sourceOrder = topologicalOrder.get(morphism.sourceNodeId);
            Integer targetOrder = topologicalOrder.get(morphism.targetNodeId);
            
            if (sourceOrder != null && targetOrder != null && sourceOrder >= targetOrder) {
                throw new IllegalStateException("Morphism violates topological order: " + morphism);
            }
        }
    }
    
    private void computeTopologicalOrder() {
        List<String> sortedNodes = topologicalSort();
        for (int i = 0; i < sortedNodes.size(); i++) {
            topologicalOrder.put(sortedNodes.get(i), i);
            dagNodes.get(sortedNodes.get(i)).topologicalIndex = i;
        }
        
        // Compute depths
        computeDepths();
    }
    
    private List<String> topologicalSort() {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        
        for (String nodeId : nodes.keySet()) {
            if (!visited.contains(nodeId)) {
                topologicalSortDFS(nodeId, visited, visiting, result);
            }
        }
        
        Collections.reverse(result);
        return result;
    }
    
    private void topologicalSortDFS(String nodeId, Set<String> visited, Set<String> visiting, List<String> result) {
        if (visiting.contains(nodeId)) {
            throw new IllegalStateException("Cycle detected during topological sort");
        }
        
        if (visited.contains(nodeId)) {
            return;
        }
        
        visiting.add(nodeId);
        
        for (String successor : adjacencyList.getOrDefault(nodeId, new ArrayList<>())) {
            if (!visited.contains(successor)) {
                topologicalSortDFS(successor, visited, visiting, result);
            }
        }
        
        visiting.remove(nodeId);
        visited.add(nodeId);
        result.add(nodeId);
    }
    
    private void computeDepths() {
        // Compute depth as longest path from any source
        for (String nodeId : nodes.keySet()) {
            int maxDepth = 0;
            for (String predecessor : dagNodes.get(nodeId).directPredecessors) {
                maxDepth = Math.max(maxDepth, dagNodes.get(predecessor).depth + 1);
            }
            dagNodes.get(nodeId).depth = maxDepth;
        }
    }
    
    private void computeAncestorsAndDescendants() {
        for (String nodeId : nodes.keySet()) {
            computeAncestors(nodeId);
            computeDescendants(nodeId);
        }
    }
    
    private void computeAncestors(String nodeId) {
        Set<String> ancestors = new HashSet<>();
        for (String predecessor : dagNodes.get(nodeId).directPredecessors) {
            ancestors.add(predecessor);
            ancestors.addAll(dagNodes.get(predecessor).ancestors);
        }
        dagNodes.get(nodeId).ancestors = ancestors;
    }
    
    private void computeDescendants(String nodeId) {
        Set<String> descendants = new HashSet<>();
        for (String successor : dagNodes.get(nodeId).directSuccessors) {
            descendants.add(successor);
            descendants.addAll(dagNodes.get(successor).descendants);
        }
        dagNodes.get(nodeId).descendants = descendants;
    }
    
    @Override
    public List<Path<N>> findAllPaths(String sourceNodeId, String targetNodeId) {
        List<Path<N>> allPaths = new ArrayList<>();
        findAllPathsDFS(sourceNodeId, targetNodeId, new ArrayList<>(), allPaths, new HashSet<>());
        return allPaths;
    }
    
    private void findAllPathsDFS(String current, String target, List<String> currentPath,
                               List<Path<N>> allPaths, Set<String> visited) {
        if (current.equals(target)) {
            allPaths.add(createDAGPath(currentPath));
            return;
        }
        
        if (visited.contains(current)) {
            return;
        }
        
        visited.add(current);
        
        for (String successor : adjacencyList.getOrDefault(current, new ArrayList<>())) {
            currentPath.add(morphisms.get(successor + "_to_" + current) != null ? 
                successor + "_to_" + current : 
                successor + "_to_" + current); // Simplified
            findAllPathsDFS(successor, target, currentPath, allPaths, visited);
            currentPath.remove(currentPath.size() - 1);
        }
        
        visited.remove(current);
    }
    
    private DAGPath<N> createDAGPath(List<String> morphismIds) {
        if (morphismIds.isEmpty()) {
            return new DAGPath<>(morphismIds, new ArrayList<>(), Function.identity());
        }
        
        List<String> nodeIds = new ArrayList<>();
        nodeIds.add(morphisms.get(morphismIds.get(0)).sourceNodeId);
        for (String morphismId : morphismIds) {
            nodeIds.add(morphisms.get(morphismId).targetNodeId);
        }
        
        return new DAGPath<>(morphismIds, nodeIds, composePath(morphismIds));
    }
    
    @Override
    public boolean isCommutative(String sourceNodeId, String targetNodeId) {
        List<Path<N>> paths = findAllPaths(sourceNodeId, targetNodeId);
        if (paths.size() <= 1) {
            return true;
        }
        
        // Check commutativity patterns
        for (CommutativityPattern pattern : commutativityPatterns) {
            if (pattern.sourceNodeIds.contains(sourceNodeId) && pattern.targetNodeIds.contains(targetNodeId)) {
                if (pattern.hasMultiplePaths(sourceNodeId, targetNodeId)) {
                    // Check if paths are equivalent
                    List<String> pathsToCheck = pattern.getPaths(sourceNodeId, targetNodeId);
                    if (pathsToCheck.size() > 1) {
                        Set<Function<N, N>> transformations = pathsToCheck.stream()
                            .map(this::composePathFromIds)
                            .collect(Collectors.toSet());
                        if (transformations.size() > 1) {
                            return false; // Not commutative
                        }
                    }
                }
            }
        }
        
        // Standard commutativity check
        return super.isCommutative(sourceNodeId, targetNodeId);
    }
    
    private Function<N, N> composePathFromIds(String pathIds) {
        // Simplified - would parse path IDs and compose
        return Function.identity();
    }
    
    @Override
    public int getDimension() {
        // Dimension based on maximum depth
        return dagNodes.values().stream()
            .mapToInt(node -> node.depth)
            .max()
            .orElse(0);
    }
    
    @Override
    public String getStructureType() {
        return "DAGCategoricalStructure";
    }
    
    // DAG-specific operations
    public List<String> getTopologicalOrder() {
        return topologicalOrder.keySet().stream()
            .sorted(Comparator.comparingInt(topologicalOrder::get))
            .collect(Collectors.toList());
    }
    
    public Set<String> getSourceNodes() {
        return new HashSet<>(sourceNodes);
    }
    
    public Set<String> getSinkNodes() {
        return new HashSet<>(sinkNodes);
    }
    
    public int getDepth(String nodeId) {
        return dagNodes.get(nodeId).depth;
    }
    
    public boolean hasPath(String sourceNodeId, String targetNodeId) {
        return dagNodes.get(sourceNodeId).hasPathTo(targetNodeId);
    }
    
    public boolean isAncestor(String ancestorId, String descendantId) {
        return dagNodes.get(ancestorId).isAncestorOf(descendantId);
    }
    
    public boolean isDescendant(String descendantId, String ancestorId) {
        return dagNodes.get(descendantId).isDescendantOf(ancestorId);
    }
    
    public List<DAGPath<N>> getAllSourceToSinkPaths() {
        List<DAGPath<N>> sourceToSinkPaths = new ArrayList<>();
        for (String source : sourceNodes) {
            for (String sink : sinkNodes) {
                sourceToSinkPaths.addAll(findAllPaths(source, sink).stream()
                    .map(path -> (DAGPath<N>) path)
                    .collect(Collectors.toList()));
            }
        }
        return sourceToSinkPaths;
    }
    
    public DAGCategoricalStructure<N> getSubDAG(String subDAGName, Set<String> nodeIds) {
        DAGCategoricalStructure<N> subDAG = new DAGCategoricalStructure<>(subDAGName);
        
        // Add nodes
        for (String nodeId : nodeIds) {
            if (nodes.containsKey(nodeId)) {
                subDAG.addDAGNode(nodeId, nodes.get(nodeId).data);
            }
        }
        
        // Add morphisms that stay within the subset
        for (Morphism<N> morphism : morphisms.values()) {
            if (nodeIds.contains(morphism.sourceNodeId) && nodeIds.contains(morphism.targetNodeId)) {
                subDAG.addDAGMorphism(morphism.sourceNodeId, morphism.targetNodeId,
                    morphism.transformation, morphism.label);
            }
        }
        
        return subDAG;
    }
    
    public List<CommutativityPattern> getCommutativityPatterns() {
        return new ArrayList<>(commutativityPatterns);
    }
    
    public boolean addCommutativityConstraint(String patternId, String sourceNodeId, String targetNodeId) {
        // Find multiple paths and create a commutativity pattern
        List<Path<N>> paths = findAllPaths(sourceNodeId, targetNodeId);
        if (paths.size() < 2) {
            return false; // No need for commutativity constraint
        }
        
        Map<String, List<String>> pathMap = new HashMap<>();
        pathMap.put(sourceNodeId + "_" + targetNodeId, paths.stream()
            .map(path -> String.join("_", path.morphismIds))
            .collect(Collectors.toList()));
        
        CommutativityPattern pattern = new CommutativityPattern(
            patternId, Arrays.asList(sourceNodeId), Arrays.asList(targetNodeId),
            pathMap, "Multiple paths between " + sourceNodeId + " and " + targetNodeId
        );
        
        commutativityPatterns.add(pattern);
        return true;
    }
}