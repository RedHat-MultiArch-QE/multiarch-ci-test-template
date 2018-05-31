# Multi-Arch CI Test Template
This project serves as a template for multi-arch tests for RedHat's downstream CI. Currently, this template test can be run only on Jenkins enviroments deployed with the tools provided by the [Multi-Arch CI Provisioner](https://github.com/RedHat-MultiArch-QE/multiarch-ci-provisioner). The only known environment that supports this exists internal to RedHat; however, efforts are being made to support further multi-arch testing upstream as part of the great CentOS CI initiative (see [multiarch-openshift-ci](https://github.com/CentOS-PaaS-SIG/multiarch-openshift-ci) for an example of this effort specifically for OpenShift). You can see our latest template release notes [here](https://github.com/RedHat-MultiArch-QE/multiarch-ci-test-template/releases).

## Table of Contents
- [Getting Started](#getting-started)
- [License](#license)
- [Authors](#authors)

## Getting Started
For directions on how to add your own tests more details on the pipeline, please visit our [wiki](https://github.com/RedHat-MultiArch-QE/multiarch-ci-test-template/wiki).

## License
This project is licensed under the Apache 2.0 License - see the LICENSE file for details.

## Authors
This project would not be possible without the work of following people.
- [jaypoulz](https://github.com/jaypoulz/) - *Develops and maintains the current template.*
- [detiber](https://github.com/detiber/) - *Engineered the starting point for this template in [multiarch-openshift-ci](https://github.com/CentOS-PaaS-SIG/multiarch-openshift-ci).*
- [arilivigni](https://github.com/arilivigni) - *Provided basis of the Jenkinsfile via [ci-pipeline](https://github.com/CentOS-PaaS-SIG/ci-pipeline).*
- [dbenoit](https://github.com/dbenoit17) - *Added support for script based tests and is working to add support for multi-arch containers.*
