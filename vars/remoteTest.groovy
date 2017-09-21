def call(List<String> remote_tasks=[]) {
  try {
    def test_hostname = readFile('duffy.hostname')
    echo "test_hostname: ${test_hostname}"
    withEnv(["test_hostname=${test_hostname}"]) {
      sh('''#!/usr/bin/bash -xeu
            ssh_opts="-t -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -l root"
            ssh ${ssh_opts} ${test_hostname} hostname
         ''')
    }
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
}
