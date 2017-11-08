/**
 * archSlave.groovy
 *
 * Runs closure body on a multi-arch slave for each arch in params.ARCHES.
 *
 * @param body Closure that takes a single String parameter representing the hostName of the slave.
 * @param runOnSlave Boolean that specificies whether the
 *        closure should be run on directly on the provisioned slave.
 */
def call(Closure body, def Boolean runOnSlave = false) {
  arches(
    { a ->
      def arch = new String(a)
      return {
        def slave = [ buildNumber: null, hostName: null, buildError: null ]
        try {
          println arch
          slave = getSlave(arch, runOnSlave)

          // If the provision failed, there will be an error
          if (slave.buildError != null) {
            throw slave.buildError
          }

          if (slave.hostName == null) {
            throw new Exception("Failed to provisioned multiarch slave: ${slave}")
          }

          println slave

          if (runOnSlave) {
            node(slave.hostName) {
              body(slave.hostName)
            }
            return
          }

          body(slave.hostName)
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
                  string(name: 'BUILD_NUMBER', value: slave.buildNumber)
                ],
                propagate: true,
                wait: true
              ]
            )
          }
        }
      }
    }
  )
}
