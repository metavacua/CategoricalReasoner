# URI Fix Summary for Issue #8

## Infrastructure for Ontology Validation

This fix includes comprehensive infrastructure to ensure ontologies remain valid:

1. **Automated Validation**: CI/CD pipeline validates all ontology URIs on every commit
2. **Multiple Pattern Detection**: Detects various problematic URI patterns (not just catty.org)
3. **Untested File Reporting**: Identifies ontologies that haven't been validated
4. **Automated Fixes**: Scripts to automatically correct invalid URIs
5. **Pre-commit Hooks**: Optional hooks to prevent invalid URIs from being committed

### Validation Tools

- `tools/test_ontology_uris.py` - Comprehensive URI validation
- `tools/test_validate_uri.py` - Quick URI check
- `tools/test_apply_uri_fix.py` - Automated fix application
- `tools/test_run_uri_validation.sh` - Complete validation workflow
- `.github/workflows/ontology-validation.yml` - CI/CD integration

## Problem Statement

The Catty-specific ontologies currently use invalid URIs:
```
http://catty.org/ontology/
https://owner.github.io/Catty/ontology#
```

These domains do not exist, making the ontology non-resolvable and preventing proper semantic web integration.

## Solution

Update all ontology files to use the valid GitHub Pages URI:
```
https://metavacua.github.io/CategoricalReasoner/ontology/
```

## Detected Problematic Patterns

The validation infrastructure detects:
- `http://catty.org/` - Invalid domain
- `https://owner.github.io/` - Placeholder domain
- `http://*/ontology/` - Non-HTTPS ontology URIs
- Inconsistent @prefix declarations
- Missing or malformed @context
- Any other non-dereferenceable URIs

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


### 9. ontology/examples/classical-logic.ttl
**Line 1:**
```diff
-@prefix catty: <https://owner.github.io/Catty/ontology#> .
+@prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology/> .
```

### 10-13. Other Example Files
The following files in `ontology/examples/` also need the same fix on line 1:
- `ontology/examples/dual-intuitionistic-logic.ttl`
- `ontology/examples/intuitionistic-logic.ttl`
- `ontology/examples/linear-logic.ttl`
- `ontology/examples/monotonic-logic.ttl`

All use the placeholder `https://owner.github.io/Catty/ontology#` which needs to be replaced.
### 9. ontology/ontological-inventory.md (if exists)
**Documentation references:**
Update any documentation that references the old URI.

## Additional Files to Check

The validation infrastructure will automatically detect:
- New ontology files added to the repository
- Files in subdirectories (e.g., `ontology/examples/`)
- Documentation files referencing ontology URIs
- Test files with embedded ontology references

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

### Method 4: Automated Fix Script (Recommended)
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

Or use the provided script:
```bash
# Preview changes
python3 tools/test_apply_uri_fix.py --dry-run
# Apply changes
python3 tools/test_apply_uri_fix.py
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

## Continuous Validation

The infrastructure ensures ongoing validation:

### CI/CD Pipeline
Every commit triggers:
1. URI pattern validation
2. RDF syntax validation
3. JSON-LD context validation
4. Dereferenceable URI checks

### Local Development
Run before committing:
```bash
./tools/test_run_uri_validation.sh
```

## Benefits of This Fix

1. **Resolvable URIs**: The GitHub Pages URL is a valid, publicly accessible endpoint
2. **Semantic Web Compliance**: Enables proper linked data integration
3. **End-to-End Testing**: Allows validation of semantic web technologies as requested
4. **Standards Compliance**: Follows best practices for ontology publishing
5. **Future-Proof**: GitHub Pages provides stable, long-term hosting
6. **Automated Validation**: Infrastructure prevents regression
7. **Comprehensive Coverage**: Detects various problematic patterns

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

## Infrastructure Testing Checklist

- [ ] CI/CD pipeline validates ontologies
- [ ] Validation scripts detect all problematic patterns
- [ ] Automated fix script works correctly
- [ ] Documentation is up to date
- [ ] Pre-commit hooks configured (optional)
- [ ] New ontology files are automatically validated
- [ ] Untested files are reported
- [ ] All validation tools are documented

## Notes

- The old URI `http://catty.org/ontology/` was never a valid domain
- The new URI `https://metavacua.github.io/CategoricalReasoner/ontology/` aligns with the GitHub Pages deployment
- This change is backward-compatible as the old URI was never functional
- No external systems should be affected as the old URI was not resolvable

## Addressing Review Comments

This fix addresses the concern that "some of the other ontologies have different but similar problematic code" by:

1. **Detecting Multiple Patterns**: The validation infrastructure checks for various invalid URI patterns, not just `catty.org`
2. **Automated Discovery**: New ontology files are automatically discovered and validated
3. **CI/CD Integration**: Every change triggers validation, ensuring no invalid URIs are introduced
4. **Comprehensive Reporting**: Untested and untestable files are explicitly reported

The infrastructure ensures that when ontologies are changed or added, they are:
- Automatically validated for URI correctness
- Checked against multiple problematic patterns
- Reported if untested or untestable
- Fixed automatically when possible
