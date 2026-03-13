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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import proof.licensing.LicenseClassification;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for proof-theoretic metadata.
 *
 * <p>These tests verify that metadata correctly captures the proof
 * normalization results and generates appropriate JSON output for
 * SBOM generation and provenance tracking.</p>
 */
@DisplayName("Proof Metadata Tests")
class ProofMetadataTest {

    @Test
    @DisplayName("Create metadata for closed AGPL classification")
    void testCreateClosedAGPLMetadata() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.CLOSED_AGPL,
                "main",
                "abc123"
        );

        assertEquals(LicenseClassification.CLOSED_AGPL, metadata.classification());
        assertEquals("main", metadata.gitBranch());
        assertEquals("abc123", metadata.gitCommit());
        assertEquals("maven-compile", metadata.normalizationStrategy());
        assertEquals("java-25", metadata.typeSystem());
        assertTrue(metadata.isSuccessful());
    }

    @Test
    @DisplayName("Create metadata for open CC-BY-SA classification")
    void testCreateOpenCCBYSAMetadata() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.OPEN_CCBYSA,
                "feature/docs",
                "def456"
        );

        assertEquals(LicenseClassification.OPEN_CCBYSA, metadata.classification());
        assertTrue(metadata.isSuccessful());
        assertEquals("CC-BY-SA-4.0", metadata.getSpdxLicenseId());
        assertEquals("OPEN", metadata.getProofStatus());
    }

    @Test
    @DisplayName("Create metadata for invalid classification")
    void testCreateInvalidMetadata() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.INVALID,
                "bugfix",
                "ghi789"
        );

        assertEquals(LicenseClassification.INVALID, metadata.classification());
        assertFalse(metadata.isSuccessful());
        assertEquals("NONE", metadata.getSpdxLicenseId());
        assertEquals("INVALID", metadata.getProofStatus());
    }

    @Test
    @DisplayName("Get SPDX license ID for closed proof")
    void testGetSpdxLicenseIdClosed() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.CLOSED_AGPL,
                "main",
                "abc123"
        );

        assertEquals("AGPL-3.0-or-later", metadata.getSpdxLicenseId());
    }

    @Test
    @DisplayName("Get proof status for open branch")
    void testGetProofStatusOpen() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.OPEN_CCBYSA,
                "main",
                "abc123"
        );

        assertEquals("OPEN", metadata.getProofStatus());
    }

    @Test
    @DisplayName("Generate valid JSON output")
    void testToJson() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.CLOSED_AGPL,
                "main",
                "abc123"
        );

        String json = metadata.toJson();

        assertNotNull(json);
        assertTrue(json.contains("\"classification\": \"CLOSED_AGPL\""));
        assertTrue(json.contains("\"license\": \"AGPL-3.0-or-later\""));
        assertTrue(json.contains("\"proof-status\": \"CLOSED\""));
        assertTrue(json.contains("\"normalization-strategy\": \"maven-compile\""));
        assertTrue(json.contains("\"type-system\": \"java-25\""));
        assertTrue(json.contains("\"git-branch\": \"main\""));
        assertTrue(json.contains("\"git-commit\": \"abc123\""));
    }

    @Test
    @DisplayName("ToString contains classification and license")
    void testToString() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.CLOSED_AGPL,
                "main",
                "abc123"
        );

        String str = metadata.toString();

        assertTrue(str.contains("CLOSED_AGPL"));
        assertTrue(str.contains("AGPL-3.0-or-later"));
        assertTrue(str.contains("CLOSED"));
    }

    @Test
    @DisplayName("Metadata with timestamp")
    void testMetadataTimestamp() {
        ProofMetadata metadata = ProofMetadata.create(
                LicenseClassification.CLOSED_AGPL,
                "main",
                "abc123"
        );

        assertNotNull(metadata.timestamp());
    }
}
