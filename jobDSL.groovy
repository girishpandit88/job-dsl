def projName = 'girishpandit88/StarTrooper'
def branchApi = new URL("https://api.github.com/repos/${projName}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())


branches.each {
    def branchName = it.name
    def downstreamiOSJob = job{
    	name "${projName}-${branchName}.iOS".replaceAll('/','-')
		label('osx')
		scm {
		    git("git://github.com/${projName}.git", branchName)
		}
    }
    def downstreamUnityJob = job {
		name "${projName}-${branchName}.unity".replaceAll('/','-')
		label('osx')
		scm {
		    git("git://github.com/${projName}.git", branchName)
		}
		steps{
			shell('mkdir -p target')
		}
		configure { project ->
			project/ builders / 'org.jenkinsci.plugins.unity3d.Unity3dBuilder'(plugin: 'unity3d-plugin@0.5') {
				unity3dName('Unity3d')
				argLine('-quit -batchmode -executeMethod AutoBuilder.PerformiOSBuild')
			}
		}
		publishers{
			archiveArtifacts 'target/**'
			downstreamParameterized{
				trigger(downstreamiOSJob.name, 'SUCCESS'){
					predefinedProp("UNITY_BUILD_NUMBER","\${BUILD_NUMBER}")
				}
			}
		}

	}
	//print downstreamUnityJob.name
    downstreamiOSJob.with {
		steps{
			copyArtifacts(downstreamUnityJob.name,"target/**"){
				buildParameter("\$UNITY_BUILD_NUMBER")
			}
		}
		
		configure { project ->
			project/ builders / 'au.com.rayh.XCodeBuilder'(plugin: 'xcode-plugin@1.4.1'){
				cleanBeforeBuild('true')
				configuration('Debug')
				target('Unity-iPhone')
				configurationBuildDir("${WORKSPACE}/target/build")
				xcodeProjectPath('target/StarTrooper')
				embeddedProfileFile("${HOME}/Library/MobileDevice/Provisioning Profile")
				buildIPA('true')
				unlockKeychain('true')
				keychainName('none (specify one below)')
				keychainPath("${HOME}/Library/Keychains/login.keychain")
				keychainPwd('*****')
			}
		}
	}

    def initJob = job {
        name "${projName}-${branchName}".replaceAll('/','-')
        label('osx')
		scm {
	            git("git://github.com/${projName}.git", branchName)
	        }

		publishers{
			downstream(downstreamUnityJob.name,'SUCCESS')
		}

    }


}