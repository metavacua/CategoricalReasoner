# Contributing to Catty

Contributions are welcome via pull requests. This repository follows an **issue-driven development model** where all contributions address specific, well-defined tasks.

## Core Principles

1. **No placeholders**: Every section is either substantive or explicitly marked as a future vacancy
2. **Issue-driven**: Create an issue before starting significant work
3. **Validation required**: All LaTeX must build; all RDF must parse
4. **Incremental progress**: Small, focused contributions preferred
5. **Keep changes focused**: One logical change per PR

## Before You Start

Review the white paper (`thesis/main.pdf` after building) or existing issues for:
- Vacancies explicitly marked in the thesis
- Missing proofs or formalizations
- Ontology extensions needed
- Code generation features
- Documentation improvements

## Issue Templates

Use `.github/ISSUE_TEMPLATE/` for:
- **theorem.md**: Formalizing theorems or meta-theorems
- **chapter.md**: Expanding or creating chapters
- **ontology.md**: Extending RDF/OWL ontologies
- **proof.md**: Completing specific proofs
- **feature.md**: Implementing tools or code generation

## Local Checks

### LaTeX Build

Before opening a PR, ensure the LaTeX build works:

```sh
cd thesis
make clean
make
```

### RDF Validation

Validate all RDF/TTL files:

```sh
python3 tools/validate-rdf.py --all
```

(Requires `pip install rdflib`)

### Git Status

Remove any temporary or test files:

```sh
git status
# Clean up any unwanted files before committing
```

## Contribution Types

### LaTeX Contributions

- Place new chapters in `thesis/chapters/`
- Use existing chapters as style templates
- Ensure all cross-references resolve
- Add bibliography entries to `thesis/backmatter/bibliography.bib`

### RDF/Ontology Contributions

- Place new ontologies in `ontology/` or `ontology/examples/`
- Follow existing naming conventions
- Include external references (DBpedia, Wikidata) where applicable
- Add SPARQL examples if introducing new queries

### Code/Tool Contributions

- Place scripts in `tools/` or appropriate subdirectory
- Include docstrings and usage documentation
- Add tests or usage examples
- Update `tools/README.md`

## Scope

- Keep changes small and focused
- Do not commit generated build artifacts (PDF/HTML/aux files)
- Thesis source lives under `thesis/` and chapters under `thesis/chapters/`
- Ontologies live under `ontology/`

## Style

- **LaTeX**: Prefer plain LaTeX without heavy custom macros unless needed
- **RDF/Turtle**: Use consistent prefixes (see `ontology/examples/`)
- **Python**: Follow PEP 8; include docstrings and type hints
- If you add new packages or tooling, include rationale in PR description

## Pull Request Process

1. Reference the issue number in your PR description
2. Provide a clear summary of changes
3. List all files added/modified
4. Confirm validation steps passed
5. Respond to review feedback promptly

## License

By contributing, you agree that your contributions will be licensed under **AGPL-3.0-or-later**.

## Questions?

- Check existing issues for similar questions
- Open a new issue with the "question" label
- Consult the thesis documentation for technical details

Thank you for contributing!
