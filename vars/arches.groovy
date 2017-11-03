def call(List<String> arches = [], Closure body) { 
  // Loop through the submitted arches and wrap each body call in a closure
  def archTasks = [:]
  for (arch in arches) {
    archTasks[arch] = arch//wrapFunction(arch, body)
  }

  for (entry in archTasks) {
    entry.setValue(wrapFunction(entry.getValue(), body))
  }
  
  parallel archTasks
}

def wrapFunction(String arch, Closure body) {
  def a = new String(arch)
  return { body(a) }
}
