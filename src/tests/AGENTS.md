SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: AGPL-3.0-or-later

# AGENTS.md - Tests Directory

## Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE FORMALLY DEFINED IN:
- **[Formal Document Policy](../../docs/formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](../../docs/document-policy-implementation.html)** - Operational rules and validation

## Scope
This directory contains unit tests, integration tests, and verification frameworks for the Categorical Reasoner.
All content is classified as **Software** under the formal model.

## Classification
- **Type:** Software (A_soft)
- **License:** AGPL v3.0 or later
- **Format:** Java unit tests, JUnit test classes, test fixtures
- **Copyrightable:** ⊤

## Content Types
- JUnit 5 test classes
- Integration test suites
- Property-based tests
- KeY verification specifications
- Test fixtures and mock data

## Structural Requirements
- All test files must include SPDX license headers
- Tests must follow JUnit 5 conventions and annotations
- Test class names must end with "Test"
- Test methods must be deterministic and independent
- Tests should use descriptive names following given-when-then or should-when patterns

## Constraints
- All files must include AGPL v3.0 license headers
- Tests must not depend on external network resources
- Tests must be deterministic (no random data without fixed seeds)
- Tests must clean up resources after execution
- Test coverage should be maintained at appropriate levels

## Validation
- Java compilation without errors
- All tests pass (JUnit)
- License header completeness check
- KeY verification attempted for applicable code
- Test coverage analysis

## See Also
- [Formal Document Policy](../../docs/formal-document-policy.html) - Mathematical model
- [Implementation Guide](../../docs/document-policy-implementation.html) - Rules and procedures
- [Root AGENTS.md](../../AGENTS.md) - Repository-wide policies
- [Source AGENTS.md](../AGENTS.md) - Parent directory policy
