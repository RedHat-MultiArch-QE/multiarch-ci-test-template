def call(List<String> remote_cmds=[]) {
  try {
    unstash('test.hostname')
    def test_hostname = readFile('test.hostname').replaceAll("\\s","")
    echo "test_hostname: ${test_hostname}"
    for (cmd in remote_cmds) {
      withEnv(["test_hostname=${test_hostname}", "cmd=${cmd}"]) {
        sh('''#!/usr/bin/bash -xeu
              ssh_opts="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"
              SCRIPT_START_TIME="$( date +%s )" && export SCRIPT_START_TIME && echo "########## STARTING Command ##########" && trap 'export status=FAILURE' ERR && trap 'set +o xtrace; SCRIPT_END_TIME="$( date +%s )"; ELAPSED_TIME="$(( SCRIPT_END_TIME - SCRIPT_START_TIME ))"; echo "########## FINISHED Command: ${status:-SUCCESS}: [$( printf "%02dh %02dm %02ds" "$(( ELAPSED_TIME/3600 ))" "$(( (ELAPSED_TIME%3600)/60 ))" "$(( ELAPSED_TIME%60 ))" )] ##########"' EXIT && set -o errexit -o nounset -o pipefail -o xtrace && if [[ -s "${WORKSPACE}/activate" ]]; then source "${WORKSPACE}/activate"; fi
              script="$( mktemp )"
              cat <<SCRIPT >"${script}"
#!/bin/bash
set -o errexit -o nounset -o pipefail -o xtrace
${cmd}
SCRIPT
              chmod +x "${script}"
              scp ${ssh_opts} "${script}" root@${test_hostname}:"${script}"
              #ssh ${ssh_opts} root@${test_hostname} bash -l -c "timeout 14400 ${script}"
              ssh ${ssh_opts} root@${test_hostname} bash -l -c "${script}"
           ''')
      }
    }
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
}
