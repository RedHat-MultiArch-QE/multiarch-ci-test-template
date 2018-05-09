#!/bin/bash
#
# Attempts to clone down the test package and run it
cd "$(dirname ${BASH_SOURCE[0]})"
workdir=$(pwd)

touch ~/.ssh/known_hosts
ssh-keyscan -H 10.19.208.80 >> ~/.ssh/known_hosts
ssh-keyscan -H pkgs.devel.redhat.com >> ~/.ssh/known_hosts

sudo yum install -y yum-utils
curl -L -O http://download.devel.redhat.com/rel-eng/internal/rcm-tools-rhel-7-server.repo
sudo yum-config-manager --add-repo rcm-tools-rhel-7-server.repo
sudo yum install -y rhpkg

rhpkg clone tests/rhel-system-roles
cd rhel-system-roles/Sanity/Upstream-testsuite
mkdir -p $workdir/artifacts
make &> $workdir/artifacts/output.txt run
