#!/usr/bin/env python3
"""
Test script to validate that ontology URIs are correctly configured.

This test ensures that all Catty ontology files use the correct GitHub Pages URI
instead of the invalid http://catty.org/ontology/ URI.

Expected URI: https://metavacua.github.io/CategoricalReasoner/ontology/
"""

import json
import sys
from pathlib import Path
from typing import List, Tuple


EXPECTED_URI = "https://metavacua.github.io/CategoricalReasoner/ontology/"
INVALID_URI = "http://catty.org/ontology/"


def check_jsonld_file(filepath: Path) -> Tuple[bool, str]:
    """
    Check if a JSON-LD file uses the correct ontology URI.

    Args:
        filepath: Path to the JSON-LD file

    Returns:
        Tuple of (is_valid: bool, message: str)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)

        # Check @context for catty URI
        if '@context' in data:
            context = data['@context']
            if isinstance(context, dict) and 'catty' in context:
                catty_uri = context['catty']

                if catty_uri == INVALID_URI:
                    return False, f"Uses invalid URI: {INVALID_URI}"
                elif catty_uri == EXPECTED_URI:
                    return True, f"✓ Uses correct URI: {EXPECTED_URI}"
                else:
                    return False, f"Uses unexpected URI: {catty_uri}"

        return True, "No catty URI found in @context"

    except json.JSONDecodeError as e:
        return False, f"JSON parse error: {e}"
    except Exception as e:
        return False, f"Error: {e}"


def check_ttl_file(filepath: Path) -> Tuple[bool, str]:
    """
    Check if a Turtle file uses the correct ontology URI.

    Args:
        filepath: Path to the Turtle file

    Returns:
        Tuple of (is_valid: bool, message: str)
    """
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        if INVALID_URI in content:
            return False, f"Uses invalid URI: {INVALID_URI}"
        elif EXPECTED_URI in content:
            return True, f"✓ Uses correct URI: {EXPECTED_URI}"
        else:
            # Check if catty prefix is defined at all
            if 'catty:' in content and '<http' not in content:
                return False, "catty prefix used but URI not found"
            return True, "No catty URI found"

    except Exception as e:
        return False, f"Error: {e}"


def main():
    """Main test function."""
    print("URI Validation Test")
    print(f"Expected URI: {EXPECTED_URI}")
    print(f"Invalid URI: {INVALID_URI}")
    print("="*60)
