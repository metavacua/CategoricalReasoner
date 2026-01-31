# AGENTS.md - Testing Framework

## Scope
The `tests/` directory contains automated tests for validation, consistency checking, and integration verification.

## Core Constraints
- **Framework**: Python unittest or pytest compatible. Tests validate success and failure scenarios.
- **Coverage**: All critical validation paths, error conditions, edge cases, and integration scenarios must have tests.
- **Integration**: Tests integrate with `.catty/validation/validate.py` and use same acceptance criteria as operational specifications.
- **Reporting**: Results in structured format for CI/CD. Exit codes indicate pass/fail status.

## Validation
Run tests with `python -m pytest tests/` or `python -m unittest discover tests/`. All tests must pass before deployment.

## See Also
- `.catty/AGENTS.md` - Validation framework and acceptance criteria
