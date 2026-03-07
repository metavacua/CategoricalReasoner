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
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PROV-O Entity as defined by W3C Provenance Ontology.
 * Represents a physical or digital thing that can be identified.
 */
public class PROVEntity {

    private static final String PROV_NS = "http://www.w3.org/ns/prov#";

    private final String id;
    private final String type;
    private final String seeAlso;
    private final List<PROVActivity> wasGeneratedBy = new ArrayList<>();
    private final List<PROVAgent> wasAttributedTo = new ArrayList<>();

    public PROVEntity(final String id, final String type, final String seeAlso) {
        this.id = id;
        this.type = type;
        this.seeAlso = seeAlso;
    }

    public void wasGeneratedBy(final PROVActivity activity) {
        this.wasGeneratedBy.add(activity);
    }

    public void wasAttributedTo(final PROVAgent agent) {
        this.wasAttributedTo.add(agent);
    }

    public String toTurtle() {
        Model model = ModelFactory.createDefaultModel();

        Resource entity = model.createResource(PROV_NS + "Entity");
        Resource entityInstance = model.createResource(PROV_NS + id);

        Property typeProp = model.createProperty(PROV_NS, "type");
        Property labelProp = RDFS.label;
        Property seeAlsoProp = RDFS.seeAlso;
        Property wasGeneratedBy = model.createProperty(PROV_NS, "wasGeneratedBy");
        Property wasAttributedTo = model.createProperty(PROV_NS, "wasAttributedTo");

        entityInstance.addProperty(typeProp, entity);
        entityInstance.addProperty(typeProp, type);
        entityInstance.addProperty(labelProp, "Entity: " + id);
        if (seeAlso != null) {
            entityInstance.addProperty(seeAlsoProp, seeAlso);
        }

        for (PROVActivity activity : wasGeneratedBy) {
            entityInstance.addProperty(wasGeneratedBy,
                model.createResource(PROV_NS + activity.getId()));
        }

        for (PROVAgent agent : wasAttributedTo) {
            entityInstance.addProperty(wasAttributedTo,
                model.createResource(PROV_NS + agent.getId()));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        model.write(baos, "Turtle");
        return baos.toString();
    }

    public String getId() {
        return id;
    }
}
