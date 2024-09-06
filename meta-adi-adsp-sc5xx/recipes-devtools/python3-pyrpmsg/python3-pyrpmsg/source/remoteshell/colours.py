#! /usr/bin/env python3

"""Colour helper library for remoteshell."""

ESCAPE = "\u001b"


def graphics(n: object) -> str:
    """Macro for graphics ANSI codes."""
    return ESCAPE + "[" + str(n) + "m"


class Colours:
    """Colour codes."""

    RESET = graphics(0)

    BOLD = graphics(1)
    NORMAL = graphics(22)

    class Fore:
        BLACK = graphics(30)
        RED = graphics(31)
        GREEN = graphics(32)
        YELLOW = graphics(33)
        BLUE = graphics(34)
        MAGENTA = graphics(35)
        CYAN = graphics(36)
        WHITE = graphics(37)
        DEFAULT = graphics(39)

    class Back:
        BLACK = graphics(40)
        RED = graphics(41)
        GREEN = graphics(42)
        YELLOW = graphics(43)
        BLUE = graphics(44)
        MAGENTA = graphics(45)
        CYAN = graphics(46)
        WHITE = graphics(47)
        DEFAULT = graphics(49)


class DummyColours:
    """Dummy colours for shells that don't support ANSI colour codes."""

    RESET = ""

    BOLD = ""
    NORMAL = ""

    class Fore:
        BLACK = ""
        RED = ""
        GREEN = ""
        YELLOW = ""
        BLUE = ""
        MAGENTA = ""
        CYAN = ""
        WHITE = ""
        DEFAULT = ""

    class Back:
        BLACK = ""
        RED = ""
        GREEN = ""
        YELLOW = ""
        BLUE = ""
        MAGENTA = ""
        CYAN = ""
        WHITE = ""
        DEFAULT = ""
