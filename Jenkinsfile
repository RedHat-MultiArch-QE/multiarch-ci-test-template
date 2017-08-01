properties([
  parameters([
    choiceParam(
      name: 'ARCH',
      options: [
        'x86_64',
        'ppc64le',
        'aarch64',
        's390x'
      ],
      description: 'Architecture'
    )
  ])
])

stage('Provision Slave') {
  node('master') {
    ansiColor('xterm') {
      timestamps {
        build([
          job: 'provision_beaker_slave',
          parameters: [
            string(name: 'ARCH', value: "${params.ARCH}"),
            string(name: 'NAME', value: "multiarch-slave-${params.ARCH}"),
            string(name: 'LABEL', value: "multiarch-slave-${params.ARCH}")
          ]
        ])
      }
    }
  }
}

stage('Tests') {
  node("multiarch-slave-${params.ARCH}") {
    ansiColor('xterm') {
      timestamps {
        deleteDir()
        git(url: 'https://github.com/detiber/origin.git', branch: 'ppc64le')
        sh '''#!/bin/bash -xeu
          make build-base-images
          make build-release-images
          make check
        '''
      }
    }
  }
}
