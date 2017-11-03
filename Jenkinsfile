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
          selector: 'name = \'openshift\' AND CI_TYPE = \'brew-tag\' AND tag (tag LIKE \'rhaos-%-rhel-%-newarches-candidate\' OR tag LIKE \'rhose-%-rhel-%-candidate\')'
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
        ),
        string(
          name: 'ORIGIN_REPO',
          description: 'Origin repo',
          defaultValue: 'https://github.com/openshift/origin.git'
        ),
        string(
          name: 'ORIGIN_BRANCH',
          description: 'Origin branch',
          defaultValue: 'master'
        ),
        string(
          name: 'OS_BUILD_ENV_IMAGE',
          description: 'openshift-release image',
          defaultValue: 'openshiftmultiarch/origin-release:golang-1.8'
        )
      ]
    )
  ]
)

@Library('multiarch-ci-test-template@master') _

ansiColor('xterm') {
  timestamps {
    node(params.TARGET_NODE) {
      arches(
        params.ARCHES.tokenize(','),
        {
          arch -> println("${arch}")
        }
      )
    }
  }
}
