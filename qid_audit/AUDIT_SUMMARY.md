# Wikidata QID Audit Summary

## Overview
A comprehensive batch audit of all Wikidata QID references (`wd:Q[0-9]+`) in the Catty repository was conducted. 
Total Unique QIDs Discovered: 10
Total Mismatches Found: 10 (100% failure rate)

## Key Findings
Every single QID audited in the repository points to a Wikidata entity that contradicts its documented usage in the code.

1. **Systematic Misalignment**: There is no partial correctness. The QIDs appear to be completely arbitrary or sourced from a different dataset, yet are prefixed with `wd:` (Wikidata) and documented as if they were correct.
   
2. **Specific Examples**:
   - `Q16917`: Used as **Category (mathematics)**, but is actually **Hospital**.
   - `Q846544`: Used as **Adjoint functors**, but is actually **disaster film**.
   - `Q1149560`: Used as **Linear logic**, but is actually **right fielder** (baseball).
   - `Q211231`: Used as **Intuitionistic logic**, but is actually a **football tournament season**.

3. **Contextual Contradiction**: Code comments explicitly identify these IDs as specific mathematical concepts (e.g., `# Wikidata: Classical logic`), but the IDs resolve to completely unrelated entities (e.g., humans, films, awards).

## Recommendations
- **Immediate Remediation**: All QIDs in the repository must be treated as invalid.
- **Root Cause Analysis**: Investigate the source of these QIDs. They do not appear to be simple typos (e.g., off by one). They might be from a test Wikibase or generated via a faulty script.
- **Replacement**: A full sweep is required to lookup the correct Wikidata QIDs for the intended concepts (e.g., lookup "Category theory" -> `Q219388`, "Functor" -> `Q864483`, etc.) and replace the invalid ones.

## Audit Artifacts
- `qid_audit/discovered_qids.json`: Detailed locations of all QID usages.
- `qid_audit/wikidata_results.json`: Raw metadata retrieved from Wikidata.
- `qid_audit/mismatch_report.json`: Semantic analysis of the discrepancies.
