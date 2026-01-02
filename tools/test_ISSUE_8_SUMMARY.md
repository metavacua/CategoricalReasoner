# Issue #8: Catty Specific Ontologies Have Invalid URI

## Summary

All Catty ontology files currently use an invalid URI that points to a non-existent domain. This needs to be fixed to use the valid GitHub Pages URL.

## Problem

**Invalid URI (current):**
```
http://catty.org/ontology/
```

This domain does not exist and the ontology cannot be resolved.

## Solution

**Valid URI (required):**
```
https://metavacua.github.io/CategoricalReasoner/ontology/
```

This is the correct GitHub Pages URL for the Categorical Reasoner project.

## Files Requiring Changes

The following 8 files need to be updated:

### JSON-LD Files (line 4 in each)
1. `ontology/catty-categorical-schema.jsonld`
2. `ontology/catty-complete-example.jsonld`
3. `ontology/curry-howard-categorical-model.jsonld`
4. `ontology/logics-as-objects.jsonld`
5. `ontology/morphism-catalog.jsonld`
6. `ontology/two-d-lattice-category.jsonld`

**Change required:**
```diff
- "catty": "http://catty.org/ontology/",
+ "catty": "https://metavacua.github.io/CategoricalReasoner/ontology/",
```

### Turtle File (line 6)
7. `ontology/catty-shapes.ttl`

**Change required:**
```diff
- @prefix catty: <http://catty.org/ontology/> .
+ @prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology/> .
```

### Markdown File (multiple lines)
8. `ontology/queries/sparql-examples.md`

**Change required:**
Replace all occurrences of `http://catty.org/ontology/` with `https://metavacua.github.io/CategoricalReasoner/ontology/`

## How to Apply the Fix

### Option 1: Manual Fix

Edit each file and replace the invalid URI with the valid URI on the specified lines.

### Option 2: Automated Fix (Unix/Linux/Mac)

Run this command from the repository root:

```bash
find ontology/ -type f \( -name '*.jsonld' -o -name '*.ttl' -o -name '*.md' \) \
  -exec sed -i.bak 's|http://catty.org/ontology/|https://metavacua.github.io/CategoricalReasoner/ontology/|g' {} +

# Remove backup files after verifying changes:
find ontology/ -name '*.bak' -delete
```

### Option 3: Automated Fix (Python)

Create and run this Python script:

```python
#!/usr/bin/env python3
from pathlib import Path

OLD_URI = "http://catty.org/ontology/"
NEW_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"

ontology_dir = Path("ontology")
for filepath in ontology_dir.rglob("*"):
    if filepath.suffix in ['.jsonld', '.ttl', '.md'] and filepath.is_file():
        content = filepath.read_text()
        if OLD_URI in content:
            new_content = content.replace(OLD_URI, NEW_URI)
            filepath.write_text(new_content)
            print(f"Updated: {filepath}")
```

## Verification

After applying the fix, verify the changes:

### 1. Run the URI validation test:
```bash
python3 tools/test_validate_uri.py
```

Expected output: All files should pass validation.

### 2. Validate RDF syntax:
```bash
python3 tools/validate-rdf.py --all
```

Expected output: All RDF files should be syntactically valid.

### 3. Check for any remaining invalid URIs:
```bash
grep -r 'http://catty.org/ontology/' ontology/
```

Expected output: No matches found.

### 4. Verify GitHub Pages deployment:
```bash
curl -I https://metavacua.github.io/CategoricalReasoner/ontology/
```

Expected output: HTTP 200 OK or appropriate response.

## Impact

This fix is necessary for:
- **Semantic Web Validation**: Ontology URIs must be resolvable for proper semantic web integration
- **End-to-End Testing**: As stated in the issue, "We want end to end testing of validated semantic web technologies"
- **External Integration**: Other systems and tools need to be able to resolve the ontology URIs
- **Best Practices**: Using valid, resolvable URIs is a fundamental requirement for RDF/OWL ontologies

## Related Files

- `tools/test_validate_uri.py` - Test to validate URI correctness
- `tools/test_ontology_uris.py` - Comprehensive URI validation test
- `tools/test_uri_fix_summary.md` - Detailed fix instructions
- `tools/test_README.md` - Documentation for URI validation tests

## References

- Issue: https://github.com/metavacua/CategoricalReasoner/issues/8
- GitHub Pages URL: https://metavacua.github.io/CategoricalReasoner/
- Referenced file: https://github.com/metavacua/CategoricalReasoner/blob/0e68f389bb541299af3c2e8018e3536e356040f6/ontology/catty-categorical-schema.jsonld#L4
