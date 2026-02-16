package org.metavacua.categoricalreasoner.citation;

import java.util.Objects;
import java.util.Optional;

/**
 * Value object for Digital Object Identifier (DOI).
 * Validates DOI format according to DOI Handbook.
 *
 * @param value The DOI string (e.g., "10.1000/xyz123")
 */
public record Doi(String value) {

    private static final String DOI_PATTERN = "^10\\.\\d{4,9}/[-._;()/:A-Z0-9]+$";

    /**
     * Compact constructor validating DOI format.
     *
     * @throws IllegalArgumentException if value is null or invalid DOI format
     */
    public Doi {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DOI cannot be null or blank");
        }

        // Normalize to lowercase for prefix, but keep case for suffix
        value = value.trim().toLowerCase();

        // Validate DOI format (case-insensitive)
        String upperValue = value.toUpperCase();
        if (!upperValue.matches(DOI_PATTERN)) {
            throw new IllegalArgumentException(
                "Invalid DOI format. Expected format: 10.xxxx/... : " + value
            );
        }
    }

    /**
     * Creates a DOI from a string.
     *
     * @param value The DOI string
     * @return A new Doi instance
     */
    public static Doi of(String value) {
        return new Doi(value);
    }

    /**
     * Creates an optional DOI from a string.
     *
     * @param value The DOI string (may be null)
     * @return Optional Doi
     */
    public static Optional<Doi> ofNullable(String value) {
        return Optional.ofNullable(value).filter(s -> !s.isBlank()).map(Doi::new);
    }

    /**
     * Returns the DOI URL (https://doi.org/...).
     *
     * @return The DOI URL
     */
    public String url() {
        return "https://doi.org/" + value;
    }

    @Override
    public String toString() {
        return value;
    }
}
