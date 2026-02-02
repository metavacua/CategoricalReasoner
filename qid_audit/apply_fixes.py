import os
import json

PROJECT_ROOT = "/home/engine/project"

# Old QID -> New QID
REPLACEMENTS = {
    "Q192960": "Q236975",   # Classical logic (Baibars -> Classical logic)
    "Q211231": "Q176786",   # Intuitionistic logic (Football -> Intuitionistic logic)
    "Q1149560": "Q841728",  # Linear logic (Right fielder -> Linear logic)
    "Q16917": "Q719395",    # Category (Hospital -> Category)
    "Q1370384": "Q864475",  # Functor (Musician -> Functor)
    "Q568825": "Q1442189",  # Natural transformation (Crypto attack -> Natural transformation)
    "Q846544": "Q357858",   # Adjoint functors (Disaster film -> Adjoint functors)
    "Q8462": "Q8078",       # Logic (Timur -> Logic)
    "Q7680564": "Q176786",  # LJ (Award -> Intuitionistic logic) - LJ is the sequent calculus for Int. Logic
    "Q847878": "Q841728"    # Linear Logic (TV Series -> Linear logic)
}

def apply_fixes():
    # Load discovered QIDs to know which files to touch
    with open(f"{PROJECT_ROOT}/qid_audit/discovered_qids.json", "r") as f:
        discovery = json.load(f)
        
    files_to_edit = set()
    for item in discovery["discovered_qids"]:
        if item["qid"] in REPLACEMENTS:
            for occ in item["occurrences"]:
                files_to_edit.add(occ["file"])
                
    print(f"Applying fixes to {len(files_to_edit)} files...")
    
    for rel_path in files_to_edit:
        file_path = os.path.join(PROJECT_ROOT, rel_path)
        print(f"  Editing {rel_path}...")
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            new_content = content
            for old_qid, new_qid in REPLACEMENTS.items():
                # Simple string replacement for "wd:OLD" -> "wd:NEW"
                # and just "OLD" if it appears as text? No, safer to target "wd:OLD" 
                # or "Q..." if strictly formatted.
                # The audit showed they appear as "wd:Q..." mostly.
                # But LaTeX might have \texttt{Q...}.
                
                # Replace wd:Qxxxx
                new_content = new_content.replace(f"wd:{old_qid}", f"wd:{new_qid}")
                
                # Replace Qxxxx (without wd:, usually in comments or latex text)
                # Be careful not to replace substrings of other QIDs (e.g. Q1 vs Q12)
                # But here the QIDs are specific.
                # Using regex word boundary is safer.
                import re
                new_content = re.sub(f"\\b{old_qid}\\b", new_qid, new_content)

            if new_content != content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"    Updated.")
            else:
                print(f"    No changes made (patterns might not have matched).")
                
        except Exception as e:
            print(f"Error editing {file_path}: {e}")

    # Save the mapping for documentation
    with open(f"{PROJECT_ROOT}/qid_audit/corrected_qids.json", "w") as f:
        json.dump(REPLACEMENTS, f, indent=2)

if __name__ == "__main__":
    apply_fixes()
