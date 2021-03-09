#!/bin/bash
#!/bin/bash
set -o errexit
if [[ $CI_SSH_PREPARATION_SCRIPT ]] ; then
    printf '[delivery] Sending supplementary files for delivery to the server:\n'
    tar cvvf delivered-files.tar -- xstampp-deploy xstampp-docs LICENSE
    ssh server delivery "$CI_JOB_NAME" "$CI_PIPELINE_ID" "$CI_COMMIT_REF_SLUG" "$CI_COMMIT_REF_NAME" "$CI_COMMIT_SHA" < delivered-files.tar
else
    printf '[delivery] No SSH configuration present, not trying to contact the server\n'
fi