# Multi-Arch CI Test Template
This project is a template for multi-arch tests for RedHat's downstream CI. You can see our latest release notes [here](https://github.com/jaypoulz/multiarch-ci-test-template/releases).

## Overview
Currently, this template is equipped on to run on a maintained installation of the [Multi-Arch CI Pipeline](https://github.com/jaypoulz/multiarch-ci-pipeline) project. However, since development work on end-to-end installation for that project is currently a work in progress, there exists only one such maintained [instance](https://multiarch-qe-aos-jenkins.rhev-ci-vms.eng.rdu2.redhat.com), which is only accessible from within the RedHat network. It is possible that this project may be generalized for CentOS CI Jenkins environments in the future (see https://github.com/CentOS-PaaS-SIG/multiarch-openshift-ci for an example of this effort specifically for OpenShift), but currently all efforts are focused on setting up this generalized test infrastructure for internal RedHat resources.

## Using the Template
These are all the changes to the template that should be made in order to run the test.

### Build Trigger
This is where you'd set the JMS trigger for the brew event that kicks off your build. In upstream tests, this would be replaced by a Github PR trigger.
```
// TODO Fill out pkg-name and relevant-tag
selector: 'name = \'pkg-name\' AND CI_TYPE = \'brew-tag\' AND tag LIKE \'relevant-tag\''

```

### Parameters
When kicking off the test, there are 3 key parameters.
- `ARCH` - The build architecture you want provisioned
- `CONNECT_AS_SLAVE` - If you intend to run your tests on the provisioned node directly, this should be set to `true`.
- `TARGET_NODE` - this is the node the test runs from by default. If `CONNECT_AS_SLAVE` is set, the body of the test will be performed on the provisioned slave. Otherwise, it will be run on this node (which could be a static slave).

### Post Provisioning Configuration
Should you want to run some configuration after provisioning the machine, you can specific a public repo that contains an [ansible-playbook](http://docs.ansible.com/ansible/latest/playbooks.html) that can be run to perform this configuration. The `CONFIG_FILE` string specifies the file path for the playbook within the `CONFIG_REPO` repository.
```
// TODO Add repo and file path for optional post provision configuration
string(name: 'CONFIG_FILE', value: ''),
string(name: 'CONFIG_REPO', value: '')
```

### Running the Test Body
As declared above, you have the option to run your test body directly on the provisioned slave. If so, you would fill in your test body here.
```
if (params.CONNECT_AS_SLAVE) {
  node(provisionedNode) {
    // TODO Insert test code to run directly on provisioned node here
  }         
}
```
Otherwise, the test will be run from the target node. You can override the body here.
```
else {
  // TODO Insert test code to connect and test the provisioned node from your static slave here
}
```
