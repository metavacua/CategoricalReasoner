# AGENTS.md - Structural Rules Monograph

## Scope
The `docs/structural-rules/` directory contains the monograph "Structural Rules: A Categorical Investigation." This is separate from the thesis ("Theoretical Metalinguistics").

## Separation from Thesis
This monograph is a **separate scholarly work** from the thesis in `docs/dissertation/`. They have:
- Different titles
- Different compilation targets
- Different logical arguments

## Part Structure
The monograph is organized into parts:
- `part-weakening/` - Part I: Weakening structural rule
- `part-contraction/` - Part II: Contraction structural rule (future)
- `part-exchange/` - Part III: Exchange structural rule (future)

## Chapter Context Configurations
Each part contains chapters organized by context configuration:

| Chapter | LHS Context | RHS Context | Example Logic |
|---------|--------------|--------------|---------------|
| `chap-full-full` | Full (unrestricted) | Full (unrestricted) | Classical LK |
| `chap-full-single` | Full | Single | Intuitionistic LJ |
| `chap-single-full` | Single | Full | Dual intuitionistic |
| `chap-single-single` | Single | Single | Minimal logic |

**Important**: These chapters are NOT named after logics. LK, LJ, etc. are examples of logics that fit a context configuration, not definitions of the chapter. The chapter defines the structural rule under investigation in a given context configuration.

## Theorem Scoping
Theorems in this monograph follow scoping rules by directory depth:
- Root-level theorems in `theorems/` apply to all parts
- Part-level theorems apply to all chapters in that part
- Chapter-level theorems apply within that chapter

## Core Constraints
- **Formats**: Primary `.md` (Markdown), compiled to `.tex` via Quarto
- **IDs**: Globally unique: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`
- **Content**: Mathematical rigor with proper notation
- **Quarto**: Use Quarto for compilation; `.md` files are GitHub-renderable

## Compilation
```
quarto render docs/structural-rules/
```

## Validation
All artifacts must pass validation. Use Quarto compilation and ID uniqueness checks.

## See Also
- `docs/structural-rules/part/AGENTS.md` - Part policies
- `docs/dissertation/AGENTS.md` - Thesis policies
- `docs/dev/AGENTS.md` - Development artifacts
