#!/bin/bash
set -e

echo "=== Starting Catty Thesis Preview ==="

# Start Fuseki server in background on port 3030
echo "Starting Fuseki server on port 3030..."
/opt/fuseki/fuseki-server --port=3030 --update --mem /catty > fuseki.log 2>&1 &
FUSEKI_PID=$!
echo "Fuseki PID: $FUSEKI_PID"

# Wait for Fuseki to start
echo "Waiting for Fuseki to initialize..."
for i in {1..10}; do
    if curl -s http://localhost:3030/$/ping > /dev/null 2>&1; then
        echo "Fuseki is ready!"
        break
    fi
    sleep 1
done

# Load ontologies into Fuseki
echo "Loading ontologies into Fuseki..."
for file in ontology/*.jsonld; do
    echo "  Loading $(basename $file)..."
    curl -s -X POST --data-binary @"$file" \
         -H "Content-Type: application/ld+json" \
         http://localhost:3030/catty/data > /dev/null
done

for file in ontology/examples/*.ttl; do
    if [ -f "$file" ]; then
        echo "  Loading $(basename $file)..."
        curl -s -X POST --data-binary @"$file" \
             -H "Content-Type: text/turtle" \
             http://localhost:3030/catty/data > /dev/null
    fi
done

echo "Ontologies loaded successfully!"
echo ""
echo "=== Services Started ==="
echo "Web Interface: http://localhost:3000"
echo "Fuseki SPARQL Endpoint: http://localhost:3030/catty/sparql"
echo "Fuseki Web UI: http://localhost:3030"
echo ""
echo "Starting web server on port 3000..."

# Start Python web server on port 3000 (blocking)
cd site
python3 -m http.server 3000
