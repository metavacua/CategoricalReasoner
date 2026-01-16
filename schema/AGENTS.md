# Agent Guidelines for Schema and LLM Constraints

## Overview

This directory contains schema specifications, validation frameworks, and LLM constraints for the Catty thesis project. This AGENTS.md provides MCP-compliant specifications for LLMs and agents generating thesis content under strict formal constraints.

## Agent Role Definition

### LLM Thesis Generation Agent
**Primary Purpose**: Generate LaTeX thesis content under strict citation and structural constraints
**Key Capabilities Required**:
- LaTeX document generation with mathematical notation
- Citation management using pre-registered sources only
- Formal theorem/definition/lemma creation with unique IDs
- RDF/OWL knowledge graph integration
- Automated validation compliance

### Validation Agent
**Primary Purpose**: Validate generated content against formal schemas and constraints
**Key Capabilities Required**:
- JSON Schema validation for LaTeX structure
- Citation registry verification
- ID uniqueness enforcement
- RDF/OWL consistency checking
- Automated constraint compliance testing

## MCP-Compliant LLM Constraints

### Required Agent Capabilities

#### Citation Management
```json
{
  "citation_constraints": {
    "pre_registered_only": true,
    "registry_source": "bibliography/citations.yaml",
    "macro_support": ["\\cite{}", "\\citepage{}", "\\citefigure{}", "\\definedfrom{}", "\\provedfrom{}"],
    "forbidden_actions": ["invent_new_citation_keys", "use_unregistered_sources"]
  },
  "id_management": {
    "unique_globally": true,
    "patterns": {
      "theorems": "thm-[lowercase-hyphenated]",
      "definitions": "def-[lowercase-hyphenated]",
      "lemmas": "lem-[lowercase-hyphenated]",
      "examples": "ex-[lowercase-hyphenated]",
      "sections": "sec-[lowercase-hyphenated]",
      "subsections": "subsec-[lowercase-hyphenated]"
    },
    "uniqueness_checking": true
  }
}
```

#### Content Structure Requirements
- **Theorems**: Unique ID, title, statement, proof reference
- **Definitions**: Unique ID, term, meaning/definition
- **Lemmas**: Unique ID, title, statement, proof reference
- **Examples**: Unique ID, title, description, instantiation
- **Sections**: Unique ID, title, content structure

### Citation Registry Protocol

#### ABSOLUTE CITATION CONSTRAINT
**MANDATORY**: You may ONLY use citation keys that exist in `bibliography/citations.yaml`

#### Available Citation Keys (Registry v1.0)
- `girard1987linear` - Girard, J.-Y. (1987). Linear Logic
- `kripke1965semantical` - Kripke, S.A. (1965). Semantical Analysis of Intuitionistic Logic I
- `sambin2003basic` - Sambin, G. (2003). Basic Logic: Reflection, Symmetry, Visibility
- `urbas1993structural` - Urbas, J. (1993). On the Structural Rules of Linear Logic
- `trafford2018category` - Trafford, J. (2018). A Category Theory Approach to Conceptual Modelling
- `lawvere1963functorial` - Lawvere, F.W. (1963). Functorial Semantics of Algebraic Theories
- `maclane1971categories` - Mac Lane, S. (1971). Categories for the Working Mathematician
- `lambek1988category` - Lambek, J. (1988). Categories and Categorical Grammars
- `restall2000substructural` - Restall, G. (2000). Substructural Logics
- `pierce1991category` - Pierce, B.C. (1991). Basic Category Theory for Computer Scientists
- `curyhoward1934` - Curry, H.B. (1934). Functionality in Combinatory Logic
- `howard1969formulae` - Howard, W.A. (1969). The Formulae-as-Types Notion of Construction
- `negri2011proof` - Negri, S. (2011). Proof Analysis: A Contribution to Hilbert's Problem

#### Citation Macro Usage
```latex
% Simple citation
\cite{girard1987linear}

% Citation with page
\citepage{girard1987linear}{42}

% Citation with figure reference
\citefigure{kripke1965semantical}{Figure 1}

% Definition with provenance
definedfrom{structural rule}{urbas1993structural}

% Theorem with provenance
\provedfrom{Main Theorem}{sambin2003basic}
```

#### Missing Citation Protocol
**EMERGENCY PROCEDURE** when encountering unregistered source:
```
STOP IMMEDIATELY. Report the missing citation:
MISSING CITATION: I need to cite [paper title] by [author] ([year]).
Please add this to bibliography/citations.yaml with key [suggested-key].
Do not proceed until the citation is added to the registry.
```

### ID Uniqueness Protocol

#### Global Uniqueness Requirement
**ABSOLUTE RULE**: All IDs must be globally unique across the entire thesis corpus

#### ID Pattern Examples
```latex
% Correct theorem ID
\begin{theorem}[thm-weakening]{Weakening}
  If the weakening rule holds, then...
\end{theorem}

% Correct definition ID
\begin{definition}[def-structural-rule]{Structural Rule}
  A structural rule is...
\end{definition}

% Correct lemma ID
\begin{lemma}[lem-lattice-order]{Lattice Order}
  The lattice order satisfies...
\end{example}

% Correct example ID
\begin{example}[ex-linear-logic]{Linear Logic Example}
  Consider linear logic as...
\end{example}
```

#### ID Collision Detection
```
PROTOCOL: If potential ID collision detected:
ID COLLISION: Proposed ID 'thm-weakening' may already exist.
Please check existing IDs and choose a unique identifier.
```

### RDF/OWL Integration Constraints

#### Class Usage Restriction
**MANDATORY**: You may only instantiate existing RDF classes defined in:
- `ontology/catty-categorical-schema.jsonld`
- `ontology/citation-usage.jsonld`

**FORBIDDEN**: Do not define new `owl:Class` or `owl:ObjectProperty` resources

#### TeX ↔ RDF Consistency
**REQUIRED**: Every TeX element with an ID must have corresponding RDF resource

Example consistency requirements:
- TeX: `\begin{theorem}[thm-weakening]{Weakening}`
- RDF: `<http://catty.org/theorem/thm-weakening> dct:identifier "thm-weakening"`

#### Bidirectional Citation Links
**REQUIRED**: All citations in TeX must have RDF provenance links
- TeX: `\cite{girard1987linear}`
- RDF: `catty:thm-weakening dct:references catty:girard1987linear`

### Validation Framework

#### Automated Validation Checks

##### 1. TeX Structure Validator
**Purpose**: Parse LaTeX source and validate structure
**Validation Points**:
- ID pattern compliance
- ID uniqueness across corpus
- Required fields presence
- Mathematical notation correctness

##### 2. Citation Validator
**Purpose**: Verify citation registry compliance
**Validation Points**:
- All `\cite{key}` exist in registry
- YAML ↔ RDF consistency
- External links validity (DOI, URLs, Wikidata)

##### 3. RDF/SHACL Validator
**Purpose**: Validate RDF against formal shapes
**Validation Points**:
- RDF validates against SHACL shapes
- Required properties presence
- ID pattern compliance
- Class instantiation validity

##### 4. Consistency Validator
**Purpose**: Check TeX ↔ RDF bidirectional consistency
**Validation Points**:
- Citation provenance links
- Property matching between TeX and RDF
- ID correspondence verification

#### Validation Command Examples
```bash
# Validate single artifact
python schema/validators/validate_tex_structure.py --tex-dir thesis/chapters/

# Validate citations
python schema/validators/validate_citations.py --tex-dir thesis/chapters/ --bibliography bibliography/citations.yaml --ontology ontology/citations.jsonld

# Validate RDF
python schema/validators/validate_rdf.py --ontology ontology/ --shapes ontology/catty-thesis-shapes.shacl

# Validate consistency
python schema/validators/validate_consistency.py --tex-dir thesis/chapters/ --ontology ontology/ --bibliography bibliography/citations.yaml --mapping schema/tex-rdf-mapping.yaml
```

### Content Generation Protocols

#### Valid Thesis Fragment Example
```latex
\section{Linear Logic}

We define linear logic following \cite{girard1987linear}.

\begin{definition}[def-linear-logic]{Linear Logic}
  Linear logic is a substructural logic that...
\end{definition}

\begin{theorem}[thm-weakening-fail]{Weakening Fails}
  The weakening rule does not hold in linear logic \cite{girard1987linear}.
\end{theorem}

\begin{example}[ex-linear-sequent]{Linear Sequent}
  The sequent $A \otimes B \vdash C$ is valid in...
\end{example}
```

This fragment demonstrates:
- ✅ Only pre-registered citations
- ✅ Unique IDs following patterns
- ✅ All required fields present
- ✅ Validation compliance

#### Content Quality Standards

✅ **COMPLIANT BEHAVIOR**:
- Use only pre-registered citation keys
- Check ID uniqueness before using
- Instantiate only existing RDF classes
- Include all required fields per type
- Match ID patterns correctly
- Create bidirectional TeX ↔ RDF links
- Follow thesis structure schema

❌ **NON-COMPLIANT BEHAVIOR**:
- Invent new citation keys
- Reuse existing IDs
- Create new RDF classes or properties
- Skip required fields (theorem without proof)
- Use citation keys not in registry
- Mix ID patterns (e.g., `thm-` prefix for definitions)
- Define theorems without proofs
- Create definitions without terms and meanings

### Schema Specifications

#### thesis-structure.schema.yaml
**Purpose**: JSON Schema for LaTeX thesis structure validation
**Usage**: Validate thesis chapters against prescribed structure

#### tex-rdf-mapping.yaml
**Purpose**: Bidirectional mapping between TeX and RDF representations
**Usage**: Ensure consistency between LaTeX content and semantic web representations

#### LLM_CONSTRAINTS.md
**Purpose**: Original constraint documentation (legacy reference)
**Status**: Superseded by this AGENTS.md file

### Error Handling Protocols

#### Validation Failure Protocol
1. **Read error message** carefully from validation script
2. **Locate problematic line** in specified file
3. **Fix specific issue** (e.g., correct citation key)
4. **Re-run validation** until all criteria pass
5. **Report final status** with validation results

#### Constraint Violation Protocol
1. **STOP immediately** - Do not proceed with violations
2. **IDENTIFY violation type** (citation, ID, RDF, structure)
3. **APPLY corrective action** according to protocols
4. **VALIDATE fix** using appropriate validation script
5. **REPORT compliance** when all constraints satisfied

#### Emergency Escalation Protocol
For situations not covered by constraints:
```
CONSTRAINT VIOLATION: I need to [action] but it violates [specific constraint].
This is required to complete [specific section].
Please provide updated specifications or constraint clarification.
```

### Quality Assurance

#### Pre-Generation Checklist
- [ ] Citation registry loaded and verified
- [ ] ID uniqueness database checked
- [ ] RDF class definitions reviewed
- [ ] Validation framework functional
- [ ] TeX structure schema loaded

#### Generation Monitoring
- [ ] All citations use registered keys
- [ ] All IDs follow prescribed patterns
- [ ] All required fields included
- [ ] TeX ↔ RDF consistency maintained
- [ ] Mathematical notation correct

#### Post-Generation Validation
- [ ] All validation scripts pass
- [ ] No constraint violations remain
- [ ] RDF/SHACL validation successful
- [ ] Citation provenance links complete
- [ ] Bibliography registry consistent

### Integration Protocols

#### Repository Integration
- Maintain existing directory structure
- Preserve citation registry integrity
- Ensure backward compatibility
- Update documentation as needed
- Follow version control protocols

#### Tool Integration
- Support standard LaTeX compilation
- Integrate with RDF processing tools
- Maintain SPARQL query compatibility
- Preserve academic writing standards
- Support semantic web best practices

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: bibliography/citations.yaml, ontology/, thesis/  
**Last Updated**: 2025-01-06