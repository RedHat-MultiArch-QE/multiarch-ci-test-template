def call(List<String> arches = [], Closure body) {
  def Closure wrapBody = { String a, Closure b ->
    println "String ${a} is totally immutable, like for sure - now wrapping it in a closure"
    def s = new String(a)
    return { b(s) }
  }
  
  println arches
  def archTasks = [:]
  for (arch in arches) {
    archTasks[arch] = { body(new String(arch)) }
  }
  
  parallel archTasks
}
