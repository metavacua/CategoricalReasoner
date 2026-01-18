function $(id) {
  return document.getElementById(id);
}

function setText(id, text) {
  $(id).textContent = text;
}

async function fetchJson(url, opts) {
  const res = await fetch(url, opts);
  const text = await res.text();
  if (!res.ok) {
    throw new Error(text || `HTTP ${res.status}`);
  }
  return JSON.parse(text);
}

async function refreshOntologies() {
  try {
    const data = await fetchJson('/api/ontologies');
    setText('ontologies', JSON.stringify(data, null, 2));
  } catch (e) {
    setText('ontologies', String(e));
  }
}

async function runQuery() {
  setText('result', 'Running...');

  const query = $('query').value;
  const format = $('format').value;

  try {
    const res = await fetch('/api/query', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ query, format }),
    });

    const text = await res.text();
    if (!res.ok) {
      setText('result', text || `HTTP ${res.status}`);
      return;
    }

    setText('result', text);
  } catch (e) {
    setText('result', String(e));
  }
}

function setExampleQuery() {
  $('query').value = `PREFIX catty: <http://localhost:8080/ontology/catty-categorical-schema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT ?logic ?label WHERE {
  GRAPH <http://localhost:8080/ontology/logics-as-objects#> {
    ?logic a catty:Logic .
    OPTIONAL { ?logic rdfs:label ?label . }
  }
}
LIMIT 50`;
}

async function exportDataset() {
  const format = $('exportFormat').value;
  const url = `/api/graph?format=${encodeURIComponent(format)}`;
  const res = await fetch(url);
  const text = await res.text();

  if (!res.ok) {
    setText('result', text || `HTTP ${res.status}`);
    return;
  }

  const blob = new Blob([text], { type: res.headers.get('Content-Type') || 'text/plain' });
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = `catty-dataset.${format === 'turtle' ? 'ttl' : format}`;
  a.click();
}

function init() {
  setText('serverUrl', window.location.origin);
  $('refreshOntologies').addEventListener('click', refreshOntologies);
  $('run').addEventListener('click', runQuery);
  $('example').addEventListener('click', setExampleQuery);
  $('export').addEventListener('click', exportDataset);

  setExampleQuery();
  refreshOntologies();
}

init();
