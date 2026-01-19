package io.catty.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Serves ontology artifacts from the repository's {@code ontology/} directory.
 *
 * <p>This allows localhost dereferencing of IRIs like:
 * {@code http://localhost:8080/ontology/context.jsonld}.</p>
 */
public final class RepoOntologyHandler implements HttpHandler {

  private final Path ontologyDir;

  public RepoOntologyHandler(final Path repoRoot) {
    Objects.requireNonNull(repoRoot, "repoRoot");
    this.ontologyDir = repoRoot.resolve("ontology").normalize();
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
      HttpUtil.sendText(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
      return;
    }

    final String rawPath = exchange.getRequestURI().getPath();
    final String prefix = "/ontology/";
    if (!rawPath.startsWith(prefix)) {
      HttpUtil.sendText(exchange, 404, "text/plain; charset=utf-8", "Not Found");
      return;
    }

    final String rel = rawPath.substring(prefix.length());
    // Security: prevent path traversal attacks
    if (rel.isBlank() || rel.contains("..") || rel.contains("\\\\") || rel.contains("//")) {
      HttpUtil.sendText(exchange, 400, "text/plain; charset=utf-8", "Invalid path");
      return;
    }

    final Path file = ontologyDir.resolve(rel).normalize();
    // Security: ensure resolved path stays within ontologyDir (prevents path traversal)
    if (!file.startsWith(ontologyDir) || !Files.exists(file) || !Files.isRegularFile(file)) {
      HttpUtil.sendText(exchange, 404, "text/plain; charset=utf-8", "Not Found");
      return;
    }

    final String contentType = guessContentType(rel);
    final byte[] bytes = Files.readAllBytes(file);

    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(200, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.getResponseBody().close();
  }

  private static String guessContentType(final String rel) {
    final String lower = rel.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".jsonld") || lower.endsWith(".json")) {
      return "application/ld+json; charset=utf-8";
    }
    if (lower.endsWith(".ttl")) {
      return "text/turtle; charset=utf-8";
    }
    if (lower.endsWith(".md")) {
      return "text/markdown; charset=utf-8";
    }
    return "application/octet-stream";
  }
}
