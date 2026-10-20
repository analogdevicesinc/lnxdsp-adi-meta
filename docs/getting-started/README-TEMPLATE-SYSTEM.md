# Template System

The getting-started guides use templates instead of maintaining 30+ duplicate files. Each board/version combination is just a 2-line RST file that points to shared template + data.

## What's here

```
getting-started/
├── templates/getting-started-3.rst.jinja     - guide for all 3.x.x releases
├── templates/getting-started-5.rst.jinja     - guide for all 5.x.x releases
├── templates/getting-started-main.rst.jinja  - guide for the main branch (kas)
├── data/*.yaml                               - board configs (sc573-5.0.1.yaml, sc573-main.yaml, etc)
└── Getting-Started-*.rst                     - 2-line wrappers that reference the above
```

There is one template per major release version, shared by every release that
starts with that number, plus one for the `main` branch. Released guides must
keep reading exactly as they did when the release was made, so only touch the
3.x and 5.x templates to fix errors in a specific release. New instructions,
such as the kas based setup, go in the `main` template.

Where releases within a major version differ, the template selects the content
with a Jinja condition on `version` (for example `tftp_flow` in the 5.x
template, which is true for 5.0.0 and 5.0.1).

## How it works

Each RST file looks like this:

```rst
.. include-template:: templates/getting-started-5.rst.jinja
   :file: data/sc573-5.0.1.yaml
```

When Sphinx builds the docs, the ADI doctools `include-template` directive loads the template, loads the YAML data, runs Jinja2 templating, and outputs the final RST content. Standard Sphinx directives (code-blocks, notes, images, etc.) work in the templates.

## Updating docs

If you need to change the instructions for the development version, edit
`templates/getting-started-main.rst.jinja` and rebuild. All the `main` guides
update automatically.

If you need board-specific changes, edit the relevant YAML file in `data/`.

## Adding a new board or version

You'll need two files:

1. Create the data file: `data/boardname-X.X.X.yaml` (or `data/boardname-main.yaml`)
   Copy an existing one and modify the values. Required fields include board_name, machine, version, ubuntu_version, arch, gdb_prefix, openocd_target, and hardware config details.
   The `main` data files also set `sdk_version`, the `DISTRO_VERSION` built from `main`, and may set `kas_file` (default `kas/adsp-sc5xx.yml`).
   Boards built with musl (`kas_file: kas/adi-distro-musl.yml`) also set `libc: musl`, `images` (musl only supports `adsp-sc5xx-tiny`) and `target_sys`; see `data/sc573-main.yaml`.

2. Create the RST wrapper: `Getting-Started-with-BOARDNAME-(Linux-for-ADSP-SC5xx-Processors-X.X.X).rst`
   Just put the two-line include-template directive in it, pointing to the template for that major version and your data file.

When cutting a new release, copy the `main` data files to `data/boardname-X.X.X.yaml`
and point the new wrappers at the template for that major version, adding a new
template (or a `version` condition in the existing one) if the instructions changed.

Then run `make html`.

## Why templates

Previously we had 24+ nearly-identical RST files. If you found a typo, you had to fix it 24 times. If you wanted to add a section, you had to update 24 files. Now you edit one template and everything updates.

The tradeoff is you need to understand the template syntax if you want to make complex changes. For most updates though, it's straightforward.
