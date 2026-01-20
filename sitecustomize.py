"""Project-local Python site customization.

The Catty ontologies use a remote JSON-LD context URL (localhost or GitHub Pages)
so that IRIs are stable across environments.

When running locally (including CI), there may be no HTTP server available to
serve that context. rdflib's JSON-LD parser will attempt to fetch remote
contexts via `urllib.request.urlopen`, which would fail.

This module is auto-imported by Python (when present on sys.path) and patches
`urllib.request.urlopen` to serve the context from the repository's
`ontology/context.jsonld` file.

This keeps RDF parsing deterministic and offline.
"""

from __future__ import annotations

from email.message import Message
from io import BytesIO
from pathlib import Path
from urllib.response import addinfourl
import urllib.request


_REPO_ROOT = Path(__file__).resolve().parent
_CONTEXT_FILE = _REPO_ROOT / "ontology" / "context.jsonld"

if _CONTEXT_FILE.exists():
    _CONTEXT_BYTES = _CONTEXT_FILE.read_bytes()

    _CONTEXT_URLS = {
        "http://localhost:8080/ontology/context.jsonld",
        "https://metavacua.github.io/CategoricalReasoner/ontology/context.jsonld",
        "https://example.com/ontology/context.jsonld",
    }

    _real_urlopen = urllib.request.urlopen

    def _catty_urlopen(url, *args, **kwargs):
        req_url = url.full_url if isinstance(url, urllib.request.Request) else str(url)
        if req_url in _CONTEXT_URLS:
            headers = Message()
            headers.add_header("Content-Type", "application/ld+json")
            return addinfourl(BytesIO(_CONTEXT_BYTES), headers, req_url)

        # Block all other network access during Catty operations to prevent SSRF
        raise urllib.error.URLError(
            f"Network access blocked by Catty offline mode for URL: {req_url}. "
            "Only registered context URLs are allowed."
        )

    urllib.request.urlopen = _catty_urlopen
