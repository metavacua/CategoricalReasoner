#!/usr/bin/env python3
import re
import sys
from pathlib import Path

WORKFLOW_DIR = Path(".github/workflows")


def require(condition: bool, message: str, errors: list):
    if not condition:
        errors.append(message)


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def ensure_deploy_workflow(errors: list):
    path = WORKFLOW_DIR / "deploy.yml"
    require(path.exists(), "deploy.yml missing", errors)
    if not path.exists():
        return

    text = read_text(path)
    require("pull_request:" in text, "deploy.yml missing pull_request trigger", errors)
    require("push:" in text and "- main" in text, "deploy.yml missing push trigger for main", errors)
    require("build-and-deploy:" in text, "deploy.yml missing build-and-deploy job", errors)
    require("deploy-pages:" in text, "deploy.yml missing deploy-pages job", errors)
    require("$GITHUB_WORKSPACE/site" in text, "deploy.yml missing site output path", errors)
    require("latexpand" in text and "pandoc" in text, "deploy.yml missing pandoc conversion", errors)
    require("build-and-deploy:" in text and "pull_request" in text, "build-and-deploy must run on pull_request", errors)


def ensure_codeql_workflow(errors: list):
    path = WORKFLOW_DIR / "codeql.yml"
    require(path.exists(), "codeql.yml missing", errors)


def ensure_ci_workflow(errors: list):
    path = WORKFLOW_DIR / "ci.yml"
    require(path.exists(), "ci.yml missing", errors)
    if not path.exists():
        return
    text = read_text(path)
    require("quality-gates:" in text, "ci.yml missing quality-gates job", errors)


def main():
    errors = []
    ensure_deploy_workflow(errors)
    ensure_codeql_workflow(errors)
    ensure_ci_workflow(errors)

    if errors:
        print("Workflow validation failed:")
        for error in errors:
            print(f"- {error}")
        sys.exit(1)

    print("Workflow validation passed.")


if __name__ == "__main__":
    main()
