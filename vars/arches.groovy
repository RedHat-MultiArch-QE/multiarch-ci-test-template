def call(List<String> arches = [], Closure body) {
  println arches
  archTasks = arches.collectEntries {
    [ (it) : { body(it) } ]
  }
  println archTasks
  parallel archTasks
}
