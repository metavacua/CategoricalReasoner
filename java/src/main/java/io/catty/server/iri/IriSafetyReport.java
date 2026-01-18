package io.catty.server.iri;

import java.util.List;

/** Result of an IRI safety validation run. */
public record IriSafetyReport(boolean ok, List<String> errors, String baseIri) {}
