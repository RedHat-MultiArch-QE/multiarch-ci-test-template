properties(
  [
    parameters(
      [
        string(
          defaultValue: 'x86_64,ppc64le',
          description: 'A comma separated list of architectures to run the test on. Valid values include [x86_64, ppc64le, aarch64, s390x].',
          name: 'ARCHES'
        )
      ]
    )
  ]
)

@Library('multiarch-ci-libraries@test') _

import com.redhat.multiarch.ci.Slave

def List arches = params.ARCHES.tokenize(',')
def Boolean runOnProvisionedHosts = true;
def Boolean installAnsible = true;

parallelMultiArchTest(
  arches,
  runOnProvisionedHosts,
  installAnsible,
  { Slave slave ->
    /******************************************************************/
    /* TEST BODY                                                      */
    /* @param provisionedSlave    Name of the provisioned host.       */
    /* @param arch                Architeture of the provisioned host */
    /******************************************************************/
    stage ('Download Test Files') {
      checkout scm
    }

    // TODO insert test body here
    stage ('Run Test') {
      sh 'ansible-playbook tests/ansible-playbooks/*/playbook.yml'
    }

    stage ('Archive Test Output') {
      archiveArtifacts artifacts: 'tests/ansible-playbooks/**/artifacts/*', fingerprint: true
      try {
        junit 'tests/ansible-playbooks/**/reports/*.xml'
      } catch (e) {
        // We don't care if this step fails
      }
    }

    /*****************************************************************/
    /* END TEST BODY                                                 */
    /* Do not edit beyond this point                                 */
    /*****************************************************************/
  },
  { exception, arch ->
    println("Exception ${exception} occured on ${arch}")
    if (arch.equals("x86_64") || arch.equals("ppc64le")) {
      currentBuild.result = 'FAILURE'
    }
  }
)
