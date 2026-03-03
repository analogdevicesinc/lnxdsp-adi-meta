# Template System

The getting-started guides use templates instead of maintaining 24+ duplicate files. Each board/version combination is just a 2-line RST file that points to shared template + data.

## What's here

```
getting-started/
├── templates/getting-started.rst.jinja  - the actual doc content
├── data/*.yaml                          - board configs (sc573-5.0.1.yaml, etc)
└── Getting-Started-*.rst                - 2-line wrappers that reference the above
```

## How it works

Each RST file looks like this:

```rst
.. include-template:: templates/getting-started.rst.jinja
   :file: data/sc573-5.0.1.yaml
```

When Sphinx builds the docs, the ADI doctools `include-template` directive loads the template, loads the YAML data, runs Jinja2 templating, and outputs the final RST content. Standard Sphinx directives (code-blocks, notes, images, etc.) work in the templates.

## Updating docs

If you need to change something in the getting-started guides:

Edit `templates/getting-started.rst.jinja` and rebuild. That's it. All 24 guides update automatically.

If you need board-specific changes, edit the relevant YAML file in `data/`.

## Adding a new board or version

You'll need two files:

1. Create the data file: `data/boardname-X.X.X.yaml`
   Copy an existing one and modify the values. Required fields include board_name, machine, version, ubuntu_version, arch, gdb_prefix, openocd_target, and hardware config details.

2. Create the RST wrapper: `Getting-Started-with-BOARDNAME-(Linux-for-ADSP-SC5xx-Processors-X.X.X).rst`
   Just put the two-line include-template directive in it, pointing to your data file.

Then run `make html`.

## Why templates

Previously we had 24+ nearly-identical RST files. If you found a typo, you had to fix it 24 times. If you wanted to add a section, you had to update 24 files. Now you edit one template and everything updates.

The tradeoff is you need to understand the template syntax if you want to make complex changes. For most updates though, it's straightforward.
