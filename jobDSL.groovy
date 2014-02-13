def project = 'girishpandit88/StarTrooper'
def branchApi = new URL("https://api.github.com/repos/${project}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
branches.each { 
    def branchName = it.name
    job {
        name "${project}-${branchName}".replaceAll('/','-')
        label('osx')
	scm {
            git("git://github.com/${project}.git", branchName)
        }
    def downstreamUnityJob = job {
			name "${project}-${branchName}.unity".replaceAll('/','-')
			scm {
			    git("git://github.com/${project}.git", branchName)
			}
			steps {
					shell("mkdir -p target")
					unity{
						name "Unity3d"
					}
			}
		}
    }
	publishers{
		downstream(downstreamUnityJob, 'SUCCESS')
	}
        
    }
}
