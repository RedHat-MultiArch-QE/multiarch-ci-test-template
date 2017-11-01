def call(List<String> arches=[], Closure body) {
  archTasks=[:]
  for (arch in arches) {
    def task = { a -> body(a) }
    archTasks[arch] = { task(arch) }
  }
  parallel archTasks
}
