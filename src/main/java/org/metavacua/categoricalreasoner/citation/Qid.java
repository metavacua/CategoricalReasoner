package org.metavacua.categoricalreasoner.citation;

import java.util.Objects;
import java.util.Optional;

/**
 * Value object for Wikidata QID (Wikidata identifier).
 * Validates QID format (Q followed by digits).
 *
 * @param value The QID string (e.g., "Q123456")
 */
public record Qid(String value) {

    private static final String QID_PATTERN = "^Q\\d+$";

    /**
     * Compact constructor validating QID format.
     *
     * @throws IllegalArgumentException if value is null or invalid QID format
     */
    public Qid {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("QID cannot be null or blank");
        }

        // Normalize to uppercase
        value = value.trim().toUpperCase();

        // Validate QID format
        if (!value.matches(QID_PATTERN)) {
            throw new IllegalArgumentException(
                "Invalid QID format. Expected format: Q followed by digits (e.g., Q123456): " + value
            );
        }
    }

    /**
     * Creates a QID from a string.
     *
     * @param value The QID string
     * @return A new Qid instance
     */
    public static Qid of(String value) {
        return new Qid(value);
    }

    /**
     * Creates an optional QID from a string.
     *
     * @param value The QID string (may be null)
     * @return Optional Qid
     */
    public static Optional<Qid> ofNullable(String value) {
        return Optional.ofNullable(value).filter(s -> !s.isBlank()).map(Qid::new);
    }

    /**
     * Returns the numeric portion of the QID.
     *
     * @return The numeric ID as a string
     */
    public String numericId() {
        return value.substring(1); // Remove leading 'Q'
    }

    /**
     * Returns the Wikidata entity URL.
     *
     * @return The Wikidata URL
     */
    public String url() {
        return "https://www.wikidata.org/entity/" + value;
    }

    @Override
    public String toString() {
        return value;
    }
}
