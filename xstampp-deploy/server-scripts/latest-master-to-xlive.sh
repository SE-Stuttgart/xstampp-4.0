#!/bin/bash
unset latest_marker_file latest_marker_name latest_marker_file_base latest_marker_name_base

latest_marker_file=$(readlink -e "$HOME"/artifacts/latest-master_pipeline-complete.passed)
latest_marker_name=$(readlink "$HOME"/artifacts/latest-master_pipeline-complete.passed)
latest_marker_file_base=${latest_marker_file%_pipeline-complete.passed}
latest_marker_name_base=${latest_marker_name%_pipeline-complete.passed}

set -o errexit

if [[ "$latest_marker_name_base" == */* ]] || ! [[ "$latest_marker_name_base" == *_master_* ]] ; then
	printf '[accept-artifacts] ERROR: Invalid base name "%s"\n' $latest_marker_name_base 1>&2
	exit 1
fi

printf '[latest-master-to-xlive] latest appears to be %s\n' "$latest_marker_file"
printf '[latest-master-to-xlive] identifier %s\n' "$latest_marker_name_base"


read -p 'Q: [latest-master-to-xlive] Really send latest master to xlive? Type uppercase yes to continue > '
if ! [[ "$REPLY" == "YES" ]] ; then
	printf '[latest-master-to-xlive] Aborting due to user input\n' 1<&2
	exit 1
fi

set -x

ssh xlive mkdir -v "deploy/$latest_marker_name_base"
ssh xlive tar xv --directory="deploy/$latest_marker_name_base" < "${latest_marker_file_base}_angular-build.tar"
ssh xlive tar xv --directory="deploy/$latest_marker_name_base" < "${latest_marker_file_base}_spring-build.tar"
ssh xlive rm -fv "deploy/current_version"
ssh xlive ln -sv -- "$latest_marker_name_base" "deploy/current_version"
