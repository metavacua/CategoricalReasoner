# Contributing

Contributions are welcome via pull requests.

## Scope

- Keep changes small and focused.
- Do not commit generated build artifacts (PDF/HTML/aux files).
- Thesis source lives under `thesis/` and chapters under `thesis/chapters/`.

## Local checks

Before opening a PR, ensure the LaTeX build works:

```sh
cd thesis
make clean
make
```

## Style

- Prefer plain LaTeX without heavy custom macros unless needed.
- If you add new packages or tooling, include a brief rationale in the PR description.
