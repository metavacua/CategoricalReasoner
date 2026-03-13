<!-- SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean -->
<!-- SPDX-License-Identifier: CC-BY-SA-4.0 -->

# License Header Templates

This directory contains license header templates used by the license-maven-plugin to apply proof-theoretic license classification.

## Templates

- **AGPL-3.0-or-later.txt**: Template for Java source files (closed proof terms)
  - Applied during `compile` phase after successful compilation
  - Contains proof-theoretic classification metadata

- **CC-BY-SA-4.0.txt**: Template for documentation files (open branches)
  - Applied during `process-sources` phase
  - Contains proof-theoretic classification metadata

## Usage

The license-maven-plugin is configured in `pom.xml` to:

1. Apply AGPL-3.0-or-later headers to Java files after successful compilation
2. Apply CC-BY-SA-4.0 headers to documentation files
3. Verify that headers are correctly applied

## Curry-Howard Correspondence

These templates implement the proof-theoretic license classification:

- **AGPL**: Applied to files that successfully compile (closed proof)
- **CC-BY-SA**: Applied to documentation that doesn't require compilation (open branch)

See `PROOF-THEORETIC-LICENSE.md` for formal details.
