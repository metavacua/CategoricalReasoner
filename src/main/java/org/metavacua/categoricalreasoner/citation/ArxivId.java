package org.metavacua.categoricalreasoner.citation;

import java.util.Objects;
import java.util.Optional;

/**
 * Value object for arXiv identifier.
 * Validates both old and new arXiv ID formats.
 * <p>
 * Old format: archive-class/YYYYMMNNN (e.g., "math.CT/0605035")
 * New format: YYYYMM.NNNNN (e.g., "2001.12345")
 *
 * @param value The arXiv ID string
 */
public record ArxivId(String value) {

    private static final String OLD_ARXIV_PATTERN = "^[a-z-]+\\.[A-Z]{2}/\\d{7}$";
    private static final String NEW_ARXIV_PATTERN = "^\\d{4}\\.\\d{4,5}$";

    /**
     * Compact constructor validating arXiv ID format.
     *
     * @throws IllegalArgumentException if value is null or invalid arXiv ID format
     */
    public ArxivId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("arXiv ID cannot be null or blank");
        }

        // Trim and preserve case
        String trimmed = value.trim();

        // Validate format (case-sensitive for archive class)
        if (!trimmed.matches(OLD_ARXIV_PATTERN) && !trimmed.matches(NEW_ARXIV_PATTERN)) {
            throw new IllegalArgumentException(
                "Invalid arXiv ID format. Expected formats: " +
                "math.CT/0605035 (old) or 2001.12345 (new): " + trimmed
            );
        }

        value = trimmed;
    }

    /**
     * Creates an arXiv ID from a string.
     *
     * @param value The arXiv ID string
     * @return A new ArxivId instance
     */
    public static ArxivId of(String value) {
        return new ArxivId(value);
    }

    /**
     * Creates an optional arXiv ID from a string.
     *
     * @param value The arXiv ID string (may be null)
     * @return Optional ArxivId
     */
    public static Optional<ArxivId> ofNullable(String value) {
        return Optional.ofNullable(value).filter(s -> !s.isBlank()).map(ArxivId::new);
    }

    /**
     * Returns the arXiv abstract URL.
     *
     * @return The arXiv URL
     */
    public String url() {
        return "https://arxiv.org/abs/" + value;
    }

    /**
     * Returns the arXiv PDF URL.
     *
     * @return The arXiv PDF URL
     */
    public String pdfUrl() {
        return "https://arxiv.org/pdf/" + value + ".pdf";
    }

    /**
     * Checks if this is an old-format arXiv ID (with archive class).
     *
     * @return true if old format, false if new format
     */
    public boolean isOldFormat() {
        return value.contains("/");
    }

    @Override
    public String toString() {
        return value;
    }
}
