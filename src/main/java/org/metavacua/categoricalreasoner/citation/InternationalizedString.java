package org.metavacua.categoricalreasoner.citation;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Internationalized string with BCP-47 language tag.
 * Used for titles and descriptions in multiple languages.
 *
 * @param text        The string content
 * @param languageTag Optional BCP-47 language tag (e.g., "en", "fr", "zh-CN")
 */
public record InternationalizedString(
    String text,
    Optional<String> languageTag
) {

    /**
     * Compact constructor validating content.
     *
     * @throws IllegalArgumentException if text is null or blank
     * @throws IllegalArgumentException if languageTag is invalid BCP-47 format
     */
    public InternationalizedString {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or blank");
        }

        // Normalize whitespace
        text = text.trim();

        // Validate and normalize language tag
        languageTag = Optional.ofNullable(languageTag)
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .map(tag -> {
                try {
                    Locale.forLanguageTag(tag);
                    return tag;
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Invalid BCP-47 language tag: " + tag, e
                    );
                }
            });
    }

    /**
     * Creates an internationalized string without language tag.
     *
     * @param text The text content
     * @return A new InternationalizedString instance
     */
    public static InternationalizedString of(String text) {
        return new InternationalizedString(text, Optional.empty());
    }

    /**
     * Creates an internationalized string with language tag.
     *
     * @param text        The text content
     * @param languageTag The BCP-47 language tag
     * @return A new InternationalizedString instance
     */
    public static InternationalizedString of(String text, String languageTag) {
        return new InternationalizedString(text, Optional.ofNullable(languageTag));
    }

    /**
     * Returns the Locale for this string, if language tag is present.
     *
     * @return Optional Locale
     */
    public Optional<Locale> locale() {
        return languageTag.map(Locale::forLanguageTag);
    }

    @Override
    public String toString() {
        return text;
    }
}
