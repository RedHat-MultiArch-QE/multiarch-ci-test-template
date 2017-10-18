properties([
    pipelineTriggers([
        [$class: 'CIBuildTrigger', 
          checks: [], 
          providerName: 'CI Subscribe', 
          selector: 'name = \'openshift\' AND CI_TYPE = \'brew-tag\' AND tag LIKE \' rhaos-%-rhel-%-newarches-candidate\''
        ]
    ]),
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
        ),
        string(
          name: 'ORIGIN_BRANCH',
          description: 'Origin branch',
          defaultValue: 'master'
        ),
        string(
          name: 'OS_BUILD_ENV_IMAGE',
          description: 'openshift-release image',
          defaultValue: 'openshiftmultiarch/origin-release:golang-1.8'
        )
    ])
])

def provisionedNode = null
def provisionedNodeBuildNumber = null

ansiColor('xterm') {
  timestamps {
    node('master') {
      try {
        try {
          stage('Provision Slave') {
            def buildResult = build([
                job: 'provision-multiarch-slave',
                parameters: [
                  string(name: 'ARCH', value: arch),
                  string(name: 'CONFIG_REPO', value: 'https://github.com/jaypoulz/multiarch-ci-test-openshift'),
                  string(name: 'CONFIG_FILE', value: 'config/beaker-config.yml')
                ],
                propagate: true,
                wait: true
              ])

            // If provision was successful, you will be able to grab the build number
            provisionedNodeBuildNumber = buildResult.getNumber().toString()

            // Get results of provisioning job
            step([$class: 'CopyArtifact', filter: 'slave.properties',
                projectName: 'provision-multiarch-slave',
                selector: [
                  $class: 'SpecificBuildSelector',
                  buildNumber: provisionedNodeBuildNumber
                ]
              ])

            // Load slave properties (you may need to turn off sandbox or approve this in Jenkins)
            def slaveProps = readProperties file: 'slave.properties'

            // Assign the appropriate slave name
            provisionedNode = slaveProps.name
          }
        } catch (e) {
          // If provision fails, grab the build number from the error message and set build status to not built
          provisionedNodeBuildNumber = ((e =~ "(provision-multiarch-slave #)([0-9]*)")[0][2])
          currentBuild.result = 'NOT_BUILT'
          throw e
        }

        node(provisionedNode) {
          try {
            def gopath = "${pwd(tmp: true)}/go"
            def failed_stages = []
            withEnv(["GOPATH=${gopath}", "PATH=${PATH}:${gopath}/bin"]) {
              stage('Prep') {
                sh 'git config --global user.email "jpoulin@redhat.com" && git config --global user.name "Jeremy Poulin"'
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
              } catch (exc) {
                failed_stages+='Pre-release Tests'
                currentBuild.result = 'UNSTABLE'
              }
              stage('Locally build release') {
                try {
                  sh '''#!/bin/bash -xeu
                    hack/env hack/build-base-images.sh
                    hack/env JUNIT_REPORT=true make release
                    '''
                } catch (e) {
                  currentBuild.result = 'FAILURE'
                  throw e
                }
              }
              try {
                stage('Integration Tests') {
                  sh '''#!/bin/bash -xeu
                    hack/env JUNIT_REPORT='true' make test-tools test-integration
                    '''
                }
              } catch (e) {
                failed_stages+='Integration Tests'
                currentBuild.result = 'UNSTABLE'
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
              } catch (e) {
                failed_stages+='End to End Tests'
                currentBuild.result = 'UNSTABLE'
              }
            }
          } catch (e) {
            println(e)
          } finally {
            stage ('Archive Test Output') {
              archiveArtifacts '_output/scripts/**/*'
              junit '_output/scripts/**/*.xml'
            }
          }
        }
      } catch (e) {
        // This is just a wrapper step to ensure that teardown is run upon failure
        println(e)
      } finally {
        // Ensure teardown runs before the pipeline exits
        stage ('Teardown Slave') {
          build([job: 'teardown-multiarch-slave',
              parameters: [
                string(name: 'BUILD_NUMBER', value: provisionedNodeBuildNumber)
              ],
              propagate: true,
              wait: true
          ])
        }
      }
    }
  }
}
