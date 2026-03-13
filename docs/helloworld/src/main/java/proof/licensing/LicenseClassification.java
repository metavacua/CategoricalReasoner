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

/**
 * Proof-theoretic license classification based on Curry-Howard correspondence.
 *
 * <p>This enum represents the license classification determined by proof
 * normalization status in the Curry-Howard isomorphism:</p>
 *
 * <ul>
 *   <li>{@link #CLOSED_AGPL} - Successful compilation → closed proof → AGPL v3</li>
 *   <li>{@link #OPEN_CCBYSA} - Documentation only → open branch → CC BY-SA 4.0</li>
 *   <li>{@link #INVALID} - Compilation failure → ill-typed → GIGO (reject)</li>
 * </ul>
 *
 * <p>The classification is determined by the Maven compilation phase,
 * which serves as the proof normalization procedure.</p>
 */
public enum LicenseClassification {
    /**
     * Closed branch with successful proof normalization.
     *
     * <p>Corresponds to a closed proof term in Curry-Howard:
     * <code>Γ ⊢ Java : Type ∧ compile(Java) = 0 → Γ ⊢ AGPL(Java)</code></p>
     *
     * <p>Applied to Java files that successfully compile.</p>
     */
    CLOSED_AGPL("AGPL-3.0-or-later", "CLOSED"),

    /**
     * Open branch with no compilation required.
     *
     * <p>Corresponds to an open proof (incomplete) in Curry-Howard:
     * <code>Γ ⊢ Doc : Documentation ∧ ¬compile(Doc) → Γ ⊢ CC-BY-SA(Doc)</code></p>
     *
     * <p>Applied to documentation files (markdown, YAML, TOML).</p>
     */
    OPEN_CCBYSA("CC-BY-SA-4.0", "OPEN"),

    /**
     * Invalid proof - ill-typed term.
     *
     * <p>Corresponds to a contradiction in the proof system:
     * <code>Γ ⊢ Java : ¬Type ∧ compile(Java) ≠ 0 → ⊥</code></p>
     *
     * <p>Represents code that fails to compile and should be rejected.</p>
     */
    INVALID("NONE", "INVALID");

    private final String licenseId;
    private final String proofStatus;

    LicenseClassification(String licenseId, String proofStatus) {
        this.licenseId = licenseId;
        this.proofStatus = proofStatus;
    }

    /**
     * Returns the SPDX license identifier for this classification.
     *
     * @return SPDX license identifier (e.g., "AGPL-3.0-or-later")
     */
    public String getLicenseId() {
        return licenseId;
    }

    /**
     * Returns the proof-theoretic status.
     *
     * @return "CLOSED", "OPEN", or "INVALID"
     */
    public String getProofStatus() {
        return proofStatus;
    }

    /**
     * Determines if this classification represents a valid proof.
     *
     * @return true if the classification is CLOSED_AGPL or OPEN_CCBYSA
     */
    public boolean isValid() {
        return this != INVALID;
    }

    /**
     * Determines if this classification requires AGPL licensing.
     *
     * @return true if the classification is CLOSED_AGPL
     */
    public boolean requiresAGPL() {
        return this == CLOSED_AGPL;
    }

    /**
     * Determines if this classification requires CC-BY-SA licensing.
     *
     * @return true if the classification is OPEN_CCBYSA
     */
    public boolean requiresCCBYSA() {
        return this == OPEN_CCBYSA;
    }
}
