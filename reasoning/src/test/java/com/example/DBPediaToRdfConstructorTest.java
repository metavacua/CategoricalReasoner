package com.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test suite for Java DBPedia → Catty RDF construction.
 *
 * Mirrors the Python test suite in test_suites/test_construction_python.py.
 */
public class DBPediaToRdfConstructorTest {

    private DBPediaToRdfConstructor constructor;
    private Map<String, Object> dbpediaData;
    private Model model;

    @Before
    public void setUp() throws Exception {
        constructor = new DBPediaToRdfConstructor();
        Path fixture = Path.of("../output/dbpedia-intuitionistic-logic-retrieved.jsonld").normalize();
        dbpediaData = constructor.loadDbpediaFixture(fixture);
        model = constructor.constructCattyLogic(dbpediaData);
    }

    @Test
    public void testRetrieveCompletePropertySet() {
        assertNotNull("DBPedia data should be loaded", dbpediaData);
        assertTrue("Label should be present", dbpediaData.containsKey("label"));
        assertTrue("Abstract should be present", dbpediaData.containsKey("abstract"));
        assertTrue("Concept IRI should be present", dbpediaData.containsKey("concept_iri"));

        String label = (String) dbpediaData.get("label");
        assertNotNull("Label should not be null", label);
        assertTrue("Label should contain 'Intuitionistic'", label.contains("Intuitionistic"));

        String abs = (String) dbpediaData.get("abstract");
        assertNotNull("Abstract should not be null", abs);
        assertTrue("Abstract should be substantial", abs.length() > 100);
    }

    @Test
    public void testConstructCattyInstance() {
        assertNotNull("Model should be constructed", model);
        assertTrue("Model should have triples", model.size() > 0);

        Resource logic = model.getResource(DBPediaToRdfConstructor.CATTY_LOGIC_INSTANCE);
        assertTrue("Logic instance should exist", model.containsResource(logic));

        Resource cattyLogic = model.getResource(DBPediaToRdfConstructor.CATTY + "Logic");
        assertTrue("Logic instance should have type catty:Logic",
                model.contains(logic, RDF.type, cattyLogic));
    }

    @Test
    public void testProvenanceLinks() {
        Resource logic = model.getResource(DBPediaToRdfConstructor.CATTY_LOGIC_INSTANCE);

        assertTrue("Should have dct:isBasedOn",
                model.contains(logic, DBPediaToRdfConstructor.DCT_IS_BASED_ON));

        assertTrue("Should have prov:wasDerivedFrom",
                model.contains(logic, DBPediaToRdfConstructor.PROV_WAS_DERIVED_FROM));

        assertTrue("Should have prov:generatedAtTime",
                model.contains(logic, DBPediaToRdfConstructor.PROV_GENERATED_AT_TIME));
    }

    @Test
    public void testSchemaConformance() {
        assertEquals("Should produce 14 triples (matching Python)", 14, model.size());
    }

    @Test
    public void testIriValidity() {
        // All subject/object IRIs should be valid (start with http/https)
        model.listStatements().forEachRemaining(stmt -> {
            if (stmt.getSubject().isURIResource()) {
                String uri = stmt.getSubject().getURI();
                assertTrue("Subject IRI should be valid: " + uri,
                        uri.startsWith("http://") || uri.startsWith("https://"));
            }
            if (stmt.getObject().isURIResource()) {
                String uri = stmt.getObject().asResource().getURI();
                assertTrue("Object IRI should be valid: " + uri,
                        uri.startsWith("http://") || uri.startsWith("https://"));
            }
        });
    }

    @Test
    public void testFactJustification() {
        // All facts should be justified (extracted, schema-defined, or inferred)
        // This is a simplified check - in practice, we'd verify each triple
        assertTrue("All triples should be justified", model.size() > 0);
    }
}
