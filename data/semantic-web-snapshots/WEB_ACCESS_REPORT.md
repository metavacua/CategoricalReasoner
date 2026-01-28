# Web Access Capabilities and Limitations (Staged Verification)

**Updated:** 2026-01-28  
**Repository:** Catty thesis  
**Primary scripts:**
- `scripts/verify-web-access.sh` (Stage 0 connectivity + tool installation + SPARQL checks)
- `scripts/fetch-semantic-web-logics.sh` (reproducible snapshot pipeline)

---

## 1. Network Connectivity

### DNS Resolution
- **Status:** PASS
- **Tools used:** `nslookup`, `dig`, `host`
- **Evidence:** `data/semantic-web-snapshots/web-access/verify-web-access_*.log`
- **Notes:** DNS resolution confirmed for `google.com`, `www.wikidata.org`, `dbpedia.org`, `ncatlab.org`, `query.wikidata.org`.

### ICMP (ping)
- **Status:** PASS
- **Tool used:** `ping` (iputils)
- **Targets tested:** `8.8.8.8`, `1.1.1.1`
- **Evidence:** `data/semantic-web-snapshots/web-access/verify-web-access_*.log`

### HTTPS + Certificate Validation
- **Status:** PASS
- **Tools used:** `wget`, `curl`, `openssl s_client`
- **Evidence:** `data/semantic-web-snapshots/web-access/verify-web-access_*.log`
- **Observed:** TLS handshake succeeds; certificate verification OK.

---

## 2. CLI Tools (Availability + Installation Attempts)

Stage 0 was re-run **after installing missing tools**.

### Newly installed via apt
- `dnsutils` (provides `nslookup`, `dig`, `host`)
- `iputils-ping` (provides `ping`)
- `jq`
- `traceroute`
- `raptor2-utils` (provides `rapper`, an RDF CLI parser)

### Installed tools used in verification and/or pipeline
- `wget` (fetch)
- `curl` (fetch + SPARQL requests)
- `nslookup`, `dig`, `host` (DNS)
- `ping` (ICMP)
- `openssl` (TLS/cert inspection)
- `jq` (JSON inspection)
- `rapper` (RDF parsing)

### apt-get note
`sudo apt-get update` produced a warning about an expired Yarn repository signing key, but Ubuntu repository packages were still installable. Full output is preserved in the `verify-web-access` log.

---

## 3. Semantic Web Connectivity (Endpoints)

### Wikidata SPARQL endpoint
- **Endpoint:** https://query.wikidata.org/sparql
- **Status:** PASS
- **Tool used:** `curl` (Accept: text/csv)
- **Evidence:** CSV output saved under `data/semantic-web-snapshots/web-access/wikidata_sparql_sample_*.csv`

### DBPedia SPARQL endpoint
- **Endpoint:** https://dbpedia.org/sparql
- **Status:** PASS
- **Tool used:** `curl` (Accept: text/csv)
- **Evidence:** CSV output saved under `data/semantic-web-snapshots/web-access/dbpedia_sparql_sample_*.csv`

---

## 4. Fetch Results (RDF / JSON-LD / Turtle Snapshots)

### Primary snapshot manifest
- **Manifest:** `data/semantic-web-snapshots/MANIFEST.yaml`
- Includes SHA256 hashes, sizes, timestamps, and per-source `.log` files.

### Wikidata
**Core logic identifiers used for Catty:**
- Classical logic: **Q236975**
- Intuitionistic logic: **Q176786**

**Additional logic / proof-system items fetched (examples):**
- Sequent calculus: **Q1771121**
- Structural rule: **Q4548693**
- Weakening (structural rule): **Q19720140**
- Cut rule: **Q18400501**
- Linear logic: **Q841728**
- Affine logic: **Q4688943**
- Relevance logic: **Q176630**
- Noncommutative logic: **Q7049221**

**Axioms / proof principles fetched (examples):**
- Principle of excluded middle: **Q468422**
- Law of noncontradiction: **Q868437**
- Double negation elimination: **Q5300067**
- Principle of explosion: **Q60190**
- Proof by contradiction: **Q184899**
- Reductio ad absurdum: **Q14402006**

**LDJ status:**
- No dedicated Wikidata item for “dual intuitionistic logic” as a *logic system* was found via `wbsearchentities` during this run.
- A related **scholarly article** item exists and was snapshotted: **Q124829676** (“Dual Intuitionistic Logic and a Variety of Negations: The Logic of Scientific Research”).

Each Wikidata item is fetched in **two formats**:
- `Special:EntityData/<Q>.json`
- `Special:EntityData/<Q>.ttl`

### DBPedia
DBPedia TTL pages fetched (with local triple counts via `rdflib`):
- `Classical_logic.ttl` — **312 triples**
- `Intuitionistic_logic.ttl` — **407 triples**
- `Sequent_calculus.ttl` — **236 triples**
- `Linear_logic.ttl` — **251 triples**
- `Relevance_logic.ttl` — **145 triples**

---

## 5. What Was Found vs. What Catty Needs

### Found (confirmed)
- **Stable identifiers** for logics and many related concepts (Wikidata QIDs; DBPedia resources)
- **Separate Wikidata entities** for key axioms and proof principles (excluded middle, noncontradiction, explosion, etc.)
- **Separate Wikidata entities** for proof-system / structural-rule concepts (sequent calculus, weakening, cut, etc.)
- **SPARQL access** to both Wikidata and DBPedia endpoints

### Not found (in this limited, reproducible scan)
This is **NOT an exhaustive claim**, only the outcome of the current snapshot pipeline:
- Detailed *machine-readable* sequent calculus rule schemata embedded as structured RDF *inside* the “classical logic” / “intuitionistic logic” entities.
- Direct “morphism between logics” encoding as categorical maps (Catty will still need its own ontology structures for this).

---

## 6. Repository References Used

Semantic web resource pointers already exist in this repo:
- `ontology/external-ontologies.md` (DBPedia, Wikidata, COLORE, OpenMath, nLab)
- `ontology/ontological-inventory.md` (includes SPARQL endpoints and example URIs)

These documents guided which endpoints/resources to test and snapshot.

---

## 7. How to Reproduce

```bash
# Stage 0: connectivity + tools + SPARQL
./scripts/verify-web-access.sh

# Stage 1+: snapshot pipeline (writes MANIFEST.yaml + per-file .log)
./scripts/fetch-semantic-web-logics.sh

# Validate some artifacts
python3 -m json.tool data/semantic-web-snapshots/wikidata/Q236975_Classical_logic.json > /dev/null
python3 -c "from rdflib import Graph; g=Graph(); g.parse('data/semantic-web-snapshots/dbpedia/Classical_logic.ttl', format='turtle'); print(len(g))"

# RDF CLI parse test
rapper -i turtle data/semantic-web-snapshots/dbpedia/Classical_logic.ttl -o ntriples | head
```
