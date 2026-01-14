#!/usr/bin/env python3
"""IRI consistency validator for Catty ontologies.

This validator enforces Phase 1 IRI safety rules:
- Each ontology file must be valid JSON (JSON-LD payload)
- Each ontology must define an `@base` IRI (via `@context`)
- The `@base` IRI must be registered in `.catty/iri-config.yaml`
- No unauthorized `@id` IRIs may appear in the content
- `@context` should point to the configured context URL (mismatches are warnings)

It is designed to run locally without any external services or network calls.
"""

from __future__ import annotations

import argparse
import importlib.util
import json
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple


def _yaml_strip_quotes(value: str) -> str:
    value = value.strip()
    if (value.startswith('"') and value.endswith('"')) or (value.startswith("'") and value.endswith("'")):
        return value[1:-1]
    return value


def _load_simple_yaml_mapping(text: str) -> Dict[str, Any]:
    """Load a restricted YAML mapping (nested dicts of scalar strings)."""

    root: Dict[str, Any] = {}
    stack: List[Tuple[int, Dict[str, Any]]] = [(0, root)]

    for raw_line in text.splitlines():
        line = raw_line.rstrip("\n")
        if not line.strip() or line.lstrip().startswith("#"):
            continue

        indent = len(line) - len(line.lstrip(" "))
        if indent % 2 != 0:
            raise ValueError(f"Unsupported YAML indentation (expected 2 spaces): {raw_line!r}")

        while stack and indent < stack[-1][0]:
            stack.pop()
        if not stack:
            raise ValueError(f"Malformed YAML near line: {raw_line!r}")

        current = stack[-1][1]
        if ":" not in line:
            raise ValueError(f"Malformed YAML line (missing ':'): {raw_line!r}")

        key, rest = line.lstrip().split(":", 1)
        key = key.strip()
        value = rest.strip()

        if value == "":
            new_map: Dict[str, Any] = {}
            current[key] = new_map
            stack.append((indent + 2, new_map))
        else:
            current[key] = _yaml_strip_quotes(value)

    return root


@dataclass
class Finding:
    """A validation finding."""

    file: str
    message: str
    severity: str  # "ERROR" or "WARNING"


def _load_iri_config_class(repo_root: Path) -> Any:
    """Load IRIConfig from scripts/iri-config.py via importlib."""

    iri_config_path = repo_root / "scripts" / "iri-config.py"
    if not iri_config_path.exists():
        raise FileNotFoundError(f"Expected IRIConfig module not found: {iri_config_path}")

    spec = importlib.util.spec_from_file_location("catty_iri_config", iri_config_path)
    if spec is None or spec.loader is None:
        raise ImportError(f"Unable to load module from {iri_config_path}")

    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)  # type: ignore[arg-type]
    return module.IRIConfig


class IRIValidator:
    """Validate ontology JSON-LD files against `.catty/iri-config.yaml`."""

    def __init__(self, config_path: str = ".catty/iri-config.yaml") -> None:
        """Create a validator.

        Args:
            config_path: Path to `.catty/iri-config.yaml`.

        Raises:
            FileNotFoundError: If the config is missing.
            ValueError: If the config cannot be parsed.
        """

        self.config_path = Path(config_path)
        self.errors: List[Finding] = []
        self.warnings: List[Finding] = []

        self._config = self._load_config(self.config_path)
        self._repo_root = Path(__file__).resolve().parents[2]
        IRIConfig = _load_iri_config_class(self._repo_root)
        self._iri_config = IRIConfig(config_path=str(self.config_path))

    def validate_ontology_file(self, file_path: str) -> bool:
        """Validate a single ontology file.

        Args:
            file_path: Path to a JSON-LD file.

        Returns:
            True if the file has no errors (warnings are allowed).
        """

        path = Path(file_path)
        if not path.exists():
            self.errors.append(Finding(file=str(path), message=f"Ontology file not found: {path}", severity="ERROR"))
            return False

        try:
            content = path.read_text(encoding="utf-8")
        except OSError as e:
            self.errors.append(Finding(file=str(path), message=f"Unable to read ontology file: {e}", severity="ERROR"))
            return False

        try:
            payload: Any = json.loads(content)
        except json.JSONDecodeError as e:
            self.errors.append(
                Finding(file=str(path), message=f"Invalid JSON-LD (malformed JSON): {e}", severity="ERROR")
            )
            return False

        base_iri = self._extract_base_iri(payload)
        if base_iri is None:
            self.errors.append(Finding(file=str(path), message="Missing @base in @context", severity="ERROR"))
            return False

        if not self._is_registered_base(base_iri):
            self.errors.append(
                Finding(
                    file=str(path),
                    message=f"@base IRI is not registered in iri-config.yaml: {base_iri}",
                    severity="ERROR",
                )
            )
            return False

        context_ok = self._context_includes_expected(payload)
        if not context_ok:
            expected = self._expected_context_url()
            self.warnings.append(
                Finding(
                    file=str(path),
                    message=f"@context does not include expected context URL: {expected}",
                    severity="WARNING",
                )
            )

        iri_scan = self._iri_config.validate_no_fabricated_iris(content)
        if not bool(iri_scan.get("ok", False)):
            for msg in iri_scan.get("errors", []):
                self.errors.append(Finding(file=str(path), message=str(msg), severity="ERROR"))
            return False

        return True

    def validate_all_ontologies(self) -> bool:
        """Validate all ontologies referenced in `.catty/iri-config.yaml`.

        Returns:
            True if all registered ontologies validate without errors.
        """

        ok = True
        for key, entry in self._config["ontologies"].items():
            file_rel = entry.get("file")
            if not isinstance(file_rel, str):
                self.errors.append(
                    Finding(file=str(self.config_path), message=f"Ontology '{key}' missing 'file' entry", severity="ERROR")
                )
                ok = False
                continue

            file_path = (self._repo_root / file_rel).resolve()
            if not self.validate_ontology_file(str(file_path)):
                ok = False

        return ok

    def report(self) -> bool:
        """Print a report and return success status.

        Returns:
            True if there are no errors, otherwise False.
        """

        if self.errors:
            print("IRI validation failed:", file=sys.stderr)
            for err in self.errors:
                print(f"  [ERROR] {err.file}: {err.message}", file=sys.stderr)

        if self.warnings:
            print("IRI validation warnings:", file=sys.stderr)
            for warn in self.warnings:
                print(f"  [WARN]  {warn.file}: {warn.message}", file=sys.stderr)

        if not self.errors:
            print("IRI validation successful: all ontologies conform.")
            return True

        return False

    # -----------------
    # Internal helpers
    # -----------------

    def _load_config(self, path: Path) -> Dict[str, Any]:
        if not path.exists():
            raise FileNotFoundError(f"IRI config not found: {path}")

        text = path.read_text(encoding="utf-8")
        try:
            data = _load_simple_yaml_mapping(text)
        except ValueError as e:
            raise ValueError(f"Invalid YAML in IRI config: {e}") from e

        if "ontologies" not in data or not isinstance(data["ontologies"], dict):
            raise ValueError("IRI config must contain an 'ontologies' mapping")

        return data  # type: ignore[return-value]

    def _expected_context_url(self) -> str:
        localhost = self._config.get("localhost")
        if not isinstance(localhost, dict) or not isinstance(localhost.get("base_url"), str):
            return "http://localhost:8080/ontology/context.jsonld"

        base = str(localhost["base_url"]).rstrip("/")
        namespace = str(localhost.get("namespace_path", "/ontology")).rstrip("/")
        return f"{base}{namespace}/context.jsonld"

    def _registered_bases(self) -> List[str]:
        bases: List[str] = []
        for entry in self._config["ontologies"].values():
            if isinstance(entry, dict):
                for k in ("localhost_iri", "production_iri"):
                    val = entry.get(k)
                    if isinstance(val, str):
                        bases.append(val)
        return bases

    def _is_registered_base(self, base_iri: str) -> bool:
        return base_iri in set(self._registered_bases())

    def _extract_base_iri(self, payload: Any) -> Optional[str]:
        if not isinstance(payload, dict):
            return None

        ctx = payload.get("@context")
        if isinstance(ctx, dict):
            base = ctx.get("@base")
            return base if isinstance(base, str) else None

        if isinstance(ctx, list):
            for item in ctx:
                if isinstance(item, dict) and isinstance(item.get("@base"), str):
                    return str(item["@base"])
            return None

        return None

    def _context_includes_expected(self, payload: Any) -> bool:
        expected = self._expected_context_url()
        accepted = {expected, "context.jsonld", "./context.jsonld"}
        if not isinstance(payload, dict):
            return False

        ctx = payload.get("@context")
        if isinstance(ctx, str):
            return ctx in accepted
        if isinstance(ctx, list):
            return any(isinstance(item, str) and item in accepted for item in ctx)
        if isinstance(ctx, dict):
            # Context object without an import is allowed but not preferred.
            return False
        return False


def main(argv: Optional[List[str]] = None) -> int:
    """CLI entrypoint."""

    parser = argparse.ArgumentParser(description="Validate ontology IRIs against `.catty/iri-config.yaml`")
    parser.add_argument("--config", default=".catty/iri-config.yaml", help="Path to iri-config.yaml")
    args = parser.parse_args(argv)

    validator = IRIValidator(config_path=args.config)
    ok = validator.validate_all_ontologies()
    ok = validator.report() and ok
    return 0 if ok else 1


if __name__ == "__main__":  # pragma: no cover
    raise SystemExit(main())
