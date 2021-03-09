#!/bin/bash

set -o errexit

unset why jobname pipelinenumber branch fullbranch sha
read  why jobname pipelinenumber branch fullbranch sha <<< "$SSH_ORIGINAL_COMMAND"

printf '[accept-artifacts] Connection accepted on server, parameters:\n' 1>&2
printf '[accept-artifacts]     why            : %s\n' "$why" 1>&2
printf '[accept-artifacts]     jobname        : %s\n' "$jobname" 1>&2
printf '[accept-artifacts]     pipelinenumber : %s\n' "$pipelinenumber" 1>&2
printf '[accept-artifacts]     branch         : %s\n' "$branch" 1>&2
printf '[accept-artifacts]     fullbranch     : %s\n' "$fullbranch" 1>&2
printf '[accept-artifacts]     sha            : %s\n' "$sha" 1>&2

pipelinename="$pipelinenumber"_"$branch"_"$sha"
filebasename="$pipelinename"_"$jobname"

if [[ "$filebasename" == */* ]] ; then
	printf '[accept-artifacts] ERROR: slash in arguments?\n' 1>&2
	exit 1
fi


case "$why" in
artifacts)
	# cleanup
	find /opt/xstampp/xstampp-ci/artifacts/ -mindepth 1 -maxdepth 1 -name '*.tar' \( \( -mtime +40 \) -o \( -mtime +20 -\! -name '*_master_*_*.tar' \) \) -delete

	printf '[accept-artifacts] Writing artifacts to %s\n' "$filebasename".tar 1>&2
	cat > "$HOME"/artifacts/"$filebasename".tar
	;;
javadoc)
	! [[ -d /opt/xstampp-webroot/apidocs ]] || rm -r /opt/xstampp-webroot/apidocs
	mkdir -v /opt/xstampp-webroot/apidocs
	printf '[accept-artifacts] Saving javadoc to %s and extracting.\n' "$filebasename".tar 1>&2
	cd /opt/xstampp-webroot/apidocs
	tee "$HOME"/artifacts/"$filebasename".tar.gz | tar xz --strip-components=1
	;;
passed)
	printf '[accept-artifacts] Creating %s\n' "$filebasename".passed 1>&2
	touch "$HOME"/artifacts/"$filebasename".passed
	rm -fv "$HOME"/artifacts/latest-"$branch"_"$jobname".passed
	ln -sv "$filebasename".passed "$HOME"/artifacts/latest-"$branch"_"$jobname".passed
	;;
delivery)
	printf '[accept-artifacts] Building artifact for delivery.\n'
	deliveryname="xstampp_$pipelinenumber"_"$branch"_"${sha::8}"

	set -x
	mkdir -v /opt/xstampp/xstampp-ci/deliver-temp/"$deliveryname"
	cd /opt/xstampp/xstampp-ci/deliver-temp/"$deliveryname"
	tar xvvf /opt/xstampp/xstampp-ci/artifacts/"$pipelinename"_spring-build.tar
	tar xvvf /opt/xstampp/xstampp-ci/artifacts/"$pipelinename"_angular-build.tar
	tee "$HOME"/artifacts/"$filebasename".tar | tar xvv
	cp -R /opt/xstampp/xstampp-ci/deliver-temp/"$deliveryname"/xstampp-deploy/server-scripts /opt/xstampp/xstampp-ci
	chmod +x /opt/xstampp/xstampp-ci/server-scripts/*
	cd /opt/xstampp/xstampp-ci/deliver-temp
	tar czf /opt/xstampp-webroot/builds/"$deliveryname".tgz "$deliveryname"
	rm -rf /opt/xstampp/xstampp-ci/deliver-temp/"$deliveryname"
	rm -fv /opt/xstampp-webroot/builds/xstampp_latest_"$branch".tgz
	ln -sv "$deliveryname".tgz /opt/xstampp-webroot/builds/xstampp_latest_"$branch".tgz
	;;
deploystage)
	printf '[accept-artifacts] Deploying to xstage.\n' 1>&2 
	./latest-master-to-xstage.sh 
	;;
*)
	printf '[accept-artifacts] Unknown command specified!\n' 1>&2
	exit 1
	;;
esac
