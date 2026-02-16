package org.metavacua.categoricalreasoner.citation;

import java.util.Objects;
import java.util.Optional;

/**
 * LLM-facing structured notes for citation context.
 * Enables automated agents to understand the relevance and usage
 * of citations in the categorical reasoning context.
 *
 * @param relevance    Brief description of why this source is relevant
 * @param category     Categorical category (e.g., "linear-logic", "topos-theory", "adjunctions")
 * @param notes        Additional agent-facing notes
 * @param lastUpdated  Last update timestamp (ISO8601)
 */
public record AgentContext(
    String relevance,
    Optional<String> category,
    Optional<String> notes,
    Optional<String> lastUpdated
) {

    /**
     * Compact constructor validating content.
     *
     * @throws IllegalArgumentException if relevance is null or blank
     */
    public AgentContext {
        if (relevance == null || relevance.isBlank()) {
            throw new IllegalArgumentException("Relevance cannot be null or blank");
        }

        // Normalize whitespace
        relevance = relevance.trim();

        // Wrap nulls in Optional and trim
        category = Optional.ofNullable(category).map(String::trim).filter(s -> !s.isBlank());
        notes = Optional.ofNullable(notes).map(String::trim).filter(s -> !s.isBlank());
        lastUpdated = Optional.ofNullable(lastUpdated).map(String::trim).filter(s -> !s.isBlank());
    }

    /**
     * Creates an agent context with just relevance.
     *
     * @param relevance The relevance description
     * @return A new AgentContext instance
     */
    public static AgentContext of(String relevance) {
        return new AgentContext(relevance, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates an agent context with relevance and category.
     *
     * @param relevance The relevance description
     * @param category  The categorical category
     * @return A new AgentContext instance
     */
    public static AgentContext of(String relevance, String category) {
        return new AgentContext(relevance, Optional.ofNullable(category), Optional.empty(), Optional.empty());
    }

    /**
     * Creates an agent context with full details.
     *
     * @param relevance   The relevance description
     * @param category    The categorical category
     * @param notes       Additional notes
     * @param lastUpdated Last update timestamp
     * @return A new AgentContext instance
     */
    public static AgentContext of(String relevance, String category, String notes, String lastUpdated) {
        return new AgentContext(
            relevance,
            Optional.ofNullable(category),
            Optional.ofNullable(notes),
            Optional.ofNullable(lastUpdated)
        );
    }

    @Override
    public String toString() {
        return relevance;
    }
}
