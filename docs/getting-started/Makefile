.PHONY: help all dry-run clean check-deps test

# Default target
help:
	@echo "Getting Started Guide Template System"
	@echo ""
	@echo "Usage:"
	@echo "  make all              - Generate all guides for all releases"
	@echo "  make VERSION=5.0.1    - Generate all boards for version 5.0.1"
	@echo "  make dry-run          - Preview what would be generated"
	@echo "  make clean            - Remove all generated RST files"
	@echo "  make check-deps       - Check if Python dependencies are installed"
	@echo "  make test             - Generate one test file"
	@echo ""
	@echo "Examples:"
	@echo "  make VERSION=5.0.2    # Generate new release"
	@echo "  make dry-run          # Preview changes"
	@echo "  make test             # Quick test"

# Check dependencies
check-deps:
	@echo "Checking Python dependencies..."
	@python3 -c "import yaml, jinja2" 2>/dev/null && echo "✓ Dependencies OK" || \
		(echo "✗ Missing dependencies. Install with:" && \
		 echo "  pip install pyyaml jinja2" && exit 1)

# Generate all guides
all: check-deps
	@echo "Generating all getting-started guides..."
	python3 generate-getting-started.py

# Generate for specific version
ifdef VERSION
all: check-deps
	@echo "Generating guides for version $(VERSION)..."
	python3 generate-getting-started.py --version $(VERSION)
endif

# Dry run
dry-run: check-deps
	@echo "Dry run - showing what would be generated..."
	python3 generate-getting-started.py --dry-run

# Test with one board
test: check-deps
	@echo "Testing with SC573 5.0.1..."
	python3 generate-getting-started.py --board adsp-sc573-ezkit --version 5.0.1
	@echo ""
	@echo "✓ Test passed! Check the generated file."

# Clean generated files
clean:
	@echo "Removing generated RST files..."
	@rm -f Getting-Started-with-ADSP-SC*-\(Linux-for-ADSP*.rst
	@echo "✓ Cleaned"
