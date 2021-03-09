#!/bin/bash
set -o errexit
if [[ $CI_SSH_PREPARATION_SCRIPT ]] ; then
    printf '[javadoc-to-server] Sending the supplied javadoc tar.gz to server:\n'
    ssh server javadoc "$CI_JOB_NAME" "$CI_PIPELINE_ID" "$CI_COMMIT_REF_SLUG" "$CI_COMMIT_REF_NAME" "$CI_COMMIT_SHA" < "$1"
else
    printf '[javadoc-to-server] No SSH configuration present, not trying to contact the server\n'
fi