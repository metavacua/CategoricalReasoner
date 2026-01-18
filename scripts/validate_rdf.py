import os
import sys
try:
    from rdflib import Graph
except ImportError:
    print("rdflib not found. Please install it with 'pip install rdflib'")
    sys.exit(1)

def validate_rdf(directory):
    success = True

    # JSON-LD files use a remote context URL (localhost or GitHub Pages). Allow
    # validation to run offline by serving that context from the repo.
    import urllib.request
    from io import BytesIO
    from urllib.response import addinfourl
    from email.message import Message

    context_path = os.path.join(directory, "context.jsonld")
    if os.path.exists(context_path):
        context_url_map = {
            "http://localhost:8080/ontology/context.jsonld": context_path,
            "https://metavacua.github.io/CategoricalReasoner/ontology/context.jsonld": context_path,
        }

        real_urlopen = urllib.request.urlopen

        def mock_urlopen(url, *args, **kwargs):
            req_url = url.full_url if isinstance(url, urllib.request.Request) else str(url)
            if req_url in context_url_map:
                data = open(context_url_map[req_url], "rb").read()
                headers = Message()
                headers.add_header("Content-Type", "application/ld+json")
                return addinfourl(BytesIO(data), headers, req_url)
            return real_urlopen(url, *args, **kwargs)

        urllib.request.urlopen = mock_urlopen

    for filename in os.listdir(directory):
        if filename.endswith(".jsonld") or filename.endswith(".ttl"):
            path = os.path.join(directory, filename)
            g = Graph()
            try:
                # Determine format
                fmt = "json-ld" if filename.endswith(".jsonld") else "turtle"
                g.parse(path, format=fmt)
                print(f"✅ {filename} is valid RDF.")
            except Exception as e:
                print(f"❌ {filename} is invalid: {e}")
                success = False
    return success

if __name__ == "__main__":
    ontology_dir = "ontology"
    if validate_rdf(ontology_dir):
        sys.exit(0)
    else:
        sys.exit(1)
