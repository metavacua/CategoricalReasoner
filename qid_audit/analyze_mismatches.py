import json
import os
import re

DISCOVERY_FILE = "/home/engine/project/qid_audit/discovered_qids.json"
WIKIDATA_FILE = "/home/engine/project/qid_audit/wikidata_results.json"
OUTPUT_FILE = "/home/engine/project/qid_audit/mismatch_report.json"

def analyze():
    # Load data
    with open(DISCOVERY_FILE, 'r') as f:
        discovery = json.load(f)
        
    with open(WIKIDATA_FILE, 'r') as f:
        wikidata_raw = json.load(f)

    # Process Wikidata results into a lookup map
    # qid_url -> { label, description, instanceOf }
    wikidata_map = {}
    for binding in wikidata_raw['results']['bindings']:
        qid_url = binding['qid']['value']
        qid = qid_url.split('/')[-1]
        
        label = binding.get('qidLabel', {}).get('value', 'N/A')
        description = binding.get('description', {}).get('value', 'N/A')
        instance_of = binding.get('instanceOfLabel', {}).get('value', 'N/A')
        
        # There might be multiple rows per QID if multiple instanceOf.
        # We just take the first or aggregate.
        if qid not in wikidata_map:
            wikidata_map[qid] = {
                "label": label,
                "description": description,
                "instance_of": set()
            }
        
        if instance_of != 'N/A':
            wikidata_map[qid]["instance_of"].add(instance_of)

    # Analyze mismatches
    mismatches = []
    
    for item in discovery['discovered_qids']:
        qid = item['qid']
        occurrences = item['occurrences']
        
        wd_info = wikidata_map.get(qid)
        if not wd_info:
            print(f"Warning: No Wikidata info for {qid}")
            continue
            
        wd_label = wd_info['label']
        wd_desc = wd_info['description']
        wd_instance = ", ".join(wd_info['instance_of'])
        
        # Heuristic to detect repository usage intent
        # Look for text in context lines
        repo_usage_hints = []
        for occ in occurrences:
            context = occ['context']
            # Remove the QID itself
            context_clean = context.replace(f"wd:{qid}", "").replace(qid, "")
            # Remove punctuation / common syntax
            context_clean = re.sub(r"[#;:{}\\\"]", " ", context_clean)
            # Remove 'owl:sameAs', 'wdt:P...', etc
            context_clean = re.sub(r"\b\w+:\w+\b", " ", context_clean)
            
            clean_words = " ".join(context_clean.split())
            if len(clean_words) > 3:
                repo_usage_hints.append(clean_words)
        
        repo_usage_summary = " | ".join(set(repo_usage_hints))
        if not repo_usage_summary:
            repo_usage_summary = "Unknown (no clear context)"

        # Mismatch detection logic
        # If the label or description from Wikidata is totally absent in repo usage, flag it.
        # But here we see complete mismatches (Logic vs Hospital).
        
        # We will assume everything is a mismatch for now and let the user verify, 
        # but we can try to be smart.
        # If WD label is 'hospital' and repo usage says 'Category theory', it's a mismatch.
        
        # Let's just output everything where the WD label is not substring of repo usage (case insensitive)
        # and vice versa.
        
        is_mismatch = True
        if wd_label.lower() in repo_usage_summary.lower():
            is_mismatch = False
        
        # Special manual check for known bad mappings seen in exploration
        # (This is just for the report generation quality)
        
        mismatch_entry = {
            "qid": qid,
            "wikidata_label": wd_label,
            "wikidata_description": wd_desc,
            "wikidata_instance_of": wd_instance,
            "repository_usage_hints": repo_usage_summary,
            "occurrences": [f"{o['file']}:{o['line']}" for o in occurrences]
        }
        
        if is_mismatch:
            mismatch_entry["mismatch_type"] = "Semantic Contradiction"
            mismatch_entry["severity"] = "High"
            mismatch_entry["recommendation"] = f"Replace {qid} ({wd_label}) with correct Wikidata entity for '{repo_usage_summary}'."
            mismatches.append(mismatch_entry)
        else:
             # Even if it matches, check description
             pass

    report = {
        "total_qids_audited": len(discovery['discovered_qids']),
        "mismatches_found": len(mismatches),
        "mismatches": mismatches
    }
    
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, 'w') as f:
        json.dump(report, f, indent=2)
        
    print(f"Analysis complete. Found {len(mismatches)} mismatches.")
    print(f"Report saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    analyze()
