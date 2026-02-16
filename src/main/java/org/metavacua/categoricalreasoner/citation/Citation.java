package org.metavacua.categoricalreasoner.citation;

import java.util.List;
import java.util.Optional;

/**
 * Canonical citation record with total structure validation.
 * Javadoc annotations required for RO-Crate generation.
 * <p>
 * This record represents a formal citation in the categorical reasoning system.
 * It supports integration with external knowledge bases (Wikidata, Crossref, arXiv)
 * and tracks formalization status for theorem proving.
 *
 * @param key          Citation key format: [familyName][year][disambiguator]
 * @param authors      At least one author; structured names
 * @param title        Internationalized title with optional BCP-47 language tag
 * @param date         EDTF/ISO8601-2 parsed date
 * @param type         Publication type enum
 * @param doi          Optional validated DOI (10.xxxx/... format)
 * @param wikidata     Optional Wikidata QID (Q123456 format)
 * @param arxiv        Optional arXiv ID (2001.12345 format)
 * @param status       Formalization status tracking
 * @param agentContext LLM-facing structured notes
 * @param dependsOn    Graph dependencies for ARATU traversal
 */
public record Citation(
    CitationKey key,
    List<Person> authors,
    InternationalizedString title,
    PublicationDate date,
    WorkType type,
    Optional<Doi> doi,
    Optional<Qid> wikidata,
    Optional<ArxivId> arxiv,
    FormalizationStatus status,
    Optional<AgentContext> agentContext,
    List<CitationKey> dependsOn
) {

    /**
     * Compact constructor enforcing invariants.
     *
     * @throws IllegalArgumentException if authors list is empty
     * @throws IllegalArgumentException if any required field is null
     */
    public Citation {
        if (key == null) {
            throw new IllegalArgumentException("Citation key cannot be null");
        }

        if (authors == null || authors.isEmpty()) {
            throw new IllegalArgumentException("Citation must have at least one author");
        }

        // Create defensive copy of authors
        authors = List.copyOf(authors);

        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("Work type cannot be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("Formalization status cannot be null");
        }

        // Wrap nulls in Optional
        doi = Optional.ofNullable(doi);
        wikidata = Optional.ofNullable(wikidata);
        arxiv = Optional.ofNullable(arxiv);
        agentContext = Optional.ofNullable(agentContext);

        // Create defensive copy of dependencies
        dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
    }

    /**
     * Creates a minimal citation with required fields only.
     *
     * @param key     The citation key
     * @param authors List of authors
     * @param title   The title
     * @param date    The publication date
     * @param type    The work type
     * @return A new Citation instance
     */
    public static Citation of(
        CitationKey key,
        List<Person> authors,
        InternationalizedString title,
        PublicationDate date,
        WorkType type
    ) {
        return new Citation(
            key,
            authors,
            title,
            date,
            type,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            FormalizationStatus.UNVERIFIED,
            Optional.empty(),
            List.of()
        );
    }

    /**
     * Creates a citation with full details.
     *
     * @param key          The citation key
     * @param authors      List of authors
     * @param title        The title
     * @param date         The publication date
     * @param type         The work type
     * @param doi          Optional DOI
     * @param wikidata     Optional Wikidata QID
     * @param arxiv        Optional arXiv ID
     * @param status       Formalization status
     * @param agentContext Optional agent context
     * @param dependsOn    List of dependency keys
     * @return A new Citation instance
     */
    public static Citation of(
        CitationKey key,
        List<Person> authors,
        InternationalizedString title,
        PublicationDate date,
        WorkType type,
        Doi doi,
        Qid wikidata,
        ArxivId arxiv,
        FormalizationStatus status,
        AgentContext agentContext,
        List<CitationKey> dependsOn
    ) {
        return new Citation(
            key,
            authors,
            title,
            date,
            type,
            Optional.ofNullable(doi),
            Optional.ofNullable(wikidata),
            Optional.ofNullable(arxiv),
            status,
            Optional.ofNullable(agentContext),
            dependsOn
        );
    }

    /**
     * Returns the primary author (first in list).
     *
     * @return The primary author
     */
    public Person primaryAuthor() {
        return authors.get(0);
    }

    /**
     * Returns the short citation format: "PrimaryAuthor (Year)".
     *
     * @return Short citation string
     */
    public String shortCitation() {
        return primaryAuthor().familyName() + " (" + date.year() + ")";
    }

    /**
     * Checks if this citation has external identifiers.
     *
     * @return true if any external identifier is present
     */
    public boolean hasExternalIdentifiers() {
        return doi.isPresent() || wikidata.isPresent() || arxiv.isPresent();
    }

    /**
     * Returns all available URLs for this citation.
     *
     * @return List of URL strings
     */
    public List<String> urls() {
        return java.util.stream.Stream.of(
                doi.map(Doi::url),
                wikidata.map(Qid::url),
                arxiv.map(ArxivId::url)
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
}
