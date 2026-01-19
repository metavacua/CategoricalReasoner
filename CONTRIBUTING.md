# Contributing to Catty

Thank you for your interest in contributing to the Catty thesis project!

## Development Setup

### Prerequisites

- Python 3.11+
- Java 17+ (OpenJDK recommended)
- Maven 3.8+
- Git

### Initial Setup

```bash
# Clone the repository
git clone https://github.com/metavacua/CategoricalReasoner.git
cd CategoricalReasoner

# Install Python dependencies
pip install -r requirements-dev.txt

# Test Python stack
pytest tests/

# Build and test Java stack
cd java
mvn clean test
cd ..
```

## Development Workflow

1. **Create a feature branch**: `git checkout -b feat-your-feature`
2. **Make your changes**: Follow the coding standards below
3. **Run tests**: Ensure all tests pass
4. **Commit**: Use descriptive commit messages
5. **Push**: `git push origin feat-your-feature`
6. **Open a PR**: Include description and testing notes

## Coding Standards

### Python

- Use **Black** for formatting: `black scripts/ schema/ tests/`
- Use **type hints** wherever possible
- Follow **PEP 8** conventions
- Maximum line length: 120 characters
- Document all public functions with docstrings

### Java

- Follow **Java Code Conventions**
- Use **records** for immutable data classes
- Add **JavaDoc** for all public classes and methods
- Compile with `-Xlint:all -Werror` (enforced by Maven)
- Use `final` for immutability where appropriate

### Ontologies (RDF/OWL)

- Use **JSON-LD** format for new ontologies
- Register all new ontologies in `.catty/iri-config.yaml`
- Create corresponding `.meta.yaml` file
- Use **relative context** references (`context.jsonld`)
- Validate with `python schema/validators/validate_iris.py`

## Testing

### Python Tests

```bash
# Run all tests
pytest tests/

# Run specific test file
pytest tests/test_iri_config.py

# Run with coverage
pytest --cov=scripts --cov=schema tests/
```

### Java Tests

```bash
cd java
mvn test
```

### Integration Tests

```bash
# Validate full semantic web stack
python schema/validators/validate_iris.py --config .catty/iri-config.yaml
python scripts/validate_rdf.py
python benchmarks/run.py
```

## Pull Request Guidelines

### PR Title Format

- `feat: Add new feature`
- `fix: Fix bug description`
- `docs: Update documentation`
- `test: Add tests for feature`
- `refactor: Refactor component`
- `chore: Update dependencies`

### PR Description Template

```markdown
## Description
Brief description of changes

## Motivation
Why is this change needed?

## Changes
- List of changes made

## Testing
How was this tested?

## Checklist
- [ ] Tests pass locally
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] No new warnings
```

## Validation Checklist

Before submitting a PR, ensure:

- [ ] All Python tests pass: `pytest tests/`
- [ ] All Java tests pass: `mvn -f java/pom.xml test`
- [ ] IRI validation passes: `python schema/validators/validate_iris.py --config .catty/iri-config.yaml`
- [ ] RDF validation passes: `python scripts/validate_rdf.py`
- [ ] SPARQL benchmarks run: `python benchmarks/run.py`
- [ ] Code is formatted (Black for Python)
- [ ] No linting errors
- [ ] Documentation updated if needed

## Semantic Web Development

### Adding a New Ontology

1. Create the JSON-LD file: `ontology/your-ontology.jsonld`
2. Use relative context: `"@context": ["context.jsonld", {...}]`
3. Set `@base` to match localhost pattern
4. Register in `.catty/iri-config.yaml`:
   ```yaml
   your-ontology:
     localhost_iri: "http://localhost:8080/ontology/your-ontology#"
     production_iri: "https://metavacua.github.io/CategoricalReasoner/ontology/your-ontology#"
     context_url: "http://localhost:8080/ontology/context.jsonld"
     file: "ontology/your-ontology.jsonld"
   ```
5. Create `ontology/your-ontology.meta.yaml` with metadata
6. Validate: `python schema/validators/validate_iris.py --config .catty/iri-config.yaml`

### Local Development Server

Test ontologies with the Java server:

```bash
cd java
mvn exec:java
```

Open `http://localhost:8080/` to query ontologies interactively.

## Questions?

- Open an issue for bug reports
- Use discussions for questions
- Check existing issues before creating new ones

## License

By contributing, you agree that your contributions will be licensed under the AGPL-3.0 license.
