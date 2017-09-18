properties([
    parameters([
        choiceParam(
          name: 'ARCH',
          choices: "x86_64\nppc64le\naarch64\ns390x",
          description: 'Architecture'
        ),
        string(
          name: 'ORIGIN_REPO',
          description: 'Origin repo',
          defaultValue: 'https://github.com/openshift/origin.git'
          //      defaultValue: 'https://github.com/detiber/origin.git'
        ),
        string(
          name: 'ORIGIN_BRANCH',
          description: 'Origin branch',
          defaultValue: 'master'
          //      defaultValue: 'ppc64le-rebase-wip'
        ),
        string(
          name: 'OS_BUILD_ENV_IMAGE',
          description: 'openshift-release image',
          defaultValue: 'openshiftmultiarch/origin-release:golang-1.8'
        )
      ])
  ])

def provisionedNode = null

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
          def buildResult = build([
              job: 'provision-multiarch-slave',
              parameters: [
                string(name: 'ARCH', value: arch),
                string(name: 'NAME', value: node_name),
                string(name: 'LABEL', value: node_label)
              ],
              propagate: true,
              wait: true
            ])

          // Get results of provisioning job
          step([$class: 'CopyArtifact',
              filter: 'slave.properties',
              fingerprintArtifacts: true,
              flatten             : true,
              projectName         : 'provision-multiarch-slave',
              selector: [
                $class: 'SpecificBuildSelector',
                buildNumber: buildResult.getNumber().toString()
              ]
            ])

          // Load slave properties (you may need to turn off sandbox or approve this in Jenkins)
          Properties slaveProps = new Properties()
          provisioner.load(new StringReader(readFile('slave.properties')))

          // Assign the appropriate slave name
          provisionedNode = slaveProps.name
        }
      }
    }
  }
}

node(provisionedNode) {
  ansiColor('xterm') {
    timestamps {
      def gopath = "${pwd(tmp: true)}/go"
      def failed_stages = []
      withEnv(["GOPATH=${gopath}", "PATH=${PATH}:${gopath}/bin"]) {
        stage('Prep') {
          git(url: params.ORIGIN_REPO, branch: params.ORIGIN_BRANCH)
          sh '''#!/bin/bash -xeu
            git remote add detiber https://github.com/detiber/origin.git || true
        git fetch detiber
        git merge detiber/multiarch
      '''
        }
        try {
          stage('Pre-release Tests') {
            sh '''#!/bin/bash -xeu
              hack/env JUNIT_REPORT=true DETECT_RACES=false TIMEOUT=300s make check -k
            '''
          }
        }
        catch (exc) {
          failed_stages+='Pre-release Tests'
        }
        stage('Locally build release') {
          try {
            sh '''#!/bin/bash -xeu
              hack/env hack/build-base-images.sh
              hack/env JUNIT_REPORT=true make release
            '''
          }
          catch (exc) {
            archiveArtifacts '_output/scripts/**/*'
            junit '_output/scripts/**/*.xml'
            throw exc
          }
        }
        try {
          stage('Integration Tests') {
            sh '''#!/bin/bash -xeu
              hack/env JUNIT_REPORT='true' make test-tools test-integration
            '''
          }
        }
        catch (exc) {
          failed_stages+='Integration Tests'
        }
        try {
          stage('End to End tests') {
            sh '''#!/bin/bash -xeu
              arch=$(go env GOHOSTARCH)
              OS_BUILD_ENV_PRESERVE=_output/local/bin/linux/${arch}/end-to-end.test hack/env make build-router-e2e-test
              OS_BUILD_ENV_PRESERVE=_output/local/bin/linux/${arch}/etcdhelper hack/env make build WHAT=tools/etcdhelper
              OPENSHIFT_SKIP_BUILD='true' JUNIT_REPORT='true' make test-end-to-end -o build
            '''
          }
        }
        catch (exc) {
          failed_stages+='End to End Tests'
        }
        archiveArtifacts '_output/scripts/**/*'
        junit '_output/scripts/**/*.xml'
      }
    }
  }
}
