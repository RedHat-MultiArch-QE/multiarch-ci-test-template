import org.centos.Utils

def call(Closure body) {

  def utils = new Utils()
  try {
    utils.allocateDuffyCciskel('test')
    body()
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
  finally {
    utils.teardownDuffyCciskel('test')
  }
}
