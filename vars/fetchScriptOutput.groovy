def call() {
  try {
    unstash('test.hostname')
    def test_hostname = readFile('test.hostname').replaceAll("\\s","")
    echo "test_hostname: ${test_hostname}"
    withEnv(["test_hostname=${test_hostname}", "d=${d}"]) {
      sh('''#!/usr/bin/bash -xeu
            ssh_opts="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -l root"
            rsync -azhe "ssh ${ssh_opts}" "${test_hostname}:origin/_output" "origin"
         ''')
    }
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
}
