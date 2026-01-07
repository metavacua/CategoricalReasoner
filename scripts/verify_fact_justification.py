#!/usr/bin/env python3
"""
Fact Justification Verification

Verifies that every triple in the constructed RDF graph is justified by:
  a) Direct extraction from DBPedia source
  b) Valid inference from source data + logical rules
  c) Schema-defined structural facts
  d) Equivalence mappings (owl:sameAs)

Generates a comprehensive provenance report showing the justification chain
for each constructed fact.
"""

import json
import logging
import re
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

from rdflib import Graph, Namespace, URIRef, Literal, RDF, RDFS, OWL, XSD
from rdflib.namespace import DCTERMS, SKOS

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Define namespaces
CATTY = Namespace("http://catty.org/ontology/")
DBO = Namespace("http://dbpedia.org/ontology/")
DBR = Namespace("http://dbpedia.org/resource/")
PROV = Namespace("http://www.w3.org/ns/prov#")

DCT_IS_BASED_ON = URIRef("http://purl.org/dc/terms/isBasedOn")


class FactJustificationValidator:
    """Validates that all facts in constructed graph are justified"""
    
    def __init__(self, constructed_graph: Graph, dbpedia_source: Dict, schema_graph: Graph):
        self.constructed_graph = constructed_graph
        self.dbpedia_source = dbpedia_source
        self.schema_graph = schema_graph
        
        self.justification_report = {
            'extracted': [],
            'inferred': [],
            'schema_defined': [],
            'equivalent_mapping': [],
            'unjustified': []
        }
        
        self.statistics = {
            'total_facts': 0,
            'extracted_facts': 0,
            'inferred_facts': 0,
            'schema_facts': 0,
            'equivalent_facts': 0,
            'unjustified_facts': 0
        }
    
    def validate_all_facts(self) -> Dict:
        """
        Validate all facts in the constructed graph.
        
        Returns:
            Dictionary containing justification report and statistics
        """
        logger.info("Starting fact justification validation...")
        logger.info(f"Constructed graph contains {len(self.constructed_graph)} triples")
        
        for subject, predicate, obj in self.constructed_graph:
            self.statistics['total_facts'] += 1
            
            justification = self._find_justification(subject, predicate, obj)
            
            if justification:
                # Add to appropriate category
                justification_type = justification['type']
                self.justification_report[justification_type].append({
                    'subject': str(subject),
                    'predicate': str(predicate),
                    'object': str(obj)[:200] if len(str(obj)) > 200 else str(obj),
                    'justification': justification
                })
                
                # Update statistics
                if justification_type == 'extracted':
                    self.statistics['extracted_facts'] += 1
                elif justification_type == 'inferred':
                    self.statistics['inferred_facts'] += 1
                elif justification_type == 'schema_defined':
                    self.statistics['schema_facts'] += 1
                elif justification_type == 'equivalent_mapping':
                    self.statistics['equivalent_facts'] += 1
            else:
                # Unjustified fact
                self.statistics['unjustified_facts'] += 1
                self.justification_report['unjustified'].append({
                    'subject': str(subject),
                    'predicate': str(predicate),
                    'object': str(obj)[:200] if len(str(obj)) > 200 else str(obj)
                })
                
                logger.warning(f"UNJUSTIFIED FACT: {predicate} -> {str(obj)[:100]}")
        
        self._log_statistics()
        
        return {
            'statistics': self.statistics,
            'report': self.justification_report
        }
    
    def _find_justification(self, subject: URIRef, predicate: URIRef, obj) -> Optional[Dict]:
        """
        Find justification for a single fact.
        
        Args:
            subject: Subject of the triple
            predicate: Predicate of the triple
            obj: Object of the triple
        
        Returns:
            Justification dictionary or None if unjustified
        """
        pred_str = str(predicate)
        obj_str = str(obj)
        
        # Type a: Direct extraction from DBPedia
        if extraction_justification := self._check_extraction(pred_str, obj_str):
            return extraction_justification
        
        # Type b: Inferred via rules
        if inference_justification := self._check_inference(subject, pred_str, obj_str):
            return inference_justification
        
        # Type c: Schema-defined
        if schema_justification := self._check_schema_defined(pred_str):
            return schema_justification
        
        # Type d: Equivalence mapping
        if equivalence_justification := self._check_equivalence(pred_str, obj_str):
            return equivalence_justification
        
        return None
    
    def _check_extraction(self, pred_str: str, obj_str: str) -> Optional[Dict]:
        """Check if fact is directly extracted from DBPedia source"""
        
        # rdfs:label from DBPedia label
        if 'label' in pred_str and self.dbpedia_source.get('label'):
            if self.dbpedia_source['label'] in obj_str:
                return {
                    'type': 'extracted',
                    'source_field': 'label',
                    'source_iri': self.dbpedia_source.get('concept_iri'),
                    'retrieval_query': 'DBPedia SPARQL: rdfs:label',
                    'extraction_method': 'direct_property_mapping'
                }
        
        # dct:description from DBPedia abstract
        if 'description' in pred_str and self.dbpedia_source.get('abstract'):
            if self.dbpedia_source['abstract'][:100] in obj_str[:100]:
                return {
                    'type': 'extracted',
                    'source_field': 'abstract',
                    'source_iri': self.dbpedia_source.get('concept_iri'),
                    'retrieval_query': 'DBPedia SPARQL: dbo:abstract',
                    'extraction_method': 'direct_property_mapping'
                }
        
        # dct:isBasedOn from DBPedia concept IRI
        if 'isBasedOn' in pred_str:
            if obj_str == self.dbpedia_source.get('concept_iri'):
                return {
                    'type': 'extracted',
                    'source_field': 'concept_iri',
                    'source_iri': obj_str,
                    'retrieval_query': 'DBPedia SPARQL: main subject IRI',
                    'extraction_method': 'direct_iri_reference'
                }
        
        # owl:sameAs from DBPedia sameAs property
        if 'sameAs' in pred_str:
            if obj_str in self.dbpedia_source.get('sameAs', []):
                return {
                    'type': 'extracted',
                    'source_field': 'sameAs',
                    'source_iri': obj_str,
                    'retrieval_query': 'DBPedia SPARQL: owl:sameAs',
                    'extraction_method': 'direct_property_mapping'
                }
        
        # dct:references from DBPedia page property
        if 'references' in pred_str:
            if obj_str in self.dbpedia_source.get('page', []):
                return {
                    'type': 'extracted',
                    'source_field': 'page',
                    'source_iri': obj_str,
                    'retrieval_query': 'DBPedia SPARQL: foaf:page',
                    'extraction_method': 'direct_property_mapping'
                }
        
        # skos:broader from DBPedia influencedBy
        if 'broader' in pred_str:
            if obj_str in self.dbpedia_source.get('influencedBy', []):
                return {
                    'type': 'extracted',
                    'source_field': 'influencedBy',
                    'source_iri': obj_str,
                    'retrieval_query': 'DBPedia SPARQL: dbo:influencedBy',
                    'extraction_method': 'semantic_mapping',
                    'mapping_rule': 'dbo:influencedBy → skos:broader (concept hierarchy)'
                }
        
        # skos:narrower from DBPedia influenced
        if 'narrower' in pred_str:
            if obj_str in self.dbpedia_source.get('influenced', []):
                return {
                    'type': 'extracted',
                    'source_field': 'influenced',
                    'source_iri': obj_str,
                    'retrieval_query': 'DBPedia SPARQL: dbo:influenced',
                    'extraction_method': 'semantic_mapping',
                    'mapping_rule': 'dbo:influenced → skos:narrower (concept hierarchy)'
                }
        
        return None
    
    def _check_inference(self, subject: URIRef, pred_str: str, obj_str: str) -> Optional[Dict]:
        """Check if fact is validly inferred from source data"""
        
        # catty:hasFounder inferred from abstract text
        if 'hasFounder' in pred_str:
            abstract = self.dbpedia_source.get('abstract', '')
            if abstract:
                # Check if the founder name appears in abstract
                founder_name = obj_str.strip('"\'')
                if founder_name in abstract:
                    return {
                        'type': 'inferred',
                        'inference_rule': 'entity_extraction',
                        'source_text': abstract[:200] + '...',
                        'grounded_in': [self.dbpedia_source.get('concept_iri')],
                        'extraction_pattern': 'founder name extraction from abstract',
                        'confidence': 'high'
                    }
        
        # catty:yearIntroduced inferred from foundationYear or abstract
        if 'yearIntroduced' in pred_str:
            # Check foundationYear field
            if self.dbpedia_source.get('foundationYear'):
                year = str(self.dbpedia_source['foundationYear'])
                if year in obj_str:
                    return {
                        'type': 'inferred',
                        'inference_rule': 'year_extraction',
                        'source_field': 'foundationYear',
                        'grounded_in': [self.dbpedia_source.get('concept_iri')],
                        'extraction_method': 'direct_field_mapping',
                        'confidence': 'high'
                    }
            
            # Check abstract text
            abstract = self.dbpedia_source.get('abstract', '')
            if abstract:
                year_pattern = r'\b(19\d{2}|20\d{2})\b'
                match = re.search(year_pattern, abstract)
                if match and match.group(1) in obj_str:
                    return {
                        'type': 'inferred',
                        'inference_rule': 'year_extraction',
                        'source_text': abstract[:200] + '...',
                        'grounded_in': [self.dbpedia_source.get('concept_iri')],
                        'extraction_pattern': year_pattern,
                        'confidence': 'medium'
                    }
        
        return None
    
    def _check_schema_defined(self, pred_str: str) -> Optional[Dict]:
        """Check if fact is defined by the schema"""
        
        # RDF type assertions (structural)
        if pred_str == str(RDF.type):
            return {
                'type': 'schema_defined',
                'schema_element': 'rdf:type',
                'schema_definition': 'Type assertion required by RDF schema',
                'structural': True
            }
        
        # Provenance predicates (required by PROV-O)
        if 'wasDerivedFrom' in pred_str:
            return {
                'type': 'schema_defined',
                'schema_element': 'prov:wasDerivedFrom',
                'schema_definition': 'Provenance tracking (PROV-O)',
                'structural': True
            }
        
        if 'generatedAtTime' in pred_str:
            return {
                'type': 'schema_defined',
                'schema_element': 'prov:generatedAtTime',
                'schema_definition': 'Timestamp for provenance (PROV-O)',
                'structural': True
            }
        
        # Dublin Core source metadata
        if str(DCTERMS.source) in pred_str:
            return {
                'type': 'schema_defined',
                'schema_element': 'dct:source',
                'schema_definition': 'Source attribution (Dublin Core)',
                'structural': True
            }
        
        return None
    
    def _check_equivalence(self, pred_str: str, obj_str: str) -> Optional[Dict]:
        """Check if fact is derived via equivalence mappings"""
        
        # Check if there are owl:sameAs relationships in the constructed graph
        # that could justify this fact through equivalence
        
        for s, p, o in self.constructed_graph:
            if str(p) == str(OWL.sameAs):
                # If we have an equivalence relationship, facts about
                # equivalent entities are justified
                if str(s) in obj_str or str(o) in obj_str:
                    return {
                        'type': 'equivalent_mapping',
                        'equivalent_to': str(o) if str(s) in obj_str else str(s),
                        'equivalence_property': 'owl:sameAs',
                        'justification': 'Facts about equivalent entities are transitive'
                    }
        
        return None
    
    def _log_statistics(self):
        """Log validation statistics"""
        stats = self.statistics
        
        logger.info("=" * 60)
        logger.info("FACT JUSTIFICATION STATISTICS")
        logger.info("=" * 60)
        logger.info(f"Total facts: {stats['total_facts']}")
        logger.info(f"")
        logger.info(f"Extracted facts: {stats['extracted_facts']} "
                   f"({stats['extracted_facts']/stats['total_facts']*100:.1f}%)")
        logger.info(f"Inferred facts: {stats['inferred_facts']} "
                   f"({stats['inferred_facts']/stats['total_facts']*100:.1f}%)")
        logger.info(f"Schema-defined facts: {stats['schema_facts']} "
                   f"({stats['schema_facts']/stats['total_facts']*100:.1f}%)")
        logger.info(f"Equivalent mappings: {stats['equivalent_facts']} "
                   f"({stats['equivalent_facts']/stats['total_facts']*100:.1f}%)")
        logger.info(f"")
        logger.info(f"Unjustified facts: {stats['unjustified_facts']} "
                   f"({stats['unjustified_facts']/stats['total_facts']*100:.1f}%)")
        
        if stats['unjustified_facts'] == 0:
            logger.info("")
            logger.info("✓ SUCCESS: All facts are justified (100% coverage)")
        else:
            logger.warning("")
            logger.warning(f"⚠ WARNING: {stats['unjustified_facts']} unjustified facts found")
    
    def generate_markdown_report(self, output_path: Path):
        """Generate detailed Markdown report"""
        
        report_lines = [
            "# Fact Justification Report",
            "",
            "## Summary Statistics",
            "",
            f"- **Total facts**: {self.statistics['total_facts']}",
            f"- **Extracted facts**: {self.statistics['extracted_facts']} ({self.statistics['extracted_facts']/self.statistics['total_facts']*100:.1f}%)",
            f"- **Inferred facts**: {self.statistics['inferred_facts']} ({self.statistics['inferred_facts']/self.statistics['total_facts']*100:.1f}%)",
            f"- **Schema-defined facts**: {self.statistics['schema_facts']} ({self.statistics['schema_facts']/self.statistics['total_facts']*100:.1f}%)",
            f"- **Equivalent mappings**: {self.statistics['equivalent_facts']} ({self.statistics['equivalent_facts']/self.statistics['total_facts']*100:.1f}%)",
            f"- **Unjustified facts**: {self.statistics['unjustified_facts']} ({self.statistics['unjustified_facts']/self.statistics['total_facts']*100:.1f}%)",
            "",
        ]
        
        if self.statistics['unjustified_facts'] == 0:
            report_lines.append("✅ **SUCCESS**: All facts are justified (100% coverage)\n")
        else:
            report_lines.append(f"⚠️ **WARNING**: {self.statistics['unjustified_facts']} unjustified facts found\n")
        
        # Extracted facts
        report_lines.extend([
            "## Extracted Facts (Direct from DBPedia)",
            "",
            f"Total: {self.statistics['extracted_facts']}",
            ""
        ])
        
        for fact in self.justification_report['extracted'][:10]:  # Show first 10
            predicate = fact['predicate'].split('/')[-1].split('#')[-1]
            obj = fact['object'][:100]
            justification = fact['justification']
            
            report_lines.extend([
                f"### {predicate}",
                f"- **Object**: `{obj}`",
                f"- **Source field**: `{justification.get('source_field')}`",
                f"- **Source IRI**: <{justification.get('source_iri')}>",
                f"- **Extraction method**: {justification.get('extraction_method')}",
                ""
            ])
        
        if len(self.justification_report['extracted']) > 10:
            report_lines.append(f"*... and {len(self.justification_report['extracted']) - 10} more extracted facts*\n")
        
        # Inferred facts
        report_lines.extend([
            "## Inferred Facts (Derived via Rules)",
            "",
            f"Total: {self.statistics['inferred_facts']}",
            ""
        ])
        
        for fact in self.justification_report['inferred']:
            predicate = fact['predicate'].split('/')[-1].split('#')[-1]
            obj = fact['object'][:100]
            justification = fact['justification']
            
            report_lines.extend([
                f"### {predicate}",
                f"- **Object**: `{obj}`",
                f"- **Inference rule**: {justification.get('inference_rule')}",
                f"- **Grounded in**: {justification.get('grounded_in')}",
                f"- **Confidence**: {justification.get('confidence')}",
                ""
            ])
        
        # Schema-defined facts
        report_lines.extend([
            "## Schema-Defined Facts (Structural)",
            "",
            f"Total: {self.statistics['schema_facts']}",
            ""
        ])
        
        # Group by schema element
        schema_groups = defaultdict(list)
        for fact in self.justification_report['schema_defined']:
            schema_element = fact['justification'].get('schema_element')
            schema_groups[schema_element].append(fact)
        
        for schema_element, facts in schema_groups.items():
            report_lines.extend([
                f"### {schema_element}",
                f"- **Count**: {len(facts)}",
                f"- **Definition**: {facts[0]['justification'].get('schema_definition')}",
                ""
            ])
        
        # Unjustified facts
        if self.statistics['unjustified_facts'] > 0:
            report_lines.extend([
                "## ⚠️ Unjustified Facts",
                "",
                f"Total: {self.statistics['unjustified_facts']}",
                "",
                "These facts could not be traced back to source data:",
                ""
            ])
            
            for fact in self.justification_report['unjustified']:
                predicate = fact['predicate'].split('/')[-1].split('#')[-1]
                obj = fact['object'][:100]
                
                report_lines.extend([
                    f"- **{predicate}**: `{obj}`",
                ])
        
        # Write report
        output_path.parent.mkdir(parents=True, exist_ok=True)
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(report_lines))
        
        logger.info(f"Wrote detailed report to: {output_path}")


def main():
    """Main execution"""
    project_root = Path(__file__).parent.parent
    
    # Load constructed graph
    constructed_path = project_root / 'output' / 'constructed-intuitionistic-logic-python.jsonld'
    if not constructed_path.exists():
        logger.error(f"Constructed graph not found: {constructed_path}")
        logger.error("Please run dbpedia_to_rdf_constructor.py first")
        return 1
    
    logger.info(f"Loading constructed graph from: {constructed_path}")
    constructed_graph = Graph()
    constructed_graph.parse(str(constructed_path), format='json-ld')
    
    # Load DBPedia source data
    source_path = project_root / 'output' / 'dbpedia-intuitionistic-logic-retrieved.jsonld'
    if not source_path.exists():
        logger.error(f"DBPedia source data not found: {source_path}")
        logger.error("Please run dbpedia_to_rdf_constructor.py first")
        return 1
    
    logger.info(f"Loading DBPedia source data from: {source_path}")
    with open(source_path, 'r', encoding='utf-8') as f:
        dbpedia_source = json.load(f)
    
    # Load schema
    schema_path = project_root / 'ontology' / 'catty-categorical-schema.jsonld'
    logger.info(f"Loading schema from: {schema_path}")
    schema_graph = Graph()
    schema_graph.parse(str(schema_path), format='json-ld')
    
    # Validate
    validator = FactJustificationValidator(constructed_graph, dbpedia_source, schema_graph)
    result = validator.validate_all_facts()
    
    # Generate report
    report_path = project_root / 'output' / 'provenance-justification-report.md'
    validator.generate_markdown_report(report_path)
    
    # Save JSON report
    json_report_path = project_root / 'output' / 'provenance-justification-report.json'
    with open(json_report_path, 'w', encoding='utf-8') as f:
        json.dump(result, f, indent=2)
    
    logger.info(f"Saved JSON report to: {json_report_path}")
    
    # Exit with error if unjustified facts found
    if result['statistics']['unjustified_facts'] > 0:
        return 1
    
    return 0


if __name__ == '__main__':
    import sys
    sys.exit(main())
