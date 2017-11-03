def call(List<String> arches = [], Closure body) {
  archTasks = arches.collectEntries {
    [ ${it} : { body(it) } ]
  }
  parallel archTasks
}
