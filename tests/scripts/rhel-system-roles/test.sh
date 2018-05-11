#!/bin/bash
#
# Attempts to clone down the test package and run it
cd "$(dirname ${BASH_SOURCE[0]})"
workdir=$(pwd)

echo "pkgs.devel.redhat.com,10.19.208.80 ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAplqWKs26qsoaTxvWn3DFcdbiBxqRLhFngGiMYhbudnAj4li9/VwAJqLm1M6YfjOoJrj9dlmuXhNzkSzvyoQODaRgsjCG5FaRjuN8CSM/y+glgCYsWX1HFZSnAasLDuW0ifNLPR2RBkmWx61QKq+TxFDjASBbBywtupJcCsA5ktkjLILS+1eWndPJeSUJiOtzhoN8KIigkYveHSetnxauxv1abqwQTk5PmxRgRt20kZEFSRqZOJUlcl85sZYzNC/G7mneptJtHlcNrPgImuOdus5CW+7W49Z/1xqqWI/iRjwipgEMGusPMlSzdxDX4JzIx6R53pDpAwSAQVGDz4F9eQ==" | sudo tee -a /etc/ssh/ssh_known_hosts

echo "Host pkgs.devel.redhat.com" | sudo tee -a /etc/ssh/ssh_config
echo "IdentityFile /home/jenkins/.ssh/id_rsa" | sudo tee -a /etc/ssh/ssh_config

sudo yum install -y yum-utils git
curl -L -O http://download.devel.redhat.com/rel-eng/internal/rcm-tools-rhel-7-server.repo
sudo yum-config-manager --add-repo rcm-tools-rhel-7-server.repo
sudo yum install -y rhpkg
git config --global user.name "jenkins"
rhpkg --verbose --user=jenkins clone tests/rhel-system-roles
cd rhel-system-roles/Sanity/Upstream-testsuite
mkdir -p "$workdir/artifacts/rhel-system-roles/$(arch)/output.txt"
sudo make &> "$workdir/artifacts/rhel-system-roles/$(arch)/output.txt" run
