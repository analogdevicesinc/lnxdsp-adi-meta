#!/bin/bash
# Documentation build script with automatic virtual environment activation

# Check if documentation-env exists
if [ -d "../documentation-env" ]; then
    echo "Using existing documentation-env virtual environment..."
    source ../documentation-env/bin/activate
elif [ -d "documentation-env" ]; then
    echo "Using existing documentation-env virtual environment..."
    source documentation-env/bin/activate
else
    echo "Creating new virtual environment..."
    python3 -m venv ../documentation-env
    source ../documentation-env/bin/activate
    echo "Installing dependencies..."
    pip install -r requirements.txt
fi

# Clean previous build
echo "Cleaning previous build..."
make clean

# Build HTML documentation
echo "Building HTML documentation..."
make html

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Documentation built successfully!"
    echo "📁 Output: _build/html/index.html"
    echo ""
    echo "To view locally:"
    echo "  python3 -m http.server 8000 --directory _build/html"
    echo "  Then open: http://localhost:8000"
else
    echo ""
    echo "❌ Build failed. See errors above."
    exit 1
fi