# URI Validation Test for Catty Ontologies
# Ontology URI Validation Infrastructure

This directory contains the validation infrastructure for Catty ontology URIs, addressing Issue #8.

## Overview

The validation infrastructure ensures that:
1. All ontology files use valid, dereferenceable URIs
2. New or modified ontologies are automatically validated
3. Problematic URI patterns are detected and reported
4. Untested/untestable ontologies are identified

## Components

### 1. Validation Scripts

#### `test_ontology_uris.py` (Primary Validation)
Comprehensive validation script that:
- Scans all ontology files (`.jsonld`, `.ttl`, `.rdf`, `.owl`, `.md`)
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
- Checks known ontology files for the catty.org → GitHub Pages URI migration
- Provides detailed fix instructions
- Offers automated fix commands

**Usage:**
```bash
python3 tools/test_validate_uri.py
```

#### `test_apply_uri_fix.py` (Automated Fix)
Automated script to fix invalid URIs:
- Replaces invalid URIs with valid GitHub Pages URIs
- Creates backups before modification
- Validates changes after application

**Usage:**
```bash
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
4. Run URI validation
5. Run RDF syntax validation
6. Run SHACL validation (if shapes exist)
7. Report results

### Status Checks
- ✅ All validations pass → PR can be merged
- ❌ Any validation fails → PR blocked until fixed

## Usage Scenarios

### During Development
```bash
# Check if your changes are valid
python3 tools/test_ontology_uris.py

# Apply automated fixes
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
