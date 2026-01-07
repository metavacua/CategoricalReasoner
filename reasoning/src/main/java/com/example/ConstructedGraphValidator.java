package com.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.InfModel;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.ValidationReport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Validates constructed graph with reasoning + SHACL.
 *
 * Loads:
 * - Constructed Catty instance (JSON-LD)
 * - Catty schema (JSON-LD)
 * - Thesis SHACL shapes (Turtle)
 *
 * Runs:
 * - OWL mini reasoner (RDFS/OWL-lite)
 * - SHACL validation (Jena SHACL)
 *
 * Writes a Markdown report to output/semantic-validation-report.md.
 */
public final class ConstructedGraphValidator {

    public static class Result {
        public final long baseTriples;
        public final long inferredTriples;
        public final boolean shaclConforms;
        public final String shaclText;

        public Result(long baseTriples, long inferredTriples, boolean shaclConforms, String shaclText) {
            this.baseTriples = baseTriples;
            this.inferredTriples = inferredTriples;
            this.shaclConforms = shaclConforms;
            this.shaclText = shaclText;
        }
    }

    public Result validate(Path constructedJsonLd, Path schemaJsonLd, Path shapesTtl) {
        Model base = ModelFactory.createDefaultModel();
        RDFDataMgr.read(base, constructedJsonLd.toString());

        Model schema = ModelFactory.createDefaultModel();
        RDFDataMgr.read(schema, schemaJsonLd.toString());

        Model shapes = ModelFactory.createDefaultModel();
        RDFDataMgr.read(shapes, shapesTtl.toString());

        // Combine schema + instance
        Model union = ModelFactory.createUnion(schema, base);

        // Reasoning
        Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, union);

        long baseCount = union.size();
        long infCount = inf.size();

        // SHACL validation against inferred model
        ValidationReport report = ShaclValidator.get().validate(shapes.getGraph(), inf.getGraph());

        return new Result(baseCount, infCount, report.conforms(), report.toString());
    }

    public void writeReport(Path reportPath, Result result) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# Semantic Validation Report\n\n");
        sb.append("## Summary\n\n");
        sb.append("- Base triple count (schema + constructed): ").append(result.baseTriples).append("\n");
        sb.append("- Inferred triple count (OWL mini reasoner): ").append(result.inferredTriples).append("\n");
        sb.append("- SHACL conforms: ").append(result.shaclConforms ? "PASS" : "FAIL").append("\n\n");

        sb.append("## SHACL Report\n\n");
        sb.append("```\n");
        sb.append(result.shaclText);
        sb.append("\n```\n");

        Files.createDirectories(reportPath.getParent());
        Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        Path projectRoot = Path.of("..").normalize();
        Path constructed = projectRoot.resolve("output/constructed-intuitionistic-logic-java.jsonld");
        Path schema = projectRoot.resolve("ontology/catty-categorical-schema.jsonld");
        Path shapes = projectRoot.resolve("ontology/catty-thesis-shapes.shacl");
        Path out = projectRoot.resolve("output/semantic-validation-report.md");

        ConstructedGraphValidator validator = new ConstructedGraphValidator();
        Result result = validator.validate(constructed, schema, shapes);
        validator.writeReport(out, result);

        System.out.println("Wrote: " + out.toAbsolutePath());
        System.out.println("SHACL conforms: " + result.shaclConforms);
    }
}
