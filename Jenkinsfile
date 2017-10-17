properties([
    parameters([
        choiceParam(
          name: 'ARCH',
          choices: "x86_64\nppc64le\naarch64\ns390x",
          description: 'Architecture'
        )
      ])
  ])

def provisionedNode = null
def provisionedNodeBuildNumber = null

ansiColor('xterm') {
  timestamps {
    node('master') {
      try {
        try {
          stage('Provision Slave') {
            def buildResult = build([
                job: 'provision-multiarch-slave',
                parameters: [
                  string(name: 'ARCH', value: arch),
                  // TODO Add repo and file path for optional post provision configuration
                  string(name: 'CONFIG_REPO', value: '')
                  string(name: 'CONFIG_FILE', value: '')
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

        node(provisionedNode) {
          try {
            // TODO INSERT TEST CODE HERE
          } catch (e) {
            println(e)
          } finally {
            stage ('Archive Test Output') {
              archiveArtifacts '_output/scripts/**/*'
              junit '_output/scripts/**/*.xml'
            }
          }
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
