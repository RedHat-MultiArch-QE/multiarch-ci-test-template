properties([
  parameters([
    choiceParam(
      name: 'ARCH',
      choices: "x86_64\nppc64le\naarch64\ns390x",
      description: 'Architecture'
    )
  ])
])

node('master') {
  stage('Provision Slave') {
    ansiColor('xterm') {
      timestamps {
        @Library('multiarch-openshift-ci-libraries')
        arch=params.ARCH
        def node_name = "multiarch-slave-${arch}"
        def node_label = node_name
        echo "nodes: ${nodes.getNodes()}"
        if (! nodes.nodeExists(node_name)) {
          build([
            job: 'provision_beaker_slave',
            parameters: [
              string(name: 'ARCH', value: arch),
              string(name: 'NAME', value: node_name),
              string(name: 'LABEL', value: node_label)
            ]
          ])
        }
      }
    }
  }
}

node("multiarch-slave-${params.ARCH}") {
  ansiColor('xterm') {
    timestamps {
      def gopath = "${pwd(tmp: true)}/go"
      withEnv(["GOPATH=${gopath}", "PATH=${PATH}:${gopath}/bin"]) {
        stage('Prep') {
          git(url: 'https://github.com/detiber/origin.git', branch: 'ppc64le')
	  try {
	    sh '''#!/bin/bash -xeu
              go get -u github.com/openshift/imagebuilder/cmd/imagebuilder
              make build-base-images
              make build-release-images
            '''
	  }
	  catch (exc) {
	    archiveArtifacts '_output/scripts/**/*'
	    junit '_output/scripts/**/*.xml'
	    throw exc
	  }
	}
        stage('Tests') {
	  def success = True
	  try {
	    sh '''#!/bin/bash -xeu
              hack/env JUNIT_REPORT=true DETECT_RACES=false make check -k
            '''
	  }
	  catch (exc) {
	    success = False
	  }
	}
        archiveArtifacts '_output/scripts/**/*'
        junit '_output/scripts/**/*.xml'
      }
    }
  }
}
