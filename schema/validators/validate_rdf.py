#!/usr/bin/env python3
"""
RDF/SHACL Validator for Catty Thesis
Validates RDF graphs against SHACL shapes

EXIT CODES:
  0 = Validation PASSED (no violations)
  1 = Validation FAILED (violations present)
"""

import argparse
import logging
import sys
from pathlib import Path
from typing import List, Tuple, Dict, Optional
from dataclasses import dataclass
import importlib.util

# Setup logging
logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
logger = logging.getLogger(__name__)

# Try to import required libraries
try:
    from rdflib import Graph, URIRef, Literal
    from pyshacl import validate
    import rdflib
except ImportError:
    logger.error("rdflib and pyshacl are required. Install with: pip install rdflib pyshacl")
    sys.exit(2)


def _load_iri_config_class(repo_root: Path):
    """Load IRIConfig from scripts/iri-config.py via importlib."""
    iri_config_path = repo_root / "scripts" / "iri-config.py"
    if not iri_config_path.exists():
        raise FileNotFoundError(f"Expected IRIConfig module not found: {iri_config_path}")

    spec = importlib.util.spec_from_file_location("catty_iri_config", iri_config_path)
    if spec is None or spec.loader is None:
        raise ImportError(f"Unable to load module from {iri_config_path}")

    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module.IRIConfig


@dataclass
class ValidationResult:
    """Represents a validation violation"""
    severity: str  # FATAL, ERROR, WARNING
    focus_node: str
    constraint: str
    message: str
    path: str = None


@dataclass 
class ValidationSummary:
    """Summary of validation results"""
    total_violations: int = 0
    fatal_errors: int = 0
    violations: int = 0
    warnings: int = 0
    conforms: bool = True


class RDFValidator:
    """Validates RDF against SHACL shapes"""

    def __init__(self):
        self.results: List[ValidationResult] = []
        self.summary = ValidationSummary()

    def load_ontology(self, ontology_dir: Path) -> Graph:
        """Load all RDF files from ontology directory.

        Note: JSON-LD files in this repo use a remote context URL (localhost or
        GitHub Pages). For deterministic offline validation we intercept remote
        context fetching and serve the context from `ontology/context.jsonld`.
        """
        g = Graph()

        repo_root = ontology_dir.parent
        IRIConfig = _load_iri_config_class(repo_root)
        config = IRIConfig(config_path=str(repo_root / ".catty" / "iri-config.yaml"))

        # Supported formats
        formats = {
            '.jsonld': 'json-ld',
            '.ttl': 'turtle', 
            '.rdf': 'xml',
            '.n3': 'n3',
            '.nt': 'nt',
        }

        loaded_count = 0
        # Load all RDF files
        with config.offline_context():
            for ext, fmt in formats.items():
                for rdf_file in ontology_dir.glob(f'*{ext}'):
                    try:
                        g.parse(rdf_file, format=fmt)
                        logger.info(f"✓ Loaded {rdf_file.name} ({fmt})")
                        loaded_count += 1
                    except Exception as e:
                        self.results.append(ValidationResult(
                            severity="FATAL",
                            focus_node=str(rdf_file),
                            constraint="RDF Parsing",
                            message=f"Error parsing RDF: {e}"
                        ))

        if loaded_count == 0:
            self.results.append(ValidationResult(
                severity="FATAL",
                focus_node=str(ontology_dir),
                constraint="RDF Loading",
                message="No RDF files could be loaded from directory"
            ))

        logger.info(f"📊 Total triples loaded: {len(g)}")
        return g

    def load_shapes(self, shapes_file: Path) -> Graph:
        """Load SHACL shapes"""
        try:
            shapes_g = Graph()
            # Determine format
            if shapes_file.suffix == '.ttl':
                fmt = 'turtle'
            elif shapes_file.suffix == '.jsonld':
                fmt = 'json-ld'
            elif shapes_file.suffix == '.rdf':
                fmt = 'xml'
            else:
                fmt = 'turtle'  # Default

            shapes_g.parse(shapes_file, format=fmt)
            if len(shapes_g) == 0:
                self.results.append(ValidationResult(
                    severity="FATAL",
                    focus_node=str(shapes_file),
                    constraint="SHACL Loading", 
                    message="SHACL shapes file is empty"
                ))
                return None

            logger.info(f"✓ Loaded {len(shapes_g)} shape triples from {shapes_file.name}")
            return shapes_g
        except Exception as e:
            self.results.append(ValidationResult(
                severity="FATAL",
                focus_node=str(shapes_file),
                constraint="SHACL Parsing",
                message=f"Error parsing SHACL shapes: {e}"
            ))
            return None

    def parse_shacl_results(self, results_graph: Graph) -> List[ValidationResult]:
        """Parse SHACL validation results into structured format"""
        violations = []
        
        # SHACL violation types
        SHACL_violation = URIRef("http://www.w3.org/ns/shacl#Violation")
        SHACL_warning = URIRef("http://www.w3.org/ns/shacl#Warning")
        
        # Extract violation details
        for result in results_graph.subjects():
            severity = "VIOLATION"  # Default
            focus_node = ""
            constraint = ""
            message = ""
            path = ""
            
            # Get severity
            for severity_type in results_graph.objects(result, URIRef("http://www.w3.org/ns/shacl#resultSeverity")):
                if severity_type == SHACL_violation:
                    severity = "VIOLATION"
                elif severity_type == SHACL_warning:
                    severity = "WARNING"
            
            # Get focus node
            for focus in results_graph.objects(result, URIRef("http://www.w3.org/ns/shacl#focusNode")):
                focus_node = str(focus)
            
            # Get constraint
            for constraint_comp in results_graph.objects(result, URIRef("http://www.w3.org/ns/shacl#sourceConstraintComponent")):
                constraint = str(constraint_comp).split("/")[-1]  # Get just the local name
            
            # Get message
            for msg in results_graph.objects(result, URIRef("http://www.w3.org/ns/shacl#resultMessage")):
                message = str(msg)
            
            # Get path
            for path_prop in results_graph.objects(result, URIRef("http://www.w3.org/ns/shacl#resultPath")):
                path = str(path_prop).split("/")[-1]  # Get just the local name
            
            violations.append(ValidationResult(
                severity=severity,
                focus_node=focus_node,
                constraint=constraint,
                message=message,
                path=path
            ))
        
        return violations

    def validate(self, ontology_dir: Path, shapes_file: Path) -> bool:
        """Validate RDF against SHACL shapes"""
        logger.info("Starting RDF/SHACL validation...")

        # Load ontology
        logger.info(f"📁 Loading RDF from {ontology_dir}...")
        data_graph = self.load_ontology(ontology_dir)

        # Check for fatal errors during loading
        fatal_errors = [r for r in self.results if r.severity == "FATAL"]
        if fatal_errors:
            logger.error("❌ FATAL ERRORS during RDF loading:")
            for error in fatal_errors:
                logger.error(f"  • {error.focus_node}: {error.message}")
            return False

        # Load shapes
        logger.info(f"📋 Loading SHACL shapes from {shapes_file}...")
        shapes_graph = self.load_shapes(shapes_file)

        if shapes_graph is None:
            logger.error("❌ FATAL: Failed to load SHACL shapes")
            return False

        # Validate
        logger.info("🔍 Running SHACL validation...")
        try:
            conforms, results_graph, results_text = validate(
                data_graph,
                shacl_graph=shapes_graph,
                ont_graph=None,
                inference='rdfs',
                abort_on_first=False,
                allow_infos=False,
                allow_warnings=False,  # Treat warnings as violations
                meta_shacl=False,
                debug=False
            )

            # Parse results
            violations = self.parse_shacl_results(results_graph)
            self.results.extend(violations)
            
            # Count results
            self.summary.conforms = conforms
            self.summary.total_violations = len(violations)
            self.summary.violations = len([v for v in violations if v.severity == "VIOLATION"])
            self.summary.warnings = len([v for v in violations if v.severity == "WARNING"])
            
            return conforms

        except Exception as e:
            self.results.append(ValidationResult(
                severity="FATAL",
                focus_node="validation",
                constraint="SHACL Execution",
                message=f"SHACL validation error: {e}"
            ))
            return False

    def print_results(self):
        """Print validation results"""
        # We use print here because this is the primary CLI report output.
        print("=" * 60)
        print("VALIDATION RESULTS")
        print("=" * 60)
        
        # Print summary
        if self.summary.conforms:
            print("✅ VALIDATION PASSED")
            print(f"   • Total violations: {self.summary.total_violations}")
            print(f"   • Status: RDF graph conforms to all SHACL shapes")
        else:
            print("❌ VALIDATION FAILED")
            print(f"   • Total violations: {self.summary.total_violations}")
            print(f"   • Violations: {self.summary.violations}")
            print(f"   • Warnings: {self.summary.warnings}")
        
        print("")
        
        # Print violations if any
        if not self.summary.conforms and self.results:
            print("VIOLATIONS:")
            print("-" * 40)
            
            # Group by severity
            violations = [r for r in self.results if r.severity == "VIOLATION"]
            warnings = [r for r in self.results if r.severity == "WARNING"]
            
            if violations:
                print(f"\n🚨 VIOLATIONS ({len(violations)}):")
                for i, violation in enumerate(violations[:10], 1):  # Show first 10
                    print(f"  {i:2d}. Focus: {violation.focus_node}")
                    print(f"      Constraint: {violation.constraint}")
                    print(f"      Path: {violation.path}")
                    print(f"      Message: {violation.message}")
                    print("")
                
                if len(violations) > 10:
                    print(f"      ... and {len(violations) - 10} more violations")
            
            if warnings:
                print(f"\n⚠️  WARNINGS ({len(warnings)}):")
                for i, warning in enumerate(warnings[:5], 1):  # Show first 5
                    print(f"  {i:2d}. Focus: {warning.focus_node}")
                    print(f"      Message: {warning.message}")
                    print("")
        
        # Print fatal errors if any
        fatal_errors = [r for r in self.results if r.severity == "FATAL"]
        if fatal_errors:
            print("💥 FATAL ERRORS:")
            print("-" * 20)
            for error in fatal_errors:
                print(f"  • {error.focus_node}: {error.message}")
            print("")
        
        print("=" * 60)


def main():
    parser = argparse.ArgumentParser(
        description='Validate RDF against SHACL shapes',
        epilog='EXIT CODES: 0 = PASS, 1 = FAIL, 2 = ERROR'
    )
    parser.add_argument(
        '--ontology',
        type=Path,
        default=Path('ontology'),
        help='Directory containing RDF ontology files'
    )
    parser.add_argument(
        '--shapes',
        type=Path,
        default=Path('ontology/catty-thesis-shapes.shacl'),
        help='Path to SHACL shapes file'
    )

    args = parser.parse_args()

    # Check if directory exists
    if not args.ontology.exists():
        print(f"ERROR: Directory not found: {args.ontology}")
        sys.exit(2)

    # Check if shapes file exists
    if not args.shapes.exists():
        print(f"ERROR: SHACL shapes file not found: {args.shapes}")
        sys.exit(2)

    # Create validator
    validator = RDFValidator()

    # Validate
    success = validator.validate(args.ontology, args.shapes)

    # Print results
    validator.print_results()

    # Exit with appropriate code
    if success:
        logger.info("🎉 Validation completed successfully")
        sys.exit(0)
    else:
        logger.error("💥 Validation failed - please fix violations")
        sys.exit(1)


if __name__ == '__main__':
    main()
