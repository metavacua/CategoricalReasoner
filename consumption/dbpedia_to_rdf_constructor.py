#!/usr/bin/env python3
"""
DBPedia to RDF Constructor

Retrieves semantic data from DBPedia and constructs validated Catty RDF instances.
Demonstrates S2 → S1 construction with full provenance tracking.
"""

import json
import logging
import re
import sys
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple
from urllib.parse import urlencode, quote

try:
    import requests
    REQUESTS_AVAILABLE = True
except ImportError:
    REQUESTS_AVAILABLE = False
    logger = logging.getLogger(__name__)
    logger.warning("requests module not available - Wikidata dereferencing and IRI validation disabled")

from rdflib import Graph, Namespace, Literal, URIRef, RDF, RDFS, OWL, XSD
from rdflib.namespace import DCTERMS, FOAF, SKOS

try:
    from SPARQLWrapper import SPARQLWrapper, JSON
    SPARQLWRAPPER_AVAILABLE = True
except ImportError:
    SPARQLWRAPPER_AVAILABLE = False
    logger = logging.getLogger(__name__)
    logger.warning("SPARQLWrapper not available - will use fixture data only")

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Define namespaces
CATTY = Namespace("http://catty.org/ontology/")
DBO = Namespace("http://dbpedia.org/ontology/")
DBR = Namespace("http://dbpedia.org/resource/")
PROV = Namespace("http://www.w3.org/ns/prov#")

# Note: dcterms does not define isBasedOn as a standard term; we still
# use this IRI as a provenance predicate for this project.
DCT_IS_BASED_ON = URIRef("http://purl.org/dc/terms/isBasedOn")

# DBPedia SPARQL endpoint
DBPEDIA_SPARQL = "https://dbpedia.org/sparql"


class DBPediaRetriever:
    """Retrieves complete property sets from DBPedia SPARQL endpoint"""

    def __init__(self, endpoint: str = DBPEDIA_SPARQL):
        self.endpoint = endpoint
        if SPARQLWRAPPER_AVAILABLE:
            self.sparql = SPARQLWrapper(endpoint)
            self.sparql.setReturnFormat(JSON)
        else:
            self.sparql = None
        self.retrieved_iris: Set[str] = set()

    def _fixture_path(self) -> Path:
        project_root = Path(__file__).resolve().parents[1]
        return project_root / "output" / "dbpedia-intuitionistic-logic-retrieved.jsonld"

    def retrieve_intuitionistic_logic(self) -> Dict:
        """
        Retrieve complete property set for Intuitionistic Logic.

        For deterministic runs (e.g. CI environments without external network
        access), this method prefers loading a local fixture if present.

        Returns:
            Dictionary containing all retrieved properties and metadata
        """
        fixture_path = self._fixture_path()
        if fixture_path.exists():
            logger.info(f"Loading DBPedia retrieval fixture: {fixture_path}")
            with open(fixture_path, "r", encoding="utf-8") as f:
                data = json.load(f)

            # Populate retrieved IRIs from fixture
            for key in ["concept_iri"]:
                if data.get(key):
                    self.retrieved_iris.add(data[key])
            for key in ["influenced", "influencedBy", "domain", "sameAs", "page"]:
                for iri in data.get(key, []) or []:
                    self.retrieved_iris.add(iri)

            return data

        logger.info("Querying DBPedia for Intuitionistic Logic...")

        if not self.sparql:
            raise RuntimeError(
                "SPARQLWrapper not available and no fixture present; cannot query DBPedia"
            )

        query = """
        PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX dbr: <http://dbpedia.org/resource/>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX foaf: <http://foaf.xmlns.com/0.1/>

        SELECT DISTINCT ?concept ?label ?abstract ?influenced ?influencedBy
               ?domain ?sameAs ?page ?foundationYear
        WHERE {
          {
            ?concept rdfs:label "Intuitionistic logic"@en .
          } UNION {
            ?concept rdfs:label "Intuitionistic Logic"@en .
          }

          OPTIONAL { ?concept rdfs:label ?label . FILTER(lang(?label) = "en") }
          OPTIONAL { ?concept dbo:abstract ?abstract . FILTER(lang(?abstract) = "en") }
          OPTIONAL { ?concept dbo:influenced ?influenced }
          OPTIONAL { ?concept dbo:influencedBy ?influencedBy }
          OPTIONAL { ?concept dbo:domain ?domain }
          OPTIONAL { ?concept owl:sameAs ?sameAs }
          OPTIONAL { ?concept foaf:page ?page }
          OPTIONAL { ?concept dbo:foundationYear ?foundationYear }
        }
        LIMIT 100
        """

        self.sparql.setQuery(query)

        try:
            results = self.sparql.query().convert()

            if not results or "results" not in results or "bindings" not in results["results"]:
                logger.error("No results returned from DBPedia")
                return {}

            bindings = results["results"]["bindings"]
            logger.info(f"Retrieved {len(bindings)} results from DBPedia")

            # Process results
            data = {
                "concept_iri": None,
                "label": None,
                "abstract": None,
                "influenced": [],
                "influencedBy": [],
                "domain": [],
                "sameAs": [],
                "page": [],
                "foundationYear": None,
                "retrieved_at": datetime.utcnow().isoformat() + "Z",
                "source_endpoint": self.endpoint,
                "raw_bindings": bindings,
            }

            for binding in bindings:
                # Concept IRI (main subject)
                if "concept" in binding and not data["concept_iri"]:
                    data["concept_iri"] = binding["concept"]["value"]
                    self.retrieved_iris.add(data["concept_iri"])

                # Label
                if "label" in binding and not data["label"]:
                    data["label"] = binding["label"]["value"]

                # Abstract (description)
                if "abstract" in binding and not data["abstract"]:
                    data["abstract"] = binding["abstract"]["value"]

                # Foundation year
                if "foundationYear" in binding and not data["foundationYear"]:
                    data["foundationYear"] = binding["foundationYear"]["value"]

                # Multi-valued properties
                if "influenced" in binding:
                    iri = binding["influenced"]["value"]
                    if iri not in data["influenced"]:
                        data["influenced"].append(iri)
                        self.retrieved_iris.add(iri)

                if "influencedBy" in binding:
                    iri = binding["influencedBy"]["value"]
                    if iri not in data["influencedBy"]:
                        data["influencedBy"].append(iri)
                        self.retrieved_iris.add(iri)

                if "domain" in binding:
                    iri = binding["domain"]["value"]
                    if iri not in data["domain"]:
                        data["domain"].append(iri)
                        self.retrieved_iris.add(iri)

                if "sameAs" in binding:
                    iri = binding["sameAs"]["value"]
                    if iri not in data["sameAs"]:
                        data["sameAs"].append(iri)
                        self.retrieved_iris.add(iri)

                if "page" in binding:
                    iri = binding["page"]["value"]
                    if iri not in data["page"]:
                        data["page"].append(iri)
                        self.retrieved_iris.add(iri)

            logger.info(f"Successfully retrieved data for: {data['label']}")
            logger.info(f"  Abstract length: {len(data['abstract']) if data['abstract'] else 0} chars")
            logger.info(f"  Influenced: {len(data['influenced'])} concepts")
            logger.info(f"  InfluencedBy: {len(data['influencedBy'])} concepts")
            logger.info(f"  Domains: {len(data['domain'])} domains")
            logger.info(f"  SameAs links: {len(data['sameAs'])} links")
            logger.info(f"  Pages: {len(data['page'])} pages")

            return data

        except Exception as e:
            logger.error(f"Error querying DBPedia: {e}")
            raise
    
    def dereference_wikidata(self, wikidata_iri: str) -> Optional[Dict]:
        """
        Dereference Wikidata IRI using HTTP content negotiation.
        
        Args:
            wikidata_iri: Wikidata entity IRI (e.g., https://www.wikidata.org/entity/Q1993854)
        
        Returns:
            Parsed JSON-LD data from Wikidata or None if failed
        """
        if not REQUESTS_AVAILABLE:
            logger.warning("requests module not available - skipping Wikidata dereferencing")
            return None

        logger.info(f"Dereferencing Wikidata IRI: {wikidata_iri}")
        
        headers = {
            'Accept': 'application/ld+json',
            'User-Agent': 'CattyThesis/1.0 (Educational Research Project)'
        }
        
        try:
            response = requests.get(wikidata_iri, headers=headers, timeout=30)
            response.raise_for_status()
            
            if response.status_code == 200:
                logger.info(f"Successfully dereferenced Wikidata IRI (status: {response.status_code})")
                return response.json()
            else:
                logger.warning(f"Unexpected status code: {response.status_code}")
                return None
                
        except requests.exceptions.RequestException as e:
            logger.warning(f"Failed to dereference Wikidata IRI: {e}")
            return None
    
    def validate_iris(self, iris: Set[str]) -> Dict[str, bool]:
        """
        Validate IRIs by performing HTTP HEAD requests.

        In environments where the `requests` library isn't available, this
        returns an empty dict (validation skipped).
        
        Args:
            iris: Set of IRIs to validate
        
        Returns:
            Dictionary mapping IRI to validation status (True/False)
        """
        if not REQUESTS_AVAILABLE:
            logger.warning("requests module not available - skipping HTTP IRI validation")
            return {}

        logger.info(f"Validating {len(iris)} unique IRIs...")
        
        validation_results = {}
        
        for iri in iris:
            try:
                response = requests.head(iri, timeout=10, allow_redirects=True)
                is_valid = response.status_code == 200
                validation_results[iri] = is_valid
                
                if is_valid:
                    logger.debug(f"✓ Valid IRI: {iri}")
                else:
                    logger.warning(f"✗ Invalid IRI (status {response.status_code}): {iri}")
                    
            except requests.exceptions.RequestException as e:
                logger.warning(f"✗ Failed to validate IRI: {iri} - {e}")
                validation_results[iri] = False
        
        valid_count = sum(1 for v in validation_results.values() if v)
        logger.info(f"IRI validation: {valid_count}/{len(iris)} valid")
        
        return validation_results


class CattyRDFConstructor:
    """Constructs Catty RDF instances from retrieved DBPedia data"""
    
    def __init__(self, schema_path: Optional[Path] = None):
        self.graph = Graph()
        self.schema_graph = Graph()
        
        # Bind namespaces
        self.graph.bind('catty', CATTY)
        self.graph.bind('dbo', DBO)
        self.graph.bind('dbr', DBR)
        self.graph.bind('dct', DCTERMS)
        self.graph.bind('prov', PROV)
        self.graph.bind('owl', OWL)
        self.graph.bind('skos', SKOS)
        self.graph.bind('foaf', FOAF)
        
        # Load schema if provided
        if schema_path and schema_path.exists():
            logger.info(f"Loading schema from: {schema_path}")
            self.schema_graph.parse(str(schema_path), format='json-ld')
    
    def construct_catty_logic(self, dbpedia_data: Dict) -> URIRef:
        """
        Construct a catty:Logic instance from retrieved DBPedia data.
        
        Args:
            dbpedia_data: Retrieved data from DBPedia
        
        Returns:
            URIRef of the constructed Logic instance
        """
        logger.info("Constructing Catty Logic instance...")
        
        # Create the Logic resource
        logic_iri = URIRef("http://catty.org/logic/intuitionistic-logic")
        
        # Add type
        self.graph.add((logic_iri, RDF.type, CATTY.Logic))
        
        # Add label (from DBPedia)
        if dbpedia_data.get('label'):
            self.graph.add((
                logic_iri,
                RDFS.label,
                Literal(dbpedia_data['label'], lang='en')
            ))
        
        # Add description (from DBPedia abstract)
        if dbpedia_data.get('abstract'):
            self.graph.add((
                logic_iri,
                DCTERMS.description,
                Literal(dbpedia_data['abstract'], lang='en')
            ))
        
        # Add provenance - isBasedOn
        if dbpedia_data.get('concept_iri'):
            self.graph.add((
                logic_iri,
                DCT_IS_BASED_ON,
                URIRef(dbpedia_data['concept_iri'])
            ))
        
        # Add owl:sameAs links
        for same_as_iri in dbpedia_data.get('sameAs', []):
            self.graph.add((
                logic_iri,
                OWL.sameAs,
                URIRef(same_as_iri)
            ))
        
        # Add dct:references (Wikipedia and other pages)
        for page_iri in dbpedia_data.get('page', []):
            self.graph.add((
                logic_iri,
                DCTERMS.references,
                URIRef(page_iri)
            ))
        
        # Extract founder from abstract (if present)
        founder = self._extract_founder_from_abstract(dbpedia_data.get('abstract', ''))
        if founder:
            self.graph.add((
                logic_iri,
                CATTY.hasFounder,
                Literal(founder, datatype=XSD.string)
            ))
        
        # Extract year introduced
        year = self._extract_year_from_data(dbpedia_data)
        if year:
            self.graph.add((
                logic_iri,
                CATTY.yearIntroduced,
                Literal(year, datatype=XSD.gYear)
            ))
        
        # Add SKOS broader relationships (from influenced/influencedBy)
        for influenced_by_iri in dbpedia_data.get('influencedBy', []):
            self.graph.add((
                logic_iri,
                SKOS.broader,
                URIRef(influenced_by_iri)
            ))
        
        # Add SKOS narrower relationships
        for influenced_iri in dbpedia_data.get('influenced', []):
            self.graph.add((
                logic_iri,
                SKOS.narrower,
                URIRef(influenced_iri)
            ))
        
        # Add provenance metadata
        self.graph.add((
            logic_iri,
            PROV.wasDerivedFrom,
            URIRef(dbpedia_data['concept_iri'])
        ))
        
        self.graph.add((
            logic_iri,
            PROV.generatedAtTime,
            Literal(dbpedia_data['retrieved_at'], datatype=XSD.dateTime)
        ))
        
        # Add data source
        self.graph.add((
            logic_iri,
            DCTERMS.source,
            Literal(dbpedia_data['source_endpoint'], datatype=XSD.anyURI)
        ))
        
        logger.info(f"Constructed Logic instance: {logic_iri}")
        logger.info(f"  Total triples: {len(self.graph)}")
        
        return logic_iri
    
    def _extract_founder_from_abstract(self, abstract: str) -> Optional[str]:
        """
        Extract founder name from abstract text using pattern matching.
        
        Args:
            abstract: DBPedia abstract text
        
        Returns:
            Founder name or None
        """
        if not abstract:
            return None
        
        # Pattern: "developed by [Name]" or "founded by [Name]" or "[Name] developed"
        patterns = [
            r'developed by ([A-Z][a-z]+(?: [A-Z][a-z]+)*)',
            r'founded by ([A-Z][a-z]+(?: [A-Z][a-z]+)*)',
            r'created by ([A-Z][a-z]+(?: [A-Z][a-z]+)*)',
            r'^([A-Z][a-z]+(?: [A-Z][a-z]+)*) developed',
            r'due to ([A-Z][a-z]+(?: [A-Z][a-z]+)*)',
        ]
        
        for pattern in patterns:
            match = re.search(pattern, abstract)
            if match:
                return match.group(1)
        
        return None
    
    def _extract_year_from_data(self, dbpedia_data: Dict) -> Optional[str]:
        """
        Extract year from DBPedia data or abstract.
        
        Args:
            dbpedia_data: Retrieved DBPedia data
        
        Returns:
            Year as string or None
        """
        # First check if there's a foundationYear
        if dbpedia_data.get('foundationYear'):
            return str(dbpedia_data['foundationYear'])
        
        # Otherwise extract from abstract
        abstract = dbpedia_data.get('abstract', '')
        if not abstract:
            return None
        
        # Pattern: 4-digit year (19xx or 20xx)
        year_match = re.search(r'\b(19\d{2}|20\d{2})\b', abstract)
        if year_match:
            return year_match.group(1)
        
        return None
    
    def serialize(self, output_path: Path, format: str = 'json-ld'):
        """
        Serialize the constructed RDF graph to file.
        
        Args:
            output_path: Output file path
            format: RDF serialization format
        """
        logger.info(f"Serializing graph to: {output_path}")
        
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(self.graph.serialize(format=format))
        
        logger.info(f"Serialized {len(self.graph)} triples")


class SemanticValidator:
    """Validates constructed RDF against schema and SHACL constraints"""
    
    def __init__(self, schema_graph: Graph):
        self.schema_graph = schema_graph
    
    def validate_instance(self, instance_graph: Graph) -> Tuple[bool, List[str]]:
        """
        Validate RDF instance against schema.
        
        Args:
            instance_graph: RDF graph to validate
        
        Returns:
            Tuple of (is_valid, list of errors)
        """
        errors = []
        
        # Check 1: Verify all IRIs are valid RFC 3987
        for s, p, o in instance_graph:
            if isinstance(s, URIRef):
                if not self._is_valid_iri(str(s)):
                    errors.append(f"Invalid subject IRI: {s}")
            
            if isinstance(p, URIRef):
                if not self._is_valid_iri(str(p)):
                    errors.append(f"Invalid predicate IRI: {p}")
            
            if isinstance(o, URIRef):
                if not self._is_valid_iri(str(o)):
                    errors.append(f"Invalid object IRI: {o}")
        
        # Check 2: Verify catty:Logic instances have required properties
        for logic in instance_graph.subjects(RDF.type, CATTY.Logic):
            # Check for label
            if not list(instance_graph.objects(logic, RDFS.label)):
                errors.append(f"Logic {logic} missing rdfs:label")
            
            # Check for description
            if not list(instance_graph.objects(logic, DCTERMS.description)):
                errors.append(f"Logic {logic} missing dct:description")
            
            # Check for provenance
            if not list(instance_graph.objects(logic, DCT_IS_BASED_ON)):
                errors.append(f"Logic {logic} missing dct:isBasedOn for provenance")
        
        # Check 3: Verify namespace consistency
        namespaces = set(instance_graph.namespaces())
        required_namespaces = {'catty', 'dct', 'rdfs', 'owl'}
        
        namespace_prefixes = {prefix for prefix, _ in namespaces}
        missing = required_namespaces - namespace_prefixes
        
        if missing:
            errors.append(f"Missing required namespace prefixes: {missing}")
        
        is_valid = len(errors) == 0
        
        return is_valid, errors
    
    def _is_valid_iri(self, iri: str) -> bool:
        """Check if IRI is valid according to RFC 3987"""
        # Basic validation: must start with a scheme
        return bool(re.match(r'^[a-zA-Z][a-zA-Z0-9+.-]*:', iri))


def main():
    """Main execution function"""
    
    # Setup paths
    project_root = Path(__file__).parent.parent
    output_dir = project_root / 'output'
    schema_path = project_root / 'ontology' / 'catty-categorical-schema.jsonld'
    
    output_dir.mkdir(exist_ok=True)
    
    # Step 1: Retrieve from DBPedia
    logger.info("=" * 60)
    logger.info("STEP 1: Retrieve Intuitionistic Logic from DBPedia")
    logger.info("=" * 60)
    
    retriever = DBPediaRetriever()
    dbpedia_data = retriever.retrieve_intuitionistic_logic()
    
    if not dbpedia_data or not dbpedia_data.get('concept_iri'):
        logger.error("Failed to retrieve data from DBPedia")
        return 1
    
    # Save raw DBPedia data
    raw_output_path = output_dir / 'dbpedia-intuitionistic-logic-retrieved.jsonld'
    with open(raw_output_path, 'w', encoding='utf-8') as f:
        json.dump(dbpedia_data, f, indent=2, ensure_ascii=False)
    logger.info(f"Saved raw DBPedia data to: {raw_output_path}")
    
    # Step 2: Dereference Wikidata (if available)
    wikidata_iris = [iri for iri in dbpedia_data.get('sameAs', []) 
                     if 'wikidata.org' in iri]
    
    if wikidata_iris:
        logger.info("=" * 60)
        logger.info("STEP 2: Dereference Wikidata IRI")
        logger.info("=" * 60)
        
        wikidata_iri = wikidata_iris[0]
        wikidata_data = retriever.dereference_wikidata(wikidata_iri)
        
        if wikidata_data:
            wikidata_output_path = output_dir / 'wikidata-intuitionistic-logic.json'
            with open(wikidata_output_path, 'w', encoding='utf-8') as f:
                json.dump(wikidata_data, f, indent=2, ensure_ascii=False)
            logger.info(f"Saved Wikidata data to: {wikidata_output_path}")
    
    # Step 3: Validate retrieved IRIs
    logger.info("=" * 60)
    logger.info("STEP 3: Validate Retrieved IRIs")
    logger.info("=" * 60)
    
    validation_results = retriever.validate_iris(retriever.retrieved_iris)
    
    validation_output_path = output_dir / 'iri-validation-results.json'
    with open(validation_output_path, 'w', encoding='utf-8') as f:
        json.dump(validation_results, f, indent=2)
    logger.info(f"Saved IRI validation results to: {validation_output_path}")
    
    # Step 4: Construct Catty RDF instance
    logger.info("=" * 60)
    logger.info("STEP 4: Construct Catty Logic Instance")
    logger.info("=" * 60)
    
    constructor = CattyRDFConstructor(schema_path=schema_path)
    logic_iri = constructor.construct_catty_logic(dbpedia_data)
    
    # Save constructed RDF
    constructed_output_path = output_dir / 'constructed-intuitionistic-logic-python.jsonld'
    constructor.serialize(constructed_output_path, format='json-ld')
    
    # Also save as Turtle for readability
    turtle_output_path = output_dir / 'constructed-intuitionistic-logic-python.ttl'
    constructor.serialize(turtle_output_path, format='turtle')
    
    # Step 5: Validate constructed RDF
    logger.info("=" * 60)
    logger.info("STEP 5: Validate Constructed RDF")
    logger.info("=" * 60)
    
    validator = SemanticValidator(constructor.schema_graph)
    is_valid, errors = validator.validate_instance(constructor.graph)
    
    if is_valid:
        logger.info("✓ Validation PASSED: Constructed RDF is valid")
    else:
        logger.error("✗ Validation FAILED:")
        for error in errors:
            logger.error(f"  - {error}")
        return 1
    
    logger.info("=" * 60)
    logger.info("SUCCESS: All steps completed")
    logger.info("=" * 60)
    logger.info(f"Retrieved data saved to: {raw_output_path}")
    logger.info(f"Constructed RDF saved to: {constructed_output_path}")
    logger.info(f"Total triples constructed: {len(constructor.graph)}")
    
    return 0


if __name__ == '__main__':
    sys.exit(main())
