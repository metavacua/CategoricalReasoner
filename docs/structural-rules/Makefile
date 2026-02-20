# Makefile for Structural Rules Monograph
# Supports: PDF generation, standalone theorems, HTML conversion

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

# HTML output directory
HTML_DIR := html
HTML_FILES := $(patsubst %.pdf,$(HTML_DIR)/%.html,$(PDF))

# =============================================================================
# Main Targets
# =============================================================================

.PHONY: all clean view theorems html test

all: $(PDF)

# Main monograph PDF
$(PDF): $(TEX) preamble.tex thm-preamble.tex $(wildcard part-*/*.tex) $(wildcard part-*/*/*/thm/*.tex)
	@echo "Building main monograph..."
	$(LATEX) $(LATEX_FLAGS) $(TEX)
	$(LATEX) $(LATEX_FLAGS) $(TEX)
	@echo "Build complete: $(PDF)"

# =============================================================================
# Theorem Compilation
# =============================================================================

theorems: $(THM_PDFS)
	@echo "Compiled $(words $(THM_PDFS)) theorem documents"

# Pattern rule for building individual theorems
part-%/chap-%/sec-%/thm/%.pdf: part-%/chap-%/sec-%/thm/%.tex thm-preamble.tex
	@echo "Building theorem: $@"
	@cd $(dir $<) && $(LATEX) $(LATEX_FLAGS) $(notdir $<)
	@cd $(dir $<) && rm -f \
		*.aux *.bbl *.bcf *.blg *.fdb_latexmk *.fls *.lof *.log *.lot \
		*.nav *.out *.run.xml *.snm *.synctex.gz *.toc *.vrb

# Build a specific theorem by ID
theorem-%:
	@find . -name "thm-$*.tex" -exec $(LATEX) $(LATEX_FLAGS) {} \;

# List all available theorems
list-theorems:
	@echo "Available theorem files:"
	@find . -path "*/thm/*.tex" -name "thm-*.tex" | sed 's|\.\/||' | sort

# =============================================================================
# HTML Generation (requires tex4ht or pandoc)
# =============================================================================

html: $(HTML_DIR)/index.html

# Check for HTML conversion tools
HTML_TOOL := $(shell command -v make4ht 2>/dev/null || echo "pandoc")

$(HTML_DIR):
	mkdir -p $(HTML_DIR)

$(HTML_DIR)/index.html: $(PDF) $(HTML_DIR)
	@echo "Generating HTML..."
ifeq ($(HTML_TOOL),pandoc)
	@echo "Using pandoc for HTML conversion..."
	pandoc -s $(TEX) -o $(HTML_DIR)/index.html \
		--mathjax \
		--template=default \
		--css=style.css \
		--toc \
		--metadata title="Structural Rules Monograph" \
		--metadata author="Ian Douglas Lawrence Norman McLean" 2>/dev/null || \
		echo "Note: pandoc HTML conversion requires pandoc to be installed"
else
	@echo "Using make4ht for HTML conversion..."
	make4ht $(TEX) "mathml" -d $(HTML_DIR) 2>/dev/null || \
		echo "Note: make4ht HTML conversion requires tex4ht to be installed"
endif
	@echo "HTML generation complete"

# CSS for HTML output
$(HTML_DIR)/style.css: $(HTML_DIR)
	@echo "Creating stylesheet..."
	@echo 'body { max-width: 800px; margin: 0 auto; padding: 20px; font-family: serif; line-height: 1.6; }' > $@
	@echo 'h1, h2, h3 { color: #333; }' >> $@
	@echo '.theorem { border-left: 3px solid #0066cc; padding-left: 15px; margin: 15px 0; }' >> $@
	@echo '.proof { font-style: italic; }' >> $@
	@echo '.sequent { font-family: monospace; }' >> $@

# =============================================================================
# Testing and Validation
# =============================================================================

test: test-tex test-structure

test-tex:
	@echo "Testing TeX compilation..."
	@$(LATEX) $(LATEX_FLAGS) -draftmode $(TEX) > /dev/null 2>&1 && \
		echo "  ✓ Main document compiles" || \
		echo "  ✗ Main document compilation failed"
	@for thm in $(THM_FILES); do \
		cd $$(dirname $$thm) && $(LATEX) $(LATEX_FLAGS) -draftmode $$(basename $$thm) > /dev/null 2>&1 && \
		echo "  ✓ $$(basename $$thm) compiles" || \
		echo "  ✗ $$(basename $$thm) compilation failed"; \
		rm -f *.aux *.log; \
		cd - > /dev/null; \
	done

test-structure:
	@echo "Testing directory structure..."
	@test -d part-weakening || (echo "  ✗ part-weakening missing" && exit 1)
	@test -d part-contraction || (echo "  ✗ part-contraction missing" && exit 1)
	@test -d part-exchange || (echo "  ✗ part-exchange missing" && exit 1)
	@test -f AGENTS.md || (echo "  ✗ AGENTS.md missing" && exit 1)
	@echo "  ✓ All required directories present"
	@echo "  ✓ Structure validation complete"

# =============================================================================
# Cleanup
# =============================================================================

clean:
	rm -f \
		*.aux *.bbl *.bcf *.blg *.fdb_latexmk *.fls *.lof *.log *.lot \
		*.nav *.out *.run.xml *.snm *.synctex.gz *.toc *.vrb \
		$(PDF)
	@find . -path "*/thm/*.aux" -delete
	@find . -path "*/thm/*.log" -delete
	@find . -path "*/thm/*.pdf" -delete
	@echo "Clean complete"

clean-all: clean
	rm -rf $(HTML_DIR)
	@echo "Full clean complete"

# =============================================================================
# Viewing
# =============================================================================

view: all
	@if command -v xdg-open >/dev/null 2>&1; then \
		xdg-open $(PDF) >/dev/null 2>&1 & \
	elif command -v open >/dev/null 2>&1; then \
		open $(PDF) \
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
	@echo "  all         - Build the main monograph PDF (default)"
	@echo "  theorems    - Build all standalone theorem PDFs"
	@echo "  theorem-ID  - Build specific theorem by ID (e.g., make theorem-weakening-admissibility)"
	@echo "  list-theorems - List all available theorem files"
	@echo "  html        - Generate HTML version (requires pandoc or tex4ht)"
	@echo "  test        - Run validation tests"
	@echo "  test-tex    - Test TeX compilation"
	@echo "  clean       - Remove auxiliary files"
	@echo "  clean-all   - Remove all generated files including HTML"
	@echo "  view        - Build and open PDF"
	@echo "  help        - Show this help message"
