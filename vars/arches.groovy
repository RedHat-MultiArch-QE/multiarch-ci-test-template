def call(List<String> arches=[], Closure body) {
  archTasks=[:]
  for (arch in arches) {
    def task = { arch -> body(arch) }
    archTasks[arch] = { task(arch) }
  }
  parallel archTasks
}
