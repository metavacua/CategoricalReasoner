> [!IMPORTANT]
> This file has been consolidated into the "Architecture of Catty" TeX document and is marked for future removal.

# External Ontologies and Resources

This document catalogs external RDF/OWL ontologies and semantic resources that can be integrated with or referenced by the Catty categorical framework.

## Category Theory Foundations

### DBPedia Category Theory
- **URL**: https://dbpedia.org/page/Category_theory
- **License**: CC BY-SA 3.0
- **Coverage**: Basic categorical concepts (categories, functors, natural transformations)
- **Integration**: Reference for foundational concepts

### Wikidata Mathematical Ontology
- **URL**: https://www.wikidata.org/wiki/Q16917
- **License**: CC0 (Public Domain)
- **Coverage**: Structured categorical concepts with explicit typing
- **Integration**: Direct linking for CC0-licensed content

## Logic Ontologies

### COLORE (Common Logic Ontology Repository)
- **URL**: http://www.loa.istc.cnr.it/colore/
- **License**: CC BY 4.0
- **Coverage**: Modular ontologies including DOLCE, BFO, and logic-specific modules
- **Integration**: Extension with categorical axioms

### OpenMath Content Dictionaries
- **URL**: https://www.openmath.org/cd/
- **License**: BSD 3-Clause
- **Coverage**: Mathematical symbols including category theory (category1.cd)
- **Integration**: Transformation to RDF/OWL

## Type Theory and Proof Assistants

### nLab Semantic Linked Data
- **URL**: https://ncatlab.org/
- **License**: CC BY-SA 3.0
- **Coverage**: Category theory and type theory content
- **Integration**: External discovery and reference

### Coq Mathematical Components
- **URL**: https://math-comp.github.io/
- **License**: Apache 2.0
- **Coverage**: Formalized category theory
- **Integration**: Custom parsing for categorical structures

### Lean Mathlib Category Theory
- **URL**: https://leanprover-community.github.io/mathlib_docs/category_theory/
- **License**: Apache 2.0
- **Coverage**: Comprehensive category theory formalization
- **Integration**: Export parsing and transformation

## License Compatibility

All external resources are compatible with Catty's AGPL-3.0 license:

| Resource | License | Compatibility |
|----------|---------|---------------|
| DBPedia | CC BY-SA 3.0 | Compatible (Attribution) |
| Wikidata | CC0 | Fully Compatible |
| OpenMath | BSD 3-Clause | Fully Compatible |
| COLORE | CC BY 4.0 | Compatible (Attribution) |
| nLab | CC BY-SA 3.0 | Compatible (Attribution) |
| Coq Libraries | Apache 2.0 | Fully Compatible |
| Lean Mathlib | Apache 2.0 | Fully Compatible |

## Integration Strategy

1. **Direct Import**: Wikidata (CC0), OpenMath (BSD)
2. **Extension Required**: COLORE (add categorical axioms)
3. **Reference Only**: DBPedia, nLab (external discovery)
4. **Custom Parsing**: Coq/Lean exports (structured data transformation)