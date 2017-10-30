def call(List<String> arches=[], Closure body) {
  arch_tasks=[:]
  for (arch in arches) {
    def task_name = arch
    arch_tasks[arch] = {
      task task_name
      body()
    }
  }
  parallel arch_tasks
}
