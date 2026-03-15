# Chapter: Symmetric Weakening with Single Antecedent and Single Succedent

## Scope
This chapter examines weakening in a sequent calculus structure related to Minimal Logic (Minimalkalkül), where both the LHS and RHS are restricted to single formulas. This structure is distinct from but structurally analogous to Johansson's Minimal Logic (1936).

## Content

This chapter covers:
- **Full Weakening**: Asymmetric weakening rule preventing empty sequent derivation
- **Linear Weakening**: Resource-sensitive weakening
- **Affine Weakening**: Weakening without contraction
- **Relevant Weakening**: Weakening rejected entirely

## Special Consideration: Asymmetric Weakening Rule

In this context, weakening on one side EXCLUDES weakening on the other:
- If LHS can be weakened, RHS CANNOT be weakened
- If RHS can be weakened, LHS CANNOT be weakened

This prevents the derivation of the empty sequent.

## Sequent Calculus Representation

In minimal logic, weakening is restricted to ensure at least one formula remains on LHS or RHS.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../../AGENTS.md` - Chapter naming convention and cross-part references
- `../../../part-contraction/chap-symmetric-single-antecedent-single-succedent/` - Corresponding contraction chapter
- `../../../part-exchange/chap-symmetric-single-antecedent-single-succedent/` - Corresponding exchange chapter
