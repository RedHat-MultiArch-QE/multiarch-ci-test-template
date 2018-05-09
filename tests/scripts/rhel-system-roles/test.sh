#!/bin/bash
#
# Attempts to clone down the test package and run it
workdir=$(pwd)

sh-keyscan -H 10.19.208.80 >> ~/.ssh/known_hosts
ssh-keyscan -H pkgs.devel.redhat.com >> ~/.ssh/known_hosts
sudo yum install rhpkg
rhpkg clone tests/rhel-system-roles
cd rhel-system-roles/Sanity/Upstream-testsuite
make run > $workdir/artifacts/output.txt
