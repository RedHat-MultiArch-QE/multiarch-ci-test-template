def call(List<String> arches = [], Closure body) {
  // This closure is necessary to ensure that the arch string param gets wrapped immutably
  def Closure wrapBody = { arch ->
    def a = new String(arch)
    println "Wrapped ${arch} to new variable ${a}"
    return { body(a) }
  }
  
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  for (arch in arches) {
    archTasks[arch] = wrapBody(arch)
  }

  parallel archTasks
}
