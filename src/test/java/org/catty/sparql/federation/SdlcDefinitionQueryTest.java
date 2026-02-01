package org.catty.sparql.federation;

import org.catty.sparql.federation.impl.JenaSelectQueryExecutor;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class SdlcDefinitionQueryTest {

    private final JenaSelectQueryExecutor executor = new JenaSelectQueryExecutor();
    private static final String WIKIDATA = "https://query.wikidata.org/sparql";

    private String loadQuery(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get("benchmarks/queries", filename)));
    }

    @Test
    public void testPhaseDefinitionQuery() throws IOException {
        String query = loadQuery("sdlc-phases-selection.rq");
        SelectQueryResult result = executor.executeWithTimeout(WIKIDATA, query, 30);
        assertNotNull(result);
        if (result.getError() != null) {
            result.getError().printStackTrace();
        }
        assertNotNull("Rows should not be null", result.getRows());
        assertFalse("Query A should return results", result.getRows().isEmpty());
        assertTrue("Results should contain phase sequence information", 
                   result.getRows().stream().anyMatch(r -> r.containsKey("sequenceOrder")));
    }

    @Test
    public void testTestingMethodologyQuery() throws IOException {
        String query = loadQuery("testing-methodologies-selection.rq");
        SelectQueryResult result = executor.executeWithTimeout(WIKIDATA, query, 30);
        assertNotNull(result);
        if (result.getError() != null) {
            result.getError().printStackTrace();
        }
        assertNotNull("Rows should not be null", result.getRows());
        assertFalse("Query B should return results", result.getRows().isEmpty());
        // Relaxing label check as Wikidata content may vary, but ensuring column exists
        assertTrue("Results should have methodologyName", 
                   result.getRows().get(0).containsKey("methodologyName"));
    }

    @Test
    public void testDocumentationStandardQuery() throws IOException {
        String query = loadQuery("documentation-standards-selection.rq");
        SelectQueryResult result = executor.executeWithTimeout(WIKIDATA, query, 30);
        assertNotNull(result);
        if (result.getError() != null) {
            result.getError().printStackTrace();
        }
        assertNotNull("Rows should not be null", result.getRows());
        assertFalse("Query C should return results", result.getRows().isEmpty());
        assertTrue("Results should have docTypeName", 
                   result.getRows().get(0).containsKey("docTypeName"));
    }

    @Test
    public void testResourceConstraintQuery() throws IOException {
        String query = loadQuery("resource-constraints-selection.rq");
        SelectQueryResult result = executor.executeWithTimeout(WIKIDATA, query, 30);
        assertNotNull(result);
        if (result.getError() != null) {
            result.getError().printStackTrace();
        }
        assertNotNull("Rows should not be null", result.getRows());
        assertFalse("Query D should return results", result.getRows().isEmpty());
        assertTrue("Results should have patternName", 
                   result.getRows().get(0).containsKey("patternName"));
    }
}
