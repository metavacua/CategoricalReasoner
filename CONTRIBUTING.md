# Contributing

Contributions are welcome via pull requests.

## Scope

- Keep changes small and focused.
- Do not commit generated build artifacts (PDF/HTML/aux files).
- Thesis source lives under `docs/dissertation/` and chapters under `docs/dissertation/chapters/`.

## Semantic Web Standards (SWTI)

This project follows the **Semantic Web Technology Index (SWTI)** criteria from the 2022 Nature Scientific Reports study. Please ensure your contributions adhere to these guidelines:

- **S1 (Standard Models)**: All knowledge representation must use RDF/OWL standards (JSON-LD or Turtle).
- **S2 (External Data)**: Link entities to external Linked Open Data sources (DBpedia, Wikidata) using `owl:sameAs` or `skos:exactMatch`.
- **S4 (Evaluation)**: Include reproducible evaluation queries in `src/benchmarks/queries/`.
- **S6 (Reasoning)**: Ensure the ontology is consistent and passes SHACL validation.

## Local checks

### Java Build (Maven)

Before opening a PR, ensure the Java build works:

```sh
# Install Maven 3.9+ and Java 21+
mvn clean compile
```

### Java Tests

```sh
mvn test
```

### Thesis Build

Before opening a PR, ensure the LaTeX build works:

```sh
cd docs/dissertation
make clean
make
```

### Ontology Validation

Validate the RDF and run benchmarks locally (requires `rdflib` and `pyshacl`):

```sh
python src/scripts/validate_rdf.py
python src/benchmarks/run.py
python src/tests/test_consistency.py
```

### Python Development

Install development dependencies:

```sh
# Install pip-tools for dependency management
pip install pip-tools

# Install dependencies
pip install -r requirements-dev.txt

# Run tests
pytest src/tests/ -v
```

## Style

- Prefer plain LaTeX without heavy custom macros unless needed.
- If you add new packages or tooling, include a brief rationale in the PR description.
- Follow `.editorconfig` for consistent formatting.
- Run pre-commit hooks before committing:

```sh
pre-commit run --all-files
```
