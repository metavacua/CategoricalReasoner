# Bibliography Directory

## Purpose

The `bibliography/` directory contains the master citation registry (`citations.yaml`) for the Catty thesis. This registry is the single source of truth for all citations used in the thesis.

## Citation Registry

### `citations.yaml`

The authoritative source for all citations used in the thesis.

**Structure**:
```yaml
citation-key:
  author: "Author Name"
  title: "Paper or Book Title"
  year: 2020
  type: "book"  # or article, conference, incollection, thesis, report
  doi: "10.xxxx/..."  # optional
  url: "https://..."  # optional
  wikidata: "Q123456"  # optional (Wikidata QID)
  arxiv: "2001.12345"  # optional
  notes: "Additional notes or comments"
```

**Key Format**: `[author][year][keyword]` - lowercase, hyphenated, globally unique
- Example: `girard1987linear`, `kripke1965semantical`

**Required Fields**:
- `author`: Author name(s)
- `title`: Full title of the work
- `year`: Publication year
- `type`: One of: book, article, conference, incollection, thesis, report

**Optional Fields**:
- `doi`: Digital Object Identifier
- `url": Permanent URL to the work
- `wikidata`: Wikidata QID (e.g., "Q123456")
- `arxiv`: arXiv identifier (e.g., "2001.12345")
- `notes`: Additional context or comments

## Current Citations

The registry contains pre-registered foundational references:

| Key | Author | Title | Year |
|-----|---------|-------|------|
| `girard1987linear` | J.-Y. Girard | Linear Logic | 1987 |
| `kripke1965semantical` | S.A. Kripke | Semantical Analysis of Intuitionistic Logic I | 1965 |
| `sambin2003basic` | G. Sambin | Basic Logic: Reflection, Symmetry, Visibility | 2003 |
| `urbas1993structural` | J. Urbas | On the Structural Rules of Linear Logic | 1993 |
| `trafford2018category` | J. Trafford | A Category Theory Approach to Conceptual Modelling | 2018 |
| `lawvere1963functorial` | F.W. Lawvere | Functorial Semantics of Algebraic Theories | 1963 |
| `mac lane1971categories` | S. Mac Lane | Categories for the Working Mathematician | 1971 |
| `lambek1988category` | J. Lambek | Categories and Categorical Grammars | 1988 |
| `restall2000substructural` | G. Restall | Substructural Logics | 2000 |
| `pierce1991category` | B.C. Pierce | Basic Category Theory for Computer Scientists | 1991 |
| `curyhoward1934` | H.B. Curry | Functionality in Combinatory Logic | 1934 |
| `howard1969formulae` | W.A. Howard | The Formulae-as-Types Notion of Construction | 1969 |
| `negri2011proof` | S. Negri | Proof Analysis: A Contribution to Hilbert's Problem | 2011 |

## Integration with Thesis

### TeX Citation Macros

Citations in thesis chapters use macros defined in `thesis/macros/citations.tex`:

- `\cite{key}` - Simple citation
- `\citepage{key}{page}` - Citation with page number
- `\citefigure{key}{figure}` - Citation with figure/table reference
- `\definedfrom{term}{key}` - Definition cites source
- `\provedfrom{theorem}{key}` - Theorem cites proof source

**Example**:
```latex
Linear logic was introduced by \cite{girard1987linear}.

As shown in \citepage{girard1987linear}{42}, the multiplicative
connective satisfies certain properties.
```

### Validation

All citations used in TeX must exist in `citations.yaml`. Validation enforces:

1. **TeX citations**: Every `\cite{key}` in TeX files must have a corresponding entry in the registry
2. **External links**: DOI, URL, Wikidata, and arXiv identifiers (if present) must be resolvable
3. **Key format**: All citation keys must match the pattern `[author][year][keyword]`

**Run validation**:
```bash
python schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml \
  --check-external
```

## Adding New Citations

### Step 1: Add to Registry

Add a new entry to `bibliography/citations.yaml`:

```yaml
newauthor2020paper:
  author: "First Last"
  title: "Paper Title"
  year: 2020
  type: "journal"
  doi: "10.xxxx/..."
  url: "https://..."
  wikidata: "Q123456"
  notes: "Description of why this citation is included"
```

### Step 2: Validate Key Format

Ensure the key follows `[author][year][keyword]` pattern:
- Lowercase only
- Hyphenated
- Globally unique (no duplicates)

**Good**: `smith2020quantum`, `johnsondoe2019distributed`
**Bad**: `Smith2020Quantum` (uppercase), `my-paper` (no year)

### Step 3: Validate External Links

Ensure external identifiers are resolvable:

```bash
# Validate all citations
python schema/validators/validate_citations.py \
  --bibliography bibliography/citations.yaml \
  --check-external
```

### Step 4: Use in Thesis

Reference the citation in TeX chapters:

```latex
This work builds on \cite{newauthor2020paper}.
```

## Architecture Note

**Important**: The `bibliography/` directory is **not** connected to local `/ontology/` RDF files. The ontology directory contains example/reference materials only. Citation management is centralized in `citations.yaml`.

No RDF files are required for citation management. All citation validation happens via:
1. TeX macro validation (structure)
2. YAML registry validation (key existence, format)
3. External link validation (resolvability)

## Technology Note

Citation registry uses YAML for simplicity and human readability. Future enhancements could include:
- Integration with external bibliography managers (Zotero, Mendeley)
- Automatic metadata retrieval from DOI/Wikidata
- BibTeX export for compatibility with LaTeX bibliography tools

## Validation Commands

### Validate Syntax

```bash
python -c "import yaml; yaml.safe_load(open('bibliography/citations.yaml'))"
```

### Validate Citations in Thesis

```bash
python schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml \
  --check-external
```

### Check for Unused Citations

```bash
# (Future enhancement) List citations in registry not used in TeX
python scripts/check-unused-citations.py \
  --bibliography bibliography/citations.yaml \
  --tex-dir thesis/chapters/
```

## See Also

- `schema/README.md` - Validation schemas and constraints
- `schema/AGENTS.md` - Citation usage constraints for LLMs
- `thesis/macros/citations.tex` - TeX citation macro definitions
