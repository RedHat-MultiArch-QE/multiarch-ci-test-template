properties(
  [
    pipelineTriggers(
      [
        // TODO Fill out pkg-name and relevant-tag
        [
          $class: 'CIBuildTrigger',
          checks: [],
          overrides: [topic: "Consumer.rh-jenkins-ci-plugin.${UUID.randomUUID().toString()}.VirtualTopic.qe.ci.>"],
          providerName: 'Red Hat UMB',
          selector: 'name = \'openshift\' AND CI_TYPE = \'brew-tag\' AND tag (tag LIKE \'rhaos-%-rhel-%-newarches-candidate\' OR tag LIKE \'rhose-%-rhel-%-candidate\')'
        ]
      ]
    ),
    parameters(
      [
        string(
          defaultValue: 'x86_64,ppc64le,aarch64,s390x',
          description: 'Architectures to run the test on.',
          name: 'ARCHES'
        ),
        string(
          defaultValue: 'master',
          description: 'Slave node to run the test on.',
          name: 'TARGET_NODE'
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
      ]
    )
  ]
)

@Library('multiarch-ci-test-template@master') _

ansiColor('xterm') {
  timestamps {
    node(params.TARGET_NODE) {
      arches(
        params.ARCHES.tokenize(','),
        {
          arch -> 
          def s = new String(arch)
          return {
            println("${arch}")
            slave(
              arch,
              {
                provisionedSlave ->

                /************************************************************/
                /* TEST BODY                                                */
                /* @param provisionedSlave    Name of the provisioned host. */
                /************************************************************/

                node(provisionedSlave) {
                  try {
                    stage ("Install dependencies") {
                      git 'https://github.com/jaypoulz/multiarch-ci-test-openshift'
                      sh "sudo yum install epel-release -y"
                      sh "sudo yum install python-pip python-setuptools -y"
                      sh "sudo pip install -U pip; sudo pip install -U setuptools; sudo pip install ansible"
                      sh "ansible-playbook -i 'localhost,' config/beaker-config.yml"
                    }

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

                /************************************************************/
                /* END TEST BODY                                            */
                /* Do not edit beyond this point                            */
                /************************************************************/
              }, true
            )
          }
        }
      )
    }
  }
}
