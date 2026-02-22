# AGENTS.md - Structural Rules Part Directory

## Scope
This directory contains the hierarchical structure for the Weakening (Part I) of the Structural Rules monograph.

## Part Structure
The monograph has multiple parts:
- `part-weakening/` - Weakening structural rule (this directory)
- `part-contraction/` - Contraction structural rule (future)
- `part-exchange/` - Exchange structural rule (future)

## Chapter Context Configurations

Each part contains chapters organized by context configuration:

| Directory | LHS Context | RHS Context | Example Logic |
|-----------|-------------|-------------|---------------|
| `chap-full-full` | Full (unrestricted) | Full (unrestricted) | Classical LK |
| `chap-full-single` | Full | Single | Intuitionistic LJ |
| `chap-single-full` | Single | Full | Dual intuitionistic |
| `chap-single-single` | Single | Single | Minimal logic |

**Important**: These chapters are NOT named after logics. LK, LJ, etc. are examples of logics that fit a context configuration, not definitions of the chapter. The chapter defines the structural rule under investigation in a given context configuration.

## TeX Assembly Algorithm

This section describes the procedural algorithm for assembling the TeX Part from the chapter, section, and subsection files.

### Assembly Algorithm (Pseudocode)

```
FUNCTION AssembleWeakeningPart(output_file):
    # Initialize the main document
    INITIALIZE latex_document with preamble
    
    # Process each chapter in order
    FOR each chapter_dir IN ["chap-full-full",
                             "chap-full-single",
                             "chap-single-full",
                             "chap-single-single"]:
        
        chapter_file = READ chapter_dir + "/chapter.md"  # Quarto processes .md
        chapter_title = EXTRACT_TITLE(chapter_file)
        
        ADD "\chapter{" + chapter_title + "}" TO latex_document
        
        # Process sections within chapter
        FOR each section_dir IN ["sec-lhs-rules",
                                 "sec-rhs-rules"]:
            
            section_file = READ chapter_dir + "/" + section_dir + "/section.md"
            section_title = EXTRACT_TITLE(section_file)
            
            ADD "\section{" + section_title + "}" TO latex_document
            ADD section_content TO latex_document
            
            # Process subsections within section (if any)
            FOR each subsection_dir IN LIST_SUBDIRS(chapter_dir + "/" + section_dir):
                subsection_file = READ subsection_dir + "/subsection.md"
                subsection_title = EXTRACT_TITLE(subsection_file)
                
                ADD "\subsection{" + subsection_title + "}" TO latex_document
                ADD subsection_content TO latex_document
    
    # Add bibliography and indices
    ADD bibliography TO latex_document
    
    # Write final document
    WRITE latex_document TO output_file
    
    RETURN output_file
```

### File Resolution Order

1. **Part-level files**: `part-weakening/part.md` provides structure and content
2. **Chapter files**: `chap-*/chapter.md` - Markdown processed by Quarto
3. **Section files**: `chap-*/sec-*/section.md`
4. **Subsection files**: `chap-*/sec-*/subsec-*/subsection.md`

### Compilation Process (Quarto)

```
quarto render docs/structural-rules/
```

Quarto handles the full pipeline from `.md` → `.tex` → PDF.

## Core Constraints
- **Formats**: Primary `.md`, compiled via Quarto. Also `*.tex` for compatibility.
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`, `thm-*`, `def-*`, `lem-*`.
- **Content Files**: Should be a page to a few pages of text minimum.
- **Content Structure**: 
  - `chap-*/` - Contains chapter-level content
  - `sec-*/` - Contains section-level content (lhs-rules, rhs-rules)

## Exchange and Contraction Independence

**IMPORTANT - Applies to ALL chapters in this part**:

This part is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied in this part.

## Special Consideration: Symmetric Single Antecedent and Succedent

The "chap-single-single" (Symmetric Single Antecedent and Single Succedent) chapter requires special handling:

**Asymmetric Weakening Rule**: Weakening on one side EXCLUDES weakening on the other side:
- **If LHS can be weakened**, then RHS CANNOT be weakened
- **If RHS can be weakened**, then LHS CANNOT be weakened
- This prevents the derivation of the empty sequent

If we naively allow weakening arbitrarily on both sides, we would derive the empty sequent, meaning the empty sequent is derivable in all extensions including LJ and LK. Instead, specific structural restrictions ensure that at least one formula is valid on LHS or RHS.

This corresponds to **Minimal Logic** (*Minimalkalkül*) by Ingebrigt Johansson (1936).

## Theorem Scoping
Theorems in this part follow scoping rules:
- Root theorems in `docs/structural-rules/theorems/` apply to all parts
- Part-level theorems (in `part-weakening/theorems/`) apply to all chapters in this part
- Chapter theorems apply only within that chapter

## Validation
All artifacts must pass automated validation against the structure schema.

## See Also
- `/docs/structural-rules/AGENTS.md` - Monograph policies
- `/docs/structural-rules/theorems/AGENTS.md` - Theorem policies
