#!/usr/bin/env python3
"""
extract_wikidata_logic.py
Extract properties from Wikidata JSON for logic entities.
Part of Catty Thesis - Categorical Foundations for Logics
"""

import json
import sys
from pathlib import Path


def load_wikidata_json(json_file: str) -> dict:
    """Load and parse Wikidata JSON export."""
    with open(json_file) as f:
        return json.load(f)


def extract_entity_info(entity_data: dict, entity_id: str) -> dict:
    """Extract key information from a Wikidata entity."""
    info = {
        'entity_id': entity_id,
        'labels': {},
        'descriptions': {},
        'aliases': [],
        'claims': {},
        'external_ids': {}
    }
    
    # Extract labels in all languages
    for lang, label_data in entity_data.get('labels', {}).items():
        info['labels'][lang] = label_data.get('value')
    
    # Extract descriptions
    for lang, desc_data in entity_data.get('descriptions', {}).items():
        info['descriptions'][lang] = desc_data.get('value')
    
    # Extract aliases
    for lang, alias_list in entity_data.get('aliases', {}).items():
        info['aliases'].extend([a.get('value') for a in alias_list])
    
    # Extract claims (properties)
    for prop_id, prop_values in entity_data.get('claims', {}).items():
        info['claims'][prop_id] = {
            'count': len(prop_values),
            'values': []
        }
        # Extract main snak value from each claim
        for claim in prop_values:
            mainsnak = claim.get('mainsnak', {})
            datavalue = mainsnak.get('datavalue', {})
            value = datavalue.get('value')
            info['claims'][prop_id]['values'].append(value)
    
    # Extract external identifiers
    for ext_id, ext_value in entity_data.get('identifiers', {}).items():
        info['external_ids'][ext_id] = ext_value
    
    return info


def format_property_name(prop_id: str) -> str:
    """Get human-readable property name for common Wikidata properties."""
    common_props = {
        'P31': 'instance of',
        'P279': 'subclass of',
        'P361': 'part of',
        'P1705': 'native label',
        'P373': 'commons category',
        'P646': 'freebase ID',
        'P227': 'GND ID',
        'P10283': 'OpenAlex ID',
        'P1051': 'PSB ID',
        'P1424': "topic's main category",
        'P1482': 'stack exchange tag',
        'P1552': 'described by source',
        'P156': 'followed by',
        'P2579': 'seen in',
        'P3123': 'philosophy portal ID',
        'P3235': 'swedish protected areas ID',
        'P3417': 'Quora topic ID',
        'P4215': 'dlshub topic ID',
        'P6366': 'researchgate ID',
        'P8408': 'kbpedia ID',
        'P910': "topic's main category",
        'P9545': 'Biodiversity Heritage Library work ID',
        'P9621': 'trecID',
        'P1269': 'facet of',
        'P1343': 'described by',
        'P1417': 'Encyclopædia Britannica Online ID',
        'P1889': 'different from',
        'P2812': 'semantic scholar ID',
        'P3827': ' JSTOR topic ID',
        'P6104': 'storey',
        'P6900': 'archinform ID',
        'P7726': 'ioss crossref ID',
        'P8313': 'persée article ID',
    }
    return common_props.get(prop_id, f'property {prop_id}')


def print_entity_summary(info: dict) -> None:
    """Print formatted summary of extracted entity."""
    print("=" * 70)
    print(f"Wikidata Entity: {info['entity_id']}")
    print("=" * 70)
    
    # Labels
    en_label = info['labels'].get('en', 'N/A')
    print(f"\nLabel (en): {en_label}")
    
    # Description
    en_desc = info['descriptions'].get('en', 'N/A')
    print(f"Description (en): {en_desc}")
    
    # External IDs
    if info['external_ids']:
        print(f"\nExternal IDs ({len(info['external_ids'])}):")
        for ext_id, value in info['external_ids'].items():
            print(f"  {ext_id}: {value}")
    
    # Claims summary
    print(f"\nClaims ({len(info['claims'])} properties):")
    for prop_id in sorted(info['claims'].keys()):
        prop_info = info['claims'][prop_id]
        prop_name = format_property_name(prop_id)
        count = prop_info['count']
        print(f"  {prop_id} ({prop_name}): {count} value(s)")


def extract_for_catty(info: dict) -> dict:
    """
    Extract properties specifically relevant for Catty thesis.
    Returns structured data for use in formal specifications.
    """
    catty_data = {
        'wikidata_id': info['entity_id'],
        'label': info['labels'].get('en', ''),
        'description': info['descriptions'].get('en', ''),
        'related_logics': [],
        'external_references': {}
    }
    
    # Extract subclass relationships (P279) - indicates logical system hierarchy
    if 'P279' in info['claims']:
        for value in info['claims']['P279']['values']:
            if isinstance(value, dict) and 'id' in value:
                catty_data['related_logics'].append({
                    'type': 'subclass_of',
                    'wikidata_id': value['id']
                })
    
    # Extract part of relationships (P361)
    if 'P361' in info['claims']:
        for value in info['claims']['P361']['values']:
            if isinstance(value, dict) and 'id' in value:
                catty_data['related_logics'].append({
                    'type': 'part_of',
                    'wikidata_id': value['id']
                })
    
    # Extract external references (GND, Freebase, etc.)
    if info['external_ids']:
        catty_data['external_references'] = info['external_ids']
    
    # Store Freebase ID for cross-referencing
    if 'P646' in info['claims'] and info['claims']['P646']['values']:
        value = info['claims']['P646']['values'][0]
        if isinstance(value, str):
            catty_data['external_references']['freebase'] = value
    
    # Store OpenAlex ID if available
    if 'P10283' in info['claims'] and info['claims']['P10283']['values']:
        value = info['claims']['P10283']['values'][0]
        if isinstance(value, str):
            catty_data['external_references']['openalex'] = value
    
    return catty_data


def main(json_file: str, output_json: str = None) -> int:
    """Main entry point."""
    print(f"\nExtracting Wikidata data from: {json_file}")
    
    # Load data
    data = load_wikidata_json(json_file)
    
    # Process first entity found
    entity_id = list(data.get('entities', {}).keys())[0]
    entity_data = data['entities'][entity_id]
    
    # Extract info
    info = extract_entity_info(entity_data, entity_id)
    
    # Print summary
    print_entity_summary(info)
    
    # Extract Catty-relevant data
    catty_data = extract_for_catty(info)
    
    print("\n" + "=" * 70)
    print("Catty-Relevant Data:")
    print("=" * 70)
    print(json.dumps(catty_data, indent=2))
    
    # Save if output specified
    if output_json:
        with open(output_json, 'w') as f:
            json.dump(catty_data, f, indent=2)
        print(f"\nCatty data saved to: {output_json}")
    
    return 0


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: extract_wikidata_logic.py <wikidata_json_file> [output_json]")
        print("  Extracts logic properties from Wikidata JSON export.")
        sys.exit(1)
    
    json_file = sys.argv[1]
    output_json = sys.argv[2] if len(sys.argv) > 2 else None
    
    sys.exit(main(json_file, output_json))
