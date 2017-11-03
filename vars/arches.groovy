def call(List<String> arches = [], Closure body) {
  // This closure is necessary to ensure that the arch string param gets wrapped immutably
  def Closure wrapBody = { String arch ->
    println "Wrapped ${arch}"
    return {
      body(arch) 
    }
  }
  
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  for (arch in arches) {
    def a = new String(arch)
    println "Looping through arch ${a}"
    archTasks[a] = wrapBody(a)
  }

  parallel archTasks
}
