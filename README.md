# multiarch-openshift-ci

OpenShift Multi-architecture CI Pipelines

## Generic Pipeline info

This job merges in a set of multi-arch enabling changes from the multiarch branch here: https://github.com/detiber/origin/tree/multiarch These changes are being temporarily carried in this branch until they are merged and accepted in upstream Origin.

## CentOS CI

CentOS CI Job is located here: https://ci.centos.org/view/PaaS-SIG/job/paas-multiarch-test/

There is a single parameterized pipeline job that can run against x86_64 and ppc64le arches. Aarch64 support is currently blocked on Duffy support for provisioning aarch64 hosts, and s390x support is blocked on availability of a s390x CentOS port.

The job is currently triggered manually, but the plan is to add webhooks to trigger the job automatically based on changes to the master branch of this repository, and the relevant origin repositories/branches.

## Fedora CI

Fedora CI testing has not been implemented yet.

## Other

TODO: describe custom pipeline configuration here.


## Contributing
Issues related to host and test setup for running the jobs should be reported and addressed in this repository.

Issues that are reproducable locally on an x86_64 host should be reported and addressed in https://github.com/openshift/origin

Issues that are reproducable locally on a non-x86_64 host should be reported and addressed in the multiarch branch of https://github.com/detiber/origin or https://github.com/openshift/origin if the multiarch branch changeset is not required for the changes.
