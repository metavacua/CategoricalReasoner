package io.catty.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.config.OntologyEntry;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Lists registered ontologies. */
public final class ApiOntologiesHandler implements HttpHandler {

  private final CattyIriConfig config;

  public ApiOntologiesHandler(final CattyIriConfig config) {
    this.config = Objects.requireNonNull(config, "config");
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
      HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
      return;
    }

    final List<Map<String, String>> payload = config.listOntologies().stream()
        .map(this::asJson)
        .collect(Collectors.toList());

    HttpUtil.sendJson(exchange, 200, payload);
  }

  private Map<String, String> asJson(final OntologyEntry e) {
    return Map.of(
        "key", e.key(),
        "localhost_iri", e.localhostIri(),
        "production_iri", e.productionIri(),
        "context_url", e.contextUrl(),
        "file", e.filePath().toString()
    );
  }
}
