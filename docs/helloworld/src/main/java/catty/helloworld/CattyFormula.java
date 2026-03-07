/*
 * SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Catty Logical Formula Annotation - demonstrates JSR 269 Annotation Processing
 * for transforming semantic HTML documentation into Java records.
 */
package catty.helloworld;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as representing a logical formula in the Catty thesis.
 *
 * This annotation demonstrates the JSR 269 Annotation Processing pattern:
 * - Semantic HTML serves as the source of truth
 * - JDK compiler automates construction of records
 * - Proof terms are embedded during compilation phase
 *
 * Per AGENTS.md:
 * "By treating Semantic HTML as the source of truth, the JDK can automate
 * the construction of records where the proof terms are embedded during
 * the compilation phase."
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface CattyFormula {

    /**
     * The logical formula identifier.
     */
    String id();

    /**
     * Human-readable name of the formula.
     */
    String name();

    /**
     * Description of the formula's role in the sequent calculus.
     */
    String description();

    /**
     * Category of structural rule (e.g., "weakening", "contraction", "cut").
     */
    String category() default "generic";
}
