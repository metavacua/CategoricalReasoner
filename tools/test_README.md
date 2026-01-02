# URI Validation Test for Catty Ontologies

## Issue #8: Catty specific ontologies have invalidate URI

### Problem

The Catty ontology files currently use an invalid URI:
```
http://catty.org/ontology/
```

This domain does not exist and the ontology cannot be resolved.

### Solution

Update all ontology files to use the GitHub Pages URI:
```
https://metavacua.github.io/CategoricalReasoner/ontology/
```

### Test Script

Run the validation test:
```bash
python3 tools/test_ontology_uris.py
```

This will check all ontology files and report which ones need to be updated.

### Files Affected

1. `ontology/catty-categorical-schema.jsonld` (line 4)
2. `ontology/catty-complete-example.jsonld` (line 4)
3. `ontology/curry-howard-categorical-model.jsonld` (line 4)
4. `ontology/logics-as-objects.jsonld` (line 4)
5. `ontology/morphism-catalog.jsonld` (line 4)
6. `ontology/two-d-lattice-category.jsonld` (line 4)
7. `ontology/catty-shapes.ttl` (line 6)
8. `ontology/queries/sparql-examples.md` (multiple lines)

### Detailed Instructions

See `test_uri_fix_summary.md` for:
- Exact line-by-line changes needed
- Automated fix commands
- Verification steps
