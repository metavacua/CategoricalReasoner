/*
 * SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * PROV-O (W3C Provenance Ontology) implementation for tracking
 * agent activities and their evidence graphs.
 */
package catty.helloworld.prov;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * PROV-O Agent as defined by W3C Provenance Ontology.
 * Represents a person, group, or software entity that can act.
 */
public class PROVAgent {

    private static final String PROV_NS = "http://www.w3.org/ns/prov#";

    private final String id;
    private final String type;
    private final String seeAlso;

    public PROVAgent(final String id, final String type, final String seeAlso) {
        this.id = id;
        this.type = type;
        this.seeAlso = seeAlso;
    }

    public String toTurtle() {
        Model model = ModelFactory.createDefaultModel();

        Resource agent = model.createResource(PROV_NS + "Agent");
        Resource agentInstance = model.createResource(PROV_NS + id);

        Property typeProp = model.createProperty(PROV_NS, "type");
        Property labelProp = RDFS.label;
        Property seeAlsoProp = RDFS.seeAlso;

        agentInstance.addProperty(typeProp, agent);
        agentInstance.addProperty(typeProp, type);
        agentInstance.addProperty(labelProp, "Agent: " + id);
        if (seeAlso != null) {
            agentInstance.addProperty(seeAlsoProp, seeAlso);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        model.write(baos, "Turtle");
        return baos.toString();
    }

    public String getId() {
        return id;
    }
}
