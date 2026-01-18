package io.catty.server.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.iri.IriRebinder;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/** Rebinds JSON-LD content between localhost and production IRIs. */
public final class ApiRebindHandler implements HttpHandler {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final CattyIriConfig config;

  public ApiRebindHandler(final CattyIriConfig config) {
    this.config = Objects.requireNonNull(config, "config");
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

    final String target = payload.hasNonNull("target") ? payload.get("target").asText() : "production";
    final IriRebinder.Target rebTarget = switch (target.toLowerCase(Locale.ROOT)) {
      case "localhost" -> IriRebinder.Target.LOCALHOST;
      case "production" -> IriRebinder.Target.PRODUCTION;
      default -> null;
    };

    if (rebTarget == null) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Invalid target; expected 'localhost' or 'production'");
      return;
    }

    final JsonNode contentNode = payload.get("content");
    if (contentNode == null || !contentNode.isTextual()) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Missing required field 'content'");
      return;
    }

    final IriRebinder rebinder = new IriRebinder(config, rebTarget);
    final String out = rebinder.rebind(contentNode.asText());

    final ObjectNode resp = MAPPER.createObjectNode();
    resp.put("content", out);
    HttpUtil.sendJson(exchange, 200, resp);
  }
}
