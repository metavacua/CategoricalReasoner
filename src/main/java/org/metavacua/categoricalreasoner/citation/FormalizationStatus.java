package org.metavacua.categoricalreasoner.citation;

/**
 * Formalization status tracking for mathematical sources.
 * Enables automated agents to query and validate citation formalization.
 */
public enum FormalizationStatus {
    /**
     * Not yet verified or reviewed
     */
    UNVERIFIED,

    /**
     * Axioms have been formalized in a theorem prover
     */
    AXIOMATIZED,

    /**
     * Theorems have been formally verified
     */
    PROVEN,

    /**
     * Dependencies have been discharged/verified
     */
    DISCHARGED,

    /**
     * Superseded by a more recent result
     */
    DEPRECATED,

    /**
     * Found to be incorrect
     */
    REFUTED
}
