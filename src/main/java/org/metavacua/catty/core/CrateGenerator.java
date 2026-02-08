package org.metavacua.catty.core;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class CrateGenerator {
    
    public void generateROCrate(List<Map<String, String>> discoveries, String outputPath) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"@context\": \"https://w3id.org/ro/crate/1.2/context\",\n");
        json.append("  \"@graph\": [\n");
        
        json.append("    {\n");
        json.append("      \"@id\": \"ro-crate-metadata.json\",\n");
        json.append("      \"@type\": \"CreativeWork\",\n");
        json.append("      \"conformsTo\": {\"@id\": \"https://w3id.org/ro/crate/1.2\"},\n");
        json.append("      \"about\": {\"@id\": \"./\"},\n");
        json.append("      \"datePublished\": \"").append(Instant.now().toString()).append("\"\n");
        json.append("    },\n");
        
        json.append("    {\n");
        json.append("      \"@id\": \"./\",\n");
        json.append("      \"@type\": \"Dataset\",\n");
        json.append("      \"name\": \"Catty Thesis Semantic Web Discovery\",\n");
        json.append("      \"description\": \"Research Object Crate containing semantic web discovery results\",\n");
        json.append("      \"datePublished\": \"").append(Instant.now().toString()).append("\",\n");
        json.append("      \"license\": {\"@id\": \"https://creativecommons.org/licenses/by/4.0/\"},\n");
        json.append("      \"hasPart\": [\n");
        
        for (int i = 0; i < discoveries.size(); i++) {
            json.append("        {\"@id\": \"#discovery-").append(i).append("\"}");
            if (i < discoveries.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("      ]\n");
        json.append("    }");
        
        for (int i = 0; i < discoveries.size(); i++) {
            json.append(",\n");
            Map<String, String> discovery = discoveries.get(i);
            json.append("    {\n");
            json.append("      \"@id\": \"#discovery-").append(i).append("\",\n");
            json.append("      \"@type\": \"CreativeWork\",\n");
            
            if (discovery.containsKey("itemLabel")) {
                json.append("      \"name\": ").append(escapeJson(discovery.get("itemLabel"))).append(",\n");
            }
            if (discovery.containsKey("description")) {
                json.append("      \"description\": ").append(escapeJson(discovery.get("description"))).append(",\n");
            }
            if (discovery.containsKey("item")) {
                json.append("      \"url\": ").append(escapeJson(discovery.get("item"))).append(",\n");
            }
            
            json.append("      \"dateDiscovered\": \"").append(Instant.now().toString()).append("\"\n");
            json.append("    }");
        }
        
        json.append("\n  ]\n");
        json.append("}\n");
        
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(json.toString());
            System.out.println("RO-Crate metadata written to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Failed to write RO-Crate: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String escapeJson(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }
}
