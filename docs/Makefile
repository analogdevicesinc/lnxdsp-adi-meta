SHELL         = /bin/bash
SPHINXOPTS   ?=
# Check for virtual environment and use it if available
VENV_DIR      = ../documentation-env
ifeq ($(wildcard $(VENV_DIR)/bin/sphinx-build),)
    SPHINXBUILD  ?= sphinx-build
else
    SPHINXBUILD  ?= $(VENV_DIR)/bin/sphinx-build
endif
SOURCEDIR     = .
BUILDDIR      = _build

help:
	@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

.PHONY: help Makefile

%: Makefile
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
	@if [ "$@" = "html" ]; then \
		echo "Creating .nojekyll file for GitHub Pages..."; \
		touch "$(BUILDDIR)/html/.nojekyll"; \
	fi
