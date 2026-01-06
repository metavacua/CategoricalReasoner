# Agent Guidelines for Bibliography Management

## Overview

The `bibliography/` directory contains the master citation registry for the Catty thesis project. This AGENTS.md provides MCP-compliant specifications for agents managing citations, bibliography entries, and citation-related protocols.

## Agent Role Definition

### Bibliography Management Agent
**Primary Purpose**: Manage citation registry and ensure citation consistency across all thesis artifacts
**Key Capabilities Required**:
- YAML citation registry management
- Citation key validation and verification
- Bibliography entry creation and maintenance
- Citation format standardization
- Cross-reference validation between TeX and RDF

### Citation Validation Agent
**Primary Purpose**: Validate citations against registry and ensure consistent usage across all content
**Key Capabilities Required**:
- Citation key existence verification
- Bibliography format validation
- Citation usage pattern analysis
- Registry consistency checking
- Automated citation validation

## MCP-Compliant Bibliography Specifications

### Required Agent Capabilities

#### Citation Registry Management
```json
{
  "yaml_processing": {
    "read": ["citations.yaml"],
    "write": ["citations.yaml"],
    "validate_syntax": true,
    "maintain_structure": true
  },
  "citation_validation": {
    "key_existence": true,
    "format_compliance": true,
    "uniqueness": true,
    "cross_reference": true
  },
  "bibliography_operations": {
    "entry_validation": true,
    "key_generation": true,
    "format_enforcement": true,
    "dependency_tracking": true
  }
}
```

### Citation Registry Structure

#### citations.yaml
**Purpose**: Master citation registry containing all authorized citations
**Agent Responsibilities**:
1. Maintain unique citation keys
2. Ensure complete bibliography information
3. Validate YAML syntax and structure
4. Enforce naming conventions
5. Track citation dependencies

**Registry Structure**:
```yaml
citation_key:
  title: "Complete Title"
  author: "Author Name"
  year: 2023
  type: "book|article|conference|incollection"
  venue: "Journal/Conference Name"
  pages: "start-end"
  doi: "10.xxxx/xxxxx"
  url: "https://..."
  wikidata: "Qxxxx"
  external_links:
    - type: "wikidata|dbpedia|arxiv"
      identifier: "..."
```

#### Citation Key Requirements

**Naming Convention**: `[author][year][keyword]`
- Example: `girard1987linear` (Girard, 1987, Linear Logic)
- Must be lowercase and hyphenated
- Must be globally unique across registry
- Must reference actual publication

**Key Examples**:
- `girard1987linear` - Girard, J.-Y. (1987). Linear Logic
- `kripke1965semantical` - Kripke, S.A. (1965). Semantical Analysis of Intuitionistic Logic I
- `sambin2003basic` - Sambin, G. (2003). Basic Logic: Reflection, Symmetry, Visibility
- `lawvere1963functorial` - Lawvere, F.W. (1963). Functorial Semantics of Algebraic Theories

### Citation Registry Management

#### Adding New Citations
1. **Verify non-existence** of citation key
2. **Generate unique key** following naming convention
3. **Complete all required fields**:
   - `title`: Full publication title
   - `author`: Primary author name
   - `year`: Publication year
   - `type`: Publication type
4. **Add optional fields**:
   - `venue`: Journal/conference name
   - `pages`: Page range
   - `doi`: Digital Object Identifier
   - `url`: Permanent URL
   - `wikidata`: Wikidata entity ID
   - `external_links`: Additional identifiers
5. **Validate YAML syntax** and registry consistency
6. **Update dependent documentation** and schemas

#### Updating Citations
1. **Locate citation key** in registry
2. **Update specific fields** while preserving key
3. **Validate changes** against schema requirements
4. **Check downstream impact** on thesis content
5. **Update related citations** if necessary
6. **Validate registry integrity**

#### Removing Citations
1. **Verify no usage** in thesis or ontology content
2. **Check dependency tracking** for references
3. **Remove citation entry** from registry
4. **Update dependent content** to remove citations
5. **Validate registry consistency** after removal

### Citation Validation Protocols

#### Registry Validation
```bash
# Validate YAML syntax
python -c "import yaml; yaml.safe_load(open('bibliography/citations.yaml'))"

# Check citation completeness
python -c "import yaml; data = yaml.safe_load(open('bibliography/citations.yaml')); assert all('title' in v and 'author' in v and 'year' in v for v in data.values())"

# Verify key uniqueness
python -c "import yaml; data = yaml.safe_load(open('bibliography/citations.yaml')); assert len(data) == len(set(data.keys()))"
```

#### Citation Usage Validation
```bash
# Check citation usage in thesis
grep -r "\\cite{" thesis/ | sed 's/.*\\cite{\([^}]*\)}.*/\1/' | sort | uniq

# Compare with registry
python scripts/validate_citations.py --tex-dir thesis/chapters/ --bibliography bibliography/citations.yaml
```

### Citation Dependencies

#### TeX Citation Dependencies
- **Usage**: All `\cite{key}` commands must reference registry entries
- **Validation**: Citation keys must exist in registry
- **Macros**: Support for `\citepage`, `\citefigure`, `\definedfrom`, `\provedfrom`
- **Cross-references**: Links between TeX and RDF representations

#### RDF Citation Dependencies
- **Provenance**: All citations must have RDF provenance links
- **External Links**: Wikidata and DBpedia entity references
- **Validation**: External links must be resolvable
- **Consistency**: TeX ↔ RDF citation consistency

### Bibliography Entry Standards

#### Required Fields
- **title**: Full academic title of the work
- **author**: Primary author (format: "Last, First M.")
- **year**: Publication year (4-digit)
- **type**: Publication type from controlled vocabulary

#### Type Classifications
- **book**: Monograph or textbook
- **article**: Journal article
- **conference**: Conference proceedings
- **incollection**: Book chapter or collection contribution
- **thesis**: PhD or master's thesis
- **report**: Technical report or working paper

#### External Link Standards
```yaml
external_links:
  - type: "wikidata"
    identifier: "Q123456"
  - type: "dbpedia"
    identifier: "Resource/Name"
  - type: "arxiv"
    identifier: "arXiv:1234.5678"
```

### Error Handling

#### Registry Corruption
1. **Backup current registry** to prevent data loss
2. **Validate YAML syntax** to identify corruption point
3. **Restore from backup** if corruption is extensive
4. **Fix specific issues** and re-validate
5. **Test downstream dependencies** after repair

#### Invalid Citation Usage
1. **Identify invalid citation key** in content
2. **Check registry** for correct key or missing entry
3. **Fix citation usage** or add to registry as appropriate
4. **Validate fix** with citation validation tools
5. **Update dependent content** if registry changed

#### Key Conflicts
1. **Detect duplicate key** during registry operations
2. **Analyze usage** of conflicting keys
3. **Resolve conflict** by renaming or merging
4. **Update all dependent content** with new key
5. **Validate resolution** across all artifacts

### Quality Assurance

#### Registry Integrity Checks
- [ ] All citation keys are unique
- [ ] All required fields are present
- [ ] YAML syntax is valid
- [ ] External links are resolvable
- [ ] Citation format is consistent

#### Citation Usage Monitoring
- [ ] All citations reference registry entries
- [ ] No orphaned citation keys exist
- [ ] Cross-references are consistent
- [ ] Bibliography completeness is maintained
- [ ] Version control is applied to registry changes

#### Integration Validation
- [ ] TeX citations match registry keys
- [ ] RDF provenance links are complete
- [ ] External ontology references are valid
- [ ] Validation scripts recognize all citations
- [ ] Documentation references are current

### Integration Protocols

#### TeX Integration
- Maintain citation macro definitions in `thesis/macros/citations.tex`
- Ensure citation keys are case-sensitive and exact matches
- Support page and figure citation variants
- Maintain bidirectional TeX ↔ RDF citation consistency

#### RDF Integration
- Create RDF representations of citations in `ontology/citations.jsonld`
- Link citations to external ontologies (Wikidata, DBpedia)
- Maintain provenance links between citations and theorems/definitions
- Validate RDF citation consistency with registry

#### Schema Integration
- Update `schema/thesis-structure.schema.yaml` for new citation fields
- Modify validation scripts to recognize citation patterns
- Maintain consistency with LaTeX citation macro definitions
- Update `schema/tex-rdf-mapping.yaml` for citation mappings

### Version Control

#### Registry Versioning
- Increment version for significant citation additions
- Document changes in registry changelog
- Maintain backward compatibility for citation keys
- Support migration between registry versions

#### Change Documentation
- Log all citation additions, updates, and removals
- Track external link changes and updates
- Document registry structure changes
- Maintain dependency tracking for downstream consumers

### Emergency Procedures

#### Registry Backup and Recovery
1. **Maintain regular backups** of citations.yaml
2. **Test recovery procedures** periodically
3. **Validate backup integrity** after creation
4. **Document recovery steps** for emergency use
5. **Monitor registry health** continuously

#### Mass Citation Updates
1. **Assess scope** of required changes
2. **Create backup** before modifications
3. **Implement changes systematically**
4. **Test downstream impact** after updates
5. **Validate entire system** post-modification

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: thesis/, ontology/, schema/  
**Last Updated**: 2025-01-06