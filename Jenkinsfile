properties([
    pipelineTriggers([
        // TODO Fill out pkg-name and relevant-tag
        [$class: 'CIBuildTrigger', 
          checks: [], 
          providerName: 'CI Subscribe', 
          selector: 'name = \'pkg-name\' AND CI_TYPE = \'brew-tag\' AND tag LIKE \'relevant-tag\''
        ]
    ]),
    parameters([
        choiceParam(
          choices: 'x86_64\nppc64le\naarch64\ns390x',
          defaultValue: 'x86_64',
          description: 'Architecture',
          name: 'ARCH'
        ),
        booleanParam(
          defaultValue: true,
          description: 'Connect the provisioned slave node to the Jenkins master to run the test on it directly.',
          name: 'CONNECT_AS_SLAVE'
        ),
        string(
          defaultValue: 'master',
          description: 'Default node to run the test from. If CONNECT_AS_SLAVE is true, only the provisioning and teardown will be run on this node.',
          name: 'TARGET_NODE'
        )
    ])
])

def provisionedNode = null
def provisionedNodeBuildNumber = null

ansiColor('xterm') {
  timestamps {
    node(params.TARGET_NODE) {
      try {
        try {
          stage('Provision Slave') {
            def buildResult = build([
                job: 'provision-multiarch-slave',
                parameters: [
                  string(name: 'ARCH', value: arch),
                  boolean(name: 'CONNECT_AS_SLAVE', value: params.CONNECT_AS_SLAVE),
                  // TODO Add repo and file path for optional post provision configuration
                  string(name: 'CONFIG_FILE', value: ''),
                  string(name: 'CONFIG_REPO', value: '')
                ],
                propagate: true,
                wait: true
              ])

            // If provision was successful, you will be able to grab the build number
            provisionedNodeBuildNumber = buildResult.getNumber().toString()

            // Get results of provisioning job
            step([$class: 'CopyArtifact', filter: 'slave.properties',
                projectName: 'provision-multiarch-slave',
                selector: [
                  $class: 'SpecificBuildSelector',
                  buildNumber: provisionedNodeBuildNumber
                ]
              ])

            // Load slave properties (you may need to turn off sandbox or approve this in Jenkins)
            def slaveProps = readProperties file: 'slave.properties'

            // Assign the appropriate slave name
            provisionedNode = slaveProps.name
          }
        } catch (e) {
          // If provision fails, grab the build number from the error message and set build status to not built
          provisionedNodeBuildNumber = ((e =~ "(provision-multiarch-slave #)([0-9]*)")[0][2])
          currentBuild.result = 'NOT_BUILT'
          throw e
        }

        if (params.CONNECT_AS_SLAVE) {
          node(provisionedNode) {
            // TODO Insert test code to run directly on provisioned node here
          }
        } else {
          // TODO Insert test code to connect and test the provisioned node from your static slave here
        }
      } catch (e) {
        // This is just a wrapper step to ensure that teardown is run upon failure
        println(e)
      } finally {
        // Ensure teardown runs before the pipeline exits
        stage ('Teardown Slave') {
          build([job: 'teardown-multiarch-slave',
              parameters: [
                string(name: 'BUILD_NUMBER', value: provisionedNodeBuildNumber)
              ],
              propagate: true,
              wait: true
          ])
        }
      }
    }
  }
}
