def call(String arch, Closure body, def Boolean runOnProvisionedHost = false) {
  def provisionedNodeBuildNumber = null
  def provisionedNode = null

  try {
    try {
      stage('Provision Slave') {
        throw new Exception("Provisioning ${arch} slave and runOnProvisionedHost=${runOnProvisionedHost}")
        /*
        def buildResult = build([
            job: 'provision-multiarch-slave',
            parameters: [
              string(name: 'ARCH', value: arch),
              booleanParam(name: 'CONNECT_AS_SLAVE', value: runOnProvisionedHost)
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
        def slaveProps = readProperties file: "${params.ARCH}-slave.properties"

        // Assign the appropriate slave name
        provisionedNode = slaveProps.name
        */
      }
    } catch (e) {
      // If provision fails, grab the build number from the error message and set build status to not built
      provisionedNodeBuildNumber = ((e =~ "(provision-multiarch-slave #)([0-9]*)")[0][2])
      currentBuild.result = 'NOT_BUILT'
      throw e
    }

    if (connectAsSlave) {
      node(provisionedNode) {
        body(provisionedNode)
      }
      return
    }

    // The body will connect to the node in its own fashion
    body(provisionedNode)
  } catch (e) {
    // This is just a wrapper step to ensure that teardown is run upon failure
    println(e)
  } finally {
    // Ensure teardown runs before the pipeline exits
    stage ('Teardown Slave') {
      build(
        [
          job: 'teardown-multiarch-slave',
          parameters: [
            string(name: 'BUILD_NUMBER', value: provisionedNodeBuildNumber)
          ],
          propagate: true,
          wait: true
        ]
      )
    }
  }
}
