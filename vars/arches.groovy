def call(Closure body) { 
  def archTasks = [:]
  for (arch in params.ARCHES.tokenize(',')) {
    archTasks[arch] = body(arch)
  }
  
  parallel archTasks
}
