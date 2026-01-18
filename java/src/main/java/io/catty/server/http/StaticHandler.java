package io.catty.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/** Serves static resources bundled in the JAR under {@code /static}. */
public final class StaticHandler implements HttpHandler {

  private final String resourcePath;
  private final String contentType;

  public StaticHandler(final String resourcePath, final String contentType) {
    this.resourcePath = resourcePath;
    this.contentType = contentType;
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
      HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
      return;
    }

    try (InputStream in = StaticHandler.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        HttpUtil.sendText(exchange, 404, "text/plain; charset=utf-8", "Not Found");
        return;
      }
      final byte[] bytes = in.readAllBytes();
      exchange.getResponseHeaders().set("Content-Type", contentType);
      exchange.sendResponseHeaders(200, bytes.length);
      exchange.getResponseBody().write(bytes);
      exchange.getResponseBody().close();
    }
  }

  public static String guessContentType(final String path) {
    final String lower = path.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".html")) {
      return "text/html; charset=utf-8";
    }
    if (lower.endsWith(".js")) {
      return "text/javascript; charset=utf-8";
    }
    if (lower.endsWith(".css")) {
      return "text/css; charset=utf-8";
    }
    if (lower.endsWith(".json")) {
      return "application/json; charset=utf-8";
    }
    return "application/octet-stream";
  }

  public static StaticHandler forString(final String body, final String contentType) {
    return new StaticHandler("/static/inline", contentType) {
      @Override
      public void handle(final HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
          HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
          return;
        }
        HttpUtil.sendText(exchange, 200, contentType, body);
      }
    };
  }
}
