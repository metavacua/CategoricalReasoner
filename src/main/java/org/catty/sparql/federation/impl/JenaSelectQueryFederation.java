package org.catty.sparql.federation.impl;

import org.catty.sparql.federation.SelectQueryFederation;
import org.catty.sparql.federation.SelectQueryResult;
import org.catty.sparql.federation.discovery.WikidataEndpointDiscovery;

import java.util.List;

public class JenaSelectQueryFederation implements SelectQueryFederation {

    private final JenaSelectQueryExecutor executor;
    private final WikidataEndpointDiscovery discovery;

    public JenaSelectQueryFederation() {
        this.executor = new JenaSelectQueryExecutor();
        this.discovery = new WikidataEndpointDiscovery();
    }

    @Override
    public SelectQueryResult executeSelectQuery(String endpoint, String selectQuery, int timeoutSeconds) {
        return executor.executeWithTimeout(endpoint, selectQuery, timeoutSeconds);
    }

    @Override
    public List<String> discoverEndpoints() {
        return discovery.discover();
    }
}
