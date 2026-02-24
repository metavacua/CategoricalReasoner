#!/usr/bin/env python3
import sys
from pathlib import Path
import yaml

WORKFLOW_DIR = Path(".github/workflows")


def load_yaml(path: Path):
    with path.open("r", encoding="utf-8") as handle:
        return yaml.safe_load(handle)


def require(condition: bool, message: str, errors: list):
    if not condition:
        errors.append(message)


def has_trigger(triggers, key, branch=None):
    if not triggers or key not in triggers:
        return False
    if branch is None:
        return True
    config = triggers[key]
    if isinstance(config, dict):
        branches = config.get("branches", [])
        return branch in branches
    return False


def ensure_deploy_workflow(errors: list):
    path = WORKFLOW_DIR / "deploy.yml"
    require(path.exists(), "deploy.yml missing", errors)
    if not path.exists():
        return
    data = load_yaml(path)
    triggers = data.get("on")
    require(has_trigger(triggers, "pull_request"), "deploy.yml missing pull_request trigger", errors)
    require(has_trigger(triggers, "push", "main"), "deploy.yml missing push trigger for main", errors)

    jobs = data.get("jobs", {})
    require("build-preview" in jobs, "deploy.yml missing build-preview job", errors)
    require("build-and-deploy" in jobs, "deploy.yml missing build-and-deploy job", errors)

    for job_name in ["build-preview", "build-and-deploy"]:
        job = jobs.get(job_name, {})
        steps = job.get("steps", [])
        run_steps = [step.get("run", "") for step in steps if isinstance(step, dict)]
        joined = "\n".join(run_steps)
        require("$GITHUB_WORKSPACE/site" in joined, f"{job_name} missing site output path", errors)
        require("latexpand" in joined and "pandoc" in joined, f"{job_name} missing pandoc conversion", errors)


def ensure_codeql_workflow(errors: list):
    path = WORKFLOW_DIR / "codeql.yml"
    require(path.exists(), "codeql.yml missing", errors)


def ensure_ci_workflow(errors: list):
    path = WORKFLOW_DIR / "ci.yml"
    require(path.exists(), "ci.yml missing", errors)
    if not path.exists():
        return
    data = load_yaml(path)
    jobs = data.get("jobs", {})
    require("quality-gates" in jobs, "ci.yml missing quality-gates job", errors)


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
