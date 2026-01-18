#!/usr/bin/env python3
"""Catty IRI configuration and rebinding utilities.

This module is the single source of truth for:
- Loading the IRI registry in `.catty/iri-config.yaml`
- Looking up the canonical localhost/production ontology IRIs
- Rebasing ("rebinding") JSON-LD content between environments
- Detecting fabricated/unauthorized IRIs in JSON-LD content

The implementation is intentionally self-contained (no network calls).
"""

from __future__ import annotations

import json
import logging
import re
from pathlib import Path
from typing import Any, Dict, Iterable, List, Mapping, MutableMapping, Optional, Sequence, Set, Tuple
from urllib.parse import urlparse

logger = logging.getLogger(__name__)


JsonObject = Dict[str, Any]


def _yaml_strip_quotes(value: str) -> str:
    value = value.strip()
    if (value.startswith('"') and value.endswith('"')) or (value.startswith("'") and value.endswith("'")):
        return value[1:-1]
    return value


def _load_simple_yaml_mapping(text: str) -> Dict[str, Any]:
    """Load a restricted YAML mapping.

    This project only needs a small subset of YAML for the IRI registry:
    nested mappings of scalar string keys to scalar string values.

    PyYAML is allowed, but not required, so this function provides a
    deterministic, dependency-free parser for that subset.

    Raises:
        ValueError: If the YAML cannot be parsed.
    """

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


def _dump_simple_yaml_mapping(data: Dict[str, Any], indent: int = 0) -> str:
    """Dump a nested mapping as YAML (restricted subset)."""

    lines: List[str] = []
    pad = " " * indent
    for key, value in data.items():
        if isinstance(value, dict):
            lines.append(f"{pad}{key}:")
            lines.append(_dump_simple_yaml_mapping(value, indent=indent + 2))
        else:
            s = str(value)
            # Quote values that contain special characters.
            if any(ch.isspace() for ch in s) or ":" in s or "#" in s:
                s = '"' + s.replace('"', '\\"') + '"'
            lines.append(f"{pad}{key}: {s}")
    return "\n".join(lines)


class IRIConfig:
    """Load and manage the Catty ontology IRI registry."""

    def __init__(self, config_path: str = ".catty/iri-config.yaml") -> None:
        """Create an IRIConfig instance.

        Args:
            config_path: Path to `.catty/iri-config.yaml`.

        Raises:
            FileNotFoundError: If the YAML config does not exist.
            ValueError: If the YAML config is malformed.
        """

        self.config_path = Path(config_path)
        self._config: JsonObject = self._load_config(self.config_path)

    def get_localhost_iri(self, ontology_name: str) -> str:
        """Return the localhost IRI for a registered ontology.

        Args:
            ontology_name: The ontology key in the config.

        Raises:
            ValueError: If the ontology is not registered.
        """

        ontology = self._get_ontology_entry(ontology_name)
        return str(ontology["localhost_iri"])

    def get_production_iri(self, ontology_name: str) -> str:
        """Return the production IRI for a registered ontology.

        Args:
            ontology_name: The ontology key in the config.

        Raises:
            ValueError: If the ontology is not registered.
        """

        ontology = self._get_ontology_entry(ontology_name)
        return str(ontology["production_iri"])

    def rebind_iri(self, rdf_content: str, ontology_name: str, target: str = "production") -> str:
        """Rebind JSON-LD content between localhost and production IRIs.

        This method performs an *in-graph* rewrite. It rewrites every occurrence of
        registered ontology IRIs so that the content becomes consistent for the
        selected target environment.

        Args:
            rdf_content: JSON-LD content as a string.
            ontology_name: The ontology being rebound (used for validation only).
            target: Either "production" or "localhost".

        Returns:
            The rebound JSON-LD as a string.

        Raises:
            ValueError: If target is invalid, ontology is unknown, or JSON is invalid.
        """

        if target not in {"production", "localhost"}:
            raise ValueError(f"Invalid target '{target}'. Expected 'production' or 'localhost'.")

        # Validate ontology exists
        self._get_ontology_entry(ontology_name)

        try:
            payload: Any = json.loads(rdf_content)
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON-LD (malformed JSON): {e}") from e

        replacements = self._build_replacements(target=target)

        def rewrite(value: Any) -> Any:
            if isinstance(value, str):
                return self._replace_many(value, replacements)
            if isinstance(value, list):
                return [rewrite(v) for v in value]
            if isinstance(value, dict):
                return {k: rewrite(v) for k, v in value.items()}
            return value

        rebound = rewrite(payload)
        return json.dumps(rebound, indent=2, ensure_ascii=False) + "\n"

    def validate_iri_format(self, iri: str) -> bool:
        """Validate that a candidate IRI looks syntactically safe.

        This is intentionally strict: it requires an explicit http/https scheme,
        a network location, and prohibits whitespace.

        Args:
            iri: Candidate IRI.

        Returns:
            True if the IRI is acceptable, otherwise False.
        """

        if not iri or any(ch.isspace() for ch in iri):
            return False

        parsed = urlparse(iri)
        if parsed.scheme not in {"http", "https"}:
            return False
        if not parsed.netloc:
            return False
        return True

    def validate_no_fabricated_iris(self, rdf_content: str) -> Dict[str, object]:
        """Detect fabricated/unauthorized IRIs inside JSON-LD.

        The scan is intentionally conservative and focuses on:
        - all `@id` values
        - all `@base` values (within `@context`)

        Compact IRIs (e.g. `catty:Logic`) are validated by prefix allow-list.

        Args:
            rdf_content: JSON-LD content as a string.

        Returns:
            A dict containing `ok`, `errors`, `warnings`, `found_iris`,
            `unauthorized_iris`, and `base_iri`.
        """

        try:
            payload: Any = json.loads(rdf_content)
        except json.JSONDecodeError as e:
            return {
                "ok": False,
                "errors": [f"Invalid JSON-LD (malformed JSON): {e}"],
                "warnings": [],
                "found_iris": [],
                "unauthorized_iris": [],
                "base_iri": None,
            }

        base_iri = self._extract_base_iri(payload)
        id_values = self._extract_id_values(payload)

        found: List[str] = []
        unauthorized: List[str] = []
        errors: List[str] = []

        if base_iri is not None:
            found.append(base_iri)
            if not self._is_registered_base(base_iri):
                unauthorized.append(base_iri)
                errors.append(f"Unregistered @base IRI: {base_iri}")

        for raw_id in id_values:
            expanded = self._expand_id(raw_id, base_iri)
            found.append(expanded)

            if not self._is_allowed_identifier(raw_id, expanded):
                unauthorized.append(expanded)
                errors.append(f"Unauthorized @id IRI: {raw_id} (expanded: {expanded})")

        return {
            "ok": len(errors) == 0,
            "errors": errors,
            "warnings": [],
            "found_iris": sorted(set(found)),
            "unauthorized_iris": sorted(set(unauthorized)),
            "base_iri": base_iri,
        }

    def new_ontology(self, name: str, description: str) -> Dict[str, object]:
        """Create an in-memory scaffold for a new ontology JSON-LD document.

        The returned object is suitable for serializing with `json.dumps` and uses
        the configured localhost IRI pattern:
        `http://localhost:8080/ontology/{name}#`.

        Args:
            name: Ontology config key (kebab-case).
            description: Human description.

        Returns:
            A JSON-LD document as a Python dict.

        Raises:
            ValueError: If the name does not match the naming convention.
        """

        if not re.fullmatch(r"[a-z0-9]+(?:-[a-z0-9]+)*", name):
            raise ValueError(
                "Invalid ontology name. Expected kebab-case (lowercase letters, digits, hyphens)."
            )

        localhost_iri = f"{self._localhost_base_url()}{self._localhost_namespace_path()}/{name}#"

        return {
            "@context": [
                "context.jsonld",
                {"@base": localhost_iri},
            ],
            "@graph": [
                {
                    "@id": "",
                    "@type": "owl:Ontology",
                    "rdfs:label": name,
                    "rdfs:comment": description,
                }
            ],
        }

    def register_ontology(self, name: str, localhost_iri: str, production_iri: str, file_path: str) -> None:
        """Add an ontology entry to the registry and persist it to disk.

        Args:
            name: Ontology config key.
            localhost_iri: Canonical localhost base IRI.
            production_iri: Canonical production base IRI.
            file_path: Repository-relative path to the JSON-LD file.

        Raises:
            ValueError: If the entry is invalid or would overwrite an existing entry.
            FileNotFoundError: If the config cannot be found.
        """

        if name in self._ontologies:
            raise ValueError(f"Ontology '{name}' is already registered.")

        if not self.validate_iri_format(localhost_iri):
            raise ValueError(f"Invalid localhost IRI format: {localhost_iri}")
        if not self.validate_iri_format(production_iri):
            raise ValueError(f"Invalid production IRI format: {production_iri}")

        if not localhost_iri.endswith("#") or not production_iri.endswith("#"):
            raise ValueError("Ontology IRIs must end with '#'.")

        if not file_path:
            raise ValueError("file_path is required")

        ontologies = self._ontologies
        ontologies[name] = {
            "localhost_iri": localhost_iri,
            "production_iri": production_iri,
            "context_url": self._localhost_context_url(),
            "file": file_path,
        }

        self._config["ontologies"] = ontologies
        self._persist_config()

    # -----------------
    # Internal helpers
    # -----------------

    @property
    def _ontologies(self) -> Dict[str, Dict[str, Any]]:
        ontologies = self._config.get("ontologies")
        if not isinstance(ontologies, dict):
            raise ValueError("Config is missing required 'ontologies' mapping")
        return ontologies  # type: ignore[return-value]

    def _load_config(self, path: Path) -> JsonObject:
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

    def _persist_config(self) -> None:
        yaml_text = _dump_simple_yaml_mapping(self._config)
        self.config_path.write_text(yaml_text + "\n", encoding="utf-8")

    def _get_ontology_entry(self, ontology_name: str) -> Dict[str, Any]:
        ontologies = self._ontologies
        if ontology_name not in ontologies:
            known = ", ".join(sorted(ontologies.keys()))
            raise ValueError(f"Unknown ontology '{ontology_name}'. Known: {known}")

        entry = ontologies[ontology_name]
        if not isinstance(entry, dict):
            raise ValueError(f"Ontology entry '{ontology_name}' must be a mapping")
        return entry

    def _registered_bases(self) -> Set[str]:
        bases: Set[str] = set()
        for entry in self._ontologies.values():
            if isinstance(entry, dict):
                localhost = entry.get("localhost_iri")
                production = entry.get("production_iri")
                if isinstance(localhost, str):
                    bases.add(localhost)
                if isinstance(production, str):
                    bases.add(production)
        return bases

    def _registered_prefixes(self) -> Set[str]:
        return {
            "catty",
            "lao",
            "mc",
            "lattice",
            "ch",
            "ex",
            "cit",
            "cu",
            "owl",
            "rdf",
            "rdfs",
            "xsd",
            "dct",
            "prov",
            "bibo",
            "skos",
            "dbo",
            "wd",
            "math",
        }

    def _is_registered_base(self, base_iri: str) -> bool:
        return base_iri in self._registered_bases()

    def _is_allowed_identifier(self, raw_id: str, expanded: str) -> bool:
        # Blank nodes are allowed
        if raw_id.startswith("_:"):
            return True

        # Absolute IRIs must be registered or in the external allow-list
        if self.validate_iri_format(expanded):
            return self._is_allowed_absolute_iri(expanded)

        # Compact IRIs must use a known prefix
        if ":" in raw_id and not raw_id.startswith("http:") and not raw_id.startswith("https:"):
            prefix = raw_id.split(":", 1)[0]
            return prefix in self._registered_prefixes()

        # Relative identifiers become IRIs via @base
        return True

    def _is_allowed_absolute_iri(self, iri: str) -> bool:
        for base in self._registered_bases():
            if iri.startswith(base):
                return True

        allowed_external_prefixes = (
            "http://www.w3.org/",
            "https://www.w3.org/",
            "http://purl.org/",
            "https://purl.org/",
            "http://dbpedia.org/",
            "https://dbpedia.org/",
            "http://www.wikidata.org/",
            "https://www.wikidata.org/",
            "http://doi.org/",
            "https://doi.org/",
            "http://en.wikipedia.org/",
            "https://en.wikipedia.org/",
            "http://arxiv.org/",
            "https://arxiv.org/",
            "http://metavacua.github.io/",
            "https://metavacua.github.io/",
        )
        return iri.startswith(allowed_external_prefixes)

    def _extract_id_values(self, payload: Any) -> List[str]:
        ids: List[str] = []

        def walk(node: Any) -> None:
            if isinstance(node, dict):
                if "@id" in node and isinstance(node["@id"], str):
                    ids.append(node["@id"])
                for v in node.values():
                    walk(v)
            elif isinstance(node, list):
                for v in node:
                    walk(v)

        walk(payload)
        return ids

    def _extract_base_iri(self, payload: Any) -> Optional[str]:
        if not isinstance(payload, dict):
            return None

        ctx = payload.get("@context")
        if ctx is None:
            return None

        # @context can be a dict, list, or string.
        if isinstance(ctx, dict):
            base = ctx.get("@base")
            return base if isinstance(base, str) else None

        if isinstance(ctx, list):
            for item in ctx:
                if isinstance(item, dict) and isinstance(item.get("@base"), str):
                    return str(item["@base"])
            return None

        # string contexts cannot carry @base
        return None

    def _expand_id(self, raw_id: str, base_iri: Optional[str]) -> str:
        # Blank nodes
        if raw_id.startswith("_:"):
            return raw_id

        if self.validate_iri_format(raw_id):
            return raw_id

        # Compact IRI
        if ":" in raw_id and not raw_id.startswith("http:") and not raw_id.startswith("https:"):
            return raw_id

        if base_iri is None:
            return raw_id

        # JSON-LD base resolution for simple fragment bases is string concatenation.
        return f"{base_iri}{raw_id}"

    def _build_replacements(self, target: str) -> List[Tuple[str, str]]:
        replacements: List[Tuple[str, str]] = []

        for entry in self._ontologies.values():
            if not isinstance(entry, dict):
                continue

            localhost = entry.get("localhost_iri")
            production = entry.get("production_iri")
            if not isinstance(localhost, str) or not isinstance(production, str):
                continue

            if target == "production":
                replacements.append((localhost, production))
            else:
                replacements.append((production, localhost))

        # Context URL rebinding as well
        localhost_ctx = self._localhost_context_url()
        production_ctx = self._production_context_url()

        relative_contexts = ("context.jsonld", "./context.jsonld")

        if target == "production":
            replacements.append((localhost_ctx, production_ctx))
            for rc in relative_contexts:
                replacements.append((rc, production_ctx))
        else:
            replacements.append((production_ctx, localhost_ctx))
            for rc in relative_contexts:
                replacements.append((rc, localhost_ctx))

        # Longer strings first prevents partial replacements.
        replacements.sort(key=lambda p: len(p[0]), reverse=True)
        return replacements

    def _replace_many(self, s: str, replacements: Sequence[Tuple[str, str]]) -> str:
        out = s
        for src, dst in replacements:
            out = out.replace(src, dst)
        return out

    def _localhost_base_url(self) -> str:
        localhost = self._config.get("localhost")
        if not isinstance(localhost, dict) or not isinstance(localhost.get("base_url"), str):
            raise ValueError("Config is missing localhost.base_url")
        return str(localhost["base_url"]).rstrip("/")

    def _localhost_namespace_path(self) -> str:
        localhost = self._config.get("localhost")
        if not isinstance(localhost, dict) or not isinstance(localhost.get("namespace_path"), str):
            raise ValueError("Config is missing localhost.namespace_path")
        return str(localhost["namespace_path"]).rstrip("/")

    def _production_base_url(self) -> str:
        prod = self._config.get("production")
        if not isinstance(prod, dict) or not isinstance(prod.get("base_url"), str):
            raise ValueError("Config is missing production.base_url")
        return str(prod["base_url"]).rstrip("/")

    def _production_namespace_path(self) -> str:
        prod = self._config.get("production")
        if not isinstance(prod, dict) or not isinstance(prod.get("namespace_path"), str):
            raise ValueError("Config is missing production.namespace_path")
        return str(prod["namespace_path"]).rstrip("/")

    def _localhost_context_url(self) -> str:
        return f"{self._localhost_base_url()}{self._localhost_namespace_path()}/context.jsonld"

    def _production_context_url(self) -> str:
        return f"{self._production_base_url()}{self._production_namespace_path()}/context.jsonld"


if __name__ == "__main__":  # pragma: no cover
    import argparse
    import sys

    parser = argparse.ArgumentParser(description="Rebind an ontology JSON-LD file between localhost and production IRIs")
    parser.add_argument("--config", default=".catty/iri-config.yaml", help="Path to iri-config.yaml")
    parser.add_argument("--ontology", required=True, help="Ontology key to validate")
    parser.add_argument("--target", choices=["localhost", "production"], default="production")
    parser.add_argument("--input", required=True, help="Input JSON-LD file")
    parser.add_argument("--output", required=True, help="Output JSON-LD file")
    args = parser.parse_args()

    cfg = IRIConfig(config_path=args.config)
    content = Path(args.input).read_text(encoding="utf-8")
    rebound = cfg.rebind_iri(content, ontology_name=args.ontology, target=args.target)
    Path(args.output).write_text(rebound, encoding="utf-8")

    sys.exit(0)
