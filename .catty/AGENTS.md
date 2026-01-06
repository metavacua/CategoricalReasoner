# Agent Guidelines for .catty Operations Model

## Overview

The `.catty/` directory contains the formal, machine-readable operational skeleton for Catty development. This AGENTS.md provides MCP-compliant specifications for coding agents working with the operational model.

## Agent Role Definition

### Operational Model Agent
**Primary Purpose**: Execute tasks according to formal specifications in the operations model
**Key Capabilities Required**:
- YAML parsing and interpretation
- Dependency graph analysis
- Task sequencing and execution
- Automated validation framework operation
- SHACL and JSON schema validation

## MCP-Compliant Operational Specifications

### Required Agent Capabilities

#### Technical Capabilities
```json
{
  "yaml_processing": {
    "read": ["operations.yaml", "phases.yaml"],
    "parse": true,
    "validate_syntax": true
  },
  "validation_execution": {
    "python_scripts": true,
    "shacl_validation": true,
    "json_schema_validation": true,
    "sparql_queries": true
  },
  "dependency_resolution": {
    "topological_sort": true,
    "cycle_detection": true,
    "parallelization_analysis": true
  }
}
```

#### Knowledge Requirements
- **Operations Research**: Dependency graphs, critical path analysis
- **Validation Technologies**: SHACL, JSON Schema, automated testing
- **Software Engineering**: Task orchestration, artifact management
- **Formal Methods**: Boolean acceptance criteria, testable specifications

### Core Operational Files

#### operations.yaml
**Purpose**: Complete artifact and task registry
**Agent Actions Required**:
1. Parse YAML structure with artifact and task definitions
2. Extract task descriptions for execution
3. Identify dependencies for prerequisite resolution
4. Validate acceptance criteria against outputs
5. Reference validation scripts for verification

**Key Sections**:
- `artifacts`: Complete artifact specifications with paths, formats, schemas
- `tasks`: Task definitions with phases, dependencies, descriptions
- `metadata`: Version control and changelog information

#### phases.yaml
**Purpose**: Dependency graph and execution sequencing
**Agent Actions Required**:
1. Load dependency matrix for task sequencing
2. Identify critical path for duration optimization
3. Determine parallelization opportunities
4. Validate dependency resolution before execution
5. Report execution status and timing

**Key Information**:
- Phase definitions and task groupings
- Dependency relationships between tasks
- Critical path analysis
- Parallelization opportunities

#### validation/validate.py
**Purpose**: Unified validation framework
**Agent Actions Required**:
1. Execute validation for specific artifacts or tasks
2. Parse and report validation results
3. Provide diagnostic information for failures
4. Support batch validation operations
5. Integrate with acceptance criteria testing

### Task Execution Protocol

#### Pre-Execution Checklist
1. **Load operations model**: Parse operations.yaml and phases.yaml
2. **Verify dependencies**: Check all required artifacts exist
3. **Load specifications**: Read artifact content specifications
4. **Prepare validation**: Initialize validation framework
5. **Plan execution**: Determine task sequence and dependencies

#### Execution Steps
1. **Select task**: Choose task by task_id from operations.yaml
2. **Check prerequisites**: Verify all depends_on artifacts exist
3. **Read specifications**: Load artifact content specifications
4. **Execute operations**: Follow operational description step-by-step
5. **Create outputs**: Generate artifacts at specified paths
6. **Validate results**: Run validation scripts and acceptance criteria
7. **Report status**: Provide execution and validation status

#### Post-Execution Protocol
1. **Verify success**: All acceptance criteria must pass
2. **Update dependencies**: Note artifacts created for dependent tasks
3. **Report results**: Provide detailed execution and validation report
4. **Handle failures**: Apply corrective actions and re-validate
5. **Log completion**: Record successful task completion

### Artifact Management

#### Artifact Types and Validation

| Type | Format | Validation Method | Tools |
|------|--------|------------------|--------|
| JSON-LD | JSON-LD | JSON syntax + RDF parsing + SHACL | json, rdflib, pyshacl |
| RDF/Turtle | Turtle | Turtle syntax + SHACL | rdflib, pyshacl |
| LaTeX | LaTeX | Syntax + structure + compilation | pdflatex, regex |
| Markdown | Markdown | Syntax + content checks | regex, line counting |
| Python | Python | Syntax + imports + entry point | compile(), AST |

#### SHACL Validation Framework
**Purpose**: Structural constraints for RDF artifacts
**Agent Actions**:
1. Load data graph and shapes graph separately
2. Verify both parse without errors
3. Execute SHACL validation
4. Report constraint violations with fixes
5. Iterate until all constraints pass

**Example Constraint Types**:
- Property constraints: `sh:minCount`, `sh:maxCount`, `sh:datatype`
- Value constraints: `sh:minInclusive`, `sh:maxInclusive`, `sh:pattern`
- SPARQL constraints: Custom validation logic

### Validation Protocols

#### Single Artifact Validation
```bash
python .catty/validation/validate.py --artifact <artifact_id>
```

#### Task Validation
```bash
python .catty/validation/validate.py --task <task_id>
```

#### Batch Validation
```bash
python .catty/validation/validate.py --all
```

### Acceptance Criteria Framework

#### Criteria Categories
1. **Existence**: File must exist at specified path
2. **Syntax**: File must parse without errors
3. **Content**: Required elements must be present
4. **Structure**: Must conform to schema specifications
5. **Validation**: Must pass automated validation tests

#### Boolean Test Examples
✅ **Good (testable)**:
- "file ontology/logics-as-objects.jsonld exists"
- "valid JSON-LD syntax (parse without error)"
- "at least 7 logic instances defined"
- "validates against SHACL shape"

❌ **Bad (subjective)**:
- "produce high-quality ontology"
- "write comprehensive documentation"

### Error Handling Protocols

#### Validation Failure Protocol
1. **Read error messages** from validation script output
2. **Identify specific failure** point and criteria
3. **Apply corrective actions** according to specifications
4. **Re-run validation** to verify fixes
5. **Iterate until success** or identify blocking issues
6. **Report final status** with diagnostic information

#### Dependency Resolution Failure
1. **Check depends_on list** for the task
2. **Verify all dependencies** exist and are valid
3. **Find missing dependencies** and their producing tasks
4. **Execute prerequisite tasks** in correct order
5. **Verify resolution** before proceeding
6. **Report dependency status** and execution plan

#### Invalid Specifications Protocol
1. **Validate operations.yaml** syntax and structure
2. **Check artifact specifications** for completeness
3. **Verify task definitions** for required fields
4. **Validate dependency graph** for cycles and completeness
5. **Report specification issues** with correction guidance
6. **Request specifications update** if structural changes needed

### Quality Assurance

#### Pre-Execution Quality Checks
- Operations model is syntactically valid
- All referenced artifacts and tasks exist
- Dependency graph is acyclic and complete
- Validation framework is functional
- Agent capabilities match requirements

#### Execution Quality Monitoring
- Tasks execute according to operational descriptions
- Outputs match content specifications
- Validation criteria are properly tested
- Dependencies are correctly resolved
- Error handling is appropriate and complete

#### Post-Execution Quality Verification
- All acceptance criteria evaluate to true
- Validation framework produces consistent results
- Artifacts integrate correctly with existing system
- No breaking changes to specifications or dependencies
- Documentation remains accurate and complete

### Extension Protocols

#### Adding New Artifacts
1. **Choose unique artifact_id** following naming conventions
2. **Define complete specifications** in operations.yaml
3. **Create validation schema** if required (SHACL or JSON)
4. **Update task dependencies** to include new artifact
5. **Test validation framework** with new artifact
6. **Update documentation** to reflect changes

#### Adding New Tasks
1. **Choose unique task_id** following naming conventions
2. **Assign appropriate phase** based on dependencies
3. **Define operational description** (step-by-step instructions)
4. **Specify acceptance criteria** (testable boolean conditions)
5. **Update dependency graph** in phases.yaml
6. **Test task execution** and validation
7. **Update documentation** to reflect changes

### Performance Optimization

#### Critical Path Analysis
- Identify longest dependency chain
- Optimize sequential task execution
- Maximize parallelization opportunities
- Minimize validation overhead
- Streamline artifact dependencies

#### Parallel Execution Guidelines
- Tasks with no interdependencies can run simultaneously
- Tasks with shared dependencies should be sequenced
- Validation tasks can run in parallel
- Artifact generation and validation can overlap
- Monitor resource usage and constraints

### Integration Protocols

#### Repository Integration
- Preserve existing file structure and naming
- Maintain backward compatibility
- Update documentation with changes
- Follow version control protocols
- Ensure CI/CD compatibility

#### External Tool Integration
- Maintain compatibility with standard RDF tools
- Support standard LaTeX compilation workflows
- Integrate with existing validation frameworks
- Preserve academic writing standards
- Support semantic web best practices

## Emergency Procedures

### System Failure Protocol
1. **Stop all operations** immediately
2. **Identify failure scope** and affected components
3. **Check operational model** for consistency
4. **Restore from last known good state** if necessary
5. **Re-validate entire system** before resuming operations
6. **Report incident** with diagnostic information

### Specification Conflict Resolution
1. **Document conflicting requirements** clearly
2. **Identify specification source** and priority
3. **Check version control** for recent changes
4. **Request clarification** from specification owners
5. **Implement resolution** according to updated specifications
6. **Validate resolution** with affected components

### Task Execution Workflow

#### Detailed Task Execution Steps
1. **Read Task Specification**: Parse operations.yaml and extract task details
   ```python
   task_id = "task:build-logics-as-objects"
   task = operations['tasks'][task_id]
   produces = task['produces']
   depends_on = task['depends_on']
   description = task['description']
   criteria = task['acceptance_criteria']
   ```

2. **Verify Dependencies**: Check all required artifacts exist
   ```python
   for artifact_id in task['depends_on']:
       artifact = operations['artifacts'][artifact_id]
       artifact_path = project_root / artifact['path']
       if not artifact_path.exists():
           # Execute prerequisite task first
   ```

3. **Execute Operational Instructions**: Follow step-by-step descriptions
4. **Validate Results**: Run validation scripts and acceptance criteria
5. **Report Completion**: Provide execution and validation status

#### Dependency Resolution Algorithm
```python
def resolve_dependencies(task_id, operations):
    """Resolve task dependencies recursively."""
    task = operations['tasks'][task_id]
    
    for artifact_id in task['depends_on']:
        if not artifact_exists(artifact_id):
            producing_task = find_producing_task(artifact_id, operations)
            resolve_dependencies(producing_task, operations)
    
    execute_task(task_id)
```

#### Critical Path Analysis
The **critical path** (longest dependency chain) determines minimum project duration:

**Sequential duration**: ~400 minutes (6.7 hours)
**Optimized duration**: ~260 minutes (4.3 hours) with parallelization
**Parallelization savings**: ~140 minutes (2.3 hours)

**Critical Path Tasks**:
1. conduct-semantic-web-audit (30 min)
2. build-categorical-schema (20 min)
3. build-logics-as-objects (25 min)
4. build-morphism-catalog (20 min)
5. build-two-d-lattice (30 min)
6. build-curry-howard-model (25 min)
7. build-complete-example (30 min)
8. write-ontology-documentation (40 min)
9. write-audit-chapter (60 min)
10. write-conclusions (30 min)
11. build-thesis-pdf (10 min)
12. build-validation-framework (60 min)
13. validate-ontology (20 min)

#### Phase Execution Model

**Phase 0: Foundation (30 minutes)**
- `init-repository-structure`: Initialize repository structure
- `conduct-semantic-web-audit`: Conduct semantic web audit of external resources
- **Parallelizable**: Yes

**Phase 1: Core Ontology (220 minutes)**
- Subphase 1a: Schema Foundation (`build-categorical-schema`)
- Subphase 1b: Logic Definitions (`build-logics-as-objects`, `build-morphism-catalog`)
- Subphase 1c: Advanced Structures (`build-two-d-lattice`, `build-curry-howard-model`)
- Subphase 1d: Examples and Validation (`build-complete-example`, `build-shacl-shapes`, `write-ontology-documentation`)
- **Parallelizable**: Partial (within subphases)

**Phase 2: Thesis (145 minutes)**
- Subphase 2a: Thesis Structure (`init-thesis-structure`)
- Subphase 2b: Chapter Writing (`write-introduction`, `write-audit-chapter`, `write-conclusions`)
- Subphase 2c: PDF Compilation (`build-thesis-pdf`)
- **Parallelizable**: Partial (chapters can be written in parallel)

**Phase 3: Validation Framework (80 minutes)**
- Subphase 3a: Framework Construction (`build-validation-framework`)
- Subphase 3b: Artifact Validation (`validate-ontology`, `validate-thesis`)
- **Parallelizable**: Partial (validation tasks can run in parallel)

### Quick Start for Agents

#### For Coding Agents

**Execute a task:**
```python
import yaml

with open('.catty/operations.yaml', 'r') as f:
    operations = yaml.safe_load(f)

task = operations['tasks']['task:build-logics-as-objects']
print(task['description'])  # Operational instructions
print(task['acceptance_criteria'])  # Boolean validation checks
```

**Execute a task workflow:**
1. Verify dependencies exist
2. Read artifact specifications
3. Follow operational description
4. Create artifact at specified path
5. Validate against acceptance criteria
6. Run validation script

#### For Humans

**View all artifacts:**
```bash
python .catty/validation/validate.py --list-artifacts
```

**View all tasks:**
```bash
python .catty/validation/validate.py --list-tasks
```

**Validate an artifact:**
```bash
python .catty/validation/validate.py --artifact catty-categorical-schema
```

**Understand dependencies:**
```bash
cat .catty/phases.yaml
```

#### Key Operational Commands

**Validate single artifact:**
```bash
python .catty/validation/validate.py --artifact logics-as-objects
```

**Validate all artifacts from a task:**
```bash
python .catty/validation/validate.py --task task:build-logics-as-objects
```

**Expected validation output:**
```
======================================================================
Validating artifact: logics-as-objects
Path: /path/to/ontology/logics-as-objects.jsonld
Format: JSON-LD
======================================================================

✓ Valid JSON syntax
✓ Contains @context
✓ Contains catty prefix
✓ Parses as RDF graph (42 triples)

Checking content specifications:
  ✓ JSON-LD @context referencing catty-categorical-schema...
  ✓ Logic instances: LM, LK, LJ, LDJ, LL, ALL, RLL (minimum 7)...
  ✓ Each logic has: rdf:type catty:Logic...

✓ SHACL validation passed

✓ Validation PASSED for logics-as-objects
```

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: operations.yaml, phases.yaml, validation/  
**Last Updated**: 2025-01-06