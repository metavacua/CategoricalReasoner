# URI Validation Infrastructure Checklist

## Pre-Merge Checklist

Before merging this PR, verify that:

### Infrastructure Files
- [x] `tools/test_ontology_uris.py` - Comprehensive URI validation script
- [x] `tools/test_validate_uri.py` - Quick URI validation script
- [x] `tools/test_apply_uri_fix.py` - Automated fix script with dynamic file discovery
- [x] `tools/test_run_uri_validation.sh` - Shell script for complete validation workflow
- [x] `tools/test_infrastructure_validation.py` - Infrastructure validation test
- [x] `tools/test_fix_script_validation.py` - Fix script validation test
- [x] `tools/test_comprehensive_validation.py` - Master validation test

### Documentation Files
- [x] `tools/test_README.md` - Infrastructure documentation
- [x] `tools/test_ISSUE_8_SUMMARY.md` - Issue summary with example files
- [x] `tools/test_uri_fix_summary.md` - Detailed fix instructions
- [x] `tools/test_CHANGES_SUMMARY.md` - Changes documentation
- [x] `tools/test_PR_SUMMARY.md` - Pull request summary
- [x] `tools/test_CHECKLIST.md` - This checklist

### CI/CD Integration
- [x] `.github/workflows/ontology-validation.yml` - Workflow with infrastructure validation

### Key Features
- [x] Dynamic file discovery in all scripts
- [x] Recursive search for ontology files
- [x] Support for subdirectories (e.g., `ontology/examples/`)
- [x] Multiple invalid URI pattern detection
- [x] Automated fix capability
- [x] Dry-run mode for preview
- [x] Infrastructure self-validation
- [x] Comprehensive documentation

## Post-Merge Checklist

After merging this PR, complete these steps:

### 1. Apply URI Fixes
```bash
# Preview changes
python3 tools/test_apply_uri_fix.py --dry-run

# Apply changes
python3 tools/test_apply_uri_fix.py
```

### 2. Verify Fixes
```bash
# Run comprehensive validation
python3 tools/test_comprehensive_validation.py

# Or run individual tests
python3 tools/test_ontology_uris.py
python3 tools/test_validate_uri.py
```

### 3. Commit Changes
```bash
git add ontology/
git commit -m "Fix: Update ontology URIs to GitHub Pages URL (Issue #8)"
git push
```

### 4. Verify CI/CD
- [ ] Check that the GitHub Actions workflow passes
- [ ] Verify all validation steps complete successfully
- [ ] Confirm no invalid URIs are detected

## Files That Will Be Modified (Post-Merge)

When you run the fix script, these files will be updated:

### JSON-LD Files (6 files)
- [ ] `ontology/catty-categorical-schema.jsonld`
- [ ] `ontology/catty-complete-example.jsonld`
- [ ] `ontology/curry-howard-categorical-model.jsonld`
- [ ] `ontology/logics-as-objects.jsonld`
- [ ] `ontology/morphism-catalog.jsonld`
- [ ] `ontology/two-d-lattice-category.jsonld`

### Turtle Files (6 files)
- [ ] `ontology/catty-shapes.ttl`
- [ ] `ontology/examples/classical-logic.ttl`
- [ ] `ontology/examples/dual-intuitionistic-logic.ttl`
- [ ] `ontology/examples/intuitionistic-logic.ttl`
- [ ] `ontology/examples/linear-logic.ttl`
- [ ] `ontology/examples/monotonic-logic.ttl`

### Markdown Files (1 file)
- [ ] `ontology/queries/sparql-examples.md`

## Validation Tests

Run these tests to verify everything works:

### Infrastructure Tests
```bash
# Test that all scripts and docs exist
python3 tools/test_infrastructure_validation.py

# Test that the fix script works
python3 tools/test_fix_script_validation.py

# Run all tests together
python3 tools/test_comprehensive_validation.py
```

### URI Validation Tests
```bash
# Comprehensive URI validation
python3 tools/test_ontology_uris.py

# Quick URI validation
python3 tools/test_validate_uri.py
```

### Shell Script
```bash
# Complete validation workflow
bash tools/test_run_uri_validation.sh

# With automated fix
bash tools/test_run_uri_validation.sh --fix
```

## Expected Results

### Before Applying Fixes
- ❌ URI validation tests will fail (13 files with invalid URIs)
- ✅ Infrastructure validation tests will pass
- ✅ Fix script validation tests will pass

### After Applying Fixes
- ✅ All URI validation tests will pass
- ✅ All infrastructure validation tests will pass
- ✅ CI/CD pipeline will pass

## Troubleshooting

### If Infrastructure Tests Fail
1. Check that all required files exist
2. Verify Python 3 is installed
3. Check file permissions (scripts should be executable)
4. Review error messages for specific issues

### If URI Validation Tests Fail After Applying Fixes
1. Verify the fix script ran successfully
2. Check that all files were updated
3. Look for any remaining invalid URIs:
   ```bash
   grep -r "http://catty.org/ontology/" ontology/
   grep -r "owner.github.io" ontology/
   ```
4. Re-run the fix script if needed

### If CI/CD Pipeline Fails
1. Check the workflow logs for specific errors
2. Run the validation scripts locally to reproduce
3. Verify all changes were committed and pushed
4. Check that the workflow file is correct

## Success Criteria

This PR is successful when:
- ✅ All infrastructure validation tests pass
- ✅ All documentation is complete and accurate
- ✅ Dynamic file discovery works correctly
- ✅ Fix script can find and update all files
- ✅ CI/CD workflow includes infrastructure validation
- ✅ All 13 files with invalid URIs are identified

After applying fixes:
- ✅ All URI validation tests pass
- ✅ No invalid URIs remain in ontology files
- ✅ CI/CD pipeline passes
- ✅ Ontologies are resolvable and dereferenceable

## Additional Notes

### Dynamic File Discovery
The infrastructure now uses dynamic file discovery, which means:
- New ontology files are automatically found and validated
- Files in subdirectories are automatically included
- No manual updates to file lists are needed
- The system is maintainable and extensible

### Invalid URI Patterns Detected
The infrastructure detects these invalid patterns:
- `http://catty.org/ontology/` - Non-existent domain
- `https://owner.github.io/Catty/ontology#` - Placeholder domain
- `http://owner.github.io/Catty/ontology#` - Placeholder domain (non-HTTPS)
- Non-HTTPS ontology URIs
- Inconsistent @prefix declarations

### Valid URI
All invalid URIs should be replaced with:
```
https://metavacua.github.io/CategoricalReasoner/ontology/
```

## Questions or Issues?

If you encounter any problems:
1. Review the documentation in `tools/test_README.md`
2. Check the detailed changes in `tools/test_CHANGES_SUMMARY.md`
3. Read the issue summary in `tools/test_ISSUE_8_SUMMARY.md`
4. See fix instructions in `tools/test_uri_fix_summary.md`
5. Review the PR summary in `tools/test_PR_SUMMARY.md`

## References

- Issue: https://github.com/metavacua/CategoricalReasoner/issues/8
- CI/CD Workflow: `.github/workflows/ontology-validation.yml`
- Validation Scripts: `tools/test_*.py`
- Documentation: `tools/test_*.md`
