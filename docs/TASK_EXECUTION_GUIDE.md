# Task Execution Guide for Coding Agents

## Overview

This guide explains how to read and execute tasks from `operations.yaml`. It is designed for coding agents that need to produce artifacts according to the Catty operational model.

## Task Structure

Every task in `operations.yaml` has this structure:

```yaml
task-id:
  task_id: "task:task-name"
  phase: N
  produces: [artifact-id-1, artifact-id-2, ...]
  depends_on: [artifact-id-a, artifact-id-b, ...]
  description: |
    Operational instructions...
  acceptance_criteria:
    - "criterion 1"
    - "criterion 2"
  validation_script: "command to validate"
```

## Execution Workflow

### Step 1: Read Task Specification

```python
# Example: Read task from operations.yaml
task_id = "task:build-logics-as-objects"
task = operations['tasks'][task_id]

# Extract key information
produces = task['produces']           # ["logics-as-objects"]
depends_on = task['depends_on']       # ["catty-categorical-schema"]
description = task['description']     # Operational instructions
criteria = task['acceptance_criteria']  # Boolean checks
```

### Step 2: Verify Dependencies

Check that all required artifacts exist:

```python
for artifact_id in task['depends_on']:
    artifact = operations['artifacts'][artifact_id]
    artifact_path = project_root / artifact['path']
    
    if not artifact_path.exists():
        # Execute task that produces this artifact first
        dependency_task = find_task_that_produces(artifact_id)
        execute_task(dependency_task)
```

### Step 3: Read Artifact Specifications

For each artifact the task produces, read its specification:

```python
for artifact_id in task['produces']:
    artifact = operations['artifacts'][artifact_id]
    
    path = artifact['path']              # Where to create file
    format = artifact['format']          # JSON-LD, RDF/Turtle, LaTeX, etc.
    content_spec = artifact['content_spec']  # What must be in the file
    schema = artifact.get('schema')      # SHACL shape or JSON schema
```

### Step 4: Execute Task Description

The `description` field contains operational, step-by-step instructions. Follow them exactly.

**Example task description (from `build-logics-as-objects`):**

```yaml
description: |
  Create logic instances as categorical objects in JSON-LD format.
  Reference @context from catty-categorical-schema.jsonld.
  Define minimum 7 logic instances: LM, LK, LJ, LDJ, LL, ALL, RLL.
  Each logic instance must have:
  - rdf:type: catty:Logic
  - rdfs:label: human-readable name (e.g., "Classical Logic")
  - dct:description: detailed description
  - catty:hasLogicalSignature: list of connectives (e.g., ["∧", "∨", "→", "¬"])
  - catty:hasLogicalAxiom: axioms (LEM, LNC, Explosion, DNE) as booleans or references
  - catty:hasWeakeningLHS: boolean (true/false)
  - catty:hasWeakeningRHS: boolean
  - catty:hasContractionLHS: boolean
  - catty:hasContractionRHS: boolean
  - catty:hasExchange: boolean
  - owl:sameAs: link to Wikidata item (if available)
  - dct:source: link to Wikipedia or reference (if available)
  Logic definitions:
  - LM: initial logic (no LEM, no LNC, has Exchange)
  - LK: classical logic (has LEM, LNC, all structural rules)
  - LJ: intuitionistic logic (no LEM, has LNC, all structural rules)
  - LDJ: dual intuitionistic logic (has LEM, no LNC, all structural rules)
  - LL: linear logic (no LEM, no LNC, has Exchange only)
  - ALL: affine linear logic (no LEM, no LNC, has Exchange and Weakening)
  - RLL: relevant linear logic (no LEM, no LNC, has Exchange and Contraction)
  Output: src/ontology/logics-as-objects.jsonld.
```

**How to execute:**

1. Create a new file at `src/ontology/logics-as-objects.jsonld`
2. Add JSON-LD `@context` referencing `catty-categorical-schema.jsonld`
3. Define 7 logic instances (LM, LK, LJ, LDJ, LL, ALL, RLL)
4. For each logic, add all required properties (see list above)
5. Use the specific definitions provided (e.g., LM has Exchange but no LEM/LNC)
6. Add external links (owl:sameAs to Wikidata) where available
7. Save the file

### Step 5: Validate Against Content Spec

Check that the artifact satisfies `content_spec`:

```python
content_spec = artifact['content_spec']

# Example content_spec items:
# - "JSON-LD @context referencing catty-categorical-schema"
# - "Logic instances: LM, LK, LJ, LDJ, LL, ALL, RLL (minimum 7)"
# - "Each logic has: rdf:type catty:Logic"
# - "Each logic has: rdfs:label, dct:description"

# Verify each requirement
for spec_item in content_spec:
    verify_content_requirement(artifact_path, spec_item)
```

### Step 6: Run Acceptance Criteria

Test each acceptance criterion (boolean checks):

```python
criteria = task['acceptance_criteria']

# Example criteria:
# - "file src/ontology/logics-as-objects.jsonld exists"
# - "valid JSON-LD syntax"
# - "at least 7 logic instances defined (LM, LK, LJ, LDJ, LL, ALL, RLL)"
# - "each logic has rdf:type catty:Logic"
# - "validates against .catty/validation/shapes/logics-as-objects.shacl"

all_pass = True
for criterion in criteria:
    result = check_criterion(artifact_path, criterion)
    if not result:
        print(f"FAIL: {criterion}")
        all_pass = False
    else:
        print(f"PASS: {criterion}")

if not all_pass:
    # Fix artifact and re-run validation
    fix_artifact(artifact_path)
    goto Step 6
```

### Step 7: Run Validation Script

Execute the validation script specified in the task:

```bash
python .catty/validation/validate.py --artifact logics-as-objects
```

Expected output if successful:

```
======================================================================
Validating artifact: logics-as-objects
Path: /path/to/src/ontology/logics-as-objects.jsonld
Format: JSON-LD
======================================================================

✓ Valid JSON syntax
✓ Contains @context
✓ Parses as RDF graph (42 triples)
✓ SHACL validation passed

✓ Validation PASSED for logics-as-objects
```

If validation fails, fix the artifact and re-run.

### Step 8: Mark Task Complete

Once all validation passes:

```python
mark_task_complete(task_id)
proceed_to_next_task()
```

## Example: Complete Task Execution

### Task: `build-categorical-schema`

**1. Read task:**

```python
task = operations['tasks']['task:build-categorical-schema']
# produces: ["catty-categorical-schema"]
# depends_on: []  # Phase 0 task, no dependencies
```

**2. Verify dependencies:**

No dependencies (Phase 0 task). Proceed.

**3. Read artifact specification:**

```python
artifact = operations['artifacts']['catty-categorical-schema']
# path: "src/ontology/catty-categorical-schema.jsonld"
# format: "JSON-LD"
# schema: ".catty/validation/shapes/categorical-schema.shacl"
# content_spec:
#   - "JSON-LD @context with catty, rdf, rdfs, owl, skos, dct prefixes"
#   - "Core categorical classes: Category, Object, Morphism, Composition"
#   - "Functor, NaturalTransformation, AdjointFunctors classes"
#   - [... more requirements ...]
```

**4. Execute description:**

Create `src/ontology/catty-categorical-schema.jsonld`:

```json
{
  "@context": {
    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology#",
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "owl": "http://www.w3.org/2002/07/owl#",
    "skos": "http://www.w3.org/2004/02/skos/core#",
    "dct": "http://purl.org/dc/terms/",
    "xsd": "http://www.w3.org/2001/XMLSchema#"
  },
  "@graph": [
    {
      "@id": "catty:Category",
      "@type": "rdfs:Class",
      "rdfs:label": "Category",
      "rdfs:comment": "A mathematical category consisting of objects and morphisms",
      "dct:description": "A category C consists of a collection of objects and morphisms..."
    },
    {
      "@id": "catty:Object",
      "@type": "rdfs:Class",
      "rdfs:label": "Object",
      "rdfs:comment": "An object in a category"
    },
    {
      "@id": "catty:Morphism",
      "@type": "rdfs:Class",
      "rdfs:label": "Morphism",
      "rdfs:comment": "A morphism (arrow) between objects in a category",
      "dct:description": "A morphism f: A → B..."
    },
    ...
  ]
}
```

**5. Validate content spec:**

- ✅ Contains @context with catty, rdf, rdfs, owl, skos, dct prefixes
- ✅ Defines Category class
- ✅ Defines Object class
- ✅ Defines Morphism class
- ✅ All classes have rdfs:label and rdfs:comment

**6. Run acceptance criteria:**

```
✓ file src/ontology/catty-categorical-schema.jsonld exists
✓ valid JSON-LD syntax (parse without error)
✓ contains @context with required prefixes
✓ defines all required classes (Category, Object, Morphism, Logic, Functor)
✓ all classes have rdfs:label and rdfs:comment
```

**7. Run validation script:**

```bash
python .catty/validation/validate.py --artifact catty-categorical-schema
```

Output:
```
✓ Validation PASSED for catty-categorical-schema
```

**8. Task complete. Proceed to next task.**

## Artifact Type Examples

### JSON-LD Artifact

**Template:**

```json
{
  "@context": {
    "catty": "https://metavacua.github.io/CategoricalReasoner/ontology#",
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "owl": "http://www.w3.org/2002/07/owl#",
    "dct": "http://purl.org/dc/terms/"
  },
  "@graph": [
    {
      "@id": "catty:ExampleInstance",
      "@type": "catty:ExampleClass",
      "rdfs:label": "Example Instance",
      "dct:description": "Description of instance",
      "catty:exampleProperty": "value"
    }
  ]
}
```

**Validation:**
1. Valid JSON syntax
2. Contains `@context`
3. Parses as RDF using `rdflib`
4. Validates against SHACL shape

### RDF/Turtle Artifact

**Template:**

```turtle
@prefix catty: <https://metavacua.github.io/CategoricalReasoner/ontology#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix sh: <http://www.w3.org/ns/shacl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

catty:ExampleShape
    a sh:NodeShape ;
    sh:targetClass catty:ExampleClass ;
    sh:property [
        sh:path rdfs:label ;
        sh:minCount 1 ;
        sh:datatype xsd:string ;
    ] .
```

**Validation:**
1. Valid Turtle syntax
2. Parses with `rdflib`
3. Contains SHACL constructs (if SHACL file)

### LaTeX Artifact

**Template:**

```latex
\chapter{Example Chapter}

\section{Introduction}

This is an example chapter demonstrating the structure requirements.

\section{Main Content}

Content with definitions, examples, and code listings.

\begin{lstlisting}[language=Python]
# Example code
print("Hello, world!")
\end{lstlisting}

\begin{table}[h]
\centering
\begin{tabular}{|l|l|}
\hline
Column 1 & Column 2 \\
\hline
Value 1 & Value 2 \\
\hline
\end{tabular}
\caption{Example table}
\end{table}

\section{Conclusion}

Summary of chapter.
```

**Validation:**
1. Valid LaTeX syntax
2. Contains `\chapter` command
3. Contains minimum number of `\section` commands
4. Contains code listings (if required)
5. Contains tables (if required)
6. Minimum line count satisfied

### Markdown Artifact

**Template:**

```markdown
# Example Document

## Section 1

Content for section 1.

## Section 2

Content for section 2 with code example:

```python
# Example code
print("Hello, world!")
```

## Section 3

Content with table:

| Column 1 | Column 2 |
|----------|----------|
| Value 1  | Value 2  |
```

**Validation:**
1. Valid Markdown syntax
2. Minimum number of headers (sections)
3. Minimum line count
4. Contains code blocks (if required)
5. Contains tables (if required)

## Common Validation Checks

### File Existence

```python
assert artifact_path.exists(), f"File does not exist: {artifact_path}"
```

### Syntax Validation

**JSON/JSON-LD:**
```python
with open(artifact_path, 'r') as f:
    data = json.load(f)  # Raises JSONDecodeError if invalid
```

**RDF/Turtle:**
```python
from rdflib import Graph
g = Graph()
g.parse(artifact_path, format='turtle')  # Raises exception if invalid
```

**LaTeX:**
```bash
pdflatex -interaction=nonstopmode main.tex
# Check exit code
```

**Python:**
```python
with open(artifact_path, 'r') as f:
    content = f.read()
compile(content, artifact_path, 'exec')  # Raises SyntaxError if invalid
```

### Content Validation

**Check for required classes (JSON-LD/RDF):**
```python
from rdflib import Graph, Namespace
g = Graph()
g.parse(artifact_path)

CATTY = Namespace("https://metavacua.github.io/CategoricalReasoner/ontology#")
logics = list(g.subjects(RDF.type, CATTY.Logic))
assert len(logics) >= 7, f"Expected at least 7 logics, found {len(logics)}"
```

**Check for required sections (LaTeX):**
```python
with open(artifact_path, 'r') as f:
    content = f.read()

sections = re.findall(r'\\section\{([^}]+)\}', content)
assert len(sections) >= 3, f"Expected at least 3 sections, found {len(sections)}"
```

**Check line count:**
```python
with open(artifact_path, 'r') as f:
    lines = [l for l in f if l.strip() and not l.strip().startswith('%')]
assert len(lines) >= 300, f"Expected at least 300 lines, found {len(lines)}"
```

### SHACL Validation

```python
from pyshacl import validate
from rdflib import Graph

data_graph = Graph()
data_graph.parse(artifact_path)

shapes_graph = Graph()
shapes_graph.parse(shape_path)

conforms, results_graph, results_text = validate(
    data_graph,
    shacl_graph=shapes_graph,
    inference='rdfs'
)

assert conforms, f"SHACL validation failed:\n{results_text}"
```

## Error Handling

### Validation Failures

When validation fails:

1. **Read error messages** carefully
2. **Identify which criterion failed**
3. **Modify artifact** to satisfy the failed criterion
4. **Re-run validation**
5. **Iterate** until all criteria pass

**Example:**

```
✗ SHACL validation failed:
  Constraint Violation in LogicShape:
    Severity: sh:Violation
    Source Shape: catty:LogicShape
    Focus Node: catty:LogicLM
    Result Path: rdfs:label
    Message: Logic must have at least one rdfs:label
```

**Fix:** Add `rdfs:label` to `catty:LogicLM` instance.

### Missing Dependencies

When a dependency is missing:

1. **Identify the missing artifact**
2. **Find the task that produces it** (check `produces_from` in artifact spec)
3. **Execute that task first**
4. **Recursively resolve dependencies** until foundation tasks
5. **Return to original task** once dependencies satisfied

### Syntax Errors

When syntax validation fails:

1. **Use appropriate parser** to identify error location
2. **Fix syntax** (missing quote, bracket, etc.)
3. **Re-validate**

## Best Practices

### DO:

✅ Read task description completely before starting
✅ Verify all dependencies exist
✅ Follow operational instructions exactly
✅ Create artifacts at specified paths
✅ Include all required content from content_spec
✅ Run validation after creating artifact
✅ Fix errors and re-validate until all criteria pass
✅ Use provided examples as templates

### DON'T:

❌ Skip dependency verification
❌ Interpret task description creatively
❌ Omit required properties or classes
❌ Create artifacts at different paths
❌ Assume validation will pass without checking
❌ Proceed to next task if validation fails
❌ Ignore acceptance criteria

## Summary

Task execution is a deterministic process:

1. **Read** task specification from `operations.yaml`
2. **Verify** all dependencies exist
3. **Read** artifact specifications
4. **Execute** operational instructions from description
5. **Validate** against content_spec and acceptance_criteria
6. **Run** validation script
7. **Fix** any errors and iterate until validation passes
8. **Mark** task complete and proceed to next task

This ensures reproducible, high-quality artifact production with unambiguous specifications.

## References

- `operations.yaml`: Complete task and artifact registry
- `phases.yaml`: Dependency graph and execution sequencing
- `validation/validate.py`: Automated validation script
- `README.md`: Overview of operational model
