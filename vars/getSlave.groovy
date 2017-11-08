/**
 * getSlave.groovy
 *
 * Provisions a multi-arch slave.
 *
 * @param arch String specifying the architecture of the slave to provision.
 * @param runOnProvisionedHost Boolean determining whether cinch should be run post provisioning.
 * @return LinkedHashMap contained the hostName and buildNumber of the provisioned slave.
 */
def LinkedHashMap call(String arch, Boolean runOnProvisionedHost) {
  def slave = [ buildNumber: null, hostName: null, buildError: null ]

  try {
    stage('Provision Slave') {
      println("Provisioning ${arch}-slave with runOnProvisionedHost=${runOnProvisionedHost}")

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
      slave.buildNumber = buildResult.getNumber().toString()
      println "getSlave build successful: ${slave}"

      // Get results of provisioning job
      step([$class: 'CopyArtifact', filter: 'slave.properties',
          projectName: 'provision-multiarch-slave',
          selector: [
            $class: 'SpecificBuildSelector',
            buildNumber: slave.buildNumber
          ]
        ])

      // Load slave properties (you may need to turn off sandbox or approve this in Jenkins)
      def slaveProps = readProperties file: "${arch}-slave.properties"

      // Assign the appropriate slave name
      slave.hostName = slaveProps.name
      println "assigned the host name from the properties file ${slave}"
    }
  } catch (e) {
    // If provision fails, grab the build number from the error message and set build status to not built
    slave.buildNumber = ((e =~ "(provision-multiarch-slave #)([0-9]*)")[0][2])
    currentBuild.result = 'NOT_BUILT'
    slave.buildError = e
  } finally {
    println "getSlave returning ${slave}"
    return slave
  }
}
