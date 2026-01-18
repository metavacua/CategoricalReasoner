package io.catty.server.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class CattyIriConfigTest {

  @TempDir
  Path tmp;

  @Test
  void testLoadValidConfig() throws Exception {
    final Path cfg = tmp.resolve(".catty").resolve("iri-config.yaml");
    Files.createDirectories(cfg.getParent());

    Files.writeString(
        cfg,
        """
localhost:
  base_url: \"http://localhost:8080\"
  namespace_path: \"/ontology\"
production:
  base_url: \"https://example.com\"
  namespace_path: \"/ontology\"
ontologies:
  a:
    localhost_iri: \"http://localhost:8080/ontology/a#\"
    production_iri: \"https://example.com/ontology/a#\"
    context_url: \"http://localhost:8080/ontology/context.jsonld\"
    file: \"ontology/a.jsonld\"
"""
    );

    final CattyIriConfig config = new CattyIriConfig(cfg);
    final OntologyEntry a = config.ontology("a");

    assertNotNull(a);
    assertEquals("http://localhost:8080/ontology/a#", a.localhostIri());
    assertEquals(Path.of("ontology/a.jsonld"), a.filePath());
  }
}
