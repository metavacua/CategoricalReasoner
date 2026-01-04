#!/usr/bin/env python3
"""
Catty Validation Script
Validates task outputs against acceptance criteria defined in operations.yaml
"""

import argparse
import json
import os
import re
import sys
from pathlib import Path
from typing import Dict, List, Tuple, Any

try:
    import yaml
except ImportError:
    print("ERROR: PyYAML is required. Install with: pip install pyyaml")
    sys.exit(1)

try:
    import rdflib
    from rdflib import Graph
    RDFLIB_AVAILABLE = True
except ImportError:
    RDFLIB_AVAILABLE = False
    print("WARNING: rdflib not available. RDF validation will be skipped.")

try:
    from pyshacl import validate as shacl_validate
    PYSHACL_AVAILABLE = True
except ImportError:
    PYSHACL_AVAILABLE = False
    print("WARNING: pyshacl not available. SHACL validation will be skipped.")


class CattyValidator:
    """Main validator class for Catty artifacts"""
    
    def __init__(self, operations_file: str):
        self.operations_file = Path(operations_file)
        self.project_root = self.operations_file.parent.parent
        self.operations = self._load_operations()
        
    def _load_operations(self) -> Dict:
        """Load operations.yaml"""
        if not self.operations_file.exists():
            raise FileNotFoundError(f"Operations file not found: {self.operations_file}")
        
        with open(self.operations_file, 'r') as f:
            return yaml.safe_load(f)
    
    def validate_artifact(self, artifact_id: str) -> Tuple[bool, List[str]]:
        """
        Validate an artifact against its specification
        Returns: (success: bool, errors: List[str])
        """
        if artifact_id not in self.operations.get('artifacts', {}):
            return False, [f"Artifact '{artifact_id}' not found in operations.yaml"]
        
        artifact = self.operations['artifacts'][artifact_id]
        artifact_path = self.project_root / artifact['path']
        
        print(f"\n{'='*70}")
        print(f"Validating artifact: {artifact_id}")
        print(f"Path: {artifact_path}")
        print(f"Format: {artifact['format']}")
        print(f"{'='*70}\n")
        
        errors = []
        
        # Check file existence
        if not artifact_path.exists():
            errors.append(f"Artifact file does not exist: {artifact_path}")
            return False, errors
        
        # Format-specific validation
        format_type = artifact['format'].lower()
        
        if 'json-ld' in format_type or format_type == 'json':
            success, format_errors = self._validate_json_ld(artifact_path, artifact)
            errors.extend(format_errors)
        
        elif 'rdf' in format_type or 'turtle' in format_type or 'ttl' in format_type:
            success, format_errors = self._validate_rdf(artifact_path, artifact)
            errors.extend(format_errors)
        
        elif 'latex' in format_type:
            success, format_errors = self._validate_latex(artifact_path, artifact)
            errors.extend(format_errors)
        
        elif 'markdown' in format_type:
            success, format_errors = self._validate_markdown(artifact_path, artifact)
            errors.extend(format_errors)
        
        elif 'python' in format_type:
            success, format_errors = self._validate_python(artifact_path, artifact)
            errors.extend(format_errors)
        
        else:
            # Generic file validation
            success, format_errors = self._validate_generic(artifact_path, artifact)
            errors.extend(format_errors)
        
        # Validate content specifications
        if 'content_spec' in artifact:
            content_errors = self._validate_content_spec(artifact_path, artifact)
            errors.extend(content_errors)
        
        # Run SHACL validation if applicable
        if 'schema' in artifact and artifact.get('format') in ['JSON-LD', 'RDF/Turtle']:
            shacl_success, shacl_errors = self._validate_shacl(artifact_path, artifact)
            errors.extend(shacl_errors)
        
        success = len(errors) == 0
        
        if success:
            print(f"✓ Validation PASSED for {artifact_id}")
        else:
            print(f"✗ Validation FAILED for {artifact_id}")
            print(f"\nErrors ({len(errors)}):")
            for i, error in enumerate(errors, 1):
                print(f"  {i}. {error}")
        
        return success, errors
    
    def _validate_json_ld(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Validate JSON-LD file"""
        errors = []
        
        try:
            with open(file_path, 'r') as f:
                data = json.load(f)
        except json.JSONDecodeError as e:
            errors.append(f"Invalid JSON syntax: {e}")
            return False, errors
        
        print("✓ Valid JSON syntax")
        
        # Check for @context
        if '@context' not in data:
            errors.append("Missing @context in JSON-LD")
        else:
            print("✓ Contains @context")
        
        # Check for required prefixes in context
        context = data.get('@context', {})
        if isinstance(context, dict):
            required_prefixes = ['catty', 'rdf', 'rdfs', 'owl']
            for prefix in required_prefixes:
                if prefix in str(context):
                    print(f"✓ Contains {prefix} prefix")
        
        # If rdflib available, try parsing as RDF
        if RDFLIB_AVAILABLE:
            try:
                g = Graph()
                g.parse(data=json.dumps(data), format='json-ld')
                print(f"✓ Parses as RDF graph ({len(g)} triples)")
            except Exception as e:
                errors.append(f"Failed to parse as RDF: {e}")
        
        return len(errors) == 0, errors
    
    def _validate_rdf(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Validate RDF/Turtle file"""
        errors = []
        
        if not RDFLIB_AVAILABLE:
            errors.append("rdflib not available - skipping RDF validation")
            return True, errors
        
        try:
            g = Graph()
            g.parse(str(file_path), format='turtle')
            print(f"✓ Valid Turtle syntax ({len(g)} triples)")
        except Exception as e:
            errors.append(f"Invalid Turtle syntax: {e}")
            return False, errors
        
        # Check for SHACL constructs if this is a SHACL file
        if 'shacl' in artifact.get('path', '').lower():
            shacl_ns = "http://www.w3.org/ns/shacl#"
            has_shapes = any(str(s).startswith(shacl_ns) or 
                           str(p).startswith(shacl_ns) or 
                           str(o).startswith(shacl_ns) 
                           for s, p, o in g)
            if has_shapes:
                print("✓ Contains SHACL constructs")
            else:
                errors.append("SHACL file does not contain SHACL constructs")
        
        return len(errors) == 0, errors
    
    def _validate_latex(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Validate LaTeX file"""
        errors = []
        
        with open(file_path, 'r') as f:
            content = f.read()
        
        # Check for required LaTeX commands
        if 'main' in file_path.name:
            if r'\documentclass' not in content:
                errors.append("Missing \\documentclass command")
            else:
                print("✓ Contains \\documentclass")
            
            if r'\begin{document}' not in content:
                errors.append("Missing \\begin{document}")
            else:
                print("✓ Contains \\begin{document}")
            
            if r'\end{document}' not in content:
                errors.append("Missing \\end{document}")
            else:
                print("✓ Contains \\end{document}")
        
        if 'chapter' in file_path.name or 'chapters' in str(file_path):
            if r'\chapter{' not in content:
                errors.append("Chapter file missing \\chapter command")
            else:
                print("✓ Contains \\chapter command")
            
            sections = content.count(r'\section{')
            print(f"✓ Contains {sections} sections")
        
        # Count lines
        lines = content.split('\n')
        line_count = len([l for l in lines if l.strip() and not l.strip().startswith('%')])
        print(f"✓ Contains {line_count} non-empty, non-comment lines")
        
        return len(errors) == 0, errors
    
    def _validate_markdown(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Validate Markdown file"""
        errors = []
        
        with open(file_path, 'r') as f:
            content = f.read()
        
        # Count lines
        lines = content.split('\n')
        line_count = len([l for l in lines if l.strip()])
        print(f"✓ Contains {line_count} non-empty lines")
        
        # Count sections (markdown headers)
        sections = len(re.findall(r'^#+\s+', content, re.MULTILINE))
        print(f"✓ Contains {sections} sections/headers")
        
        # Count code blocks
        code_blocks = len(re.findall(r'```', content)) // 2
        print(f"✓ Contains {code_blocks} code blocks")
        
        return len(errors) == 0, errors
    
    def _validate_python(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Validate Python file"""
        errors = []
        
        try:
            with open(file_path, 'r') as f:
                content = f.read()
            
            # Try to compile (syntax check)
            compile(content, str(file_path), 'exec')
            print("✓ Valid Python syntax")
        except SyntaxError as e:
            errors.append(f"Python syntax error: {e}")
            return False, errors
        
        # Check for required imports
        if 'import rdflib' in content or 'from rdflib' in content:
            print("✓ Imports rdflib")
        
        if 'import yaml' in content or 'from yaml' in content:
            print("✓ Imports yaml")
        
        # Check for main entry point
        if "if __name__ == '__main__':" in content:
            print("✓ Has main entry point")
        
        return len(errors) == 0, errors
    
    def _validate_generic(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Generic file validation"""
        errors = []
        
        # Check file is readable
        try:
            with open(file_path, 'r') as f:
                content = f.read()
            print(f"✓ File is readable ({len(content)} bytes)")
        except Exception as e:
            errors.append(f"Cannot read file: {e}")
        
        return len(errors) == 0, errors
    
    def _validate_content_spec(self, file_path: Path, artifact: Dict) -> List[str]:
        """Validate content specifications"""
        errors = []
        content_spec = artifact.get('content_spec', [])
        
        if not content_spec:
            return errors
        
        try:
            with open(file_path, 'r') as f:
                content = f.read()
        except:
            errors.append("Cannot read file for content spec validation")
            return errors
        
        print("\nChecking content specifications:")
        
        # Simple keyword checking (can be enhanced)
        for spec in content_spec:
            if isinstance(spec, str):
                # Extract keywords from spec
                keywords = re.findall(r'\b[A-Z][a-z]+(?:[A-Z][a-z]*)*\b', spec)
                keywords.extend(re.findall(r'@\w+', spec))
                
                found_any = False
                for keyword in keywords[:3]:  # Check first few keywords
                    if keyword in content:
                        found_any = True
                        break
                
                if found_any or not keywords:
                    print(f"  ✓ {spec[:60]}...")
                else:
                    print(f"  ? {spec[:60]}... (keywords not found)")
        
        return errors
    
    def _validate_shacl(self, file_path: Path, artifact: Dict) -> Tuple[bool, List[str]]:
        """Validate RDF file against SHACL shape"""
        errors = []
        
        if not RDFLIB_AVAILABLE or not PYSHACL_AVAILABLE:
            errors.append("SHACL validation requires rdflib and pyshacl")
            return True, errors
        
        shape_file = self.project_root / artifact.get('schema', '')
        
        if not shape_file.exists():
            print(f"⚠ SHACL shape file not found: {shape_file} (skipping SHACL validation)")
            return True, []
        
        try:
            data_graph = Graph()
            data_graph.parse(str(file_path))
            
            shapes_graph = Graph()
            shapes_graph.parse(str(shape_file))
            
            conforms, results_graph, results_text = shacl_validate(
                data_graph,
                shacl_graph=shapes_graph,
                inference='rdfs',
                abort_on_first=False
            )
            
            if conforms:
                print("✓ SHACL validation passed")
            else:
                errors.append(f"SHACL validation failed:\n{results_text}")
                print(f"✗ SHACL validation failed")
        
        except Exception as e:
            errors.append(f"SHACL validation error: {e}")
        
        return len(errors) == 0, errors
    
    def validate_task(self, task_id: str) -> Tuple[bool, List[str]]:
        """
        Validate all artifacts produced by a task
        Returns: (success: bool, errors: List[str])
        """
        if task_id not in self.operations.get('tasks', {}):
            return False, [f"Task '{task_id}' not found in operations.yaml"]
        
        task = self.operations['tasks'][task_id]
        produces = task.get('produces', [])
        
        if not produces:
            print(f"Task {task_id} produces no artifacts (validation task)")
            return True, []
        
        print(f"\n{'='*70}")
        print(f"Validating task: {task_id}")
        print(f"Produces: {', '.join(produces)}")
        print(f"{'='*70}")
        
        all_errors = []
        all_success = True
        
        for artifact_id in produces:
            success, errors = self.validate_artifact(artifact_id)
            all_success = all_success and success
            all_errors.extend(errors)
        
        return all_success, all_errors


def main():
    parser = argparse.ArgumentParser(
        description='Validate Catty artifacts against operations.yaml specifications'
    )
    parser.add_argument(
        '--artifact',
        type=str,
        help='Artifact ID to validate'
    )
    parser.add_argument(
        '--task',
        type=str,
        help='Task ID to validate (validates all produced artifacts)'
    )
    parser.add_argument(
        '--operations',
        type=str,
        default='.catty/operations.yaml',
        help='Path to operations.yaml (default: .catty/operations.yaml)'
    )
    parser.add_argument(
        '--list-artifacts',
        action='store_true',
        help='List all available artifacts'
    )
    parser.add_argument(
        '--list-tasks',
        action='store_true',
        help='List all available tasks'
    )
    
    args = parser.parse_args()
    
    # Find project root
    current_dir = Path.cwd()
    operations_path = current_dir / args.operations
    
    if not operations_path.exists():
        # Try parent directories
        for parent in current_dir.parents:
            test_path = parent / args.operations
            if test_path.exists():
                operations_path = test_path
                break
    
    if not operations_path.exists():
        print(f"ERROR: Operations file not found: {args.operations}")
        print(f"Searched in: {current_dir} and parent directories")
        sys.exit(1)
    
    validator = CattyValidator(str(operations_path))
    
    if args.list_artifacts:
        print("\nAvailable artifacts:")
        for artifact_id in validator.operations.get('artifacts', {}).keys():
            artifact = validator.operations['artifacts'][artifact_id]
            print(f"  - {artifact_id}: {artifact.get('path', 'N/A')}")
        sys.exit(0)
    
    if args.list_tasks:
        print("\nAvailable tasks:")
        for task_id in validator.operations.get('tasks', {}).keys():
            task = validator.operations['tasks'][task_id]
            print(f"  - {task_id}: produces {task.get('produces', [])}")
        sys.exit(0)
    
    if args.artifact:
        success, errors = validator.validate_artifact(args.artifact)
        sys.exit(0 if success else 1)
    
    elif args.task:
        success, errors = validator.validate_task(args.task)
        sys.exit(0 if success else 1)
    
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == '__main__':
    main()
