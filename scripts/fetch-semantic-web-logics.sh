#!/bin/bash
# fetch-semantic-web-logics.sh
# Fetch semantic web data for logics (LK, LJ, LDJ) with reproducibility metadata
# Part of Catty Thesis - Categorical Foundations for Logics

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="${SCRIPT_DIR}/../data/semantic-web-snapshots"
MANIFEST="${OUTPUT_DIR}/MANIFEST.yaml"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

# Create output directories
mkdir -p "${OUTPUT_DIR}/wikidata" "${OUTPUT_DIR}/dbpedia"

echo "============================================================"
echo "Semantic Web Data Fetch Pipeline for Catty Thesis"
echo "============================================================"
echo "Timestamp: ${TIMESTAMP}"
echo "Output directory: ${OUTPUT_DIR}"
echo ""

# Initialize manifest
echo "timestamp: \"${TIMESTAMP}\"" > "${MANIFEST}"
echo "pipeline_version: \"1.0.0\"" >> "${MANIFEST}"
echo "description: \"RDF data fetched from Wikidata and DBPedia for LK, LJ, LDJ logics\"" >> "${MANIFEST}"
echo "sources:" >> "${MANIFEST}"

# Fetch function with logging and manifest updates
fetch_and_log() {
    local tool=$1
    local url=$2
    local output_file=$3
    local source_name=$4
    local source_type=$5  # wikidata or dbpedia
    
    echo "------------------------------------------------------------"
    echo "Fetching: ${source_name}"
    echo "URL: ${url}"
    echo "Output: ${output_file}"
    echo "Tool: ${tool}"
    echo "------------------------------------------------------------"
    
    local log_file="${output_file}.log"
    local fetch_success=false
    
    if command -v ${tool} &> /dev/null; then
        if [ "${tool}" == "wget" ]; then
            if wget --timeout=15 -v "${url}" -O "${output_file}" 2>&1 | tee "${log_file}"; then
                fetch_success=true
            fi
        elif [ "${tool}" == "curl" ]; then
            if curl -v --connect-timeout 15 "${url}" > "${output_file}" 2>&1 | tee "${log_file}"; then
                fetch_success=true
            fi
        fi
        
        if [ "${fetch_success}" == "true" ] && [ -s "${output_file}" ]; then
            # Calculate hash and size
            local HASH=$(sha256sum "${output_file}" | awk '{print $1}')
            local SIZE=$(stat -c%s "${output_file}" 2>/dev/null || stat -f%z "${output_file}" 2>/dev/null)
            
            # Add to manifest
            echo "  - source: \"${source_name}\"" >> "${MANIFEST}"
            echo "    type: \"${source_type}\"" >> "${MANIFEST}"
            echo "    url: \"${url}\"" >> "${MANIFEST}"
            echo "    file: \"${output_file}\"" >> "${MANIFEST}"
            echo "    hash_sha256: \"${HASH}\"" >> "${MANIFEST}"
            echo "    size_bytes: ${SIZE}" >> "${MANIFEST}"
            echo "    fetched_at: \"${TIMESTAMP}\"" >> "${MANIFEST}"
            echo "    status: \"success\"" >> "${MANIFEST}"
            
            echo "✓ Successfully fetched ${source_name}"
            echo "  Size: ${SIZE} bytes"
            echo "  SHA256: ${HASH:0:16}..."
            return 0
        else
            echo "✗ Failed to fetch ${source_name}"
            echo "  - source: \"${source_name}\"" >> "${MANIFEST}"
            echo "    type: \"${source_type}\"" >> "${MANIFEST}"
            echo "    url: \"${url}\"" >> "${MANIFEST}"
            echo "    status: \"failed\"" >> "${MANIFEST}"
            echo "    error: \"Download failed or empty file\"" >> "${MANIFEST}"
            return 1
        fi
    else
        echo "✗ Tool ${tool} not available"
        echo "  - source: \"${source_name}\"" >> "${MANIFEST}"
        echo "    type: \"${source_type}\"" >> "${MANIFEST}"
        echo "    url: \"${url}\"" >> "${MANIFEST}"
        echo "    status: \"failed\"" >> "${MANIFEST}"
        echo "    error: \"Tool ${tool} not available\"" >> "${MANIFEST}"
        return 1
    fi
}

# Fetch Wikidata LK (Classical Logic) - Q236975
fetch_and_log "wget" \
    "https://www.wikidata.org/wiki/Special:EntityData/Q236975.json" \
    "${OUTPUT_DIR}/wikidata/LK_Q236975.json" \
    "Wikidata Classical Logic (LK)" \
    "wikidata"

# Fetch Wikidata LJ (Intuitionistic Logic) - Q176786
fetch_and_log "wget" \
    "https://www.wikidata.org/wiki/Special:EntityData/Q176786.json" \
    "${OUTPUT_DIR}/wikidata/LJ_Q176786.json" \
    "Wikidata Intuitionistic Logic (LJ)" \
    "wikidata"

# Fetch DBPedia Classical Logic
fetch_and_log "wget" \
    "https://dbpedia.org/data/Classical_logic.ttl" \
    "${OUTPUT_DIR}/dbpedia/Classical_logic.ttl" \
    "DBPedia Classical Logic" \
    "dbpedia"

# Try DBPedia Intuitionistic Logic (may not exist)
fetch_and_log "wget" \
    "https://dbpedia.org/data/Intuitionistic_logic.ttl" \
    "${OUTPUT_DIR}/dbpedia/Intuitionistic_logic.ttl" \
    "DBPedia Intuitionistic Logic" \
    "dbpedia" || echo "Note: DBPedia Intuitionistic Logic may not exist"

echo ""
echo "============================================================"
echo "Fetch Complete"
echo "============================================================"
echo ""
echo "Manifest saved to: ${MANIFEST}"
echo ""
echo "Summary:"
grep -c "status: \"success\"" "${MANIFEST}" > /dev/null && echo "  Successful fetches: $(grep 'status: "success"' "${MANIFEST}" | wc -l)" || echo "  Successful fetches: 0"
grep -c "status: \"failed\"" "${MANIFEST}" > /dev/null && echo "  Failed fetches: $(grep 'status: "failed"' "${MANIFEST}" | wc -l)" || echo "  Failed fetches: 0"
echo ""
echo "Files in ${OUTPUT_DIR}/wikidata:"
ls -lh "${OUTPUT_DIR}/wikidata"/*.json 2>/dev/null | awk '{print "  " $5 " " $9}' || echo "  (none)"
echo ""
echo "Files in ${OUTPUT_DIR}/dbpedia:"
ls -lh "${OUTPUT_DIR}/dbpedia"/*.ttl 2>/dev/null | awk '{print "  " $5 " " $9}' || echo "  (none)"
echo ""
echo "Full manifest:"
cat "${MANIFEST}"
