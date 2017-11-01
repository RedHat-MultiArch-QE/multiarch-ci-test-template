def call(List<String> arches=[], Closure body) {
  archTasks=[:]
  for (arch in arches) {
    def a = arch
    archTasks[a] = { body(a) }
  }
  parallel archTasks
}
