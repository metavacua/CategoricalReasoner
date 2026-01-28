#!/bin/bash
# fetch-semantic-web-logics.sh
# Reproducible snapshot fetcher for semantic web resources relevant to Catty logics.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
OUTPUT_DIR="${REPO_ROOT}/data/semantic-web-snapshots"
MANIFEST="${OUTPUT_DIR}/MANIFEST.yaml"
TIMESTAMP="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"

mkdir -p "${OUTPUT_DIR}/wikidata" "${OUTPUT_DIR}/dbpedia"

relpath() {
  local p="$1"
  if command -v realpath >/dev/null 2>&1; then
    realpath --relative-to="${REPO_ROOT}" "$p"
  else
    echo "$p" | sed "s|^${REPO_ROOT}/||"
  fi
}

sha256_of() {
  sha256sum "$1" | awk '{print $1}'
}

size_of() {
  stat -c%s "$1" 2>/dev/null || stat -f%z "$1" 2>/dev/null
}

tool_version_line() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "${cmd}: not-installed"
    return 0
  fi
  case "$cmd" in
    wget) wget --version 2>/dev/null | head -1 | sed 's/$//' ;;
    curl) curl --version 2>/dev/null | head -1 | sed 's/$//' ;;
    jq) jq --version 2>/dev/null | head -1 | sed 's/$//' ;;
    rapper) rapper --version 2>/dev/null | head -1 | sed 's/$//' ;;
    *) "$cmd" --version 2>/dev/null | head -1 | sed 's/$//' || echo "${cmd}: version-unknown" ;;
  esac
}

init_manifest() {
  cat > "${MANIFEST}" <<EOF
# Semantic Web Data Manifest for Catty Thesis
# Generated: ${TIMESTAMP}
# This manifest provides reproducibility metadata for fetched semantic web artifacts.

timestamp: "${TIMESTAMP}"
pipeline:
  name: "fetch-semantic-web-logics.sh"
  version: "1.1.0"
  description: "Fetch Wikidata/DBPedia snapshots for LK, LJ, LDJ-adjacent resources, plus axioms/rules referenced in Catty."

tools:
  wget: "$(tool_version_line wget | sed 's/"/\\"/g')"
  curl: "$(tool_version_line curl | sed 's/"/\\"/g')"
  jq: "$(tool_version_line jq | sed 's/"/\\"/g')"
  rapper: "$(tool_version_line rapper | sed 's/"/\\"/g')"

sources:
EOF
}

append_source() {
  local source_name="$1"
  local source_type="$2"
  local url="$3"
  local out_file_abs="$4"
  local status="$5"
  local note="${6:-}"

  local out_rel
  out_rel="$(relpath "${out_file_abs}")"

  # Convert sources: [] to proper YAML list by appending items in-place.
  # We keep this simple by appending at end and not trying to be a full YAML serializer.
  {
    echo
    echo "  - source: \"${source_name}\""
    echo "    type: \"${source_type}\""
    echo "    url: \"${url}\""
    echo "    file: \"${out_rel}\""
    echo "    fetched_at: \"${TIMESTAMP}\""
    echo "    status: \"${status}\""

    if [ "${status}" = "success" ] && [ -f "${out_file_abs}" ]; then
      echo "    size_bytes: $(size_of "${out_file_abs}")"
      echo "    hash_sha256: \"$(sha256_of "${out_file_abs}")\""
    fi

    if [ -n "${note}" ]; then
      local note_escaped
      note_escaped="${note//\"/\\\"}"
      echo "    note: \"${note_escaped}\""
    fi
  } >> "${MANIFEST}"
}

fetch_with_wget() {
  local url="$1"
  local out="$2"
  local log="$3"

  set +e
  wget --max-redirect=5 --timeout=15 -S -v "$url" -O "$out" 2>&1 | tee "$log"
  local rc=${PIPESTATUS[0]}
  set -e
  return $rc
}

fetch_with_curl() {
  local url="$1"
  local out="$2"
  local log="$3"

  set +e
  curl -L -v --connect-timeout 15 -o "$out" "$url" 2>&1 | tee "$log"
  local rc=${PIPESTATUS[0]}
  set -e
  return $rc
}

fetch_and_record() {
  local tool="$1"         # wget|curl
  local source_name="$2"
  local source_type="$3"  # wikidata|dbpedia|sparql
  local url="$4"
  local out_file_abs="$5"

  mkdir -p "$(dirname "$out_file_abs")"
  local log_file="${out_file_abs}.log"

  echo "============================================================"
  echo "Fetching: ${source_name}"
  echo "URL: ${url}"
  echo "Output: $(relpath "$out_file_abs")"
  echo "Tool: ${tool}"

  local rc=0
  if [ "$tool" = "wget" ]; then
    fetch_with_wget "$url" "$out_file_abs" "$log_file" || rc=$?
  else
    fetch_with_curl "$url" "$out_file_abs" "$log_file" || rc=$?
  fi

  if [ $rc -eq 0 ] && [ -s "$out_file_abs" ]; then
    append_source "$source_name" "$source_type" "$url" "$out_file_abs" "success"
  else
    append_source "$source_name" "$source_type" "$url" "$out_file_abs" "failed" "fetch rc=${rc} or empty file"
  fi

  echo
  sleep 1
}

fetch_wikidata_entity() {
  local name="$1"
  local qid="$2"

  # JSON (Wikidata entity export)
  fetch_and_record wget "Wikidata: ${name} (${qid})" "wikidata" \
    "https://www.wikidata.org/wiki/Special:EntityData/${qid}.json" \
    "${OUTPUT_DIR}/wikidata/${qid}_${name// /_}.json"

  # Turtle (RDF)
  fetch_and_record wget "Wikidata: ${name} (${qid}) [Turtle]" "wikidata" \
    "https://www.wikidata.org/wiki/Special:EntityData/${qid}.ttl" \
    "${OUTPUT_DIR}/wikidata/${qid}_${name// /_}.ttl"
}

fetch_dbpedia_ttl() {
  local name="$1"
  local resource="$2"  # DBPedia resource label with underscores

  fetch_and_record curl "DBPedia: ${name}" "dbpedia" \
    "https://dbpedia.org/data/${resource}.ttl" \
    "${OUTPUT_DIR}/dbpedia/${resource}.ttl"
}

init_manifest

# Core logic items (used as identifiers for Catty logics)
fetch_wikidata_entity "Classical logic" "Q236975"
fetch_wikidata_entity "Intuitionistic logic" "Q176786"

# LK/LJ-related proof systems / structural rules / substructural logics
fetch_wikidata_entity "Sequent calculus" "Q1771121"
fetch_wikidata_entity "Structural rule" "Q4548693"
fetch_wikidata_entity "Weakening (structural rule)" "Q19720140"
fetch_wikidata_entity "Cut rule" "Q18400501"
fetch_wikidata_entity "Linear logic" "Q841728"
fetch_wikidata_entity "Affine logic" "Q4688943"
fetch_wikidata_entity "Relevance logic" "Q176630"
fetch_wikidata_entity "Noncommutative logic" "Q7049221"
fetch_wikidata_entity "Non-monotonic logic" "Q2488768"
fetch_wikidata_entity "Monotonicity of entailment" "Q1945137"

# Classical principles / proof principles
fetch_wikidata_entity "Principle of excluded middle" "Q468422"
fetch_wikidata_entity "Law of noncontradiction" "Q868437"
fetch_wikidata_entity "Double negation elimination" "Q5300067"
fetch_wikidata_entity "Principle of explosion" "Q60190"
fetch_wikidata_entity "Proof by contradiction" "Q184899"
fetch_wikidata_entity "Reductio ad absurdum" "Q14402006"

# LDJ: no dedicated logic entity found; snapshot the main related article item
fetch_wikidata_entity "Dual intuitionistic logic (article)" "Q124829676"

# DBPedia TTL snapshots for key pages referenced by ontology docs
fetch_dbpedia_ttl "Classical logic" "Classical_logic"
fetch_dbpedia_ttl "Intuitionistic logic" "Intuitionistic_logic"
fetch_dbpedia_ttl "Sequent calculus" "Sequent_calculus" || true
fetch_dbpedia_ttl "Linear logic" "Linear_logic" || true
fetch_dbpedia_ttl "Relevance logic" "Relevance_logic" || true

cat >> "${MANIFEST}" <<'EOF'

notes:
  lk_lj_identifiers:
    classical_logic_qid: "Q236975"
    intuitionistic_logic_qid: "Q176786"
  ldj_status: |
    No dedicated Wikidata item for "dual intuitionistic logic" as a logic system was found via
    wbsearchentities at the time of this snapshot. A closely related item exists as a *scholarly
    article* (Q124829676) and is included as a reference snapshot.
  axioms_and_rules_note: |
    Several axioms/rules relevant to Catty's LK/LJ/LL picture exist as separate Wikidata entities
    (e.g. excluded middle, noncontradiction, cut rule, weakening). These are included to support
    downstream linking, but they are not (yet) structured as "sequent calculus rules" inside the
    logic entity pages.
EOF

echo "============================================================"
echo "Fetch complete. Manifest: $(relpath "${MANIFEST}")"
