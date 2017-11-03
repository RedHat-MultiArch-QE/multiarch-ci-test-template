def call(List<String> arches = [], Closure body) {
  archTasks = arches.collectEntries {
    [ arch, { body(arch) } ]
  }
  parallel archTasks
}
