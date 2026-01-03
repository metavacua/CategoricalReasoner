import os
import sys
import time
try:
    from rdflib import Graph
    import owlrl
except ImportError:
    print("rdflib or owlrl not found. Please install them with 'pip install rdflib owlrl'")
    sys.exit(1)

def run_benchmarks():
    # Load all ontology files
    g = Graph()
    ontology_dir = "ontology"
    for filename in os.listdir(ontology_dir):
        if filename.endswith(".jsonld"):
            g.parse(os.path.join(ontology_dir, filename), format="json-ld")
        elif filename.endswith(".ttl"):
            g.parse(os.path.join(ontology_dir, filename), format="turtle")
    
    print(f"Loaded {len(g)} triples. Expanding with RDFS...")
    owlrl.DeductiveClosure(owlrl.RDFS_Semantics).expand(g)
    print(f"After expansion: {len(g)} triples.")

    query_dir = "benchmarks/queries"
    results_dir = "results"
    os.makedirs(results_dir, exist_ok=True)
    
    all_success = True
    for query_file in os.listdir(query_dir):
        if query_file.endswith(".rq"):
            print(f"Running query: {query_file}")
            with open(os.path.join(query_dir, query_file), "r") as f:
                query_str = f.read()
            
            start_time = time.time()
            try:
                results = g.query(query_str)
                duration = time.time() - start_time
                print(f"✅ {query_file} succeeded in {duration:.4f}s. Rows: {len(results)}")
                
                # Save results to CSV
                output_path = os.path.join(results_dir, query_file.replace(".rq", ".csv"))
                with open(output_path, "w") as out:
                    # Write header
                    out.write(",".join([str(v) for v in results.vars]) + "\n")
                    for row in results:
                        out.write(",".join([str(v) for v in row]) + "\n")
                        
            except Exception as e:
                print(f"❌ {query_file} failed: {e}")
                all_success = False
                
    return all_success

if __name__ == "__main__":
    if run_benchmarks():
        sys.exit(0)
    else:
        sys.exit(1)
