workDir = '/u/nlopez/tmp/jenkins-mylocal-server/workspace/pocPipeline'
def File jsonOutputFile = new FileNameFinder().getFileNames(workDir, "**/*.json")
println jsonOutputFile.text 

System.exit(0)
		