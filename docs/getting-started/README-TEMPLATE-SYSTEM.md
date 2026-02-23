# Getting Started Guide Template System

This directory contains a template-based system for generating version-specific getting-started guides. This eliminates duplication and makes it easy to produce documentation for new releases.

## Overview

**Problem:** Previously, each release required duplicating 4+ getting-started guides (one per board), leading to 26+ files with hardcoded version numbers that were difficult to maintain.

**Solution:** A template system that:
- Uses a single Jinja2 template for all boards
- Stores board-specific parameters in YAML configuration
- Generates version-specific RST files automatically
- Makes creating docs for a new release a simple command

## Files

```
getting-started/
├── getting-started-template.rst.j2    # Jinja2 template (master document)
├── board-configs.yaml                 # Board and release parameters
├── generate-getting-started.py        # Generator script
├── README-TEMPLATE-SYSTEM.md          # This file
└── Generated files:
    ├── Getting-Started-with-ADSP-SC598-(Linux-for-ADSP‐SC5xx-Processors-5.0.1).rst
    ├── Getting-Started-with-ADSP-SC594-(Linux-for-ADSP‐SC5xx-Processors-5.0.1).rst
    └── ...
```

## Quick Start

### Prerequisites

Install required Python packages:

```bash
pip install pyyaml jinja2
```

### Generate All Guides

Generate all boards for all releases:

```bash
cd docs/getting-started
python3 generate-getting-started.py
```

### Generate Specific Release

Generate all boards for version 5.0.1:

```bash
python3 generate-getting-started.py --version 5.0.1
```

### Generate Specific Board

Generate SC598 guide for version 5.0.1:

```bash
python3 generate-getting-started.py --board adsp-sc598-som-ezkit --version 5.0.1
```

### Dry Run

Preview what would be generated:

```bash
python3 generate-getting-started.py --dry-run
```

## Adding a New Release

To create guides for a new release (e.g., 5.0.2):

1. **Update `board-configs.yaml`** with release-specific information:

```yaml
releases:
  "5.0.2":
    ubuntu_version: "22.04 LTS"
```

2. **Generate guides:**

```bash
python3 generate-getting-started.py --version 5.0.2
```

3. **Review and customize:**
   - Check generated files for accuracy
   - Update board-specific sections in `board-configs.yaml` if needed
   - Regenerate if you make changes

4. **Rebuild documentation:**

```bash
cd ..
./build-docs.sh
```

## Adding a New Board

To add a new board (e.g., SC600):

1. **Add board configuration to `board-configs.yaml`:**

```yaml
boards:
  adsp-sc600-ezkit:
    board_name: "ADSP-SC600"
    board_slug: "adsp-sc600-ezkit"
    machine_name: "adsp-sc600-ezkit"
    arch: "cortexa55"
    openocd_target: "adspsc60x_a55"
    jtag_tap: "adspsc60x"
    gdb_prefix: "aarch64-adi_glibc-linux"
    # ... add other board-specific parameters
```

2. **Generate guides:**

```bash
python3 generate-getting-started.py --board adsp-sc600-ezkit
```

## Configuration Parameters

### Release Parameters (`releases` section)

- `ubuntu_version`: Ubuntu version used for this release (e.g., "22.04 LTS")

### Board Parameters (`boards` section)

Common parameters for all boards:

- `board_name`: Display name (e.g., "ADSP-SC598")
- `board_slug`: URL-safe identifier (e.g., "adsp-sc598-som-ezkit")
- `machine_name`: Yocto machine name
- `arch`: Architecture string for SDK (e.g., "cortexa55", "cortexa5t2hf-neon")
- `openocd_target`: OpenOCD target configuration
- `jtag_tap`: JTAG tap name
- `gdb_prefix`: GDB toolchain prefix
- `sdk_files_example`: Example SDK file listing
- `hardware_image`: RST directive for hardware connection image
- `hardware_connections`: Bullet list of cable connections
- `boot_mode_settings`: Boot mode switch configuration
- `additional_boot_sections`: Optional extra boot methods (SD, USB, etc.)

Optional boolean flags:
- `som_rev_note`: Include SOM revision configuration section
- `som_rev_default`: Default SOM/carrier revision text
- `sdk_machine_note`: Include SDKMACHINE configuration note

## Template Variables

The template (`getting-started-template.rst.j2`) uses these Jinja2 variables:

### Substitutions (available via `|variable|` in RST)
- `|version|` - Release version
- `|release_manifest|` - Manifest filename
- `|sdk_path|` - SDK installation path
- `|sdk_installer|` - SDK installer filename

### Jinja2 Variables (used in template logic)
- `{{ board_name }}` - Board display name
- `{{ machine_name }}` - Yocto machine identifier
- `{{ arch }}` - CPU architecture
- `{% if som_rev_note %}` - Conditional sections

## Customizing the Template

To modify the template for all boards:

1. Edit `getting-started-template.rst.j2`
2. Use Jinja2 syntax for board-specific content:
   ```jinja2
   {% if some_condition %}
   This appears conditionally
   {% endif %}

   {{ board_name }} displays a variable
   ```
3. Regenerate all guides:
   ```bash
   python3 generate-getting-started.py
   ```

## Workflow for New Releases

### Option 1: Quick Update (recommended)

```bash
# 1. Update release config
vim board-configs.yaml

# 2. Generate new version
python3 generate-getting-started.py --version 5.0.2

# 3. Test build
cd ..
./build-docs.sh

# 4. Commit
git add getting-started/
git commit -m "docs: Add getting-started guides for 5.0.2"
```

### Option 2: Incremental Review

```bash
# 1. Dry run to preview
python3 generate-getting-started.py --version 5.0.2 --dry-run

# 2. Generate one board first
python3 generate-getting-started.py --board adsp-sc598-som-ezkit --version 5.0.2

# 3. Review and adjust config
vim board-configs.yaml

# 4. Generate remaining boards
python3 generate-getting-started.py --version 5.0.2
```

## Advanced Usage

### Custom Output Directory

```bash
python3 generate-getting-started.py --output-dir /tmp/preview
```

### Using Different Config File

```bash
python3 generate-getting-started.py --config my-custom-config.yaml
```

## Future Enhancements

Consider implementing:

1. **Sphinx-multiversion integration** for version switcher UI
2. **Version-specific content blocks** in template (e.g., features added in 5.0.1)
3. **Automated validation** of generated RST files
4. **CI/CD integration** to regenerate on release tags
5. **Landing page generation** with links to all versions

## Troubleshooting

### Import Error: No module named 'yaml'

```bash
pip install pyyaml jinja2
```

### Template not found

Make sure you're running the script from the `getting-started` directory or use `--template-dir`:

```bash
python3 generate-getting-started.py --template-dir /path/to/templates
```

### Encoding issues

The script uses UTF-8 encoding. If you see encoding errors, ensure your terminal supports UTF-8.

## Migration from Old System

To transition from hardcoded versioned files:

1. Keep existing files as backup
2. Generate new files using template system
3. Compare generated vs. existing files
4. Adjust `board-configs.yaml` to match any custom content
5. Once validated, remove old hardcoded files

## Support

For issues or questions:
- Check this README
- Review example configurations in `board-configs.yaml`
- Examine the template `getting-started-template.rst.j2`
- Post in EngineerZone or open a GitHub issue
