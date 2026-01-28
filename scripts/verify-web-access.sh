#!/bin/bash
# verify-web-access.sh
# Repeat staged network + semantic-web connectivity checks and log results.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
OUT_DIR="${REPO_ROOT}/data/semantic-web-snapshots/web-access"
TS="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"

mkdir -p "${OUT_DIR}"

LOG_MAIN="${OUT_DIR}/verify-web-access_${TS}.log"

exec > >(tee "${LOG_MAIN}") 2>&1

echo "# Web Access Verification"
echo "timestamp: ${TS}"
echo "repo_root: ${REPO_ROOT}"
echo

echo "## System"
uname -a || true
id || true

echo

echo "## Tool availability (pre-install)"
command -v nslookup || true
command -v dig || true
command -v host || true
command -v ping || true
command -v wget || true
command -v curl || true
command -v jq || true
command -v traceroute || true
command -v openssl || true
command -v rapper || true

maybe_install() {
  local pkg="$1"
  if dpkg -s "$pkg" >/dev/null 2>&1; then
    return 0
  fi
  echo "Attempting install: $pkg"
  sudo apt-get install -y "$pkg"
}

echo

echo "## Attempting to install missing network/semantic-web tools"
# NOTE: apt-get update can warn about third-party repos; this script does not modify apt sources.
sudo apt-get update -y || true
maybe_install dnsutils || true
maybe_install iputils-ping || true
maybe_install jq || true
maybe_install traceroute || true
maybe_install raptor2-utils || true

echo

echo "## Tool versions (post-install)"
nslookup -version 2>&1 | head -5 || true
(dig -v 2>&1 | head -5) || true
(host -V 2>&1 | head -5) || true
(ping -V 2>&1 | head -3) || true
wget --version 2>&1 | head -3 || true
curl --version 2>&1 | head -3 || true
jq --version 2>&1 || true
traceroute --version 2>&1 | head -3 || true
rapper --version 2>&1 | head -3 || true
openssl version -a 2>&1 | head -5 || true
python3 --version 2>&1 || true

echo

echo "## STAGE 0.1: DNS Resolution"
for h in google.com www.wikidata.org dbpedia.org ncatlab.org query.wikidata.org; do
  echo
  echo "### nslookup ${h}"
  (time -p nslookup "${h}") 2>&1 | head -40 || true

  echo
  echo "### dig ${h}"
  (time -p dig "${h}" +stats +time=2 +tries=1) 2>&1 | head -60 || true

  echo
  echo "### host ${h}"
  (time -p host "${h}") 2>&1 | head -20 || true

done

echo

echo "## STAGE 0.2: ICMP connectivity (ping)"
for ip in 8.8.8.8 1.1.1.1; do
  echo
  echo "### ping ${ip}"
  (time -p ping -c 3 "${ip}") 2>&1 | head -30 || true

done

echo

echo "## STAGE 0.2b: traceroute (limited hops)"
for h in 8.8.8.8 www.wikidata.org; do
  echo
  echo "### traceroute ${h} (max 5 hops)"
  (time -p traceroute -n -m 5 -q 1 "${h}") 2>&1 | head -60 || true

done

echo

echo "## STAGE 0.3: HTTPS connectivity"

echo

echo "### wget --spider https://www.wikidata.org/wiki/Q236975"
(time -p wget --spider --timeout=10 https://www.wikidata.org/wiki/Q236975) 2>&1 | head -40 || true

echo

echo "### curl -I https://www.wikidata.org/wiki/Q236975"
(time -p curl -I --connect-timeout 10 https://www.wikidata.org/wiki/Q236975) 2>&1 | head -40 || true

echo

echo "### openssl s_client (wikidata.org)"
(time -p bash -lc 'echo | openssl s_client -connect www.wikidata.org:443 -servername www.wikidata.org -brief') 2>&1 | head -80 || true

echo

echo "## STAGE 0.4: Semantic web endpoints"

echo

echo "### Wikidata SPARQL (CSV)"
WIKIDATA_CSV="${OUT_DIR}/wikidata_sparql_sample_${TS}.csv"
WIKIDATA_SPARQL='SELECT ?item ?itemLabel WHERE { VALUES ?item { wd:Q236975 wd:Q176786 } SERVICE wikibase:label { bd:serviceParam wikibase:language "en". } }'
(time -p curl -sS --connect-timeout 10 -H 'Accept: text/csv' --data-urlencode "query=${WIKIDATA_SPARQL}" https://query.wikidata.org/sparql -o "${WIKIDATA_CSV}") 2>&1 | head -20 || true
head -20 "${WIKIDATA_CSV}" || true

echo

echo "### DBPedia SPARQL (CSV)"
DBPEDIA_CSV="${OUT_DIR}/dbpedia_sparql_sample_${TS}.csv"
DBPEDIA_SPARQL='SELECT * WHERE { VALUES ?s { <http://dbpedia.org/resource/Classical_logic> <http://dbpedia.org/resource/Intuitionistic_logic> } ?s ?p ?o } LIMIT 5'
(time -p curl -sS --connect-timeout 10 -H 'Accept: text/csv' --data-urlencode "query=${DBPEDIA_SPARQL}" https://dbpedia.org/sparql -o "${DBPEDIA_CSV}") 2>&1 | head -20 || true
head -20 "${DBPEDIA_CSV}" || true

echo

echo "## STAGE 0.5: RDF parsing with CLI tools"

echo

echo "### rapper parse DBPedia Classical_logic.ttl"
(time -p rapper -q -i turtle "${REPO_ROOT}/data/semantic-web-snapshots/dbpedia/Classical_logic.ttl" -o ntriples | head -5) 2>&1 || true

echo

echo "Done. Main log: ${LOG_MAIN}"
