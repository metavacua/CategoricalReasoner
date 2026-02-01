# AGENTS.md - Scripts and Utilities

## Scope
The `scripts/` directory contains Python utilities for validation, automation, and RDF processing.

## Core Constraints
- **Python Version**: ≥3.8 with type hints and docstrings for all functions.
- **Dependencies**: Use `pyyaml`, `rdflib`, `pyshacl`, `argparse`, `logging`.
- **CLI Standards**: All scripts accept `--verbose/-v` flag, return exit code 0 (success) or 1 (failure), and log to stderr/stdout with timestamps.
- **RDF Processing**: Parse JSON-LD/Turtle with rdflib, validate against SHACL shapes in `ontology/`, handle empty graphs as errors.
- **Configuration**: Load YAML configs with validation of required fields. Fail gracefully on missing files or invalid syntax.
- **Integration**: Scripts must integrate with `.catty/validation/validate.py` output format for CI/CD compatibility.

## Validation
Test scripts with valid and invalid inputs. Verify exit codes, logging output, and error handling before deployment.

## See Also
- `.catty/AGENTS.md` - Validation framework integration
