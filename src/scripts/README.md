# Scripts Directory

## Purpose

The `src/scripts/` directory contains Python utility scripts for CI/CD orchestration, RDF processing, and transformation tasks. These scripts support the Java-primary architecture by handling auxiliary tasks.

## Architecture Context

The Catty project uses Java as its primary ecosystem (Jena, OpenLlet, JavaPoet, JUnit) for core validation and transformation. Python scripts in this directory provide auxiliary support for:

- Extracting citation metadata from TeX sources
- Running validation orchestration in CI/CD workflows
- Transforming data formats where Python provides convenience
- Glue scripts for integrating disparate tools

## Technology Note

Python ≥3.8 with type hints and docstrings for all functions.

**Normative rule**: All new scripts must justify why Python is preferable to Java. If functionality could be implemented in Java using standard libraries (Jena, OpenLlet, JUnit), prefer Java. Python is acceptable for glue/orchestration only.

## Core Scripts

### `validate_rdf.py`

Validates RDF/Turtle files for syntax and structure.

**Purpose**: Check RDF files parse correctly and satisfy basic constraints.

**Note**: Currently uses Python (pyshacl, rdflib) for pragmatic CI/CD orchestration. Could be replaced with Java validator using Jena SHACL support.

**Usage**:
```bash
python src/scripts/validate_rdf.py --file path/to/file.ttl
```

### `update.py`

Utility script for updating project metadata and configurations.

**Purpose**: Orchestrate updates across multiple configuration files and directories.

**Usage**:
```bash
python src/scripts/update.py [options]
```

## Script Development Guidelines

### When to Use Python

**Appropriate**:
- CI/CD glue scripts that orchestrate multiple tools
- Quick transformations for development convenience
- Data extraction tasks where Python's libraries provide clear advantages
- Prototype scripts that may later be reimplemented in Java

**Not Appropriate**:
- Core validation logic (use JUnit + Jena)
- Semantic web reasoning (use OpenLlet)
- Code generation (use JavaPoet)
- Production-critical transformations (use Java)

### Script Requirements

All scripts must:

1. **Python ≥3.8** with type hints
2. **Docstrings** for all functions and modules
3. **CLI Standards**: Accept `--verbose/-v` flag, return exit code 0 (success) or 1 (failure)
4. **Logging**: Log to stderr/stdout with timestamps
5. **Error Handling**: Fail gracefully on missing files or invalid syntax
6. **Configuration**: Load YAML configs with validation of required fields

### Integration

Scripts must integrate with `.catty/validation/validate.py` output format for CI/CD compatibility.

## Future Replacements

As the project matures, the following Python scripts may be replaced with Java equivalents:

- `validate_rdf.py` → Jena SHACL validation + JUnit tests
- Other transformation scripts → JavaPoet-generated code + JUnit

## See Also

- `.catty/README.md` - Operational model and validation framework
- `src/schema/README.md` - Validation schemas and constraints
- `src/tests/README.md` - Test suite documentation
