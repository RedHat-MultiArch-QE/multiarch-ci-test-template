def call(List<String> arches = [], Closure body) {
  // This closure is necessary to ensure that the arch string param gets wrapped immutably
  def Closure wrapBody = { String arch ->
    def s = new String(arch)
    println "Wrapped ${arch} into var ${s}"
    return {
      println(s) 
      body(s)
    }
  }
  
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  for (arch in arches) {
    archTasks[arch] = wrapBody(arch)
  }

  parallel archTasks
}
