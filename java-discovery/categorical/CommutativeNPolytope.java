package org.catty.categorical;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Commutative N-Polytope Structure
 * 
 * Generalizes commutative diagrams to n-dimensional polytopes with commutative properties.
 * Supports arbitrary n-dimensional geometric structures while maintaining categorical consistency.
 */
public class CommutativeNPolytope<N> extends AbstractCategoricalStructure<N> {
    
    private final int dimension;
    private final PolytopeType polytopeType;
    private final List<List<String>> faces; // n-dimensional faces
    
    public enum PolytopeType {
        POINT("0-dimensional point"),
        LINE_SEGMENT("1-dimensional line segment"),
        TRIANGLE("2-dimensional triangle"),
        SQUARE("2-dimensional square"),
        TETRAHEDRON("3-dimensional tetrahedron"),
        CUBE("3-dimensional cube"),
        N_SIMPLEX("n-dimensional simplex"),
        N_CUBE("n-dimensional cube"),
        ARBITRARY("arbitrary polytope");
        
        private final String description;
        
        PolytopeType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Face of the polytope
     */
    public static class Face {
        public final String id;
        public final int dimension;
        public final List<String> nodeIds;
        public final FaceType type;
        
        public Face(String id, int dimension, List<String> nodeIds, FaceType type) {
            this.id = id;
            this.dimension = dimension;
            this.nodeIds = new ArrayList<>(nodeIds);
            this.type = type;
        }
        
        public boolean isFaceOf(CommutativeNPolytope<?> polytope) {
            return nodeIds.stream().allMatch(nodeId -> polytope.nodes.containsKey(nodeId));
        }
        
        @Override
        public String toString() {
            return String.format("Face_%s(dimension %d, %d nodes, %s)", 
                id, dimension, nodeIds.size(), type);
        }
    }
    
    public enum FaceType {
        VERTEX, EDGE, FACE, CELL, HYPERFACE, ARBITRARY
    }
    
    /**
     * Commutativity constraint on faces
     */
    public static class CommutativityConstraint {
        public final String id;
        public final List<String> faceIds;
        public final String description;
        public final CommutativityType type;
        
        public CommutativityConstraint(String id, List<String> faceIds, 
                                    String description, CommutativityType type) {
            this.id = id;
            this.faceIds = new ArrayList<>(faceIds);
            this.description = description;
            this.type = type;
        }
        
        public boolean isSatisfied(CommutativeNPolytope<?> polytope) {
            // Check if all faces exist and paths are equivalent
            List<List<Path<N>>> facePaths = new ArrayList<>();
            
            for (String faceId : faceIds) {
                Face face = polytope.getFace(faceId);
                if (face == null) return false;
                
                // Find paths that traverse this face
                List<Path<N>> paths = polytope.findPathsThroughFace(face);
                if (paths.isEmpty()) return false;
                facePaths.add(paths);
            }
            
            // Check if all paths are equivalent (commutative)
            if (facePaths.size() < 2) return true;
            
            Set<Function<N, N>> pathTransformations = new HashSet<>();
            for (List<Path<N>> paths : facePaths) {
                for (Path<N> path : paths) {
                    pathTransformations.add(path.composedTransformation);
                }
            }
            
            return pathTransformations.size() == 1; // All transformations equivalent
        }
        
        @Override
        public String toString() {
            return String.format("Commutativity_%s: %s [%s]", 
                id, faceIds, type);
        }
    }
    
    public enum CommutativityType {
        LOCAL("Local commutativity within face"),
        GLOBAL("Global commutativity across polytope"),
        BOUNDARY("Commutativity on boundary"),
        INTERIOR("Commutativity in interior"),
        MULTIDIMENSIONAL("Commutativity across multiple dimensions");
        
        private final String description;
        
        CommutativityType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final Map<String, Face> facesMap;
    private final List<CommutativityConstraint> constraints;
    
    public CommutativeNPolytope(String name, int dimension, PolytopeType type) {
        super(name);
        this.dimension = dimension;
        this.polytopeType = type;
        this.faces = new ArrayList<>();
        this.facesMap = new HashMap<>();
        this.constraints = new ArrayList<>();
    }
    
    /**
     * Builder for Commutative N-Polytope
     */
    public static class Builder<N> {
        private final CommutativeNPolytope<N> polytope;
        
        public Builder(String name, int dimension, PolytopeType type) {
            this.polytope = new CommutativeNPolytope<>(name, dimension, type);
        }
        
        public Builder<N> addNode(String nodeId, N data) {
            polytope.addNode(nodeId, data);
            return this;
        }
        
        public Builder<N> addMorphism(String morphismId, String sourceNodeId, String targetNodeId,
                                    Function<N, N> transformation, String label) {
            polytope.addMorphism(morphismId, sourceNodeId, targetNodeId, transformation, label);
            return this;
        }
        
        public Builder<N> addFace(String faceId, int faceDimension, List<String> nodeIds, FaceType type) {
            Face face = new Face(faceId, faceDimension, nodeIds, type);
            polytope.facesMap.put(faceId, face);
            polytope.faces.add(nodeIds);
            return this;
        }
        
        public Builder<N> addCommutativityConstraint(String constraintId, List<String> faceIds,
                                                 String description, CommutativityType type) {
            CommutativityConstraint constraint = new CommutativityConstraint(constraintId, faceIds, description, type);
            polytope.constraints.add(constraint);
            return this;
        }
        
        public CommutativeNPolytope<N> build() {
            polytope.validateCommutativity();
            return polytope;
        }
    }
    
    private void validateCommutativity() {
        // Validate all commutativity constraints
        for (CommutativityConstraint constraint : constraints) {
            if (!constraint.isSatisfied(this)) {
                throw new IllegalStateException("Commutativity constraint violated: " + constraint);
            }
        }
        
        // Validate polytope structure
        validatePolytopeStructure();
    }
    
    private void validatePolytopeStructure() {
        // Ensure faces are properly connected
        for (Face face : facesMap.values()) {
            for (String nodeId : face.nodeIds) {
                if (!nodes.containsKey(nodeId)) {
                    throw new IllegalStateException("Face references non-existent node: " + nodeId);
                }
            }
        }
        
        // Check dimensional consistency
        for (Face face : facesMap.values()) {
            if (face.dimension > dimension) {
                throw new IllegalStateException("Face dimension exceeds polytope dimension");
            }
        }
    }
    
    @Override
    public List<Path<N>> findAllPaths(String sourceNodeId, String targetNodeId) {
        List<Path<N>> allPaths = new ArrayList<>();
        
        // Standard path finding
        findPathsInternal(sourceNodeId, targetNodeId, new ArrayList<>(), allPaths, new HashSet<>());
        
        // Also find paths constrained to specific faces
        for (Face face : facesMap.values()) {
            if (face.nodeIds.contains(sourceNodeId) && face.nodeIds.contains(targetNodeId)) {
                List<Path<N>> facePaths = findPathsThroughFace(face);
                allPaths.addAll(facePaths);
            }
        }
        
        return allPaths;
    }
    
    public List<Path<N>> findPathsThroughFace(Face face) {
        List<Path<N>> facePaths = new ArrayList<>();
        
        // Find all paths that traverse nodes in this face
        for (String startNodeId : face.nodeIds) {
            for (String endNodeId : face.nodeIds) {
                if (!startNodeId.equals(endNodeId)) {
                    List<Path<N>> paths = findAllPaths(startNodeId, endNodeId);
                    for (Path<N> path : paths) {
                        if (pathTraversesFace(path, face)) {
                            facePaths.add(path);
                        }
                    }
                }
            }
        }
        
        return facePaths;
    }
    
    private boolean pathTraversesFace(Path<N> path, Face face) {
        // Check if the path passes through nodes in this face
        return path.nodeIds.stream().anyMatch(face.nodeIds::contains);
    }
    
    @Override
    public boolean isCommutative(String sourceNodeId, String targetNodeId) {
        List<Path<N>> paths = findAllPaths(sourceNodeId, targetNodeId);
        if (paths.size() <= 1) {
            return true;
        }
        
        // Check both global and local commutativity
        boolean globalCommutative = checkGlobalCommutativity(paths);
        boolean localCommutative = checkLocalCommutativity(sourceNodeId, targetNodeId);
        
        return globalCommutative && localCommutative;
    }
    
    private boolean checkGlobalCommutativity(List<Path<N>> paths) {
        // Check if all paths compose to equivalent transformations
        Set<Function<N, N>> transformations = paths.stream()
            .map(path -> path.composedTransformation)
            .collect(Collectors.toSet());
        
        return transformations.size() == 1;
    }
    
    private boolean checkLocalCommutativity(String sourceNodeId, String targetNodeId) {
        // Check commutativity within each face
        for (Face face : facesMap.values()) {
            if (face.nodeIds.contains(sourceNodeId) && face.nodeIds.contains(targetNodeId)) {
                if (!checkFaceCommutativity(face, sourceNodeId, targetNodeId)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean checkFaceCommutativity(Face face, String sourceNodeId, String targetNodeId) {
        List<Path<N>> facePaths = findPathsThroughFace(face).stream()
            .filter(path -> path.nodeIds.contains(sourceNodeId) && path.nodeIds.contains(targetNodeId))
            .collect(Collectors.toList());
        
        if (facePaths.size() <= 1) {
            return true;
        }
        
        Set<Function<N, N>> faceTransformations = facePaths.stream()
            .map(path -> path.composedTransformation)
            .collect(Collectors.toSet());
        
        return faceTransformations.size() == 1;
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public String getStructureType() {
        return "CommutativeNPolytope";
    }
    
    // Polytope-specific operations
    public PolytopeType getPolytopeType() {
        return polytopeType;
    }
    
    public List<Face> getFaces() {
        return new ArrayList<>(facesMap.values());
    }
    
    public Face getFace(String faceId) {
        return facesMap.get(faceId);
    }
    
    public List<Face> getFacesOfDimension(int faceDimension) {
        return facesMap.values().stream()
            .filter(face -> face.dimension == faceDimension)
            .collect(Collectors.toList());
    }
    
    public List<CommutativityConstraint> getCommutativityConstraints() {
        return new ArrayList<>(constraints);
    }
    
    public boolean isCommutativityConstraintSatisfied(String constraintId) {
        return constraints.stream()
            .filter(c -> c.id.equals(constraintId))
            .findFirst()
            .map(c -> c.isSatisfied(this))
            .orElse(false);
    }
    
    public List<String> getBoundaryNodes() {
        // Nodes that appear in faces of dimension < polytope dimension
        Set<String> boundaryNodes = new HashSet<>();
        for (Face face : facesMap.values()) {
            if (face.dimension < dimension) {
                boundaryNodes.addAll(face.nodeIds);
            }
        }
        return new ArrayList<>(boundaryNodes);
    }
    
    public List<String> getInteriorNodes() {
        // Nodes that appear only in faces of dimension = polytope dimension
        Set<String> interiorNodes = new HashSet<>();
        for (String nodeId : nodes.keySet()) {
            boolean isInterior = true;
            for (Face face : facesMap.values()) {
                if (face.dimension < dimension && face.nodeIds.contains(nodeId)) {
                    isInterior = false;
                    break;
                }
            }
            if (isInterior) {
                interiorNodes.add(nodeId);
            }
        }
        return new ArrayList<>(interiorNodes);
    }
    
    public CommutativeNPolytope<N> getSubPolytope(String subPolytopeName, List<String> nodeIds) {
        // Create a sub-polytope from a subset of nodes
        CommutativeNPolytope<N> subPolytope = new CommutativeNPolytope<>(subPolytopeName, dimension - 1, PolytopeType.ARBITRARY);
        
        // Add subset of nodes
        for (String nodeId : nodeIds) {
            if (nodes.containsKey(nodeId)) {
                subPolytope.addNode(nodeId, nodes.get(nodeId).data);
            }
        }
        
        // Add morphisms between these nodes
        for (Morphism<N> morphism : morphisms.values()) {
            if (nodeIds.contains(morphism.sourceNodeId) && nodeIds.contains(morphism.targetNodeId)) {
                subPolytope.addMorphism(morphism.id, morphism.sourceNodeId, morphism.targetNodeId,
                    morphism.transformation, morphism.label);
            }
        }
        
        return subPolytope;
    }
    
    public void addFaceCommutativity(String faceId, CommutativityType type) {
        Face face = facesMap.get(faceId);
        if (face == null) {
            throw new IllegalArgumentException("Face not found: " + faceId);
        }
        
        List<String> faceNodeIds = new ArrayList<>(face.nodeIds);
        
        // Add commutativity constraint for this face
        String constraintId = "comm_" + faceId;
        addCommutativityConstraint(constraintId, faceNodeIds, 
            "Commutativity on face " + faceId, type);
    }
}