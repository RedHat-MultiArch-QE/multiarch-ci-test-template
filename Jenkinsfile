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
            tmp_dir=$(mktemp -d openshift-multiarch-ci-XXXXXX)
            chcon -t ssh_home_t ${tmp_dir}
	    cp ci-ops-central/targets/keys/ci-ops-central ${tmp_dir}
	    ssh_keyfile=${tmp_dir}/ci-ops-central
	    chmod 0600 ${ssh_keyfile}
	    ls -alZ ${tmp_dir}
	    cat ${ssh_keyfile}
	    ssh-keygen -vvv -y -f ${ssh_keyfile} 2&>1 | tee output.log

	    exit 1

            pub_key=$(cat ${ssh_keyfile}.pub)
            sed -i -e "s#PUB_KEY#${pub_key}#" project/config/bkr_jslave.json

            $WORKSPACE/ci-ops-central/bootstrap/provision_jslave.sh \
	    --site=${SITE} \
            --topology=project/config/bkr_jslave \
            --project_defaults=ci-ops-central/project/config/project_defaults_osp7 \
            --ssh_keyfile=${ssh_keyfile} \
	    --jenkins_master_username=${SWARM_USER} \
	    --jenkins_master_passsword=${SWARM_PASS} \
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
