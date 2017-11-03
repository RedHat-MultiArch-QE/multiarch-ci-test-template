def call(List<String> arches = [], Closure body) {
  def Closure wrapBody = { String a, Closure b ->
    println "String ${a} is totally immutable, like for sure - now wrapping it in a closure"
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
