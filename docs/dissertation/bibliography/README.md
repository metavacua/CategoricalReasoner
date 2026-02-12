# Bibliography Directory

## Purpose

The `docs/dissertation/bibliography/` directory contains the master citation registry (`citations.yaml`) for the Catty thesis. This registry is the single source of truth for all citations used in the thesis.

## Citation Registry

### `citations.yaml`

The authoritative source for all citations used in the thesis.

TODO: almost all references of interest involve multiple authors, and the current format does not allow for multiple author entries.

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
This is not a closed set; there are many entries not yet recorded in this, and there are a few recorded here that have not been read or reviewed by the author at this time (Feb 2026); the primary references read and reviewed by the author are G. Sambin, J. Trafford, and I. Urbas. 

The registry contains pre-registered foundational references:

| Key | Author | Title | Year |
|-----|---------|-------|------|
| `girard1987linear` | J.-Y. Girard | Linear Logic | 1987 |
| `kripke1965semantical` | S.A. Kripke | Semantical Analysis of Intuitionistic Logic I | 1965 |
| `sambin2003basic` | G. Sambin | Basic Logic: Reflection, Symmetry, Visibility | 2003 |
| `urbas1996LDJ` | I. Urbas | Dual Intuitionistic Logic | 1996 |
| `trafford2016CoLJ` | J. Trafford | Structuring Co-Constructive Logic for Proofs and Refutations | 2016 |
| `lawvere1963functorial` | F.W. Lawvere | Functorial Semantics of Algebraic Theories | 1963 |
| `maclane1971categories` | S. Mac Lane | Categories for the Working Mathematician | 1971 |
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

TODO: Tex, SPARQL, and Java validation infrastructure particularly translated into GitHub features like actions, workflows, or hooks.

## Adding New Citations

### Step 1: Add to Registry

Add a new entry to `docs/dissertation/bibliography/citations.yaml`:

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

TODO.

### Step 4: Use in Thesis

Reference the citation in TeX chapters:

```latex
This work builds on \cite{newauthor2020paper}.
```

## See Also

- `src/schema/README.md` - Validation schemas and constraints
- `src/schema/AGENTS.md` - Citation usage constraints for LLMs
- `thesis/macros/citations.tex` - TeX citation macro definitions
