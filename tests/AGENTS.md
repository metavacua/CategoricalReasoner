# Agent Guidelines for Testing Framework

## Overview

The `tests/` directory contains automated testing scripts for the Catty thesis project. This AGENTS.md provides MCP-compliant specifications for agents developing and executing test cases.

## Agent Role Definition

### Test Development Agent
**Primary Purpose**: Develop and maintain automated tests for validation and consistency checking

**Key Capabilities Required**:
- Python testing framework development
- Automated consistency validation
- Test case design and implementation
- Result validation and reporting
- Integration with validation frameworks

## MCP-Compliant Test Specifications

### Required Agent Capabilities

#### Test Framework Development
```json
{
  "testing_operations": {
    "unit_tests": true,
    "integration_tests": true,
    "consistency_tests": true,
    "automated_validation": true
  },
  "test_execution": {
    "python_unittest": true,
    "pytest_compatibility": true,
    "continuous_integration": true,
    "result_reporting": true
  }
}
```

### Test Development Protocols

#### Test Structure Standards
- **Test naming**: Descriptive test function names
- **Assertion patterns**: Clear, specific assertions
- **Error reporting**: Detailed failure information
- **Documentation**: Test purpose and expected behavior

#### Integration with Validation Framework
- Tests must integrate with `.catty/validation/validate.py`
- Must use same validation criteria as operational specifications
- Should validate both success and failure scenarios
- Must report results in structured format

### Quality Assurance

#### Test Coverage
- [ ] All critical validation paths are tested
- [ ] Error conditions are properly tested
- [ ] Edge cases are covered
- [ ] Integration scenarios are validated
- [ ] Performance benchmarks are established

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: .catty/, schema/, ontology/  
**Last Updated**: 2025-01-06
