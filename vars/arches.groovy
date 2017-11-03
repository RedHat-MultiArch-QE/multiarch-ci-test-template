def call(List<String> arches = [], Closure body) {
  def archTasks = [:]
  for (arch in arches) {
    archTasks[arch] = { body(new String(arch)) }
  }

  parallel archTasks
}
