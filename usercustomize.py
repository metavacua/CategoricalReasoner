"""User-level Python customization for the Catty repository.

Python automatically imports `usercustomize` during startup (if user site-packages
are enabled).

Catty JSON-LD files intentionally reference the remote context URL:

  http://localhost:8080/ontology/context.jsonld

During CI and offline validation we don't run an HTTP server on localhost.
rdflib's JSON-LD parser will attempt to fetch the remote context using
`urllib.request.urlopen`, which would fail.

This module patches `urllib.request.urlopen` so that requests for the Catty
context URL are served from the repository's local file:

  ontology/context.jsonld

This keeps RDF parsing deterministic and avoids any external network calls.
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
    }

    _real_urlopen = urllib.request.urlopen

    def _catty_urlopen(url, *args, **kwargs):
        req_url = url.full_url if isinstance(url, urllib.request.Request) else str(url)
        if req_url in _CONTEXT_URLS:
            headers = Message()
            headers.add_header("Content-Type", "application/ld+json")
            return addinfourl(BytesIO(_CONTEXT_BYTES), headers, req_url)
        return _real_urlopen(url, *args, **kwargs)

    urllib.request.urlopen = _catty_urlopen
