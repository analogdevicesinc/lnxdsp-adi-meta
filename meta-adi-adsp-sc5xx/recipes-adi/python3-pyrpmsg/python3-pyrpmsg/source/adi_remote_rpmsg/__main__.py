#! /usr/bin/env python3

"""Interface for the CLI and Remote applications."""

import sys

from . import CLI, Remote
from remoteshell.colours import Colours, DummyColours

BANNER = """############
##  ########
##     #####    Analog Devices, Inc.
##        ##
##     #####    Remote RPMsg Interface
##  ########
############
"""

if __name__ == "__main__":
    if "--nocolour" in sys.argv:
        colour = False
        c = DummyColours
    else:
        colour = True
        c = Colours

    print(c.Fore.BLUE + c.BOLD + BANNER + c.NORMAL + c.RESET)

    reverse = "--reverse" in sys.argv

    if "--cli" in sys.argv or "-c" in sys.argv:
        CLI(sys.argv, colour=colour, reverse=reverse)
    elif "--remote" in sys.argv or "-r" in sys.argv:
        Remote(sys.argv, reverse=reverse)
    else:
        print("Must specify -c/--cli or -r/--remote.")
