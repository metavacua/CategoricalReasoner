SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: AGPL-3.0-or-later

# AGENTS.md - Benchmarks Directory

## Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE FORMALLY DEFINED IN:
- **[Formal Document Policy](../../docs/formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](../../docs/document-policy-implementation.html)** - Operational rules and validation

## Scope
This directory contains performance benchmarks and evaluation frameworks for the Categorical Reasoner.
All content is classified as **Software** under the formal model.

## Classification
- **Type:** Software (A_soft)
- **License:** AGPL v3.0 or later
- **Format:** Java source code, SPARQL queries, benchmark configurations
- **Copyrightable:** ⊤

## Content Types
- Performance measurement frameworks
- SPARQL query benchmarks (in queries/ subdirectory)
- Timing and profiling code
- Comparison benchmarks against other systems
- Evaluation criteria definitions

## Structural Requirements
- All Java source files must have SPDX license headers
- SPARQL query files should include license comments
- Results should be reproducible and deterministic
- Benchmarks must be automated where possible

## Constraints
- All files must include AGPL v3.0 license headers
- Benchmarks must produce deterministic results
- Query files should be documented with expected performance characteristics
- Results storage must comply with repository policy

## Validation
- Java compilation without errors
- License header completeness check
- Deterministic result verification
- Performance regression detection

## See Also
- [Formal Document Policy](../../docs/formal-document-policy.html) - Mathematical model
- [Implementation Guide](../../docs/document-policy-implementation.html) - Rules and procedures
- [Root AGENTS.md](../../AGENTS.md) - Repository-wide policies
- [Source AGENTS.md](../AGENTS.md) - Parent directory policy
