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

printf '[latest-master-to-xdev] latest appears to be %s\n' "$latest_marker_file"
printf '[latest-master-to-xdev] identifier %s\n' "$latest_marker_name_base"

read -p 'Q: [latest-master-to-xlive] Really send latest master to xdev? Type uppercase yes to continue > '
if ! [[ "$REPLY" == "YES" ]] ; then
	printf '[latest-master-to-xdev] Aborting due to user input\n' 1<&2
	exit 1
fi

set -x

ssh xdev mkdir -v "deploy/$latest_marker_name_base"
ssh xdev tar xv --directory="deploy/$latest_marker_name_base" < "${latest_marker_file_base}_angular-build.tar"
ssh xdev tar xv --directory="deploy/$latest_marker_name_base" < "${latest_marker_file_base}_spring-build.tar"
ssh xdev rm -fv "deploy/current_version"
ssh xdev ln -sv -- "$latest_marker_name_base" "deploy/current_version"