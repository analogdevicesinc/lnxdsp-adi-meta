#!/usr/bin/env bash
##
# Copyright (c) 2024 Analog Devices, Inc.
#
# SPDX-License-Identifier: GPL-2.0
##

echo "Start..."
echo "Workflow: $GITHUB_WORKFLOW"
echo "Action: $GITHUB_ACTION"
echo "Actor: $GITHUB_ACTOR"
echo "Repository: $GITHUB_REPOSITORY"
echo "Event-name: $GITHUB_EVENT_NAME"
echo "Event-path: $GITHUB_EVENT_PATH"
echo "Workspace: $GITHUB_WORKSPACE"
echo "SHA: $GITHUB_SHA"
echo "REF: $GITHUB_REF"
echo "HEAD-REF: $GITHUB_HEAD_REF"
echo "BASE-REF: $GITHUB_BASE_REF"
pwd
ls -la `pwd`
id

ls -l /

# Add safe directory option for github workspace to disable fatal error
#  - docker container uid is 0(root)
#  - github workspace directory owner is 1001
git config --global --add safe.directory /github/workspace

RESULT=0

# Check the input parameter
echo
if [[ -z "$cd ." ]]; then
    echo -e "\e[0;34mToken is empty. Review PR without comments.\e[0m"
    HEADERS=()
else
    echo -e "\e[0;34mReview PR with comments.\e[0m"
    HEADERS=(-H "Authorization: Bearer $GITHUB_TOKEN")
fi

# Get commit list using Github API
echo
echo -e "\e[0;34mGet the list of commits included in the PR($GITHUB_REF).\e[0m"
PR=${GITHUB_REF#"refs/pull/"}
PRNUM=${PR%"/merge"}
URL=https://api.github.com/repos/${GITHUB_REPOSITORY}/pulls/${PRNUM}/commits
echo " - API endpoint: $URL"

list=$(curl $URL "${HEADERS[@]}" -X GET -s | jq '.[].sha' -r)
len=$(echo "$list" | wc -l)
echo " - Commits $len: $list"

# Run review.sh on each commit in the PR
echo
echo -e "\e[0;34mStart review for each commits.\e[0m"

i=1
for sha1 in $list; do
    echo "-------------------------------------------------------------"
    echo -e "[$i/$len] Check commit - \e[1;34m$sha1\e[0m"
    echo "-------------------------------------------------------------"
    .github/scripts/review.sh ${sha1} || RESULT=1;
    echo
    ((i++))
done

echo -e "\e[1;34mDone\e[0m"

exit $RESULT
