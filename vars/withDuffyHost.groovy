def call(Closure body) {

  def utils = library('cico-pipeline-library').org.centos.Utils.new(this)
  try {
//    utils.allocateDuffyCciskel('test')
    body()
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
  finally {
//    utils.teardownDuffyCciskel('test')
  }
}
