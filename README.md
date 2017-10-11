# multiarch-openshift-ci

### Pipeline Info
This repository backs the CI pipeline hosted here; https://ci.centos.org/view/PaaS-SIG/job/paas-multiarch-test/

The job is currently manually triggered and can provision x86_64 and ppc64le hosts from Duffy depending on the value of the ARCH parameter for the Job.

The job also merges in a set of multi-arch enabling changes from the multiarch branch here: https://github.com/detiber/origin/tree/multiarch These changes are being temporarily carried in this branch until they are merged and accepted in upstream Origin.

### Contributing fixes
Issues related to host and test setup for running the jobs should be reported and addressed in this repository.

Issues that are reproducable locally on an x86_64 host should be reported and addressed in https://github.com/openshift/origin

Issues that are reproducable locally on a non-x86_64 host should be reported and addressed in the multiarch branch of https://github.com/detiber/origin or https://github.com/openshift/origin if the multiarch branch changeset is not required for the changes.
