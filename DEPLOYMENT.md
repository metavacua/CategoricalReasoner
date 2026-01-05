# GitHub Pages Deployment Configuration

## Issue: README.md Being Deployed Instead of Thesis

If you're seeing the repository README.md deployed instead of the thesis, it's because GitHub Pages is configured to deploy from a branch (e.g., "Deploy from the main branch") rather than from GitHub Actions.

## Solution: Configure GitHub Pages to Use GitHub Actions

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Pages**
3. Under **Build and deployment**, find **Source**
4. Change the source from "Deploy from a branch" to **"GitHub Actions"**
5. The Deploy workflow will automatically build and deploy the thesis when triggered

## What Gets Deployed

The Deploy workflow (`.github/workflows/deploy.yml`) builds and deploys:

### Main Site
- `index.html` - The thesis converted from LaTeX to HTML via Pandoc
- `main.pdf` - The LaTeX thesis as a downloadable PDF
- `.nojekyll` - Prevents Jekyll from processing the files

### Ontologies
- `ontology/` directory with all RDF/OWL schemas in JSON-LD format:
  - `catty-categorical-schema.jsonld`
  - `logics-as-objects.jsonld`
  - `morphism-catalog.jsonld`
  - `two-d-lattice-category.jsonld`
  - `curry-howard-categorical-model.jsonld`
  - `catty-complete-example.jsonld`
  - `catty-shapes.ttl` (SHACL constraints)
  - `index.html` (listing of all ontologies)

### Benchmarks
- `benchmarks/queries/` directory with SPARQL queries:
  - `all-logics.rq`
  - `extension-hierarchy.rq`

## Automatic Deployment

The workflow is triggered automatically on:
- Push to the `main` branch
- Pull requests to the `main` branch (builds but doesn't deploy)
- Manual trigger via GitHub Actions UI

## Manual Deployment

To deploy manually:
1. Go to **Actions** tab
2. Select **Deploy** workflow
3. Click **Run workflow**
4. Select branch and click **Run workflow**

## Build Process

The workflow:
1. Installs LaTeX (TeX Live) and Pandoc
2. Builds the PDF from `thesis/main.tex` using make
3. Expands LaTeX includes with `latexpand`
4. Converts to HTML using Pandoc
5. Copies ontologies and benchmarks
6. Creates `.nojekyll` file
7. Uploads artifact and deploys to GitHub Pages

## Troubleshooting

### If you still see README.md deployed
- Verify GitHub Pages Source is set to "GitHub Actions" (not "Deploy from a branch")
- Check the Actions tab to see if the workflow is running successfully
- Look at the workflow logs for any build errors

### If deployment fails
- Check the workflow logs in the Actions tab
- Common issues:
  - LaTeX compilation errors (check `.log` files)
  - Missing dependencies (make, pandoc, texlive packages)
  - File permission issues
- Ensure the Makefile has the correct dependencies

### If site shows but content is wrong
- Clear browser cache
- Check that `site/index.html` exists in the workflow artifact
- Verify the HTML contains thesis content (search for "Category Theory" or similar)
