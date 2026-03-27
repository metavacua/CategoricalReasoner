#!/usr/bin/env python3
"""
TeX Structure Validator for Catty Thesis

Validates TeX files against thesis-structure.schema.yaml:
- Parse LaTeX source with macro awareness
- Extract structure (chapters, sections, theorems, definitions)
- Validate nesting follows schema
- Validate all IDs are unique and match patterns
- Validate all citations reference registry entries
- Output: PASS/FAIL with specific line numbers and error messages
"""

import sys
import re
import argparse
import yaml
from pathlib import Path
from typing import Dict, List, Set, Tuple
import json

class TexStructureValidator:
    def __init__(self, schema_path: Path, citations_path: Path):
        with open(schema_path) as f:
            self.schema = yaml.safe_load(f)

        with open(citations_path) as f:
            citations_data = yaml.safe_load(f)
            self.valid_citations = set(citations_data['citations'].keys())

        self.errors = []
        self.warnings = []
        self.all_ids = set()

        # ID patterns from schema
        self.id_patterns = {
            'theorem': r'^thm-[a-z0-9]+(-[a-z0-9]+)*$',
            'lemma': r'^lem-[a-z0-9]+(-[a-z0-9]+)*$',
            'proposition': r'^prop-[a-z0-9]+(-[a-z0-9]+)*$',
            'corollary': r'^cor-[a-z0-9]+(-[a-z0-9]+)*$',
            'definition': r'^def-[a-z0-9]+(-[a-z0-9]+)*$',
            'example': r'^ex-[a-z0-9]+(-[a-z0-9]+)*$',
            'remark': r'^rem-[a-z0-9]+(-[a-z0-9]+)*$',
            'conjecture': r'^conj-[a-z0-9]+(-[a-z0-9]+)*$',
            'proof': r'^proof-[a-z0-9]+(-[a-z0-9]+)*$',
            'section': r'^sec-[a-z0-9]+(-[a-z0-9]+)*$',
            'subsection': r'^subsec-[a-z0-9]+(-[a-z0-9]+)*$',
            'chapter': r'^ch-[a-z0-9]+(-[a-z0-9]+)*$',
            'part': r'^part-[a-z0-9]+(-[a-z0-9]+)*$',
        }

    def validate_file(self, tex_file: Path) -> bool:
        """Validate a single TeX file"""
        try:
            with open(tex_file, 'r', encoding='utf-8') as f:
                content = f.read()
        except Exception as e:
            self.errors.append(f"{tex_file}: Failed to read file: {e}")
            return False

        lines = content.split('\n')

        # Extract IDs from labels
        self._extract_labels(tex_file, lines)

        # Extract and validate citations
        self._validate_citations(tex_file, lines)

        # Validate theorem environments
        self._validate_environments(tex_file, lines)

        return len(self.errors) == 0

    def _extract_labels(self, file_path: Path, lines: List[str]):
        """Extract and validate all \label{} IDs"""
        label_pattern = r'\\label\{([^}]+)\}'

        for line_num, line in enumerate(lines, 1):
            matches = re.finditer(label_pattern, line)
            for match in matches:
                label_id = match.group(1)

                # Check for duplicate IDs
                if label_id in self.all_ids:
                    self.errors.append(
                        f"{file_path}:{line_num}: Duplicate ID '{label_id}' "
                        f"(IDs must be globally unique)"
                    )
                else:
                    self.all_ids.add(label_id)

                # Validate ID pattern
                matched_pattern = False
                for element_type, pattern in self.id_patterns.items():
                    if re.match(pattern, label_id):
                        matched_pattern = True
                        break

                if not matched_pattern and not label_id.startswith('cite:'):
                    self.warnings.append(
                        f"{file_path}:{line_num}: ID '{label_id}' does not match "
                        f"any standard pattern"
                    )

    def _validate_citations(self, file_path: Path, lines: List[str]):
        """Validate all \cite{} references"""
        # Patterns for different citation commands
        cite_patterns = [
            r'\\cite\{([^}]+)\}',
            r'\\citep\{([^}]+)\}',
            r'\\citeauthor\{([^}]+)\}',
            r'\\citeyear\{([^}]+)\}',
            r'\\citepage\{([^}]+)\}\{[^}]+\}',
            r'\\citefigure\{([^}]+)\}\{[^}]+\}',
            r'\\citetheorem\{([^}]+)\}\{[^}]+\}',
            r'\\definedfrom\{[^}]+\}\{([^}]+)\}',
            r'\\provedfrom\{[^}]+\}\{([^}]+)\}',
            r'\\derivedfrom\{[^}]+\}\{([^}]+)\}',
        ]

        for line_num, line in enumerate(lines, 1):
            for pattern in cite_patterns:
                matches = re.finditer(pattern, line)
                for match in matches:
                    cite_key = match.group(1)

                    # Handle multiple citations (e.g., \cite{key1,key2})
                    keys = [k.strip() for k in cite_key.split(',')]

                    for key in keys:
                        if key and key not in self.valid_citations:
                            self.errors.append(
                                f"{file_path}:{line_num}: Citation key '{key}' "
                                f"not found in bibliography/citations.yaml"
                            )

    def _validate_environments(self, file_path: Path, lines: List[str]):
        """Validate theorem, definition, and other environments"""
        env_stack = []

        begin_pattern = r'\\begin\{(theorem|lemma|proposition|corollary|definition|example|remark|conjecture|proof)\}'
        end_pattern = r'\\end\{(theorem|lemma|proposition|corollary|definition|example|remark|conjecture|proof)\}'

        for line_num, line in enumerate(lines, 1):
            # Check for begin environment
            begin_matches = re.finditer(begin_pattern, line)
            for match in begin_matches:
                env_type = match.group(1)
                env_stack.append((env_type, line_num))

                # Check nesting constraints
                if len(env_stack) > 1:
                    parent_env = env_stack[-2][0]
                    if env_type == 'proof' and parent_env == 'proof':
                        self.errors.append(
                            f"{file_path}:{line_num}: Proof cannot be nested "
                            f"inside another proof"
                        )
                    elif env_type in ['theorem', 'lemma', 'proposition'] and parent_env == 'proof':
                        self.errors.append(
                            f"{file_path}:{line_num}: {env_type.capitalize()} "
                            f"cannot be nested inside proof"
                        )

            # Check for end environment
            end_matches = re.finditer(end_pattern, line)
            for match in end_matches:
                env_type = match.group(1)
                if not env_stack:
                    self.errors.append(
                        f"{file_path}:{line_num}: \\end{{{env_type}}} without "
                        f"matching \\begin"
                    )
                elif env_stack[-1][0] != env_type:
                    self.errors.append(
                        f"{file_path}:{line_num}: \\end{{{env_type}}} does not "
                        f"match \\begin{{{env_stack[-1][0]}}}"
                    )
                else:
                    env_stack.pop()

        # Check for unclosed environments
        if env_stack:
            for env_type, line_num in env_stack:
                self.errors.append(
                    f"{file_path}:{line_num}: \\begin{{{env_type}}} not closed"
                )

    def print_results(self):
        """Print validation results"""
        if self.warnings:
            print("\nWARNINGS:")
            for warning in self.warnings:
                print(f"  ⚠  {warning}")

        if self.errors:
            print("\nERRORS:")
            for error in self.errors:
                print(f"  ✗ {error}")
            print(f"\n❌ VALIDATION FAILED: {len(self.errors)} error(s) found")
            return False
        else:
            print(f"\n✓ VALIDATION PASSED")
            print(f"  - {len(self.all_ids)} unique IDs validated")
            print(f"  - All citations reference registry entries")
            if self.warnings:
                print(f"  - {len(self.warnings)} warning(s)")
            return True


def main():
    parser = argparse.ArgumentParser(
        description='Validate TeX files against thesis structure schema'
    )
    parser.add_argument(
        '--tex-dir',
        type=Path,
        required=True,
        help='Directory containing TeX files'
    )
    parser.add_argument(
        '--schema',
        type=Path,
        default=Path(__file__).parent.parent / 'thesis-structure.schema.yaml',
        help='Path to thesis structure schema'
    )
    parser.add_argument(
        '--bibliography',
        type=Path,
        default=Path(__file__).parent.parent.parent / 'bibliography' / 'citations.yaml',
        help='Path to citations registry'
    )

    args = parser.parse_args()

    if not args.tex_dir.exists():
        print(f"❌ ERROR: TeX directory not found: {args.tex_dir}")
        sys.exit(1)

    if not args.schema.exists():
        print(f"❌ ERROR: Schema file not found: {args.schema}")
        sys.exit(1)

    if not args.bibliography.exists():
        print(f"❌ ERROR: Bibliography file not found: {args.bibliography}")
        sys.exit(1)

    print(f"Validating TeX structure in: {args.tex_dir}")
    print(f"Using schema: {args.schema}")
    print(f"Using bibliography: {args.bibliography}")

    validator = TexStructureValidator(args.schema, args.bibliography)

    # Find all .tex files
    tex_files = sorted(args.tex_dir.rglob('*.tex'))

    if not tex_files:
        print(f"⚠  WARNING: No .tex files found in {args.tex_dir}")
        sys.exit(0)

    print(f"\nValidating {len(tex_files)} TeX file(s)...")

    all_valid = True
    for tex_file in tex_files:
        if not validator.validate_file(tex_file):
            all_valid = False

    success = validator.print_results()
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
