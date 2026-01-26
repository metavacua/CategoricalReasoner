#!/usr/bin/env python3
"""
TeX Structure Validator for Catty Thesis
Validates TeX files against thesis-structure.schema.yaml

Enhanced with security validation and input sanitization.
"""

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Set, Optional, Tuple
from dataclasses import dataclass
import json

# Import security validation utilities
sys.path.append(str(Path(__file__).parent.parent.parent / 'scripts'))
try:
    from validate_inputs import (
        validate_path, validate_identifier, validate_file_path,
        SecurityError
    )
except ImportError:
    # Fallback if security module not available
    def validate_path(path, allowed_base=None):
        return Path(path).resolve()
    
    def validate_identifier(identifier, pattern=None):
        return identifier
    
    def validate_file_path(file_path, allowed_extensions=None):
        return Path(file_path).resolve()
    
    class SecurityError(Exception):
        pass

# Try to import PyYAML; if not available, give a helpful error
try:
    import yaml
except ImportError:
    print("ERROR: PyYAML is required. Install with: pip install pyyaml")
    sys.exit(1)


@dataclass
class TeXElement:
    """Represents a parsed TeX element"""
    element_type: str
    id: str
    title: Optional[str] = None
    line_number: int = 0
    file: str = ""
    content: str = ""


@dataclass
class ValidationError:
    """Represents a validation error"""
    file: str
    line: int
    message: str
    severity: str = "ERROR"


class TeXStructureValidator:
    """Validates TeX structure against schema"""

    def __init__(self, schema_file: str = None):
        self.schema = None
        self.errors: List[ValidationError] = []
        self.ids: Set[str] = set()
        self.elements: List[TeXElement] = []

        if schema_file:
            self.load_schema(schema_file)

    def load_schema(self, schema_file: str):
        """Load the thesis structure schema"""
        try:
            with open(schema_file, 'r') as f:
                self.schema = yaml.safe_load(f)
            print(f"Loaded schema from {schema_file}")
        except FileNotFoundError:
            self.errors.append(ValidationError(
                file=schema_file, line=0,
                message=f"Schema file not found: {schema_file}",
                severity="FATAL"
            ))
            sys.exit(1)
        except yaml.YAMLError as e:
            self.errors.append(ValidationError(
                file=schema_file, line=0,
                message=f"Invalid YAML in schema: {e}",
                severity="FATAL"
            ))
            sys.exit(1)

    def parse_tex_file(self, tex_file: Path) -> List[TeXElement]:
        """Parse a TeX file and extract structured elements"""
        elements = []

        with open(tex_file, 'r', encoding='utf-8') as f:
            lines = f.readlines()

        for i, line in enumerate(lines, start=1):
            # Parse theorems: \begin{theorem}[id]{title}
            theorem_match = re.search(r'\\begin\{theorem\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
            if theorem_match:
                elem_id = theorem_match.group(1).strip()
                title = theorem_match.group(2).strip()
                elements.append(TeXElement(
                    element_type='theorem',
                    id=elem_id,
                    title=title,
                    line_number=i,
                    file=str(tex_file),
                    content=line.strip()
                ))
                continue

            # Parse definitions: \begin{definition}[id]{term}
            def_match = re.search(r'\\begin\{definition\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
            if def_match:
                elem_id = def_match.group(1).strip()
                title = def_match.group(2).strip()
                elements.append(TeXElement(
                    element_type='definition',
                    id=elem_id,
                    title=title,
                    line_number=i,
                    file=str(tex_file),
                    content=line.strip()
                ))
                continue

            # Parse lemmas: \begin{lemma}[id]{title}
            lemma_match = re.search(r'\\begin\{lemma\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
            if lemma_match:
                elem_id = lemma_match.group(1).strip()
                title = lemma_match.group(2).strip()
                elements.append(TeXElement(
                    element_type='lemma',
                    id=elem_id,
                    title=title,
                    line_number=i,
                    file=str(tex_file),
                    content=line.strip()
                ))
                continue

            # Parse examples: \begin{example}[id]{title}
            ex_match = re.search(r'\\begin\{example\}\s*\[([^\]]+)\]\s*\{([^}]+)\}', line)
            if ex_match:
                elem_id = ex_match.group(1).strip()
                title = ex_match.group(2).strip()
                elements.append(TeXElement(
                    element_type='example',
                    id=elem_id,
                    title=title,
                    line_number=i,
                    file=str(tex_file),
                    content=line.strip()
                ))
                continue

            # Parse sections: \section{id}{title} or \section{title}
            # Note: We'll use a simpler pattern for now
            section_match = re.search(r'\\section\*?\s*\{([^}]+)\}', line)
            if section_match:
                # Generate ID from title (sanitize special characters)
                title = section_match.group(1).strip()
                # Remove special characters, keep only alphanumeric, hyphens, and spaces
                clean_title = re.sub(r'[^a-zA-Z0-9\s-]', '', title)
                elem_id = f"sec-{clean_title.lower().replace(' ', '-')}"
                elements.append(TeXElement(
                    element_type='section',
                    id=elem_id,
                    title=title,
                    line_number=i,
                    file=str(tex_file),
                    content=line.strip()
                ))
                continue

        return elements

    def validate_id_pattern(self, element: TeXElement) -> bool:
        """Validate ID pattern matches schema"""
        patterns = {
            'theorem': r'^thm-[a-z0-9-]+$',
            'definition': r'^def-[a-z0-9-]+$',
            'lemma': r'^lem-[a-z0-9-]+$',
            'example': r'^ex-[a-z0-9-]+$',
            'section': r'^sec-[a-z0-9-]+$',
            'subsection': r'^subsec-[a-z0-9-]+$',
        }

        if element.element_type in patterns:
            pattern = patterns[element.element_type]
            if not re.match(pattern, element.id):
                self.errors.append(ValidationError(
                    file=element.file,
                    line=element.line_number,
                    message=f"Invalid {element.element_type} ID '{element.id}': must match pattern {pattern}",
                    severity="ERROR"
                ))
                return False
        return True

    def validate_id_uniqueness(self, elements: List[TeXElement]):
        """Validate all IDs are globally unique"""
        id_map: Dict[str, TeXElement] = {}

        for elem in elements:
            if elem.id in id_map:
                existing = id_map[elem.id]
                self.errors.append(ValidationError(
                    file=elem.file,
                    line=elem.line_number,
                    message=f"Duplicate ID '{elem.id}' (first defined at {existing.file}:{existing.line_number})",
                    severity="ERROR"
                ))
            else:
                id_map[elem.id] = elem

    def validate_element(self, element: TeXElement):
        """Validate a single TeX element"""
        # Validate ID pattern
        self.validate_id_pattern(element)

        # Validate required fields based on type
        if element.element_type == 'theorem':
            if not element.title:
                self.errors.append(ValidationError(
                    file=element.file,
                    line=element.line_number,
                    message=f"Theorem {element.id} missing title",
                    severity="ERROR"
                ))
        elif element.element_type == 'definition':
            if not element.title:
                self.errors.append(ValidationError(
                    file=element.file,
                    line=element.line_number,
                    message=f"Definition {element.id} missing term",
                    severity="ERROR"
                ))
        elif element.element_type in ['lemma', 'example', 'section']:
            if not element.title:
                self.errors.append(ValidationError(
                    file=element.file,
                    line=element.line_number,
                    message=f"{element.element_type.capitalize()} {element.id} missing title",
                    severity="ERROR"
                ))

    def validate_directory(self, tex_dir: Path) -> bool:
        """Validate all TeX files in a directory"""
        tex_files = list(tex_dir.glob('*.tex'))

        if not tex_files:
            print(f"WARNING: No .tex files found in {tex_dir}")
            return True

        print(f"Found {len(tex_files)} TeX files to validate")

        for tex_file in tex_files:
            print(f"\nValidating {tex_file}...")
            elements = self.parse_tex_file(tex_file)
            print(f"  Found {len(elements)} structured elements")

            for elem in elements:
                self.validate_element(elem)

            self.elements.extend(elements)

        # Validate uniqueness across all files
        print("\nValidating ID uniqueness...")
        self.validate_id_uniqueness(self.elements)

        return len(self.errors) == 0

    def print_errors(self):
        """Print all validation errors"""
        if not self.errors:
            print("\n✓ No validation errors found")
            return

        print(f"\n✗ Found {len(self.errors)} validation errors:")
        for error in self.errors:
            print(f"\n  {error.severity}: {error.file}:{error.line}")
            print(f"    {error.message}")

        # Group errors by file
        by_file = {}
        for error in self.errors:
            if error.file not in by_file:
                by_file[error.file] = []
            by_file[error.file].append(error)

        print(f"\nErrors by file:")
        for file, file_errors in by_file.items():
            print(f"  {file}: {len(file_errors)} error(s)")


def main():
    parser = argparse.ArgumentParser(
        description='Validate TeX structure against schema'
    )
    parser.add_argument(
        '--tex-dir',
        type=str,
        default='thesis/chapters',
        help='Directory containing TeX files to validate'
    )
    parser.add_argument(
        '--schema',
        type=str,
        default='schema/thesis-structure.schema.yaml',
        help='Path to thesis structure schema'
    )

    args = parser.parse_args()

    try:
        # Security validation: Validate input paths
        current_dir = Path.cwd()
        
        # Validate and resolve the tex directory path
        try:
            validated_tex_dir = validate_path(args.tex_dir, allowed_base=current_dir)
            validated_schema_path = validate_path(args.schema, allowed_base=current_dir)
        except SecurityError as e:
            print(f"SECURITY ERROR: Path validation failed - {e}")
            sys.exit(2)
        
        # Check if directory exists using validated path
        if not validated_tex_dir.exists():
            print(f"ERROR: Directory not found: {validated_tex_dir}")
            sys.exit(1)

        # Check if schema exists using validated path
        if not validated_schema_path.exists():
            print(f"WARNING: Schema file not found: {validated_schema_path}")
            print("Proceeding with basic validation only...")
            schema_path = None
        else:
            schema_path = validated_schema_path

        # Create validator with validated paths
        validator = TeXStructureValidator(schema_path)

        # Validate using validated path
        success = validator.validate_directory(validated_tex_dir)

        # Print results
        validator.print_errors()

        # Exit with appropriate code
        sys.exit(0 if success else 1)
        
    except SecurityError as e:
        print(f"SECURITY ERROR: {e}")
        sys.exit(2)
    except Exception as e:
        print(f"UNEXPECTED ERROR: {e}")
        sys.exit(3)


if __name__ == '__main__':
    main()
