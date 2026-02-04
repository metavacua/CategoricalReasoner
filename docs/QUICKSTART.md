# Catty Operations Model - Quick Start Guide

## What is This?

The `.catty/` directory contains a **formal operational model** that defines:
- Every artifact the project produces (where it is, what's in it, how to validate it)
- Every task that creates artifacts (what it does, what it depends on, how to verify success)
- The complete dependency graph (what order to do things)

This eliminates ambiguity and enables automated execution by coding agents.

## Key Files

```
.catty/
├── operations.yaml          ← Main model: all artifacts and tasks
├── phases.yaml              ← Dependency graph and execution order
├── validation/
│   ├── validate.py          ← Automated validation script
│   └── shapes/              ← SHACL constraints for RDF validation
├── README.md                ← Detailed documentation
├── TASK_EXECUTION_GUIDE.md  ← How to execute tasks
└── QUICKSTART.md            ← This file
```

## For Humans: Understanding the Project

### List all artifacts

```bash
cd /path/to/Catty
python .catty/validation/validate.py --list-artifacts
```

Output:
```
Available artifacts:
  - catty-categorical-schema: src/ontology/catty-categorical-schema.jsonld
  - logics-as-objects: src/ontology/logics-as-objects.jsonld
  - morphism-catalog: src/ontology/morphism-catalog.jsonld
  - two-d-lattice-category: src/ontology/two-d-lattice-category.jsonld
  - curry-howard-categorical-model: src/ontology/curry-howard-categorical-model.jsonld
  ...
```

### List all tasks

```bash
python .catty/validation/validate.py --list-tasks
```

Output:
```
Available tasks:
  - task:build-categorical-schema: produces ['catty-categorical-schema']
  - task:build-logics-as-objects: produces ['logics-as-objects']
  - task:build-morphism-catalog: produces ['morphism-catalog']
  ...
```

### View dependency graph

```bash
cat .catty/phases.yaml
```

This shows:
- **Phase 0**: Foundation tasks (no dependencies)
- **Phase 1**: Core ontology tasks
- **Phase 2**: Thesis tasks
- **Phase 3**: Validation tasks

### Validate an artifact

```bash
python .catty/validation/validate.py --artifact catty-categorical-schema
```

Output:
```
======================================================================
Validating artifact: catty-categorical-schema
Path: src/ontology/catty-categorical-schema.jsonld
Format: JSON-LD
======================================================================

✓ Valid JSON syntax
✓ Contains @context
✓ Contains catty prefix
✓ Parses as RDF graph (250 triples)
✓ SHACL validation passed

✓ Validation PASSED for catty-categorical-schema
```

## For Coding Agents: Executing Tasks

### Step 1: Install Dependencies

```bash
pip install pyyaml rdflib pyshacl
```

### Step 2: Read a Task

```python
import yaml

with open('.catty/operations.yaml', 'r') as f:
    operations = yaml.safe_load(f)

task = operations['tasks']['task:build-logics-as-objects']
print(task['description'])  # Step-by-step instructions
```

### Step 3: Check Dependencies

```python
depends_on = task['depends_on']  # e.g., ["catty-categorical-schema"]

for artifact_id in depends_on:
    artifact = operations['artifacts'][artifact_id]
    artifact_path = artifact['path']
    
    # Verify file exists
    assert os.path.exists(artifact_path), f"Missing dependency: {artifact_path}"
```

### Step 4: Create Artifact

Follow the operational instructions in `task['description']` to create the artifact at the specified path.

### Step 5: Validate

```bash
python .catty/validation/validate.py --artifact logics-as-objects
```

If validation fails, fix the artifact and re-run.

### Step 6: Mark Complete

Once validation passes, the task is complete. Proceed to the next task.

## Example: Execute Phase 0

Phase 0 has two foundation tasks that can run in parallel:

### Task 1: Initialize Repository Structure

**Already done** (directory structure exists)

### Task 2: Conduct Semantic Web Audit

**Already done** (file: `src/ontology/ontological-inventory.md`)

Validate:

```bash
python .catty/validation/validate.py --artifact ontological-inventory
```

## Example: Execute Phase 1 (Core Ontology)

### Subphase 1a: Build Categorical Schema

**Task:** `task:build-categorical-schema`

**Dependencies:** None

**Action:**

1. Read task description from `operations.yaml`
2. Create `src/ontology/catty-categorical-schema.jsonld` with required classes
3. Validate:

```bash
python .catty/validation/validate.py --artifact catty-categorical-schema
```

### Subphase 1b: Build Logics as Objects

**Task:** `task:build-logics-as-objects`

**Dependencies:** `catty-categorical-schema`

**Action:**

1. Verify `src/ontology/catty-categorical-schema.jsonld` exists
2. Read task description
3. Create `src/ontology/logics-as-objects.jsonld` with 7+ logic instances
4. Validate:

```bash
python .catty/validation/validate.py --artifact logics-as-objects
```

### Subphase 1b: Build Morphism Catalog

**Task:** `task:build-morphism-catalog`

**Dependencies:** `catty-categorical-schema`, `logics-as-objects`

**Action:**

1. Verify dependencies exist
2. Create `src/ontology/morphism-catalog.jsonld` with 10+ morphisms
3. Validate:

```bash
python .catty/validation/validate.py --artifact morphism-catalog
```

*Continue for all Phase 1 tasks...*

## Example: Validate All Ontology Artifacts

After completing Phase 1:

```bash
python .catty/validation/validate.py --task task:validate-ontology
```

This validates all ontology artifacts at once.

## Acceptance Criteria

Every task has **testable boolean acceptance criteria**. Examples:

✅ **Testable:**
- "file src/ontology/logics-as-objects.jsonld exists"
- "valid JSON-LD syntax (parse without error)"
- "at least 7 logic instances defined"
- "each logic has rdf:type catty:Logic"
- "validates against SHACL shape"

❌ **Not testable:**
- "produce high-quality ontology"
- "write comprehensive documentation"
- "create meaningful examples"

All criteria in `operations.yaml` are testable boolean conditions.

## Task Descriptions

Task descriptions are **operational** (step-by-step instructions), not **aspirational** (goals).

✅ **Operational:**
```yaml
description: |
  Create logic instances in JSON-LD format.
  Define 7 logic instances: LM, LK, LJ, LDJ, LL, ALL, RLL.
  Each logic must have:
  - rdf:type: catty:Logic
  - rdfs:label: human-readable name
  - catty:hasWeakeningLHS: boolean
  [... explicit property list ...]
  Output: src/ontology/logics-as-objects.jsonld
```

❌ **Aspirational:**
```yaml
description: |
  Create a comprehensive model of logics.
  Ensure high quality and completeness.
  Follow best practices.
```

## Directory Structure After Completion

```
Catty/
├── .catty/                  ← Operational model (this directory)
├── src/ontology/                ← RDF/OWL ontology files
│   ├── catty-categorical-schema.jsonld
│   ├── logics-as-objects.jsonld
│   ├── morphism-catalog.jsonld
│   ├── two-d-lattice-category.jsonld
│   ├── curry-howard-categorical-model.jsonld
│   ├── catty-complete-example.jsonld
│   ├── catty-shapes.ttl
│   ├── ontological-inventory.md
│   ├── README.md
│   └── queries/
│       └── sparql-examples.md
├── thesis/                  ← LaTeX thesis
│   ├── main.tex
│   ├── preamble.tex
│   ├── chapters/
│   │   ├── introduction.tex
│   │   ├── categorical-semantic-audit.tex
│   │   └── conclusions.tex
│   └── Makefile
├── src/scripts/                 ← Utility scripts
├── README.md                ← Project overview
└── LICENSE                  ← AGPL-3.0
```

## Phases and Duration

| Phase | Name | Tasks | Duration | Parallelizable |
|-------|------|-------|----------|----------------|
| 0 | Foundation | 2 | 30 min | Yes |
| 1 | Core Ontology | 9 | 220 min | Partial |
| 2 | Thesis | 5 | 130 min | Partial |
| 3 | Validation | 3 | 90 min | Partial |

**Sequential duration:** 400 minutes (6.7 hours)  
**Optimized duration:** 260 minutes (4.3 hours) with parallelization

## Critical Path

The longest dependency chain (critical path):

```
semantic-web-audit (30min)
  ↓
categorical-schema (20min)
  ↓
logics-as-objects (25min)
  ↓
morphism-catalog (20min)
  ↓
two-d-lattice (30min)
  ↓
curry-howard-model (25min)
  ↓
complete-example (30min)
  ↓
ontology-documentation (40min)
  ↓
audit-chapter (60min)
  ↓
conclusions (30min)
  ↓
thesis-pdf (10min)
  ↓
validation-framework (60min)
  ↓
validate-ontology (20min)

Total: 400 minutes
```

## Common Commands

```bash
# List all artifacts
python .catty/validation/validate.py --list-artifacts

# List all tasks
python .catty/validation/validate.py --list-tasks

# Validate single artifact
python .catty/validation/validate.py --artifact <artifact-id>

# Validate all artifacts from a task
python .catty/validation/validate.py --task <task-id>

# View dependency graph
cat .catty/phases.yaml

# View detailed documentation
cat .catty/README.md

# View task execution guide
cat .catty/TASK_EXECUTION_GUIDE.md
```

## Troubleshooting

### "Operations file not found"

Make sure you're in the project root or a subdirectory:

```bash
cd /path/to/Catty
python .catty/validation/validate.py --artifact catty-categorical-schema
```

### "rdflib not available"

Install Python dependencies:

```bash
pip install pyyaml rdflib pyshacl
```

### "SHACL validation failed"

Read the error message to identify which constraint failed, fix the RDF data, and re-run validation.

### "Missing dependency"

Execute the task that produces the missing artifact first. Check `phases.yaml` for correct execution order.

## Next Steps

1. **Read the full documentation**: `.catty/README.md`
2. **Understand task execution**: `.catty/TASK_EXECUTION_GUIDE.md`
3. **Review the operational model**: `.catty/operations.yaml`
4. **Check the dependency graph**: `.catty/phases.yaml`
5. **Start executing tasks** according to phase sequencing

## Philosophy

The operational model is based on three principles:

1. **Eliminate interpretation**: Every task has unambiguous specifications
2. **Enable validation**: Every artifact has testable acceptance criteria
3. **Ensure reproducibility**: Task descriptions are operational, not aspirational

This enables deterministic, high-quality artifact production by coding agents.

## License

This operational model is part of the Catty project, licensed under AGPL-3.0.

## Support

For questions or issues:
- Read: `.catty/README.md` (comprehensive documentation)
- Read: `.catty/TASK_EXECUTION_GUIDE.md` (detailed execution workflow)
- Check: `operations.yaml` (task and artifact specifications)
- Check: `phases.yaml` (dependency graph and execution order)
