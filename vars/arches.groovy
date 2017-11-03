def call(List<String> arches = [], Closure body) {
  def Closure wrapBody = { String a, Closure b ->
    return { b(a) }
  }
  
  println arches
  archTasks = arches.collectEntries {
    [ (it) : wrapBody(it, body) ]
  }
  
  println archTasks
  parallel archTasks
}
