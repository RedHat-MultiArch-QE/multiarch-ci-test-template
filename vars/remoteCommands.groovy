def call(List<String> remote_cmds=[]) {
  try {
    unstash('test.hostname')
    def test_hostname = readFile('test.hostname').replaceAll("\\s","")
    echo "test_hostname: ${test_hostname}"
    for (cmd in remote_cmds) {
      withEnv(["test_hostname=${test_hostname}", "cmd=${cmd}"]) {
        sh('''#!/usr/bin/bash -xeu
              ssh_opts="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -l root"
              ssh ${ssh_opts} ${test_hostname} "${cmd}"
           ''')
      }
    }
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
}
