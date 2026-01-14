from __future__ import annotations

import importlib.util
import json
from pathlib import Path
from typing import Any, Dict

import pytest


def load_iri_config_class() -> Any:
    repo_root = Path(__file__).resolve().parents[1]
    module_path = repo_root / "scripts" / "iri-config.py"
    spec = importlib.util.spec_from_file_location("catty_iri_config", module_path)
    assert spec is not None and spec.loader is not None
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)  # type: ignore[arg-type]
    return module.IRIConfig


@pytest.fixture()
def tmp_config(tmp_path: Path) -> Path:
    cfg_dir = tmp_path / ".catty"
    cfg_dir.mkdir(parents=True)
    cfg_path = cfg_dir / "iri-config.yaml"

    cfg_path.write_text(
        """
metadata:
  version: \"1.0\"
  description: \"Test Registry\"

localhost:
  base_url: \"http://localhost:8080\"
  namespace_path: \"/ontology\"

production:
  base_url: \"https://example.com\"
  namespace_path: \"/ontology\"

ontologies:
  a:
    localhost_iri: \"http://localhost:8080/ontology/a#\"
    production_iri: \"https://example.com/ontology/a#\"
    context_url: \"http://localhost:8080/ontology/context.jsonld\"
    file: \"ontology/a.jsonld\"
  b:
    localhost_iri: \"http://localhost:8080/ontology/b#\"
    production_iri: \"https://example.com/ontology/b#\"
    context_url: \"http://localhost:8080/ontology/context.jsonld\"
    file: \"ontology/b.jsonld\"
""".lstrip(),
        encoding="utf-8",
    )

    return cfg_path


def test_load_valid_config(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert cfg.get_localhost_iri("a") == "http://localhost:8080/ontology/a#"


def test_get_localhost_iri_known_ontology(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert cfg.get_localhost_iri("b") == "http://localhost:8080/ontology/b#"


def test_get_localhost_iri_unknown_ontology(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    with pytest.raises(ValueError) as exc:
        cfg.get_localhost_iri("missing")
    assert "Unknown ontology" in str(exc.value)


def test_get_production_iri_known_ontology(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert cfg.get_production_iri("a") == "https://example.com/ontology/a#"


def test_get_production_iri_unknown_ontology(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    with pytest.raises(ValueError) as exc:
        cfg.get_production_iri("missing")
    assert "Unknown ontology" in str(exc.value)


def test_rebind_iri_localhost_to_production(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    payload: Dict[str, Any] = {
        "@context": [
            "http://localhost:8080/ontology/context.jsonld",
            {"@base": "http://localhost:8080/ontology/a#"},
        ],
        "@graph": [
            {"@id": "X", "@type": "owl:Class", "rdfs:seeAlso": "http://localhost:8080/ontology/b#Y"}
        ],
    }

    rebound = cfg.rebind_iri(json.dumps(payload), ontology_name="a", target="production")
    out = json.loads(rebound)

    assert out["@context"][1]["@base"] == "https://example.com/ontology/a#"
    assert out["@graph"][0]["rdfs:seeAlso"] == "https://example.com/ontology/b#Y"


def test_rebind_iri_production_to_localhost(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    payload: Dict[str, Any] = {
        "@context": [
            "https://example.com/ontology/context.jsonld",
            {"@base": "https://example.com/ontology/a#"},
        ],
        "@graph": [{"@id": "https://example.com/ontology/b#Y"}],
    }

    rebound = cfg.rebind_iri(json.dumps(payload), ontology_name="a", target="localhost")
    out = json.loads(rebound)

    assert out["@context"][1]["@base"] == "http://localhost:8080/ontology/a#"
    assert out["@graph"][0]["@id"] == "http://localhost:8080/ontology/b#Y"


def test_rebind_iri_preserves_relationships(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    payload: Dict[str, Any] = {
        "@context": [
            "http://localhost:8080/ontology/context.jsonld",
            {"@base": "http://localhost:8080/ontology/a#"},
        ],
        "@graph": [
            {
                "@id": "Thing",
                "related": [
                    "http://localhost:8080/ontology/b#Other",
                    {"@id": "http://localhost:8080/ontology/b#Other2"},
                ],
            }
        ],
    }

    rebound = cfg.rebind_iri(json.dumps(payload), ontology_name="a", target="production")
    out = json.loads(rebound)

    rel = out["@graph"][0]["related"]
    assert rel[0] == "https://example.com/ontology/b#Other"
    assert rel[1]["@id"] == "https://example.com/ontology/b#Other2"


def test_validate_iri_format_valid_http(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert cfg.validate_iri_format("http://example.com/x")


def test_validate_iri_format_valid_https(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert cfg.validate_iri_format("https://example.com/x")


def test_validate_iri_format_with_fragment(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert cfg.validate_iri_format("https://example.com/x#y")


def test_validate_iri_format_invalid_no_scheme(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert not cfg.validate_iri_format("example.com/x")


def test_validate_iri_format_invalid_spaces(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))
    assert not cfg.validate_iri_format("http://example.com/has space")


def test_validate_no_fabricated_iris_valid_jsonld(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    payload = {
        "@context": ["http://localhost:8080/ontology/context.jsonld", {"@base": "http://localhost:8080/ontology/a#"}],
        "@graph": [{"@id": "X"}],
    }

    res = cfg.validate_no_fabricated_iris(json.dumps(payload))
    assert res["ok"] is True
    assert res["unauthorized_iris"] == []


def test_validate_no_fabricated_iris_detects_unauthorized(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    payload = {
        "@context": ["http://localhost:8080/ontology/context.jsonld", {"@base": "http://localhost:8080/ontology/a#"}],
        "@graph": [{"@id": "http://evil.example/ontology#X"}],
    }

    res = cfg.validate_no_fabricated_iris(json.dumps(payload))
    assert res["ok"] is False
    assert any("Unauthorized" in e for e in res["errors"])


def test_validate_no_fabricated_iris_invalid_json(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    res = cfg.validate_no_fabricated_iris("{")
    assert res["ok"] is False
    assert any("Invalid JSON-LD" in e for e in res["errors"])


def test_new_ontology_generates_localhost_iri(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    scaffold = cfg.new_ontology("new-onto", "desc")
    assert scaffold["@context"][1]["@base"] == "http://localhost:8080/ontology/new-onto#"


def test_new_ontology_iri_pattern(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    with pytest.raises(ValueError):
        cfg.new_ontology("Bad Name", "desc")


def test_register_ontology_adds_to_config(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    cfg.register_ontology(
        name="c",
        localhost_iri="http://localhost:8080/ontology/c#",
        production_iri="https://example.com/ontology/c#",
        file_path="ontology/c.jsonld",
    )

    assert cfg.get_localhost_iri("c") == "http://localhost:8080/ontology/c#"


def test_register_ontology_rejects_invalid_format(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()
    cfg = IRIConfig(config_path=str(tmp_config))

    with pytest.raises(ValueError) as exc:
        cfg.register_ontology(
            name="bad",
            localhost_iri="localhost:8080/ontology/bad#",
            production_iri="https://example.com/ontology/bad#",
            file_path="ontology/bad.jsonld",
        )

    assert "Invalid localhost IRI format" in str(exc.value)


def test_register_ontology_persists_to_disk(tmp_config: Path) -> None:
    IRIConfig = load_iri_config_class()

    cfg = IRIConfig(config_path=str(tmp_config))
    cfg.register_ontology(
        name="persisted",
        localhost_iri="http://localhost:8080/ontology/persisted#",
        production_iri="https://example.com/ontology/persisted#",
        file_path="ontology/persisted.jsonld",
    )

    cfg2 = IRIConfig(config_path=str(tmp_config))
    assert cfg2.get_production_iri("persisted") == "https://example.com/ontology/persisted#"
