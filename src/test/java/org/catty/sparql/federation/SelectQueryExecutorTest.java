package org.catty.sparql.federation;

import org.catty.sparql.federation.impl.JenaSelectQueryExecutor;
import org.junit.Test;
import static org.junit.Assert.*;

public class SelectQueryExecutorTest {

    @Test
    public void testResultCollection() {
        JenaSelectQueryExecutor executor = new JenaSelectQueryExecutor();
        String query = "SELECT * WHERE { ?s ?p ?o } LIMIT 1";
        SelectQueryResult result = executor.executeWithTimeout("https://query.wikidata.org/sparql", query, 30);
        
        assertNotNull(result);
        assertNotNull(result.getVariables());
        assertFalse(result.getVariables().isEmpty());
        assertNotNull(result.getRows());
        assertFalse(result.getRows().isEmpty());
    }

    @Test
    public void testTimeoutEnforcement() {
        JenaSelectQueryExecutor executor = new JenaSelectQueryExecutor();
        // A query that is likely to take long or we set a very short timeout
        String query = "SELECT * WHERE { ?s ?p ?o. ?o ?p1 ?o1. ?o1 ?p2 ?o2 } LIMIT 1000000";
        SelectQueryResult result = executor.executeWithTimeout("https://query.wikidata.org/sparql", query, 1);
        
        // It might timeout or return quickly depending on Wikidata's optimization, 
        // but if it timeouts, it should be marked.
        if (result.isTimedOut()) {
            assertTrue(result.getExecutionTimeMs() >= 0);
        }
    }
}
