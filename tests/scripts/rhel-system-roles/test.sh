#!/bin/bash
#
# Attempts to clone down the test package and run it
workdir=$(pwd)

touch ~/.ssh/known_hosts
ssh-keyscan -H 10.19.208.80 >> ~/.ssh/known_hosts
ssh-keyscan -H pkgs.devel.redhat.com >> ~/.ssh/known_hosts

sudo yum install -y yum-utils
wget -O pnt-devops-rhpkg-continuous-delivery.repo https://copr.devel.redhat.com/coprs/pnt-devops/rhpkg-continuous-delivery/repo/rhel-7/pnt-devops-rhpkg-continuous-delivery
sudo yum-config-manager --add-repo pnt-devops-rhpkg-continuous-delivery.repo
sudo yum install -y rhpkg

rhpkg clone tests/rhel-system-roles
cd rhel-system-roles/Sanity/Upstream-testsuite
make run > $workdir/artifacts/output.txt
