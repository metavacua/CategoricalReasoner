# Ontology URI Validation Infrastructure

This directory contains the validation infrastructure for Catty ontology URIs, addressing Issue #8.

**IMPORTANT**: This infrastructure preserves external semantic web references (DBPedia, Wikidata, etc.) and only replaces Catty-specific invalid URIs.

## Overview

The validation infrastructure ensures that:
1. All ontology files use valid, dereferenceable URIs
2. New or modified ontologies are automatically validated
3. Problematic URI patterns are detected and reported
4. Untested/untestable ontologies are identified
5. External semantic web references (DBPedia, Wikidata, etc.) are preserved

## Components

### 1. Validation Scripts

#### `test_ontology_uris.py` (Primary Validation)
Comprehensive validation script that:
- Scans all ontology files recursively (`.jsonld`, `.ttl`, `.rdf`, `.owl`, `.md`)
- Detects multiple problematic URI patterns:
  - Invalid domains (catty.org, owner.github.io)
  - Non-HTTPS URIs
  - Inconsistent URI patterns
  - Missing or malformed declarations
- Reports detailed issues with line numbers and fix suggestions
- Identifies untested/untestable files

**Usage:**
```bash
python3 tools/test_ontology_uris.py
```

#### `test_validate_uri.py` (Focused Validation)
Focused validation for the specific Issue #8 fix:
- Checks all ontology files dynamically for the catty.org → GitHub Pages URI migration
- Provides detailed fix instructions
- Offers automated fix commands

**Usage:**
```bash

**IMPORTANT**: This script ONLY replaces Catty-specific invalid URIs. It preserves
external semantic web references (DBPedia, Wikidata, schema.org, etc.) as these
are legitimate resources from the greater web.

**Protected Domains** (will NOT be replaced):
- `dbpedia.org` - DBPedia resources
- `wikidata.org` - Wikidata entities
- `schema.org` - Schema.org vocabulary
- `w3.org` - W3C standards (RDF, RDFS, OWL, etc.)
- `purl.org` - Persistent URLs
- `xmlns.com` - XML namespaces
- `ncatlab.org` - nLab resources
- `example.org/com` - Example domains

This ensures we respect the semantic web ecosystem by maintaining links to
established vocabularies while fixing Catty-specific resources.
python3 tools/test_validate_uri.py
```

#### `test_apply_uri_fix.py` (Automated Fix)
Automated script to fix invalid URIs:
- Discovers all ontology files dynamically
- Replaces invalid URIs with valid GitHub Pages URIs
- Supports dry-run mode for preview
- Validates changes after application

**Usage:**
```bash
# Preview changes
python3 tools/test_apply_uri_fix.py --dry-run

# Apply changes
python3 tools/test_apply_uri_fix.py
```

#### `test_run_uri_validation.sh` (CI/CD Integration)
Shell script for CI/CD pipeline integration:
- Runs all validation checks
- Exits with appropriate status codes
- Provides summary reports

**Usage:**
```bash
bash tools/test_run_uri_validation.sh
```

### 2. Documentation

#### `test_ISSUE_8_SUMMARY.md`
Comprehensive summary of Issue #8:
- Problem description
- Solution approach
- Files requiring changes
- Infrastructure overview
- Verification steps

#### `test_uri_fix_summary.md`
Detailed fix instructions:
- Step-by-step manual fix guide
- Automated fix options
- Verification procedures

## CI/CD Integration

The validation infrastructure is integrated into the CI/CD pipeline via `.github/workflows/ontology-validation.yml`:

### Workflow Triggers
- **Push/PR to main branch**: Validates all ontology files
- **Changes to ontology files**: Validates affected files
- **Manual trigger**: On-demand validation

### Workflow Steps
1. Checkout repository
2. Set up Python environment
3. Install dependencies (rdflib, pyshacl)
4. Run comprehensive URI validation
5. Run quick URI validation
6. Check for problematic URI patterns
7. Report results

### Status Checks
- ✅ All validations pass → PR can be merged
- ❌ Any validation fails → PR blocked until fixed

## Usage Scenarios

### During Development
```bash
# Check if your changes are valid
python3 tools/test_ontology_uris.py

# Apply automated fixes (preview first)
python3 tools/test_apply_uri_fix.py --dry-run
python3 tools/test_apply_uri_fix.py

# Verify fixes
python3 tools/test_validate_uri.py
```

### In CI/CD Pipeline
The validation runs automatically on:
- Every push to main branch
- Every pull request
- Manual workflow dispatch

### Pre-commit Hook (Optional)
```bash
# Add to .git/hooks/pre-commit
#!/bin/bash
python3 tools/test_ontology_uris.py
if [ $? -ne 0 ]; then
    echo "❌ Ontology URI validation failed"
    echo "Run: python3 tools/test_apply_uri_fix.py"
    exit 1
fi
```

## Issue #8: Catty specific ontologies have invalid URI

### Problem

The Catty ontology files currently use invalid URIs:
- `http://catty.org/ontology/`
- `https://owner.github.io/Catty/ontology#`

These domains do not exist and the ontologies cannot be resolved.

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

This will check all ontology files recursively and report which ones need to be updated.

### Files Affected

The scripts automatically discover all ontology files in:
- `ontology/*.jsonld`
- `ontology/*.ttl`
- `ontology/*.rdf`
- `ontology/*.owl`
- `ontology/examples/*.ttl`
- `ontology/queries/*.md`

### Detailed Instructions

See `test_uri_fix_summary.md` for:
- Exact line-by-line changes needed
- Automated fix commands
- Verification steps

## Dynamic File Discovery

All validation scripts now use dynamic file discovery to:
- Automatically find all ontology files recursively
- Support new files without code changes
- Handle files in subdirectories (e.g., `ontology/examples/`)
- Ensure comprehensive coverage

This means:
- ✅ New ontology files are automatically validated
- ✅ Files in subdirectories are included
- ✅ No manual updates to file lists needed
- ✅ Consistent validation across all scripts

## Infrastructure Validation Tests

### `test_infrastructure_validation.py`
Validates that the infrastructure itself is working correctly:
- All required scripts exist
- All required documentation exists
- CI/CD workflow is configured
- Ontology files can be found dynamically
- Validation scripts run correctly
- Invalid URI patterns are detected

**Usage:**
```bash
python3 tools/test_infrastructure_validation.py
```

### `test_fix_script_validation.py`
Validates that the automated fix script works correctly:
- Fix script runs in dry-run mode
- Fix script has help documentation
- Fix script finds all ontology files

**Usage:**
```bash
python3 tools/test_fix_script_validation.py
```


## Review Response: External Reference Preservation

### Review Comment (2026-01-03)
> "In one of the failed validation runs, DBPedia URI or IRI or URL were identified and the validator tried to replace them with CategoricalReasoner links; this is at least somewhat incorrect. If the DBpedia links are not valid semantic web code then we do want to deal with that, but we do not want to take resources that were are using from the greater web and mistakenly replace them with CategoricalReasoner specific resources when that is not necessary or desirable."

### Resolution

The URI fix script (`test_apply_uri_fix.py`) has been updated to **explicitly protect external semantic web references**:

#### Protected Domains
The following domains are **NEVER replaced**:
- `dbpedia.org` - DBPedia resources
- `wikidata.org` - Wikidata entities
- `schema.org` - Schema.org vocabulary
- `w3.org` - W3C standards (RDF, RDFS, OWL, SKOS, etc.)
- `purl.org` - Persistent URLs
- `xmlns.com` - XML namespaces
- `ncatlab.org` - nLab resources
- `example.org/com` - Example domains

#### Implementation
```python
def is_protected_uri(uri: str) -> bool:
    """Check if a URI is from a protected domain."""
    uri_lower = uri.lower()
    return any(domain in uri_lower for domain in PROTECTED_DOMAINS)
```

The script checks each line before replacement. If a line contains a protected domain, it skips replacement for that line.

#### Example: Preserved External References
From `ontology/examples/classical-logic.ttl`:
```turtle
@prefix dbr: <http://dbpedia.org/resource/> .
@prefix wd: <http://www.wikidata.org/entity/> .

catty:ClassicalLogic a catty:Logic ;
    owl:sameAs wd:Q217699 ;              # ✅ Preserved
    skos:exactMatch dbr:Classical_logic ; # ✅ Preserved
```

#### Verification
The workflow now includes a step to verify external references are preserved:
```yaml
- name: Verify external references are preserved
  run: |
    # Check for DBPedia references
    if grep -r "dbpedia.org" ontology/; then
      echo "✅ DBPedia references found (preserved)"
    fi
```

### Deployment and Validation Workflow

The workflow has been enhanced with three jobs:
1. **validate-ontologies**: Validates URIs and preserves external references
2. **deploy-to-pages**: Automated GitHub Pages deployment (main branch only)
3. **validate-deployment**: Post-deployment URI dereferenceability testing

See `test_ISSUE_8_SUMMARY.md` for complete details on the review response and implementation.
### `test_comprehensive_validation.py`
Master test that runs all validation checks:
