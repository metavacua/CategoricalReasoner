package io.catty.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.Yaml;

/**
 * Loads and provides access to the Catty IRI configuration registry.
 *
 * <p>The canonical registry is stored at {@code .catty/iri-config.yaml} in the
 * repository root.</p>
 */
public final class CattyIriConfig {

  private final Path configPath;
  private final Map<String, OntologyEntry> ontologies;

  /**
   * Load a registry from disk.
   *
   * @param configPath path to {@code .catty/iri-config.yaml}
   * @throws IOException if the file cannot be read
   * @throws IllegalArgumentException if required fields are missing
   */
  public CattyIriConfig(final Path configPath) throws IOException {
    this.configPath = Objects.requireNonNull(configPath, "configPath");
    this.ontologies = Collections.unmodifiableMap(load(configPath));
  }

  /**
   * Return the absolute path to the loaded registry.
   */
  public Path configPath() {
    return configPath;
  }

  /**
   * Get an ontology entry by key.
   *
   * @param key ontology key
   * @return ontology entry
   * @throws IllegalArgumentException if the key is unknown
   */
  public OntologyEntry ontology(final String key) {
    final OntologyEntry entry = ontologies.get(key);
    if (entry == null) {
      throw new IllegalArgumentException("Unknown ontology key: " + key);
    }
    return entry;
  }

  /**
   * Return all registered ontology entries.
   */
  public List<OntologyEntry> listOntologies() {
    return new ArrayList<>(ontologies.values());
  }

  /**
   * Return an ordered map of ontology entries keyed by registry key.
   */
  public Map<String, OntologyEntry> ontologies() {
    return ontologies;
  }

  private static Map<String, OntologyEntry> load(final Path configPath) throws IOException {
    if (!Files.exists(configPath)) {
      throw new IOException("IRI config not found: " + configPath);
    }

    final Yaml yaml = new Yaml();
    final Map<String, Object> root;
    try (InputStream in = Files.newInputStream(configPath)) {
      @SuppressWarnings("unchecked")
      final Map<String, Object> parsed = yaml.load(in);
      root = parsed;
    }

    if (root == null) {
      throw new IllegalArgumentException("IRI config is empty: " + configPath);
    }

    final Object ontologiesObj = root.get("ontologies");
    if (!(ontologiesObj instanceof Map<?, ?> ontologiesMap)) {
      throw new IllegalArgumentException("IRI config missing required mapping 'ontologies'");
    }

    final Map<String, OntologyEntry> out = new LinkedHashMap<>();
    for (Map.Entry<?, ?> e : ontologiesMap.entrySet()) {
      final String key = String.valueOf(e.getKey());
      if (!(e.getValue() instanceof Map<?, ?> entryMap)) {
        throw new IllegalArgumentException("Ontology entry must be a mapping: " + key);
      }

      final String localhostIri = requireString(entryMap, "localhost_iri", key);
      final String productionIri = requireString(entryMap, "production_iri", key);
      final String contextUrl = requireString(entryMap, "context_url", key);
      final String file = requireString(entryMap, "file", key);

      out.put(
          key,
          new OntologyEntry(key, localhostIri, productionIri, contextUrl, Path.of(file))
      );
    }

    return out;
  }

  private static String requireString(final Map<?, ?> map, final String field, final String key) {
    final Object v = map.get(field);
    if (!(v instanceof String s) || s.isBlank()) {
      throw new IllegalArgumentException("Ontology '" + key + "' missing required field '" + field + "'");
    }
    return s;
  }
}
