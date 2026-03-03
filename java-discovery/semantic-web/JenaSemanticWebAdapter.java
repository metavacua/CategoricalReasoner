package org.catty.semanticweb;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.update.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core Jena Integration Layer
 * 
 * Provides bidirectional mapping between Java categorical objects and RDF/OWL ontologies.
 * Enables loading from semantic web datasets and exporting to semantic web formats.
 */
public class JenaSemanticWebAdapter {
    
    private final Model model;
    private final OntModel ontModel;
    private final String namespace;
    private final Map<String, Resource> resourceCache;
    
    public JenaSemanticWebAdapter(String namespace) {
        this.namespace = namespace.endsWith("#") || namespace.endsWith("/") ? namespace : namespace + "#";
        this.model = ModelFactory.createDefaultModel();
        this.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        this.resourceCache = new HashMap<>();
        
        // Set up namespaces
        model.setNsPrefix("catty", namespace);
        model.setNsPrefix("owl", OWL.NS);
        model.setNsPrefix("rdf", RDF.NS);
        model.setNsPrefix("rdfs", RDFS.NS);
        model.setNsPrefix("xsd", XSD.NS);
        
        // Create base ontology
        createBaseOntology();
    }
    
    /**
     * Create base categorical ontology
     */
    private void createBaseOntology() {
        // Create ontology metadata
        Resource ontology = ontModel.createResource(namespace + "CattyCategoricalOntology")
            .addProperty(RDF.type, OWL.Ontology)
            .addProperty(RDFS.comment, "Catty categorical reasoner ontology")
            .addProperty(RDFS.label, "Catty Categorical Ontology")
            .addProperty(DCTerms.creator, "Catty Research Team")
            .addProperty(DCTerms.created, "2025-01-15")
            .addProperty(DCTerms.description, "Ontology for categorical reasoning and logic analysis");
        
        // Create core classes
        Resource categoryClass = ontModel.createClass(namespace + "Category")
            .addProperty(RDFS.comment, "A category consisting of objects and morphisms")
            .addProperty(RDFS.label, "Category");
        
        Resource objectClass = ontModel.createClass(namespace + "Object")
            .addProperty(RDFS.comment, "An object in a category")
            .addProperty(RDFS.label, "Object");
        
        Resource morphismClass = ontModel.createClass(namespace + "Morphism")
            .addProperty(RDFS.comment, "A morphism (arrow) in a category")
            .addProperty(RDFS.label, "Morphism");
        
        Resource logicClass = ontModel.createClass(namespace + "Logic")
            .addProperty(RDFS.comment, "A logical system")
            .addProperty(RDFS.label, "Logic")
            .addProperty(RDFS.subClassOf, objectClass);
        
        // Create object properties
        OntProperty domainProp = ontModel.createObjectProperty(namespace + "domain")
            .addProperty(RDFS.comment, "The domain of a morphism")
            .addProperty(RDFS.label, "domain")
            .addProperty(RDFS.domain, morphismClass)
            .addProperty(RDFS.range, objectClass);
        
        OntProperty codomainProp = ontModel.createObjectProperty(namespace + "codomain")
            .addProperty(RDFS.comment, "The codomain of a morphism")
            .addProperty(RDFS.label, "codomain")
            .addProperty(RDFS.domain, morphismClass)
            .addProperty(RDFS.range, objectClass);
        
        OntProperty hasSignatureProp = ontModel.createObjectProperty(namespace + "hasLogicalSignature")
            .addProperty(RDFS.comment, "Logical signature of a logic")
            .addProperty(RDFS.label, "has logical signature")
            .addProperty(RDFS.domain, logicClass);
        
        OntProperty hasConnectivesProp = ontModel.createObjectProperty(namespace + "hasConnectives")
            .addProperty(RDFS.comment, "Connectives available in a logic")
            .addProperty(RDFS.label, "has connectives")
            .addProperty(RDFS.domain, logicClass)
            .addProperty(RDFS.range, XSD.string);
        
        // Create data properties
        DatatypeProperty nameProp = ontModel.createDatatypeProperty(namespace + "name")
            .addProperty(RDFS.comment, "Name of an entity")
            .addProperty(RDFS.label, "name")
            .addProperty(RDFS.domain, ontModel.createUnionOf(Arrays.asList(categoryClass, objectClass, morphismClass)))
            .addProperty(RDFS.range, XSD.string);
        
        DatatypeProperty descriptionProp = ontModel.createDatatypeProperty(namespace + "description")
            .addProperty(RDFS.comment, "Description of an entity")
            .addProperty(RDFS.label, "description")
            .addProperty(RDFS.range, XSD.string);
        
        DatatypeProperty typeProp = ontModel.createDatatypeProperty(namespace + "type")
            .addProperty(RDFS.comment, "Type classification")
            .addProperty(RDFS.label, "type")
            .addProperty(RDFS.range, XSD.string);
    }
    
    /**
     * Export Java categorical object to RDF
     */
    public void exportCategoricalObject(Object javaObject) {
        if (javaObject instanceof org.catty.categorical.StaticFiniteCategory) {
            exportStaticFiniteCategory((org.catty.categorical.StaticFiniteCategory<?>) javaObject);
        } else if (javaObject instanceof org.catty.categorical.CommutativeNPolytope) {
            exportCommutativeNPolytope((org.catty.categorical.CommutativeNPolytope<?>) javaObject);
        } else if (javaObject instanceof org.catty.categorical.DAGCategoricalStructure) {
            exportDAGCategoricalStructure((org.catty.categorical.DAGCategoricalStructure<?>) javaObject);
        } else {
            throw new IllegalArgumentException("Unsupported object type: " + javaObject.getClass());
        }
    }
    
    /**
     * Export StaticFiniteCategory to RDF
     */
    private void exportStaticFiniteCategory(org.catty.categorical.StaticFiniteCategory<?> category) {
        // Create category resource
        Resource categoryResource = ontModel.createResource(namespace + "Category_" + category.name)
            .addProperty(RDF.type, ontModel.getResource(namespace + "Category"))
            .addProperty(RDF.type, OWL.NamedIndividual)
            .addProperty(RDF.type, ontModel.getResource(namespace + "StaticFiniteCategory"))
            .addProperty(ontModel.getDatatypeProperty(namespace + "name"), category.name)
            .addProperty(ontModel.getDatatypeProperty(namespace + "description"), 
                "Static finite category with " + category.getObjects().size() + " objects")
            .addProperty(ontModel.getDatatypeProperty(namespace + "dimension"), 
                String.valueOf(category.getDimension()));
        
        // Add objects
        for (Object obj : category.getObjects()) {
            Resource objectResource = ontModel.createResource(namespace + "Object_" + obj.toString())
                .addProperty(RDF.type, ontModel.getResource(namespace + "Object"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "category"), categoryResource)
                .addProperty(ontModel.getDatatypeProperty(namespace + "name"), obj.toString())
                .addProperty(ontModel.getDatatypeProperty(namespace + "description"), obj.toString());
            
            categoryResource.addProperty(ontModel.getObjectProperty(namespace + "hasObject"), objectResource);
        }
        
        // Add morphisms
        for (org.catty.categorical.StaticFiniteCategory.CategoryMorphism<?> morphism : category.getAllMorphisms()) {
            Resource morphismResource = ontModel.createResource(namespace + "Morphism_" + morphism.id)
                .addProperty(RDF.type, ontModel.getResource(namespace + "Morphism"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "category"), categoryResource)
                .addProperty(ontModel.getObjectProperty(namespace + "domain"), 
                    ontModel.getResource(namespace + "Object_" + morphism.domainObject))
                .addProperty(ontModel.getObjectProperty(namespace + "codomain"), 
                    ontModel.getResource(namespace + "Object_" + morphism.codomainObject))
                .addProperty(ontModel.getDatatypeProperty(namespace + "name"), morphism.name)
                .addProperty(ontModel.getDatatypeProperty(namespace + "type"), morphism.type)
                .addProperty(ontModel.getDatatypeProperty(namespace + "description"), morphism.description);
            
            categoryResource.addProperty(ontModel.getObjectProperty(namespace + "hasMorphism"), morphismResource);
        }
        
        // Add initial and terminal objects if they exist
        if (category.getInitialObject() != null) {
            Resource initialResource = ontModel.getResource(namespace + "Object_" + category.getInitialObject());
            categoryResource.addProperty(ontModel.getObjectProperty(namespace + "hasInitialObject"), initialResource);
        }
        
        if (category.getTerminalObject() != null) {
            Resource terminalResource = ontModel.getResource(namespace + "Object_" + category.getTerminalObject());
            categoryResource.addProperty(ontModel.getObjectProperty(namespace + "hasTerminalObject"), terminalResource);
        }
    }
    
    /**
     * Export CommutativeNPolytope to RDF
     */
    private void exportCommutativeNPolytope(org.catty.categorical.CommutativeNPolytope<?> polytope) {
        // Create polytope resource
        Resource polytopeResource = ontModel.createResource(namespace + "Polytope_" + polytope.name)
            .addProperty(RDF.type, ontModel.getResource(namespace + "CommutativeNPolytope"))
            .addProperty(RDF.type, OWL.NamedIndividual)
            .addProperty(ontModel.getDatatypeProperty(namespace + "name"), polytope.name)
            .addProperty(ontModel.getDatatypeProperty(namespace + "description"), 
                "Commutative n-polytope with dimension " + polytope.getDimension())
            .addProperty(ontModel.getDatatypeProperty(namespace + "dimension"), 
                String.valueOf(polytope.getDimension()))
            .addProperty(ontModel.getDatatypeProperty(namespace + "polytopeType"), 
                polytope.getPolytopeType().name());
        
        // Add nodes
        for (org.catty.categorical.AbstractCategoricalStructure.Node<?> node : polytope.getAllNodes()) {
            Resource nodeResource = ontModel.createResource(namespace + "Node_" + node.id)
                .addProperty(RDF.type, ontModel.getResource(namespace + "PolytopeNode"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "polytope"), polytopeResource)
                .addProperty(ontModel.getDatatypeProperty(namespace + "nodeId"), node.id)
                .addProperty(ontModel.getDatatypeProperty(namespace + "data"), node.data.toString());
            
            polytopeResource.addProperty(ontModel.getObjectProperty(namespace + "hasNode"), nodeResource);
        }
        
        // Add faces
        for (org.catty.categorical.CommutativeNPolytope.Face face : polytope.getFaces()) {
            Resource faceResource = ontModel.createResource(namespace + "Face_" + face.id)
                .addProperty(RDF.type, ontModel.getResource(namespace + "PolytopeFace"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "polytope"), polytopeResource)
                .addProperty(ontModel.getDatatypeProperty(namespace + "faceId"), face.id)
                .addProperty(ontModel.getDatatypeProperty(namespace + "dimension"), 
                    String.valueOf(face.dimension))
                .addProperty(ontModel.getDatatypeProperty(namespace + "faceType"), face.type.name());
            
            // Add nodes to face
            for (String nodeId : face.nodeIds) {
                Resource nodeResource = ontModel.getResource(namespace + "Node_" + nodeId);
                faceResource.addProperty(ontModel.getObjectProperty(namespace + "hasNode"), nodeResource);
            }
            
            polytopeResource.addProperty(ontModel.getObjectProperty(namespace + "hasFace"), faceResource);
        }
        
        // Add commutativity constraints
        for (org.catty.categorical.CommutativeNPolytope.CommutativityConstraint constraint : polytope.getCommutativityConstraints()) {
            Resource constraintResource = ontModel.createResource(namespace + "Constraint_" + constraint.id)
                .addProperty(RDF.type, ontModel.getResource(namespace + "CommutativityConstraint"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "polytope"), polytopeResource)
                .addProperty(ontModel.getDatatypeProperty(namespace + "constraintId"), constraint.id)
                .addProperty(ontModel.getDatatypeProperty(namespace + "constraintType"), constraint.type.name())
                .addProperty(ontModel.getDatatypeProperty(namespace + "description"), constraint.description);
            
            polytopeResource.addProperty(ontModel.getObjectProperty(namespace + "hasConstraint"), constraintResource);
        }
    }
    
    /**
     * Export DAGCategoricalStructure to RDF
     */
    private void exportDAGCategoricalStructure(org.catty.categorical.DAGCategoricalStructure<?> dag) {
        // Create DAG resource
        Resource dagResource = ontModel.createResource(namespace + "DAG_" + dag.name)
            .addProperty(RDF.type, ontModel.getResource(namespace + "DAGCategoricalStructure"))
            .addProperty(RDF.type, OWL.NamedIndividual)
            .addProperty(ontModel.getDatatypeProperty(namespace + "name"), dag.name)
            .addProperty(ontModel.getDatatypeProperty(namespace + "description"), 
                "DAG categorical structure with dimension " + dag.getDimension())
            .addProperty(ontModel.getDatatypeProperty(namespace + "dimension"), 
                String.valueOf(dag.getDimension()));
        
        // Add DAG nodes
        for (org.catty.categorical.AbstractCategoricalStructure.Node<?> node : dag.getAllNodes()) {
            Resource nodeResource = ontModel.createResource(namespace + "DAGNode_" + node.id)
                .addProperty(RDF.type, ontModel.getResource(namespace + "DAGNode"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "dag"), dagResource)
                .addProperty(ontModel.getDatatypeProperty(namespace + "nodeId"), node.id)
                .addProperty(ontModel.getDatatypeProperty(namespace + "data"), node.data.toString());
            
            // Add DAG-specific properties
            if (node instanceof org.catty.categorical.DAGCategoricalStructure.DAGNode) {
                org.catty.categorical.DAGCategoricalStructure.DAGNode<?> dagNode = 
                    (org.catty.categorical.DAGCategoricalStructure.DAGNode<?>) node;
                nodeResource.addProperty(ontModel.getDatatypeProperty(namespace + "topologicalIndex"), 
                    String.valueOf(dagNode.topologicalIndex));
                nodeResource.addProperty(ontModel.getDatatypeProperty(namespace + "depth"), 
                    String.valueOf(dagNode.depth));
                
                if (dagNode.isSource()) {
                    nodeResource.addProperty(RDF.type, ontModel.getResource(namespace + "SourceNode"));
                }
                if (dagNode.isSink()) {
                    nodeResource.addProperty(RDF.type, ontModel.getResource(namespace + "SinkNode"));
                }
            }
            
            dagResource.addProperty(ontModel.getObjectProperty(namespace + "hasDAGNode"), nodeResource);
        }
        
        // Add topological order
        List<String> topologicalOrder = dag.getTopologicalOrder();
        for (int i = 0; i < topologicalOrder.size(); i++) {
            dagResource.addProperty(ontModel.getObjectProperty(namespace + "hasTopologicalOrder"), 
                topologicalOrder.get(i) + "@" + i);
        }
        
        // Add commutativity patterns
        for (org.catty.categorical.DAGCategoricalStructure.CommutativityPattern pattern : dag.getCommutativityPatterns()) {
            Resource patternResource = ontModel.createResource(namespace + "Pattern_" + pattern.id)
                .addProperty(RDF.type, ontModel.getResource(namespace + "CommutativityPattern"))
                .addProperty(RDF.type, OWL.NamedIndividual)
                .addProperty(ontModel.getObjectProperty(namespace + "dag"), dagResource)
                .addProperty(ontModel.getDatatypeProperty(namespace + "patternId"), pattern.id)
                .addProperty(ontModel.getDatatypeProperty(namespace + "description"), pattern.description);
            
            dagResource.addProperty(ontModel.getObjectProperty(namespace + "hasPattern"), patternResource);
        }
    }
    
    /**
     * Query categorical objects from RDF
     */
    public <T> T importCategoricalObject(String sparqlQuery, Class<T> expectedType) {
        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            ResultSet results = qexec.execSelect();
            
            if (expectedType.equals(org.catty.categorical.StaticFiniteCategory.class)) {
                return (T) importStaticFiniteCategory(results);
            } else if (expectedType.equals(org.catty.categorical.CommutativeNPolytope.class)) {
                return (T) importCommutativeNPolytope(results);
            } else if (expectedType.equals(org.catty.categorical.DAGCategoricalStructure.class)) {
                return (T) importDAGCategoricalStructure(results);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + expectedType);
            }
        }
    }
    
    /**
     * Import StaticFiniteCategory from SPARQL results
     */
    private org.catty.categorical.StaticFiniteCategory<String> importStaticFiniteCategory(ResultSet results) {
        // This is a simplified example - in practice, you'd need more sophisticated parsing
        org.catty.categorical.StaticFiniteCategory.Builder<String> builder = 
            new org.catty.categorical.StaticFiniteCategory.Builder<String>("ImportedCategory");
        
        // Parse SPARQL results and build category
        while (results.hasNext()) {
            QuerySolution soln = results.next();
            // Implementation would parse the solution and add objects/morphisms
        }
        
        return builder.build();
    }
    
    /**
     * Import CommutativeNPolytope from SPARQL results
     */
    private org.catty.categorical.CommutativeNPolytope<String> importCommutativeNPolytope(ResultSet results) {
        org.catty.categorical.CommutativeNPolytope.Builder<String> builder = 
            new org.catty.categorical.CommutativeNPolytope.Builder<String>("ImportedPolytope", 2, 
                org.catty.categorical.CommutativeNPolytope.PolytopeType.TRIANGLE);
        
        // Parse SPARQL results and build polytope
        while (results.hasNext()) {
            QuerySolution soln = results.next();
            // Implementation would parse the solution and add nodes/faces/constraints
        }
        
        return builder.build();
    }
    
    /**
     * Import DAGCategoricalStructure from SPARQL results
     */
    private org.catty.categorical.DAGCategoricalStructure<String> importDAGCategoricalStructure(ResultSet results) {
        org.catty.categorical.DAGCategoricalStructure.Builder<String> builder = 
            new org.catty.categorical.DAGCategoricalStructure.Builder<String>("ImportedDAG");
        
        // Parse SPARQL results and build DAG
        while (results.hasNext()) {
            QuerySolution soln = results.next();
            // Implementation would parse the solution and add nodes/dependencies
        }
        
        return builder.build();
    }
    
    /**
     * Execute SPARQL query
     */
    public ResultSet executeSPARQLQuery(String sparqlQuery) {
        try (QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model)) {
            return qexec.execSelect();
        }
    }
    
    /**
     * Export model to file
     */
    public void exportToFile(String filePath, String format) {
        if (format.equals("ttl") || format.equals("turtle")) {
            model.write(System.out, "TTL");
        } else if (format.equals("rdf") || format.equals("xml")) {
            model.write(System.out, "RDF/XML");
        } else if (format.equals("jsonld")) {
            model.write(System.out, "JSON-LD");
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
    
    /**
     * Load model from file
     */
    public void loadFromFile(String filePath, String format) {
        if (format.equals("ttl") || format.equals("turtle")) {
            model.read(filePath, "TTL");
        } else if (format.equals("rdf") || format.equals("xml")) {
            model.read(filePath, "RDF/XML");
        } else if (format.equals("jsonld")) {
            model.read(filePath, "JSON-LD");
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
    
    /**
     * Get the underlying model
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Get the ontology model
     */
    public OntModel getOntModel() {
        return ontModel;
    }
}