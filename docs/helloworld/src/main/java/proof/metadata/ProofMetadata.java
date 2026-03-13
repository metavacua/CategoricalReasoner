// SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
// SPDX-License-Identifier: AGPL-3.0-or-later
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

package proof.metadata;

import proof.licensing.LicenseClassification;

import java.time.Instant;
import java.util.Objects;

/**
 * Proof-theoretic metadata for Curry-Howard license classification.
 *
 * <p>This record captures the metadata generated during the proof normalization
 * process (Maven compilation), linking the compilation status to license
 * classification via the Curry-Howard correspondence.</p>
 *
 * <p>The metadata is used for:</p>
 * <ul>
 *   <li>SBOM generation via spdx-maven-plugin</li>
 *   <li>Pre-commit hook classification</li>
 *   <li>Provenance tracking (PROV-O)</li>
 * </ul>
 */
public record ProofMetadata(
        /**
         * The proof-theoretic classification of the build.
         */
        LicenseClassification classification,

        /**
         * The timestamp when the proof normalization was completed.
         */
        Instant timestamp,

        /**
         * The Git branch on which the build was performed.
         */
        String gitBranch,

        /**
         * The Git commit hash for which the metadata was generated.
         */
        String gitCommit,

        /**
         * The normalization strategy used (typically "maven-compile").
         */
        String normalizationStrategy,

        /**
         * The type system version (typically "java-25").
         */
        String typeSystem,

        /**
         * Additional metadata in JSON format.
         */
        String additionalMetadata
) {
    /**
     * Creates a ProofMetadata instance with the given classification.
     *
     * @param classification the license classification
     * @param gitBranch the Git branch
     * @param gitCommit the Git commit hash
     * @return a new ProofMetadata instance
     */
    public static ProofMetadata create(LicenseClassification classification,
                                        String gitBranch,
                                        String gitCommit) {
        return new ProofMetadata(
                classification,
                Instant.now(),
                gitBranch,
                gitCommit,
                "maven-compile",
                "java-25",
                null
        );
    }

    /**
     * Returns whether the proof normalization was successful.
     *
     * @return true if the classification is valid (not INVALID)
     */
    public boolean isSuccessful() {
        return classification.isValid();
    }

    /**
     * Returns the SPDX license identifier.
     *
     * @return the SPDX license identifier (e.g., "AGPL-3.0-or-later")
     */
    public String getSpdxLicenseId() {
        return classification.getLicenseId();
    }

    /**
     * Returns the proof-theoretic status.
     *
     * @return "CLOSED", "OPEN", or "INVALID"
     */
    public String getProofStatus() {
        return classification.getProofStatus();
    }

    /**
     * Generates JSON representation of the metadata.
     *
     * @return JSON string
     */
    public String toJson() {
        return String.format("""
                {
                  "proof-theoretic": {
                    "classification": "%s",
                    "license": "%s",
                    "proof-status": "%s",
                    "timestamp": "%s",
                    "git-branch": "%s",
                    "git-commit": "%s"
                  },
                  "curry-howard": {
                    "correspondence": "proof-term",
                    "normalization-strategy": "%s",
                    "type-system": "%s"
                  }%s
                }
                """,
                classification.name(),
                classification.getLicenseId(),
                classification.getProofStatus(),
                timestamp.toString(),
                gitBranch,
                gitCommit,
                normalizationStrategy,
                typeSystem,
                additionalMetadata != null ? ",\n  \"additional\": " + additionalMetadata : ""
        );
    }

    /**
     * Parses ProofMetadata from JSON.
     *
     * @param json the JSON string
     * @return the parsed ProofMetadata
     */
    public static ProofMetadata fromJson(String json) {
        throw new UnsupportedOperationException("JSON parsing not yet implemented");
    }

    @Override
    public String toString() {
        return String.format("ProofMetadata{classification=%s, license=%s, status=%s, timestamp=%s}",
                classification, classification.getLicenseId(), classification.getProofStatus(), timestamp);
    }
}
