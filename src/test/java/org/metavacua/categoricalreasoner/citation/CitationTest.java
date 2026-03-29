package org.metavacua.categoricalreasoner.citation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for citation system components.
 */
class CitationTest {

    @Test
    void testCitationKeyValidation() {
        assertDoesNotThrow(() -> CitationKey.of("girard1987linear"));
        assertDoesNotThrow(() -> CitationKey.of("mac-lane1971categories"));
        assertDoesNotThrow(() -> CitationKey.of("lawvere1963"));

        assertThrows(IllegalArgumentException.class, () -> CitationKey.of(null));
        assertThrows(IllegalArgumentException.class, () -> CitationKey.of(""));
        assertThrows(IllegalArgumentException.class, () -> CitationKey.of("Girard1987")); // Uppercase not allowed
        assertThrows(IllegalArgumentException.class, () -> CitationKey.of("123test")); // Must start with letter
    }

    @Test
    void testPersonCreation() {
        Person person1 = Person.of("Girard", "Jean-Yves");
        assertEquals("Girard", person1.familyName());
        assertTrue(person1.givenName().isPresent());
        assertEquals("Jean-Yves", person1.givenName().get());

        Person person2 = Person.of("Mac Lane");
        assertEquals("Mac Lane", person2.familyName());
        assertFalse(person2.givenName().isPresent());

        assertThrows(IllegalArgumentException.class, () -> Person.of(null));
        assertThrows(IllegalArgumentException.class, () -> Person.of(""));
    }

    @Test
    void testInternationalizedString() {
        InternationalizedString en = InternationalizedString.of("Linear Logic", "en");
        assertEquals("Linear Logic", en.text());
        assertTrue(en.languageTag().isPresent());
        assertEquals("en", en.languageTag().get());

        InternationalizedString noLang = InternationalizedString.of("Categories for the Working Mathematician");
        assertEquals("Categories for the Working Mathematician", noLang.text());
        assertFalse(noLang.languageTag().isPresent());

        assertThrows(IllegalArgumentException.class, () -> InternationalizedString.of(null));
        assertThrows(IllegalArgumentException.class, () -> InternationalizedString.of("", "en"));
    }

    @Test
    void testPublicationDate() {
        PublicationDate yearOnly = PublicationDate.of(1987);
        assertEquals(1987, yearOnly.year());
        assertFalse(yearOnly.month().isPresent());
        assertFalse(yearOnly.day().isPresent());
        assertEquals("1987", yearOnly.toIsoString());

        PublicationDate yearMonth = PublicationDate.of(1987, 3);
        assertEquals(1987, yearMonth.year());
        assertEquals(3, yearMonth.month().get());
        assertEquals("1987-03", yearMonth.toIsoString());

        PublicationDate fullDate = PublicationDate.of(1987, 3, 15);
        assertEquals(1987, fullDate.year());
        assertEquals(3, fullDate.month().get());
        assertEquals(15, fullDate.day().get());
        assertEquals("1987-03-15", fullDate.toIsoString());

        PublicationDate parsed = PublicationDate.parse("1987-03-15");
        assertEquals(fullDate.year(), parsed.year());
        assertEquals(fullDate.month(), parsed.month());
        assertEquals(fullDate.day(), parsed.day());

        assertThrows(IllegalArgumentException.class, () -> PublicationDate.of(-1));
        assertThrows(IllegalArgumentException.class, () -> PublicationDate.of(1987, 13)); // Invalid month
        assertThrows(IllegalArgumentException.class, () -> PublicationDate.of(1987, 2, 30)); // Invalid day
    }

    @Test
    void testDoiValidation() {
        assertDoesNotThrow(() -> Doi.of("10.1000/xyz123"));
        assertDoesNotThrow(() -> Doi.of("10.1016/0304-3975(87)90045-4"));

        assertThrows(IllegalArgumentException.class, () -> Doi.of(null));
        assertThrows(IllegalArgumentException.class, () -> Doi.of(""));
        assertThrows(IllegalArgumentException.class, () -> Doi.of("not-a-doi"));

        Doi doi = Doi.of("10.1000/xyz123");
        assertEquals("https://doi.org/10.1000/xyz123", doi.url());
    }

    @Test
    void testQidValidation() {
        assertDoesNotThrow(() -> Qid.of("Q123456"));
        assertDoesNotThrow(() -> Qid.of("Q2696528"));

        assertThrows(IllegalArgumentException.class, () -> Qid.of(null));
        assertThrows(IllegalArgumentException.class, () -> Qid.of(""));
        assertThrows(IllegalArgumentException.class, () -> Qid.of("123456")); // Missing Q prefix

        Qid qid = Qid.of("Q123456");
        assertEquals("Q123456", qid.value());
        assertEquals("123456", qid.numericId());
        assertEquals("https://www.wikidata.org/entity/Q123456", qid.url());
    }

    @Test
    void testArxivIdValidation() {
        assertDoesNotThrow(() -> ArxivId.of("2001.12345"));
        assertDoesNotThrow(() -> ArxivId.of("math.CT/0605035"));

        assertThrows(IllegalArgumentException.class, () -> ArxivId.of(null));
        assertThrows(IllegalArgumentException.class, () -> ArxivId.of(""));
        assertThrows(IllegalArgumentException.class, () -> ArxivId.of("not-arxiv"));

        ArxivId newFormat = ArxivId.of("2001.12345");
        assertFalse(newFormat.isOldFormat());
        assertEquals("https://arxiv.org/abs/2001.12345", newFormat.url());
        assertEquals("https://arxiv.org/pdf/2001.12345.pdf", newFormat.pdfUrl());

        ArxivId oldFormat = ArxivId.of("math.CT/0605035");
        assertTrue(oldFormat.isOldFormat());
    }

    @Test
    void testAgentContext() {
        AgentContext minimal = AgentContext.of("Important paper on linear logic");
        assertEquals("Important paper on linear logic", minimal.relevance());
        assertFalse(minimal.category().isPresent());

        AgentContext full = AgentContext.of(
            "Important paper",
            "linear-logic",
            "Key result",
            "2024-02-01T00:00:00Z"
        );
        assertEquals("Important paper", full.relevance());
        assertTrue(full.category().isPresent());
        assertEquals("linear-logic", full.category().get());

        assertThrows(IllegalArgumentException.class, () -> AgentContext.of(null));
    }

    @Test
    void testCitationCreation() {
        Citation citation = Citation.of(
            CitationKey.of("girard1987linear"),
            List.of(Person.of("Girard", "Jean-Yves")),
            InternationalizedString.of("Linear Logic"),
            PublicationDate.of(1987),
            WorkType.ARTICLE
        );

        assertEquals("girard1987linear", citation.key().value());
        assertEquals(1, citation.authors().size());
        assertEquals("Girard", citation.primaryAuthor().familyName());
        assertEquals("Linear Logic", citation.title().text());
        assertEquals(1987, citation.date().year());
        assertEquals(WorkType.ARTICLE, citation.type());
        assertEquals(FormalizationStatus.UNVERIFIED, citation.status());

        assertThrows(IllegalArgumentException.class, () ->
            Citation.of(
                null,
                List.of(Person.of("Girard")),
                InternationalizedString.of("Linear Logic"),
                PublicationDate.of(1987),
                WorkType.ARTICLE
            )
        );

        assertThrows(IllegalArgumentException.class, () ->
            Citation.of(
                CitationKey.of("girard1987linear"),
                List.of(),
                InternationalizedString.of("Linear Logic"),
                PublicationDate.of(1987),
                WorkType.ARTICLE
            )
        );
    }

    @Test
    void testCitationRepository() {
        // Test that repository is populated
        assertTrue(CitationRepository.count() > 0);

        // Test finding by key
        var macLane = CitationRepository.findByKey(CitationKey.of("mac lane1971categories"));
        assertTrue(macLane.isPresent());
        assertEquals("Categories for the Working Mathematician", macLane.get().title().text());

        // Test finding by author
        var girardWorks = CitationRepository.findByAuthor("Girard");
        assertFalse(girardWorks.isEmpty());

        // Test finding by type
        var books = CitationRepository.findByType(WorkType.BOOK);
        assertFalse(books.isEmpty());

        // Test finding by status
        var axiomatized = CitationRepository.findByStatus(FormalizationStatus.AXIOMATIZED);
        assertFalse(axiomatized.isEmpty());

        // Test finding by category
        var linearLogic = CitationRepository.findByCategory("linear-logic");
        assertFalse(linearLogic.isEmpty());
    }
}
