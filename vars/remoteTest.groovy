def call(List<String> remote_tasks=[]) {
  try {
    unstash('test.hostname')
    def test_hostname = readFile('test.hostname')
    echo "test_hostname: ${test_hostname}"
    for (task in remote_tasks) {
      withEnv(["test_hostname=${test_hostname}", "task=${task}"]) {
        sh('''#!/usr/bin/bash -xeu
              ssh_opts="-t -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -l root"
              ssh ${ssh_opts} ${test_hostname} "${task}"
           ''')
      }
    }
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
}
