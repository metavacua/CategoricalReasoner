package io.catty.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.catty.server.rdf.InMemoryCattyDataset;
import io.catty.server.rdf.RdfFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

/** Serializes the in-memory dataset as a single union model. */
public final class ApiGraphHandler implements HttpHandler {

  private final InMemoryCattyDataset dataset;

  public ApiGraphHandler(final InMemoryCattyDataset dataset) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
      HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
      return;
    }

    final String query = exchange.getRequestURI().getQuery();
    final String format = QueryString.get(query, "format");
    final RdfFormat rdfFormat = RdfFormat.fromId(format);

    dataset.dataset().begin(ReadWrite.READ);
    try {
      final Model union = dataset.dataset().getUnionModel();
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      RDFDataMgr.write(out, union, rdfFormat.lang());
      HttpUtil.sendText(exchange, 200, rdfFormat.contentType(), out.toString(StandardCharsets.UTF_8));
    } finally {
      dataset.dataset().end();
    }
  }
}
