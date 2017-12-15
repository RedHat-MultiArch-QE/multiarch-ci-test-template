properties(
  [
    //pipelineTriggers(
    //  [
    //    // TODO Fill out pkg-name and relevant-tag
    //    [
    //      $class: 'CIBuildTrigger',
    //      checks: [],
    //      overrides: [topic: "Consumer.rh-jenkins-ci-plugin.${UUID.randomUUID().toString()}.VirtualTopic.qe.ci.>"],
    //      providerName: 'Red Hat UMB',
    //      selector: 'name = \'pkg-name\' AND CI_TYPE = \'brew-tag\' AND (tag LIKE \'relevant-tag\')'
    //    ]
    //  ]
    //),
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

println arches

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
