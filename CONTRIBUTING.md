# Contributing

Contributions are welcome via pull requests.

## Scope

- Keep changes small and focused.
- Do not commit generated build artifacts (PDF/HTML/aux files).
- Thesis source lives under `thesis/` and chapters under `thesis/chapters/`.

## Semantic Web Standards (SWTI)

This project follows the **Semantic Web Technology Index (SWTI)** criteria from the 2022 Nature Scientific Reports study. Please ensure your contributions adhere to these guidelines:

- **S1 (Standard Models)**: All knowledge representation must use RDF/OWL standards (JSON-LD or Turtle).
- **S2 (External Data)**: Link entities to external Linked Open Data sources (DBpedia, Wikidata) using `owl:sameAs` or `skos:exactMatch`.
- **S4 (Evaluation)**: Include reproducible evaluation queries in `benchmarks/queries/`.
- **S6 (Reasoning)**: Ensure the ontology is consistent and passes SHACL validation.

## Local checks

### Thesis Build

Before opening a PR, ensure the LaTeX build works:

```sh
cd thesis
make clean
make
```

### Ontology Validation

Validate the RDF and run benchmarks locally (requires `rdflib` and `pyshacl`):

```sh
python scripts/validate_rdf.py
python benchmarks/run.py
python tests/test_consistency.py
```

## Style

- Prefer plain LaTeX without heavy custom macros unless needed.
- If you add new packages or tooling, include a brief rationale in the PR description.
