# Agent Guidelines for Thesis Development

## Overview

The `thesis/` directory contains the LaTeX source code for the Catty thesis. This AGENTS.md provides MCP-compliant specifications for agents generating, compiling, and managing thesis content.

## Agent Role Definition

### Thesis Generation Agent
**Primary Purpose**: Generate LaTeX content according to formal specifications and constraints
**Key Capabilities Required**:
- LaTeX document generation and compilation
- Mathematical notation and formal logic typesetting
- Bibliography management and citation integration
- Cross-reference and linking maintenance
- PDF generation and validation

## MCP-Compliant Thesis Specifications

### Required Agent Capabilities

#### LaTeX Processing
```json
{
  "latex_operations": {
    "compilation": ["pdflatex", "biber", "bibtex"],
    "macros": ["\\cite{}", "\\begin{theorem}", "\\begin{definition}", "\\begin{example}"],
    "structure": ["chapter", "section", "subsection"],
    "validation": ["syntax_check", "reference_check", "bibliography_check"]
  },
  "mathematical_typesetting": {
    "logic_notation": ["\\vdash", "\\land", "\\lor", "\\to", "\\neg"],
    "category_theory": ["\\rightarrow", "\\times", "\\oplus", "\\otimes"],
    "formal_systems": ["\\frac", "\\mathcal", "\\mathbb", "\\mathfrak"]
  }
}
```

### Directory Structure

```
thesis/
├── main.tex                    # Main thesis document
├── chapters/                   # Individual chapter files
│   ├── introduction.tex
│   ├── categorical-semantic-audit.tex
│   └── conclusions.tex
├── macros/                     # Custom LaTeX macros
│   └── citations.tex          # Citation and reference macros
├── bibliography/              # Bibliography files
│   └── references.bib         # BibTeX bibliography
├── figures/                   # Figure files
└── build/                     # Compiled output
    └── catty-thesis.pdf       # Final thesis PDF
```

### LaTeX Structure Requirements

#### Chapter Organization
Each chapter must follow prescribed structure:
- Unique ID following pattern `sec-[lowercase-hyphenated]`
- Formal mathematical content with proper notation
- Citations using only registered keys
- Cross-references using generated labels

#### Mathematical Content Standards
- **Theorems**: Must have unique IDs, statements, and proofs
- **Definitions**: Must define terms clearly with examples
- **Lemmas**: Must support theorem proofs with clear statements
- **Examples**: Must instantiate abstract concepts concretely

### Compilation Protocol

#### Pre-Compilation Checklist
1. **Verify source files** exist and are syntactically valid
2. **Check bibliography** consistency with citation registry
3. **Validate cross-references** and labels
4. **Ensure all dependencies** are available
5. **Run validation scripts** to verify content compliance

#### Compilation Process
```bash
# Clean previous builds
cd thesis
make clean

# Compile thesis
make

# Validate output
python ../schema/validators/validate_tex_structure.py --tex-dir chapters/
```

#### Compilation Requirements
- All LaTeX files must compile without errors
- Bibliography must resolve all citations
- PDF must contain all chapters and figures
- Cross-references must resolve correctly
- Generated labels must be unique and consistent

### Citation Integration

#### Citation Macros
The thesis uses custom citation macros defined in `macros/citations.tex`:

```latex
% Basic citation
\cite{girard1987linear}

% Citation with page number
\citepage{girard1987linear}{42}

% Definition with provenance
\definedfrom{linear logic}{girard1987linear}

% Theorem with proof reference
\provedfrom{main theorem}{sambin2003basic}
```

#### Bibliography Management
- All citations must reference keys in `bibliography/citations.yaml`
- Bibliography file: `bibliography/references.bib`
- Citation processing: Biber backend
- Bibliography style: Academic standard

### Content Validation

#### Automated Validation
```bash
# Validate TeX structure
python ../schema/validators/validate_tex_structure.py --tex-dir chapters/

# Validate citations
python ../schema/validators/validate_citations.py --tex-dir chapters/

# Validate overall consistency
python ../schema/validators/validate_consistency.py --tex-dir chapters/
```

#### Content Quality Checks
- Mathematical notation correctness
- Citation registry compliance
- ID uniqueness across all files
- Cross-reference integrity
- Bibliography completeness

### Error Handling

#### Compilation Errors
1. **Identify error location** in LaTeX source
2. **Check syntax compliance** with LaTeX standards
3. **Verify macro definitions** and usage
4. **Resolve bibliography issues** with citation keys
5. **Fix cross-reference problems** with labels
6. **Re-compile** until successful

#### Validation Failures
1. **Read specific validation error** messages
2. **Locate problematic content** in source files
3. **Apply corrective actions** according to schema constraints
4. **Re-run validation** to verify fixes
5. **Report compliance status** when all checks pass

### Integration with Other Components

#### RDF/OWL Integration
- Thesis content must correspond to RDF representations
- IDs must match semantic web identifiers
- Citations must have provenance links in ontology
- Mathematical structures must map to categorical objects

#### Validation Framework Integration
- Thesis content validated against `schema/thesis-structure.schema.yaml`
- Citations validated against `bibliography/citations.yaml`
- RDF consistency validated against ontology structures
- Overall consistency validated against bidirectional mappings

### Quality Assurance

#### Pre-Generation Standards
- Review citation registry for required sources
- Check ID uniqueness requirements
- Understand mathematical notation standards
- Verify LaTeX macro definitions and usage

#### Generation Monitoring
- Ensure all citations use registered keys
- Verify mathematical notation correctness
- Maintain cross-reference integrity
- Preserve formal logical structure

#### Post-Generation Validation
- Complete LaTeX compilation without errors
- All validation scripts pass successfully
- PDF output contains complete content
- Bibliography and citations resolve correctly

### Emergency Procedures

#### Unresolvable Compilation Issues
1. **Document specific error** and context
2. **Check dependencies** and file availability
3. **Verify LaTeX installation** and packages
4. **Request assistance** with technical issues
5. **Report system status** for resolution

#### Constraint Violations
1. **STOP generation** immediately
2. **IDENTIFY violation type** (citation, structure, ID)
3. **REFER to schema constraints** for corrective actions
4. **APPLY fixes** systematically
5. **VALIDATE compliance** before proceeding

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: schema/, bibliography/, ontology/  
**Last Updated**: 2025-01-06