from __future__ import annotations

import importlib.util
import json
from pathlib import Path
from typing import Any, Dict


def load_iri_config_class() -> Any:
    repo_root = Path(__file__).resolve().parents[1]
    module_path = repo_root / "scripts" / "iri-config.py"
    spec = importlib.util.spec_from_file_location("catty_iri_config", module_path)
    assert spec is not None and spec.loader is not None
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)  # type: ignore[arg-type]
    return module.IRIConfig


def load_validator_class() -> Any:
    repo_root = Path(__file__).resolve().parents[1]
    module_path = repo_root / "schema" / "validators" / "validate_iris.py"
    spec = importlib.util.spec_from_file_location("catty_validate_iris", module_path)
    assert spec is not None and spec.loader is not None
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)  # type: ignore[arg-type]
    return module.IRIValidator


def write_config(path: Path, ontologies: Dict[str, Dict[str, str]]) -> None:
    lines = [
        'localhost:',
        '  base_url: "http://localhost:8080"',
        '  namespace_path: "/ontology"',
        '',
        'production:',
        '  base_url: "https://example.com"',
        '  namespace_path: "/ontology"',
        '',
        'ontologies:',
    ]

    for key, entry in ontologies.items():
        lines.extend(
            [
                f'  {key}:',
                f'    localhost_iri: "{entry["localhost_iri"]}"',
                f'    production_iri: "{entry["production_iri"]}"',
                '    context_url: "http://localhost:8080/ontology/context.jsonld"',
                f'    file: "{entry["file"]}"',
            ]
        )

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def test_create_new_ontology_and_validate(tmp_path: Path) -> None:
    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    write_config(
        cfg,
        {
            "new-onto": {
                "localhost_iri": "http://localhost:8080/ontology/new-onto#",
                "production_iri": "https://example.com/ontology/new-onto#",
                "file": "ontology/new-onto.jsonld",
            }
        },
    )

    IRIConfig = load_iri_config_class()
    cfg_obj = IRIConfig(config_path=str(cfg))

    scaffold = cfg_obj.new_ontology("new-onto", "A new ontology")
    onto_path = tmp_path / "ontology" / "new-onto.jsonld"
    onto_path.write_text(json.dumps(scaffold), encoding="utf-8")

    IRIValidator = load_validator_class()
    v = IRIValidator(config_path=str(cfg))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto_path)) is True


def test_register_and_validate_ontology(tmp_path: Path) -> None:
    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    write_config(
        cfg,
        {
            "a": {
                "localhost_iri": "http://localhost:8080/ontology/a#",
                "production_iri": "https://example.com/ontology/a#",
                "file": "ontology/a.jsonld",
            }
        },
    )

    IRIConfig = load_iri_config_class()
    cfg_obj = IRIConfig(config_path=str(cfg))

    scaffold = cfg_obj.new_ontology("b", "B ontology")
    (tmp_path / "ontology" / "b.jsonld").write_text(json.dumps(scaffold), encoding="utf-8")

    cfg_obj.register_ontology(
        name="b",
        localhost_iri="http://localhost:8080/ontology/b#",
        production_iri="https://example.com/ontology/b#",
        file_path="ontology/b.jsonld",
    )

    IRIValidator = load_validator_class()
    v = IRIValidator(config_path=str(cfg))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_all_ontologies() is True


def test_rebind_all_ontologies_to_production(tmp_path: Path) -> None:
    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    write_config(
        cfg,
        {
            "a": {
                "localhost_iri": "http://localhost:8080/ontology/a#",
                "production_iri": "https://example.com/ontology/a#",
                "file": "ontology/a.jsonld",
            },
            "b": {
                "localhost_iri": "http://localhost:8080/ontology/b#",
                "production_iri": "https://example.com/ontology/b#",
                "file": "ontology/b.jsonld",
            },
        },
    )

    IRIConfig = load_iri_config_class()
    cfg_obj = IRIConfig(config_path=str(cfg))

    payload = {
        "@context": ["http://localhost:8080/ontology/context.jsonld", {"@base": "http://localhost:8080/ontology/a#"}],
        "@graph": [{"@id": "A", "link": "http://localhost:8080/ontology/b#B"}],
    }

    out = json.loads(cfg_obj.rebind_iri(json.dumps(payload), ontology_name="a", target="production"))
    assert out["@context"][1]["@base"] == "https://example.com/ontology/a#"
    assert out["@graph"][0]["link"] == "https://example.com/ontology/b#B"


def test_roundtrip_localhost_to_production_to_localhost(tmp_path: Path) -> None:
    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    write_config(
        cfg,
        {
            "a": {
                "localhost_iri": "http://localhost:8080/ontology/a#",
                "production_iri": "https://example.com/ontology/a#",
                "file": "ontology/a.jsonld",
            },
        },
    )

    IRIConfig = load_iri_config_class()
    cfg_obj = IRIConfig(config_path=str(cfg))

    original = {
        "@context": ["http://localhost:8080/ontology/context.jsonld", {"@base": "http://localhost:8080/ontology/a#"}],
        "@graph": [{"@id": "X"}],
    }

    prod = cfg_obj.rebind_iri(json.dumps(original), ontology_name="a", target="production")
    back = cfg_obj.rebind_iri(prod, ontology_name="a", target="localhost")

    assert json.loads(back) == original
