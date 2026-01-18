package io.catty.server;

import com.sun.net.httpserver.HttpServer;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.http.ApiGraphHandler;
import io.catty.server.http.ApiLoadHandler;
import io.catty.server.http.ApiOntologiesHandler;
import io.catty.server.http.ApiQueryHandler;
import io.catty.server.http.ApiRebindHandler;
import io.catty.server.http.HttpUtil;
import io.catty.server.http.RepoOntologyHandler;
import io.catty.server.http.StaticHandler;
import io.catty.server.rdf.InMemoryCattyDataset;
import io.catty.server.util.RepoLocator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Embedded localhost server for Catty semantic web development.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Loads all registered ontologies into an in-memory Jena dataset</li>
 *   <li>Exposes SPARQL query endpoint</li>
 *   <li>Serves a minimal browser UI for interactive querying</li>
 *   <li>Supports JSON-LD IRI rebinding (localhost ↔ production)</li>
 * </ul>
 */
public final class CattyServer {

  private final HttpServer server;

  /**
   * Create a new server instance.
   *
   * @param address bind address
   * @param repoRoot repository root
   * @throws IOException if the server cannot start or ontologies cannot load
   */
  public CattyServer(final InetSocketAddress address, final Path repoRoot) throws IOException {
    Objects.requireNonNull(address, "address");
    Objects.requireNonNull(repoRoot, "repoRoot");

    final Path configPath = repoRoot.resolve(".catty").resolve("iri-config.yaml");
    final CattyIriConfig iriConfig = new CattyIriConfig(configPath);
    final InMemoryCattyDataset dataset = new InMemoryCattyDataset(repoRoot, iriConfig);

    this.server = HttpServer.create(address, 0);

    // Static UI
    server.createContext("/", new StaticHandler("/static/index.html", "text/html; charset=utf-8"));
    server.createContext("/app.js", new StaticHandler("/static/app.js", "text/javascript; charset=utf-8"));
    server.createContext("/styles.css", new StaticHandler("/static/styles.css", "text/css; charset=utf-8"));

    // Ontology dereferencing
    server.createContext("/ontology/", new RepoOntologyHandler(repoRoot));

    // APIs
    server.createContext("/api/ontologies", new ApiOntologiesHandler(iriConfig));
    server.createContext("/api/query", new ApiQueryHandler(dataset));
    server.createContext("/api/graph", new ApiGraphHandler(dataset));
    server.createContext("/api/load", new ApiLoadHandler(dataset, iriConfig));
    server.createContext("/api/rebind", new ApiRebindHandler(iriConfig));

    server.createContext("/health", exchange -> HttpUtil.sendText(exchange, 200, "text/plain; charset=utf-8", "ok"));

    server.setExecutor(null);
  }

  /** Start serving requests. */
  public void start() {
    server.start();
  }

  /** Stop the server. */
  public void stop(final int delaySeconds) {
    server.stop(delaySeconds);
  }

  /**
   * CLI entrypoint.
   *
   * <p>Typical usage:</p>
   *
   * <pre>{@code
   * cd java
   * mvn -q test
   * mvn -q exec:java -Dexec.args="--port 8080"
   * }</pre>
   */
  public static void main(final String[] args) throws Exception {
    int port = 8080;
    Path repoRoot = null;

    for (int i = 0; i < args.length; i++) {
      final String arg = args[i];
      if ("--port".equals(arg) && i + 1 < args.length) {
        port = Integer.parseInt(args[++i]);
      } else if ("--repo".equals(arg) && i + 1 < args.length) {
        repoRoot = Path.of(args[++i]);
      }
    }

    if (repoRoot == null) {
      // If launched from ./java, repo root is likely parent.
      repoRoot = RepoLocator.findRepoRoot(Path.of(System.getProperty("user.dir")));
    }

    final CattyServer srv = new CattyServer(new InetSocketAddress("127.0.0.1", port), repoRoot);
    srv.start();

    System.out.println("Catty server running at http://localhost:" + port + "/");
  }
}
