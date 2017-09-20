import org.centos.Utils

def call(Closure body) {

  def utils = new Utils()
  try {
    sh('''#!/usr/bin/bash
          ssid=$(cico node get -f value -c comment)
          if [[ -z "${ssid:-}" ]]; then
            echo "Failed to provision duffy host"
            exit 1
          fi

          cico inventory --ssid ${ssid}

          echo "${ssid}" > duffy.ssid
          cico inventory --ssid ${ssid} -f json > duffy.inventory
       ''')
    body()
  }
  catch (err) {
    echo err.getMessage()
    throw err
  }
  finally {
     sh('''#!/usr/bin/bash
          ssid=$(cat duffy.ssid)
          if [[ -n "${ssid:-}" ]]; then
            cico node done $ssid
          fi
       ''')
  }
}
