package org.catty.sparql.federation;

import org.catty.sparql.federation.impl.JenaSelectQueryFederation;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SdlcSpecificationGenerator {
    public static void main(String[] args) throws Exception {
        JenaSelectQueryFederation federation = new JenaSelectQueryFederation();
        String wikidata = "https://query.wikidata.org/sparql";

        try (PrintWriter out = new PrintWriter(new FileWriter("generated/SDLC_SPECIFICATION.md"))) {
            out.println("# SDLC Specification");
            out.println("Generated from semantic query results.");
            out.println();

            generateSection(out, "SDLC Phases", wikidata, "sdlc-phases-selection.rq", federation);
            generateSection(out, "Testing Methodologies", wikidata, "testing-methodologies-selection.rq", federation);
            generateSection(out, "Documentation Standards", wikidata, "documentation-standards-selection.rq", federation);
            generateSection(out, "Resource Constraints", wikidata, "resource-constraints-selection.rq", federation);
        }
    }

    private static void generateSection(PrintWriter out, String title, String endpoint, String queryFile, JenaSelectQueryFederation federation) throws Exception {
        out.println("## " + title);
        String query = new String(Files.readAllBytes(Paths.get("benchmarks/queries", queryFile)));
        SelectQueryResult result = federation.executeSelectQuery(endpoint, query, 30);

        if (result.getRows() == null || result.getRows().isEmpty()) {
            out.println("No results retrieved or query failed.");
            if (result.getError() != null) {
                out.println("Error: " + result.getError().getMessage());
            }
        } else {
            out.println("| " + String.join(" | ", result.getVariables()) + " |");
            out.println("| " + result.getVariables().stream().map(v -> "---").reduce((a, b) -> a + " | " + b).get() + " |");
            for (Map<String, String> row : result.getRows()) {
                out.print("| ");
                for (String var : result.getVariables()) {
                    out.print(row.getOrDefault(var, "") + " | ");
                }
                out.println();
            }
        }
        out.println();
    }
}
