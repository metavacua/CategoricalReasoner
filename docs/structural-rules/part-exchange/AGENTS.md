# AGENTS.md - Structural Rules: Part III (Exchange)

## Scope
This directory contains the hierarchical structure for the Exchange part (Part III) of the Structural Rules monograph.

## TeX Assembly Algorithm

### Assembly Algorithm (Pseudocode)

```
FUNCTION AssembleExchangePart(output_file):
    # Initialize the main document
    INITIALIZE latex_document with preamble
    
    # Add Part declaration
    ADD "\\part{Exchange}" TO latex_document
    
    # Process each chapter in order
    FOR each chapter_dir IN ["chap-symmetric-full-context-lhs-rhs",
                             "chap-asymmetric-full-context-lhs-single-succedent",
                             "chap-asymmetric-single-antecedent-full-context-rhs",
                             "chap-symmetric-single-antecedent-single-succedent"]:
        
        chapter_file = READ chapter_dir + "/chapter.tex"  # If exists
        chapter_title = EXTRACT_TITLE(chapter_file)
        
        ADD "\\chapter{" + chapter_title + "}" TO latex_document
        
        # Process sections within chapter
        FOR each section_dir IN ["sec-full-exchange",
                                 "sec-linear-exchange",
                                 "sec-affine-exchange",
                                 "sec-relevant-exchange"]:
            
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

### File Resolution Order

1. **Part-level files**: `part-exchange.tex` provides structure and `\input{}` directives
2. **Chapter files**: `chap-*/chapter.tex` or direct content
3. **Section files**: `chap-*/sec-*/section.tex`
4. **Subsection files**: `chap-*/sec-*/subsec-*/subsection.tex`

### Key TeX Commands Used

- `\part{Exchange}` - Part declaration
- `\chapter{...}` - Chapter declarations
- `\section{...}` - Section declarations  
- `\subsection{...}` - Subsection declarations
- `\input{path}` - Include external TeX files
- `\label{...}` and `\ref{...}` - Cross-referencing

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`. Create subdirectories as needed.
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **TeX Files**: Subsections should be a page to a few pages of text minimum.
- **Content Structure**: 
  - `chap-*/` - Contains chapter-level content
  - `sec-*/` - Contains section-level content

## Content Requirements

This part covers:
1. **Symmetric Full Context (Classical LK)**: Both LHS and RHS have exchange
2. **Asymmetric Full Context LHS / Single Succedent (Intuitionistic LJ)**: LHS exchange, RHS single formula
3. **Asymmetric Single Antecedent / Full Context RHS (Dual Intuitionistic LDJ)**: LHS single formula, RHS exchange
4. **Symmetric Single Antecedent and Single Succedent (Minimal Logic)**: Both restricted

Each chapter contains four sections:
- **Full Exchange**: Classical exchange
- **Linear Exchange**: Exchange in linear logic
- **Affine Exchange**: Exchange in affine logic
- **Relevant Exchange**: Exchange in relevance logic

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../part-weakening/AGENTS.md` - Part I: Weakening
- `../part-contraction/AGENTS.md` - Part II: Contraction
- `../AGENTS.md` - Parent directory
