# AGENTS.md - Catty Operational Model

## Scope
The `.catty/` directory contains machine-readable operational specifications: `operations.yaml` (artifact/task registry), `phases.yaml` (dependency graph), and `validation/validate.py` (unified validation framework).

## Core Constraints
- **Task Execution**: Parse `operations.yaml` for artifact specs and task definitions. Resolve dependencies via `phases.yaml` before execution. Follow operational descriptions verbatim.
- **Validation**: Execute `python validation/validate.py --artifact <id>` or `--task <id>`. Validation covers: file existence, syntax parsing, required content presence, schema/SHACL compliance, acceptance criteria.
- **Artifact Types**: JSON-LD/Turtle (validate RDF syntax + SHACL), LaTeX (syntax + compilation), Markdown (structure), Python (syntax + imports).
- **Dependencies**: Check `depends_on` lists before task execution. Execute prerequisite tasks for missing artifacts.
- **Acceptance Criteria**: All criteria are boolean tests (e.g., "file exists", "validates against SHACL"). No subjective quality assessments.

## Validation
Run `python validation/validate.py --all` for batch validation. Fix reported errors and re-validate until all constraints pass.
