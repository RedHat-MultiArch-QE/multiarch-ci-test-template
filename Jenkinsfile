properties(
  [
    pipelineTriggers(
      [
        [
          $class: 'CIBuildTrigger',
          checks: [],
          overrides: [topic: "Consumer.rh-jenkins-ci-plugin.afe3c710-9a13-4039-98d4-0f7c2c74a60b.VirtualTopic.qe.ci.>"],
          providerName: 'Red Hat UMB',
          selector: 'name = \'ansible\' AND CI_TYPE = \'brew-tag\' AND tag LIKE \'ansible-%-rhel-%-candidate\''
        ]
      ]
    ),
    parameters(
      [
        string(
          defaultValue: 'x86_64,ppc64le',
          description: 'A comma separated list of architectures to run the test on. Valid values include [x86_64, ppc64le, aarch64, s390x].',
          name: 'ARCHES'
        ),
        string(
          defaultValue: 'https://github.com/jaypoulz/multiarch-ci-libraries',
          description: 'Repo for shared libraries.',
          name: 'LIBRARIES_REPO'
        ),
        string(
          defaultValue: 'dev-v1.0',
          description: 'Git reference to the branch or tag of shared libraries.',
          name: 'LIBRARIES_REF'
        ),
        string(
          defaultValue: '',
          description: 'Repo for tests to run. If left blank, the current repo is assumed (*note* this default will only work for multibranch pipelines).',
          name: 'TEST_REPO'
        ),
        string(
          defaultValue: '',
          description: 'Git reference to the branch or tag of the tests repo.',
          name: 'TEST_REF'
        ),
        string(
          defaultValue: 'tests',
          description: 'Directory containing tests to run. Should at least one of the follow: an ansible-playbooks directory containing one or more test directories each of which having a playbook.yml, a scripts directory containing one or more test directories each of which having a run-test.sh',
          name: 'TEST_DIR'
        ),
        string(
          defaultValue: '',
          description: 'Contains the CI_MESSAGE for a message bus triggered build.',
          name: 'CI_MESSAGE'
        ),
        string(
          defaultValue: '',
          description: 'Build task ID for which to run the pipeline',
          name: 'TASK_ID'
        )
      ]
    )
  ]
)

library(
  changelog: false,
  identifier: "multiarch-ci-libraries@${params.LIBRARIES_REF}",
  retriever: modernSCM([$class: 'GitSCMSource',remote: "${params.LIBRARIES_REPO}"])
)

List arches = params.ARCHES.tokenize(',')
def config = TestUtils.getProvisioningConfig(this)

node ('provisioner-v1.0') {
    /*********************************************************/
    /* TEST BODY                                             */
    /* @param host               Provisioned host details.   */
    /*********************************************************/
    def host = [ 
      arch: 'x86_64',
      hostName: 'localhost',
      name: 'provisioner-v1.0',
      target: null,
      inventory: null,
      initialized: true,
      provisioned: true,
      connectedToMaster: true,
      ansibledInstalled: true
    ]

    def taskRepoCreated = false
    if (params.CI_MESSAGE != '') {
      tid = getTaskId(params.CI_MESSAGE)
      createTaskRepo(taskIds: tid)
      taskRepoCreated = true
    } else if (params.TASK_ID != '') {
      createTaskRepo(taskIds: params.TASK_ID)
      taskRepoCreated = true
    }

    if (taskRepoCreated == true) {
      sh """
        cat task-repo.properties
        URL=\$(cat task-repo.properties | grep TASK_REPO_URLS= | sed 's/TASK_REPO_URLS=//' | sed 's/;/\\n/g')
        sudo yum-config-manager --add-repo \${URL}
        sudo yum --nogpgcheck install -y ansible
      """
    }

    dir('test') {
      stage ('Download Test Files') {
        downloadTests() 
      }

      stage ('Run Test') {
        runTests(config, host)
      }

      stage ('Archive Test Output') {
        archiveOutput()
      }
    }

    /*****************************************************************/
    /* END TEST BODY                                                 */
    /* Do not edit beyond this point                                 */
    /*****************************************************************/
}
