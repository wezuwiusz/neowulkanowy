#!/bin/bash -

content=$(cat < "app/src/main/play/release-notes/pl-PL/default.txt") || exit
if [[ "${#content}" -gt 500 ]]; then
    echo >&2 "Release notes content has reached the limit of 500 characters"
    exit 1
fi
