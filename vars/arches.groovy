/**
 * arches.groovy
 *
 * Calls a closure for each arch specified in params.ARCHES.
 *
 * @param body Closure with a single String parameter representing an arch.
 */
def call(Closure body) {
  def archTasks = [:]
  for (arch in params.ARCHES.tokenize(',')) {
    archTasks[arch] = body(arch)
  }

  parallel archTasks
}
