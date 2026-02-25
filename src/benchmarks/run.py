import os
import sys
import time
import argparse
import requests
from rdflib import Graph

def run_benchmarks(endpoint=None, query_file=None, output_dir="results", verbose=False, timeout=60):
    query_dir = "src/benchmarks/queries"
    os.makedirs(output_dir, exist_ok=True)
    
    if query_file:
        if os.path.exists(query_file):
            queries = [query_file]
        elif os.path.exists(os.path.join(query_dir, query_file)):
            queries = [os.path.join(query_dir, query_file)]
        else:
            print(f"Query file not found: {query_file}")
            return False
    else:
        queries = [os.path.join(query_dir, f) for f in os.listdir(query_dir) if f.endswith(".rq") or f.endswith(".sparql")]
    
    all_success = True

    # Load local graph once if needed
    g = None
    if not endpoint:
        g = Graph()
        ontology_dir = "src/ontology"
        if os.path.exists(ontology_dir):
            for filename in os.listdir(ontology_dir):
                path = os.path.join(ontology_dir, filename)
                if filename.endswith(".jsonld"):
                    g.parse(path, format="json-ld")
                elif filename.endswith(".ttl"):
                    g.parse(path, format="turtle")

    for q_path in queries:
        q_file = os.path.basename(q_path)
        print(f"Running query: {q_file}")
        with open(q_path, "r") as f:
            query_str = f.read()
        
        start_time = time.time()
        try:
            is_construct = "CONSTRUCT" in query_str.upper() or "DESCRIBE" in query_str.upper()
            
            if endpoint:
                # Use requests for remote SPARQL to have better control over headers
                headers = {
                    "User-Agent": "CattyThesisAgent/1.0 (https://github.com/metavacua/CategoricalReasoner; mailto:admin@example.org)",
                    "Accept": "text/turtle" if is_construct else "application/sparql-results+json"
                }
                
                if verbose:
                    print(f"Querying {endpoint}...")
                
                response = requests.post(endpoint, data={"query": query_str}, headers=headers, timeout=timeout)
                response.raise_for_status()

                duration = time.time() - start_time

                if is_construct:
                    graph = Graph()
                    graph.parse(data=response.text, format="turtle")
                    triple_count = len(graph)
                    if triple_count == 0:
                        print(f"❌ {q_file} (CONSTRUCT) returned 0 triples in {duration:.4f}s.")
                        all_success = False
                        continue
                    print(f"✅ {q_file} (CONSTRUCT) succeeded in {duration:.4f}s. Triples: {triple_count}")
                    output_path = os.path.join(output_dir, q_file.replace(".rq", ".ttl").replace(".sparql", ".ttl"))
                    graph.serialize(destination=output_path, format="turtle")
                    print(f"   Results saved to {output_path}")
                else:
                    data = response.json()
                    row_count = len(data.get("results", {}).get("bindings", []))
                    if row_count == 0:
                        print(f"❌ {q_file} (SELECT) returned 0 rows in {duration:.4f}s.")
                        all_success = False
                        continue
                    print(f"✅ {q_file} (SELECT) succeeded in {duration:.4f}s. Rows: {row_count}")
                    output_path = os.path.join(output_dir, q_file.replace(".rq", ".csv").replace(".sparql", ".csv"))
                    # Very simple CSV conversion for demonstration
                    with open(output_path, "w") as out:
                        vars = data.get("head", {}).get("vars", [])
                        out.write(",".join(vars) + "\n")
                        for binding in data.get("results", {}).get("bindings", []):
                            row = [binding.get(v, {}).get("value", "") for v in vars]
                            out.write(",".join([f'"{r}"' for r in row]) + "\n")
                    print(f"   Results saved to {output_path}")
            else:
                # Local query
                results = g.query(query_str)
                duration = time.time() - start_time
                
                if is_construct:
                    graph = results.graph if hasattr(results, "graph") and results.graph is not None else results
                    triple_count = len(graph)
                    if triple_count == 0:
                        print(f"❌ {q_file} (CONSTRUCT) returned 0 triples in {duration:.4f}s.")
                        all_success = False
                        continue
                    print(f"✅ {q_file} (CONSTRUCT) succeeded in {duration:.4f}s. Triples: {triple_count}")
                    output_path = os.path.join(output_dir, q_file.replace(".rq", ".ttl").replace(".sparql", ".ttl"))
                    graph.serialize(destination=output_path, format="turtle")
                else:
                    row_count = len(results)
                    if row_count == 0:
                        print(f"❌ {q_file} (SELECT) returned 0 rows in {duration:.4f}s.")
                        all_success = False
                        continue
                    print(f"✅ {q_file} (SELECT) succeeded in {duration:.4f}s. Rows: {row_count}")
                    output_path = os.path.join(output_dir, q_file.replace(".rq", ".csv").replace(".sparql", ".csv"))
                    results.serialize(destination=output_path, format="csv")
                print(f"   Results saved to {output_path}")
                
        except Exception as e:
            print(f"❌ {q_file} failed: {e}")
            all_success = False
            
    return all_success

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run SPARQL benchmarks")
    parser.add_argument("--endpoint", help="Remote SPARQL endpoint URL")
    parser.add_argument("--query", help="Specific query file name or path")
    parser.add_argument("--output-dir", default="results", help="Directory to save results")
    parser.add_argument("--timeout", type=int, default=60, help="Timeout in seconds for remote SPARQL requests")
    parser.add_argument("--verbose", "-v", action="store_true", help="Verbose output")
    
    args = parser.parse_args()
    
    if run_benchmarks(endpoint=args.endpoint, query_file=args.query, output_dir=args.output_dir, verbose=args.verbose, timeout=args.timeout):
        sys.exit(0)
    else:
        sys.exit(1)
