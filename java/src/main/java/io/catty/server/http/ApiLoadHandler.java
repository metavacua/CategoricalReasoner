package io.catty.server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.iri.IriSafetyReport;
import io.catty.server.iri.IriSafetyValidator;
import io.catty.server.rdf.InMemoryCattyDataset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Loads posted RDF into the dataset.
 *
 * <p>Supported content-types: application/ld+json, text/turtle, application/rdf+xml</p>
 */
public final class ApiLoadHandler implements HttpHandler {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final InMemoryCattyDataset dataset;
  private final CattyIriConfig config;

  public ApiLoadHandler(final InMemoryCattyDataset dataset, final CattyIriConfig config) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
    this.config = Objects.requireNonNull(config, "config");
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!"POST".equals(exchange.getRequestMethod())) {
      HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
      return;
    }

    final String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
    final Lang lang = inferLang(contentType);

    final String body = HttpUtil.readBodyUtf8(exchange);

    if (lang.equals(Lang.JSONLD)) {
      final IriSafetyReport report = IriSafetyValidator.validate(body, config);
      if (!report.ok()) {
        final ObjectNode err = MAPPER.createObjectNode();
        err.put("ok", false);
        err.put("base_iri", report.baseIri());
        for (String msg : report.errors()) {
          err.withArray("errors").add(msg);
        }
        HttpUtil.sendJson(exchange, 400, err);
        return;
      }
    }

    final Model m = ModelFactory.createDefaultModel();
    try {
      RDFDataMgr.read(m, new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), lang);
    } catch (Exception e) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Unable to parse RDF payload: " + e.getMessage());
      return;
    }

    dataset.dataset().begin(ReadWrite.WRITE);
    try {
      dataset.dataset().getDefaultModel().add(m);
      dataset.dataset().commit();
    } finally {
      dataset.dataset().end();
    }

    final ObjectNode ok = MAPPER.createObjectNode();
    ok.put("ok", true);
    ok.put("triples_loaded", m.size());
    HttpUtil.sendJson(exchange, 200, ok);
  }

  private static Lang inferLang(final String contentType) {
    if (contentType == null) {
      return Lang.TURTLE;
    }

    final String ct = contentType.toLowerCase(Locale.ROOT);
    if (ct.contains("application/ld+json") || ct.contains("json")) {
      return Lang.JSONLD;
    }
    if (ct.contains("turtle") || ct.contains("text/plain")) {
      return Lang.TURTLE;
    }
    if (ct.contains("rdf+xml") || ct.contains("application/xml") || ct.contains("text/xml")) {
      return Lang.RDFXML;
    }

    return Lang.TURTLE;
  }
}
