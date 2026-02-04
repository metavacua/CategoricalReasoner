# SPARQL Queries for Catty Ontology

This directory contains example SPARQL queries for querying the Catty categorical ontology.

## Query Files

### Basic Queries

#### `all-logics.rq`
List all logics with their logical signatures and axioms.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?logic ?label ?signature ?axiom
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?label .
  OPTIONAL { ?logic catty:hasLogicalSignature ?signature . }
  OPTIONAL { ?logic catty:hasLogicalAxiom ?axiom . }
}
ORDER BY ?label
```

#### `structural-rules.rq`
Get all structural rules for a given logic.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?rule ?ruleLabel
WHERE {
  catty:LL catty:hasStructuralRule ?rule .
  ?rule rdfs:label ?ruleLabel .
}
```

### Morphism Queries

#### `morphisms-from-logic.rq`
Find all morphisms from a specific logic.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?morphism ?target ?targetLabel ?morphismType
WHERE {
  ?morphism catty:domain catty:LJ ;
            catty:codomain ?target .
  ?target rdfs:label ?targetLabel .
  OPTIONAL { ?morphism a ?morphismType . }
}
```

#### `lattice-order.rq`
Get all lattice order relations (Logic A extends Logic B).

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?source ?sourceLabel ?target ?targetLabel
WHERE {
  ?morphism a catty:Extension ;
            catty:domain ?source ;
            catty:codomain ?target .
  ?source rdfs:label ?sourceLabel .
  ?target rdfs:label ?targetLabel .
}
ORDER BY ?sourceLabel ?targetLabel
```

### Adjoint Queries

#### `adjoint-relationships.rq`
Find all adjoint functor pairs.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?adjoint ?label ?source ?target
WHERE {
  ?adjoint a catty:AdjointFunctors ;
           rdfs:label ?label ;
           catty:sourceCategory ?source ;
           catty:targetCategory ?target .
}
```

### Curry-Howard Queries

#### `curry-howard-mapping.rq`
Get the type theory corresponding to a logic under Curry-Howard.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?logic ?logicLabel ?typeTheory ?typeTheoryLabel
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?logicLabel ;
         catty:correspondsToLogic ?typeTheory .
  ?typeTheory rdfs:label ?typeTheoryLabel .
}
```

#### `curry-howard-functor.rq`
Get the Curry-Howard functor mappings.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?logic ?typeTheory
WHERE {
  catty:CurryHowardFunctor catty:objectMapping ?mapping .
  ?mapping ?logic ?typeTheory .
}
```

### Lattice Queries

#### `extension-hierarchy.rq`
Get the extension hierarchy of logics (Logic A extends Logic B).

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?source ?sourceLabel ?target ?targetLabel
WHERE {
  ?morphism a catty:Extension ;
            catty:domain ?source ;
            catty:codomain ?target .
  ?source rdfs:label ?sourceLabel .
  ?target rdfs:label ?targetLabel .
}
ORDER BY ?sourceLabel ?targetLabel
```

#### `lattice-neighbors.rq`
Find all immediate neighbors of a logic in the lattice.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?neighbor ?neighborLabel ?relation
WHERE {
  {
    # Immediate successors (less restrictive)
    ?morphism catty:domain catty:LL ;
              catty:codomain ?neighbor .
    ?neighbor rdfs:label ?neighborLabel .
    BIND("successor" AS ?relation)
  } UNION {
    # Immediate predecessors (more restrictive)
    ?morphism catty:domain ?neighbor ;
              catty:codomain catty:LL .
    ?neighbor rdfs:label ?neighborLabel .
    BIND("predecessor" AS ?relation)
  }
}
```

### Validation Queries

#### `orphan-logics.rq`
Find logics that have no morphisms to/from them.

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?logic ?label
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?label .
  FILTER NOT EXISTS {
    ?morphism catty:domain ?logic .
  }
  FILTER NOT EXISTS {
    ?morphism catty:codomain ?logic .
  }
}
```

#### `terminal-logics.rq`
Find terminal logics (logics that are not extended by any other logic).

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?logic ?label
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?label .
  FILTER NOT EXISTS {
    ?morphism a catty:Extension ;
              catty:domain ?logic .
  }
}
```

## Running Queries

### Using Jena ARQ

```bash
arq --data ../ontology/catty-categorical-schema.jsonld \
    --data ../ontology/logics-as-objects.jsonld \
    --data ../ontology/morphism-catalog.jsonld \
    --query all-logics.rq \
    --results csv
```

### Using RDF4J (SPARQL endpoint)

```bash
curl -X POST -H "Content-Type: application/sparql-query" \
  --data @all-logics.rq \
  "http://localhost:8080/rdf4j-server/repositories/catty?format=json"
```

### Using rdflib (Python)

```python
from rdflib import Graph

# Load the ontology
g = Graph()
g.parse("../ontology/catty-categorical-schema.jsonld", format="json-ld")
g.parse("../ontology/logics-as-objects.jsonld", format="json-ld")
g.parse("../ontology/morphism-catalog.jsonld", format="json-ld")

# Load and execute a query
with open("all-logics.rq", "r") as f:
    query = f.read()

results = g.query(query)
for row in results:
    print(f"{row.label}: {row.coordinate}")
```

### Using Apache Jena (Java)

```java
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

Model model = ModelFactory.createDefaultModel();
model.read("catty-categorical-schema.jsonld", "JSON-LD");
model.read("logics-as-objects.jsonld", "JSON-LD");

Query query = QueryFactory.read("all-logics.rq");
try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
    ResultSet results = qexec.execSelect();
    ResultSetFormatter.out(results);
}
```

## Query Results Format

Most queries can output results in various formats:

- **CSV**: `--results csv` or `?format=csv`
- **JSON**: `--results json` or `?format=json`
- **XML**: `--results xml` or `?format=xml`
- **TSV**: `--results tsv` or `?format=tsv`

## Complex Queries

### Find all paths in the lattice

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?source ?sourceLabel ?target ?targetLabel (COUNT(?path) AS ?distance)
WHERE {
  ?path a rdf:Seq .
  ?path rdf:_1 ?source .
  ?path rdf:last ?target .
  ?source rdfs:label ?sourceLabel .
  ?target rdfs:label ?targetLabel .
  FILTER NOT EXISTS {
    ?morphism catty:domain ?source ;
              catty:codomain ?target .
  }
}
GROUP BY ?source ?sourceLabel ?target ?targetLabel
```

### Validate Curry-Howard correspondence

```sparql
PREFIX catty: <https://metavacua.github.io/CategoricalReasoner/ontology/>

SELECT ?logic ?logicLabel ?typeTheory ?typeTheoryLabel
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?logicLabel ;
         catty:hasSequentForm ?sequentForm .
  ?typeTheory a catty:TypeTheory ;
               rdfs:label ?typeTheoryLabel .
  FILTER EXISTS {
    ?logic catty:correspondsToLogic ?typeTheory .
  }
}
ORDER BY ?logicLabel
```
