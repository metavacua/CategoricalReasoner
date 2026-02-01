# AGENTS.md - Catty Operational Model

## Scope
The `.catty/` directory contains machine-readable operational specifications: `operations.yaml` (artifact/task registry), `phases.yaml` (dependency graph), and `validation/validate.py` (unified validation framework).

## Core Constraints
- **Task Execution**: Parse `operations.yaml` for artifact specs and task definitions. Resolve dependencies via `phases.yaml` before execution. Follow operational descriptions verbatim.
- **Validation**: Execute `python validation/validate.py --artifact <id>` or `--task <id>`. Validation covers: file existence, syntax parsing, required content presence, schema compliance, acceptance criteria.
- **Artifact Types**: LaTeX (syntax + compilation), Markdown (structure), Python (syntax + imports), JSON/YAML (syntax + schema validation).
- **Dependencies**: Check `depends_on` lists before task execution. Execute prerequisite tasks for missing artifacts.
- **Acceptance Criteria**: All criteria are boolean tests (e.g., "file exists", "validates against schema"). No subjective quality assessments.
- **Technology Note**: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## Validation
Run `python validation/validate.py --all` for batch validation. Fix reported errors and re-validate until all constraints pass.
