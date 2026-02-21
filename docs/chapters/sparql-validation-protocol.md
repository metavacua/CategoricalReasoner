---
title: "SPARQL Validation Protocol"
abbrev-title: "SPARQL Validation"
msc-primary: "68T35"
msc-secondary: ["03B35", "03B70"]
keywords: [SPARQL, validation, knowledge graph, query protocol]
---

# SPARQL Validation Protocol {#sec-sparql-validation}

## Protocol Overview {#subsec-protocol-overview}

The SPARQL validation protocol ensures that:

1. All documented queries run against external endpoints
2. Results are returned as valid TTL
3. Empty results or timeouts (over 60s) are treated as failures

## Query Categories {#subsec-query-categories}

| Category | Purpose | Example |
|----------|---------|---------|
| Discovery | Find related concepts | Find logics with specific properties |
| Verification | Check categorical axioms | Verify functor composition |
| Extraction | Pull metadata | Extract citation information |
| Validation | Confirm consistency | Check for contradictory axioms |

## Execution Requirements {#subsec-execution}

Queries must:

- Be syntactically valid SPARQL 1.1
- Return non-empty results against appropriate endpoints
- Complete within 60 seconds
- Produce well-formed Turtle output

## Endpoint Configuration {#subsec-endpoints}

Primary endpoints:

- Wikidata Query Service
- Custom Catty knowledge graph endpoint
- DBpedia SPARQL endpoint
