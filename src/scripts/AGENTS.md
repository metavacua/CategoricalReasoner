# AGENTS.md - Scripts and Utilities

## Scope
The `src/scripts/` directory contains Python utilities for validation, automation, and RDF processing.

## Core Constraints
- **Python Version**: ≥3.8 with type hints and docstrings for all functions.
- **Dependencies**: Use `pyyaml`, `rdflib`, `argparse`, `logging`.
- **CLI Standards**: All scripts accept `--verbose/-v` flag, return exit code 0 (success) or 1 (failure), and log to stderr/stdout with timestamps.
- **Configuration**: Load YAML configs with validation of required fields. Fail gracefully on missing files or invalid syntax.
- **Integration**: Scripts must integrate with `.catty/validation/validate.py` output format for CI/CD compatibility.
- **Technology Normative Statement**: Python scripts are auxiliary CI/CD tools. Primary validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit). New script decisions must justify why Python is necessary rather than using Java equivalents.

## Validation
Test scripts with valid and invalid inputs. Verify exit codes, logging output, and error handling before deployment.

## See Also
- `.catty/AGENTS.md` - Validation framework integration
