package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * DBPedia → Catty RDF constructor (Java / Apache Jena).
 *
 * This implementation is designed to be deterministic in CI: it prefers loading
 * the DBPedia retrieval fixture from ../output/dbpedia-intuitionistic-logic-retrieved.jsonld.
 *
 * It constructs the same triples as the Python implementation:
 * - catty:Logic instance
 * - rdfs:label, dct:description
 * - dct:isBasedOn (project-specific DC Terms IRI)
 * - owl:sameAs, dct:references
 * - skos:broader / skos:narrower
 * - prov:wasDerivedFrom, prov:generatedAtTime, dct:source
 */
public final class DBPediaToRdfConstructor {

    public static final String CATTY = "http://catty.org/ontology/";
    public static final String CATTY_LOGIC_INSTANCE = "http://catty.org/logic/intuitionistic-logic";

    public static final String DCT = "http://purl.org/dc/terms/";
    /**
     * Note: dcterms:isBasedOn is not a standard DCTerms predicate.
     * We still use this IRI for provenance per the ticket requirements.
     */
    public static final Property DCT_IS_BASED_ON = ResourceFactory.createProperty(DCT + "isBasedOn");

    public static final String PROV = "http://www.w3.org/ns/prov#";
    public static final Property PROV_WAS_DERIVED_FROM = ResourceFactory.createProperty(PROV + "wasDerivedFrom");
    public static final Property PROV_GENERATED_AT_TIME = ResourceFactory.createProperty(PROV + "generatedAtTime");

    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> loadDbpediaFixture(Path fixturePath) throws IOException {
        try (InputStream in = Files.newInputStream(fixturePath)) {
            return mapper.readValue(in, new TypeReference<Map<String, Object>>() {});
        }
    }

    public Model constructCattyLogic(Map<String, Object> dbpediaData) {
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("catty", CATTY);
        model.setNsPrefix("rdfs", RDFS.uri);
        model.setNsPrefix("owl", OWL.NS);
        model.setNsPrefix("dct", DCT);
        model.setNsPrefix("skos", SKOS.uri);
        model.setNsPrefix("prov", PROV);

        Resource logic = model.createResource(CATTY_LOGIC_INSTANCE);
        Resource cattyLogicClass = model.createResource(CATTY + "Logic");

        logic.addProperty(RDF.type, cattyLogicClass);

        String label = asString(dbpediaData.get("label"));
        if (label != null) {
            logic.addProperty(RDFS.label, model.createLiteral(label, "en"));
        }

        String abs = asString(dbpediaData.get("abstract"));
        if (abs != null) {
            logic.addProperty(DCTerms.description, model.createLiteral(abs, "en"));
        }

        String conceptIri = asString(dbpediaData.get("concept_iri"));
        if (conceptIri != null) {
            logic.addProperty(DCT_IS_BASED_ON, model.createResource(conceptIri));
            logic.addProperty(PROV_WAS_DERIVED_FROM, model.createResource(conceptIri));
        }

        String endpoint = asString(dbpediaData.get("source_endpoint"));
        if (endpoint != null) {
            logic.addProperty(DCTerms.source, model.createTypedLiteral(endpoint, XSDDatatype.XSDanyURI));
        }

        String retrievedAt = asString(dbpediaData.get("retrieved_at"));
        if (retrievedAt != null) {
            // Normalize into xsd:dateTime lexical form if possible
            try {
                OffsetDateTime odt = OffsetDateTime.parse(retrievedAt.replace("Z", "+00:00"));
                logic.addProperty(PROV_GENERATED_AT_TIME, model.createTypedLiteral(odt.toString(), XSDDatatype.XSDdateTime));
            } catch (Exception e) {
                logic.addProperty(PROV_GENERATED_AT_TIME, model.createTypedLiteral(retrievedAt, XSDDatatype.XSDdateTime));
            }
        }

        for (String sameAs : asStringList(dbpediaData.get("sameAs"))) {
            logic.addProperty(OWL.sameAs, model.createResource(sameAs));
        }

        for (String page : asStringList(dbpediaData.get("page"))) {
            logic.addProperty(DCTerms.references, model.createResource(page));
        }

        for (String broader : asStringList(dbpediaData.get("influencedBy"))) {
            logic.addProperty(SKOS.broader, model.createResource(broader));
        }

        for (String narrower : asStringList(dbpediaData.get("influenced"))) {
            logic.addProperty(SKOS.narrower, model.createResource(narrower));
        }

        return model;
    }

    public void writeJsonLd(Model model, Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        try (var out = Files.newOutputStream(outputPath)) {
            RDFDataMgr.write(out, model, RDFFormat.JSONLD_FLAT);
        }
    }

    private static String asString(Object obj) {
        return obj == null ? null : String.valueOf(obj);
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object obj) {
        if (obj == null) return List.of();
        if (obj instanceof List) {
            return ((List<?>) obj).stream().map(String::valueOf).toList();
        }
        return List.of(String.valueOf(obj));
    }

    public static void main(String[] args) throws Exception {
        Path projectRoot = Path.of("..").normalize();
        Path fixture = projectRoot.resolve("output/dbpedia-intuitionistic-logic-retrieved.jsonld");
        Path out = projectRoot.resolve("output/constructed-intuitionistic-logic-java.jsonld");

        DBPediaToRdfConstructor ctor = new DBPediaToRdfConstructor();
        Map<String, Object> dbpedia = ctor.loadDbpediaFixture(fixture);
        Model model = ctor.constructCattyLogic(dbpedia);
        ctor.writeJsonLd(model, out);

        System.out.println("Wrote: " + out.toAbsolutePath());
        System.out.println("Triples: " + model.size());
    }
}
