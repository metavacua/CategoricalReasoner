import logging
import os
import sys
import importlib.util
from pathlib import Path

# Setup logging
logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
logger = logging.getLogger(__name__)

try:
    from rdflib import Graph
except ImportError:
    logger.error("rdflib not found. Please install it with 'pip install rdflib'")
    sys.exit(1)


def _load_iri_config_class(repo_root: Path):
    """Load IRIConfig from scripts/iri-config.py via importlib."""
    iri_config_path = repo_root / "scripts" / "iri-config.py"
    if not iri_config_path.exists():
        raise FileNotFoundError(f"Expected IRIConfig module not found: {iri_config_path}")

    spec = importlib.util.spec_from_file_location("catty_iri_config", iri_config_path)
    if spec is None or spec.loader is None:
        raise ImportError(f"Unable to load module from {iri_config_path}")

    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module.IRIConfig


def validate_rdf(directory):
    success = True

    repo_root = Path(directory).parent
    IRIConfig = _load_iri_config_class(repo_root)
    config = IRIConfig(config_path=str(repo_root / ".catty" / "iri-config.yaml"))

    with config.offline_context():
        for filename in os.listdir(directory):
            if filename.endswith(".jsonld") or filename.endswith(".ttl"):
                path = os.path.join(directory, filename)
                g = Graph()
                try:
                    # Determine format
                    fmt = "json-ld" if filename.endswith(".jsonld") else "turtle"
                    g.parse(path, format=fmt)
                    logger.info(f"✅ {filename} is valid RDF.")
                except Exception as e:
                    logger.error(f"❌ {filename} is invalid: {e}")
                    success = False
    return success

if __name__ == "__main__":
    ontology_dir = "ontology"
    if validate_rdf(ontology_dir):
        sys.exit(0)
    else:
        sys.exit(1)
