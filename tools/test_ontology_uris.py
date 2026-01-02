#!/usr/bin/env python3
"""
Test suite for validating Catty ontology URIs.

This test ensures all ontology files use the correct GitHub Pages URI.

Expected URI: https://metavacua.github.io/CategoricalReasoner/ontology/
Invalid URI: http://catty.org/ontology/

Issue: #8 - Catty specific ontologies have invalidate URI
"""

import json
import unittest
from pathlib import Path
from typing import List, Tuple

EXPECTED_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"
INVALID_URI = "http://catty.org/ontology/"


class TestOntologyURIs(unittest.TestCase):
    """Test case for validating ontology URIs."""

    @classmethod
    def setUpClass(cls):
        """Set up test fixtures."""
        cls.script_dir = Path(__file__).parent
        cls.repo_root = cls.script_dir.parent
        cls.ontology_dir = cls.repo_root / 'ontology'

    def get_ontology_files(self) -> List[Path]:
        """Get all ontology files."""
        if not self.ontology_dir.exists():
            return []

        files = []
        files.extend(self.ontology_dir.glob('*.jsonld'))
        files.extend(self.ontology_dir.glob('*.ttl'))
        files.extend(self.ontology_dir.glob('**/*.jsonld'))
        files.extend(self.ontology_dir.glob('**/*.ttl'))

        return sorted(set(files))

    def check_jsonld_uri(self, filepath: Path) -> Tuple[bool, str]:
        """Check if a JSON-LD file uses the correct URI."""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)

            if '@context' in data:
                context = data['@context']
                if isinstance(context, dict) and 'catty' in context:
                    catty_uri = context['catty']

                    if catty_uri == INVALID_URI:
                        return False, f"Uses invalid URI: {INVALID_URI}"
                    elif catty_uri == EXPECTED_URI:
                        return True, f"Uses correct URI: {EXPECTED_URI}"
                    else:
                        return False, f"Uses unexpected URI: {catty_uri}"

            return True, "No catty URI found in @context"

        except Exception as e:
            return False, f"Error: {e}"

    def check_ttl_uri(self, filepath: Path) -> Tuple[bool, str]:
        """Check if a Turtle file uses the correct URI."""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            if INVALID_URI in content:
                return False, f"Uses invalid URI: {INVALID_URI}"
            elif EXPECTED_URI in content:
                return True, f"Uses correct URI: {EXPECTED_URI}"
            elif 'catty:' in content and '<http://catty.org/ontology/>' in content:
                return False, "Uses invalid URI in angle brackets"

            return True, "No catty URI found"

        except Exception as e:
            return False, f"Error: {e}"

    def test_ontology_directory_exists(self):
        """Test that the ontology directory exists."""
        self.assertTrue(
            self.ontology_dir.exists(),
            f"Ontology directory not found: {self.ontology_dir}"
        )

    def test_ontology_files_exist(self):
        """Test that ontology files exist."""
        files = self.get_ontology_files()
        self.assertGreater(
            len(files), 0,
            "No ontology files found"
        )

    def test_all_ontology_uris_are_valid(self):
        """Test that all ontology files use the correct GitHub Pages URI."""
        files = self.get_ontology_files()
        invalid_files = []

        for filepath in files:
            if filepath.suffix == '.jsonld':
                is_valid, message = self.check_jsonld_uri(filepath)
            elif filepath.suffix == '.ttl':
                is_valid, message = self.check_ttl_uri(filepath)
            else:
                continue

            if not is_valid:
                rel_path = filepath.relative_to(self.repo_root)
                invalid_files.append(f"{rel_path}: {message}")

        self.assertEqual(
            len(invalid_files), 0,
            f"\n\nThe following files use invalid URIs:\n" +
            "\n".join(f"  - {f}" for f in invalid_files) +
            f"\n\nExpected URI: {EXPECTED_URI}\n" +
            f"Invalid URI: {INVALID_URI}\n\n" +
            "Please update all ontology files to use the correct GitHub Pages URI."
        )


if __name__ == '__main__':
    unittest.main()
