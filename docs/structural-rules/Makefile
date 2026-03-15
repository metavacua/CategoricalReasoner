# Makefile for Structural Rules Monograph
# Supports: PDF generation, standalone theorems, HTML conversion
# Error handling: All failures propagate with meaningful context

SHELL := /bin/bash
.SHELLFLAGS := -euo pipefail -c

TEX := main.tex
PDF := main.pdf
LATEX := pdflatex
LATEX_FLAGS := -interaction=nonstopmode -halt-on-error -file-line-error

# Directory structure
PART_DIRS := part-weakening part-contraction part-exchange
THM_DIRS := $(foreach part,$(PART_DIRS),$(wildcard $(part)/*/*/thm))

# Find all theorem files
THM_FILES := $(foreach dir,$(THM_DIRS),$(wildcard $(dir)/*.tex))
THM_PDFS := $(patsubst %.tex,%.pdf,$(THM_FILES))

# HTML output directory (avoid 'html' to prevent target collision)
HTML_OUTPUT := html

# Colors for output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[1;33m
NC := \033[0m # No Color

# =============================================================================
# Main Targets
# =============================================================================

.PHONY: all clean view theorems html test test-tex test-structure list-theorems help check-tools check-html-tools

all: check-tools $(PDF)
	@echo -e "$(GREEN)✓ Build successful: $(PDF)$(NC)"

# Check required tools are available
check-tools:
	@command -v $(LATEX) >/dev/null 2>&1 || { \
	    echo -e "$(RED)✗ Error: $(LATEX) not found$(NC)" >&2; \
	    echo "Please install a LaTeX distribution (e.g., texlive)" >&2; \
	    exit 1; \
	}
	@echo -e "$(GREEN)✓ LaTeX toolchain available$(NC)"

# Main monograph PDF
$(PDF): $(TEX) preamble.tex thm-preamble.tex $(wildcard part-*/*.tex) $(wildcard part-*/*/*/theorems/*.tex)
	@echo "Building main monograph..."
	$(LATEX) $(LATEX_FLAGS) $(TEX) || { \
	    echo -e "$(RED)✗ First pass failed$(NC)" >&2; \
	    exit 1; \
	}
	$(LATEX) $(LATEX_FLAGS) $(TEX) || { \
	    echo -e "$(RED)✗ Second pass failed$(NC)" >&2; \
	    exit 1; \
	}
	@test -f $(PDF) || { \
	    echo -e "$(RED)✗ PDF not generated$(NC)" >&2; \
	    exit 1; \
	}

# =============================================================================
# Theorem Compilation
# =============================================================================

theorems: check-tools $(THM_PDFS)
	@if [ -n "$(THM_PDFS)" ]; then \
	    echo -e "$(GREEN)✓ Compiled $(words $(THM_PDFS)) theorem documents$(NC)"; \
	else \
	    echo -e "$(YELLOW)⚠ No theorem files found$(NC)"; \
	fi

# Pattern rule for building individual theorems
.PRECIOUS: %.pdf
part-%/chap-%/sec-%/theorems/%.pdf: part-%/chap-%/sec-%/theorems/%.tex thm-preamble.tex
	@echo "Building theorem: $@"
	@cd $(dir $<) && { \
	    $(LATEX) $(LATEX_FLAGS) $(notdir $<) || { \
	        echo -e "$(RED)✗ Theorem compilation failed: $<$(NC)" >&2; \
	        exit 1; \
	    }; \
	    rm -f *.aux *.bbl *.bcf *.blg *.fdb_latexmk *.fls *.lof *.log *.lot \
	        *.nav *.out *.run.xml *.snm *.synctex.gz *.toc *.vrb; \
	}
	@test -f $@ || { \
	    echo -e "$(RED)✗ PDF not generated: $@$(NC)" >&2; \
	    exit 1; \
	}
	@echo -e "$(GREEN)✓ Built: $@$(NC)"

# Build a specific theorem by ID
theorem-%: check-tools
	@THEMFILE=$$(find . -name "thm-$*.tex" -print -quit); \
	if [ -z "$$THEMFILE" ]; then \
	    echo -e "$(RED)✗ Theorem 'thm-$*.tex' not found$(NC)" >&2; \
	    exit 1; \
	fi; \
	echo "Building theorem: $$THEMFILE"; \
	cd $$(dirname $$THEMFILE) && { \
	    $(LATEX) $(LATEX_FLAGS) $$(basename $$THEMFILE) || { \
	        echo -e "$(RED)✗ Theorem compilation failed$(NC)" >&2; \
	        exit 1; \
	    }; \
	    rm -f *.aux *.log; \
	}; \
	echo -e "$(GREEN)✓ Theorem built successfully$(NC)"

# List all available theorems
list-theorems:
	@echo "Available theorem files:"
	@THEMS=$$(find . -path "*/theorems/*.tex" -name "thm-*.tex" 2>/dev/null | sed 's|^\./||' | sort); \
	if [ -z "$$THEMS" ]; then \
	    echo "  (none found)"; \
	else \
	    echo "$$THEMS" | while read line; do echo "  $$line"; done; \
	fi

# =============================================================================
# HTML Generation (requires tex4ht or pandoc)
# =============================================================================

# Check for HTML conversion tools and set HTML_TOOL
check-html-tools:
	@if ! command -v pandoc >/dev/null 2>&1 && ! command -v make4ht >/dev/null 2>&1; then \
	    echo -e "$(RED)✗ Error: No HTML conversion tool found$(NC)" >&2; \
	    echo "Please install pandoc or tex4ht (make4ht)" >&2; \
	    exit 1; \
	fi

# Determine which HTML tool to use (evaluated at runtime in recipe)
HTML_TOOL_CMD = if command -v make4ht >/dev/null 2>&1; then echo "make4ht"; else echo "pandoc"; fi

html: check-html-tools $(HTML_OUTPUT)/index.html $(HTML_OUTPUT)/style.css
	@echo -e "$(GREEN)✓ HTML generation complete$(NC)"

html-dir:
	@mkdir -p $(HTML_OUTPUT)

$(HTML_OUTPUT)/index.html: $(PDF) | html-dir
	@HTML_TOOL=$$($(HTML_TOOL_CMD)); \
	echo "Generating HTML using $$HTML_TOOL..."; \
	if [ "$$HTML_TOOL" = "pandoc" ]; then \
	    pandoc -s $(TEX) -o $(HTML_OUTPUT)/index.html \
	        --mathjax \
	        --template=default \
	        --css=style.css \
	        --toc \
	        --metadata title="Structural Rules Monograph" \
	        --metadata author="Ian Douglas Lawrence Norman McLean" || { \
	            echo -e "$(RED)✗ Pandoc HTML conversion failed$(NC)" >&2; \
	            exit 1; \
	        }; \
	else \
	    make4ht $(TEX) "mathml" -d $(HTML_OUTPUT) || { \
	        echo -e "$(RED)✗ make4ht HTML conversion failed$(NC)" >&2; \
	        exit 1; \
	    }; \
	fi; \
	test -f $(HTML_OUTPUT)/index.html || { \
	    echo -e "$(RED)✗ HTML file not generated$(NC)" >&2; \
	    exit 1; \
	}

# CSS for HTML output
$(HTML_OUTPUT)/style.css: | html-dir
	@echo "Creating stylesheet..."
	@echo 'body { max-width: 800px; margin: 0 auto; padding: 20px; font-family: serif; line-height: 1.6; }' > $@
	@echo 'h1, h2, h3 { color: #333; }' >> $@
	@echo '.theorem { border-left: 3px solid #0066cc; padding-left: 15px; margin: 15px 0; }' >> $@
	@echo '.proof { font-style: italic; }' >> $@
	@echo '.sequent { font-family: monospace; }' >> $@

# =============================================================================
# Testing and Validation
# =============================================================================

test: test-structure test-tex
	@echo -e "$(GREEN)✓ All tests passed$(NC)"

test-tex: check-tools
	@echo "Testing TeX compilation..."
	@$(LATEX) $(LATEX_FLAGS) -draftmode $(TEX) > /dev/null 2>&1 || { \
	    echo -e "$(RED)✗ Main document compilation failed$(NC)" >&2; \
	    exit 1; \
	}
	@echo -e "$(GREEN)  ✓ Main document compiles$(NC)"
	@FAILED=0; \
	for thm in $(THM_FILES); do \
	    cd $$(dirname $$thm) && { \
	        $(LATEX) $(LATEX_FLAGS) -draftmode $$(basename $$thm) > /dev/null 2>&1 && \
	        echo -e "$(GREEN)  ✓ $$(basename $$thm) compiles$(NC)" || \
	        { echo -e "$(RED)  ✗ $$(basename $$thm) compilation failed$(NC)" >&2; FAILED=1; }; \
	        rm -f *.aux *.log; \
	        cd - > /dev/null; \
	    }; \
	done; \
	exit $$FAILED

test-structure:
	@echo "Testing directory structure..."
	@ERRORS=0; \
	for dir in $(PART_DIRS); do \
	    if [ ! -d $$dir ]; then \
	        echo -e "$(RED)  ✗ $$dir missing$(NC)" >&2; \
	        ERRORS=1; \
	    fi; \
	done; \
	if [ ! -f AGENTS.md ]; then \
	    echo -e "$(RED)  ✗ AGENTS.md missing$(NC)" >&2; \
	    ERRORS=1; \
	fi; \
	if [ $$ERRORS -eq 0 ]; then \
	    echo -e "$(GREEN)  ✓ All required directories present$(NC)"; \
	    echo -e "$(GREEN)  ✓ Structure validation complete$(NC)"; \
	else \
	    exit 1; \
	fi

# =============================================================================
# Cleanup
# =============================================================================

clean:
	@rm -f \
	    *.aux *.bbl *.bcf *.blg *.fdb_latexmk *.fls *.lof *.log *.lot \
	    *.nav *.out *.run.xml *.snm *.synctex.gz *.toc *.vrb \
	    $(PDF)
	@find . -path "*/theorems/*.aux" -delete 2>/dev/null || true
	@find . -path "*/theorems/*.log" -delete 2>/dev/null || true
	@find . -path "*/theorems/*.pdf" -delete 2>/dev/null || true
	@echo -e "$(GREEN)✓ Clean complete$(NC)"

clean-all: clean
	@rm -rf $(HTML_OUTPUT)
	@echo -e "$(GREEN)✓ Full clean complete$(NC)"

# =============================================================================
# Viewing
# =============================================================================

view: all
	@if [ ! -f $(PDF) ]; then \
	    echo -e "$(RED)✗ PDF not found: $(PDF)$(NC)" >&2; \
	    exit 1; \
	fi
	@if command -v xdg-open >/dev/null 2>&1; then \
	    xdg-open $(PDF) >/dev/null 2>&1 & \
	elif command -v open >/dev/null 2>&1; then \
	    open $(PDF); \
	else \
	    echo "Built $(PDF). Open it with your PDF viewer."; \
	fi

# =============================================================================
# Help
# =============================================================================

help:
	@echo "Structural Rules Monograph - Build System"
	@echo ""
	@echo "Targets:"
	@echo "  all            - Build the main monograph PDF (default)"
	@echo "  theorems       - Build all standalone theorem PDFs"
	@echo "  theorem-ID     - Build specific theorem by ID"
	@echo "  list-theorems  - List all available theorem files"
	@echo "  html           - Generate HTML version"
	@echo "  test           - Run validation tests"
	@echo "  test-tex       - Test TeX compilation"
	@echo "  test-structure - Test directory structure"
	@echo "  clean          - Remove auxiliary files"
	@echo "  clean-all      - Remove all generated files"
	@echo "  view           - Build and open PDF"
	@echo "  help           - Show this help message"
	@echo ""
	@echo "Error Handling:"
	@echo "  All build failures propagate with non-zero exit codes"
	@echo "  Missing tools are detected before build attempts"
	@echo "  Meaningful error messages with file context"
