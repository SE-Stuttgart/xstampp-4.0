#!/bin/bash
set -o errexit
if [[ $CI_SSH_PREPARATION_SCRIPT ]] ; then
    printf '[deploy] Notifying server that it has to deploy to xstage\n'
    ssh server "deploy"$1 "$CI_JOB_NAME" "$CI_PIPELINE_ID" "$CI_COMMIT_REF_SLUG" "$CI_COMMIT_REF_NAME" "$CI_COMMIT_SHA"
else
    printf '[deploy] No SSH configuration present, not trying to contact the server\n'
fi