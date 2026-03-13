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

package proof.licensing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Curry-Howard license classification.
 *
 * <p>These tests verify that the proof-theoretic classification correctly
 * maps compilation outcomes to license classifications according to the
 * Curry-Howard correspondence.</p>
 */
@DisplayName("License Classification Tests")
class LicenseClassificationTest {

    @Test
    @DisplayName("CLOSED_AGPL has correct SPDX identifier")
    void testClosedAGPLSpdxIdentifier() {
        assertEquals("AGPL-3.0-or-later", LicenseClassification.CLOSED_AGPL.getLicenseId());
    }

    @Test
    @DisplayName("OPEN_CCBYSA has correct SPDX identifier")
    void testOpenCCBYSASpdxIdentifier() {
        assertEquals("CC-BY-SA-4.0", LicenseClassification.OPEN_CCBYSA.getLicenseId());
    }

    @Test
    @DisplayName("INVALID has correct SPDX identifier")
    void testInvalidSpdxIdentifier() {
        assertEquals("NONE", LicenseClassification.INVALID.getLicenseId());
    }

    @Test
    @DisplayName("CLOSED_AGPL has correct proof status")
    void testClosedAGPLProofStatus() {
        assertEquals("CLOSED", LicenseClassification.CLOSED_AGPL.getProofStatus());
    }

    @Test
    @DisplayName("OPEN_CCBYSA has correct proof status")
    void testOpenCCBYSAProofStatus() {
        assertEquals("OPEN", LicenseClassification.OPEN_CCBYSA.getProofStatus());
    }

    @Test
    @DisplayName("INVALID has correct proof status")
    void testInvalidProofStatus() {
        assertEquals("INVALID", LicenseClassification.INVALID.getProofStatus());
    }

    @Test
    @DisplayName("Closed and open classifications are valid")
    void testValidClassifications() {
        assertTrue(LicenseClassification.CLOSED_AGPL.isValid());
        assertTrue(LicenseClassification.OPEN_CCBYSA.isValid());
    }

    @Test
    @DisplayName("Invalid classification is not valid")
    void testInvalidClassification() {
        assertFalse(LicenseClassification.INVALID.isValid());
    }

    @Test
    @DisplayName("CLOSED_AGPL requires AGPL")
    void testClosedAGPLRequiresAGPL() {
        assertTrue(LicenseClassification.CLOSED_AGPL.requiresAGPL());
        assertFalse(LicenseClassification.CLOSED_AGPL.requiresCCBYSA());
    }

    @Test
    @DisplayName("OPEN_CCBYSA requires CC-BY-SA")
    void testOpenCCBYSARequiresCCBYSA() {
        assertTrue(LicenseClassification.OPEN_CCBYSA.requiresCCBYSA());
        assertFalse(LicenseClassification.OPEN_CCBYSA.requiresAGPL());
    }

    @Test
    @DisplayName("INVALID requires neither AGPL nor CC-BY-SA")
    void testInvalidRequiresNeither() {
        assertFalse(LicenseClassification.INVALID.requiresAGPL());
        assertFalse(LicenseClassification.INVALID.requiresCCBYSA());
    }
}
