# Multi-Arch CI Test Template
This project serves as a template for multi-arch tests for RedHat's downstream CI. Currently, this template test can be run only on Jenkins enviroments deployed with the tools provided by the [Multi-Arch CI Pipeline](https://github.com/RedHat-MultiArch-QE/multiarch-ci-pipeline). The only known environment that supports this exists internal to RedHat; however, efforts are being made to support further multi-arch testing upstream as part of the great CentOS CI initiative (see [multiarch-openshift-ci](https://github.com/CentOS-PaaS-SIG/multiarch-openshift-ci) for an example of this effort specifically for OpenShift). You can see our latest template release notes [here](https://github.com/RedHat-MultiArch-QE/multiarch-ci-test-template/releases).

## Table of Contents
- [Getting Started](#getting-started)
  - [Forking the Template](#forking-the-template)
  - [Creating Your Own Jenkins Job](#creating-your-own-jenkins-job)
  - [Running the Test](#running-the-test)
- [License](#license)
- [Further Documentation](#further-documentation)

## Getting Started
The only current Jenkins [instance](https://multiarch-qe-aos-jenkins.rhev-ci-vms.eng.rdu2.redhat.com) equipped with the tools and configuration capable of running this test template is internal to RedHat. This guide assumes that you have admin access to this enviroment, or have been able to set up an equivalent enviroment using Multi-Arch CI Pipeline](https://github.com/RedHat-MultiArch-QE/multiarch-ci-pipeline). Further documentation on setting up your own enviroment can be found [here](https://github.com/RedHat-MultiArch-QE/multiarch-ci-pipeline).

### Forking the Template
1. In the github UI, fork this project.

### Creating Your Own Jenkins Job
1. Login to [Multi-Arch QE Jenkins](https://multiarch-qe-aos-jenkins.rhev-ci-vms.eng.rdu2.redhat.com).
2. Navigate to your teams directory. You may need a create a directory for your team if one does not already exist.
3. Select `new item`.
4. Name your test. I recommend keeping the name lowercase and using dashes as delimiters between words, since the test name will be used as a directory name when it is run, and special characters have been known to cause problems because of this.
4. For pipeline type, select `Multi-Branch Pipeline`.
5. In the test configuration scroll down the Jenkinsfile specification. Change it from script to SCM.
6. Populate the git information with the url of your template clone repo.
7. Hit save. This should scan the repo for branches and kickoff builds for each branch. Since you cloned the template, the only branch should be master. 
8. The test will be run automatically with the default build parameters, so just by waiting for the test to complete you should see the results of the first build under artifacts.

### Running the Test
1. To run a the test manually, start by logging in to [Multi-Arch QE Jenkins](https://multiarch-qe-aos-jenkins.rhev-ci-vms.eng.rdu2.redhat.com).
2. Navigate to the test you want to run.
3. In the left panel, select build with parameters.
4. Specify the `ARCHES` you want to build on. This parameter takes a list of arches delimited by `,`. The supported arches are `x86_64`, `ppc64le`, `aarch64`, and `s390x`.
5. Specify the node to run the test from. This defaults to `master`, but could be a static slave.
6. Click build.

## License
This project is licensed under the Apache 2.0 License - see the LICENSE file for details.

## Further Documentation
For directions on how to add your own tests more details on the pipeline, please visit our [wiki](https://github.com/RedHat-MultiArch-QE/multiarch-ci-test-template/wiki).
