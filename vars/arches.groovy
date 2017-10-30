def call(List<String> arches=[], Closure body) {
  archTasks=[:]
  for (arch in arches) {
    def task_name = arch
    archTasks[arch] = {
      body()
    }
  }
  parallel archTasks
}
