def projectName = 'girishpandit88/StarTrooper'
def branchApi = new URL("https://api.github.com/repos/${projectName}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
branches.each { 
    def branchName = it.name
    job {
        name "${projectName}-${branchName}".replaceAll('/','-')
        label('osx')
		scm {
	            git("git://github.com/${projectName}.git", branchName)
	        }
	    steps {
			shell("mkdir -p target")
		}
		configure { 
			builders/ 'org.jenkinsci.plugins.unity3d.Unity3dBuilder'(plugin: 'unity3d-plugin@0.5') {
				unity3dName('Unity3d')
				argLine('-quit -batchmode -executeMethod AutoBuilder.PerformiOSBuild')
			}
		}    

	    def downstreamUnityJob = job {
			name "${projectName}-${branchName}.unity".replaceAll('/','-')
			scm {
			    git("git://github.com/${project}.git", branchName)
			}
			
		}
    }
}
