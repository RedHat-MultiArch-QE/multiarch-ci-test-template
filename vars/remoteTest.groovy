def call(List<String> remote_tasks=[]) {
  try {
    def test_hostname = readFile('duffy.hostname')
    echo "test_hostname: ${test_hostname}"
    withEnv(["test_hostname=${test_hostname}"]) {
      sh('''#!/usr/bin/bash -xeu
            ssh -o StrictHostKeyChecking=no ${test_hostname} hostname
         ''')
    }
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
}
