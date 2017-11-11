properties(
  [
    pipelineTriggers(
      [
        // TODO Fill out pkg-name and relevant-tag
        [
          $class: 'CIBuildTrigger',
          checks: [],
          overrides: [topic: "Consumer.rh-jenkins-ci-plugin.${UUID.randomUUID().toString()}.VirtualTopic.qe.ci.>"],
          providerName: 'Red Hat UMB',
          selector: 'name = \'pkg-name\' AND CI_TYPE = \'brew-tag\' AND (tag LIKE \'relevant-tag\')'
        ]
      ]
    ),
    parameters(
      [
        string(
          defaultValue: 'x86_64,ppc64le,aarch64,s390x',
          description: 'Architectures to run the test on.',
          name: 'ARCHES'
        ),
        string(
          defaultValue: 'master',
          description: 'Slave node to run the test on.',
          name: 'TARGET_NODE'
        )
      ]
    )
  ]
)

@Library('multiarch-ci-libraries') _

ansiColor('xterm') {
  timestamps {
    node(params.TARGET_NODE) {
      archSlave(
        { provisionedSlave, arch ->
          /************************************************************/
          /* TEST BODY                                                */
          /* @param provisionedSlave    Name of the provisioned host. */
          /************************************************************/
          stage ('Download Test Files') {
            checkout scm
            sh 'sudo yum install python-devel openssl-devel libffi-devel -y'
            sh 'sudo pip install --upgrade pip setuptools; sudo pip install ansible'
          }

          // TODO insert test body here
          stage ('Run Test') {
            sh 'ansible-playbook tests/playbooks/*/playbook.yml'
          }

          stage ('Archive Test Output') {
            archiveArtifacts 'tests/playbooks/**/report.d/*'
          }
		

          /************************************************************/
          /* END TEST BODY                                            */
          /* Do not edit beyond this point                            */
          /************************************************************/
        }
      )
    }
  }
}
