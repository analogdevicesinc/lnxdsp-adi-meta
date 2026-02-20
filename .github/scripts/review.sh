#!/bin/bash
##
# Copyright (c) 2024 Analog Devices, Inc.
#
# SPDX-License-Identifier: GPL-2.0
##

# To debug the current script, please uncomment the following 'set -x' line
#set -x

if [[ -z "$CHECKPATCH_COMMAND" ]] ; then
    CHECKPATCH_COMMAND="./checkpatch.pl --no-tree"
fi

# Generate email style commit message
PATCH_FILE=$(git format-patch $1 -1)
PATCHMAIL=$($CHECKPATCH_COMMAND $PATCH_FILE)

# Internal state variables
RESULT=0
FOUND=0
MESSAGE=

#
# checkpatch.pl result format
# ---------------------------
#
# Template:
# ---------
#
# [WARNING/ERROR]: [message for code line]
# #[id]: FILE: [filename]:[line-number]
# +[code]
# [empty line]
#
# [WARNING/ERROR]: [message for commit itself]
#
# total: [n] erros, [n] warnings, [n] lines checked
#
# example:
# --------
#
# ERROR: xxxx
# #15: FILE: a.c:3:
# +int main() {
#
# ERROR: Missing Signed-off-by: line(s)
#
# total: ...
#

while read -r row
do
    # End of checkpatch.pl message
    if [[ "$row" =~ ^total: ]]; then
        echo -e "\e[1m$row\e[0m"
        break
    fi

    # Additional parsing is needed
    if [[ "$FOUND" == "1" ]]; then

        # The row is started with "#"
        if [[ "$row" =~ ^\# ]]; then
            # Split the string using ':' separator
            IFS=':' read -r -a list <<< "$row"

            # Get file-name after removing spaces.
            FILE=$(echo ${list[2]} | xargs)

            # Get line-number
            LINE=${list[3]}
        else
            # An empty line means the paragraph is over.
            if [[ -z $row ]]; then
                if [[ -z $FILE ]]; then
                    echo "::error ::${MESSAGE}"
                else
                    echo "::error file=${FILE},line=${LINE}::${MESSAGE}"
                fi
                # Output empty line
                echo

                # Code review found a problem.
                RESULT=1

                FOUND=0
                FILE=
                LINE=
            fi
        fi
    fi

    # Found warning or error paragraph
    if [[ "$row" =~ ^(WARNING|ERROR) ]]; then
        MESSAGE=$row
        FOUND=1
        FILE=
        LINE=

        echo -e "\e[1;31m$row\e[0m"
    else
        echo $row
    fi

done <<<"$PATCHMAIL"

if [[ "$RESULT" == "0" ]]; then
    echo -e "\e[1;32m>> Success\e[0m"
else
    echo -e "\e[1;31m>> Failure\e[0m"
fi

exit $RESULT
