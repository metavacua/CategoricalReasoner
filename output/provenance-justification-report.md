# Fact Justification Report

## Summary Statistics

- **Total facts**: 14
- **Extracted facts**: 10 (71.4%)
- **Inferred facts**: 0 (0.0%)
- **Schema-defined facts**: 4 (28.6%)
- **Equivalent mappings**: 0 (0.0%)
- **Unjustified facts**: 0 (0.0%)

✅ **SUCCESS**: All facts are justified (100% coverage)

## Extracted Facts (Direct from DBPedia)

Total: 10

### description
- **Object**: `Intuitionistic logic, sometimes more generally called constructive logic, refers to systems of symbo`
- **Source field**: `abstract`
- **Source IRI**: <http://dbpedia.org/resource/Intuitionistic_logic>
- **Extraction method**: direct_property_mapping

### sameAs
- **Object**: `http://rdf.freebase.com/ns/m.03cxd`
- **Source field**: `sameAs`
- **Source IRI**: <http://rdf.freebase.com/ns/m.03cxd>
- **Extraction method**: direct_property_mapping

### broader
- **Object**: `http://dbpedia.org/resource/L._E._J._Brouwer`
- **Source field**: `influencedBy`
- **Source IRI**: <http://dbpedia.org/resource/L._E._J._Brouwer>
- **Extraction method**: semantic_mapping

### broader
- **Object**: `http://dbpedia.org/resource/Mathematical_constructivism`
- **Source field**: `influencedBy`
- **Source IRI**: <http://dbpedia.org/resource/Mathematical_constructivism>
- **Extraction method**: semantic_mapping

### sameAs
- **Object**: `http://www.wikidata.org/entity/Q1993854`
- **Source field**: `sameAs`
- **Source IRI**: <http://www.wikidata.org/entity/Q1993854>
- **Extraction method**: direct_property_mapping

### narrower
- **Object**: `http://dbpedia.org/resource/Type_theory`
- **Source field**: `influenced`
- **Source IRI**: <http://dbpedia.org/resource/Type_theory>
- **Extraction method**: semantic_mapping

### narrower
- **Object**: `http://dbpedia.org/resource/Constructive_mathematics`
- **Source field**: `influenced`
- **Source IRI**: <http://dbpedia.org/resource/Constructive_mathematics>
- **Extraction method**: semantic_mapping

### references
- **Object**: `http://en.wikipedia.org/wiki/Intuitionistic_logic`
- **Source field**: `page`
- **Source IRI**: <http://en.wikipedia.org/wiki/Intuitionistic_logic>
- **Extraction method**: direct_property_mapping

### isBasedOn
- **Object**: `http://dbpedia.org/resource/Intuitionistic_logic`
- **Source field**: `concept_iri`
- **Source IRI**: <http://dbpedia.org/resource/Intuitionistic_logic>
- **Extraction method**: direct_iri_reference

### label
- **Object**: `Intuitionistic logic`
- **Source field**: `label`
- **Source IRI**: <http://dbpedia.org/resource/Intuitionistic_logic>
- **Extraction method**: direct_property_mapping

## Inferred Facts (Derived via Rules)

Total: 0

## Schema-Defined Facts (Structural)

Total: 4

### rdf:type
- **Count**: 1
- **Definition**: Type assertion required by RDF schema

### prov:wasDerivedFrom
- **Count**: 1
- **Definition**: Provenance tracking (PROV-O)

### dct:source
- **Count**: 1
- **Definition**: Source attribution (Dublin Core)

### prov:generatedAtTime
- **Count**: 1
- **Definition**: Timestamp for provenance (PROV-O)
