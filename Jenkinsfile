properties(
  [
    pipelineTriggers(
      [
        // TODO Fill out pkg-name and relevant-tag
        [
          $class: 'CIBuildTrigger',
          checks: [],
          providerName: 'CI Subscribe',
          selector: 'name = \'pkg-name\' AND CI_TYPE = \'brew-tag\' AND tag LIKE \'relevant-tag\''
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

@Library("multiarch-ci-test-template")

ansiColor('xterm') {
  timestamps {
    node(params.TARGET_NODE) {
      arches(
        params.ARCHES.split(','),
        {
          arch ->
          slave(
            arch,
            {
              provisionedSlave ->
              /************************************************************/
              /* TEST BODY                                                */
              /* @param provisionedSlave    Name of the provisioned host. */
              /************************************************************/

              // TODO write test body here

              /************************************************************/
              /* END TEST BODY                                            */
              /* Do not edit beyond this point                            */
              /************************************************************/
            }
          )
        }
      )
    }
  }
}
