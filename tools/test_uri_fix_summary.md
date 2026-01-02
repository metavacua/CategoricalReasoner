# URI Fix Summary for Issue #8

## Problem Statement

The Catty-specific ontologies currently use an invalid URI:
```
http://catty.org/ontology/
```

This domain does not exist, making the ontology non-resolvable and preventing proper semantic web integration.

## Solution

Update all ontology files to use the valid GitHub Pages URI:
```
https://metavacua.github.io/CategoricalReasoner/ontology/
```

## Files Requiring Updates

### 1. ontology/catty-categorical-schema.jsonld
**Line 4:**
```diff
-    "catty": "http://catty.org/ontology/",
+    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### 2. ontology/catty-complete-example.jsonld
**Line 4:**
```diff
-    "catty": "http://catty.org/ontology/",
+    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### 3. ontology/curry-howard-categorical-model.jsonld
**Line 4:**
```diff
-    "catty": "http://catty.org/ontology/",
+    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### 4. ontology/logics-as-objects.jsonld
**Line 4:**
```diff
-    "catty": "http://catty.org/ontology/",
+    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### 5. ontology/morphism-catalog.jsonld
**Line 4:**
```diff
-    "catty": "http://catty.org/ontology/",
+    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### 6. ontology/two-d-lattice-category.jsonld
**Line 4:**
```diff
-    "catty": "http://catty.org/ontology/",
+    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### 7. ontology/catty-shapes.ttl
**Line 6:**
```diff
-@prefix catty: <http://catty.org/ontology/> .
+@prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology/> .
```

### 8. ontology/queries/sparql-examples.md
**Multiple lines (13, 29, 44, 59, 78, 95, 110, 125, 142, 168, 187):**
```diff
-PREFIX catty: <http://catty.org/ontology/>
+PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>
```

## Implementation Methods

### Method 1: Manual Edit
Open each file and replace the URI on the specified line(s).

### Method 2: Find and Replace (Recommended)
Use your editor's find-and-replace feature:
- Find: `http://catty.org/ontology/`
- Replace: `https://metavacua.github.io/CategoricalReasoner/ontology/`
- Scope: All files in `ontology/` directory

### Method 3: Command Line (Unix/Linux/Mac)
```bash
# From repository root
find ontology/ -type f \( -name '*.jsonld' -o -name '*.ttl' -o -name '*.md' \) \
  -exec sed -i 's|http://catty.org/ontology/|https://metavacua.github.io/CategoricalReasoner/ontology/|g' {} +
```

For macOS (BSD sed):
```bash
find ontology/ -type f \( -name '*.jsonld' -o -name '*.ttl' -o -name '*.md' \) \
  -exec sed -i '' 's|http://catty.org/ontology/|https://metavacua.github.io/CategoricalReasoner/ontology/|g' {} +
```

### Method 4: Python Script
```python
#!/usr/bin/env python3
from pathlib import Path

OLD_URI = "http://catty.org/ontology/"
NEW_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

ontology_dir = Path("ontology")
for filepath in ontology_dir.rglob("*"):
    if filepath.suffix in ['.jsonld', '.ttl', '.md']:
        content = filepath.read_text()
        if OLD_URI in content:
            new_content = content.replace(OLD_URI, NEW_URI)
            filepath.write_text(new_content)
            print(f"Updated: {filepath}")
```

## Verification

After making the changes, verify them by running:

```bash
python3 tools/test_ontology_uris.py
```

This test will:
- Check all ontology files for the correct URI
- Report any files still using the invalid URI
- Provide specific line numbers and fixes if issues remain

Expected output after successful fix:
```
✅ SUCCESS: All ontology files use the correct GitHub Pages URI!
```

## Benefits of This Fix

1. **Resolvable URIs**: The GitHub Pages URL is a valid, publicly accessible endpoint
2. **Semantic Web Compliance**: Enables proper linked data integration
3. **End-to-End Testing**: Allows validation of semantic web technologies as requested
4. **Standards Compliance**: Follows best practices for ontology publishing
5. **Future-Proof**: GitHub Pages provides stable, long-term hosting

## Related Documentation Updates

After fixing the ontology files, consider updating:
- README.md (if it references the old URI)
- Documentation in thesis/ (if applicable)
- Any external references or citations

## Testing Checklist

- [ ] All JSON-LD files updated
- [ ] Turtle file updated
- [ ] SPARQL examples updated
- [ ] Test script passes
- [ ] RDF validation passes (`python3 tools/validate-rdf.py --all`)
- [ ] Files can be loaded into a triplestore
- [ ] URIs resolve correctly (after GitHub Pages deployment)

## Notes

- The old URI `http://catty.org/ontology/` was never a valid domain
- The new URI `https://metavacua.github.io/CategoricalReasoner/ontology/` aligns with the GitHub Pages deployment
- This change is backward-compatible as the old URI was never functional
- No external systems should be affected as the old URI was not resolvable
