#!/bin/bash
set -o errexit
if [[ $CI_SSH_PREPARATION_SCRIPT ]] ; then
    printf '[artifacts-to-server] Sending artifacts to server:\n'
    tar cvvf artifacts.tar -- "$@"
    ssh server artifacts "$CI_JOB_NAME" "$CI_PIPELINE_ID" "$CI_COMMIT_REF_SLUG" "$CI_COMMIT_REF_NAME" "$CI_COMMIT_SHA" < artifacts.tar
else
    printf '[artifacts-to-server] No SSH configuration present, not trying to contact the server\n'
fi