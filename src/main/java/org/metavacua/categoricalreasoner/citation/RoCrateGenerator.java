package org.metavacua.categoricalreasoner.citation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * RO-Crate 1.1 metadata generator.
 * Converts citation records to RO-Crate JSON-LD format.
 * <p>
 * Generates RO-Crate metadata that conforms to:
 * - RO-Crate 1.1 specification (https://w3id.org/ro/crate/1.1)
 * - OAIS Reference Model (ISO 14721:2012)
 * - schema.org vocabulary
 */
public class RoCrateGenerator {

    private static final String RO_CRATE_CONTEXT = "https://w3id.org/ro/crate/1.1/context";
    private static final String RO_CRATE_METADATA_FILE = "ro-crate-metadata.json";
    private static final String OUTPUT_DIR = "docs/dissertation/bibliography";

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        try {
            generate();
            System.out.println("RO-Crate metadata generated successfully");
        } catch (Exception e) {
            System.err.println("Error generating RO-Crate metadata: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Generates RO-Crate metadata from citation repository.
     *
     * @throws IOException if unable to write output file
     */
    public static void generate() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject roCrate = new JsonObject();

        // Set @context
        roCrate.addProperty("@context", RO_CRATE_CONTEXT);

        // Create @graph array
        JsonArray graph = new JsonArray();

        // Add metadata descriptor
        graph.add(createMetadataDescriptor());

        // Add root dataset
        graph.add(createRootDataset());

        // Add all citations
        List<Citation> citations = CitationRepository.getAll();
        for (Citation citation : citations) {
            graph.add(createCitationEntity(citation));
        }

        // Add author entities
        for (Citation citation : citations) {
            for (Person author : citation.authors()) {
                graph.add(createPersonEntity(author));
            }
        }

        roCrate.add("@graph", graph);

        // Write to file
        Path outputPath = Paths.get(OUTPUT_DIR, RO_CRATE_METADATA_FILE);
        Files.createDirectories(outputPath.getParent());
        Files.writeString(
            outputPath,
            gson.toJson(roCrate),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );

        System.out.println("RO-Crate metadata written to: " + outputPath);
    }

    /**
     * Creates the RO-Crate metadata descriptor.
     */
    private static JsonObject createMetadataDescriptor() {
        JsonObject metadata = new JsonObject();

        metadata.addProperty("@id", RO_CRATE_METADATA_FILE);
        metadata.addProperty("@type", "CreativeWork");
        metadata.addProperty("conformsTo", "https://w3id.org/ro/crate/1.1");

        JsonObject about = new JsonObject();
        about.addProperty("@id", "./");
        metadata.add("about", about);

        return metadata;
    }

    /**
     * Creates the root dataset entity.
     */
    private static JsonObject createRootDataset() {
        JsonObject dataset = new JsonObject();

        dataset.addProperty("@id", "./");
        dataset.addProperty("@type", "Dataset");
        dataset.addProperty("name", "Categorical Reasoner Bibliography");

        // Add hasPart references
        JsonArray hasPart = new JsonArray();
        for (Citation citation : CitationRepository.getAll()) {
            JsonObject partRef = new JsonObject();
            partRef.addProperty("@id", "#" + citation.key().value());
            hasPart.add(partRef);
        }
        dataset.add("hasPart", hasPart);

        return dataset;
    }

    /**
     * Creates a citation entity from a Citation record.
     */
    private static JsonObject createCitationEntity(Citation citation) {
        JsonObject entity = new JsonObject();

        String id = "#" + citation.key().value();
        entity.addProperty("@id", id);

        // Set type based on work type
        entity.addProperty("@type", mapWorkTypeToSchemaOrg(citation.type()));
        entity.addProperty("name", citation.title().text());

        // Add language tag if present
        citation.title().languageTag().ifPresent(tag ->
            entity.addProperty("inLanguage", tag)
        );

        // Add authors
        JsonArray authors = new JsonArray();
        for (Person author : citation.authors()) {
            JsonObject authorRef = new JsonObject();
            authorRef.addProperty("@id", "#person-" + normalizePersonId(author));
            authors.add(authorRef);
        }
        entity.add("author", authors);

        // Add publication date
        entity.addProperty("datePublished", citation.date().toIsoString());

        // Add DOI if present
        citation.doi().ifPresent(doi -> {
            entity.addProperty("doi", doi.value());
            entity.addProperty("url", doi.url());
        });

        // Add Wikidata QID if present
        citation.wikidata().ifPresent(qid ->
            entity.addProperty("sameAs", qid.url())
        );

        // Add arXiv link if present
        citation.arxiv().ifPresent(arxiv -> {
            entity.addProperty("arxivId", arxiv.value());
            entity.addProperty("url", arxiv.url());
        });

        // Add formalization status
        entity.addProperty("formalizationStatus", citation.status().name().toLowerCase());

        // Add agent context if present
        citation.agentContext().ifPresent(context -> {
            entity.addProperty("relevance", context.relevance());
            context.category().ifPresent(cat ->
                entity.addProperty("category", cat)
            );
            context.notes().ifPresent(notes ->
                entity.addProperty("agentNotes", notes)
            );
            context.lastUpdated().ifPresent(updated ->
                entity.addProperty("lastUpdated", updated)
            );
        });

        // Add dependencies
        if (!citation.dependsOn().isEmpty()) {
            JsonArray dependencies = new JsonArray();
            for (CitationKey depKey : citation.dependsOn()) {
                JsonObject depRef = new JsonObject();
                depRef.addProperty("@id", "#" + depKey.value());
                dependencies.add(depRef);
            }
            entity.add("dependencies", dependencies);
        }

        return entity;
    }

    /**
     * Creates a person entity.
     */
    private static JsonObject createPersonEntity(Person person) {
        JsonObject entity = new JsonObject();

        String id = "#person-" + normalizePersonId(person);
        entity.addProperty("@id", id);
        entity.addProperty("@type", "Person");
        entity.addProperty("familyName", person.familyName());

        person.givenName().ifPresent(name ->
            entity.addProperty("givenName", name)
        );

        person.particle().ifPresent(particle ->
            entity.addProperty("additionalName", particle)
        );

        person.suffix().ifPresent(suffix ->
            entity.addProperty("honorificSuffix", suffix)
        );

        return entity;
    }

    /**
     * Maps WorkType enum to schema.org types.
     */
    private static String mapWorkTypeToSchemaOrg(WorkType type) {
        return switch (type) {
            case ARTICLE -> "ScholarlyArticle";
            case BOOK -> "Book";
            case CONFERENCE -> "ScholarlyArticle";
            case INCOLLECTION -> "Chapter";
            case THESIS -> "Thesis";
            case REPORT -> "TechArticle";
            case PREPRINT -> "ScholarlyArticle";
        };
    }

    /**
     * Normalizes a person to a stable identifier.
     */
    private static String normalizePersonId(Person person) {
        StringBuilder sb = new StringBuilder(person.familyName().toLowerCase().replace(" ", "-"));
        person.givenName().ifPresent(name ->
            sb.append("-").append(name.toLowerCase().replace(" ", "-"))
        );
        return sb.toString();
    }
}
