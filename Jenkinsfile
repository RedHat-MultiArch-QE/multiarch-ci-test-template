node('master') {
  ansiColor('xterm') {
    timestamps {
      deleteDir()
      stage('Provision Slave') {
        git(url: 'https://github.com/detiber/multiarch-openshift-ci', branch: 'master')
        dir('ci-ops-central') {
          git(url: 'https://code.engineering.redhat.com/gerrit/ci-ops-central', branch: 'master')
        }
        dir('job-runner') {
          git(url: 'https://code.engineering.redhat.com/gerrit/job-runner', branch: 'master')
        }
        withEnv(['JSLAVENAME=multiarch-test-slave']) {
          sh '''#!/bin/bash -xeu
	    ls
	    which ssh-keygen
	    alias
	    cat /proc/sys/kernel/random/entropy_avail
	    set +e
	    yum repolist
	    yum list installed openssh
	    yum list installed jenkins
	    whoami
	    pwd
	    ssh-keygen -vvv -N '' -f ssh_${JSLAVENAME} 2>&1 | tee output.log || true
	    cat output.log
	    ls -alZ
	    touch ssh_${JSLAVENAME}
	    chmod 0600 ssh_${JSLAVENAME}
	    ssh-keygen -vvv -N '' -f ssh_${JSLAVENAME} 2>&1 | tee output.log || true
	    cat output.log
	    ls -alZ
	    exit 1

	    set -e
	    ls
            $WORKSPACE/ci-ops-central/bootstrap/provision_jslave.sh --topology=project/config/bkr_jslave \
            --project_defaults=ci-ops-central/project/config/project_defaults_osp7 --ssh_keyfile=ssh_${JSLAVENAME} \
            --jslavename=${JSLAVENAME} --jslavecreate --resources_file=${JSLAVENAME}.json
            TR_STATUS=$?
            if [ "$TR_STATUS" != 0 ]; then echo "ERROR: Provisioning\nSTATUS: $TR_STATUS"; exit 1; fi
          '''
        }
      }
    }
  }
}

// node('multiarch-test-slave') {
//   ansiColor('xterm') {
//     timestamps {
//       deleteDir()
//       stage('Checkout Repos') {
//         dir('origin') {
//           git(url: 'https://github.com/openshift/origin.git', branch: 'master')
//         }
//       }
//     }
//   }
// }
