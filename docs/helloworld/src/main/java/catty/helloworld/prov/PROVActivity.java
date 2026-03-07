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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PROV-O Activity as defined by W3C Provenance Ontology.
 * Represents something that occurs over a period of time and acts upon entities.
 */
public class PROVActivity {

    private static final String PROV_NS = "http://www.w3.org/ns/prov#";

    private final String id;
    private final String type;
    private final Instant startTime;
    private Instant endTime;
    private final List<PROVAgent> wasAssociatedWith = new ArrayList<>();
    private final List<PROVEntity> used = new ArrayList<>();

    public PROVActivity(final String id, final String type, final Instant startTime) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
    }

    public void wasAssociatedWith(final PROVAgent agent) {
        this.wasAssociatedWith.add(agent);
    }

    public void used(final PROVEntity entity) {
        this.used.add(entity);
    }

    public void endedAt(final Instant time) {
        this.endTime = time;
    }

    public String toTurtle() {
        Model model = ModelFactory.createDefaultModel();

        Resource activity = model.createResource(PROV_NS + "Activity");
        Resource activityInstance = model.createResource(PROV_NS + id);

        Property typeProp = model.createProperty(PROV_NS, "type");
        Property labelProp = RDFS.label;
        Property startedAt = model.createProperty(PROV_NS, "startedAtTime");
        Property endedAt = model.createProperty(PROV_NS, "endedAtTime");
        Property wasAssociatedWith = model.createProperty(PROV_NS, "wasAssociatedWith");

        activityInstance.addProperty(typeProp, activity);
        activityInstance.addProperty(typeProp, type);
        activityInstance.addProperty(labelProp, "Activity: " + id);
        activityInstance.addProperty(startedAt,
            startTime.toString(), org.apache.jena.datatypes.xsd.XSDDatatype.XSDDateTime);

        if (endTime != null) {
            activityInstance.addProperty(endedAt,
                endTime.toString(), org.apache.jena.datatypes.xsd.XSDDatatype.XSDDateTime);
        }

        for (PROVAgent agent : wasAssociatedWith) {
            activityInstance.addProperty(wasAssociatedWith,
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
