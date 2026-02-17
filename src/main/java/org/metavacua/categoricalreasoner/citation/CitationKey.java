package org.metavacua.categoricalreasoner.citation;

import java.util.Objects;

/**
 * Value object for citation keys with validation.
 * Format: [familyName][year][disambiguator] - lowercase, hyphenated
 * <p>
 * Examples:
 * <ul>
 *   <li>girard1987linear</li>
 *   <li>lawvere1963functor</li>
 *   <li>mac lane1971a</li>
 * </ul>
 *
 * @param value The citation key string
 */
public record CitationKey(String value) {

    /**
     * Compact constructor validating citation key format.
     *
     * @throws IllegalArgumentException if value is null, blank, or invalid format
     */
    public CitationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Citation key cannot be null or blank");
        }

        // Validate format: lowercase, alphanumeric, hyphens allowed
        if (!value.matches("^[a-z0-9-]+$")) {
            throw new IllegalArgumentException(
                "Citation key must be lowercase alphanumeric with hyphens only: " + value
            );
        }

        // Validate starts with letter
        if (!Character.isLetter(value.charAt(0))) {
            throw new IllegalArgumentException(
                "Citation key must start with a letter: " + value
            );
        }

        // Normalize
        value = value.trim().toLowerCase();
    }

    /**
     * Creates a citation key from a string.
     *
     * @param value The key string
     * @return A new CitationKey instance
     */
    public static CitationKey of(String value) {
        return new CitationKey(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CitationKey that = (CitationKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
