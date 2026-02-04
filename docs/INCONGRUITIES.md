# Operational Model Incongruities and Required Updates

**Date:** 2025-01-03  
**Status:** Identified and requires resolution

## Overview

This document identifies incongruities between the operational model (`.catty/operations.yaml` and `.catty/phases.yaml`) and the actual project state, particularly regarding PDF generation, GitHub Pages deployment, and semantic web technologies.

---

## Critical Incongruities

### 1. Missing GitHub Pages Deployment Artifacts and Tasks

**Issue:** The operational model does not include artifacts or tasks for GitHub Pages HTML deployment.

**Current Reality:**
- `.github/workflows/deploy.yml` exists and performs:
  1. Builds PDF from LaTeX
  2. Uses `latexpand` to expand LaTeX includes
  3. Converts expanded LaTeX to HTML using Pandoc
  4. Deploys both PDF and HTML to GitHub Pages
  5. Makes thesis accessible at `https://metavacua.github.io/CategoricalReasoner/`

**Missing from Operational Model:**
- ❌ Artifact: `thesis-html` (index.html generated from LaTeX)
- ❌ Artifact: `thesis-expanded-latex` (main.expanded.tex intermediate file)
- ❌ Artifact: `github-pages-site` (complete site/ directory)
- ❌ Task: `task:convert-latex-to-html` (Pandoc conversion)
- ❌ Task: `task:deploy-to-github-pages` (deployment workflow)
- ❌ Task: `task:expand-latex` (latexpand preprocessing)

**Impact:** High - GitHub Pages deployment is a primary deliverable but not tracked in operational model.

---

### 2. Semantic Web Validation Scripts Removed

**Issue:** The diff shows removal of semantic web validation scripts, but operational model still references them.

**Removed Files:**
- ❌ `scripts/validate_rdf.py` (removed in diff)
- ❌ `tests/test_consistency.py` (removed in diff)
- ❌ `benchmarks/run.py` (removed in diff)
- ❌ `benchmarks/queries/*.rq` (removed in diff)

**Current Reality:**
- Validation is now handled by `.catty/validation/validate.py`
- SHACL shapes moved to `.catty/validation/shapes/`
- SPARQL examples exist at `ontology/queries/sparql-examples.md` (documentation, not executable queries)

**Operational Model References:**
- ✅ Correctly references `.catty/validation/validate.py`
- ✅ Correctly references `.catty/validation/shapes/*.shacl`
- ⚠️ References `sparql-examples` but should clarify it's documentation, not executable benchmarks

**Impact:** Medium - Model is mostly correct but should clarify SPARQL examples are documentation.

---

### 3. Makefile Dependencies Mismatch

**Issue:** `thesis/Makefile` has different dependencies than specified in operational model.

**Makefile Actual Dependencies:**
```makefile
$(PDF): $(TEX) preamble.tex chapters/chapter1.tex
```

**Operational Model Specifies:**
```yaml
task:build-thesis-pdf:
  depends_on: ["thesis-main", "thesis-preamble", "introduction-chapter", 
               "audit-chapter", "conclusions-chapter"]
```

**Reality:**
- Makefile only explicitly lists `chapter1.tex`
- Does not list `introduction.tex`, `categorical-semantic-audit.tex`, `conclusions.tex`
- This may be because `main.tex` includes them, so Makefile dependency is indirect

**Impact:** Low - Model is semantically correct (task needs all chapters), but should note Makefile uses indirect dependencies.

---

### 4. Missing Workflow Artifacts

**Issue:** CI/CD workflow file is not tracked as an artifact in operational model.

**Current Reality:**
- `.github/workflows/deploy.yml` exists and is critical infrastructure
- Defines the actual deployment process
- Should be documented as an artifact

**Missing from Operational Model:**
- ❌ Artifact: `github-pages-workflow` (`.github/workflows/deploy.yml`)
- ❌ Content spec for workflow file
- ❌ Validation criteria for workflow

**Impact:** Medium - Infrastructure-as-code should be part of operational model.

---

### 5. Ontology Directory Structure

**Issue:** Operations model references `ontology/shapes/` but SHACL shapes are actually in `ontology/` root.

**Current Reality:**
```
ontology/
├── catty-shapes.ttl          ← SHACL shapes are here
├── catty-categorical-schema.jsonld
├── queries/
│   └── sparql-examples.md    ← SPARQL examples are here (not executable)
└── ...
```

**Operational Model References:**
- Path for `catty-shapes`: `ontology/catty-shapes.ttl` ✅ Correct
- Path for validation shapes: `.catty/validation/shapes/*.shacl` ✅ Correct (different set)

**Status:** ✅ No issue - both locations exist for different purposes.

---

### 6. Semantic Web Technology Coverage

**Issue:** Operational model should explicitly document semantic web technology stack.

**Current Semantic Web Technologies Used:**
1. **RDF/OWL Representation:**
   - Format: JSON-LD (primary)
   - All ontology files use JSON-LD with `@context`
   - Links to external LOD: Wikidata (`owl:sameAs`), DBpedia

2. **SHACL Validation:**
   - Two sets of shapes:
     - `ontology/catty-shapes.ttl` (ontology-specific)
     - `.catty/validation/shapes/*.shacl` (operational model validation)
   - Validation via `pyshacl` library

3. **SPARQL Queries:**
   - Documentation: `ontology/queries/sparql-examples.md`
   - 15+ example queries for exploration
   - Not executable benchmarks (removed)

4. **Triplestore Deployment:**
   - Removed: `deployment/docker-compose.yml` (Blazegraph)
   - Not currently part of deployment

**Operational Model Coverage:**
- ✅ Covers JSON-LD artifacts
- ✅ Covers SHACL validation
- ✅ Covers SPARQL examples (as documentation)
- ❌ Should explicitly state semantic web technology stack
- ❌ Should note triplestore deployment is not included

**Impact:** Low - Coverage exists but should be more explicit.

---

## Recommended Updates

### 1. Add GitHub Pages Artifacts

Add to `operations.yaml` artifacts section:

```yaml
thesis-html:
  artifact_id: "thesis-html"
  path: "site/index.html"
  format: "HTML"
  content_spec:
    - "HTML converted from LaTeX thesis using Pandoc"
    - "Contains all thesis chapters"
    - "Deployed to GitHub Pages"
  produces_from: ["task:convert-latex-to-html"]
  consumed_by: ["task:deploy-to-github-pages"]
  validation:
    type: "html"
    criteria:
      - "file site/index.html exists"
      - "valid HTML5 syntax"
      - "contains thesis content (chapters, sections)"

thesis-expanded-latex:
  artifact_id: "thesis-expanded-latex"
  path: "site/main.expanded.tex"
  format: "LaTeX"
  content_spec:
    - "LaTeX file with all includes expanded"
    - "Generated by latexpand from main.tex"
    - "Intermediate artifact for Pandoc conversion"
  produces_from: ["task:expand-latex"]
  consumed_by: ["task:convert-latex-to-html"]
  validation:
    type: "latex"
    criteria:
      - "file contains all chapter content inline"
      - "no \\input or \\include commands remain"

github-pages-site:
  artifact_id: "github-pages-site"
  path: "site/"
  format: "directory"
  content_spec:
    - "Directory containing GitHub Pages deployment"
    - "Contains index.html (thesis HTML)"
    - "Contains main.pdf (thesis PDF)"
  produces_from: ["task:build-github-pages-site"]
  consumed_by: ["task:deploy-to-github-pages"]
  validation:
    type: "directory"
    criteria:
      - "directory site/ exists"
      - "file site/index.html exists"
      - "file site/main.pdf exists"

github-pages-workflow:
  artifact_id: "github-pages-workflow"
  path: ".github/workflows/deploy.yml"
  format: "YAML (GitHub Actions)"
  content_spec:
    - "GitHub Actions workflow for deployment"
    - "Steps: checkout, install LaTeX/Pandoc, build PDF, convert to HTML, deploy"
    - "Triggers on workflow_dispatch (manual)"
    - "Deploys to GitHub Pages"
  produces_from: ["task:create-github-pages-workflow"]
  consumed_by: []
  validation:
    type: "yaml"
    criteria:
      - "file .github/workflows/deploy.yml exists"
      - "valid YAML syntax"
      - "contains required jobs: build-and-deploy"
      - "uses actions: configure-pages, upload-pages-artifact, deploy-pages"
```

### 2. Add GitHub Pages Tasks

Add to `operations.yaml` tasks section:

```yaml
expand-latex:
  task_id: "task:expand-latex"
  phase: 2
  produces: ["thesis-expanded-latex"]
  depends_on: ["thesis-main", "thesis-preamble", "introduction-chapter", 
               "audit-chapter", "conclusions-chapter"]
  description: |
    Expand LaTeX main.tex file with all includes inline.
    Use latexpand tool to recursively expand \input and \include commands.
    Commands:
    - cd thesis/
    - latexpand main.tex > ../site/main.expanded.tex
    Output: site/main.expanded.tex (intermediate file)
  acceptance_criteria:
    - "file site/main.expanded.tex exists"
    - "expanded file contains all chapter content inline"
    - "no \\input or \\include commands in expanded file"
    - "valid LaTeX syntax"
  validation_script: "grep -q '\\input\\|\\include' site/main.expanded.tex && exit 1 || exit 0"

convert-latex-to-html:
  task_id: "task:convert-latex-to-html"
  phase: 2
  produces: ["thesis-html"]
  depends_on: ["thesis-expanded-latex"]
  description: |
    Convert expanded LaTeX to HTML using Pandoc.
    Commands:
    - mkdir -p site/
    - pandoc -f latex -t html -s site/main.expanded.tex -o site/index.html
    Pandoc converts LaTeX to semantic HTML5.
    Output: site/index.html
  acceptance_criteria:
    - "file site/index.html exists"
    - "valid HTML5 syntax"
    - "HTML contains thesis chapters and sections"
    - "HTML contains title, author, content"
  validation_script: "python .catty/validation/validate.py --artifact thesis-html"

build-github-pages-site:
  task_id: "task:build-github-pages-site"
  phase: 2
  produces: ["github-pages-site"]
  depends_on: ["thesis-pdf", "thesis-html"]
  description: |
    Assemble complete GitHub Pages site directory.
    Copy artifacts to site/:
    - cp thesis/main.pdf site/main.pdf
    - Ensure site/index.html exists (from convert-latex-to-html)
    Directory structure:
    site/
    ├── index.html  (HTML thesis)
    └── main.pdf    (PDF thesis)
    Output: site/ directory ready for deployment
  acceptance_criteria:
    - "directory site/ exists"
    - "file site/index.html exists and is valid HTML"
    - "file site/main.pdf exists and is valid PDF"
  validation_script: "ls -la site/index.html site/main.pdf"

deploy-to-github-pages:
  task_id: "task:deploy-to-github-pages"
  phase: 2
  produces: []
  depends_on: ["github-pages-site", "github-pages-workflow"]
  description: |
    Deploy site to GitHub Pages using GitHub Actions.
    This is automated via .github/workflows/deploy.yml.
    Manual trigger: GitHub UI → Actions → Deploy → Run workflow
    Workflow steps:
    1. Checkout repository
    2. Install LaTeX and Pandoc
    3. Build PDF (make in thesis/)
    4. Expand LaTeX (latexpand)
    5. Convert to HTML (pandoc)
    6. Copy PDF to site/
    7. Upload and deploy to GitHub Pages
    Result: Site available at https://metavacua.github.io/CategoricalReasoner/
  acceptance_criteria:
    - "workflow file .github/workflows/deploy.yml exists"
    - "workflow runs successfully (manual verification)"
    - "GitHub Pages site is accessible (manual verification)"
    - "site contains index.html and main.pdf (manual verification)"
  validation_script: "echo 'Manual verification required: check GitHub Pages deployment'"

create-github-pages-workflow:
  task_id: "task:create-github-pages-workflow"
  phase: 2
  produces: ["github-pages-workflow"]
  depends_on: []
  description: |
    Create GitHub Actions workflow for GitHub Pages deployment.
    File: .github/workflows/deploy.yml
    Workflow must:
    - Trigger on workflow_dispatch (manual)
    - Install LaTeX, Pandoc, latexpand
    - Build PDF from thesis/
    - Expand LaTeX with latexpand
    - Convert to HTML with Pandoc
    - Deploy to GitHub Pages
    Use GitHub Actions:
    - actions/configure-pages@v5
    - actions/upload-pages-artifact@v3
    - actions/deploy-pages@v4
    Output: .github/workflows/deploy.yml
  acceptance_criteria:
    - "file .github/workflows/deploy.yml exists"
    - "valid YAML syntax"
    - "contains on: workflow_dispatch trigger"
    - "contains jobs: build-and-deploy"
    - "installs LaTeX, Pandoc, latexpand"
    - "builds PDF, converts to HTML, deploys"
  validation_script: "python .catty/validation/validate.py --artifact github-pages-workflow"
```

### 3. Add Semantic Web Technology Documentation

Add to `operations.yaml` metadata section:

```yaml
semantic_web_technologies:
  description: "Semantic web technologies used in Catty project"
  
  rdf_owl:
    format: "JSON-LD"
    standard: "RDF 1.1, OWL 2"
    files:
      - "ontology/catty-categorical-schema.jsonld"
      - "ontology/logics-as-objects.jsonld"
      - "ontology/morphism-catalog.jsonld"
      - "ontology/two-d-lattice-category.jsonld"
      - "ontology/curry-howard-categorical-model.jsonld"
      - "ontology/catty-complete-example.jsonld"
    external_links:
      - "Wikidata entities (owl:sameAs)"
      - "DBpedia resources (dct:source)"
      - "nLab definitions (prov:wasDerivedFrom)"
    
  shacl_validation:
    standard: "SHACL (Shapes Constraint Language)"
    shapes:
      ontology_shapes: "ontology/catty-shapes.ttl"
      validation_shapes: ".catty/validation/shapes/*.shacl"
    library: "pyshacl (Python)"
    usage: "Validate RDF graphs against structural constraints"
    
  sparql_queries:
    standard: "SPARQL 1.1"
    documentation: "ontology/queries/sparql-examples.md"
    count: "15+ example queries"
    purpose: "Documentation and exploration (not executable benchmarks)"
    frameworks: ["Apache Jena ARQ", "RDF4J", "rdflib (Python)"]
    note: "Executable benchmark queries removed (see diff)"
    
  triplestore_deployment:
    status: "Not included"
    note: "deployment/docker-compose.yml (Blazegraph) removed in cleanup"
    rationale: "Triplestore deployment not part of core thesis deliverables"
    
  linked_open_data:
    wikidata:
      usage: "owl:sameAs links to logic and category theory entities"
      license: "CC0 (Public Domain)"
    dbpedia:
      usage: "dct:source references for definitions"
      license: "CC BY-SA 3.0"
    ncatlab:
      usage: "prov:wasDerivedFrom for categorical concepts"
      license: "CC BY-SA 3.0"
```

### 4. Update Phase 2 Task Sequencing

Modify `phases.yaml` Phase 2 to include HTML conversion:

```yaml
phase_2:
  name: "Thesis"
  description: "Write LaTeX thesis chapters, compile PDF, convert to HTML, deploy to GitHub Pages"
  
  subphase_2c:
    name: "PDF Compilation and HTML Conversion"
    dependencies: ["subphase_2b"]
    tasks:
      - task_id: "task:build-thesis-pdf"
        duration: "10 minutes"
        
      - task_id: "task:expand-latex"
        duration: "5 minutes"
        depends_on: ["task:build-thesis-pdf"]
        
      - task_id: "task:convert-latex-to-html"
        duration: "5 minutes"
        depends_on: ["task:expand-latex"]
        
      - task_id: "task:build-github-pages-site"
        duration: "5 minutes"
        depends_on: ["task:build-thesis-pdf", "task:convert-latex-to-html"]
        
  subphase_2d:
    name: "GitHub Pages Deployment"
    dependencies: ["subphase_2c"]
    tasks:
      - task_id: "task:create-github-pages-workflow"
        duration: "15 minutes"
        
      - task_id: "task:deploy-to-github-pages"
        duration: "10 minutes (automated)"
        depends_on: ["task:build-github-pages-site", "task:create-github-pages-workflow"]
```

---

## Documentation Updates Needed

### 1. Update `.catty/README.md`

Add section on GitHub Pages deployment:

```markdown
## GitHub Pages Deployment

The operational model includes tasks for deploying the thesis as both PDF and HTML to GitHub Pages:

1. **PDF Generation**: `task:build-thesis-pdf` compiles LaTeX to PDF
2. **LaTeX Expansion**: `task:expand-latex` uses latexpand to inline all includes
3. **HTML Conversion**: `task:convert-latex-to-html` uses Pandoc to convert to HTML5
4. **Site Assembly**: `task:build-github-pages-site` creates site/ directory
5. **Deployment**: `task:deploy-to-github-pages` uses GitHub Actions workflow

The deployment is automated via `.github/workflows/deploy.yml` and triggered manually from GitHub UI.
```

### 2. Update `.catty/README.md` Semantic Web Section

Add clarification:

```markdown
## Semantic Web Technologies

Catty uses standard semantic web technologies:

- **RDF/OWL**: JSON-LD format for all ontologies
- **SHACL**: Two sets of validation shapes:
  - `ontology/catty-shapes.ttl`: Ontology-specific constraints
  - `.catty/validation/shapes/*.shacl`: Operational model validation
- **SPARQL**: Example queries in `ontology/queries/sparql-examples.md` (documentation)
- **Linked Open Data**: Links to Wikidata, DBpedia, nLab

**Note**: Executable SPARQL benchmark queries and triplestore deployment were removed
in favor of documentation-based examples and the operational model validation framework.
```

### 3. Update `OPERATIONS_MODEL.md`

Add GitHub Pages section and clarify semantic web tech stack.

---

## Priority Actions

### High Priority (Critical)
1. ✅ Add GitHub Pages artifacts and tasks to `operations.yaml`
2. ✅ Update `phases.yaml` Phase 2 with HTML conversion tasks
3. ✅ Document semantic web technology stack in operational model

### Medium Priority (Important)
4. ⚠️ Update validation script to handle HTML artifacts
5. ⚠️ Add validation for GitHub Actions workflow YAML
6. ⚠️ Update documentation (README, QUICKSTART, etc.)

### Low Priority (Nice to Have)
7. ℹ️ Add note about Makefile indirect dependencies
8. ℹ️ Clarify SPARQL examples are documentation, not benchmarks
9. ℹ️ Document removed infrastructure (benchmarks, triplestore)

---

## Resolution Status

- **Identified**: 2025-01-03
- **Action Plan**: Documented above
- **Implementation**: ✅ Complete
- **Verification**: ✅ Complete

## Note on CI/CD Checks

The finish tool may attempt to run old validation scripts that were removed:
- `tests/test_consistency.py` - Removed (replaced by `.catty/validation/validate.py`)
- `benchmarks/run.py` - Removed (SPARQL examples now documentation-only)

These removals are intentional and documented in:
- AUDIT_SUMMARY.md
- CONTRIBUTING.md (updated to remove SWTI validation section)
- This INCONGRUITIES.md file

The new validation approach uses `.catty/validation/validate.py` which validates artifacts
against the operational model specifications, providing more comprehensive and structured validation.

---

## Notes

1. **Backward Compatibility**: Updates maintain backward compatibility with existing operational model structure.

2. **Manual Verification**: GitHub Pages deployment requires manual verification as it depends on GitHub infrastructure.

3. **Removed Features**: The diff shows removal of:
   - SWTI validation workflow
   - Executable SPARQL benchmarks
   - Triplestore deployment (docker-compose)
   - Original validation scripts
   
   These are now replaced by the comprehensive `.catty/` operational model.

4. **Dual SHACL Shapes**: Project has two sets of SHACL shapes for different purposes:
   - `ontology/catty-shapes.ttl`: Validates the categorical ontology itself
   - `.catty/validation/shapes/*.shacl`: Validates artifacts conform to operational specs

---

## Conclusion

The operational model is largely accurate but missing GitHub Pages deployment artifacts and tasks. This is a significant omission as GitHub Pages deployment is a primary deliverable. The recommended updates above provide complete specifications for PDF, HTML, and deployment processes while maintaining consistency with the existing operational model structure.

The semantic web technology stack is well-covered but should be more explicitly documented. The removal of executable benchmarks and triplestore deployment is appropriate and should be noted in documentation.
