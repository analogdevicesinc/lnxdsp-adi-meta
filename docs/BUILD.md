# Building the Documentation

## Quick Start

The easiest way to build the documentation is:

```bash
cd docs
make html
```

The Makefile automatically detects and uses the `documentation-env` virtual environment if it exists.

## Alternative Build Methods

### Using the build script
```bash
cd docs
./build-docs.sh
```

### Manual virtual environment activation
```bash
cd docs
source ../documentation-env/bin/activate
make html
```

## First Time Setup

If the virtual environment doesn't exist:

```bash
# Create virtual environment
python3 -m venv documentation-env

# Activate it
source documentation-env/bin/activate

# Install dependencies
cd docs
pip install -r requirements.txt
```

## Viewing the Documentation

After building, open `_build/html/index.html` in your browser, or run a local server:

```bash
python3 -m http.server 8000 --directory _build/html
# Then open: http://localhost:8000
```

## Troubleshooting

### "sphinx-build: command not found"
The virtual environment is not activated. Either:
- Run `make html` (it now auto-detects the venv)
- Or activate manually: `source ../documentation-env/bin/activate`

### Build warnings
The build may show warnings about:
- Document heading levels (some docs start at H2 instead of H1)
- Missing cross-references in README

These are non-critical and the documentation will still build successfully.

## Required Packages

The following packages are installed in the virtual environment:
- Sphinx 7.1.2
- adi-doctools 0.4.32 (ADI's documentation tools)
- myst-parser 4.0.1 (for Markdown support)
- harmonic theme (via adi-doctools)

See `requirements.txt` for the complete list.