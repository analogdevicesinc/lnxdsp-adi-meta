#! /usr/bin/env python3

"""Utilities not specific to any of the other modules."""


def getarg(argv: list[str], dash: str, double: str | None = None) -> str | None:
    """Get a CLI argument's value."""
    try:
        i = argv.index("-" + dash)
        val = argv[i+1]
    except ValueError:
        val = None
    except IndexError:
        raise IndexError("Malformed CLI Arguments.")

    if val is None and double is not None:
        try:
            i = argv.index("--" + double)
            val = argv[i+1]
        except ValueError:
            val = None
        except IndexError:
            raise IndexError("Malformed CLI Arguments.")

    return val
