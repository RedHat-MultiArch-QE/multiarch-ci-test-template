properties(
  [
    parameters(
      [
        string(
          defaultValue: 'x86_64,ppc64le',
          description: 'A comma separated list of architectures to run the test on. Valid values include [x86_64, ppc64le, aarch64, s390x].',
          name: 'ARCHES'
        ),
        string(
          defaultValue: 'SSHPRIVKEY',
          description: 'SSH private key Jenkins credential ID for Beaker/SSH operations',
          name: 'SSHPRIVKEYCREDENTIALID'
        ),
        string(
          defaultValue: 'SSHPUBKEY',
          description: 'SSH public key Jenkins credential ID for Beaker/SSH operations',
          name: 'SSHPUBKEYCREDENTIALID'
        ),
        string(
          defaultValue: 'KEYTAB',
          description: 'Kerberos keytab file Jenkins credential ID for Beaker/SSH operations',
          name: 'KEYTABID'
        ),
        string(
          defaultValue: 'https://github.com/RedHat-MultiArch-QE/multiarch-ci-libraries',
          description: 'Repo for shared libraries',
          name: 'LIBRARIES_REPO'
        ),
        string(
          defaultValue: 'v0.2',
          description: 'Version of shared libraries to use',
          name: 'LIBRARIES_VERSION'
        ),
        string(
          defaultValue: null,
          description: 'Repo for tests to run. If left blank, the current repo is assumed (*note* this default will only work for multibranch pipelines).',
          name: 'TEST_REPO'
        ),
        string(
          defaultValue: '',
          description: 'Version of repo for tests to run.',
          name: 'TEST_VERSION'
        ),
        string(
          defaultValue: 'tests',
          description: 'Directory containing tests to run. Should at least one of the follow: an ansible-playbooks directory containing one or more test directories each of which having a playbook.yml, a scripts directory containing one or more test directories each of which having a run-test.sh',
          name: 'TEST_DIR'
        )
      ]
    )
  ]
)

List arches = params.ARCHES.tokenize(',')

library changelog: false,
identifier: "multiarch-ci-libraries@${params.LIBRARIES_VERSION}",
retriever: modernSCM([$class: 'GitSCMSource',remote: "${params.LIBRARIES_REPO}"])

parallelMultiArchTest(
  arches,
  provisioningConfig.create(params),
  { Slave slave ->
    /*********************************************************/
    /* TEST BODY                                             */
    /* @param slave               Provisioned slave details. */
    /*********************************************************/
    dir('test') {
      stage ('Download Test Files') {
        if (params.TEST_REPO) {
          git url: params.TEST_REPO, branch: params.TEST_VERSION, changelog: false
        }
        else {
          checkout scm
        }
      }

      // TODO insert test body here
      stage ('Run Test') {
        if (config.runOnSlave) {
          sh "ansible-playbook ${TEST_DIR}/ansible-playbooks/*/playbook.yml"
          // TODO insert logic for calling script(s) here
        }
        else {
          sh "ansible-playbook -i '${slave.inventory}' ${TEST_DIR}/ansible-playbooks/*/playbook.yml"
          // TODO insert logic for all script(s) remotely here
        }
      }

      stage ('Archive Test Output') {
        try {
          archiveArtifacts allowEmptyArchive: true, artifacts: "${TEST_DIR}/ansible-playbooks/**/artifacts/*", fingerprint: true
          junit "${TEST_DIR}/ansible-playbooks/**/reports/*.xml"
        }
        catch (e) {
          // We don't care if this step fails
        }
        try {
          archiveArtifacts allowEmptyArchive: true, artifacts: "${TEST_DIR}/scripts/**/artifacts/*", fingerprint: true
          junit "${TEST_DIR}/scripts/**/reports/*.xml"
        }
        catch (e) {
          // We don't care if this step fails
        }
      }
    }

    /*****************************************************************/
    /* END TEST BODY                                                 */
    /* Do not edit beyond this point                                 */
    /*****************************************************************/
  },
  { Exception exception, Slave slave ->
    echo "Exception ${exception} occured on ${slave.arch}"
    if (slave.arch.equals("x86_64") || slave.arch.equals("ppc64le")) {
      currentBuild.result = 'FAILURE'
    }
  }
)
