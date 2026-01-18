package io.catty.server.iri;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.config.OntologyEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Validates that JSON-LD content does not introduce fabricated IRIs.
 *
 * <p>This implementation is intentionally conservative: it validates all
 * {@code @id} values plus the {@code @base} found inside {@code @context}.</p>
 */
public final class IriSafetyValidator {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private IriSafetyValidator() {}

  /**
   * Validate JSON-LD content for IRI safety.
   *
   * @param jsonLd JSON-LD as a UTF-8 string
   * @param config loaded IRI registry
   * @return report with errors
   */
  public static IriSafetyReport validate(final String jsonLd, final CattyIriConfig config) {
    Objects.requireNonNull(jsonLd, "jsonLd");
    Objects.requireNonNull(config, "config");

    final List<String> errors = new ArrayList<>();
    final JsonNode root;

    try {
      root = MAPPER.readTree(jsonLd);
    } catch (IOException e) {
      errors.add("Invalid JSON-LD (malformed JSON): " + e.getMessage());
      return new IriSafetyReport(false, List.copyOf(errors), null);
    }

    final Set<String> allowedBases = new HashSet<>();
    for (OntologyEntry e : config.listOntologies()) {
      allowedBases.add(e.localhostIri());
      allowedBases.add(e.productionIri());
    }

    final Set<String> allowedCompactPrefixes = Set.of(
        "catty",
        "lao",
        "mc",
        "lattice",
        "ch",
        "ex",
        "cit",
        "cu",
        "owl",
        "rdf",
        "rdfs",
        "xsd",
        "dct",
        "prov",
        "bibo",
        "skos",
        "dbo",
        "wd",
        "math"
    );

    final String baseIri = extractBaseIri(root);
    if (baseIri == null) {
      errors.add("Missing @base in @context");
    } else if (!allowedBases.contains(baseIri)) {
      errors.add("Unregistered @base IRI: " + baseIri);
    }

    final List<String> ids = new ArrayList<>();
    collectIds(root, ids);

    for (String rawId : ids) {
      if (rawId.startsWith("_:") || rawId.isBlank()) {
        continue;
      }

      if (rawId.startsWith("http://") || rawId.startsWith("https://")) {
        if (!isAllowedAbsolute(rawId, allowedBases)) {
          errors.add("Unauthorized @id IRI: " + rawId);
        }
        continue;
      }

      final int colonIdx = rawId.indexOf(':');
      if (colonIdx > 0) {
        final String prefix = rawId.substring(0, colonIdx);
        if (!allowedCompactPrefixes.contains(prefix)) {
          errors.add("Unauthorized compact IRI prefix in @id: " + rawId);
        }
      }
      // Relative IDs are allowed (they resolve against @base).
    }

    return new IriSafetyReport(errors.isEmpty(), List.copyOf(errors), baseIri);
  }

  private static boolean isAllowedAbsolute(final String iri, final Set<String> allowedBases) {
    for (String base : allowedBases) {
      if (iri.startsWith(base)) {
        return true;
      }
    }

    return iri.startsWith("http://www.w3.org/")
        || iri.startsWith("https://www.w3.org/")
        || iri.startsWith("http://purl.org/")
        || iri.startsWith("https://purl.org/")
        || iri.startsWith("http://dbpedia.org/")
        || iri.startsWith("https://dbpedia.org/")
        || iri.startsWith("http://www.wikidata.org/")
        || iri.startsWith("https://www.wikidata.org/")
        || iri.startsWith("http://doi.org/")
        || iri.startsWith("https://doi.org/")
        || iri.startsWith("http://arxiv.org/")
        || iri.startsWith("https://arxiv.org/")
        || iri.startsWith("http://en.wikipedia.org/")
        || iri.startsWith("https://en.wikipedia.org/")
        || iri.startsWith("http://metavacua.github.io/")
        || iri.startsWith("https://metavacua.github.io/")
        || iri.startsWith("https://ncatlab.org/");
  }

  private static String extractBaseIri(final JsonNode root) {
    final JsonNode ctx = root.get("@context");
    if (ctx == null) {
      return null;
    }

    if (ctx.isObject()) {
      final JsonNode base = ctx.get("@base");
      return base != null && base.isTextual() ? base.asText() : null;
    }

    if (ctx.isArray()) {
      for (JsonNode item : ctx) {
        if (item != null && item.isObject()) {
          final JsonNode base = item.get("@base");
          if (base != null && base.isTextual()) {
            return base.asText();
          }
        }
      }
    }

    return null;
  }

  private static void collectIds(final JsonNode node, final List<String> ids) {
    if (node == null) {
      return;
    }

    if (node.isObject()) {
      final JsonNode id = node.get("@id");
      if (id != null && id.isTextual()) {
        ids.add(id.asText());
      }
      node.fields().forEachRemaining(e -> collectIds(e.getValue(), ids));
      return;
    }

    if (node.isArray()) {
      for (JsonNode item : node) {
        collectIds(item, ids);
      }
    }
  }
}
