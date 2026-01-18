package io.catty.server.rdf;

import org.apache.jena.riot.Lang;

/** Supported RDF serialization formats for API endpoints. */
public enum RdfFormat {
  TURTLE("turtle", "text/turtle; charset=utf-8", Lang.TURTLE),
  JSONLD("jsonld", "application/ld+json; charset=utf-8", Lang.JSONLD),
  RDFXML("rdfxml", "application/rdf+xml; charset=utf-8", Lang.RDFXML);

  private final String id;
  private final String contentType;
  private final Lang lang;

  RdfFormat(final String id, final String contentType, final Lang lang) {
    this.id = id;
    this.contentType = contentType;
    this.lang = lang;
  }

  public String id() {
    return id;
  }

  public String contentType() {
    return contentType;
  }

  public Lang lang() {
    return lang;
  }

  public static RdfFormat fromId(final String id) {
    if (id == null) {
      return TURTLE;
    }
    return switch (id.toLowerCase()) {
      case "turtle", "ttl" -> TURTLE;
      case "jsonld", "json-ld" -> JSONLD;
      case "rdfxml", "rdf-xml", "xml" -> RDFXML;
      default -> TURTLE;
    };
  }
}
