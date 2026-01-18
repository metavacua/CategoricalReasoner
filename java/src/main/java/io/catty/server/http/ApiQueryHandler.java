package io.catty.server.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.catty.server.rdf.InMemoryCattyDataset;
import io.catty.server.rdf.RdfFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

/** Executes SPARQL queries against the in-memory dataset. */
public final class ApiQueryHandler implements HttpHandler {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final InMemoryCattyDataset dataset;

  public ApiQueryHandler(final InMemoryCattyDataset dataset) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!"POST".equals(exchange.getRequestMethod())) {
      HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
      return;
    }

    final String body = HttpUtil.readBodyUtf8(exchange);

    final JsonNode payload;
    try {
      payload = MAPPER.readTree(body);
    } catch (IOException e) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Invalid JSON body: " + e.getMessage());
      return;
    }

    final JsonNode queryNode = payload.get("query");
    if (queryNode == null || !queryNode.isTextual() || queryNode.asText().isBlank()) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Missing required field 'query'");
      return;
    }

    final String queryText = queryNode.asText();
    final RdfFormat rdfFormat = RdfFormat.fromId(payload.hasNonNull("format") ? payload.get("format").asText() : null);

    final Query query;
    try {
      query = QueryFactory.create(queryText);
    } catch (Exception e) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Invalid SPARQL query: " + e.getMessage());
      return;
    }

    dataset.dataset().begin(ReadWrite.READ);
    try {
      try (QueryExecution qe = QueryExecutionFactory.create(query, dataset.dataset())) {
        if (query.isSelectType()) {
          final ResultSet rs = qe.execSelect();
          final ByteArrayOutputStream out = new ByteArrayOutputStream();
          ResultSetFormatter.outputAsJSON(out, rs);
          HttpUtil.sendText(exchange, 200, "application/sparql-results+json; charset=utf-8", out.toString(StandardCharsets.UTF_8));
          return;
        }

        if (query.isAskType()) {
          final boolean value = qe.execAsk();
          final ObjectNode obj = MAPPER.createObjectNode();
          obj.put("boolean", value);
          HttpUtil.sendJson(exchange, 200, obj);
          return;
        }

        if (query.isConstructType()) {
          final Model m = qe.execConstruct();
          final ByteArrayOutputStream out = new ByteArrayOutputStream();
          RDFDataMgr.write(out, m, rdfFormat.lang());
          HttpUtil.sendText(exchange, 200, rdfFormat.contentType(), out.toString(StandardCharsets.UTF_8));
          return;
        }

        if (query.isDescribeType()) {
          final Model m = qe.execDescribe();
          final ByteArrayOutputStream out = new ByteArrayOutputStream();
          RDFDataMgr.write(out, m, rdfFormat.lang());
          HttpUtil.sendText(exchange, 200, rdfFormat.contentType(), out.toString(StandardCharsets.UTF_8));
          return;
        }
      }

      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Unsupported SPARQL query type");
    } finally {
      dataset.dataset().end();
    }
  }
}
