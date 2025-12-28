# Catty

Catty is a minimal LaTeX thesis repository scaffold. It is intentionally light on content and focuses on a reliable build pipeline (PDF locally, HTML deployment to GitHub Pages) so chapters can be added incrementally over time.

Deployed thesis (after running the Deploy workflow): https://<owner>.github.io/Catty/

## Build (PDF)

Requirements: a LaTeX distribution (e.g. TeX Live) and `make`.

```sh
cd thesis
make
```

The output PDF is `thesis/main.pdf`.

## Deploy (HTML to GitHub Pages)

This repository includes a manual GitHub Actions workflow:

1. GitHub UI → **Actions**
2. Select **Deploy**
3. **Run workflow**

If this is the first deployment, ensure GitHub Pages is set to deploy from Actions:
**Settings → Pages → Build and deployment → Source: GitHub Actions**.

The workflow builds `main.pdf`, converts the expanded LaTeX source to `index.html` using Pandoc, and deploys the resulting site to GitHub Pages.
