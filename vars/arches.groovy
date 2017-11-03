def call(List<String> arches = [], Closure body) {
  def wrapBody(arch) {
    return { body(arch) }
  }
  
  println arches
  archTasks = arches.collectEntries {
    [ (it) : wrapBody(it) ]
  }
  
  println archTasks
  parallel archTasks
}
