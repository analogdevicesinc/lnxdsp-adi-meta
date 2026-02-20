from os import path

# -- Project information -----------------------------------------------------

repository = 'adsp'
project = 'ADSP Processors'
copyright = '2026, Analog Devices, Inc.'
author = 'Analog Devices, Inc.'

locale_dirs = ['locales/']  # path is relative to the source directory
language = 'en'

# -- General configuration ---------------------------------------------------

extensions = [
    'adi_doctools',
    'sphinx.ext.intersphinx',
    'myst_parser',
]

needs_extensions = {
    'adi_doctools': '0.3'
}

exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']
source_suffix = {
    '.rst': 'restructuredtext',
    '.md': 'markdown',
}

# -- External docs configuration ----------------------------------------------

interref_repos = [
    'hdl',
]

intersphinx_mapping = {
    'upstream': ('https://docs.kernel.org', None),
    'b4': ('https://b4.docs.kernel.org/en/latest', None),
}

# -- Options for HTML output --------------------------------------------------

html_theme = 'harmonic'

html_theme_options = {}

html_favicon = path.join("sources", "icon.svg")
