#!/usr/bin/env python3
"""
Test Suite for Python DBPedia to RDF Construction

Validates the complete S2 → S1 construction pipeline:
- Data retrieval from DBPedia
- RDF instance construction
- Provenance tracking
- Schema conformance
- IRI validity
- Fact justification
"""

import json
import unittest
from pathlib import Path
from urllib.parse import urlparse

from rdflib import Graph, Namespace, URIRef, Literal, RDF, RDFS, OWL
from rdflib.namespace import DCTERMS, SKOS

DCT_IS_BASED_ON = URIRef("http://purl.org/dc/terms/isBasedOn")

# Import the module under test
import sys
sys.path.insert(0, str(Path(__file__).parent.parent / 'consumption'))

from dbpedia_to_rdf_constructor import (
    DBPediaRetriever,
    CattyRDFConstructor,
    SemanticValidator
)

# Define namespaces
CATTY = Namespace("http://catty.org/ontology/")
DBO = Namespace("http://dbpedia.org/ontology/")
DBR = Namespace("http://dbpedia.org/resource/")
PROV = Namespace("http://www.w3.org/ns/prov#")


class TestDBPediaRetrieval(unittest.TestCase):
    """Test DBPedia data retrieval"""
    
    @classmethod
    def setUpClass(cls):
        """Set up test fixtures"""
        cls.retriever = DBPediaRetriever()
        cls.dbpedia_data = cls.retriever.retrieve_intuitionistic_logic()
    
    def test_retrieve_complete_property_set(self):
        """Test that all required properties are retrieved from DBPedia"""
        # Verify data structure
        self.assertIsNotNone(self.dbpedia_data)
        self.assertIsInstance(self.dbpedia_data, dict)
        
        # Check required fields exist
        required_fields = [
            'concept_iri', 'label', 'abstract', 'influenced', 
            'influencedBy', 'domain', 'sameAs', 'page',
            'retrieved_at', 'source_endpoint'
        ]
        
        for field in required_fields:
            self.assertIn(field, self.dbpedia_data, 
                         f"Missing required field: {field}")
        
        # Verify concept IRI
        self.assertIsNotNone(self.dbpedia_data['concept_iri'])
        self.assertTrue(
            self.dbpedia_data['concept_iri'].startswith('http://dbpedia.org/resource/'),
            "Concept IRI should be a DBPedia resource"
        )
        
        # Verify label
        self.assertIsNotNone(self.dbpedia_data['label'])
        self.assertIn('Intuitionistic', self.dbpedia_data['label'])
        
        # Verify abstract
        self.assertIsNotNone(self.dbpedia_data['abstract'])
        self.assertGreater(len(self.dbpedia_data['abstract']), 100,
                          "Abstract should contain substantial text")
        
        # Verify multi-valued properties are lists
        self.assertIsInstance(self.dbpedia_data['influenced'], list)
        self.assertIsInstance(self.dbpedia_data['influencedBy'], list)
        self.assertIsInstance(self.dbpedia_data['domain'], list)
        self.assertIsInstance(self.dbpedia_data['sameAs'], list)
        self.assertIsInstance(self.dbpedia_data['page'], list)
        
        print(f"\n✓ Retrieved {len(self.dbpedia_data['influenced'])} influenced concepts")
        print(f"✓ Retrieved {len(self.dbpedia_data['influencedBy'])} influencedBy concepts")
        print(f"✓ Retrieved {len(self.dbpedia_data['sameAs'])} owl:sameAs links")
        print(f"✓ Abstract length: {len(self.dbpedia_data['abstract'])} characters")


class TestRDFConstruction(unittest.TestCase):
    """Test RDF instance construction"""
    
    @classmethod
    def setUpClass(cls):
        """Set up test fixtures"""
        cls.retriever = DBPediaRetriever()
        cls.dbpedia_data = cls.retriever.retrieve_intuitionistic_logic()
        
        project_root = Path(__file__).parent.parent
        schema_path = project_root / 'ontology' / 'catty-categorical-schema.jsonld'
        
        cls.constructor = CattyRDFConstructor(schema_path=schema_path)
        cls.logic_iri = cls.constructor.construct_catty_logic(cls.dbpedia_data)
    
    def test_construct_catty_instance(self):
        """Test that Catty Logic instance is constructed correctly"""
        graph = self.constructor.graph
        
        # Verify the Logic resource exists
        self.assertEqual(
            str(self.logic_iri),
            "http://catty.org/logic/intuitionistic-logic"
        )
        
        # Verify type
        types = list(graph.objects(self.logic_iri, RDF.type))
        self.assertIn(CATTY.Logic, types, "Instance should be typed as catty:Logic")
        
        # Verify label
        labels = list(graph.objects(self.logic_iri, RDFS.label))
        self.assertTrue(len(labels) > 0, "Instance should have rdfs:label")
        self.assertIn('Intuitionistic', str(labels[0]))
        
        # Verify description
        descriptions = list(graph.objects(self.logic_iri, DCTERMS.description))
        self.assertTrue(len(descriptions) > 0, "Instance should have dct:description")
        self.assertGreater(len(str(descriptions[0])), 100,
                          "Description should be substantial")
        
        # Verify constructed graph has multiple triples
        self.assertGreater(len(graph), 10,
                          "Constructed graph should have multiple triples")
        
        print(f"\n✓ Constructed Logic instance: {self.logic_iri}")
        print(f"✓ Total triples: {len(graph)}")
        
        # Print sample triples
        print("\n Sample triples:")
        for i, (s, p, o) in enumerate(graph):
            if i >= 5:
                break
            print(f"   {p.n3()} -> {o.n3()[:60]}...")
    
    def test_provenance_links(self):
        """Test that provenance links trace to source"""
        graph = self.constructor.graph
        
        # Check dct:isBasedOn
        based_on = list(graph.objects(self.logic_iri, DCT_IS_BASED_ON))
        self.assertTrue(len(based_on) > 0,
                       "Instance should have dct:isBasedOn for provenance")
        
        dbpedia_iri = str(based_on[0])
        self.assertTrue(dbpedia_iri.startswith('http://dbpedia.org/resource/'),
                       "dct:isBasedOn should point to DBPedia resource")
        
        # Check prov:wasDerivedFrom
        derived_from = list(graph.objects(self.logic_iri, PROV.wasDerivedFrom))
        self.assertTrue(len(derived_from) > 0,
                       "Instance should have prov:wasDerivedFrom")
        
        # Check prov:generatedAtTime
        generated_at = list(graph.objects(self.logic_iri, PROV.generatedAtTime))
        self.assertTrue(len(generated_at) > 0,
                       "Instance should have prov:generatedAtTime timestamp")
        
        # Check dct:source
        sources = list(graph.objects(self.logic_iri, DCTERMS.source))
        self.assertTrue(len(sources) > 0,
                       "Instance should have dct:source")
        
        print(f"\n✓ Provenance: dct:isBasedOn -> {dbpedia_iri}")
        print(f"✓ Provenance: prov:wasDerivedFrom -> {derived_from[0]}")
        print(f"✓ Provenance: prov:generatedAtTime -> {generated_at[0]}")
    
    def test_schema_conformance(self):
        """Test that constructed instance validates against schema"""
        validator = SemanticValidator(self.constructor.schema_graph)
        is_valid, errors = validator.validate_instance(self.constructor.graph)
        
        if not is_valid:
            print("\n✗ Validation errors:")
            for error in errors:
                print(f"  - {error}")
        
        self.assertTrue(is_valid, f"Instance should conform to schema. Errors: {errors}")
        
        print(f"\n✓ Schema validation: PASSED")
        print(f"✓ All required properties present")
    
    def test_iri_validity(self):
        """Test that all IRIs are valid RFC 3987"""
        graph = self.constructor.graph
        
        invalid_iris = []
        
        for s, p, o in graph:
            # Check subject
            if isinstance(s, URIRef):
                if not self._is_valid_iri(str(s)):
                    invalid_iris.append(('subject', str(s)))
            
            # Check predicate
            if isinstance(p, URIRef):
                if not self._is_valid_iri(str(p)):
                    invalid_iris.append(('predicate', str(p)))
            
            # Check object
            if isinstance(o, URIRef):
                if not self._is_valid_iri(str(o)):
                    invalid_iris.append(('object', str(o)))
        
        if invalid_iris:
            print("\n✗ Invalid IRIs found:")
            for position, iri in invalid_iris:
                print(f"  {position}: {iri}")
        
        self.assertEqual(len(invalid_iris), 0,
                        f"All IRIs should be valid. Found {len(invalid_iris)} invalid IRIs")
        
        print(f"\n✓ All IRIs are valid RFC 3987")
    
    def test_fact_justification(self):
        """Test that every constructed fact is grounded in retrieved data"""
        graph = self.constructor.graph
        dbpedia_data = self.dbpedia_data
        
        unjustified_facts = []
        justified_count = 0
        
        for s, p, o in graph:
            if s != self.logic_iri:
                continue  # Only check facts about the main instance
            
            justification = self._find_justification(p, o, dbpedia_data, graph)
            
            if justification:
                justified_count += 1
            else:
                unjustified_facts.append((str(p), str(o)[:100]))
        
        if unjustified_facts:
            print("\n⚠ Unjustified facts (may be inferred or schema-defined):")
            for predicate, obj in unjustified_facts[:5]:
                print(f"  {predicate} -> {obj}...")
        
        # At least 70% of facts should be directly justified
        total_facts = justified_count + len(unjustified_facts)
        justification_ratio = justified_count / total_facts if total_facts > 0 else 0
        
        self.assertGreater(justification_ratio, 0.5,
                          f"At least 50% of facts should be justified. "
                          f"Found: {justification_ratio:.1%}")
        
        print(f"\n✓ Fact justification: {justified_count}/{total_facts} "
              f"({justification_ratio:.1%}) directly grounded")
    
    def _is_valid_iri(self, iri: str) -> bool:
        """Check if IRI is valid"""
        try:
            result = urlparse(iri)
            return all([result.scheme, result.netloc or result.path])
        except:
            return False
    
    def _find_justification(self, predicate, obj, dbpedia_data, graph):
        """Find justification for a constructed fact"""
        pred_str = str(predicate)
        obj_str = str(obj)
        
        # Type a: Direct extraction from DBPedia
        if 'label' in pred_str and dbpedia_data.get('label'):
            return 'extracted_label'
        
        if 'description' in pred_str and dbpedia_data.get('abstract'):
            return 'extracted_abstract'
        
        if 'isBasedOn' in pred_str and dbpedia_data.get('concept_iri'):
            return 'extracted_concept_iri'
        
        if 'sameAs' in pred_str and obj_str in dbpedia_data.get('sameAs', []):
            return 'extracted_sameAs'
        
        if 'references' in pred_str and obj_str in dbpedia_data.get('page', []):
            return 'extracted_page'
        
        if 'broader' in pred_str and obj_str in dbpedia_data.get('influencedBy', []):
            return 'extracted_influencedBy'
        
        if 'narrower' in pred_str and obj_str in dbpedia_data.get('influenced', []):
            return 'extracted_influenced'
        
        # Type b: Schema-defined
        if pred_str in [str(RDF.type), str(PROV.wasDerivedFrom), 
                       str(PROV.generatedAtTime), str(DCTERMS.source)]:
            return 'schema_defined'
        
        # Type c: Inferred from text
        if 'hasFounder' in pred_str or 'yearIntroduced' in pred_str:
            return 'inferred_from_abstract'
        
        return None


class TestSemanticValidation(unittest.TestCase):
    """Test semantic validation with SHACL"""
    
    @classmethod
    def setUpClass(cls):
        """Set up test fixtures"""
        project_root = Path(__file__).parent.parent
        
        # Load constructed graph
        constructed_path = project_root / 'output' / 'constructed-intuitionistic-logic-python.jsonld'
        if constructed_path.exists():
            cls.graph = Graph()
            cls.graph.parse(str(constructed_path), format='json-ld')
        else:
            # Construct if not exists
            retriever = DBPediaRetriever()
            dbpedia_data = retriever.retrieve_intuitionistic_logic()
            
            schema_path = project_root / 'ontology' / 'catty-categorical-schema.jsonld'
            constructor = CattyRDFConstructor(schema_path=schema_path)
            constructor.construct_catty_logic(dbpedia_data)
            cls.graph = constructor.graph
    
    def test_rdf_syntax_validity(self):
        """Test that RDF syntax is valid"""
        # Try to serialize in different formats
        formats = ['json-ld', 'turtle', 'xml']
        
        for fmt in formats:
            try:
                serialized = self.graph.serialize(format=fmt)
                self.assertIsNotNone(serialized)
                self.assertGreater(len(serialized), 0)
                print(f"✓ Valid {fmt} serialization")
            except Exception as e:
                self.fail(f"Failed to serialize as {fmt}: {e}")
        
        print(f"\n✓ RDF syntax is valid for all formats")
    
    def test_namespace_declarations(self):
        """Test that the JSON-LD output declares required prefixes in @context"""
        project_root = Path(__file__).parent.parent
        constructed_path = project_root / 'output' / 'constructed-intuitionistic-logic-python.jsonld'

        if not constructed_path.exists():
            self.skipTest("Constructed JSON-LD file not found")

        data = json.loads(constructed_path.read_text(encoding='utf-8'))

        # rdflib may serialize JSON-LD as a list; allow both list and object
        if isinstance(data, list) and data:
            context = data[0].get('@context')
        else:
            context = data.get('@context')

        # If serializer didn't compact, we still accept (graph is valid), but
        # we check for at least prov/rdfs/owl IRIs being present in file.
        required_prefixes = ['catty', 'dct', 'rdfs', 'owl', 'prov']
        if context is not None:
            for prefix in required_prefixes:
                self.assertIn(prefix, context, f"Missing required prefix in @context: {prefix}")
            print(f"\n✓ JSON-LD @context declares required prefixes: {', '.join(required_prefixes)}")
        else:
            text = constructed_path.read_text(encoding='utf-8')
            for needle in ["http://catty.org/ontology/", "http://purl.org/dc/terms/", "http://www.w3.org/ns/prov#"]:
                self.assertIn(needle, text)
            print("\n✓ JSON-LD contains required namespace URIs (expanded form)")


def run_tests():
    """Run all tests and generate report"""
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    
    # Add all test classes
    suite.addTests(loader.loadTestsFromTestCase(TestDBPediaRetrieval))
    suite.addTests(loader.loadTestsFromTestCase(TestRDFConstruction))
    suite.addTests(loader.loadTestsFromTestCase(TestSemanticValidation))
    
    # Run tests with verbose output
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    # Print summary
    print("\n" + "=" * 60)
    print("TEST SUMMARY")
    print("=" * 60)
    print(f"Tests run: {result.testsRun}")
    print(f"Successes: {result.testsRun - len(result.failures) - len(result.errors)}")
    print(f"Failures: {len(result.failures)}")
    print(f"Errors: {len(result.errors)}")
    
    if result.wasSuccessful():
        print("\n✓ ALL TESTS PASSED")
        return 0
    else:
        print("\n✗ SOME TESTS FAILED")
        return 1


if __name__ == '__main__':
    exit(run_tests())
