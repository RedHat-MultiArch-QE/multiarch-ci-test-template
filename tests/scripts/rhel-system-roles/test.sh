#!/bin/bash
#
# Attempts to clone down the test package and run it
workdir=$(pwd)
git clone ssh://jpoulin@pkgs.devel.redhat.com/tests/rhel-system-roles
cd rhel-system-roles/Sanity/Upstream-testsuite
make run > $workdir/artifacts/output.txt
