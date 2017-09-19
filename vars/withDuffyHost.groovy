import org.centos.Utils

def call(Closure body) {

  def utils = new Utils()
  try {
    sh('''#!/usr/bin/python
from cicoclient.wrapper import CicoWrapper

api_key=open('/home/sig-paas/duffy.key').read().strip()
cico=CicoWrapper(api_key=api_key)
print(cico.inventory)
''')
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
