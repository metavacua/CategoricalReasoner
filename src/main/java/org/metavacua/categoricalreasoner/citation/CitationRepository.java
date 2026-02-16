package org.metavacua.categoricalreasoner.citation;

import java.util.List;
import java.util.Optional;

/**
 * Repository for citation records.
 * Generates RO-Crate metadata during compilation.
 * <p>
 * This is the central registry for all citations in the categorical reasoning system.
 * Citations are stored as immutable records and accessed via static methods.
 */
public final class CitationRepository {

    private static final List<Citation> CITATIONS = List.of(
        // Foundational works in Category Theory
        Citation.of(
            CitationKey.of("lawvere1963functor"),
            List.of(Person.of("Lawvere", "F. William")),
            InternationalizedString.of(
                "Functorial Semantics of Algebraic Theories",
                "en"
            ),
            PublicationDate.of(1963),
            WorkType.THESIS
        ),

        Citation.of(
            CitationKey.of("mac lane1971categories"),
            List.of(Person.of("Mac Lane", "Saunders")),
            InternationalizedString.of(
                "Categories for the Working Mathematician",
                "en"
            ),
            PublicationDate.of(1971),
            WorkType.BOOK,
            Doi.of("10.1007/978-1-4757-4721-8"),
            Qid.of("Q2344868"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Standard reference for category theory",
                "category-theory",
                "Foundational text for categorical constructions",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        ),

        // Linear Logic
        Citation.of(
            CitationKey.of("girard1987linear"),
            List.of(Person.of("Girard", "Jean-Yves")),
            InternationalizedString.of(
                "Linear Logic",
                "en"
            ),
            PublicationDate.of(1987),
            WorkType.ARTICLE,
            Doi.of("10.1016/0304-3975(87)90045-4"),
            Qid.of("Q2696528"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Introduces linear logic, a resource-aware logic",
                "linear-logic",
                "Key paper for substructural logics",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        ),

        // Topos Theory
        Citation.of(
            CitationKey.of("grothendieck1972topos"),
            List.of(Person.of("Grothendieck", "Alexander")),
            InternationalizedString.of(
                "Categories fibrées et descente",
                "fr"
            ),
            PublicationDate.of(1972),
            WorkType.REPORT,
            null,
            Qid.of("Q2852462"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Foundational work on topos theory",
                "topos-theory",
                "Grothendieck topos theory and descent",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        ),

        // Categorical Logic
        Citation.of(
            CitationKey.of("joyal1987geometric"),
            List.of(
                Person.of("Joyal", "André"),
                Person.of("Tierney", "Myles")
            ),
            InternationalizedString.of(
                "An Extension of the Galois Theory of Grothendieck",
                "en"
            ),
            PublicationDate.of(1984),
            WorkType.ARTICLE,
            Doi.of("10.2307/1994232"),
            Qid.of("Q17082150"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Geometric logic and topos theory",
                "topos-theory",
                "Connections between logic and geometry",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        ),

        // Adjunctions
        Citation.of(
            CitationKey.of("kan1958adjoint"),
            List.of(Person.of("Kan", "Daniel M.")),
            InternationalizedString.of(
                "Adjoint Functors",
                "en"
            ),
            PublicationDate.of(1958),
            WorkType.ARTICLE,
            null,
            Qid.of("Q105758729"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Introduction of adjoint functors",
                "category-theory",
                "Fundamental concept in category theory",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        ),

        // Monads
        Citation.of(
            CitationKey.of("mac lane1965monad"),
            List.of(Person.of("Mac Lane", "Saunders")),
            InternationalizedString.of(
                "Homology",
                "en"
            ),
            PublicationDate.of(1963),
            WorkType.BOOK,
            Doi.of("10.1007/978-3-662-12920-1"),
            Qid.of("Q21040470"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Introduces monads (triples)",
                "category-theory",
                "Monads and categorical algebra",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        ),

        // Recent categorical logic work
        Citation.of(
            CitationKey.of("johnstone2002sketches"),
            List.of(Person.of("Johnstone", "Peter T.")),
            InternationalizedString.of(
                "Sketches of an Elephant: A Topos Theory Compendium",
                "en"
            ),
            PublicationDate.of(2002),
            WorkType.BOOK,
            Doi.of("10.1017/CBO9781107340993"),
            Qid.of("Q11773897"),
            null,
            FormalizationStatus.AXIOMATIZED,
            AgentContext.of(
                "Comprehensive topos theory reference",
                "topos-theory",
                "Two-volume work on topos theory",
                "2024-02-01T00:00:00Z"
            ),
            List.of()
        )
    );

    private CitationRepository() {
        // Utility class - prevent instantiation
    }

    /**
     * Returns all citations in the repository.
     *
     * @return Unmodifiable list of all citations
     */
    public static List<Citation> getAll() {
        return List.copyOf(CITATIONS);
    }

    /**
     * Finds a citation by its key.
     *
     * @param key The citation key to search for
     * @return Optional containing the citation if found
     */
    public static Optional<Citation> findByKey(CitationKey key) {
        return CITATIONS.stream()
            .filter(c -> c.key().equals(key))
            .findFirst();
    }

    /**
     * Finds citations by primary author family name.
     *
     * @param familyName The author's family name
     * @return List of citations by that author
     */
    public static List<Citation> findByAuthor(String familyName) {
        return CITATIONS.stream()
            .filter(c -> c.primaryAuthor().familyName().equalsIgnoreCase(familyName))
            .toList();
    }

    /**
     * Finds citations by work type.
     *
     * @param type The work type
     * @return List of citations of that type
     */
    public static List<Citation> findByType(WorkType type) {
        return CITATIONS.stream()
            .filter(c -> c.type() == type)
            .toList();
    }

    /**
     * Finds citations by formalization status.
     *
     * @param status The formalization status
     * @return List of citations with that status
     */
    public static List<Citation> findByStatus(FormalizationStatus status) {
        return CITATIONS.stream()
            .filter(c -> c.status() == status)
            .toList();
    }

    /**
     * Finds citations by agent context category.
     *
     * @param category The categorical category
     * @return List of citations in that category
     */
    public static List<Citation> findByCategory(String category) {
        return CITATIONS.stream()
            .filter(c -> c.agentContext().isPresent())
            .filter(c -> c.agentContext().get().category().isPresent())
            .filter(c -> c.agentContext().get().category().get().equalsIgnoreCase(category))
            .toList();
    }

    /**
     * Returns the total number of citations.
     *
     * @return Number of citations
     */
    public static int count() {
        return CITATIONS.size();
    }
}
