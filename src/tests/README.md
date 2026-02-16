# Tests Directory

## Purpose

The `src/tests/` directory contains the test suite for validation, consistency checking, and integration verification. Tests ensure that all artifacts meet quality standards and constraints.

## Architecture

The test suite uses a hybrid approach:

1. **Python Tests** - For thesis validation (temporary, pragmatic CI/CD)
2. **Future Java Tests** - For code generation and transformation (long-term goal)

## Current Test Suite

### `test_consistency.py`

Main test file for validating thesis consistency.

**Purpose**: Verify that thesis structure and citations satisfy all constraints.

**Test Categories**:
1. **TeX Structure Tests**
   - All chapter IDs follow pattern `sec-[lowercase-hyphenated]`
   - All theorem IDs follow pattern `thm-[lowercase-hyphenated]`
   - All definition IDs follow pattern `def-[lowercase-hyphenated]`
   - All lemma IDs follow pattern `lem-[lowercase-hyphenated]`
   - All example IDs follow pattern `ex-[lowercase-hyphenated]`
   - All IDs are globally unique (no duplicates)

2. **Citation Tests** (TEMPORARILY DISABLED)
   - All `\cite{key}` references exist in citation registry
   - Citation keys match format `[author][year][keyword]`
   - External links (DOI, URL, Wikidata, arXiv) are resolvable
   - Note: Citation validation is temporarily disabled pending Java/RO-Crate implementation. See `docs/dissertation/bibliography/README.md` for details.

3. **Content Tests**
   - Theorems have required fields (statement, proof)
   - Definitions have required fields (term, meaning)
   - Examples have required fields (description, instantiation)

**Running Tests**:
```bash
# Run all tests
python -m pytest src/tests/

# Run specific test file
python -m pytest src/tests/test_consistency.py

# Run with verbose output
python -m pytest src/tests/ -v

# Run specific test
python -m pytest src/tests/ -k test_citation_uniqueness
```

**Running with unittest**:
```bash
python -m unittest discover src/tests/
```

## Test Structure

### Test Class Organization

```python
import unittest

class TestThesisStructure(unittest.TestCase):
    def test_chapter_id_pattern(self):
        """Test that all chapter IDs match required pattern."""
        pass

    def test_theorem_id_pattern(self):
        """Test that all theorem IDs match required pattern."""
        pass

class TestCitations(unittest.TestCase):
    def test_citation_keys_exist(self):
        """Test that all citations reference registry keys."""
        pass

    def test_external_links_resolve(self):
        """Test that external links are resolvable."""
        pass
```

### Test Naming Conventions

- Test files: `test_*.py`
- Test classes: `Test*`
- Test methods: `test_*`

## Test Coverage

### Required Coverage Areas

1. **Thesis Structure**
   - ID pattern validation (chapters, sections, theorems, definitions, lemmas, examples)
   - ID uniqueness (no duplicates across corpus)
   - Required fields (theorems: statement, proof; definitions: term, meaning)
   - Proper nesting and structure

2. **Citation Registry** (TEMPORARILY DISABLED)
   - All TeX citations reference registry entries
   - Registry entries have required fields (author, title, year, type)
   - Citation keys follow correct format
   - External links (if present) are resolvable
   - Note: Citation validation is temporarily disabled pending Java/RO-Crate implementation. See `docs/dissertation/bibliography/README.md` for details.

3. **Integration**
   - Cross-reference validation (chapters reference each other correctly)
   - Citation usage tracking (who cites what) - disabled pending implementation
   - Consistency between TeX and registry - disabled pending implementation

### Coverage Metrics

Track coverage with:
```bash
pytest --cov=tests --cov-report=html
```

**Goal**: ≥80% coverage of validation logic.

## Technology Note

### Current State (Python)

Tests currently use Python for pragmatic CI/CD orchestration:
- `unittest` or `pytest` framework
- Quick to write and execute
- Easy integration with CI/CD workflows

### Future State (Java)

Long-term test infrastructure should use Java:
- **JUnit** for unit tests of Java validation code
- **Jena SHACL** for RDF validation tests
- **Integration tests** for code generation pipelines

**Java Test Examples** (future):
```java
@Test
public void testSHACLValidation() {
    Model data = JenaUtil.loadModel("test-data.ttl");
    Model shapes = JenaUtil.loadModel("shapes.shacl");
    boolean valid = SHACLValidator.validate(data, shapes);
    assertTrue(valid, "Data must conform to SHACL shapes");
}
```

## CI/CD Integration

### GitHub Actions Workflow

Tests run automatically on every pull request:

```yaml
- name: Run tests
  run: |
    python -m pytest src/tests/ -v --tb=short

- name: Generate coverage report
  run: |
    pytest --cov=tests --cov-report=xml
```

### Test Requirements for Merge

All tests must pass before merging:
- Exit code 0 (success)
- No test failures or errors
- Coverage meets minimum threshold (if configured)

## Test Development Guidelines

### Writing Good Tests

**DO**:
- Test specific, verifiable behavior
- Use descriptive test names (`test_citation_key_exists_in_registry`)
- Test both success and failure scenarios
- Keep tests independent (no shared state)
- Use fixtures for common test data

**DON'T**:
- Test implementation details (test behavior, not code)
- Write overly complex tests that are hard to maintain
- Skip error cases or edge cases
- Depend on external resources without mocking

### Test Data Management

- Store test data in `src/tests/fixtures/` directory
- Use small, representative datasets
- Document expected test data structure
- Keep test data versioned with code

### Mocking External Dependencies

Mock external resources to ensure tests are reliable and fast:

```python
from unittest.mock import patch

@patch('requests.get')
def test_external_link_resolution(mock_get):
    mock_get.return_value.status_code = 200
    result = check_link_resolves("https://example.com")
    assertTrue(result)
```

## Debugging Failed Tests

### Running Individual Tests

```bash
# Run specific test function
python -m pytest src/tests/test_consistency.py::TestCitations::test_citation_keys_exist -v

# Run with debugging
python -m pytest src/tests/ --pdb
```

### Common Failure Modes

**Test Timeout**: External resource not responding
- Fix: Mock the external resource or increase timeout

**Import Error**: Missing dependency
- Fix: Install with `pip install <package>`

**Assertion Error**: Unexpected result
- Fix: Debug with `--pdb` flag or add print statements

## Future Enhancements

### Planned Test Categories

1. **JavaPoet Code Generation Tests**
   - Validate generated Java code compiles
   - Test code generation templates
   - Verify output satisfies constraints

2. **Jena Integration Tests**
   - Test RDF loading and parsing
   - Validate SPARQL query execution
   - Test reasoning with OpenLlet

3. **End-to-End Integration Tests**
   - Test complete workflow: TeX → validation → metadata extraction
   - Validate CI/CD pipeline execution
   - Test deployment artifacts

### Test Performance Optimization

- Parallel test execution: `pytest -n auto` (with pytest-xdist)
- Test caching: `--cache-show` to see cached results
- Selective test execution: `pytest -k "citation"` to run only citation tests

## See Also

- `src/tests/AGENTS.md` - Testing framework constraints
- `src/schema/README.md` - Validation schemas and constraints
- `src/scripts/README.md` - Utility scripts for validation
- `src/schema/validators/` - Validation scripts that tests verify
