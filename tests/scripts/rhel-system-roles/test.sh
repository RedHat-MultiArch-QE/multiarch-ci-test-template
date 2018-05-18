#!/bin/bash
#
# Attempts to clone down the test package and run it
cd "$(dirname ${BASH_SOURCE[0]})"
workdir=$(pwd)
rhpkg --verbose --user=jenkins clone tests/rhel-system-roles
cd rhel-system-roles
git checkout CoreOS-rhel-system-roles-Sanity-Upstream-testsuite-multiarch-ci-1_1-1
cd Sanity/Upstream-testsuite-multiarch-ci
output_dir="$workdir/artifacts/rhel-system-roles/$(arch)"
mkdir -p $output_dir
sudo make &> $output_dir/results.txt run
grep "OVERALL RESULT" $output_dir/results.txt | grep "PASS"
