package io.catty.server.rdf;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.config.OntologyEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

/**
 * In-memory Jena dataset for local Catty development.
 *
 * <p>This dataset is created at process startup and populated from the ontology
 * files referenced by {@code .catty/iri-config.yaml}. No external services are
 * required.</p>
 */
public final class InMemoryCattyDataset {

  private final Dataset dataset;

  /**
   * Create a dataset and load all ontologies.
   *
   * @param repoRoot repository root directory
   * @param iriConfig loaded IRI registry
   * @throws IOException if any ontology file cannot be read
   */
  public InMemoryCattyDataset(final Path repoRoot, final CattyIriConfig iriConfig) throws IOException {
    Objects.requireNonNull(repoRoot, "repoRoot");
    Objects.requireNonNull(iriConfig, "iriConfig");

    this.dataset = DatasetFactory.createTxnMem();
    loadAll(repoRoot, iriConfig.listOntologies());
  }

  /**
   * Access the underlying Jena dataset.
   */
  public Dataset dataset() {
    return dataset;
  }

  private void loadAll(final Path repoRoot, final List<OntologyEntry> entries) throws IOException {
    for (OntologyEntry entry : entries) {
      final Path path = repoRoot.resolve(entry.filePath()).normalize();
      if (!Files.exists(path)) {
        throw new IOException("Ontology file not found for key '" + entry.key() + "': " + path);
      }

      final Model m = ModelFactory.createDefaultModel();
      // Provide a base URI so that relative context references (e.g. "context.jsonld") resolve
      // against the ontology file location.
      RDFParser.source(path.toUri().toString())
          .lang(Lang.JSONLD)
          .parse(m);

      // Store each ontology as a named graph using its localhost base IRI.
      dataset.addNamedModel(entry.localhostIri(), m);
    }
  }
}
