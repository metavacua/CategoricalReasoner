# Tools for Catty

This directory contains utility scripts for validating and working with the Catty ontologies and thesis.

## RDF Validation: `validate-rdf.py`

Validates RDF/TTL files for syntactic correctness using RDFLib.

### Installation

```bash
pip install rdflib
```

### Usage

**Validate a single file:**

```bash
python3 tools/validate-rdf.py ontology/examples/classical-logic.ttl
```

**Validate a directory (recursively):**

```bash
python3 tools/validate-rdf.py ontology/
```

**Validate all ontology files:**

```bash
python3 tools/validate-rdf.py --all
```

### Output

```
✓ classical-logic.ttl
  ✓ Valid (45 triples, 12 subjects, 8 predicates)

✗ broken-file.ttl
  ✗ Syntax Error: Expected '}' but got EOF
```

### Supported Formats

- Turtle (`.ttl`)
- JSON-LD (`.jsonld`)
- RDF/XML (`.rdf`, `.xml`)
- N-Triples (`.nt`)

## Future Tools

The following tools are planned for future development:

### SHACL Validation: `validate-shacl.py`

Validate RDF graphs against SHACL constraints.

**Status**: To be implemented (see issue #X)

**Dependencies**: `pyshacl`

### Code Generator: `generate-witness.py`

Generate executable programs from RDF logic-theory specifications.

**Status**: To be implemented (Phase 2 of roadmap)

**Dependencies**: `rdflib`, target language toolchain

### SPARQL Query Tool: `query-ontology.py`

Execute SPARQL queries against the Catty knowledge graph.

**Status**: To be implemented (see issue #X)

**Dependencies**: `rdflib`

## Development

To add a new tool:

1. Create the script in this directory
2. Make it executable: `chmod +x tools/your-script.py`
3. Add a section to this README documenting usage
4. Add tests (if applicable)
5. Update CI/CD workflows to run the tool (if applicable)

## Testing

Run validation on all ontology files:

```bash
cd /path/to/Catty
python3 tools/validate-rdf.py --all
```

Expected output: All files valid.

## License

All tools in this directory are licensed under the same license as the Catty project: **AGPL-3.0-or-later**.
