# Theorems and Proofs: Affine Weakening

## Scope
This directory contains theorems and proofs specific to **affine weakening** (weakening without contraction).

## Theorem Format Requirements

### Proof Presentation
All proofs must use **sequent calculus proof trees** with:
- **Root-first decomposition**: Build proofs from conclusion to premises
- **Identity Axiom** (`Id`) or **Logical Constant** as terminal leaves
- **Open goals** explicitly labeled for incomplete branches

### Example Template

```latex
\begin{theorem}[Theorem Name]
\label{thm:identifier}
Statement of theorem in sequent notation.
\end{theorem}

\begin{proof}
Proof using root-first decomposition:

\[
\infer[\text{Rule Name}]{\text{Conclusion}}{
    \infer[\text{Rule}]{\text{Premise 1}}{
        \infer[\text{Id}]{A \vdash A}{}} &
    \text{[Open Goal: Premise 2]}}
\]
\end{proof}
```

## File Naming Convention
- `thm-{variant}-{property}.tex` - Theorem with proof
- Example: `thm-affine-weakening-admissibility.tex`

## Section Content Rule
**IMPORTANT**: This section covers **ONLY** affine weakening (weakening without contraction). 
- Do NOT include theorems about full weakening here
- Do NOT include theorems about linear or relevant weakening here
- Cross-references to other variants are acceptable, but proofs belong in their respective sections

## Compilation
Each theorem document is self-contained with minimal preamble.
Use `make theorem-{id}` to compile individual theorems.

## Validation
All theorems must:
- Use proper sequent calculus notation
- Include complete proofs or explicitly marked open goals
- Pass automated structure validation
