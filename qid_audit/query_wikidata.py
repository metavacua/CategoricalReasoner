import json
import urllib.request
import urllib.parse
import os
import sys

DISCOVERY_FILE = "/home/engine/project/qid_audit/discovered_qids.json"
OUTPUT_FILE = "/home/engine/project/qid_audit/wikidata_results.json"
WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql"

def query_wikidata():
    # 1. Load Discovered QIDs
    try:
        with open(DISCOVERY_FILE, 'r') as f:
            data = json.load(f)
            qids = [item['qid'] for item in data['discovered_qids']]
    except FileNotFoundError:
        print(f"Error: {DISCOVERY_FILE} not found. Run discovery first.")
        sys.exit(1)
        
    if not qids:
        print("No QIDs to query.")
        return

    print(f"Querying Wikidata for {len(qids)} QIDs...")

    # 2. Build SPARQL Query
    # VALUES ?qid { wd:Q1 wd:Q2 ... }
    values_clause = " ".join([f"wd:{qid}" for qid in qids])
    
    sparql_query = f"""
    SELECT DISTINCT ?qid ?qidLabel ?description ?instanceOf ?instanceOfLabel WHERE {{
      VALUES ?qid {{ {values_clause} }}
      
      OPTIONAL {{ ?qid rdfs:label ?qidLabel . FILTER(LANG(?qidLabel) = "en") }}
      OPTIONAL {{ ?qid schema:description ?description . FILTER(LANG(?description) = "en") }}
      OPTIONAL {{ ?qid wdt:P31 ?instanceOf . 
                  OPTIONAL {{ ?instanceOf rdfs:label ?instanceOfLabel . FILTER(LANG(?instanceOfLabel) = "en") }}
      }}
    }}
    """
    
    # 3. Execute Query
    params = {
        "query": sparql_query,
        "format": "json"
    }
    
    # User-Agent is required by Wikidata Policy
    headers = {
        "User-Agent": "CattyThesisAuditBot/1.0 (internal audit tool)"
    }
    
    data_encoded = urllib.parse.urlencode(params).encode('utf-8')
    req = urllib.request.Request(WIKIDATA_ENDPOINT, data=data_encoded, headers=headers)
    
    try:
        with urllib.request.urlopen(req) as response:
            result_data = json.loads(response.read().decode('utf-8'))
            
        # 4. Save Results
        os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
        with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
            json.dump(result_data, f, indent=2)
            
        print(f"Query successful. Saved results to {OUTPUT_FILE}")
        
    except urllib.error.HTTPError as e:
        print(f"HTTP Error: {e.code} - {e.reason}")
        print(e.read().decode('utf-8'))
    except Exception as e:
        print(f"Error querying Wikidata: {e}")

if __name__ == "__main__":
    query_wikidata()
