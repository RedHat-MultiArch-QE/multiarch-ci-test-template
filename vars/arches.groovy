def call(List<String> arches = [], Closure body) {
  // This closure is necessary to ensure that the arch string param gets wrapped immutably
  def Closure wrapBody = { String arch ->
    println "Wrapped ${arch}"
    return {
      println(arch) 
    }
  }
  
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  def counter = 0
  for (arch in arches) {
    def a = new String(arch) + "${counter}"
    println "Looping through arch ${a}"
    archTasks[arch] = wrapBody(a)
    counter++;
  }

  parallel archTasks
}
