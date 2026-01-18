package io.catty.server.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** Tiny query-string parsing helper. */
public final class QueryString {

  private QueryString() {}

  public static String get(final String query, final String key) {
    if (query == null || query.isBlank()) {
      return null;
    }
    for (String part : query.split("&")) {
      final int idx = part.indexOf('=');
      if (idx <= 0) {
        continue;
      }
      final String k = decode(part.substring(0, idx));
      if (key.equals(k)) {
        return decode(part.substring(idx + 1));
      }
    }
    return null;
  }

  private static String decode(final String s) {
    return URLDecoder.decode(s, StandardCharsets.UTF_8);
  }
}
