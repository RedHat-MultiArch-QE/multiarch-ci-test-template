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
	    rpm -qa | grep rng-utils || true
	    which ssh-keygen
	    cat /proc/sys/kernel/random/entropy_avail
	    set +e
	    echo 'hi' 1>&2
	    ssh-keygen -vvv -N 'test' -f ssh_${JSLAVENAME} 2>&1
	    ret_code=$?
	    if [[ $ret_code != 0 ]]; then
	      echo $ret_code
	      exit 1
	    fi

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
