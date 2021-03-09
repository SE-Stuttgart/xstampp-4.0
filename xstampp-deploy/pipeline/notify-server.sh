#!/bin/bash
set -o errexit
if [[ $CI_SSH_PREPARATION_SCRIPT ]] ; then
    printf '[notify-server] Notifying server that the build was successful\n'
    ssh server passed "$CI_JOB_NAME" "$CI_PIPELINE_ID" "$CI_COMMIT_REF_SLUG" "$CI_COMMIT_REF_NAME" "$CI_COMMIT_SHA"
else
    printf '[notify-server] No SSH configuration present, not trying to contact the server\n'
fi
