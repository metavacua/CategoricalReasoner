# AGENTS.md - Development Reports

## Scope
The `docs/dev/report/` directory contains derived validation and verification reports. These are operational artifacts documenting the development process, not primary thesis or monograph content.

## Content Type
These are **derived artifacts** - outputs from SPARQL queries, Wikidata verification, and semantic audits. They serve as evidence of validation but are not part of the logical argument of the thesis or monograph.

## File Inventory
- `qid-verification-report.tex` - Wikidata QID verification results
- `categorical-semantic-audit.tex` - Semantic web data audit
- `semantic-web-rag.tex` - RAG extraction from semantic web sources
- `sparql-validation-protocol.tex` - SPARQL validation protocols
- `curry-howard-extracted.tex` - Curry-Howard correspondence data
- `logic-lattice-extracted.tex` - Logic lattice hierarchy data

## Core Constraints
- **Format**: `.tex` files that may be converted to `.md` as workflow matures
- **SPARQL Evidence**: All reported query results must have actual SPARQL execution evidence in TTL format
- **No Fabrication**: Reports must not contain fabricated or assumed data

## Relationship to Thesis
Reports in this directory are NOT part of the thesis. They document the validation process that ensures thesis content is grounded in actual semantic web data.

## Validation
- Reports must compile without LaTeX errors
- SPARQL queries documented in reports must have been actually executed
- Evidence must be valid TTL

## See Also
- `docs/dev/AGENTS.md` - Parent directory
- `docs/dissertation/AGENTS.md` - Thesis policies
- `docs/structural-rules/AGENTS.md` - Monograph policies
