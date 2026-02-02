import os
import re
import json

PROJECT_ROOT = "/home/engine/project"
OUTPUT_FILE = "/home/engine/project/qid_audit/discovered_qids.json"

# Regex to find wd:Q...
# The prompt asks for "wd:Q[0-9]+"
# We should capture the QID part specifically.
QID_PATTERN = re.compile(r"wd:(Q\d+)")

def discover_qids():
    discovered_map = {} # QID -> { occurrences: [] }
    
    print(f"Scanning {PROJECT_ROOT} for QIDs...")
    
    for root, dirs, files in os.walk(PROJECT_ROOT):
        # Skip hidden directories and the audit directory itself to avoid loops if re-running
        dirs[:] = [d for d in dirs if not d.startswith('.') and d != 'qid_audit']
        
        for file in files:
            # Skip hidden files
            if file.startswith('.'):
                continue
                
            file_path = os.path.join(root, file)
            rel_path = os.path.relpath(file_path, PROJECT_ROOT)
            
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    lines = f.readlines()
                    
                for i, line in enumerate(lines):
                    matches = QID_PATTERN.findall(line)
                    for qid in matches:
                        # qid is "Q12345"
                        
                        if qid not in discovered_map:
                            discovered_map[qid] = {
                                "qid": qid,
                                "occurrences": []
                            }
                        
                        # Get context (trim whitespace)
                        context = line.strip()
                        
                        discovered_map[qid]["occurrences"].append({
                            "file": rel_path,
                            "line": i + 1,
                            "context": context
                        })
                        
            except Exception as e:
                print(f"Error reading {file_path}: {e}")

    # Convert map to list for output format
    discovered_list = list(discovered_map.values())
    
    output = {
        "total_unique_qids": len(discovered_list),
        "discovered_qids": discovered_list
    }
    
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(output, f, indent=2)
        
    print(f"Discovery complete. Found {len(discovered_list)} unique QIDs.")
    print(f"Saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    discover_qids()
