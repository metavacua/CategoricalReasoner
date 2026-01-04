# Catty Operational Model

## Overview

This document provides an entry point to the **Catty Operations Model** - a formal, machine-readable specification of the complete task/artifact system for Catty development.

The operational model is located in the `.catty/` directory and serves as the **execution contract** between human specifications and coding agent implementation.

## What is the Operations Model?

The operations model eliminates interpretation ambiguity by defining:

1. **All artifacts** (30+ items)
   - Paths, formats, schemas, dependencies
   - Content specifications (structured, not prose)
   - Validation criteria (testable conditions)

2. **All tasks** (20+ items)
   - What they produce, what they depend on
   - Operational instructions (step-by-step)
   - Acceptance criteria (boolean tests)

3. **Complete dependency graph**
   - Task sequencing across 4 phases
   - Parallelization opportunities
   - Critical path analysis

4. **Validation framework**
   - SHACL shapes for RDF artifacts
   - JSON schemas for structured data
   - Automated validation scripts

## Directory Structure

```
.catty/
├── operations.yaml          # Main operational model (artifact & task registry)
├── phases.yaml              # Dependency graph and phase sequencing
├── validation/
│   ├── validate.py          # Automated validation script
│   ├── shapes/              # SHACL shape files for RDF validation
│   │   ├── categorical-schema.shacl
│   │   ├── logics-as-objects.shacl
│   │   ├── morphism-catalog.shacl
│   │   ├── two-d-lattice-category.shacl
│   │   ├── curry-howard-model.shacl
│   │   └── complete-example.shacl
│   └── thesis-structure.json  # JSON Schema for thesis structure
├── README.md                # Comprehensive documentation
├── QUICKSTART.md            # Quick start guide
├── TASK_EXECUTION_GUIDE.md  # Detailed task execution workflow
└── DEPENDENCY_GRAPH.md      # Visual dependency graph
```

## Quick Start

### For Humans

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

### For Coding Agents

**Read a task:**
```python
import yaml

with open('.catty/operations.yaml', 'r') as f:
    operations = yaml.safe_load(f)

task = operations['tasks']['task:build-logics-as-objects']
print(task['description'])  # Operational instructions
print(task['acceptance_criteria'])  # Boolean validation checks
```

**Execute a task:**
1. Verify dependencies exist
2. Read artifact specifications
3. Follow operational description
4. Create artifact at specified path
5. Validate against acceptance criteria
6. Run validation script

See `.catty/TASK_EXECUTION_GUIDE.md` for detailed workflow.

## Key Documents

| Document | Purpose | Audience |
|----------|---------|----------|
| `.catty/README.md` | Comprehensive documentation | All |
| `.catty/QUICKSTART.md` | Quick start guide | All |
| `.catty/TASK_EXECUTION_GUIDE.md` | Detailed execution workflow | Coding agents |
| `.catty/DEPENDENCY_GRAPH.md` | Visual dependency graphs | All |
| `.catty/operations.yaml` | Main operational model | Coding agents |
| `.catty/phases.yaml` | Dependency graph | Coding agents |

## Phases

The project is organized into 4 phases:

### Phase 0: Foundation (30 minutes)
- Initialize repository structure
- Conduct semantic web audit of external resources

### Phase 1: Core Ontology (220 minutes)
- Build RDF/OWL categorical schema
- Define logics as categorical objects
- Create morphism catalog
- Build two-dimensional lattice
- Model Curry-Howard correspondence
- Create complete example
- Write SHACL shapes and SPARQL queries
- Document ontology

### Phase 2: Thesis (145 minutes)
- Initialize LaTeX structure
- Write introduction chapter
- Write categorical semantic audit chapter
- Write conclusions chapter
- Compile thesis to PDF

### Phase 3: Validation Framework (80 minutes)
- Build validation scripts and SHACL shapes
- Validate all ontology artifacts
- Validate all thesis artifacts

**Total duration (sequential):** 470 minutes (7.8 hours)  
**Total duration (optimized):** 260 minutes (4.3 hours)

## Artifact Types

The project produces artifacts in multiple formats:

| Format | Examples | Validation |
|--------|----------|------------|
| JSON-LD | `catty-categorical-schema.jsonld` | JSON syntax + RDF parsing + SHACL |
| RDF/Turtle | `catty-shapes.ttl` | Turtle syntax + SHACL constructs |
| LaTeX | `thesis/chapters/*.tex` | Syntax + structure + compilation |
| Markdown | `ontology/README.md` | Syntax + content checks |
| Python | `.catty/validation/validate.py` | Syntax + imports |

## Validation

All artifacts have **testable validation criteria**:

```bash
# Validate single artifact
python .catty/validation/validate.py --artifact logics-as-objects

# Validate all artifacts from a task
python .catty/validation/validate.py --task task:build-logics-as-objects
```

Expected output:
```
======================================================================
Validating artifact: logics-as-objects
Path: ontology/logics-as-objects.jsonld
Format: JSON-LD
======================================================================

✓ Valid JSON syntax
✓ Contains @context
✓ Parses as RDF graph (42 triples)
✓ SHACL validation passed

✓ Validation PASSED for logics-as-objects
```

## Acceptance Criteria

All acceptance criteria are **boolean tests**, not subjective assessments:

✅ **Good (testable):**
- "file ontology/logics-as-objects.jsonld exists"
- "valid JSON-LD syntax (parse without error)"
- "at least 7 logic instances defined"
- "validates against SHACL shape"

❌ **Bad (subjective):**
- "produce high-quality ontology"
- "write comprehensive documentation"

## Task Descriptions

Task descriptions are **operational** (step-by-step), not **aspirational** (goals):

✅ **Good (operational):**
```yaml
description: |
  Create logic instances in JSON-LD format.
  Define 7 logic instances: LM, LK, LJ, LDJ, LL, ALL, RLL.
  Each logic must have:
  - rdf:type: catty:Logic
  - rdfs:label: human-readable name
  [... explicit property list ...]
```

❌ **Bad (aspirational):**
```yaml
description: |
  Create a comprehensive model of logics.
  Ensure high quality.
```

## Dependencies

The operational model enables automatic dependency resolution:

```
task:write-audit-chapter
  depends_on: [logics-as-objects, morphism-catalog, ...]
    ↓
task:build-morphism-catalog
  depends_on: [catty-categorical-schema, logics-as-objects]
    ↓
task:build-logics-as-objects
  depends_on: [catty-categorical-schema]
    ↓
task:build-categorical-schema
  depends_on: []  # Foundation task
```

See `.catty/DEPENDENCY_GRAPH.md` for visual representations.

## Critical Path

The **critical path** (longest dependency chain) determines minimum project duration:

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

**Total:** 400 minutes (6.7 hours)

## Extending the Model

To add a new artifact or task:

1. **Add artifact** to `operations.yaml` artifacts section
2. **Define** path, format, content_spec, validation
3. **Create SHACL shape** if RDF artifact
4. **Add task** to `operations.yaml` tasks section
5. **Define** produces, depends_on, description, acceptance_criteria
6. **Update** `phases.yaml` with dependencies
7. **Test** validation script

See `.catty/README.md` section "Extending the Operational Model" for details.

## Philosophy

The operational model is based on three principles:

1. **Eliminate interpretation**: Every task has unambiguous specifications
2. **Enable validation**: Every artifact has testable acceptance criteria
3. **Ensure reproducibility**: Task descriptions are operational, not aspirational

This enables deterministic, high-quality artifact production by coding agents.

## Requirements

```bash
pip install pyyaml rdflib pyshacl
```

## Status

The operational model is **complete** for Phases 0-3:

- ✅ 30+ artifacts defined with complete specifications
- ✅ 20+ tasks defined with operational descriptions
- ✅ Dependency graph with 4 phases
- ✅ Validation framework with SHACL shapes
- ✅ Automated validation script
- ✅ Comprehensive documentation

## Usage Examples

### Example 1: Understanding Project Structure

```bash
# List all ontology artifacts
python .catty/validation/validate.py --list-artifacts | grep ontology

# View dependency graph
less .catty/DEPENDENCY_GRAPH.md

# Read operational model
less .catty/operations.yaml
```

### Example 2: Validating Existing Artifacts

```bash
# Validate categorical schema
python .catty/validation/validate.py --artifact catty-categorical-schema

# Validate all ontology artifacts
for artifact in catty-categorical-schema logics-as-objects morphism-catalog; do
    python .catty/validation/validate.py --artifact $artifact
done
```

### Example 3: Executing a Task (Agent)

```python
import yaml
from pathlib import Path

# Load operations model
with open('.catty/operations.yaml', 'r') as f:
    operations = yaml.safe_load(f)

# Select task
task_id = 'task:build-logics-as-objects'
task = operations['tasks'][task_id]

# Verify dependencies
for artifact_id in task['depends_on']:
    artifact = operations['artifacts'][artifact_id]
    path = Path(artifact['path'])
    assert path.exists(), f"Missing dependency: {path}"

# Read artifact spec
artifact = operations['artifacts']['logics-as-objects']
output_path = Path(artifact['path'])
content_spec = artifact['content_spec']

# Execute task (follow description)
# ... create artifact ...

# Validate
import subprocess
result = subprocess.run([
    'python', '.catty/validation/validate.py',
    '--artifact', 'logics-as-objects'
], capture_output=True, text=True)

assert result.returncode == 0, "Validation failed"
```

## Benefits

### For Humans
- **Clear project structure**: All artifacts and dependencies documented
- **Quality assurance**: Automated validation of all outputs
- **Progress tracking**: Phase-based execution with clear milestones
- **Reproducibility**: Unambiguous specifications enable consistent results

### For Coding Agents
- **Unambiguous execution**: Operational descriptions, not aspirational goals
- **Automated validation**: Boolean acceptance criteria, not subjective assessment
- **Dependency resolution**: Automatic determination of execution order
- **Error handling**: Clear validation failure messages with fix guidance

## Next Steps

1. **Read comprehensive docs**: `.catty/README.md`
2. **Try quick start**: `.catty/QUICKSTART.md`
3. **Understand execution**: `.catty/TASK_EXECUTION_GUIDE.md`
4. **View dependencies**: `.catty/DEPENDENCY_GRAPH.md`
5. **Execute tasks**: Follow phase sequencing in `.catty/phases.yaml`

## License

This operational model is part of the Catty project, licensed under AGPL-3.0.

## Contact

For questions or contributions:
- See `CONTRIBUTING.md` in repository root
- Review `.catty/README.md` for detailed documentation
- Check `.catty/operations.yaml` for complete specifications
