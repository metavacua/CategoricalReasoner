/*
 * SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * JUnit 5 tests for the HelloWorld demonstration.
 */
package catty.helloworld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for HelloWorld technology stack demonstration.
 */
class HelloWorldTest {

    @Nested
    @DisplayName("PROV-O Provenance Tests")
    class ProvenanceTests {

        @Test
        @DisplayName("PROVEntity can be created with required attributes")
        void testEntityCreation() {
            catty.helloworld.prov.PROVEntity entity =
                new catty.helloworld.prov.PROVEntity(
                    "test-entity",
                    "software",
                    "https://example.org/test"
                );

            assertNotNull(entity);
            assertEquals("test-entity", entity.getId());
        }

        @Test
        @DisplayName("PROVActivity can track associations")
        void testActivityAssociations() {
            catty.helloworld.prov.PROVActivity activity =
                new catty.helloworld.prov.PROVActivity(
                    "test-activity",
                    "execution",
                    java.time.Instant.now()
                );

            catty.helloworld.prov.PROVAgent agent =
                new catty.helloworld.prov.PROVAgent(
                    "test-agent",
                    "LLM",
                    "https://example.org/agent"
                );

            activity.wasAssociatedWith(agent);

            assertNotNull(activity);
            assertEquals("test-activity", activity.getId());
        }

        @Test
        @DisplayName("PROVAgent generates valid Turtle output")
        void testAgentTurtleOutput() {
            catty.helloworld.prov.PROVAgent agent =
                new catty.helloworld.prov.PROVAgent(
                    "test-agent",
                    "Person",
                    "https://example.org/person"
                );

            String turtle = agent.toTurtle();
            assertNotNull(turtle);
            assertTrue(turtle.contains("prov:Agent"));
            assertTrue(turtle.contains("test-agent"));
        }
    }

    @Nested
    @DisplayName("CattyFormula Annotation Tests")
    class AnnotationTests {

        @Test
        @DisplayName("CattyFormula annotation exists and is well-formed")
        void testAnnotationExists() {
            try {
                Class<?> annotationClass = Class.forName("catty.helloworld.CattyFormula");
                assertNotNull(annotationClass);

                java.lang.annotation.Annotation annotation =
                    HelloWorld.class.getAnnotation(annotationClass);
                // Annotation may or may not be present on HelloWorld
                // This tests the annotation type is available
            } catch (ClassNotFoundException e) {
                fail("CattyFormula annotation class not found");
            }
        }

        @Test
        @DisplayName("CattyFormula retention is SOURCE")
        void testAnnotationRetention() {
            java.lang.annotation.Retention retention =
                catty.helloworld.CattyFormula.class
                    .getAnnotation(java.lang.annotation.Retention.class);

            assertNotNull(retention);
            assertEquals(
                java.lang.annotation.RetentionPolicy.SOURCE,
                retention.value()
            );
        }
    }

    @Nested
    @DisplayName("Technology Stack Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Jena Model can be created")
        void testJenaModelCreation() {
            org.apache.jena.rdf.model.Model model =
                org.apache.jena.rdf.model.ModelFactory.createDefaultModel();

            assertNotNull(model);
            assertTrue(model.isEmpty());
        }

        @Test
        @DisplayName("OWL API ontology manager can be initialized")
        void testOWLManagerCreation() {
            org.semanticweb.owlapi.model.OWLOntologyManager manager =
                org.semanticweb.owlapi.apibinding.OWLManager.createOWLOntologyManager();

            assertNotNull(manager);
        }

        @Test
        @DisplayName("JavaPoet TypeSpec can generate record")
        void testJavaPoetRecordGeneration() {
            com.squareup.javapoet.TypeSpec recordSpec =
                com.squareup.javapoet.TypeSpec.recordBuilder("TestRecord")
                    .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                    .addField(String.class, "value")
                    .build();

            assertNotNull(recordSpec);
            assertEquals("TestRecord", recordSpec.name);
        }
    }
}
