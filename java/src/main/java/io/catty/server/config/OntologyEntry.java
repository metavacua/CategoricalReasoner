package io.catty.server.config;

import java.nio.file.Path;

/**
 * Represents a single ontology entry from {@code .catty/iri-config.yaml}.
 *
 * @param key ontology key in the registry
 * @param localhostIri localhost base IRI
 * @param productionIri production base IRI
 * @param contextUrl configured context URL (registry metadata; files may use a relative context for offline work)
 * @param filePath repository-relative ontology file path
 */
public record OntologyEntry(
    String key,
    String localhostIri,
    String productionIri,
    String contextUrl,
    Path filePath
) {}
