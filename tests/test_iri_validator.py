from __future__ import annotations

import importlib.util
import json
from pathlib import Path
from typing import Any

import pytest


def load_validator_class() -> Any:
    repo_root = Path(__file__).resolve().parents[1]
    module_path = repo_root / "schema" / "validators" / "validate_iris.py"
    spec = importlib.util.spec_from_file_location("catty_validate_iris", module_path)
    assert spec is not None and spec.loader is not None
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)  # type: ignore[arg-type]
    return module.IRIValidator


@pytest.fixture()
def tmp_registry(tmp_path: Path) -> Path:
    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    cfg.write_text(
        """
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
""".lstrip(),
        encoding="utf-8",
    )
    return cfg


def write_jsonld(
    path: Path,
    base: str,
    context: str = "http://localhost:8080/ontology/context.jsonld",
    extra_graph: Any = None,
) -> None:
    payload: dict[str, Any] = {
        "@context": [context, {"@base": base}],
        "@graph": [{"@id": "X"}],
    }
    if extra_graph is not None:
        payload["@graph"].append(extra_graph)

    path.write_text(json.dumps(payload), encoding="utf-8")


def test_validate_ontology_file_valid_localhost(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "a.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/a#")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is True


def test_validate_ontology_file_valid_production(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "a-prod.jsonld"
    write_jsonld(onto, base="https://example.com/ontology/a#", context="https://example.com/ontology/context.jsonld")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is True
    assert any("@context" in w.message for w in v.warnings)


def test_validate_ontology_file_missing_base(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "missing-base.jsonld"
    onto.write_text(json.dumps({"@context": "http://localhost:8080/ontology/context.jsonld", "@graph": []}), encoding="utf-8")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is False
    assert any("Missing @base" in e.message for e in v.errors)


def test_validate_ontology_file_unregistered_base(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "unregistered.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/unregistered#")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is False
    assert any("not registered" in e.message for e in v.errors)


def test_validate_ontology_file_fabricated_iri(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "fabricated.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/a#", extra_graph={"@id": "http://evil.example/ontology#X"})

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is False
    assert any("Unauthorized" in e.message for e in v.errors)


def test_validate_ontology_file_invalid_context(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "wrong-context.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/a#", context="http://localhost:8080/ontology/wrong.jsonld")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is True
    assert any("expected context URL" in w.message for w in v.warnings)


def test_validate_ontology_file_missing_file(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(tmp_path / "ontology" / "nope.jsonld")) is False
    assert any("not found" in e.message for e in v.errors)


def test_validate_ontology_file_invalid_json(tmp_registry: Path, tmp_path: Path) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "invalid.jsonld"
    onto.write_text("{", encoding="utf-8")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_ontology_file(str(onto)) is False
    assert any("Invalid JSON-LD" in e.message for e in v.errors)


def test_validate_all_ontologies_scans_all(tmp_path: Path) -> None:
    IRIValidator = load_validator_class()

    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    cfg.write_text(
        """
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

    write_jsonld(tmp_path / "ontology" / "a.jsonld", base="http://localhost:8080/ontology/a#")
    write_jsonld(tmp_path / "ontology" / "b.jsonld", base="http://localhost:8080/ontology/b#")

    v = IRIValidator(config_path=str(cfg))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_all_ontologies() is True


def test_validate_all_ontologies_returns_false_on_error(tmp_path: Path) -> None:
    IRIValidator = load_validator_class()

    (tmp_path / ".catty").mkdir()
    (tmp_path / "ontology").mkdir()

    cfg = tmp_path / ".catty" / "iri-config.yaml"
    cfg.write_text(
        """
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
""".lstrip(),
        encoding="utf-8",
    )

    # File has unregistered base
    write_jsonld(tmp_path / "ontology" / "a.jsonld", base="http://localhost:8080/ontology/other#")

    v = IRIValidator(config_path=str(cfg))
    v._repo_root = tmp_path  # type: ignore[attr-defined]

    assert v.validate_all_ontologies() is False


def test_report_prints_errors(tmp_registry: Path, tmp_path: Path, capsys: pytest.CaptureFixture[str]) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "unregistered.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/unregistered#")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]
    v.validate_ontology_file(str(onto))

    ok = v.report()
    captured = capsys.readouterr()

    assert ok is False
    assert "IRI validation failed" in captured.err


def test_report_prints_warnings(tmp_registry: Path, tmp_path: Path, capsys: pytest.CaptureFixture[str]) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "a.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/a#", context="http://localhost:8080/ontology/wrong.jsonld")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]
    v.validate_ontology_file(str(onto))

    v.report()
    captured = capsys.readouterr()
    assert "warnings" in captured.err


def test_report_success_message(tmp_registry: Path, tmp_path: Path, capsys: pytest.CaptureFixture[str]) -> None:
    IRIValidator = load_validator_class()
    onto = tmp_path / "ontology" / "a.jsonld"
    write_jsonld(onto, base="http://localhost:8080/ontology/a#")

    v = IRIValidator(config_path=str(tmp_registry))
    v._repo_root = tmp_path  # type: ignore[attr-defined]
    v.validate_ontology_file(str(onto))

    ok = v.report()
    captured = capsys.readouterr()
    assert ok is True
    assert "successful" in captured.out
