package org.metavacua.categoricalreasoner.citation;

import java.util.Objects;
import java.util.Optional;

/**
 * Structured person record for citation authors.
 * All fields are immutable; defensive copies in constructors.
 * <p>
 * Based on schema.org Person specification.
 *
 * @param familyName Required family name
 * @param givenName  Optional given name(s)
 * @param particle   Optional particle (von, van, de, etc.)
 * @param suffix     Optional suffix (Jr., Sr., III, etc.)
 * @see <a href="https://schema.org/Person">schema.org Person specification</a>
 */
public record Person(
    String familyName,
    Optional<String> givenName,
    Optional<String> particle,
    Optional<String> suffix
) {

    /**
     * Compact constructor enforcing invariants.
     *
     * @throws IllegalArgumentException if familyName is null or blank
     */
    public Person {
        if (familyName == null || familyName.isBlank()) {
            throw new IllegalArgumentException("Family name is required");
        }

        // Normalize whitespace
        familyName = familyName.trim();

        // Wrap nulls in Optional and trim
        givenName = Optional.ofNullable(givenName).map(String::trim).filter(s -> !s.isBlank());
        particle = Optional.ofNullable(particle).map(String::trim).filter(s -> !s.isBlank());
        suffix = Optional.ofNullable(suffix).map(String::trim).filter(s -> !s.isBlank());
    }

    /**
     * Creates a person with only family name.
     *
     * @param familyName The family name
     * @return A new Person instance
     */
    public static Person of(String familyName) {
        return new Person(familyName, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a person with family and given names.
     *
     * @param familyName The family name
     * @param givenName  The given name
     * @return A new Person instance
     */
    public static Person of(String familyName, String givenName) {
        return new Person(familyName, Optional.ofNullable(givenName), Optional.empty(), Optional.empty());
    }

    /**
     * Returns the full name formatted for display.
     *
     * @return Formatted name string
     */
    public String fullName() {
        StringBuilder sb = new StringBuilder();

        givenName.ifPresent(gn -> {
            sb.append(gn);
            sb.append(" ");
        });

        particle.ifPresent(sb::append);
        sb.append(familyName);
        suffix.ifPresent(s -> {
            sb.append(", ");
            sb.append(s);
        });

        return sb.toString();
    }

    /**
     * Returns the name formatted as "FamilyName, GivenName" for sorting.
     *
     * @return Formatted name string
     */
    public String sortName() {
        StringBuilder sb = new StringBuilder(familyName);
        givenName.ifPresent(gn -> {
            sb.append(", ");
            sb.append(gn);
        });
        return sb.toString();
    }
}
