import org.centos.Utils

def call(Closure body) {

  def utils = library(identifier: 'cico-pipeline-library@master',
                      retriever: modernSCM([$class: 'GitSCMSource',
                                           remote: "https://github.com/CentOS/cico-pipeline-library"])).org.centos.Utils()
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
