# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |

## Security Measures

### Java Server Security

The Catty localhost server (`java/`) implements several security measures:

1. **Path Traversal Prevention**: All file access through `RepoOntologyHandler` validates paths to prevent directory traversal attacks
2. **IRI Safety**: JSON-LD uploads are validated to prevent fabricated IRIs
3. **Request Size Limits**: HTTP request bodies are limited to 1MB
4. **Localhost Only**: Server binds to 127.0.0.1 (not 0.0.0.0)

### Known Limitations

- The server is designed for **localhost development only**
- Not suitable for production deployment without additional security hardening
- No authentication or authorization mechanisms
- CORS not configured (same-origin policy enforced)

## Reporting a Vulnerability

If you discover a security vulnerability in this project:

1. **Do not** open a public GitHub issue
2. Email the maintainer directly (see repository owner)
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if available)

We will acknowledge receipt within 48 hours and provide a timeline for a fix.

## Development Security Best Practices

When contributing to this project:

- Never commit credentials or secrets
- Use parameterized queries for all SPARQL operations
- Validate all user input
- Follow principle of least privilege
- Keep dependencies up to date (use Dependabot)
