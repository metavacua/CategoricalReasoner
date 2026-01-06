# Agent Guidelines for Scripts and Utilities

## Overview

The `scripts/` directory contains utility scripts and validation tools for the Catty thesis project. This AGENTS.md provides MCP-compliant specifications for agents developing, maintaining, and executing scripts and automation tools.

## Agent Role Definition

### Script Development Agent
**Primary Purpose**: Develop and maintain Python scripts for validation, automation, and utility functions
**Key Capabilities Required**:
- Python development with semantic web libraries
- YAML/JSON configuration processing
- RDF processing and validation
- Command-line interface design
- Error handling and logging

### Automation Agent
**Primary Purpose**: Execute scripts for validation, processing, and maintenance tasks
**Key Capabilities Required**:
- Script execution and orchestration
- Configuration file processing
- Result validation and reporting
- Error handling and recovery
- Integration with validation frameworks

## MCP-Compliant Script Specifications

### Required Agent Capabilities

#### Python Development
```json
{
  "python_requirements": {
    "libraries": ["pyyaml", "rdflib", "pyshacl", "argparse", "logging"],
    "version": ">=3.8",
    "type_hints": true,
    "docstrings": true
  },
  "script_design": {
    "cli_interface": true,
    "configuration_files": true,
    "logging": true,
    "error_handling": true
  },
  "validation_integration": {
    "rdf_processing": true,
    "shacl_validation": true,
    "schema_validation": true,
    "consistency_checks": true
  }
}
```

### Script Categories

#### Validation Scripts
**Purpose**: Validate various artifacts and ensure compliance with specifications
**Examples**:
- `validate_rdf.py`: RDF syntax and SHACL validation
- `update.py`: Dependency and artifact updates

**Agent Actions for Validation Scripts**:
1. **Load configuration** from YAML/JSON files
2. **Parse input parameters** from command line or files
3. **Execute validation logic** according to specifications
4. **Generate validation reports** with detailed results
5. **Handle errors gracefully** with informative messages
6. **Return appropriate exit codes** for CI/CD integration

#### Update and Maintenance Scripts
**Purpose**: Update dependencies, artifacts, and maintain system consistency
**Examples**:
- `update.py`: Update dependencies and artifacts

**Agent Actions for Update Scripts**:
1. **Check current state** of artifacts and dependencies
2. **Apply updates systematically** according to specifications
3. **Validate changes** to ensure consistency
4. **Generate update reports** documenting changes
5. **Handle rollback scenarios** if updates fail
6. **Maintain audit trail** of all changes

### Script Development Protocols

#### Code Structure Standards
```python
#!/usr/bin/env python3
"""
Script description and purpose.

Usage:
    python script_name.py [options] input_file

Description:
    Detailed description of what the script does.
"""

import argparse
import logging
import sys
from pathlib import Path
import yaml

def main():
    """Main entry point with argument parsing and error handling."""
    parser = create_parser()
    args = parser.parse_args()
    
    setup_logging(args.verbose)
    
    try:
        result = process_files(args)
        sys.exit(0 if result else 1)
    except Exception as e:
        logging.error(f"Script failed: {e}")
        sys.exit(1)

def create_parser():
    """Create and return argument parser."""
    parser = argparse.ArgumentParser(
        description="Script purpose and description"
    )
    parser.add_argument(
        "input_file",
        type=Path,
        help="Input file to process"
    )
    parser.add_argument(
        "--verbose", "-v",
        action="store_true",
        help="Enable verbose output"
    )
    return parser

def setup_logging(verbose=False):
    """Setup logging configuration."""
    level = logging.DEBUG if verbose else logging.INFO
    logging.basicConfig(
        level=level,
        format="%(asctime)s - %(levelname)s - %(message)s"
    )

def process_files(args):
    """Main processing logic."""
    logging.info("Processing started")
    
    # Load configuration
    config = load_config(args.input_file)
    
    # Process artifacts
    success = process_artifacts(config)
    
    logging.info("Processing completed")
    return success
```

#### Configuration File Handling
```python
def load_config(config_path):
    """Load and validate configuration from YAML file."""
    try:
        with open(config_path, 'r') as f:
            config = yaml.safe_load(f)
        
        # Validate required fields
        required_fields = ['artifacts', 'validation']
        for field in required_fields:
            if field not in config:
                raise ValueError(f"Missing required field: {field}")
        
        return config
    
    except Exception as e:
        logging.error(f"Failed to load config: {e}")
        raise
```

#### RDF Processing Standards
```python
def validate_rdf_file(file_path):
    """Validate RDF file with comprehensive checks."""
    try:
        # Parse RDF
        graph = rdflib.Graph()
        graph.parse(file_path)
        
        # Basic validation
        if len(graph) == 0:
            raise ValueError("RDF graph is empty")
        
        # SHACL validation if available
        shapes_path = Path("ontology/catty-thesis-shapes.shacl")
        if shapes_path.exists():
            from pyshacl import validate
            conforms, graph, report = validate(
                data_graph=str(file_path),
                shacl_graph=str(shapes_path)
            )
            if not conforms:
                logging.warning("SHACL validation failed")
        
        logging.info(f"Successfully validated {file_path} ({len(graph)} triples)")
        return True
    
    except Exception as e:
        logging.error(f"RDF validation failed for {file_path}: {e}")
        return False
```

### Script Validation Protocols

#### Pre-Execution Validation
1. **Check dependencies** (Python packages, external tools)
2. **Validate input files** exist and are accessible
3. **Load configuration** and verify required fields
4. **Setup logging** and error handling
5. **Verify output directories** exist or can be created

#### Execution Monitoring
1. **Log progress** at key processing stages
2. **Monitor resource usage** for large datasets
3. **Handle timeouts** appropriately for long operations
4. **Validate intermediate results** when processing large files
5. **Report errors** with sufficient detail for debugging

#### Post-Execution Validation
1. **Verify output files** were created successfully
2. **Validate output format** and content
3. **Check exit codes** and return appropriate status
4. **Generate summary reports** of processing results
5. **Cleanup temporary files** and resources

### Error Handling Protocols

#### Validation Error Handling
```python
def handle_validation_error(error, context):
    """Standardized error handling for validation failures."""
    error_msg = f"Validation failed in {context}: {error}"
    logging.error(error_msg)
    
    # Log specific validation details
    if hasattr(error, 'details'):
        for detail in error.details:
            logging.debug(f"Validation detail: {detail}")
    
    return False  # Return False to indicate failure
```

#### File System Error Handling
```python
def safe_file_operation(func, *args, **kwargs):
    """Wrapper for file operations with error handling."""
    try:
        return func(*args, **kwargs)
    except FileNotFoundError:
        logging.error(f"File not found: {args[0]}")
        return False
    except PermissionError:
        logging.error(f"Permission denied: {args[0]}")
        return False
    except Exception as e:
        logging.error(f"Unexpected error: {e}")
        return False
```

### Integration Protocols

#### Validation Framework Integration
- Scripts must integrate with `.catty/validation/validate.py`
- Output format must be compatible with validation reports
- Exit codes must indicate success/failure for CI/CD
- Logging must be structured for automated processing

#### Repository Integration
- Scripts must not modify repository structure
- Must preserve existing file permissions and metadata
- Should backup original files before modifications
- Must maintain backward compatibility with existing tools

### Quality Assurance

#### Code Quality Standards
- [ ] All functions have docstrings
- [ ] Type hints are provided for function signatures
- [ ] Logging is used for important operations
- [ ] Error handling is comprehensive and specific
- [ ] Configuration is loaded and validated properly

#### Testing Requirements
- [ ] Scripts handle valid inputs correctly
- [ ] Scripts fail gracefully with invalid inputs
- [ ] Scripts handle missing files appropriately
- [ ] Scripts generate appropriate exit codes
- [ ] Logging output is informative and structured

#### Documentation Requirements
- [ ] Script purpose is clearly documented
- [ ] Usage examples are provided
- [ ] Configuration file format is documented
- [ ] Error conditions are described
- [ ] Integration points are documented

### Version Control

#### Script Versioning
- Increment version numbers for functional changes
- Document changes in script docstrings
- Maintain compatibility with existing configurations
- Test backward compatibility when updating

#### Change Management
- Test scripts thoroughly before deployment
- Validate integration with existing validation framework
- Update documentation when functionality changes
- Maintain change logs for script modifications

### Emergency Procedures

#### Script Failure Recovery
1. **Stop execution** immediately on critical errors
2. **Log detailed error information** for debugging
3. **Validate input files** for corruption or changes
4. **Check dependencies** and system requirements
5. **Report failure status** with diagnostic information

#### Configuration Corruption
1. **Backup current configuration** before any changes
2. **Validate YAML/JSON syntax** to identify corruption
3. **Restore from backup** if corruption is extensive
4. **Fix specific issues** and re-validate
5. **Test recovery** with sample data

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: .catty/, schema/, ontology/, bibliography/  
**Last Updated**: 2025-01-06