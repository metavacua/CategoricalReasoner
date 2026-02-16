#!/usr/bin/env python3
"""
Bidirectional Consistency Validator for Catty Thesis
Validates TeX ↔ RDF ↔ Citation consistency

EXIT CODES:
  0 = Validation PASSED (no violations)
  1 = Validation FAILED (violations present)

TEMPORARILY DISABLED - February 2026

This validator is disabled because the citations.yaml citation registry system
has been eliminated from the CategoricalReasoner repository. The YAML-based
citation system was non-functional and did not meet project requirements.

This script requires a Java/RO-Crate citation system for proper implementation.
See docs/dissertation/bibliography/README.md for implementation requirements.

Required missing components:
- Maven/pom.xml build configuration
- Java source files in src/main/java/org/metavacua/categoricalreasoner/citation/
- RO-Crate 1.1 JSON-LD generation
- BibLaTeX export functionality
- Javadoc annotations for Citation, Person, and related records

When the Java/RO-Crate system is implemented, this validator should be
rewritten to validate against that system instead of citations.yaml.
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
    sys.exit(2)

try:
    from rdflib import Graph, URIRef, Literal
    from rdflib.namespace import RDF, RDFS, DC
except ImportError:
    print("ERROR: rdflib is required. Install with: pip install rdflib")
    sys.exit(2)


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
class ValidationResult:
    """Represents a validation violation"""
    severity: str  # FATAL, ERROR, WARNING
    file: str
    line: int
    message: str
    constraint: str = None


@dataclass 
class ValidationSummary:
    """Summary of validation results"""
    total_violations: int = 0
    fatal_errors: int = 0
    violations: int = 0
    warnings: int = 0
    passed: bool = True


class ConsistencyValidator:
    """Validates bidirectional consistency between TeX, RDF, and citations"""

    def __init__(self, mapping_file: Path):
        self.results: List[ValidationResult] = []
        self.tex_elements: List[TeXElement] = []
        self.rdf_elements: Dict[str, Dict] = {}
        self.citations: Set[str] = set()
        self.mapping = {}
        self.summary = ValidationSummary()

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

            print(f"✓ Loaded mapping for {len(self.mapping)} element types")
        except Exception as e:
            self.results.append(ValidationResult(
                severity="FATAL",
                file=str(mapping_file),
                line=0,
                message=f"Error loading mapping: {e}",
                constraint="Mapping Loading"
            ))

    def load_citations(self, registry_file: Path):
        """Load citation registry"""
        try:
            with open(registry_file, 'r') as f:
                data = yaml.safe_load(f)

            self.citations = set(data['citations'].keys())
            print(f"✓ Loaded {len(self.citations)} citations from registry")
        except Exception as e:
            self.results.append(ValidationResult(
                severity="FATAL",
                file=str(registry_file),
                line=0,
                message=f"Error loading citation registry: {e}",
                constraint="Citation Loading"
            ))

    def parse_tex_files(self, tex_dir: Path):
        """Parse all TeX files and extract structured elements"""
        tex_files = list(tex_dir.glob('*.tex'))
        
        if not tex_files:
            self.results.append(ValidationResult(
                severity="FATAL",
                file=str(tex_dir),
                line=0,
                message="No TeX files found in directory",
                constraint="TeX Loading"
            ))
            return

        for tex_file in tex_files:
            try:
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

                    # Parse sections: \section{title}
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

            except Exception as e:
                self.results.append(ValidationResult(
                    severity="FATAL",
                    file=str(tex_file),
                    line=0,
                    message=f"Error parsing TeX file: {e}",
                    constraint="TeX Parsing"
                ))

        print(f"✓ Parsed {len(self.tex_elements)} TeX elements from {len(tex_files)} files")

    def load_rdf_elements(self, ontology_dir: Path):
        """Load RDF elements from ontology files"""
        g = Graph()

        # Load all RDF files
        formats = {
            '.jsonld': 'json-ld',
            '.ttl': 'turtle',
            '.rdf': 'xml',
        }

        loaded_count = 0
        for ext, fmt in formats.items():
            for rdf_file in ontology_dir.glob(f'*{ext}'):
                try:
                    g.parse(rdf_file, format=fmt)
                    loaded_count += 1
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

        print(f"✓ Loaded {len(self.rdf_elements)} RDF elements with identifiers")

    def validate_id_patterns(self):
        """Validate ID patterns match mapping specification"""
        violations = 0
        
        for elem in self.tex_elements:
            if elem.element_type in self.mapping:
                mapping = self.mapping[elem.element_type]
                if 'id_pattern' in mapping:
                    pattern = mapping['id_pattern']
                    if not re.match(pattern, elem.id):
                        self.results.append(ValidationResult(
                            severity="VIOLATION",
                            file=elem.file,
                            line=elem.line_number,
                            message=f"Invalid ID pattern for '{elem.id}': expected {pattern}",
                            constraint="ID Pattern"
                        ))
                        violations += 1
        
        if violations == 0:
            print("✓ ID pattern validation passed")
        else:
            print(f"❌ ID pattern validation failed: {violations} violations")

    def validate_tex_to_rdf(self):
        """Validate that every TeX element has a corresponding RDF resource"""
        violations = 0
        
        for elem in self.tex_elements:
            # Skip sections - they are structural elements and don't require RDF
            if elem.element_type == 'section':
                continue
            if elem.id not in self.rdf_elements:
                self.results.append(ValidationResult(
                    severity="VIOLATION",
                    file=elem.file,
                    line=elem.line_number,
                    message=f"TeX element '{elem.id}' has no corresponding RDF resource",
                    constraint="TeX→RDF Mapping"
                ))
                violations += 1
        
        if violations == 0:
            print("✓ TeX→RDF consistency validation passed")
        else:
            print(f"❌ TeX→RDF consistency validation failed: {violations} violations")

    def validate_citation_consistency(self):
        """Validate that all citations are consistent across TeX and RDF"""
        violations = 0
        
        for elem in self.tex_elements:
            if elem.citations:
                for cite_key in elem.citations:
                    if cite_key not in self.citations:
                        self.results.append(ValidationResult(
                            severity="VIOLATION",
                            file=elem.file,
                            line=elem.line_number,
                            message=f"Citation '{cite_key}' not found in docs/dissertation/bibliography/citations.yaml",
                            constraint="Citation Registry"
                        ))
                        violations += 1
        
        if violations == 0:
            print("✓ Citation consistency validation passed")
        else:
            print(f"❌ Citation consistency validation failed: {violations} violations")

    def validate_properties(self):
        """Validate that properties match between TeX and RDF"""
        violations = 0
        
        for elem in self.tex_elements:
            if elem.id in self.rdf_elements:
                rdf_elem = self.rdf_elements[elem.id]

                # Check titles match (approximately)
                if elem.title and rdf_elem['title']:
                    # Normalize for comparison
                    tex_title_norm = elem.title.lower().strip()
                    rdf_title_norm = rdf_elem['title'].lower().strip()

                    if tex_title_norm != rdf_title_norm:
                        self.results.append(ValidationResult(
                            severity="WARNING",
                            file=elem.file,
                            line=elem.line_number,
                            message=f"Title mismatch for '{elem.id}': TeX='{elem.title}', RDF='{rdf_elem['title']}'",
                            constraint="Property Matching"
                        ))
                        violations += 1
        
        if violations == 0:
            print("✓ Property consistency validation passed")
        else:
            print(f"⚠️  Property consistency validation: {violations} warnings")

    def validate(self, tex_dir: Path, ontology_dir: Path,
                registry_file: Path, mapping_file: Path) -> bool:
        """Run full consistency validation"""
        print("=" * 60)
        print("Bidirectional Consistency Validator for Catty Thesis")
        print("=" * 60)
        print("Status: RUNNING")
        print("")

        # Load data
        print(f"📖 Loading citation registry...")
        self.load_citations(registry_file)
        print("")

        print(f"📝 Parsing TeX files...")
        self.parse_tex_files(tex_dir)
        print("")

        print(f"🔗 Loading RDF elements...")
        self.load_rdf_elements(ontology_dir)
        print("")

        # Check for fatal errors
        fatal_errors = [r for r in self.results if r.severity == "FATAL"]
        if fatal_errors:
            print("❌ FATAL ERRORS:")
            for error in fatal_errors:
                print(f"  • {error.file}:{error.line} - {error.message}")
            return False

        # Run validations
        print("🔍 Running validations...")
        print("")
        
        self.validate_id_patterns()
        print("")
        
        self.validate_tex_to_rdf()
        print("")
        
        self.validate_citation_consistency()
        print("")
        
        self.validate_properties()
        print("")

        # Count results
        self.summary.total_violations = len(self.results)
        self.summary.violations = len([v for v in self.results if v.severity == "VIOLATION"])
        self.summary.warnings = len([v for v in self.results if v.severity == "WARNING"])
        self.summary.passed = self.summary.violations == 0

        return self.summary.passed

    def print_results(self):
        """Print validation results"""
        print("=" * 60)
        print("VALIDATION RESULTS")
        print("=" * 60)
        
        # Print summary
        if self.summary.passed:
            print("✅ VALIDATION PASSED")
            print(f"   • Total violations: {self.summary.total_violations}")
            print(f"   • Status: All bidirectional consistency checks passed")
        else:
            print("❌ VALIDATION FAILED")
            print(f"   • Total violations: {self.summary.total_violations}")
            print(f"   • Violations: {self.summary.violations}")
            print(f"   • Warnings: {self.summary.warnings}")
        
        print("")
        
        # Print violations if any
        if not self.summary.passed and self.results:
            violations = [r for r in self.results if r.severity == "VIOLATION"]
            warnings = [r for r in self.results if r.severity == "WARNING"]
            
            if violations:
                print("🚨 VIOLATIONS:")
                print("-" * 40)
                for i, violation in enumerate(violations, 1):
                    print(f"  {i:2d}. {violation.file}:{violation.line}")
                    print(f"      Constraint: {violation.constraint}")
                    print(f"      Message: {violation.message}")
                    print("")
            
            if warnings:
                print("⚠️  WARNINGS:")
                print("-" * 20)
                for i, warning in enumerate(warnings[:5], 1):  # Show first 5
                    print(f"  {i:2d}. {warning.file}:{warning.line}")
                    print(f"      Message: {warning.message}")
                    print("")
                
                if len(warnings) > 5:
                    print(f"      ... and {len(warnings) - 5} more warnings")
        
        # Print fatal errors if any
        fatal_errors = [r for r in self.results if r.severity == "FATAL"]
        if fatal_errors:
            print("💥 FATAL ERRORS:")
            print("-" * 20)
            for error in fatal_errors:
                print(f"  • {error.file}:{error.line} - {error.message}")
            print("")
        
        print("=" * 60)


def main():
    # Early exit - this validator is disabled
    print("=" * 60)
    print("Consistency Validator - TEMPORARILY DISABLED")
    print("=" * 60)
    print("\nThis validator is disabled because the citations.yaml citation")
    print("registry system has been eliminated from the CategoricalReasoner")
    print("repository.")
    print("\nThe citation system requires a Java/RO-Crate implementation.")
    print("See docs/dissertation/bibliography/README.md for requirements.")
    print("\nExiting with success status (0).")
    sys.exit(0)

    # Original code below (commented out for future reference)
    """
    parser = argparse.ArgumentParser(
        description='Validate bidirectional consistency between TeX, RDF, and citations',
        epilog='EXIT CODES: 0 = PASS, 1 = FAIL, 2 = ERROR'
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
        default=Path('docs/dissertation/bibliography/citations.yaml'),
        help='Path to citation registry YAML file'
    )
    parser.add_argument(
        '--mapping',
        type=Path,
        default=Path('src/schema/tex-rdf-mapping.yaml'),
        help='Path to TeX-RDF mapping file'
    )

    args = parser.parse_args()

    # Check if files/directories exist
    if not args.tex_dir.exists():
        print(f"ERROR: Directory not found: {args.tex_dir}")
        sys.exit(2)

    if not args.ontology.exists():
        print(f"ERROR: Directory not found: {args.ontology}")
        sys.exit(2)

    if not args.bibliography.exists():
        print(f"ERROR: File not found: {args.bibliography}")
        sys.exit(2)

    if not args.mapping.exists():
        print(f"ERROR: File not found: {args.mapping}")
        sys.exit(2)
    """

    # Create validator
    validator = ConsistencyValidator(args.mapping)

    # Validate
    success = validator.validate(args.tex_dir, args.ontology, args.bibliography, args.mapping)

    # Print results
    validator.print_results()

    # Exit with appropriate code
    if success:
        print("🎉 Validation completed successfully")
        sys.exit(0)
    else:
        print("💥 Validation failed - please fix violations")
        sys.exit(1)


if __name__ == '__main__':
    main()
