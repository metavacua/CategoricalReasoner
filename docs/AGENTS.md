SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: CC-BY-SA-4.0

# AGENTS.md - Documentation Directory

## Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE FORMALLY DEFINED IN:
- **[Formal Document Policy](formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](document-policy-implementation.html)** - Operational rules and validation

## Core Principles (Derived from Formal Model)
1. All documentation in `docs/` is licensed under CC BY-SA v4.0
2. Documentation should be W3C standard compliant HTML5 with embedded semantic linked data
3. All content must be deployable to GitHub Pages as static web pages
4. Customized behaviors are negatives/anti-patterns; prioritize standard intersections over arbitrary expression
5. Canonicity is high value; speed is irrelevant
6. This is not agile development but spec-driven, doc-driven, test-driven formally verified development

## Documentation Standards
- W3C HTML5 specification compliance
- RDFa Lite or Microdata for semantic markup
- PROV-O provenance metadata for all agent-generated content
- RDF C14N for canonicalization where applicable
- Unicode normalization (NFC) for all text
- Valid semantic markup (W3C validator)

## Format Requirements
- HTML5 or XHTML for main documentation (not Markdown for final published content)
- Markdown allowed only for draft/exploration within development workflow
- Final documentation must be valid, well-formed HTML5
- Embedded semantic linked data (RDFa, JSON-LD, or Microdata)
- Proper DOCTYPE and character encoding declarations

## Canonicity and Normalization
Documentation should follow canonical forms where defined:
- RDF triples: Use RDFC 1.0 canonicalization
- IRIs: Lowercase scheme and host, percent-encode normalization
- Text: Unicode NFC normalization
- Dates: ISO 8601 format with timezone offset
- Citations: Follow applicable academic standards (OAIS, ISO 16363:2025)

## Agent-Specific Rules
- All agent-generated documentation must include PROV-O metadata
- LLM-generated content must be curated before commit
- No arbitrary LLM-generated files in repository root
- All agent outputs must be validated against formal model

## See Also
- [Formal Document Policy](formal-document-policy.html) - Mathematical model
- [Implementation Guide](document-policy-implementation.html) - Rules and procedures
- [Root AGENTS.md](../AGENTS.md) - Repository-wide policies
