# Subsection: Linear Weakening LHS

## Scope
Left-hand side weakening in **Classical Linear Logic** (CLL), where weakening is restricted to formulas marked with the exponential modality `?` (why-not).

## Research Context
From DBpedia/SPARQL research: Linear logic differs from classical logic by controlling structural rules through modalities. The exponential modality `?A` allows weakening and contraction, while linear formulas `A` cannot be weakened.

## Rule: Exponential Weakening (Left)

In Classical Linear Logic, weakening on the left is available only for `?`-modalized formulas:

\[
\infer[?W_L]{\Gamma, ?A \vdash \Delta}{\Gamma \vdash \Delta}
\]

## Proof of Admissibility

The rule is admissible by the definition of the `?` modality:

\[
\infer[?L]{\Gamma, ?A \vdash \Delta}{
    \infer[W_L]{\Gamma, A \vdash \Delta}{\Gamma \vdash \Delta}}
\]

Where `?L` is the left rule for the exponential modality, allowing dereliction.

## Comparison with Classical LK

| Feature | Classical LK | Classical Linear Logic |
|---------|--------------|------------------------|
| Weakening | Free for all formulas | Only for `?A` |
| Modality | None required | `?` (why-not) |
| Resource view | Unlimited | Linear by default |

## Relationship to Other Variants

- **Full Weakening**: LK allows weakening of any formula
- **Affine Weakening**: Weakening without contraction, no modality needed
- **Linear Weakening**: This subsection - weakening via `?` modality
- **Relevant Weakening**: No weakening permitted

## Categorical Semantics

In *-autonomous categories with exponential comonad `!` and monad `?`:
- The functor `?` provides the weakening structure
- The counit `?A → A` corresponds to dereliction
- The comultiplication `?A → ??A` corresponds to digging

## See Also
- `../subsec-full-weakening-lhs/` - Classical unrestricted weakening
- `../subsec-affine-weakening-lhs/` - Weakening without contraction
- `../../sec-rhs-rules/subsec-linear-weakening-rhs/` - RHS linear weakening
