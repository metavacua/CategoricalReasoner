from rdflib import Graph

g = Graph()
g.parse("metadata.ttl", format="turtle")
g.parse("docs/metadata.ttl", format="turtle")
g.parse("src/metadata.ttl", format="turtle")

print("=== Bidirectional Relationship Check ===")
print("For every schema:hasPart(A, B), there should be schema:isPartOf(B, A)\n")

query = """
PREFIX schema: <https://schema.org/>

SELECT ?parent ?child ?parentLabel ?childLabel
WHERE {
  ?parent schema:hasPart ?child .
  OPTIONAL { ?parent rdfs:label ?parentLabel }
  OPTIONAL { ?child rdfs:label ?childLabel }
}
ORDER BY ?parent
"""

results = list(g.query(query))
print(f"Found {len(results)} hasPart relationships:\n")

# Check for consistency
errors = []
for row in results:
    parent_label = row.parentLabel if row.parentLabel else str(row.parent).split("/")[-1]
    child_label = row.childLabel if row.childLabel else str(row.child).split("/")[-1]
    print(f"  {parent_label} hasPart {child_label}")
    
    # Check if the reciprocal isPartOf exists
    check_query = f"""
    PREFIX schema: <https://schema.org/>
    ASK {{ <{row.child}> schema:isPartOf <{row.parent}> }}
    """
    has_reverse = list(g.query(check_query))[0]
    if not has_reverse:
        errors.append((row.parent, row.child))
        print(f"    ERROR: Missing reverse relationship")

if errors:
    print(f"\n✗ FAIL: Found {len(errors)} missing reverse relationships")
    for parent, child in errors:
        print(f"  {parent} -> {child}")
else:
    print(f"\n✓ PASS: All {len(results)} hasPart relationships have reciprocal isPartOf")

# Count total relationships
print("\n=== Relationship Summary ===")
haspart_count = len(list(g.query("""
    PREFIX schema: <https://schema.org/>
    SELECT (COUNT(*) as ?c) WHERE { ?s schema:hasPart ?o }
""")))[0][0]

ispartof_count = len(list(g.query("""
    PREFIX schema: <https://schema.org/>
    SELECT (COUNT(*) as ?c) WHERE { ?s schema:isPartOf ?o }
""")))[0][0]

print(f"Total schema:hasPart: {haspart_count}")
print(f"Total schema:isPartOf: {ispartof_count}")
print(f"✓ Balanced: {haspart_count == ispartof_count}")
