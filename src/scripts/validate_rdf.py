import os
import sys
try:
    from rdflib import Graph
except ImportError:
    print("rdflib not found. Please install it with 'pip install rdflib'")
    sys.exit(1)

def validate_rdf(directory):
    success = True
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
    # Note: This project consumes semantic web data from external sources, not local ontologies
    # Local RDF files should be placed in docs/dissertation/bibliography/
    bibliography_dir = "docs/dissertation/bibliography"
    if validate_rdf(bibliography_dir):
        sys.exit(0)
    else:
        sys.exit(1)
