package io.catty.server.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class RepoOntologyHandlerTest {

  @TempDir
  Path tmp;

  @Test
  void testServeExistingFile() throws Exception {
    final Path ontologyDir = tmp.resolve("ontology");
    Files.createDirectories(ontologyDir);
    Files.writeString(ontologyDir.resolve("test.jsonld"), "{\"@context\": {}}");

    final RepoOntologyHandler handler = new RepoOntologyHandler(tmp);
    final MockHttpExchange exchange = new MockHttpExchange("GET", "/ontology/test.jsonld");

    handler.handle(exchange);

    assertEquals(200, exchange.responseCode);
    assertTrue(exchange.responseBody.toString().contains("@context"));
  }

  @Test
  void testRejectPathTraversal() throws Exception {
    final Path ontologyDir = tmp.resolve("ontology");
    Files.createDirectories(ontologyDir);
    Files.writeString(tmp.resolve("secret.txt"), "secret");

    final RepoOntologyHandler handler = new RepoOntologyHandler(tmp);
    final MockHttpExchange exchange = new MockHttpExchange("GET", "/ontology/../secret.txt");

    handler.handle(exchange);

    assertEquals(400, exchange.responseCode);
  }

  @Test
  void testRejectMissingFile() throws Exception {
    final Path ontologyDir = tmp.resolve("ontology");
    Files.createDirectories(ontologyDir);

    final RepoOntologyHandler handler = new RepoOntologyHandler(tmp);
    final MockHttpExchange exchange = new MockHttpExchange("GET", "/ontology/missing.jsonld");

    handler.handle(exchange);

    assertEquals(404, exchange.responseCode);
  }

  private static final class MockHttpExchange extends HttpExchange {
    private final String method;
    private final String path;
    final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    int responseCode;

    MockHttpExchange(final String method, final String path) {
      this.method = method;
      this.path = path;
    }

    @Override
    public String getRequestMethod() {
      return method;
    }

    @Override
    public URI getRequestURI() {
      return URI.create(path);
    }

    @Override
    public com.sun.net.httpserver.Headers getResponseHeaders() {
      return new com.sun.net.httpserver.Headers();
    }

    @Override
    public void sendResponseHeaders(final int code, final long length) {
      this.responseCode = code;
    }

    @Override
    public OutputStream getResponseBody() {
      return responseBody;
    }

    @Override
    public void close() {}

    @Override
    public com.sun.net.httpserver.Headers getRequestHeaders() {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.io.InputStream getRequestBody() {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sun.net.httpserver.HttpContext getHttpContext() {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.net.InetSocketAddress getRemoteAddress() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getResponseCode() {
      return responseCode;
    }

    @Override
    public java.net.InetSocketAddress getLocalAddress() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(final String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(final String name, final Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setStreams(final java.io.InputStream i, final OutputStream o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.security.Principal getPrincipal() {
      throw new UnsupportedOperationException();
    }
  }
}
