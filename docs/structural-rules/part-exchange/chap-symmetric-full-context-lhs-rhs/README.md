# Chapter: Symmetric Exchange with Full Context on LHS and RHS

## Scope
This chapter examines symmetric exchange where formulas on both the left-hand side (antecedent) and right-hand side (succedent) can be reordered. This represents the classical approach to exchange.

## Content

This chapter covers:
- **Full Exchange**: Classical exchange allowing formula reordering on both LHS and RHS
- **Linear Exchange**: Exchange in linear logic contexts
- **Affine Exchange**: Exchange in affine logic
- **Relevant Exchange**: Exchange in relevant logic contexts

## Sequent Calculus Representation

In classical sequent calculus (LK), symmetric exchange is expressed as:
- Left exchange: $\dfrac{\Gamma, A, B, \Delta \vdash \Sigma}{\Gamma, B, A, \Delta \vdash \Sigma}$
- Right exchange: $\dfrac{\Gamma \vdash \Delta, A, B, \Sigma}{\Gamma \vdash \Delta, B, A, \Sigma}$

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../../part-weakening/chap-symmetric-full-context-lhs-rhs/` - Corresponding weakening chapter
- `../../part-contraction/chap-symmetric-full-context-lhs-rhs/` - Corresponding contraction chapter
