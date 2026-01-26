#!/usr/bin/env python3
"""
Input validation and sanitization utilities for Catty thesis workflow.
Ensures all data inputs are validated, sanitized, and handled securely.
"""

import os
import sys
import re
from pathlib import Path
from typing import Union, List, Optional
import json

# Try to import yaml, but don't fail if it's not available
try:
    import yaml
    YAML_AVAILABLE = True
except ImportError:
    YAML_AVAILABLE = False

class SecurityError(Exception):
    """Custom exception for security violations"""
    pass

def validate_path(path: Union[str, Path], allowed_base: Path = None) -> Path:
    """
    Validate and sanitize file/directory paths.
    
    Args:
        path: The path to validate
        allowed_base: Base directory that the path must be within (if provided)
        
    Returns:
        Path: Sanitized Path object
        
    Raises:
        SecurityError: If path traversal or other security issues detected
    """
    if not isinstance(path, (str, Path)):
        raise SecurityError(f"Invalid path type: {type(path)}")

    base_dir = (allowed_base or Path.cwd()).resolve()
    
    # Resolve the user-provided path.
    # This will handle '..' and other relative path components.
    resolved_path = (base_dir / path).resolve()

    # Check if the resolved path is within the allowed base directory.
    # This is a robust way to prevent path traversal.
    try:
        resolved_path.relative_to(base_dir)
    except ValueError:
        raise SecurityError(f"Path traversal attempt detected. Path '{resolved_path}' is outside of allowed base directory '{base_dir}'.")

    return resolved_path

def validate_identifier(identifier: str, pattern: str = r'^[a-zA-Z0-9_]+$') -> str:
    """
    Validate identifiers using a regex pattern.
    
    Args:
        identifier: The identifier to validate
        pattern: Regex pattern to match against
        
    Returns:
        str: Sanitized identifier
        
    Raises:
        SecurityError: If identifier doesn't match pattern or is invalid
    """
    if not isinstance(identifier, str):
        raise SecurityError(f"Identifier must be string, got: {type(identifier)}")
    
    if not identifier:
        raise SecurityError("Empty identifier not allowed")
    
    if len(identifier) > 100:
        raise SecurityError(f"Identifier too long: {len(identifier)} characters")
    
    if not re.match(pattern, identifier):
        raise SecurityError(f"Invalid identifier format: {identifier}")
    
    return identifier

def validate_citation_key(key: str) -> str:
    """
    Validate citation keys specifically.
    
    Args:
        key: The citation key to validate
        
    Returns:
        str: Sanitized citation key
    """
    # Allow alphanumeric, hyphens, and underscores
    pattern = r'^[a-zA-Z0-9_]+$'
    return validate_identifier(key, pattern)

def sanitize_yaml_content(content: str) -> str:
    """
    Sanitize YAML content to prevent injection attacks.
    
    Args:
        content: Raw YAML content
        
    Returns:
        str: Sanitized content
    """
    # Remove potentially dangerous patterns
    dangerous_patterns = [
        r'!\!python.*',  # Python YAML tags
        r'!!python.*',   # Python YAML tags (alternative)
        r'!!map.*',      # Generic tags
        r'!!seq.*',      # Generic tags
        r'!!str.*',      # Generic tags
    ]
    
    for pattern in dangerous_patterns:
        content = re.sub(pattern, '', content)
    
    return content

def validate_file_content(content: str, max_size: int = 1024*1024) -> str:
    """
    Validate file content size and basic safety.
    
    Args:
        content: File content to validate
        max_size: Maximum allowed content size in bytes
        
    Returns:
        str: Validated content
        
    Raises:
        SecurityError: If content is too large or contains dangerous patterns
    """
    if len(content.encode('utf-8')) > max_size:
        raise SecurityError(f"Content too large: {len(content.encode('utf-8'))} bytes > {max_size}")
    
    # Check for null bytes
    if '\x00' in content:
        raise SecurityError("Null bytes detected in content")
    
    # Check for extremely long lines (potential DoS)
    lines = content.split('\n')
    for i, line in enumerate(lines, 1):
        if len(line) > 10000:
            raise SecurityError(f"Line {i} too long: {len(line)} characters")
    
    return content

def validate_json_input(data: str) -> dict:
    """
    Validate and parse JSON input.
    
    Args:
        data: JSON string to validate
        
    Returns:
        dict: Parsed JSON data
        
    Raises:
        SecurityError: If JSON is invalid or contains dangerous patterns
    """
    try:
        parsed = json.loads(data)
    except json.JSONDecodeError as e:
        raise SecurityError(f"Invalid JSON: {e}")
    
    # Check for deeply nested structures (DoS protection)
    def check_depth(obj, depth=0, max_depth=10):
        if depth > max_depth:
            raise SecurityError(f"JSON structure too deeply nested (> {max_depth})")
        if isinstance(obj, dict):
            for value in obj.values():
                check_depth(value, depth + 1, max_depth)
        elif isinstance(obj, list):
            for item in obj:
                check_depth(item, depth + 1, max_depth)
    
    check_depth(parsed)
    return parsed

def validate_yaml_input(data: str, allowed_keys: List[str] = None) -> dict:
    """
    Validate and parse YAML input with optional key restrictions.
    
    Args:
        data: YAML string to validate
        allowed_keys: List of allowed top-level keys (if provided)
        
    Returns:
        dict: Parsed YAML data
        
    Raises:
        SecurityError: If YAML is invalid or contains restricted keys
    """
    if not YAML_AVAILABLE:
        raise SecurityError("PyYAML not available for YAML validation")
    
    sanitized_data = sanitize_yaml_content(data)
    
    try:
        parsed = yaml.safe_load(sanitized_data)
    except yaml.YAMLError as e:
        raise SecurityError(f"Invalid YAML: {e}")
    
    if parsed is None:
        return {}
    
    if not isinstance(parsed, dict):
        raise SecurityError("YAML root must be an object/dictionary")
    
    # Check for disallowed keys if restrictions provided
    if allowed_keys:
        for key in parsed.keys():
            if key not in allowed_keys:
                raise SecurityError(f"Disallowed key in YAML: {key}")
    
    return parsed

def validate_file_path(file_path: Path, allowed_extensions: List[str] = None) -> Path:
    """
    Comprehensive file path validation.
    
    Args:
        file_path: Path to validate
        allowed_extensions: List of allowed file extensions (e.g., ['.tex', '.json'])
        
    Returns:
        Path: Validated path
        
    Raises:
        SecurityError: If path validation fails
    """
    # Use the path validation function
    validated_path = validate_path(file_path)
    
    # Check if file exists (if path refers to existing file)
    if validated_path.exists() and not validated_path.is_file():
        raise SecurityError(f"Path is not a file: {validated_path}")
    
    # Check file extensions if specified
    if allowed_extensions and validated_path.exists():
        if validated_path.suffix not in allowed_extensions:
            raise SecurityError(f"File extension not allowed: {validated_path.suffix}")
    
    return validated_path

def validate_directory_path(dir_path: Path) -> Path:
    """
    Validate directory path.
    
    Args:
        dir_path: Directory path to validate
        
    Returns:
        Path: Validated directory path
        
    Raises:
        SecurityError: If path validation fails
    """
    validated_path = validate_path(dir_path)
    
    if not validated_path.exists():
        raise SecurityError(f"Directory does not exist: {validated_path}")
    
    if not validated_path.is_dir():
        raise SecurityError(f"Path is not a directory: {validated_path}")
    
    return validated_path

if __name__ == '__main__':
    # Simple test function
    def test_validation():
        print("Testing input validation utilities...")
        
        # Test valid path
        try:
            safe_path = validate_path("thesis/chapters")
            print(f"✅ Valid path: {safe_path}")
        except SecurityError as e:
            print(f"❌ Path validation error: {e}")
        
        # Test invalid path (path traversal)
        try:
            validate_path("../etc/passwd")
            print("❌ Path traversal not caught!")
        except SecurityError:
            print("✅ Path traversal attempt properly rejected")
        
        # Test valid identifier
        try:
            safe_id = validate_identifier("valid_key_123")
            print(f"✅ Valid identifier: {safe_id}")
        except SecurityError as e:
            print(f"❌ Identifier validation error: {e}")
        
        # Test invalid identifier
        try:
            validate_identifier("invalid/key")
            print("❌ Invalid identifier not caught!")
        except SecurityError:
            print("✅ Invalid identifier properly rejected")
        
        print("Validation tests completed.")
    
    test_validation()