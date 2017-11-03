def Closure printVal = { a ->
  println a
}

def call(List<String> arches = [], Closure body) { 
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  for (arch in arches) {
    printVal(arch)
    //archTasks[arch] = wrapFunction(arch, printVal)
  }
  
  //parallel archTasks
}
