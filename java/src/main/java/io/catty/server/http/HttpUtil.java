package io.catty.server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Miscellaneous HTTP helpers for the embedded server. */
public final class HttpUtil {

  private static final int MAX_BODY_BYTES = 1_000_000;

  private HttpUtil() {}

  public static String readBodyUtf8(final HttpExchange exchange) throws IOException {
    try (InputStream in = exchange.getRequestBody()) {
      final byte[] bytes = in.readNBytes(MAX_BODY_BYTES + 1);
      if (bytes.length > MAX_BODY_BYTES) {
        throw new IOException("Request body too large (max " + MAX_BODY_BYTES + " bytes)");
      }
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }

  public static void sendJson(final HttpExchange exchange, final int status, final Object payload) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    final byte[] out = mapper.writeValueAsBytes(payload);
    final Headers headers = exchange.getResponseHeaders();
    headers.set("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(status, out.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(out);
    }
  }

  public static void sendText(final HttpExchange exchange, final int status, final String contentType, final String body)
      throws IOException {
    final byte[] out = body.getBytes(StandardCharsets.UTF_8);
    final Headers headers = exchange.getResponseHeaders();
    headers.set("Content-Type", contentType);
    exchange.sendResponseHeaders(status, out.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(out);
    }
  }

  public static void sendNoContent(final HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(204, -1);
    exchange.close();
  }
}
