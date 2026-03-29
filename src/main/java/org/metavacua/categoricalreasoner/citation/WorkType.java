package org.metavacua.categoricalreasoner.citation;

/**
 * Publication type enumeration for citations.
 * Maps to BibLaTeX entry types and schema.org work types.
 */
public enum WorkType {
    /**
     * Journal article
     */
    ARTICLE,

    /**
     * Book
     */
    BOOK,

    /**
     * Conference or workshop paper
     */
    CONFERENCE,

    /**
     * Article in a collection
     */
    INCOLLECTION,

    /**
     * Thesis or dissertation
     */
    THESIS,

    /**
     * Technical report
     */
    REPORT,

    /**
     * Preprint
     */
    PREPRINT
}
