#!/usr/bin/env python3
"""
AGENTS.md and README.md Derivation Script

This script derives AGENTS.md and README.md files from the repository-constraints.ttl
semantic web specification using canonical tools (rdflib, SPARQL).

The root AGENTS.md is the source of truth - all other files derive from it.

Usage:
    python3 derive-agents.py [--output-dir <dir>] [--dry-run]

License: AGPL-3.0-or-later
"""

import argparse
import os
import sys
from pathlib import Path
from rdflib import Graph, Namespace, RDF, RDFS
from rdflib.namespace import SKOS, DCTERMS

# Define namespaces
AGENTS = Namespace("urn:repo:categoricalreasoner:agents#")
XSD = Namespace("http://www.w3.org/2001/XMLSchema#")

def load_graph(spec_path: str) -> Graph:
    """Load the repository constraints specification."""
    g = Graph()
    g.parse(spec_path, format="turtle")
    return g

def get_node_info(g: Graph, node) -> dict:
    """Get basic information about a repository node."""
    info = {
        'label': str(g.value(node, RDFS.label)),
        'path': str(g.value(node, AGENTS.path)) if g.value(node, AGENTS.path) else None,
        'agents_file': str(g.value(node, AGENTS.hasAgentsFile)) if g.value(node, AGENTS.hasAgentsFile) else None,
        'readme_file': str(g.value(node, AGENTS.hasReadmeFile)) if g.value(node, AGENTS.hasReadmeFile) else None,
        'license': None,
        'derives_from': None,
        'contains': [],
        'constraints': []
    }
    
    # Get license
    license_uri = g.value(node, AGENTS.license)
    if license_uri:
        info['license'] = str(license_uri).split('/')[-1]
    
    # Get derivation source
    derives_from = g.value(node, AGENTS.derivesFrom)
    if derives_from:
        info['derives_from'] = {
            'label': str(g.value(derives_from, RDFS.label)),
            'agents_file': str(g.value(derives_from, AGENTS.hasAgentsFile)) if g.value(derives_from, AGENTS.hasAgentsFile) else None
        }
    
    # Get contained nodes
    for contained in g.objects(node, AGENTS.contains):
        contained_info = {
            'label': str(g.value(contained, RDFS.label)),
            'path': str(g.value(contained, AGENTS.path)) if g.value(contained, AGENTS.path) else None
        }
        if g.value(contained, AGENTS.hasAgentsFile):
            contained_info['agents_file'] = str(g.value(contained, AGENTS.hasAgentsFile))
        if g.value(contained, AGENTS.hasReadmeFile):
            contained_info['readme_file'] = str(g.value(contained, AGENTS.hasReadmeFile))
        info['contains'].append(contained_info)
    
    # Get applicable constraints (no duplicates)
    seen_labels = set()
    for constraint in g.objects(node, AGENTS.applicableConstraint):
        label = str(g.value(constraint, SKOS.prefLabel))
        if label not in seen_labels:
            seen_labels.add(label)
            constraint_info = {
                'label': label,
                'definition': str(g.value(constraint, SKOS.definition))
            }
            info['constraints'].append(constraint_info)
    
    return info

def get_all_nodes(g: Graph) -> list:
    """Get all repository nodes."""
    return list(g.subjects(RDF.type, AGENTS.RepositoryNode))

def get_core_constraints(g: Graph) -> list:
    """Get all core constraints from the concept scheme (no duplicates)."""
    seen_labels = set()
    constraints = []
    
    for constraint in g.subjects(SKOS.broader, AGENTS.CoreConstraint):
        label = str(g.value(constraint, SKOS.prefLabel))
        if label not in seen_labels:
            seen_labels.add(label)
            constraints.append({
                'label': label,
                'definition': str(g.value(constraint, SKOS.definition))
            })
    
    return constraints

def get_validation_standards(g: Graph) -> list:
    """Get validation standards (no duplicates)."""
    seen_labels = set()
    constraints = []
    
    for constraint in g.subjects(SKOS.broader, AGENTS.ValidationStandard):
        label = str(g.value(constraint, SKOS.prefLabel))
        if label not in seen_labels:
            seen_labels.add(label)
            constraints.append({
                'label': label,
                'definition': str(g.value(constraint, SKOS.definition))
            })
    return constraints

def generate_agents_md(g: Graph, node_info: dict, is_root: bool = False) -> str:
    """Generate AGENTS.md content."""
    
    if is_root:
        core_constraints = get_core_constraints(g)
        validation_standards = get_validation_standards(g)
        
        content = """# AGENTS.md - Catty Thesis Repository
All other AGENTS.md and README.md files are derivatives of this file.

## Scope

This repository attempts to implement the Categorical Reasoner "Catty" thesis: categorical foundations for morphisms between logics rather than morphisms within logics; there are expected reflections from the category of subclassical sequent calculi into sequent calculi as categories or internal logics of categories, but the repository fundamentally concerns the category of subclassical sequent calculi. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, bash scripts, or curl during development. Semantic HTML documentation collects the semantic web data and standard ontologies published online and other relevant information for software development, formal methods, engineering practices, category theory, Java development, meta-linguistics, meta-mathematical theory development, computational theories, sequent calculi, strong normalization, canonical methods, formal verification, W3C standards, OMG DOL standards, ISO COLORE standards, open science, open source software and hardware and firmware, and more.

## Core Constraints

"""
        
        for constraint in core_constraints:
            content += f"- **{constraint['label']}**: {constraint['definition']}\n"
        
        content += """
## Validation

"""
        
        for standard in validation_standards:
            content += f"- **{standard['label']}**: {standard['definition']}\n"
        
        content += """
## See Also

"""
        
        for child in node_info['contains']:
            if 'agents_file' in child:
                content += f"- `{child['agents_file']}` - {child['label']}\n"
        
        content += """
"""
        return content
    
    else:
        # Non-root AGENTS.md
        content = f"""# AGENTS.md - {node_info['label']}

## Scope

"""
        
        if node_info['path']:
            content += f"This file governs all materials under the `{node_info['path']}` directory. "
        
        content += "All contents derive from and must comply with the root `AGENTS.md` constraints.\n"
        
        if node_info['constraints']:
            content += """
## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

"""
            for constraint in node_info['constraints']:
                content += f"- **{constraint['label']}**: {constraint['definition']}\n"
        
        if node_info['contains']:
            content += """
## Subdirectories

"""
            for child in node_info['contains']:
                content += f"- `{child.get('path', child['label'])}` - {child['label']}\n"
        
        if node_info['license']:
            content += f"""
## Licensing

All contents of this directory are licensed under {node_info['license']}.

"""
        
        content += """## See Also

- `AGENTS.md` (root) - Core repository constraints

"""
        return content

def generate_readme_md(node_info: dict, is_root: bool = False) -> str:
    """Generate README.md content."""
    
    if is_root:
        content = """# CategoricalReasoner

A categorical reasoner implementation focusing on morphisms between logics.

## Overview

This repository implements the Categorical Reasoner "Catty" thesis: categorical foundations for morphisms between logics rather than morphisms within logics.

## Structure

- `docs/` - Documentation (CC BY-SA 4.0)
- `src/` - Source code (AGPLv3)

## License

Dual licensed:
- Source code: GNU Affero General Public License v3.0 (AGPLv3)
- Documentation: Creative Commons BY-SA v4.0 International

## See Also

- `AGENTS.md` - Repository governance and constraints
"""
        return content
    
    else:
        content = f"""# {node_info['label']}

## Overview

"""
        
        if node_info['path']:
            content += f"This directory contains materials for `{node_info['path']}`.\n"
        
        content += """
## Derivation

This file derives from the root `AGENTS.md` constraints.

"""
        
        if node_info['constraints']:
            content += """## Applicable Constraints

"""
            for constraint in node_info['constraints'][:5]:  # Limit to first 5 for readability
                content += f"- **{constraint['label']}**: {constraint['definition'][:100]}...\n"
        
        if node_info['license']:
            content += f"""
## License

{node_info['license']}

"""
        
        content += """## See Also

- `AGENTS.md` (root) - Core repository constraints
"""
        return content

def main():
    parser = argparse.ArgumentParser(description='Derive AGENTS.md and README.md files from semantic web specification')
    parser.add_argument('--spec', default='docs/standards/repository-constraints.ttl',
                        help='Path to repository-constraints.ttl')
    parser.add_argument('--output-dir', default='.',
                        help='Output directory for generated files')
    parser.add_argument('--dry-run', action='store_true',
                        help='Print generated content without writing files')
    parser.add_argument('--agents-only', action='store_true',
                        help='Only generate AGENTS.md files')
    parser.add_argument('--readme-only', action='store_true',
                        help='Only generate README.md files')
    args = parser.parse_args()
    
    # Load specification
    if not os.path.exists(args.spec):
        print(f"Error: Specification file not found: {args.spec}", file=sys.stderr)
        sys.exit(1)
    
    g = load_graph(args.spec)
    print(f"Loaded specification from {args.spec}")
    
    # Get all nodes
    nodes = get_all_nodes(g)
    print(f"Found {len(nodes)} repository nodes")
    
    # Generate files for each node
    for node in nodes:
        node_info = get_node_info(g, node)
        is_root = str(node) == str(AGENTS.RootNode)
        
        # Generate AGENTS.md
        if not args.readme_only and node_info['agents_file']:
            content = generate_agents_md(g, node_info, is_root)
            
            if args.dry_run:
                print(f"\n{'='*60}")
                print(f"AGENTS.md: {node_info['agents_file']}")
                print(f"{'='*60}")
                print(content)
            else:
                output_path = os.path.join(args.output_dir, node_info['agents_file'])
                os.makedirs(os.path.dirname(output_path), exist_ok=True)
                with open(output_path, 'w') as f:
                    f.write(content)
                print(f"Generated: {output_path}")
        
        # Generate README.md
        if not args.agents_only and node_info['readme_file']:
            content = generate_readme_md(node_info, is_root)
            
            if args.dry_run:
                print(f"\n{'='*60}")
                print(f"README.md: {node_info['readme_file']}")
                print(f"{'='*60}")
                print(content)
            else:
                output_path = os.path.join(args.output_dir, node_info['readme_file'])
                os.makedirs(os.path.dirname(output_path), exist_ok=True)
                with open(output_path, 'w') as f:
                    f.write(content)
                print(f"Generated: {output_path}")
    
    print("\nDerivation complete.")

if __name__ == '__main__':
    main()
