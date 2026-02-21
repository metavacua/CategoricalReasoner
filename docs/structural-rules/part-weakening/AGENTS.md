# AGENTS.md - Structural Rules: Part I (Weakening)

## Scope
This directory contains the hierarchical structure for the Weakening part (Part I) of the Structural Rules monograph.

## TeX Assembly Algorithm

See `../AGENTS.md` for general assembly algorithm. Part-specific assembly:

### Assembly Algorithm (Pseudocode)

```
FUNCTION AssembleWeakeningPart(output_file):
    # Initialize the main document
    INITIALIZE latex_document with preamble
    
    # Add Part declaration
    ADD "\\part{Weakening}" TO latex_document
    
    # Process each chapter in order
    FOR each chapter_dir IN ["chap-symmetric-full-context-lhs-rhs",
                             "chap-asymmetric-full-context-lhs-single-succedent",
                             "chap-asymmetric-single-antecedent-full-context-rhs",
                             "chap-symmetric-single-antecedent-single-succedent"]:
        
        chapter_file = READ chapter_dir + "/chapter.tex"  # If exists
        chapter_title = EXTRACT_TITLE(chapter_file)
        
        ADD "\\chapter{" + chapter_title + "}" TO latex_document
        
        # Process sections within chapter
        FOR each section_dir IN ["sec-full-weakening",
                                 "sec-linear-weakening",
                                 "sec-affine-weakening",
                                 "sec-relevant-weakening"]:
            
            section_file = READ chapter_dir + "/" + section_dir + "/section.tex"  # If exists
            section_title = EXTRACT_TITLE(section_file)
            
            ADD "\\section{" + section_title + "}" TO latex_document
            ADD section_content TO latex_document
            
            # Process subsections within section (if any)
            FOR each subsection_dir IN LIST_SUBDIRS(chapter_dir + "/" + section_dir):
                subsection_file = READ subsection_dir + "/subsection.tex"
                subsection_title = EXTRACT_TITLE(subsection_file)
                
                ADD "\\subsection{" + subsection_title + "}" TO latex_document
                ADD subsection_content TO latex_document
    
    # Write final document
    WRITE latex_document TO output_file
    
    RETURN output_file
```

## Core Constraints
See `../AGENTS.md` for shared constraints:
- **Formats**: `*.md`, `*.tex`
- **IDs**: Globally unique following patterns: `sec-*`, `subsec-*`
- **TeX Files**: Minimum 1 page per subsection
- **Content Structure**: `chap-*/` chapters, `sec-*/` sections

## Special Consideration: Symmetric Single Antecedent and Succedent

The "Symmetric Weakening with Single Antecedent and Single Succedent" chapter requires special handling:

**Asymmetric Weakening Rule**: Weakening on one side EXCLUDES weakening on the other side:
- **If LHS can be weakened**, then RHS CANNOT be weakened
- **If RHS can be weakened**, then LHS CANNOT be weakened
- This prevents the derivation of the empty sequent

If we naively allow weakening arbitrarily on both sides, we would derive the empty sequent, meaning the empty sequent is derivable in all extensions including LJ and LK. Instead, specific structural restrictions ensure that at least one formula is valid on LHS or RHS.

This structure is related to **Minimal Logic** (*Minimalkalkül*) by Ingebrigt Johansson (1936), though distinct from it.

## Weakening Content Requirements

This part covers:
1. **Symmetric Full Context (Classical LK)**: Both LHS and RHS can be freely extended
2. **Asymmetric Full Context LHS / Single Succedent (Intuitionistic LJ)**: LHS unrestricted, RHS restricted to at most one formula
   - Note: RHS "at most one" relates to $\bot$ (bottom) and explosion principle
3. **Asymmetric Single Antecedent / Full Context RHS (Dual Intuitionistic LDJ)**: LHS restricted to at most one formula, RHS unrestricted
   - Note: LHS "at most one" relates to $\top$ (top) and dual logic to Minimalkalkül
4. **Symmetric Single Antecedent and Single Succedent** (related to Minimal Logic): Both restricted with asymmetric weakening rule

Each chapter contains four sections:
- **Full Weakening**: Classical weakening allowing arbitrary formula introduction
- **Linear Weakening**: Resource-sensitive via exponential modality
- **Affine Weakening**: Weakening admitted, contraction rejected
- **Relevant Weakening**: Weakening entirely rejected

Additional topics:
- Additive vs multiplicative weakening via linear logic translation
- Sub-intuitionistic and sub-dual-intuitionistic logics
- Absence of weakening on LHS/RHS/both corresponding to quantum theorems
- Logical explosion principle and its dependence on weakening
- LFI and linear modalities involvement
- Paola Zizzi's work on absence of weakening and no erasure

## Exchange and Contraction Independence

**IMPORTANT - Applies to ALL chapters in this part**:

This part is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied in this part.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../AGENTS.md` - Complete directory structure and shared references
- `../README.md` - Monograph overview
- `../part-contraction/AGENTS.md` - Part II: Contraction
- `../part-exchange/AGENTS.md` - Part III: Exchange
