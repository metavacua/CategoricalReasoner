# Chapter: Symmetric Contraction with Full Context on LHS and RHS

## Scope
This chapter examines symmetric contraction where duplicate formulas on both the left-hand side (antecedent) and right-hand side (succedent) can be merged. This represents the classical approach to contraction.

## Content

This chapter covers:
- **Full Contraction**: Classical contraction allowing duplicate merging on both LHS and RHS
- **Linear Contraction**: Resource-sensitive contraction in linear logic with exponential modality
- **Affine Contraction**: Weakening without contraction (affine logic perspective)
- **Relevant Contraction**: Contraction in relevant logic contexts

## Sequent Calculus Representation

In classical sequent calculus (LK), symmetric contraction is expressed as:
- Left contraction: $\dfrac{\Gamma, A, A \vdash \Delta}{\Gamma, A \vdash \Delta}$
- Right contraction: $\dfrac{\Gamma \vdash \Delta, A, A}{\Gamma \vdash \Delta, A}$

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../../part-weakening/chap-symmetric-full-context-lhs-rhs/` - Corresponding weakening chapter
- `../../part-exchange/chap-symmetric-full-context-lhs-rhs/` - Corresponding exchange chapter
