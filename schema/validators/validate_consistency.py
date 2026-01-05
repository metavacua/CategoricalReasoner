#!/usr/bin/env python3
"""
Bidirectional Consistency Validator for Catty Thesis
Validates TeX ↔ RDF ↔ Citation consistency
"""

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Set, Tuple, Optional
from dataclasses import dataclass

# Try to import required libraries
try:
    import yaml
except ImportError:
    print("ERROR: PyYAML is required. Install with: pip install pyyaml")
    sys.exit(1)

try:
    from rdflib import Graph, URIRef, Literal
    from rdflib.namespace import RDF, RDFS, DC
except ImportError:
    print("ERROR: rdflib is required. Install with: pip install rdflib")
    sys.exit(1)


@dataclass
class TeXElement:
    """Represents a parsed TeX element"""
    element_type: str
    id: str
    title: Optional[str] = None
    line_number: int = 0
    file: str = ""
    citations: List[str] = None


@dataclass
class ValidationError:
    """Represents a validation error"""
    file: str
    line: int
    message: str
    severity: str = "ERROR"


class ConsistencyValidator:
    """Validates bidirectional consistency between TeX, RDF, and citations"""

    def __init__(self, mapping_file: Path):
        self.errors: List[ValidationError] = []
        self.tex_elements: List[TeXElement] = []
        self.rdf_elements: Dict[str, Dict] = {}
        self.citations: Set[str] = set()
        self.mapping = {}

        if mapping_file:
            self.load_mapping(mapping_file)

    def load_mapping(self, mapping_file: Path):
        """Load TeX-RDF mapping"""
        try:
            with open(mapping_file, 'r') as f:
                data = yaml.safe_load(f)

            # Build mapping dictionary
            for elem_type, mapping in data['mappings'].items():
                self.mapping[elem_type] = mapping

            print(f"Loaded mapping for {len(self.mapping)} element types")
        except Exception as e:
            self.errors.append(ValidationError(
                file=str(mapping_file), line=0,
                message=f"Error loading mapping: {e}",
                severity="FATAL"
            ))
            sys.exit(1)

    def load_citations(self, registry_file: Path):
        """Load citation registry"""
        try:
            with open(registry_file, 'r') as f:
                data = yaml.safe_load(f)

            self.citations = set(data['citations'].keys())
            print(f"Loaded {len(self.citations)} citations from registry")
        except Exception as e:
            self.errors.append(ValidationError(
                file=str(registry_file), line=0,
                message=f"Error loading citation registry: {e}",
                severity="ERROR"
            ))

    def parse_tex_files(self, tex_dir: Path):
        """Parse all TeX files and extract structured elements"""
        tex_files = list(tex_dir.glob('*.tex'))

        for tex_file in tex_files:
            with open(tex_file, 'r', encoding='utf-8') as f:
                lines = f.readlines()

            for i, line in enumerate(lines, start=1):
                # Parse citations
                cite_matches = re.finditer(r'\\cite\{([^}]+)\}', line)
                citations = []
                for match in cite_matches:
                    keys = match.group(1).split(',')
                    for key in keys:
                        key = key.strip()
                        if key:
                            citations.append(key)

                # Parse theorems: \begin{theorem}[id]{title}
                theorem_match = re.search(r'\\begin\{theorem\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
                if theorem_match:
                    elem_id = theorem_match.group(1).strip()
                    title = theorem_match.group(2).strip()
                    self.tex_elements.append(TeXElement(
                        element_type='theorem',
                        id=elem_id,
                        title=title,
                        line_number=i,
                        file=str(tex_file),
                        citations=citations
                    ))
                    continue

                # Parse definitions: \begin{definition}[id]{term}
                def_match = re.search(r'\\begin\{definition\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
                if def_match:
                    elem_id = def_match.group(1).strip()
                    title = def_match.group(2).strip()
                    self.tex_elements.append(TeXElement(
                        element_type='definition',
                        id=elem_id,
                        title=title,
                        line_number=i,
                        file=str(tex_file),
                        citations=citations
                    ))
                    continue

                # Parse lemmas: \begin{lemma}[id]{title}
                lemma_match = re.search(r'\\begin\{lemma\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
                if lemma_match:
                    elem_id = lemma_match.group(1).strip()
                    title = lemma_match.group(2).strip()
                    self.tex_elements.append(TeXElement(
                        element_type='lemma',
                        id=elem_id,
                        title=title,
                        line_number=i,
                        file=str(tex_file),
                        citations=citations
                    ))
                    continue

                # Parse examples: \begin{example}[id]{title}
                ex_match = re.search(r'\\begin\{example\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
                if ex_match:
                    elem_id = ex_match.group(1).strip()
                    title = ex_match.group(2).strip()
                    self.tex_elements.append(TeXElement(
                        element_type='example',
                        id=elem_id,
                        title=title,
                        line_number=i,
                        file=str(tex_file),
                        citations=citations
                    ))
                    continue

                # Parse sections: \section{id}{title} or \section{title}
                section_match = re.search(r'\\section\*?\s*\{([^}]+)\}', line)
                if section_match:
                    title = section_match.group(1).strip()
                    # Remove special characters, keep only alphanumeric, hyphens, and spaces
                    clean_title = re.sub(r'[^a-zA-Z0-9\s-]', '', title)
                    elem_id = f"sec-{clean_title.lower().replace(' ', '-')}"
                    self.tex_elements.append(TeXElement(
                        element_type='section',
                        id=elem_id,
                        title=title,
                        line_number=i,
                        file=str(tex_file),
                        citations=citations
                    ))
                    continue

        print(f"Parsed {len(self.tex_elements)} TeX elements")

    def load_rdf_elements(self, ontology_dir: Path):
        """Load RDF elements from ontology files"""
        g = Graph()

        # Load all RDF files
        formats = {
            '.jsonld': 'json-ld',
            '.ttl': 'turtle',
            '.rdf': 'xml',
        }

        for ext, fmt in formats.items():
            for rdf_file in ontology_dir.glob(f'*{ext}'):
                try:
                    g.parse(rdf_file, format=fmt)
                except Exception:
                    pass  # Skip files that don't parse

        # Extract elements by type
        for s in g.subjects(RDF.type, None):
            elem_type = str(s).split('/')[-1]  # Simple extraction

            # Get properties
            elem_data = {
                'uri': str(s),
                'type': elem_type,
                'identifier': None,
                'title': None,
            }

            # Get identifier
            identifiers = list(g.objects(s, URIRef('http://purl.org/dc/terms/identifier')))
            if identifiers:
                elem_data['identifier'] = str(identifiers[0])

            # Get title
            titles = list(g.objects(s, URIRef('http://purl.org/dc/terms/title')))
            if titles:
                elem_data['title'] = str(titles[0])

            if elem_data['identifier']:
                self.rdf_elements[elem_data['identifier']] = elem_data

        print(f"Loaded {len(self.rdf_elements)} RDF elements with identifiers")

    def validate_tex_to_rdf(self):
        """Validate that every TeX element has a corresponding RDF resource"""
        print("\nValidating TeX → RDF consistency...")

        missing_rdf = set()
        for elem in self.tex_elements:
            if elem.id not in self.rdf_elements:
                missing_rdf.add((elem.id, elem.file, elem.line_number))

        if missing_rdf:
            for elem_id, file, line in sorted(missing_rdf, key=lambda x: (x[1], x[2])):
                self.errors.append(ValidationError(
                    file=file,
                    line=line,
                    message=f"TeX element '{elem_id}' has no corresponding RDF resource",
                    severity="ERROR"
                ))
        else:
            print("  ✓ All TeX elements have corresponding RDF resources")

    def validate_rdf_to_tex(self):
        """Validate that every RDF resource is referenced in TeX"""
        print("\nValidating RDF → TeX consistency...")

        # Note: Some RDF resources might be supplementary (not in TeX)
        # For now, just warn about RDF elements not found in TeX
        tex_ids = {elem.id for elem in self.tex_elements}

        missing_tex = set()
        for rdf_id in self.rdf_elements:
            # Skip citation resources
            if not any([rdf_id.startswith(prefix) for prefix in ['girard', 'kripke', 'sambin', 'urbas', 'trafford', 'lawvere', 'mac', 'lambek', 'restall', 'pierce', 'curyhoward', 'howard', 'negri']]):
                if rdf_id not in tex_ids:
                    missing_tex.add(rdf_id)

        if missing_tex:
            for rdf_id in sorted(missing_tex):
                self.errors.append(ValidationError(
                    file="ontology/",
                    line=0,
                    message=f"RDF resource '{rdf_id}' not referenced in TeX (may be supplementary)",
                    severity="WARNING"
                ))
        else:
            print("  ✓ All RDF resources are referenced in TeX")

    def validate_citation_consistency(self):
        """Validate that all citations are consistent across TeX and RDF"""
        print("\nValidating citation consistency...")

        for elem in self.tex_elements:
            if elem.citations:
                for cite_key in elem.citations:
                    if cite_key not in self.citations:
                        self.errors.append(ValidationError(
                            file=elem.file,
                            line=elem.line_number,
                            message=f"Citation '{cite_key}' not found in bibliography/citations.yaml",
                            severity="ERROR"
                        ))

        if not any(err for err in self.errors if 'not found in bibliography' in err.message):
            print("  ✓ All citations exist in registry")

    def validate_properties(self):
        """Validate that properties match between TeX and RDF"""
        print("\nValidating property consistency...")

        for elem in self.tex_elements:
            if elem.id in self.rdf_elements:
                rdf_elem = self.rdf_elements[elem.id]

                # Check titles match (approximately)
                if elem.title and rdf_elem['title']:
                    # Normalize for comparison
                    tex_title_norm = elem.title.lower().strip()
                    rdf_title_norm = rdf_elem['title'].lower().strip()

                    if tex_title_norm != rdf_title_norm:
                        self.errors.append(ValidationError(
                            file=elem.file,
                            line=elem.line_number,
                            message=f"Title mismatch for '{elem.id}': TeX='{elem.title}', RDF='{rdf_elem['title']}'",
                            severity="WARNING"
                        ))

        print("  ✓ Property validation complete")

    def validate_id_patterns(self):
        """Validate ID patterns match mapping specification"""
        print("\nValidating ID patterns...")

        for elem in self.tex_elements:
            if elem.element_type in self.mapping:
                mapping = self.mapping[elem.element_type]
                if 'id_pattern' in mapping:
                    pattern = mapping['id_pattern']
                    if not re.match(pattern, elem.id):
                        self.errors.append(ValidationError(
                            file=elem.file,
                            line=elem.line_number,
                            message=f"Invalid ID pattern for '{elem.id}': expected {pattern}",
                            severity="ERROR"
                        ))

        print("  ✓ ID pattern validation complete")

    def validate(self, tex_dir: Path, ontology_dir: Path,
                registry_file: Path, mapping_file: Path) -> bool:
        """Run full consistency validation"""
        print("=" * 60)
        print("Bidirectional Consistency Validator for Catty Thesis")
        print("=" * 60)

        # Load data
        self.load_citations(registry_file)
        self.parse_tex_files(tex_dir)
        self.load_rdf_elements(ontology_dir)

        # Run validations
        self.validate_id_patterns()
        self.validate_tex_to_rdf()
        self.validate_rdf_to_tex()
        self.validate_citation_consistency()
        self.validate_properties()

        # Count errors vs warnings
        errors = [e for e in self.errors if e.severity == "ERROR"]
        warnings = [e for e in self.errors if e.severity == "WARNING"]

        print(f"\nValidation complete: {len(errors)} errors, {len(warnings)} warnings")

        # Consider only errors as failures
        return len(errors) == 0

    def print_errors(self):
        """Print all validation errors"""
        if not self.errors:
            print("\n✓ No consistency errors found")
            return

        errors = [e for e in self.errors if e.severity == "ERROR"]
        warnings = [e for e in self.errors if e.severity == "WARNING"]

        if errors:
            print(f"\n✗ Found {len(errors)} consistency errors:")
            for error in errors:
                print(f"\n  ERROR: {error.file}:{error.line}")
                print(f"    {error.message}")

        if warnings:
            print(f"\n⚠ Found {len(warnings)} consistency warnings:")
            for warning in warnings:
                print(f"\n  WARNING: {warning.file}:{warning.line}")
                print(f"    {warning.message}")


def main():
    parser = argparse.ArgumentParser(
        description='Validate bidirectional consistency between TeX, RDF, and citations'
    )
    parser.add_argument(
        '--tex-dir',
        type=Path,
        default=Path('thesis/chapters'),
        help='Directory containing TeX files'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        default=Path('ontology'),
        help='Directory containing RDF ontology files'
    )
    parser.add_argument(
        '--bibliography',
        type=Path,
        default=Path('bibliography/citations.yaml'),
        help='Path to citation registry YAML file'
    )
    parser.add_argument(
        '--mapping',
        type=Path,
        default=Path('schema/tex-rdf-mapping.yaml'),
        help='Path to TeX-RDF mapping file'
    )

    args = parser.parse_args()

    # Check if files/directories exist
    if not args.tex_dir.exists():
        print(f"ERROR: Directory not found: {args.tex_dir}")
        sys.exit(1)

    if not args.ontology.exists():
        print(f"ERROR: Directory not found: {args.ontology}")
        sys.exit(1)

    if not args.bibliography.exists():
        print(f"ERROR: File not found: {args.bibliography}")
        sys.exit(1)

    if not args.mapping.exists():
        print(f"ERROR: File not found: {args.mapping}")
        sys.exit(1)

    # Create validator
    validator = ConsistencyValidator(args.mapping)

    # Validate
    success = validator.validate(args.tex_dir, args.ontology, args.bibliography, args.mapping)

    # Print results
    validator.print_errors()

    # Exit with appropriate code
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
