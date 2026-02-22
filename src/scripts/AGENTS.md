# AGENTS.md - Scripts and Utilities

## Scope
The `src/scripts/` directory contains Python utilities for validation, automation, and RDF processing.

## Core Constraints
- **Python Version**: ≥3.8 with type hints and docstrings for all functions.
- **Dependencies**: Use `pyyaml`, `rdflib`, `argparse`, `logging`.
- **CLI Standards**: All scripts accept `--verbose/-v` flag, return exit code 0 (success) or 1 (failure), and log to stderr/stdout with timestamps.
- **Configuration**: Load YAML configs with validation of required fields. Fail gracefully on missing files or invalid syntax.
- **Technology Normative Statement**: Python scripts are auxiliary CI/CD tools. Primary validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit). New script decisions must justify why Python is necessary rather than using Java equivalents.
- **Markdown Support**: Scripts should handle both `.md` and `.tex` files as the workflow transitions from TeX-primary to Markdown-primary.

## Validation
Test scripts with valid and invalid inputs. Verify exit codes, logging output, and error handling before deployment.

## Migration Note
The validation infrastructure is transitioning from Python to Java. See the long-term goal in `src/schema/AGENTS.md`.

## See Also
- `src/schema/AGENTS.md` - Schema policies
- `src/tests/AGENTS.md` - Test policies
