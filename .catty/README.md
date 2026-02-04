# Catty Operations Model

## Overview

This directory (`.catty/`) contains the **formal, machine-readable operational skeleton** for Catty development. It defines the complete task/artifact system that serves as the execution contract between human specifications and coding agent implementation.

The operational model eliminates interpretation ambiguity by specifying:
1. **All artifacts**: their paths, formats, schemas, dependencies, and validation criteria
2. **All tasks**: what they produce, their dependencies, and unambiguous acceptance criteria
3. **Complete dependency graph**: task sequencing and parallelization opportunities
4. **Validation rules**: testable boolean conditions, not subjective prose

## Purpose

This is **not** a build configuration or CI/CD file. It is the **operational execution contract** that enables:

- **Unambiguous task execution**: Coding agents can read a task and know exactly what to produce
- **Automated validation**: Every artifact has testable acceptance criteria
- **Dependency resolution**: Clear artifact dependencies enable correct task sequencing
- **Quality assurance**: JSON schemas and validation criteria enforce structural constraints
- **Reproducibility**: Task specifications are operational (what to do), not aspirational (what to achieve)

The operations.yaml describes task orchestration for thesis generation and external RDF consumption, not local ontology authoring. RDF artifacts are metadata/provenance extracted from TeX content, not authored schemas.

## Directory Structure

```
.catty/
├── operations.yaml          # Complete artifact and task registry
├── phases.yaml              # Dependency graph and phase sequencing
├── validation/
│   ├── validate.py          # Unified validation script
│   └── thesis-structure.json  # JSON Schema for thesis structure
└── README.md                # This file
```

## Core Files

### operations.yaml

The **main operational model** containing:

- **Part 1: Artifact Registry**: Every artifact with complete specifications
  - `artifact_id`: Unique identifier
  - `path`: Location in repository
  - `format`: File format (LaTeX, Markdown, Python, JSON)
  - `schema`: JSON schema for validation
  - `content_spec`: Structured list of required content (not prose)
  - `produces_from`: Which task(s) create this artifact
  - `consumed_by`: Which task(s) use this artifact as input
  - `validation`: Type and criteria for validation

- **Part 2: Task Registry**: Every task with complete specifications
  - `task_id`: Unique identifier
  - `phase`: Execution phase (0, 1, 2, 3)
  - `produces`: List of artifact IDs created by this task
  - `depends_on`: List of artifact IDs required as input
  - `description`: Operational instructions (what to do, step-by-step)
  - `acceptance_criteria`: Testable boolean conditions (not subjective)
  - `validation_script`: Command to validate task output

- **Part 3: Task Execution Metadata**: How to execute tasks
  - Task template with required fields
  - Validation instructions per artifact type
  - Dependency resolution rules
  - Error handling procedures

### phases.yaml

**Dependency graph and phase sequencing** containing:

- **Phase Definitions**: Tasks grouped by dependency level
  - Phase 0: Foundation (no external dependencies)
  - Phase 1: Core Ontology (depends on Phase 0)
  - Phase 2: Thesis (depends on Phase 1)
  - Phase 3: Validation Framework (depends on Phase 1 and Phase 2)

- **Execution Order**: Recommended task execution sequence
  - Sequential tasks (must run in order)
  - Parallel tasks (can run simultaneously)
  - Blocking vs. non-blocking tasks

- **Dependency Matrix**: Complete task-to-artifact dependencies

- **Critical Path**: Longest path through dependency graph (minimum project duration)

- **Parallelization Opportunities**: Tasks that can run in parallel to reduce duration

### validation/

Directory containing validation tools:

- **validate.py**: Python script for automated validation
- **thesis-structure.json**: JSON Schema for LaTeX thesis structure validation

Note: Long-term validation infrastructure should use Java libraries (Jena SHACL support, JUnit).

## Usage

### For Coding Agents

**To execute a task:**

1. Read `operations.yaml` and locate the task by `task_id`
2. Check `depends_on` and verify all required artifacts exist
3. Read artifact specifications for `produces` artifacts
4. Execute task according to `description` (operational steps)
5. Create artifact files at specified `path`
6. Validate output against `acceptance_criteria`
7. Run `validation_script` if provided
8. If validation fails, iterate until all criteria pass

**Example:**

```bash
# Task: write-audit-chapter
# 1. Check dependencies: none (Phase 0 task)
# 2. Read artifact spec: categorical-semantic-audit
#    - path: thesis/chapters/categorical-semantic-audit.tex
#    - format: LaTeX
#    - content_spec: [list of required sections and content]
# 3. Execute task: create LaTeX chapter with required content
# 4. Validate:
python .catty/validation/validate.py --artifact categorical-semantic-audit
```

### For Humans

**To understand the project structure:**

```bash
# List all artifacts
python .catty/validation/validate.py --list-artifacts

# List all tasks
python .catty/validation/validate.py --list-tasks

# View dependency graph
cat .catty/phases.yaml
```

**To validate an artifact:**

```bash
python .catty/validation/validate.py --artifact <artifact_id>
```

**To validate all artifacts from a task:**

```bash
python .catty/validation/validate.py --task <task_id>
```

**To add a new artifact:**

1. Add entry to `artifacts:` section in `operations.yaml`
2. Define: `artifact_id`, `path`, `format`, `content_spec`, `validation`
3. Create JSON schema if needed (in `validation/`)
4. Add to `produces:` list of relevant task

**To add a new task:**

1. Add entry to `tasks:` section in `operations.yaml`
2. Define: `task_id`, `phase`, `produces`, `depends_on`, `description`, `acceptance_criteria`
3. Add to dependency graph in `phases.yaml`
4. Ensure `description` is operational (step-by-step), not aspirational

## Validation Framework

### Artifact Types and Validation

| Artifact Type | Validation Method | Tools Used |
|--------------|-------------------|------------|
| LaTeX | Syntax + structure + compilation | `pdflatex`, regex |
| Markdown | Syntax + content checks | regex, line counting |
| Python | Syntax + imports + entry point | `compile()`, AST |
| JSON/YAML | Syntax + schema validation | `jsonschema`, `pyyaml` |

Note: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

### Running Validation

**Validate a single artifact:**

```bash
python .catty/validation/validate.py --artifact logics-as-objects
```

**Validate all artifacts from a task:**

```bash
python .catty/validation/validate.py --task task:write-audit-chapter
```

**Expected output:**

```
======================================================================
Validating artifact: categorical-semantic-audit
Path: /path/to/thesis/chapters/categorical-semantic-audit.tex
Format: LaTeX
======================================================================

✓ Valid LaTeX syntax
✓ Contains required sections
✓ All citations reference docs/dissertation/bibliography/citations.yaml
✓ All IDs follow pattern (sec-*)

Checking content specifications:
  ✓ Section: Category Theory Foundation...
  ✓ Section: Logics as Categorical Objects...
  ✓ Section: Morphism Catalog...
  ✓ Section: Two-Dimensional Lattice...
  ✓ Section: Curry-Howard Model...

✓ Validation PASSED for categorical-semantic-audit
```

## Acceptance Criteria

All acceptance criteria in `operations.yaml` are **testable boolean conditions**, not subjective assessments.

**Good (testable):**
- "file thesis/chapters/categorical-semantic-audit.tex exists"
- "at least 5 sections defined"
- "all citations reference docs/dissertation/bibliography/citations.yaml"
- "validates against thesis-structure.schema.yaml"

**Bad (subjective):**
- "produce a high-quality white paper"
- "write comprehensive documentation"
- "create meaningful examples"

## Task Descriptions

Task descriptions in `operations.yaml` are **operational** (step-by-step instructions), not **aspirational** (goals or outcomes).

**Good (operational):**
```yaml
description: |
  Write audit chapter in LaTeX format.
  Use thesis-structure.schema.yaml for structure.
  Include sections: Category Theory Foundation, Logics as Categorical Objects,
  Morphism Catalog, Two-Dimensional Lattice, Curry-Howard Model.
  Each section must have: identifier, title, content.
  Use only citations from docs/dissertation/bibliography/citations.yaml.
  Output: thesis/chapters/categorical-semantic-audit.tex.
```

**Bad (aspirational):**
```yaml
description: |
  Create a comprehensive audit chapter.
  Ensure high quality and completeness.
  Follow best practices for academic writing.
```

## Dependency Resolution

The operational model enables automatic dependency resolution:

1. **Check dependencies**: Before executing a task, verify all `depends_on` artifacts exist
2. **Recursive resolution**: If an artifact doesn't exist, find the task that produces it and execute that first
3. **Topological sort**: Build dependency graph and execute tasks in correct order
4. **Parallel execution**: Tasks with no interdependencies can run simultaneously

**Example dependency chain:**

```
task:write-conclusions-chapter
  depends_on: [categorical-semantic-audit, main-thesis-chapters]
    ↓
task:write-audit-chapter
  depends_on: [citation-registry]
    ↓
task:init-citation-registry
  depends_on: []  # Phase 0 foundation task
```

## Phases

### Phase 0: Foundation
- **Tasks**: `init-repository-structure`, `conduct-semantic-web-audit`
- **Outputs**: Repository structure, ontological inventory
- **Duration**: ~30 minutes
- **Parallelizable**: Yes

### Phase 1: Thesis Content
- **Subphases**:
  - 1a: Citation Registry (`init-citation-registry`)
  - 1b: Core Chapters (`write-introduction`, `write-audit-chapter`)
  - 1c: Advanced Chapters (`write-categorical-chapters`, `write-conclusions`)
  - 1d: Compilation (`build-thesis-pdf`)
- **Outputs**: All thesis chapters, compiled PDF, citation registry
- **Duration**: ~220 minutes
- **Parallelizable**: Partial (chapters can be written in parallel)

### Phase 2: Validation and Testing
- **Subphases**:
  - 2a: Validation Framework (`build-validation-framework`)
  - 2b: Thesis Validation (`validate-thesis`)
  - 2c: Test Suite (`run-tests`)
- **Outputs**: Validation scripts, test reports
- **Duration**: ~90 minutes
- **Parallelizable**: Partial (validation tasks can run in parallel)

## Critical Path

The **critical path** (longest dependency chain) determines minimum project duration:

**Total duration**: ~400 minutes (6.7 hours) sequential

**Optimized duration**: ~260 minutes (4.3 hours) with parallelization

**Parallelization savings**: ~140 minutes (2.3 hours)

See `phases.yaml` for detailed critical path analysis and parallelization opportunities.

## Extending the Operational Model

### Adding a New Artifact

1. Choose unique `artifact_id` (e.g., `modal-logic-extension`)
2. Define `path` (e.g., `src/ontology/modal-logic-extension.jsonld`)
3. Specify `format` (e.g., `JSON-LD`)
4. List `content_spec` (structured requirements, not prose)
5. Reference `schema` (SHACL shape or JSON schema)
6. Define `validation` criteria (testable conditions)
7. Update existing task `produces` or create new task

### Adding a New Task

1. Choose unique `task_id` (e.g., `task:build-modal-logic-extension`)
2. Assign `phase` based on dependencies
3. List `produces` artifacts
4. List `depends_on` artifacts
5. Write operational `description` (step-by-step)
6. Define `acceptance_criteria` (boolean tests)
7. Specify `validation_script` command
8. Add to dependency graph in `phases.yaml`

### Updating Existing Tasks

1. Preserve `task_id` (tasks are referenced by ID)
2. Update `description` to be more operational if needed
3. Add/remove `depends_on` based on new dependencies
4. Refine `acceptance_criteria` to be more testable
5. Update dependency graph in `phases.yaml`
6. Increment version in `changelog`

## Version Control

The operational model is versioned in both files:

```yaml
metadata:
  version: "1.0.0"
  created: "2025-01-02"
  last_updated: "2025-01-02"

changelog:
  - version: "1.0.0"
    date: "2025-01-02"
    changes: "Initial operational model..."
```

**When to increment version:**
- **Patch (1.0.X)**: Fix typos, clarify descriptions, update validation criteria
- **Minor (1.X.0)**: Add new artifacts/tasks, add new phases, refine dependencies
- **Major (X.0.0)**: Breaking changes to artifact structure, task sequencing, or validation framework

## Best Practices

### For Task Descriptions

✅ **DO:**
- Write step-by-step operational instructions
- Specify exact file paths and formats
- List required properties and classes explicitly
- Provide concrete examples inline
- Use imperative commands ("Create", "Define", "Include")

❌ **DON'T:**
- Write aspirational goals ("achieve high quality")
- Use vague language ("comprehensive", "thorough")
- Leave room for interpretation
- Assume implicit knowledge
- Use subjective criteria

### For Acceptance Criteria

✅ **DO:**
- Write testable boolean conditions
- Use quantifiable metrics ("at least 7", "minimum 300 lines")
- Reference validation tools ("validates against SHACL shape")
- Check file existence and parsability
- Verify required content presence

❌ **DON'T:**
- Use subjective assessments ("good quality", "well-written")
- Write qualitative criteria ("comprehensive documentation")
- Leave validation to human judgment
- Use ambiguous language ("should", "might")

### For Content Specifications

✅ **DO:**
- List specific required classes and properties
- Enumerate minimum instance counts
- Specify exact naming patterns
- Reference external schemas and namespaces
- Provide structural requirements

❌ **DON'T:**
- Use general descriptions ("adequate coverage")
- Omit cardinality constraints
- Leave structure implicit
- Use placeholder text

## Troubleshooting

### Validation Fails

1. Read error messages from `validate.py`
2. Identify which acceptance criteria failed
3. Modify artifact to satisfy failed criteria
4. Re-run validation
5. Iterate until all criteria pass

### Missing Dependencies

1. Check `depends_on` for the task
2. Verify all required artifacts exist
3. If artifact missing, execute task that produces it
4. Resolve dependencies recursively

### SHACL Validation Errors

1. Load data graph and shapes graph separately
2. Verify both parse without errors
3. Check SHACL constraint violations in output
4. Fix RDF data to satisfy constraints
5. Re-run validation

### Python Dependencies

Required packages:
- `pyyaml`: YAML parsing
- `rdflib`: RDF parsing and manipulation
- `pyshacl`: SHACL validation

Install:
```bash
pip install pyyaml rdflib pyshacl
```

## References

- **SHACL**: [W3C SHACL Specification](https://www.w3.org/TR/shacl/)
- **JSON-LD**: [JSON-LD 1.1 Specification](https://www.w3.org/TR/json-ld11/)
- **RDF**: [RDF 1.1 Concepts](https://www.w3.org/TR/rdf11-concepts/)
- **JSON Schema**: [JSON Schema Specification](https://json-schema.org/)

## License

This operational model is part of the Catty project, licensed under AGPL-3.0.

## Contact

For questions or contributions to the operational model, see `CONTRIBUTING.md` in the repository root.
