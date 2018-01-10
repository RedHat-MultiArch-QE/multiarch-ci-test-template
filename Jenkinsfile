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
                                        defaultValue: 'dev',
                                        description: 'Branch of shared libraries to use',
                                        name: 'LIBRARIES_BRANCH'
                                ),
                                string(
                                        defaultValue: 'https://github.com/RedHat-MultiArch-QE/multiarch-ci-libraries',
                                        description: 'Repo for shared libraries',
                                        name: 'LIBRARIES_REPO'
                                ),
                        ]
                )
        ]
)

List arches = params.ARCHES.tokenize(',')

library changelog: false,
        identifier: "multiarch-ci-libraries@${params.LIBRARIES_BRANCH}",
        retriever: legacySCM([$class: 'GitSCM', branches: [[name: "*/${params.LIBRARIES_BRANCH}"]],
                      doGenerateSubmoduleConfigurations: false,
                      extensions: [], submoduleCfg: [],
                      userRemoteConfigs:
                              [[url: "${params.LIBRARIES_REPO}"]]])

parallelMultiArchTest(
        arches,
        provisioningConfig.create(params),
  { Slave slave ->
    /*******************************************************************/
    /* TEST BODY                                                       */
    /* @param slave               Name of the provisioned host.        */
    /* @param arch                Architecture of the provisioned host */
    /*******************************************************************/
      dir('test') {
          stage ('Download Test Files') {
              checkout scm
          }

          // TODO insert test body here
          stage ('Run Test') {
              if (config.runOnSlave) {
                  sh 'ansible-playbook tests/ansible-playbooks/*/playbook.yml'
              } else {
                  sh "ansible-playbook -i \'${slave.inventory}\' tests/ansible-playbooks/*/playbook.yml"
              }
          }

          stage ('Archive Test Output') {
              try {
                  archiveArtifacts allowEmptyArchive: true, artifacts: 'tests/ansible-playbooks/**/artifacts/*', fingerprint: true
                  junit 'tests/ansible-playbooks/**/reports/*.xml'
              } catch (e) {
                  // We don't care if this step fails
              }
          }
      }

    /*****************************************************************/
    /* END TEST BODY                                                 */
    /* Do not edit beyond this point                                 */
    /*****************************************************************/
  },
  { exception, slave ->
    echo "Exception ${exception} occured on ${slave.arch}"
    if (slave.arch.equals("x86_64") || slave.arch.equals("ppc64le")) {
      currentBuild.result = 'FAILURE'
    }
  }
)
