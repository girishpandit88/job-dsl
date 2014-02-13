def project = 'girishpandit88/StarTrooper'
def branchApi = new URL("https://api.github.com/repos/${project}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
branches.each { 
    def branchName = it.name
    def downstreamUnityJob = job {
	name "${project}-${branchName}.unity".replaceAll('/','-')
	scm {
	    git("git://github.com/${project}.git", branchName)
	}
	steps {
		unity(Unity3d) {
			
		}
	}
    }
    job {
        name "${project}-${branchName}".replaceAll('/','-')
        scm {
            git("git://github.com/${project}.git", branchName)
        }
	publishers{
		downstream(downstreamUnityJob, 'SUCCESS')
	}
        
    }
}
