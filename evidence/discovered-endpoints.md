# Discovered semantic web endpoints

This file records SPARQL endpoints discovered (and tested) while extracting broad evidence about sequent calculus / LK-adjacent topics.

## Primary endpoints (explicit targets)

| Endpoint | Role in this extraction | Status |
|---|---|---|
| https://query.wikidata.org/sparql | Primary evidence source; used for CONSTRUCT extraction | ✅ Queried successfully |
| https://dbpedia.org/sparql | Primary evidence source; used for CONSTRUCT extraction | ✅ Queried successfully |

## Secondary endpoints discovered (via Wikidata property P5305 “SPARQL endpoint”)

Discovery mechanism: `evidence/queries/wikidata-discover-endpoints.select.sparql` (Wikidata `wdt:P5305`).

| Endpoint | Discovered as | Attempted query | Result |
|---|---|---|---|
| https://lov.linkeddata.es/dataset/lov/sparql | Linked Open Vocabularies | `ASK { ?s ?p ?o }` | ✅ HTTP 200 |
| http://sparql.hegroup.org/sparql/ (redirects to https://sparql.hegroup.org/sparql/) | Ontobee SPARQL service | `ASK { ?s ?p ?o }` (with redirect follow) | ✅ HTTP 301 → 200 |
| https://qlever.dev/api/wikidata | QLever Wikidata service | `ASK { ?s ?p ?o }` | ✅ HTTP 200 |
| https://wcqs-beta.wmflabs.org/ | Wikimedia query service (beta) | `ASK { ?s ?p ?o }` (with redirect follow) | ✅ HTTP 301 → 200 |

Notes:
- Wikidata also lists multiple language-specific DBpedia endpoints (e.g. `http://de.dbpedia.org/sparql`), but this extraction only queried `https://dbpedia.org/sparql`.

## Known targets with **no SPARQL endpoint discovered** via Wikidata

These are relevant resources for formalization ecosystems, but no `wdt:P5305` value was found during this run.

| Resource | Wikidata check performed | Outcome |
|---|---|---|
| Archive of Formal Proofs | `wd:Q108305552 wdt:P5305 ?endpoint` | No bindings (no endpoint recorded) |

(Other resources like Isabelle/Coq/Lean/nLab were not reliably resolvable to a single Wikidata item by label in this run; if/when canonical QIDs are pinned, checking `wdt:P5305` is the intended mechanism.)
