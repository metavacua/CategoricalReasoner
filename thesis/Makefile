TEX := main.tex
PDF := main.pdf
LATEX := pdflatex
LATEX_FLAGS := -interaction=nonstopmode -halt-on-error -file-line-error

.PHONY: all clean view

all: $(PDF)

$(PDF): $(TEX) preamble.tex chapters/categorical-semantic-audit.tex chapters/conclusions.tex chapters/introduction.tex
	$(LATEX) $(LATEX_FLAGS) $(TEX)
	$(LATEX) $(LATEX_FLAGS) $(TEX)

clean:
	rm -f \
		*.aux *.bbl *.bcf *.blg *.fdb_latexmk *.fls *.lof *.log *.lot \
		*.nav *.out *.run.xml *.snm *.synctex.gz *.toc *.vrb \
		$(PDF)

view: all
	@if command -v xdg-open >/dev/null 2>&1; then \
		xdg-open $(PDF) >/dev/null 2>&1 & \
	elif command -v open >/dev/null 2>&1; then \
		open $(PDF) \
	else \
		echo "Built $(PDF). Open it with your PDF viewer."; \
	fi
