# Issue #8: Catty Specific Ontologies Have Invalid URI

## Response to Review Comment

**Reviewer's Concern:** "Some of the other ontologies have different but similar problematic code. Ideally, we want infrastructure so that when ontologies are changed or added they are made valid or at least we know when they are untested and untestable without alteration."

**Infrastructure Solution:**

This PR addresses the infrastructure concern by:

1. **CI/CD Integration**: Added `.github/workflows/ontology-validation.yml` that runs on every push and PR to validate all ontology URIs
2. **Comprehensive Validation**: The validation scripts check for:
   - Invalid/non-dereferenceable URIs (e.g., `http://catty.org/ontology/`, `https://owner.github.io/Catty/ontology#`)
   - Missing or malformed @context in JSON-LD files
   - Inconsistent URI patterns across files
   - RDF syntax validation
3. **Automated Detection**: New ontologies or changes trigger automatic validation, ensuring no invalid URIs are introduced
4. **Clear Reporting**: Test failures provide specific line numbers and fix instructions
5. **Documentation**: Comprehensive guides for maintaining valid ontologies

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

The validation scripts automatically discover all ontology files that need to be updated:

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

### Example Files (line 1 in each)
9. `ontology/examples/classical-logic.ttl`
10. `ontology/examples/dual-intuitionistic-logic.ttl`
11. `ontology/examples/intuitionistic-logic.ttl`
12. `ontology/examples/linear-logic.ttl`
13. `ontology/examples/monotonic-logic.ttl`

**Change required:**
```diff
- @prefix catty: <https://owner.github.io/Catty/ontology#> .
+ @prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology/> .
```

**Note:** The validation scripts automatically discover all ontology files recursively, including those in subdirectories like `ontology/examples/`.

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


## Infrastructure Details

### Automated Validation in CI/CD

A GitHub Actions workflow (`.github/workflows/ontology-validation.yml`) runs on every push and pull request to:
1. Validate all ontology URIs
2. Check for multiple problematic patterns (not just catty.org)
3. Validate RDF syntax
4. Report untested files

### Validation Scripts

**`tools/test_ontology_uris.py`** - Comprehensive validation that:
- Automatically discovers all ontology files (*.jsonld, *.ttl, *.rdf, *.owl, *.md)
- Checks for multiple invalid URI patterns:
  - `http://catty.org/ontology/`
  - `https://owner.github.io/Catty/ontology#`
  - Non-HTTPS ontology URIs
  - Inconsistent @prefix declarations
- Reports untested/untestable files
- Provides detailed fix instructions

**`tools/test_validate_uri.py`** - Quick validation for known files

**`tools/test_apply_uri_fix.py`** - Automated fix application with:
- Dry-run mode to preview changes
- Support for multiple invalid URI patterns
- Backup and rollback capabilities

**`tools/test_run_uri_validation.sh`** - Complete validation workflow

### How It Addresses the Review Concern

The reviewer noted: "Some of the other ontologies have different but similar problematic code. Ideally, we want infrastructure so that when ontologies are changed or added they are made valid or at least we know when they are untested and untestable without alteration."

This infrastructure addresses that by:

1. **Automatic Discovery**: New ontology files are automatically found and validated
2. **Multiple Pattern Detection**: Not limited to just `catty.org` - detects various problematic patterns
3. **CI/CD Integration**: Every change triggers validation, preventing invalid URIs from being merged
4. **Explicit Reporting**: Untested and untestable files are clearly identified in validation output
5. **Automated Fixes**: Scripts can automatically correct known issues
6. **Extensible**: Easy to add new validation rules and patterns

### Adding New Validation Rules

To add new URI patterns to check, edit `tools/test_ontology_uris.py`:

```python
# Add to PROBLEMATIC_PATTERNS list
PROBLEMATIC_PATTERNS = [
    (r'http://catty\.org/', 'Invalid domain: catty.org'),
    (r'https?://owner\.github\.io/', 'Invalid placeholder: owner.github.io'),
    # Add your new pattern here:
    (r'your-regex-pattern', 'Description of the issue'),
]
```

### Example: Detecting Different Problematic Patterns

The infrastructure catches various issues:

```
❌ ontology/example.jsonld
    ✗ 2 error(s)
    ❌ Invalid domain: catty.org: "catty": "http://catty.org/ontology/" (line 4)
       Fix: Change to: "https://metavacua.github.io/CategoricalReasoner/ontology/"
    ❌ Invalid placeholder: owner.github.io: @prefix ex: <https://owner.github.io/...> (line 10)
       Fix: Replace with valid URI: https://metavacua.github.io/CategoricalReasoner/ontology/
```

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
## Infrastructure Usage Examples

### Example 1: Developer Adding a New Ontology

A developer creates `ontology/new-feature.jsonld`:

```json
{
  "@context": {
    "catty": "http://catty.org/ontology/"
  }
}
```

**What happens:**
1. Developer runs `./tools/test_run_uri_validation.sh` before committing
2. Validation detects the invalid URI
3. Developer runs `python3 tools/test_apply_uri_fix.py` to fix it
4. Validation passes
5. CI/CD validates on push to ensure no regressions

### Example 2: Detecting Multiple Problematic Patterns

An ontology file contains:
```turtle
@prefix catty: <https://owner.github.io/Catty/ontology#> .
@prefix ex: <http://example.org/ontology/> .
```

**What the infrastructure detects:**
- Line 1: Invalid placeholder domain `owner.github.io`
- Line 2: Non-HTTPS URI (warning, may be acceptable for external ontologies)

### Example 3: CI/CD Catching Invalid Changes

A pull request modifies an ontology file and accidentally introduces:
```json
"catty": "http://catty.org/ontology/"
```

**What happens:**
1. CI/CD runs ontology validation workflow
2. Validation fails with clear error message
3. PR cannot be merged until fixed
4. Developer is notified of the specific issue and line number

### Example 4: Comprehensive Validation Report

Running `python3 tools/test_ontology_uris.py` produces:

```
✅ ontology/catty-categorical-schema.jsonld
    ✓ Valid

❌ ontology/new-ontology.jsonld
    ✗ 2 error(s)
    ❌ Uses invalid URI: http://catty.org/ontology/ (line 4)
       Fix: Change to: "https://metavacua.github.io/CategoricalReasoner/ontology/"
    ❌ Invalid placeholder: owner.github.io (line 8)
       Fix: Replace with valid URI

⚠️  ontology/experimental/test.owl
    Unsupported file type
```

## Continuous Improvement

The infrastructure is designed to evolve:

1. **Pattern Detection**: Add new problematic patterns as they are discovered
2. **File Type Support**: Extend validation to new RDF formats
3. **Custom Rules**: Add project-specific validation rules
4. **Integration**: Connect with RDF validators, reasoners, and semantic web tools
5. **Reporting**: Enhanced reporting with suggestions and auto-fixes

This ensures that as the project grows, ontology quality remains high and issues are caught early.
