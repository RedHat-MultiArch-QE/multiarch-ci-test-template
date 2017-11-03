def call(List<String> arches = [], Closure body) { 
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  for (arch in arches) {
    body(arch)
    //archTasks[arch] = wrapFunction(arch, printVal)
  }
  
  //parallel archTasks
}
