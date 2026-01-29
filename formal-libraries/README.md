# Formal Libraries

This directory integrates external formal libraries to serve as reference models for Catty's code generation and verification tasks.

## Contents

### 1. ULO (Universal Logic Ontology)
- **Path**: `ulo/`
- **Repository**: `https://gl.mathhub.info/ulo/ulo`
- **License**: CC-SAv4 (Compatible with code generation goals)

### 2. Isabelle Distribution
- **Path**: `isabelle-distribution/`
- **Repository**: `https://gl.mathhub.info/Isabelle/Distribution`
- **License**: BSD-3-Clause

### 3. Isabelle AFP (Selected Subset)
- **Path**: `isabelle-afp/`
- **Repository**: `https://gl.mathhub.info/Isabelle/AFP`
- **License**: Various (BSD/LGPL per entry)

**Included Modules:**
- `Category` (Basic Category Theory)
- `Category2`
- `Category3`
- `MonoidalCategory`
- `Bicategory`
- `AxiomaticCategoryTheory`
- `Abstract-Hoare-Logics`
- `SequentInvertibility`
- `Relation_Algebra`
- `Constructive_Cryptography`

## Developer Notes

### Updating Libraries
To update the submodules to their latest upstream versions:
```bash
git submodule update --remote
```

### Cloning
When cloning this repository, ensure submodules are initialized:
```bash
git clone --recursive <repo-url>
```
Or after cloning:
```bash
git submodule update --init --recursive
```

### AFP Structure
The AFP submodule is configured with sparse-checkout to include only relevant theories. If you need additional theories:
1. Navigate to `formal-libraries/isabelle-afp`
2. Run `git sparse-checkout add source/<NewModule>`
