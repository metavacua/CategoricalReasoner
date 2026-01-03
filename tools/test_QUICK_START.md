# Quick Start Guide: Testing Issue #8 Resolution

## For Reviewers

This guide helps you quickly test and verify the Issue #8 resolution.

## TL;DR

```bash
# 1. Preview what would be changed (safe, no modifications)
python3 tools/test_apply_uri_fix.py --dry-run

# 2. Verify external references are preserved
grep -r "dbpedia.org" ontology/
grep -r "wikidata.org" ontology/

# 3. Run validation
python3 tools/test_ontology_uris.py
```

## What Was Changed

### 1. Protected External References ✅
- DBPedia, Wikidata, and other semantic web URIs are now explicitly protected
- Script only replaces Catty-specific invalid URIs
- External references are preserved

### 2. Automated Deployment ✅
- Three-job workflow: validate → deploy → validate-deployment
- Automated GitHub Pages deployment
- Post-deployment URI testing

## Quick Tests

### Test 1: Verify Protected Domains (30 seconds)

```bash
# Check that external references exist and are preserved
echo "Checking DBPedia references..."
grep -r "dbpedia.org" ontology/ | head -3

echo "Checking Wikidata references..."
grep -r "wikidata.org" ontology/ | head -3

echo "Checking W3C references..."
grep -r "w3.org" ontology/ | head -3
```

**Expected**: You should see external references in the ontology files.

### Test 2: Preview URI Fixes (1 minute)

```bash
# See what would be changed WITHOUT modifying files
python3 tools/test_apply_uri_fix.py --dry-run
```

**Expected**:
- Shows which files would be modified
- Shows which URIs would be replaced
- Shows protected domains that are preserved
- No files are actually modified

### Test 3: Run Validation (1 minute)

```bash
# Run comprehensive validation
python3 tools/test_ontology_uris.py
```

**Expected**:
- Reports on all ontology files
- Shows any invalid URIs
- Provides fix suggestions

### Test 4: Verify Workflow (30 seconds)

```bash
# Check workflow syntax
cat .github/workflows/ontology-validation.yml | grep -A 5 "Verify external references"
```

**Expected**: You should see the new workflow step that verifies external references.

## Detailed Testing (Optional)

### Test the Fix Script

```bash
# 1. Create a backup (optional)
cp -r ontology ontology.backup

# 2. Preview changes
python3 tools/test_apply_uri_fix.py --dry-run

# 3. Apply changes (if you want to test)
python3 tools/test_apply_uri_fix.py

# 4. Verify external references are still there
grep -r "dbpedia.org" ontology/
grep -r "wikidata.org" ontology/

# 5. Restore backup (if you made changes)
rm -rf ontology
mv ontology.backup ontology
```

### Test the Workflow Locally

```bash
# Install dependencies
pip install rdflib pyshacl jsonschema

# Run the validation steps
python3 tools/test_infrastructure_validation.py
python3 tools/test_ontology_uris.py
python3 tools/test_validate_uri.py

# Check for problematic patterns
grep -r "http://catty.org/ontology/" ontology/ && echo "❌ Found invalid URI" || echo "✅ No invalid URIs"
grep -r "owner.github.io" ontology/ && echo "❌ Found placeholder" || echo "✅ No placeholders"
```

## Key Files to Review

### 1. URI Fix Script
**File**: `tools/test_apply_uri_fix.py`

**Key sections to review**:
- Lines 30-42: `PROTECTED_DOMAINS` list
- Lines 45-55: `is_protected_uri()` function
- Lines 80-105: Replacement logic with protection

### 2. Workflow
**File**: `.github/workflows/ontology-validation.yml`

**Key sections to review**:
- Lines 40-58: URI fix application with protection note
- Lines 83-106: External reference verification (NEW)
- Lines 108-200: Deployment job (NEW)
- Lines 202-280: Post-deployment validation (NEW)

### 3. Documentation
**Files**:
- `tools/test_ISSUE_8_SUMMARY.md` - Comprehensive summary
- `tools/test_PR_SUMMARY.md` - PR summary
- `tools/test_FINAL_SUMMARY.md` - Final summary

## Example: What Gets Preserved

From `ontology/examples/classical-logic.ttl`:

```turtle
# These are PRESERVED (not replaced):
@prefix dbr: <http://dbpedia.org/resource/> .
@prefix wd: <http://www.wikidata.org/entity/> .

catty:ClassicalLogic a catty:Logic ;
    owl:sameAs wd:Q217699 ;              # ✅ Preserved
    skos:exactMatch dbr:Classical_logic ; # ✅ Preserved
```

## Example: What Gets Replaced

```turtle
# BEFORE (invalid):
@prefix catty: <http://catty.org/ontology/> .

# AFTER (valid):
@prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology/> .
```

## Review Checklist

Quick checklist for reviewers:

- [ ] Protected domains list is comprehensive
- [ ] `is_protected_uri()` function works correctly
- [ ] Replacement logic checks each line
- [ ] External references are preserved
- [ ] Workflow has three jobs
- [ ] Deployment job runs on main branch only
- [ ] Post-deployment validation tests URIs
- [ ] Documentation is clear and complete

## Common Questions

### Q: Will this replace DBPedia URIs?
**A**: No. DBPedia URIs are in the `PROTECTED_DOMAINS` list and are explicitly preserved.

### Q: Will this replace Wikidata URIs?
**A**: No. Wikidata URIs are in the `PROTECTED_DOMAINS` list and are explicitly preserved.

### Q: What URIs will be replaced?
**A**: Only Catty-specific invalid URIs:
- `http://catty.org/ontology/`
- `https://owner.github.io/Catty/ontology#`
- `http://owner.github.io/Catty/ontology#`

### Q: How do I know external references are preserved?
**A**:
1. Check the `PROTECTED_DOMAINS` list in the script
2. Run the workflow step that verifies external references
3. Grep for external references before and after

### Q: What happens after merge?
**A**:
1. Workflow runs automatically
2. URIs are fixed (if needed)
3. Ontologies are deployed to GitHub Pages
4. Post-deployment validation confirms URIs work

## Need Help?

### Documentation
- `tools/test_ISSUE_8_SUMMARY.md` - Comprehensive summary
- `tools/test_PR_SUMMARY.md` - PR summary
- `tools/test_README.md` - Full documentation

### Testing
- `tools/test_apply_uri_fix.py --help` - Script help
- `tools/test_ontology_uris.py` - Validation script
- `tools/test_validate_uri.py` - Quick validation

### Questions
- Comment on the PR
- Check the documentation files
- Review the workflow file

## Summary

**What's Protected**: DBPedia, Wikidata, W3C, and other external semantic web references

**What's Replaced**: Only Catty-specific invalid URIs

**What's Automated**: Validation, deployment, and post-deployment testing

**What's Documented**: Comprehensive documentation in multiple files

---

**Ready to review?** Start with the quick tests above, then review the key files.
