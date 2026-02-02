import json
import urllib.request
import urllib.parse
import time

TERMS = [
    "Classical logic",
    "Intuitionistic logic",
    "Linear logic",
    "Category theory",
    "Category (mathematics)",
    "Functor",
    "Natural transformation",
    "Adjoint functors",
    "Curry-Howard correspondence",
    "Lambek calculus",
    "Logic",
    "Programming language",
    "Software development",
    "Sequent calculus",
    "Cartesian closed category",
    "Monad (category theory)",
    "Type theory",
    "Homotopy type theory"
]

SEARCH_API = "https://www.wikidata.org/w/api.php"

def search_wikidata(term):
    params = {
        "action": "wbsearchentities",
        "search": term,
        "language": "en",
        "format": "json",
        "limit": 5
    }
    
    url = f"{SEARCH_API}?{urllib.parse.urlencode(params)}"
    req = urllib.request.Request(url, headers={"User-Agent": "CattyThesisAuditBot/1.0"})
    
    try:
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode('utf-8'))
            return data.get('search', [])
    except Exception as e:
        print(f"Error searching for {term}: {e}")
        return []

def main():
    results = {}
    print(f"Searching Wikidata for {len(TERMS)} terms...")
    
    for term in TERMS:
        print(f"  Searching: {term}")
        matches = search_wikidata(term)
        
        # Filter for relevant results (simple heuristic: prioritize exact matches or math/logic context)
        # For now, just store the top 3
        results[term] = []
        for m in matches[:3]:
            results[term].append({
                "qid": m['id'],
                "label": m.get('label'),
                "description": m.get('description', 'No description')
            })
        
        time.sleep(1) # Be nice to the API

    with open("/home/engine/project/qid_audit/candidate_replacements.json", "w") as f:
        json.dump(results, f, indent=2)
        
    print("Done. Saved candidates to qid_audit/candidate_replacements.json")

if __name__ == "__main__":
    main()
