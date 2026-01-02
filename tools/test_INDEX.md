# URI Validation Infrastructure - Documentation Index

## Quick Links

- **Start Here**: [Quick Start Guide](test_QUICK_START.md)
- **For Reviewers**: [PR Summary](test_PR_SUMMARY.md)
- **For Users**: [Main Documentation](test_README.md)
- **For Developers**: [Changes Summary](test_CHANGES_SUMMARY.md)

---

## Documentation Files

### Getting Started
1. **[Quick Start Guide](test_QUICK_START.md)** - Fast overview for reviewers, users, and developers
2. **[Main Documentation](test_README.md)** - Comprehensive infrastructure documentation
3. **[Checklist](test_CHECKLIST.md)** - Pre/post-merge checklist

### Issue Context
4. **[Issue #8 Summary](test_ISSUE_8_SUMMARY.md)** - Detailed issue summary and context
5. **[Fix Instructions](test_uri_fix_summary.md)** - Step-by-step fix instructions

### This PR
6. **[PR Summary](test_PR_SUMMARY.md)** - Pull request summary
7. **[Changes Summary](test_CHANGES_SUMMARY.md)** - Detailed changes made
8. **[Final Summary](test_FINAL_SUMMARY.md)** - Comprehensive overview

### Reference
9. **[Index](test_INDEX.md)** - This file

---

## Script Files

### Validation Scripts
- **[test_ontology_uris.py](test_ontology_uris.py)** - Comprehensive URI validation
- **[test_validate_uri.py](test_validate_uri.py)** - Quick URI validation
- **[test_run_uri_validation.sh](test_run_uri_validation.sh)** - Shell script for complete workflow

### Fix Scripts
- **[test_apply_uri_fix.py](test_apply_uri_fix.py)** - Automated fix script with dynamic file discovery

### Test Scripts
- **[test_infrastructure_validation.py](test_infrastructure_validation.py)** - Infrastructure validation test
- **[test_fix_script_validation.py](test_fix_script_validation.py)** - Fix script validation test
- **[test_comprehensive_validation.py](test_comprehensive_validation.py)** - Master validation test

---

## Documentation by Audience

### For Reviewers
1. [Quick Start Guide](test_QUICK_START.md) - Quick overview
2. [PR Summary](test_PR_SUMMARY.md) - What this PR does
3. [Changes Summary](test_CHANGES_SUMMARY.md) - What was changed
4. [Checklist](test_CHECKLIST.md) - What to verify

**Quick Review Commands**:
```bash
python3 tools/test_infrastructure_validation.py
python3 tools/test_fix_script_validation.py
python3 tools/test_comprehensive_validation.py
```

### For Users
1. [Quick Start Guide](test_QUICK_START.md) - How to use the tools
2. [Main Documentation](test_README.md) - Complete guide
3. [Fix Instructions](test_uri_fix_summary.md) - How to fix URIs
4. [Issue #8 Summary](test_ISSUE_8_SUMMARY.md) - Why this is needed

**Quick Usage Commands**:
```bash
python3 tools/test_ontology_uris.py              # Check for issues
python3 tools/test_apply_uri_fix.py --dry-run    # Preview fixes
python3 tools/test_apply_uri_fix.py              # Apply fixes
python3 tools/test_validate_uri.py               # Verify fixes
```

### For Developers
1. [Quick Start Guide](test_QUICK_START.md) - Development overview
2. [Main Documentation](test_README.md) - Infrastructure details
3. [Changes Summary](test_CHANGES_SUMMARY.md) - Implementation details
4. [Final Summary](test_FINAL_SUMMARY.md) - Complete technical overview

**Quick Development Commands**:
```bash
python3 tools/test_infrastructure_validation.py  # Test infrastructure
python3 tools/test_fix_script_validation.py      # Test fix script
python3 tools/test_comprehensive_validation.py   # Test everything
```

---

## Documentation by Topic

### Infrastructure
- [Main Documentation](test_README.md) - Complete infrastructure guide
- [Changes Summary](test_CHANGES_SUMMARY.md) - What was changed
- [Final Summary](test_FINAL_SUMMARY.md) - Technical overview

### Issue #8
- [Issue #8 Summary](test_ISSUE_8_SUMMARY.md) - Issue context
- [Fix Instructions](test_uri_fix_summary.md) - How to fix
- [PR Summary](test_PR_SUMMARY.md) - How this PR addresses it

### Usage
- [Quick Start Guide](test_QUICK_START.md) - Fast start
- [Main Documentation](test_README.md) - Complete guide
- [Checklist](test_CHECKLIST.md) - Step-by-step

### Testing
- [Infrastructure Validation](test_infrastructure_validation.py) - Test the infrastructure
- [Fix Script Validation](test_fix_script_validation.py) - Test the fix script
- [Comprehensive Validation](test_comprehensive_validation.py) - Test everything

---

## Key Concepts

### Dynamic File Discovery
All scripts now automatically find ontology files using:
```python
def find_all_ontology_files(repo_root: Path) -> List[Path]:
    """Find all ontology files in the repository."""
    ontology_dir = repo_root / "ontology"
    files = []
    for pattern in ['*.jsonld', '*.ttl', '*.rdf', '*.owl']:
        files.extend(ontology_dir.rglob(pattern))
    # Also check markdown files in queries directory
    queries_dir = ontology_dir / 'queries'
    if queries_dir.exists():
        files.extend(queries_dir.glob('*.md'))
    return sorted(files)
```

**Benefits**:
- Automatic discovery of new files
- Includes subdirectories
- No manual file list updates
- Consistent across all scripts

### Invalid URI Patterns
The infrastructure detects these invalid patterns:
- `http://catty.org/ontology/` - Non-existent domain
- `https://owner.github.io/Catty/ontology#` - Placeholder domain
- `http://owner.github.io/Catty/ontology#` - Placeholder domain (non-HTTPS)

**Valid URI**:
- `https://metavacua.github.io/CategoricalReasoner/ontology/`

### Files Requiring Fixes
13 files total:
- 6 JSON-LD files (line 4)
- 6 Turtle files (line 1 or 6)
- 1 Markdown file (13 occurrences)

---

## Workflow

### Pre-Merge (This PR)
1. Review the changes
2. Run infrastructure validation tests
3. Verify all tests pass
4. Merge the PR

### Post-Merge
1. Run the fix script
2. Verify the fixes
3. Commit the changes
4. Push to repository
5. Verify CI/CD passes

### Commands
```bash
# Pre-merge
python3 tools/test_comprehensive_validation.py

# Post-merge
python3 tools/test_apply_uri_fix.py --dry-run
python3 tools/test_apply_uri_fix.py
python3 tools/test_ontology_uris.py
git add ontology/
git commit -m "Fix: Update ontology URIs to GitHub Pages URL (Issue #8)"
git push
```

---

## Status

### Current Status
- ✅ Infrastructure enhanced with dynamic file discovery
- ✅ All validation tests working correctly
- ✅ Fix script can find and update all files
- ✅ Documentation complete
- ✅ CI/CD integration complete
- ⏳ Waiting for merge
- ⏳ Ontology files to be fixed after merge

### Success Criteria
- ✅ All infrastructure validation tests pass
- ✅ Dynamic file discovery works correctly
- ✅ All 13 files with invalid URIs are identified
- ✅ Fix script can update all files automatically
- ✅ Documentation is comprehensive and accurate
- ✅ CI/CD integration is complete

---

## Quick Reference

### Validation
```bash
python3 tools/test_ontology_uris.py              # Comprehensive
python3 tools/test_validate_uri.py               # Quick
python3 tools/test_comprehensive_validation.py   # All tests
```

### Fix
```bash
python3 tools/test_apply_uri_fix.py --dry-run    # Preview
python3 tools/test_apply_uri_fix.py              # Apply
bash tools/test_run_uri_validation.sh --fix      # Shell script
```

### Infrastructure
```bash
python3 tools/test_infrastructure_validation.py  # Validate infrastructure
python3 tools/test_fix_script_validation.py      # Validate fix script
```

---

## Need Help?

### Quick Help
- **Getting Started**: [Quick Start Guide](test_QUICK_START.md)
- **How to Fix**: [Fix Instructions](test_uri_fix_summary.md)
- **What Changed**: [Changes Summary](test_CHANGES_SUMMARY.md)
- **Complete Guide**: [Main Documentation](test_README.md)

### Detailed Help
- **Issue Context**: [Issue #8 Summary](test_ISSUE_8_SUMMARY.md)
- **PR Details**: [PR Summary](test_PR_SUMMARY.md)
- **Technical Overview**: [Final Summary](test_FINAL_SUMMARY.md)
- **Step-by-Step**: [Checklist](test_CHECKLIST.md)

---

## References

- **Issue**: https://github.com/metavacua/CategoricalReasoner/issues/8
- **CI/CD Workflow**: `.github/workflows/ontology-validation.yml`
- **Validation Scripts**: `tools/test_*.py`
- **Documentation**: `tools/test_*.md`

---

**Last Updated**: 2026-01-02
**Status**: ✅ Ready for Review and Merge
