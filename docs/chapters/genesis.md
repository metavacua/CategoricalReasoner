---
title: "Genesis — Where Catty Comes From"
abbrev-title: "Genesis"
msc-primary: "03B47"
msc-secondary: ["03B20", "03G30", "18D15"]
keywords: [linear logic, substructural logic, BHK interpretation, Curry-Howard]
---

# Genesis — Where Catty Comes From {#sec-genesis}

## The Resource-Sensitive Revolution {#subsec-resource-revolution}

Classical linear logic (CLL) is a locally terminal object with respect to 
intuitionistic linear logic, dual intuitionistic linear logic or co-constructive 
linear logic, and monotonic linear logic. Monotonic linear logic, defined as a 
sublogic of Multiplicative-Additive Linear Logic without negation, XOR, Bicon, 
implication (lollipop), or non-implication, is initial.

Given that linear logic extends to classical logic by inclusion of the weakening 
and contraction rules on left and right, the fact that there exists an initial 
logic with respect to LL that is not LL entails that the initial logic is also 
initial with respect to LK when LK is fixed as the terminal logic.

## Structural Rules and the Anatomy of Sequents {#subsec-structural-anatomy}

Structural rules (weakening, contraction, and exchange) serve as logical 
parameters. Catty incorporates initial logics specifically related to a sublogic 
of both Ardeshir and Vaezian's U and Sambin et al's Basic Sequent Calculus. 
The existence of U entails dualizations of U such that U and the dualizations 
share a common sublogic that excludes specifically implication, provability 
predicates, interpretation functions, non-implication, non-provability 
predicates, and non-interpretation functions.

## Constructive Foundations and the Witness Principle {#subsec-constructive-foundations}

The BHK interpretation and Kripke semantics are most directly applicable to 
intuitionistic logic, which is one logic in the coordinate space. We can dualize 
them for LDJ or co-constructive logic where we have constructive assumptions but 
non-constructive consequences in proof theoretical terms.

LDJ and co-constructive subclassical logics are more conservative in their 
non-constructive proof consequences than classical logic, and this bifurcates 
the non-constructive classes into at least two classes where the anti-theorems 
of LDJ are a strict subset of the anti-theorems of LK.

## Refutation as Co-Constructive Operation {#subsec-refutation}

Using the language of James Trafford, Catty integrates some refutations as 
co-constructive operations. There are non-constructive refutations at least in LK, 
though if we use a version of LK that is restricted to its polytime decidable 
fragment(s), then we would almost assuredly be restricting to refutations that 
are co-constructive to the corresponding constructive proofs.

Catty draws from:
- Nelson's constructive negation
- Igor Urbas's Dual-Intuitionistic Logic
- James Trafford's co-constructive logic

## The Great Unification {#subsec-unification}

The Curry-Howard-Lambek correspondence has greatest direct affinity with LJ 
and intuitionistic logic. We need to formalize an explicit dual form, and we 
need to construct a commonality between the CHK correspondence and its dual. 
Catty's DSL is a computational witness of this correspondence and its dual.

## Paraconsistent Reasoning and Philosophy {#subsec-paraconsistent}

Logics that avoid explosion (Ex Falso Quodlibet) are represented, drawing from 
the work of João Marcos, Priest, Restall, Walter Carnielli, and Paola Zizzi.

## Quantum Logic and Non-Distributivity {#subsec-quantum}

Lq is the proper "basic quantum logic" whereas the Birkhoff-Neumann logic is 
a distinct entity that is A quantum logic but not THE quantum logic, and it is 
more semi-classical than properly quantum. Zizzi's work establishes Lq as the 
first logic which is substructural, many-valued and quantum at the same time. 
Lq introduces:

- A quantum metalanguage where metalinguistic links are quantum correlations
- A quantum cut rule interpreted as quantum projective measurement
- Quantum superpositions and entanglement as logical connectives
- An EPR rule allowing simultaneous proof of entangled theorems
- The qubit theorem as logical description of optical qubit state preparation

## Category Theory as Metalanguage {#subsec-category-metalanguage}

Category theory provides the formal metalanguage for Catty's structure. 
Key references include:

- Mac Lane's foundational work on categories
- Pierce's category theory for computer science
- Lawvere's functorial semantics
