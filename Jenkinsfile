properties(
  [
    parameters(
      [
        string(
          defaultValue: 'x86_64',
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
      ]
    )
  ]
)

List arches = params.ARCHES.tokenize(',')

library changelog: false, identifier: 'multiarch-ci-libraries@dev', retriever: legacySCM([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/scoheb/multiarch-ci-libraries']]])

// Populate the Provisioning Config
def config = provisioningConfig.create()
config.jobgroup = "ci-ops-central"
config.tenant = "continuous-infra"
config.dockerUrl = "172.30.1.1:5000"
config.provisioningImage = "jenkins-provisioning-slave"
config.provisioningRepoUrl = "https://github.com/scoheb/multiarch-ci-provisioner"
config.provisioningRepoRef = "dev"
config.krbPrincipal = "jenkins/ci-ops-jenkins.rhev-ci-vms.eng.rdu2.redhat.com@REDHAT.COM"
config.KEYTABCREDENTIALID = "KEYTAB"
config.runOnSlave = false
config.installAnsible= false

parallelMultiArchTest(
        arches,
        config,
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
              withCredentials([file(credentialsId: params.SSHPRIVKEYCREDENTIALID,
                                       variable: 'SSHPRIVKEY'),
                               file(credentialsId: params.SSHPUBKEYCREDENTIALID,
                                       variable: 'SSHPUBKEY')])
                  {


                    env.HOME = "/home/jenkins"

                    sh """
                            mkdir -p ~/.ssh
                            cp ${SSHPRIVKEY} ~/.ssh/id_rsa
                            cp ${SSHPUBKEY} ~/.ssh/id_rsa.pub
                            chmod 600 ~/.ssh/id_rsa
                            chmod 644 ~/.ssh/id_rsa.pub
                        """

                    sh "pwd"
                    sh "ansible-playbook -i \'../${slave.inventory}\' tests/ansible-playbooks/*/playbook.yml"
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
    println("Exception ${exception} occured on ${slave.arch}")
    sleep 200
    if (slave.arch.equals("x86_64") || slave.arch.equals("ppc64le")) {
      currentBuild.result = 'FAILURE'
    }
  }
)
