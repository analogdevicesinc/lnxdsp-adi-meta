#!/usr/bin/env python3
"""
Generate getting-started guide RST files from template and configuration.

Usage:
    # Generate all boards for all releases
    python3 generate-getting-started.py

    # Generate specific board for specific release
    python3 generate-getting-started.py --board adsp-sc598-som-ezkit --version 5.0.1

    # Generate all boards for a specific release
    python3 generate-getting-started.py --version 5.0.1
"""

import argparse
import sys
from pathlib import Path
from typing import Dict, Any

try:
    import yaml
    from jinja2 import Environment, FileSystemLoader, select_autoescape
except ImportError:
    print("ERROR: Required packages not found. Install with:")
    print("  pip install pyyaml jinja2")
    sys.exit(1)


def load_config(config_file: Path) -> Dict[str, Any]:
    """Load board configuration from YAML file."""
    with open(config_file, 'r') as f:
        return yaml.safe_load(f)


def generate_guide(
    template_env: Environment,
    board_id: str,
    board_config: Dict[str, Any],
    version: str,
    release_config: Dict[str, Any],
    output_dir: Path,
    dry_run: bool = False
) -> None:
    """Generate a getting-started guide for a specific board and version."""

    template = template_env.get_template('getting-started-template.rst.j2')

    # Merge board and release configurations
    context = {
        **board_config,
        'version': version,
        **release_config
    }

    # Render template
    content = template.render(**context)

    # Generate output filename
    board_name = board_config['board_name']
    output_filename = f"Getting-Started-with-{board_name}-(Linux-for-ADSP‐SC5xx-Processors-{version}).rst"
    output_path = output_dir / output_filename

    if dry_run:
        print(f"[DRY RUN] Would generate: {output_path}")
        return

    # Write output file
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"✓ Generated: {output_path}")


def main():
    parser = argparse.ArgumentParser(
        description='Generate getting-started guides from template'
    )
    parser.add_argument(
        '--board',
        help='Board ID to generate (e.g., adsp-sc598-som-ezkit). If not specified, generates all boards.'
    )
    parser.add_argument(
        '--version',
        help='Release version to generate (e.g., 5.0.1). If not specified, generates all versions.'
    )
    parser.add_argument(
        '--config',
        type=Path,
        default=Path(__file__).parent / 'board-configs.yaml',
        help='Path to board configuration YAML file'
    )
    parser.add_argument(
        '--template-dir',
        type=Path,
        default=Path(__file__).parent,
        help='Directory containing Jinja2 templates'
    )
    parser.add_argument(
        '--output-dir',
        type=Path,
        default=Path(__file__).parent,
        help='Directory to write generated RST files'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be generated without writing files'
    )

    args = parser.parse_args()

    # Load configuration
    print(f"Loading configuration from {args.config}")
    config = load_config(args.config)

    # Setup Jinja2 environment
    env = Environment(
        loader=FileSystemLoader(args.template_dir),
        autoescape=select_autoescape(),
        trim_blocks=True,
        lstrip_blocks=True
    )

    # Determine which boards and versions to generate
    boards_to_generate = (
        {args.board: config['boards'][args.board]} if args.board
        else config['boards']
    )

    versions_to_generate = (
        {args.version: config['releases'][args.version]} if args.version
        else config['releases']
    )

    # Generate guides
    total_generated = 0
    for board_id, board_config in boards_to_generate.items():
        for version, release_config in versions_to_generate.items():
            generate_guide(
                env,
                board_id,
                board_config,
                version,
                release_config,
                args.output_dir,
                args.dry_run
            )
            total_generated += 1

    print(f"\n{'[DRY RUN] Would generate' if args.dry_run else 'Generated'} {total_generated} guide(s)")

    if not args.dry_run:
        print("\n✓ Done! Remember to:")
        print("  1. Review the generated files")
        print("  2. Update any board-specific sections in board-configs.yaml")
        print("  3. Rebuild documentation: cd .. && ./build-docs.sh")


if __name__ == '__main__':
    main()
