# Wikidata QID Audit Summary

## Overview
A comprehensive batch audit of all Wikidata QID references (`wd:Q[0-9]+`) in the Catty repository was conducted. 
- **Total Unique QIDs Discovered**: 10
- **Total Mismatches Found**: 10 (100% failure rate)
- **Status**: FIXED

## Key Findings & Resolution
Every single QID audited in the repository pointed to a Wikidata entity that contradicted its documented usage (e.g., "Category Theory" -> "Hospital").

**Root Cause**: The QIDs were likely "hallucinated" by LLM coding agents which guessed IDs instead of looking them up.

**Remediation Applied**: 
All incorrect QIDs have been replaced with manually verified authoritative QIDs from Wikidata.

| Concept | Old (Bad) QID | New (Verified) QID |
|:--- |:--- |:--- |
| Classical logic | `Q192960` (Baibars) | `Q236975` |
| Intuitionistic logic | `Q211231` (Football) | `Q176786` |
| Linear logic | `Q1149560` (Right fielder) | `Q841728` |
| Category (math) | `Q16917` (Hospital) | `Q719395` |
| Functor | `Q1370384` (Musician) | `Q864475` |
| Natural trans. | `Q568825` (Crypto) | `Q1442189` |
| Adjoint functors | `Q846544` (Disaster film) | `Q357858` |
| Logic | `Q8462` (Timur) | `Q8078` |

## Future Prevention
A new guide has been created to prevent recurrence:
**`schema/WIKIDATA_DISCOVERY.md`**

This guide provides:
1. A **Core Domain Registry** of verified QIDs for Category Theory and Logic.
2. **Python/SPARQL patterns** for agents to dynamically discover QIDs instead of guessing.

## Audit Artifacts
- `qid_audit/discovered_qids.json`: Original locations of bad QIDs.
- `qid_audit/wikidata_results.json`: Evidence of the mismatches.
- `qid_audit/mismatch_report.json`: Detailed semantic analysis.
- `qid_audit/corrected_qids.json`: Mapping of applied fixes.
