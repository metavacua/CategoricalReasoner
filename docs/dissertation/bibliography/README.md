# Citation Registry

## Purpose

This directory contains the master citation registry for the Catty thesis. The registry serves as the authoritative source of all bibliographic references used throughout the thesis.

## Registry Format

The citation registry is stored in `citations.yaml`, a simple YAML file with the following structure:

```yaml
metadata:
  version: "1.0"
  last_updated: "2025-01-05T00:00:00Z"
  purpose: "Approved citation registry for Catty thesis - LLMs may only cite these sources"

citations:
  citationkey:
    author: "Author Name"
    title: "Title of the work"
    year: 2025
    type: "journal"  # or book, conference, preprint, etc.
    doi: "10.xxx/xxxx"  # optional
    url: "https://..."  # optional
    wikidata: "Q123456"  # optional
    arxiv: "1234.56789"  # optional
    dbpedia: "..."  # optional
    local_ontology: false
    notes: "Additional notes about the citation"
```

## Adding Citations

To add a new citation:

1. Open `citations.yaml`
2. Add a new entry under the `citations:` section
3. Choose a unique citation key following the pattern: `[author][year][keyword]`
4. Fill in all required fields: `author`, `title`, `year`, `type`
5. Add optional fields where available: `doi`, `url`, `wikidata`, `arxiv`, `dbpedia`
6. Include notes if relevant
7. Set `local_ontology: false` (all citations are from external sources)

## Citation Key Format

**Convention:** `[familyName][year][disambiguator]`

- Lowercase alphanumeric with hyphens
- Based on the first author's family name
- Disambiguator: `a`, `b`, `c`... for same-author-same-year collisions

**Examples:**
- `girard1987linear` → J.-Y. Girard, 1987, "Linear Logic"
- `trafford2015coconstructive` → J. Trafford, 2015, "Co-constructive logic for proofs and refutations"

## Citation Types

Supported types:
- `book` - Books and monographs
- `journal` - Journal articles
- `conference` - Conference proceedings
- `preprint` - Preprints (arXiv, etc.)
- `incollection` - Chapters in edited volumes
- `thesis` - PhD or Master's theses
- `report` - Technical reports

## Validation

Validate the YAML syntax:

```bash
python -c "import yaml; yaml.safe_load(open('docs/dissertation/bibliography/citations.yaml'))"
```

## Using Citations in LaTeX

Use the appropriate citation macro in LaTeX files:

- Simple citation: `\cite{girard1987linear}`
- Citation with page: `\citepage{girard1987linear}{42}`
- Definition with provenance: `\definedfrom{structural rule}{urbas1996dual}`
- Theorem with provenance: `\provedfrom{Main Theorem}{sambin2000basic}`

## Important Constraints

- **Do NOT invent new citation keys** - all keys must exist in `citations.yaml`
- **Do NOT create Java source code** - this is a simple YAML registry
- **Do NOT create RO-Crate files** - the system uses YAML directly
- **All citations must be traceable** - include DOIs, URLs, or other identifiers when available

## See Also

- `docs/dissertation/bibliography/AGENTS.md` - Citation management constraints
- `src/schema/AGENTS.md` - Citation usage constraints in validation
- Root `AGENTS.md` - General citation constraints
