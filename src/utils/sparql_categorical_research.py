#!/usr/bin/env python3
"""
SPARQL Categorical Research Tool

Queries Wikidata and DBpedia SPARQL endpoints for categorical definitions,
theorems, and axioms related to structural rules in logic.

Adheres to Wikidata User Agent Policy and implements rate limiting.
"""

import json
import time
import logging
from typing import Dict, List, Optional, Any
from dataclasses import dataclass, asdict
from urllib.parse import urlencode
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError
import ssl

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


@dataclass
class SPARQLEndpoint:
    """Configuration for a SPARQL endpoint."""
    name: str
    url: str
    user_agent: str
    rate_limit_delay: float  # seconds between requests
    timeout: int = 30


@dataclass
class QueryResult:
    """Result from a SPARQL query."""
    endpoint: str
    query_name: str
    data: List[Dict[str, Any]]
    bindings: List[str]
    timestamp: float
    source_url: Optional[str] = None


class WikidataUserAgentPolicy:
    """
    Implements Wikidata User Agent Policy.
    https://meta.wikimedia.org/wiki/User-Agent_policy
    """
    
    PROJECT_NAME = "CategoricalReasoner"
    PROJECT_VERSION = "1.0"
    PROJECT_URL = "https://github.com/metavacua/CategoricalReasoner"
    
    @classmethod
    def get_user_agent(cls, tool_name: str) -> str:
        """Generate compliant User-Agent string."""
        return (
            f"{cls.PROJECT_NAME}/{cls.PROJECT_VERSION} "
            f"({cls.PROJECT_URL}; research@metavacua.org) "
            f"{tool_name}/1.0"
        )


class RateLimiter:
    """Simple rate limiter for API requests."""
    
    def __init__(self, min_delay: float = 1.0):
        self.min_delay = min_delay
        self.last_request_time: Dict[str, float] = {}
    
    def wait_if_needed(self, endpoint_name: str):
        """Wait if necessary to respect rate limit."""
        if endpoint_name in self.last_request_time:
            elapsed = time.time() - self.last_request_time[endpoint_name]
            if elapsed < self.min_delay:
                wait_time = self.min_delay - elapsed
                logger.debug(f"Rate limiting: waiting {wait_time:.2f}s for {endpoint_name}")
                time.sleep(wait_time)
        self.last_request_time[endpoint_name] = time.time()


class SPARQLClient:
    """Client for querying SPARQL endpoints with rate limiting and error handling."""
    
    def __init__(self, rate_limit_delay: float = 1.0):
        self.rate_limiter = RateLimiter(rate_limit_delay)
        self.endpoints: Dict[str, SPARQLEndpoint] = {}
        self._setup_endpoints()
        
        # SSL context that allows us to connect even with self-signed certs
        self.ssl_context = ssl.create_default_context()
        self.ssl_context.check_hostname = False
        self.ssl_context.verify_mode = ssl.CERT_NONE
    
    def _setup_endpoints(self):
        """Configure SPARQL endpoints."""
        # Wikidata
        self.endpoints['wikidata'] = SPARQLEndpoint(
            name='wikidata',
            url='https://query.wikidata.org/sparql',
            user_agent=WikidataUserAgentPolicy.get_user_agent('sparql-research'),
            rate_limit_delay=1.0,  # Be respectful to Wikidata
            timeout=30
        )
        
        # DBpedia
        self.endpoints['dbpedia'] = SPARQLEndpoint(
            name='dbpedia',
            url='https://dbpedia.org/sparql',
            user_agent=WikidataUserAgentPolicy.get_user_agent('sparql-research'),
            rate_limit_delay=0.5,
            timeout=30
        )
        
        # Additional endpoints discovered from Wikidata
        # These are encoded in the Wikidata graph
        self.endpoints['factgrid'] = SPARQLEndpoint(
            name='factgrid',
            url='https://database.factgrid.de/sparql',
            user_agent=WikidataUserAgentPolicy.get_user_agent('sparql-research'),
            rate_limit_delay=1.0,
            timeout=30
        )
        
        self.endpoints['isidore'] = SPARQLEndpoint(
            name='isidore',
            url='https://isidore.science/sparql',
            user_agent=WikidataUserAgentPolicy.get_user_agent('sparql-research'),
            rate_limit_delay=1.0,
            timeout=30
        )
    
    def query(self, endpoint_name: str, query: str, query_name: str = "") -> Optional[QueryResult]:
        """
        Execute a SPARQL query against an endpoint.
        
        Args:
            endpoint_name: Name of the endpoint to query
            query: SPARQL query string
            query_name: Descriptive name for the query
            
        Returns:
            QueryResult or None if query failed
        """
        if endpoint_name not in self.endpoints:
            logger.error(f"Unknown endpoint: {endpoint_name}")
            return None
        
        endpoint = self.endpoints[endpoint_name]
        
        # Apply rate limiting
        self.rate_limiter.wait_if_needed(endpoint_name)
        
        # Prepare request
        params = {'query': query, 'format': 'application/sparql-results+json'}
        url = f"{endpoint.url}?{urlencode(params)}"
        
        headers = {
            'User-Agent': endpoint.user_agent,
            'Accept': 'application/sparql-results+json'
        }
        
        req = Request(url, headers=headers)
        
        try:
            logger.info(f"Querying {endpoint_name}: {query_name or 'unnamed query'}")
            with urlopen(req, timeout=endpoint.timeout, context=self.ssl_context) as response:
                data = json.loads(response.read().decode('utf-8'))
                
                # Parse results
                bindings = data.get('results', {}).get('bindings', [])
                variables = data.get('head', {}).get('vars', [])
                
                # Convert bindings to list of dicts
                results = []
                for binding in bindings:
                    row = {}
                    for var in variables:
                        if var in binding:
                            row[var] = binding[var].get('value', '')
                    results.append(row)
                
                return QueryResult(
                    endpoint=endpoint_name,
                    query_name=query_name,
                    data=results,
                    bindings=variables,
                    timestamp=time.time(),
                    source_url=endpoint.url
                )
                
        except HTTPError as e:
            logger.error(f"HTTP error from {endpoint_name}: {e.code} {e.reason}")
            return None
        except URLError as e:
            logger.error(f"URL error from {endpoint_name}: {e.reason}")
            return None
        except Exception as e:
            logger.error(f"Error querying {endpoint_name}: {e}")
            return None
    
    def query_all_endpoints(self, query: str, query_name: str = "") -> List[QueryResult]:
        """Query all configured endpoints and return results."""
        results = []
        for endpoint_name in self.endpoints:
            result = self.query(endpoint_name, query, query_name)
            if result:
                results.append(result)
        return results


class DataConsistencyVerifier:
    """Verifies data consistency between sources."""
    
    @staticmethod
    def verify_concept_consistency(results: List[QueryResult], 
                                   concept_field: str = 'concept') -> Dict[str, Any]:
        """
        Check for consistency of concepts across sources.
        
        Returns dict with:
        - consistent_concepts: List of concepts found in all sources
        - inconsistent_concepts: List of concepts with conflicting data
        - source_coverage: Dict mapping concept to sources
        """
        concept_sources: Dict[str, set] = {}
        
        for result in results:
            for row in result.data:
                concept = row.get(concept_field, '')
                if concept:
                    if concept not in concept_sources:
                        concept_sources[concept] = set()
                    concept_sources[concept].add(result.endpoint)
        
        all_sources = {r.endpoint for r in results}
        
        consistent = []
        inconsistent = []
        
        for concept, sources in concept_sources.items():
            if sources == all_sources:
                consistent.append(concept)
            else:
                inconsistent.append({
                    'concept': concept,
                    'found_in': list(sources),
                    'missing_from': list(all_sources - sources)
                })
        
        return {
            'consistent_concepts': consistent,
            'inconsistent_concepts': inconsistent,
            'source_coverage': {k: list(v) for k, v in concept_sources.items()}
        }


class CategoricalResearchQueries:
    """Predefined queries for categorical logic research."""
    
    # Query: Find category theory concepts
    CATEGORY_THEORY_CONCEPTS = """
    SELECT DISTINCT ?concept ?conceptLabel ?description ?article
    WHERE {
        ?concept wdt:P31 wd:Q5 .
        ?concept schema:description ?description .
        FILTER(LANG(?description) = "en")
        FILTER(CONTAINS(LCASE(STR(?description)), "category theory") ||
               CONTAINS(LCASE(STR(?description)), "categorical"))
        SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
        OPTIONAL { ?article schema:about ?concept ; schema:isPartOf <https://en.wikipedia.org/>. }
    }
    LIMIT 50
    """
    
    # Query: Find logic-related theorems
    LOGIC_THEOREMS = """
    SELECT DISTINCT ?theorem ?theoremLabel ?description ?discoverer ?discovererLabel ?date
    WHERE {
        ?theorem wdt:P31 wd:Q65943 .
        ?theorem schema:description ?description .
        FILTER(LANG(?description) = "en")
        FILTER(CONTAINS(LCASE(STR(?description)), "logic") ||
               CONTAINS(LCASE(STR(?description)), "sequent") ||
               CONTAINS(LCASE(STR(?description)), "proof"))
        OPTIONAL { ?theorem wdt:P61 ?discoverer. }
        OPTIONAL { ?theorem wdt:P577 ?date. }
        SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
    }
    LIMIT 50
    """
    
    # Query: Structural rules in logic
    STRUCTURAL_RULES = """
    SELECT DISTINCT ?rule ?ruleLabel ?description ?mathProperty
    WHERE {
        ?rule wdt:P31 ?type .
        ?rule schema:description ?description .
        FILTER(LANG(?description) = "en")
        FILTER(CONTAINS(LCASE(STR(?description)), "weakening") ||
               CONTAINS(LCASE(STR(?description)), "contraction") ||
               CONTAINS(LCASE(STR(?description)), "exchange") ||
               CONTAINS(LCASE(STR(?description)), "structural rule"))
        OPTIONAL { ?rule wdt:P2534 ?mathProperty. }
        SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
    }
    LIMIT 30
    """
    
    # Query: Formal logic concepts
    FORMAL_LOGIC = """
    SELECT DISTINCT ?concept ?conceptLabel ?description ?instanceOf
    WHERE {
        ?concept wdt:P31 wd:Q13226421 .
        ?concept schema:description ?description .
        FILTER(LANG(?description) = "en")
        OPTIONAL { ?concept wdt:P31 ?instanceOf. }
        SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
    }
    LIMIT 50
    """
    
    # Query: Proof theory concepts
    PROOF_THEORY = """
    SELECT DISTINCT ?concept ?conceptLabel ?description
    WHERE {
        ?concept wdt:P31 ?type .
        ?concept schema:description ?description .
        FILTER(LANG(?description) = "en")
        FILTER(CONTAINS(LCASE(STR(?description)), "proof theory") ||
               CONTAINS(LCASE(STR(?description)), "sequent calculus") ||
               CONTAINS(LCASE(STR(?description)), "natural deduction"))
        SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
    }
    LIMIT 50
    """


def run_research_session(output_file: str = "categorical_research_results.json"):
    """
    Run a complete research session querying multiple endpoints.
    
    Args:
        output_file: Path to save results
    """
    client = SPARQLClient(rate_limit_delay=1.0)
    verifier = DataConsistencyVerifier()
    queries = CategoricalResearchQueries()
    
    all_results = []
    
    # Query definitions
    query_definitions = [
        ('category_theory', queries.CATEGORY_THEORY_CONCEPTS),
        ('logic_theorems', queries.LOGIC_THEOREMS),
        ('structural_rules', queries.STRUCTURAL_RULES),
        ('formal_logic', queries.FORMAL_LOGIC),
        ('proof_theory', queries.PROOF_THEORY),
    ]
    
    for query_name, query_sparql in query_definitions:
        logger.info(f"Running query: {query_name}")
        results = client.query_all_endpoints(query_sparql, query_name)
        
        for result in results:
            logger.info(f"  {result.endpoint}: {len(result.data)} results")
            all_results.append(asdict(result))
        
        # Verify consistency if we have multiple sources
        if len(results) > 1:
            consistency = verifier.verify_concept_consistency(results)
            logger.info(f"  Consistent concepts across sources: {len(consistency['consistent_concepts'])}")
    
    # Save results
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(all_results, f, indent=2, ensure_ascii=False)
    
    logger.info(f"Research session complete. Results saved to {output_file}")
    return all_results


def export_to_tex_fragment(results: List[Dict], output_file: str = "research_import.tex"):
    """
    Export research results to a TeX fragment for inclusion in monograph.
    
    Args:
        results: List of query results
        output_file: Path to output TeX file
    """
    tex_lines = [
        "% Auto-generated from SPARQL research",
        "% Generated: " + time.strftime("%Y-%m-%d %H:%M:%S"),
        "",
        "\\section{External Knowledge Base References}",
        "",
    ]
    
    for result in results:
        if not result.get('data'):
            continue
            
        tex_lines.append(f"\\subsection{{{result['query_name'].replace('_', ' ').title()}}}")
        tex_lines.append(f"Source: {result['endpoint']}")
        tex_lines.append("")
        
        # Create itemized list
        tex_lines.append("\\begin{itemize}")
        for row in result['data'][:10]:  # Limit to first 10 items
            label = row.get('conceptLabel', row.get('theoremLabel', 'Unknown'))
            desc = row.get('description', '')
            if label:
                tex_lines.append(f"  \\item \\textbf{{{label}}}: {desc}")
        tex_lines.append("\\end{itemize}")
        tex_lines.append("")
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(tex_lines))
    
    logger.info(f"TeX fragment exported to {output_file}")


if __name__ == "__main__":
    import sys
    
    if len(sys.argv) > 1 and sys.argv[1] == '--export-tex':
        # Load existing results and export to TeX
        try:
            with open("categorical_research_results.json", 'r') as f:
                results = json.load(f)
            export_to_tex_fragment(results)
        except FileNotFoundError:
            logger.error("No results file found. Run without --export-tex first.")
            sys.exit(1)
    else:
        # Run research session
        results = run_research_session()
        
        # Also export to TeX
        export_to_tex_fragment(results)
