def call(Closure body) {
  try {
    stage("${PROVISION_STAGE_NAME}"){
      sh('''#!/usr/bin/bash -xeu
            if [[ "${ARCH}" == "x86_64" ]]; then
              ssid=$(cico node get -f value -c comment)
            else
              ssid=$(cico node get -f value -c comment --arch "${ARCH}" --flavor xram.medium )
            fi
            if [[ -z "${ssid:-}" ]]; then
              echo "Failed to provision duffy host"
              exit 1
            fi

            cico inventory --ssid ${ssid}

            echo "${ssid}" > duffy.ssid
  	  cico inventory --ssid ${ssid} -f value -c hostname > duffy.hostname
            cico inventory --ssid ${ssid} -f json > duffy.inventory
  	  cp duffy.hostname test.hostname
         ''')
      stash name: 'duffy-results', includes: 'duffy.*'
      stash name: 'test.hostname', includes: 'test.hostname'
      archiveArtifacts 'duffy.*'
      archiveArtifacts 'test.hostname'
    }
    body()
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
  finally {
    stage("${DEPROVISION_STAGE_NAME}"){
      unstash 'duffy-results'
      sh('''#!/usr/bin/bash -xeu
           ssid=$(cat duffy.ssid)
           if [[ -n "${ssid:-}" ]]; then
             cico node done $ssid
           fi
        ''')
    }
  }
}
