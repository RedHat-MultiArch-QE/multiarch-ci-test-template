def call(List<String> arches = [], Closure body) {
  def Closure wrapBody = { String a, Closure b ->
    return { b(a) }
  }
  
  println arches
  def archTasks = [:]
  for (arch in arches) {
    archTasks[arch] = wrapBody("${arch}", body)
  }
  
  for (task in archTasks) {
    task.getValue()()
  }
  
  //parallel archTasks
}
