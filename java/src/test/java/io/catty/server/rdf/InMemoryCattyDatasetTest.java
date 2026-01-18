package io.catty.server.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.catty.server.config.CattyIriConfig;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class InMemoryCattyDatasetTest {

  @TempDir
  Path tmp;

  @Test
  void testDatasetLoadsOntologyAndQueryWorks() throws Exception {
    Files.createDirectories(tmp.resolve(".catty"));
    Files.createDirectories(tmp.resolve("ontology"));

    final Path cfg = tmp.resolve(".catty").resolve("iri-config.yaml");
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

    final Path ontology = tmp.resolve("ontology").resolve("a.jsonld");
    Files.writeString(
        ontology,
        """
{
  \"@context\": [
    {
      \"@version\": 1.1,
      \"owl\": \"http://www.w3.org/2002/07/owl#\",
      \"rdfs\": \"http://www.w3.org/2000/01/rdf-schema#\"
    },
    { \"@base\": \"http://localhost:8080/ontology/a#\" }
  ],
  \"@graph\": [
    {
      \"@id\": \"Thing\",
      \"@type\": \"owl:Class\",
      \"rdfs:label\": \"Thing\"
    }
  ]
}
"""
    );

    final CattyIriConfig config = new CattyIriConfig(cfg);
    final InMemoryCattyDataset ds = new InMemoryCattyDataset(tmp, config);

    ds.dataset().begin(ReadWrite.READ);
    try (QueryExecution qe = QueryExecutionFactory.create(
        """
PREFIX owl: <http://www.w3.org/2002/07/owl#>
SELECT (COUNT(?c) AS ?n) WHERE {
  GRAPH <http://localhost:8080/ontology/a#> {
    ?c a owl:Class .
  }
}
""",
        ds.dataset()
    )) {
      final ResultSet rs = qe.execSelect();
      final String n = rs.next().get("n").asLiteral().getLexicalForm();
      assertEquals("1", n);
    } finally {
      ds.dataset().end();
    }
  }
}
