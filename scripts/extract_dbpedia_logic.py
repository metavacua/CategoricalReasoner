#!/usr/bin/env python3
"""
extract_dbpedia_logic.py
Extract and analyze logic properties from DBPedia RDF Turtle files.
Part of Catty Thesis - Categorical Foundations for Logics
"""

import sys
from pathlib import Path

try:
    from rdflib import Graph, Namespace, RDF, RDFS, OWL, URIRef
    from rdflib.namespace import DCTERMS, SKOS, FOAF
except ImportError:
    print("Error: rdflib is required. Install with: pip install rdflib")
    sys.exit(1)


def load_rdf_file(turtle_file: str) -> Graph:
    """Load and parse RDF Turtle file."""
    g = Graph()
    g.parse(turtle_file, format="turtle")
    return g


def analyze_rdf_graph(g: Graph, logic_name: str) -> dict:
    """Analyze RDF graph and extract key information."""
    analysis = {
        'name': logic_name,
        'file': None,
        'total_triples': len(g),
        'subjects': set(),
        'predicates': set(),
        'objects': set(),
        'types': {},
        'external_links': {},
        'labels': {},
        'connections': []
    }
    
    # Analyze triples
    for s, p, o in g:
        analysis['subjects'].add(str(s))
        analysis['predicates'].add(str(p))
        analysis['objects'].add(str(o))
        
        # Track rdf:type relationships
        if p == RDF.type:
            obj_short = str(o).split('/')[-1] if '/' in str(o) else str(o)
            analysis['types'][obj_short] = analysis['types'].get(obj_short, 0) + 1
        
        # Track sameAs links (external references)
        if str(p).endswith('sameAs'):
            analysis['external_links']['sameAs'] = analysis['external_links'].get('sameAs', [])
            analysis['external_links']['sameAs'].append(str(o))
        
        # Track wikiPageWikiLink (connections to other pages)
        if 'wikiPageWikiLink' in str(p):
            obj_short = str(o).split('/')[-1] if '/' in str(o) else str(o)
            analysis['connections'].append(obj_short)
    
    # Get labels
    for s, p, o in g:
        if p == RDFS.label:
            lang = o.language if hasattr(o, 'language') and o.language else 'unknown'
            analysis['labels'][lang] = str(o)
    
    # Simplify sets for output
    analysis['subject_count'] = len(analysis['subjects'])
    analysis['predicate_count'] = len(analysis['predicates'])
    analysis['object_count'] = len(analysis['objects'])
    
    return analysis


def print_analysis(analysis: dict) -> None:
    """Print formatted analysis results."""
    print("=" * 70)
    print(f"DBPedia Analysis: {analysis['name']}")
    print("=" * 70)
    
    print(f"\nBasic Statistics:")
    print(f"  Total triples: {analysis['total_triples']}")
    print(f"  Unique subjects: {analysis['subject_count']}")
    print(f"  Unique predicates: {analysis['predicate_count']}")
    print(f"  Unique objects: {analysis['object_count']}")
    
    print(f"\nTypes (rdf:type):")
    for type_name, count in sorted(analysis['types'].items(), key=lambda x: -x[1]):
        print(f"  {type_name}: {count}")
    
    print(f"\nLabels:")
    for lang, label in sorted(analysis['labels'].items()):
        print(f"  [{lang}]: {label}")
    
    print(f"\nExternal Links (sameAs):")
    same_as = analysis['external_links'].get('sameAs', [])
    for link in same_as[:10]:  # Limit to first 10
        print(f"  {link}")
    if len(same_as) > 10:
        print(f"  ... and {len(same_as) - 10} more")
    
    print(f"\nConnections to other resources ({len(analysis['connections'])} total):")
    conn = analysis['connections'][:20]  # First 20
    for c in conn:
        print(f"  -> {c}")
    if len(analysis['connections']) > 20:
        print(f"  ... and {len(analysis['connections']) - 20} more")


def extract_for_catty(analysis: dict) -> dict:
    """
    Extract properties specifically relevant for Catty thesis.
    Returns structured data for use in formal specifications.
    """
    catty_data = {
        'source': 'dbpedia',
        'name': analysis['name'],
        'triples_count': analysis['total_triples'],
        'labels': analysis['labels'],
        'types': list(analysis['types'].keys()),
        'connections': [],
        'external_references': []
    }
    
    # Extract connections to logic-related resources
    logic_keywords = ['logic', 'proof', 'axiom', 'theorem', 'semantic', 'algebra']
    for conn in analysis['connections']:
        conn_lower = conn.lower()
        if any(kw in conn_lower for kw in logic_keywords):
            catty_data['connections'].append(conn)
    
    # Extract external references
    for link in analysis['external_links'].get('sameAs', []):
        if 'dbpedia.org' not in link and 'wikidata.org' not in link:
            catty_data['external_references'].append(str(link))
    
    return catty_data


def main(turtle_file: str, output_json: str = None) -> int:
    """Main entry point."""
    print(f"\nLoading RDF from: {turtle_file}")
    
    try:
        g = load_rdf_file(turtle_file)
    except Exception as e:
        print(f"Error loading RDF file: {e}")
        return 1
    
    logic_name = Path(turtle_file).stem.replace('_', ' ').title()
    
    # Analyze
    analysis = analyze_rdf_graph(g, logic_name)
    analysis['file'] = turtle_file
    
    # Print results
    print_analysis(analysis)
    
    # Extract Catty-relevant data
    catty_data = extract_for_catty(analysis)
    
    print("\n" + "=" * 70)
    print("Catty-Relevant Data:")
    print("=" * 70)
    import json
    print(json.dumps(catty_data, indent=2))
    
    # Save if output specified
    if output_json:
        with open(output_json, 'w') as f:
            json.dump(catty_data, f, indent=2)
        print(f"\nCatty data saved to: {output_json}")
    
    return 0


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: extract_dbpedia_logic.py <turtle_file> [output_json]")
        print("  Extracts logic properties from DBPedia RDF Turtle file.")
        print("  Requires: pip install rdflib")
        sys.exit(1)
    
    turtle_file = sys.argv[1]
    output_json = sys.argv[2] if len(sys.argv) > 2 else None
    
    sys.exit(main(turtle_file, output_json))
