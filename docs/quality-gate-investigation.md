# Quality Gate Investigation Report

## Scope
Configure CTO.new quality gates for the Catty repository, including CI workflow additions and guidance for CTO.new code checks and GitHub branch protection. This report documents the investigation, choices, and assumptions made.

## Files Examined
- `AGENTS.md` (repository-wide constraints)
- `CONTRIBUTING.md` (local check instructions)
- `.github/workflows/deploy.yml` (existing GitHub workflow)
- `docs/dissertation/Makefile` (LaTeX build system)
- `src/scripts/validate_rdf.py` (RDF validation)
- `src/tests/test_consistency.py` (SHACL validation)
- `src/schema/validators/validate_rdf.py` (full RDF/SHACL validator)
- `src/schema/validators/validate_tex_structure.py` (TeX structure validator)
- `src/benchmarks/run.py` (SPARQL benchmark runner)
- `src/benchmarks/queries/*.rq` (SPARQL queries)

## Patterns Found
- LaTeX build uses `make` under `docs/dissertation/`.
- TeX structure validation defaults to `thesis/chapters`, but actual content is in `docs/dissertation/chapters`.
- RDF/SHACL scripts assume `ontology/` or `src/ontology/` directories which do not exist in the repository.
- SPARQL benchmark queries target DBpedia and Wikidata, requiring non-empty results and an external network.

## Approaches Considered
1. **Single CI workflow with all gates**
   - Chosen to ensure a single, required status check in branch protection.
   - Enables shared dependency installation and consistent execution order.

2. **Separate CI jobs for build/test/benchmarks**
   - Rejected to avoid more complex branch-protection configuration and to reduce redundant setup steps.

3. **Always run RDF/SHACL validation**
   - Rejected because the repository lacks the ontology directories; a hard failure would block all PRs.
   - Implemented conditional checks to run only if the directories exist.

4. **Benchmarks optional/skippable**
   - Rejected because repository constraints require non-empty SPARQL results.
   - Implemented timeouts and explicit non-empty result enforcement to align with those constraints.

## Changes Implemented
- Added `.github/workflows/ci.yml` to run quality gates:
  - TeX structure validation with corrected path
  - LaTeX build
  - Python syntax check
  - SPARQL benchmarks (DBpedia, Wikidata)
  - Conditional RDF and SHACL validation based on directory existence
- Updated `src/benchmarks/run.py`:
  - Added request timeouts for remote SPARQL queries
  - Enforced non-empty results for both `CONSTRUCT` and `SELECT` queries

## Assumptions
- CTO.new and GitHub branch protection are configured manually; repository changes cannot apply those settings directly.
- External SPARQL endpoints are available and respond within the timeout when CI runs.
- Future ontology directories will be added under `ontology/` or `src/ontology/` and will then enable the RDF/SHACL checks.

## Decision Rationale
The selected CI configuration balances strict quality enforcement with pragmatic handling of missing ontology directories. It aligns with repository constraints that require non-empty SPARQL results and formal LaTeX validation, while ensuring CI remains actionable for current repository contents.
