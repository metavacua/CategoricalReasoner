import json
import urllib.request
import urllib.parse
import time

TERMS = [
    "Category",
    "LJ",
    "Gentzen LJ",
    "Sequent calculus",
    "Monad"
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
        results[term] = []
        for m in matches:
             results[term].append({
                "qid": m['id'],
                "label": m.get('label'),
                "description": m.get('description', 'No description')
            })
        time.sleep(1)

    print(json.dumps(results, indent=2))

if __name__ == "__main__":
    main()
