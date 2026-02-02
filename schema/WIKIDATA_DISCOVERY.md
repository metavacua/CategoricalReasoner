# Wikidata Discovery Guide for Agents

## The Problem: Hallucinated QIDs
LLMs often "hallucinate" Wikidata QIDs (e.g., guessing that "Classical Logic" is `Q192960`, which is actually "Baibars"). This corrupts the semantic integrity of the knowledge graph.

**RULE**: Never guess a QID. Never use a QID from your training data unless you verify it first.

## The Solution: Dynamic Discovery
Agents must query Wikidata to find the authoritative QID for a concept.

### 1. Verification (If you have a QID)
If you think "Classical Logic" is `Q123`, check it:
```sparql
SELECT ?label ?description WHERE {
  wd:Q123 rdfs:label ?label .
  wd:Q123 schema:description ?description .
  FILTER(LANG(?label) = "en")
}
```

### 2. Discovery (If you have a Label)
To find the QID for "Natural transformation":

```sparql
SELECT DISTINCT ?item ?label ?description WHERE {
  ?item rdfs:label ?label .
  ?item schema:description ?description .
  FILTER(STR(?label) = "Natural transformation")
  FILTER(LANG(?label) = "en")
  FILTER(LANG(?description) = "en")
}
LIMIT 5
```
*Note: This simple query can be slow. Using the Wikidata Search API (`wbsearchentities`) is often more efficient for name resolution.*

### 3. Recommended Python Pattern (No Dependencies)
Use this standard pattern to look up QIDs in your scripts:

```python
import json
import urllib.request
import urllib.parse

def find_qid(search_term):
    url = "https://www.wikidata.org/w/api.php"
    params = {
        "action": "wbsearchentities",
        "search": search_term,
        "language": "en",
        "format": "json"
    }
    qs = urllib.parse.urlencode(params)
    req = urllib.request.Request(f"{url}?{qs}", headers={"User-Agent": "CattyAgent/1.0"})
    
    with urllib.request.urlopen(req) as r:
        data = json.loads(r.read())
        
    for item in data.get("search", []):
        print(f"{item['id']} : {item.get('label')} ({item.get('description')})")
```

## Core Domain Registry
The following QIDs have been manually verified for the Catty Thesis domain. **Prefer using these QIDs.**

| Concept | QID | Description |
|:--- |:--- |:--- |
| **Category Theory** | `wd:Q217413` | Branch of mathematics |
| **Category** | `wd:Q719395` | Algebraic structure of objects and morphisms |
| **Functor** | `wd:Q864475` | Mapping between categories |
| **Natural Transformation** | `wd:Q1442189` | Transformation between functors |
| **Adjoint Functors** | `wd:Q357858` | Relationship between functors |
| **Logic** | `wd:Q8078` | Study of correct reasoning |
| **Classical Logic** | `wd:Q236975` | Class of formal logics |
| **Intuitionistic Logic** | `wd:Q176786` | Logic lacking law of excluded middle |
| **Linear Logic** | `wd:Q841728` | Resource-aware logic |
| **Sequent Calculus** | `wd:Q1771121` | Style of formal argumentation |
| **Type Theory** | `wd:Q1056428` | Mathematical logic and CS concept |
| **Curry-Howard Correspondence** | `wd:Q975734` | Relationship between proofs and programs |
| **Programming Language** | `wd:Q9143` | Language for machine instructions |
| **Software Development** | `wd:Q638608` | Creation of software |

## Usage in Repository
When referencing these concepts in RDF/TTL/OWL:
```ttl
@prefix wd: <http://www.wikidata.org/entity/> .

catty:Logic_Module
    rdfs:seeAlso wd:Q236975 ;  # Classical logic
    rdfs:seeAlso wd:Q176786 .  # Intuitionistic logic
```
