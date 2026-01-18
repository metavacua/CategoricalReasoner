package io.catty.server.iri;

import io.catty.server.config.CattyIriConfig;
import io.catty.server.config.OntologyEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Rebinds ontology IRIs between localhost and production environments.
 *
 * <p>This is a string-based rebinding utility intended for JSON-LD content.
 * It rewrites all occurrences of registered ontology base IRIs.</p>
 */
public final class IriRebinder {

  public enum Target {
    LOCALHOST,
    PRODUCTION
  }

  private final List<Replacement> replacements;

  /**
   * Build a rebinder from a registry.
   *
   * @param config loaded registry
   * @param target rebinding direction
   */
  public IriRebinder(final CattyIriConfig config, final Target target) {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(target, "target");

    final List<Replacement> reps = new ArrayList<>();
    for (OntologyEntry entry : config.listOntologies()) {
      if (target == Target.PRODUCTION) {
        reps.add(new Replacement(entry.localhostIri(), entry.productionIri()));
      } else {
        reps.add(new Replacement(entry.productionIri(), entry.localhostIri()));
      }
    }

    // Context rebinding (absolute + relative)
    final String localhostCtx = "http://localhost:8080/ontology/context.jsonld";
    final String productionCtx = "https://metavacua.github.io/CategoricalReasoner/ontology/context.jsonld";

    if (target == Target.PRODUCTION) {
      reps.add(new Replacement(localhostCtx, productionCtx));
      reps.add(new Replacement("context.jsonld", productionCtx));
      reps.add(new Replacement("./context.jsonld", productionCtx));
    } else {
      reps.add(new Replacement(productionCtx, localhostCtx));
      reps.add(new Replacement("context.jsonld", localhostCtx));
      reps.add(new Replacement("./context.jsonld", localhostCtx));
    }

    // Avoid partial replacements by rewriting longer strings first.
    reps.sort(Comparator.comparingInt((Replacement r) -> r.from().length()).reversed());
    this.replacements = List.copyOf(reps);
  }

  /**
   * Rebind an arbitrary content string.
   */
  public String rebind(final String content) {
    String out = content;
    for (Replacement r : replacements) {
      out = out.replace(r.from(), r.to());
    }
    return out;
  }

  private record Replacement(String from, String to) {}
}
