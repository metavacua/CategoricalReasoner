package io.catty.server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Locates the Catty repository root by searching parent directories for
 * {@code .catty/iri-config.yaml}.
 */
public final class RepoLocator {

  private RepoLocator() {}

  /**
   * Search upward from the given directory for the Catty repository root.
   *
   * @param start starting directory
   * @return repo root directory
   * @throws IllegalStateException if no repo root can be found
   */
  public static Path findRepoRoot(final Path start) {
    Path cur = start.toAbsolutePath().normalize();
    while (cur != null) {
      if (Files.exists(cur.resolve(".catty").resolve("iri-config.yaml"))) {
        return cur;
      }
      cur = cur.getParent();
    }
    throw new IllegalStateException("Unable to locate repo root containing .catty/iri-config.yaml (start=" + start + ")");
  }

  /**
   * Best-effort search that returns empty rather than throwing.
   */
  public static Optional<Path> tryFindRepoRoot(final Path start) {
    try {
      return Optional.of(findRepoRoot(start));
    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }
}
