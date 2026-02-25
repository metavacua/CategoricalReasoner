#!/usr/bin/env python3
"""
AGENTS.md Derivation Script

This script derives AGENTS.md files from the repository-constraints.ttl
semantic web specification using canonical tools (rdflib, SPARQL).

Usage:
    python3 derive-agents.py [--output-dir <dir>]

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
        'agents_file': str(g.value(node, AGENTS.hasAgentsFile)),
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
            'agents_file': str(g.value(derives_from, AGENTS.hasAgentsFile))
        }
    
    # Get contained nodes
    for contained in g.objects(node, AGENTS.contains):
        info['contains'].append({
            'label': str(g.value(contained, RDFS.label)),
            'agents_file': str(g.value(contained, AGENTS.hasAgentsFile))
        })
    
    # Get applicable constraints (no duplicates)
    seen_labels = set()
    for constraint in g.objects(node, AGENTS.applicableConstraint):
        label = str(g.value(constraint, SKOS.prefLabel))
        if label not in seen_labels:
            seen_labels.add(label)
            constraint_info = {
                'label': label,
                'definition': str(g.value(constraint, SKOS.definition)),
                'scope_note': str(g.value(constraint, SKOS.scopeNote)) if g.value(constraint, SKOS.scopeNote) else None
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
    
    def add_constraint(constraint):
        label = str(g.value(constraint, SKOS.prefLabel))
        if label not in seen_labels:
            seen_labels.add(label)
            constraints.append({
                'label': label,
                'definition': str(g.value(constraint, SKOS.definition))
            })
    
    # Get direct narrower constraints of CoreConstraint
    for constraint in g.subjects(SKOS.broader, AGENTS.CoreConstraint):
        add_constraint(constraint)
    
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

def generate_root_agents_md(g: Graph, node_info: dict) -> str:
    """Generate the root AGENTS.md content."""
    core_constraints = get_core_constraints(g)
    validation_standards = get_validation_standards(g)
    
    content = """# AGENTS.md - Catty Thesis Repository

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
        content += f"- `{child['agents_file']}` - {child['label']}\n"
    
    content += """
## Specification

This file is derived from the canonical semantic web specification:
- `docs/standards/repository-constraints.ttl` - RDF/Turtle specification
- `docs/standards/derivation-queries.sparql` - SPARQL queries for derivation

"""
    return content

def generate_node_agents_md(node_info: dict, is_root_child: bool = False) -> str:
    """Generate AGENTS.md content for a non-root node."""
    
    content = f"""# AGENTS.md - {node_info['label']}

## Scope

"""
    
    if node_info['path']:
        content += f"This file governs all materials under the `{node_info['path']}` directory. "
    
    if node_info['derives_from']:
        content += f"All contents derive from and must comply with the root `AGENTS.md` constraints.\n"
    
    if node_info['constraints']:
        content += """
## Derivation from Root

The following constraints are derived from the root `AGENTS.md`:

"""
        for constraint in node_info['constraints']:
            content += f"- **{constraint['label']}**: {constraint['definition']}"
            if constraint['scope_note']:
                content += f" (Scope: {constraint['scope_note']})"
            content += "\n"
    
    if node_info['contains']:
        content += f"""
## Subdirectories

Each subdirectory has its own `AGENTS.md` that derives from root:

"""
        for child in node_info['contains']:
            content += f"- `{child['agents_file']}` - {child['label']}\n"
    
    if node_info['license']:
        content += f"""
## Licensing

All contents of this directory are licensed under {node_info['license']}.

"""
    
    content += """## See Also

"""
    if node_info['derives_from']:
        content += f"- `{node_info['derives_from']['agents_file']}` - {node_info['derives_from']['label']}\n"
    
    content += """
## Specification

This file is derived from the canonical semantic web specification:
- `docs/standards/repository-constraints.ttl` - RDF/Turtle specification

"""
    return content

def main():
    parser = argparse.ArgumentParser(description='Derive AGENTS.md files from semantic web specification')
    parser.add_argument('--spec', default='docs/standards/repository-constraints.ttl',
                        help='Path to repository-constraints.ttl')
    parser.add_argument('--output-dir', default='.',
                        help='Output directory for generated files')
    parser.add_argument('--dry-run', action='store_true',
                        help='Print generated content without writing files')
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
    
    # Generate AGENTS.md for each node
    for node in nodes:
        node_info = get_node_info(g, node)
        agents_file = node_info['agents_file']
        
        # Generate content
        if str(node) == str(AGENTS.RootNode):
            content = generate_root_agents_md(g, node_info)
        else:
            content = generate_node_agents_md(node_info)
        
        if args.dry_run:
            print(f"\n{'='*60}")
            print(f"FILE: {agents_file}")
            print(f"{'='*60}")
            print(content)
        else:
            output_path = os.path.join(args.output_dir, agents_file)
            os.makedirs(os.path.dirname(output_path), exist_ok=True)
            with open(output_path, 'w') as f:
                f.write(content)
            print(f"Generated: {output_path}")
    
    print("\nDerivation complete.")

if __name__ == '__main__':
    main()
