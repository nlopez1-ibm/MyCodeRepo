@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.build.*
import com.ibm.dbb.build.report.*
import com.ibm.dbb.build.html.*
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import groovy.json.JsonSlurper
import groovy.transform.Field

def parseArgs(String[] cliArgs, String usage) {
	def cli = new CliBuilder(usage: usage)
	cli.s(longOpt:'sourceDir', args:1, argName:'dir', 'Absolute path to source directory') 
	cli.w(longOpt:'workDir', args:1, argName:'dir', 'Absolute path to the build output directory')
	cli.b(longOpt:'buildHash', args:1, argName:'hash', 'Git commit hash for the build')
	cli.q(longOpt:'hlq', args:1, argName:'hlq', 'High level qualifier for partition data sets')
	cli.c(longOpt:'collection', args:1, argName:'name', 'Name of the dependency data collection')
	cli.t(longOpt:'team', args:1, argName:'hlq', 'Team build hlq for user build syslib concatenations')
	cli.r(longOpt:'repo', args:1, argName:'url', 'DBB repository URL')
	cli.i(longOpt:'id', args:1, argName:'id', 'DBB repository id')
	cli.p(longOpt:'pw', args:1, argName:'password', 'DBB password')
	cli.P(longOpt:'pwFile', args:1, argName:'file', 'Absolute or relative (from sourceDir) path to file containing DBB password')
	cli.e(longOpt:'logEncoding', args:1, argName:'encoding', 'Encoding of output logs. Default is EBCDIC')
	cli.u(longOpt:'userBuild', 'Flag indicating running a user build')
	cli.E(longOpt:'errPrefix', args:1, argName:'errorPrefix', 'Unique id used for IDz error message datasets')
	cli.h(longOpt:'help', 'Prints this message')
	cli.C(longOpt:'clean', 'Deletes the dependency collection and build reeult group from the DBB repository then terminates (skips build)')
	cli.f(longOpt:'confDir', args:1, argName:'dir', 'Absolute path to configuration directory  (default conf directory)')	
	cli.g(longOpt:'gitDir', args:1, argName:'dir', 'Absolute path to git base directory  (default source directory)')	
	cli.o(longOpt:'buildHost', args:1, argName:'buildHost', 'Specific properties files for this OS (example: bind.${buildHost}.properties)')
	cli.U(longOpt:'url', args:1, argName:'gitUrl', 'The Git URL')
	cli.n(longOpt:'buildNumber', args:1, argName:'buildHost', 'The Build Number')
	
	
	def opts = cli.parse(cliArgs)
	if (opts.h) { // if help option used, print usage and exit
	 	cli.usage()
		System.exit(0)
	}                

    return opts                          
}

def loadProperties(OptionAccessor opts) {
	// check to see if there is a ./build.properties to load
	def properties = BuildProperties.getInstance()
	if (opts.f) properties.confDir = opts.f else properties.confDir = "${getScriptDir()}/conf".toString()
	def buildPropFile = new File("${properties.confDir}/build.properties")
	if (buildPropFile.exists())
   		BuildProperties.load(buildPropFile)

	// set command line arguments
	if (opts.s) properties.sourceDir = opts.s
	if (opts.w) properties.workDir = opts.w
	if (opts.b) properties.buildHash = opts.b
	if (opts.q) properties.hlq = opts.q
	if (opts.c) properties.collection = opts.c
	if (opts.t) properties.team = opts.t
	if (opts.e) properties.logEncoding = opts.e
	if (opts.E) properties.errPrefix = opts.E
	if (opts.u) properties.userBuild = "true"
	if (opts.o) properties.buildHost = opts.o
	if (opts.U) properties.url = opts.U
	if (opts.g) properties.gitDir = opts.g else properties.sourceDir;
	if (opts.n) properties.buildNumber = opts.n
	
	
	// override new default properties 
	if (opts.r) properties.'dbb.RepositoryClient.url' = opts.r
	if (opts.i) properties.'dbb.RepositoryClient.userId' = opts.i
	if (opts.p) properties.'dbb.RepositoryClient.password' = opts.p
	if (opts.P) properties.'dbb.RepositoryClient.passwordFile' = opts.P
	
	// handle --clean option
	if (opts.C)  {
		println("** Clean up option selected")
		def repo = getDefaultRepositoryClient()
		
		println("* Deleting dependency collection ${properties.collection}")
		repo.deleteCollection(properties.collection)

		println("* Deleting build result group ${properties.collection}Build")
		repo.deleteBuildResults("${properties.collection}Build")
		
		System.exit(0)
	}

	// load file.properties containing file specific properties like script mappings and CICS/DB2 content flags
	properties.load(new File("${properties.confDir}/file.properties"))
	
	if ( opts.o ) {
		// load datasets.properties containing system specific PDS names used by Mortgage Application build
		if ( new File("${properties.confDir}/datasets.${opts.o}.properties").exists() )
			properties.load(new File("${properties.confDir}/datasets.${opts.o}.properties"))
		else
			properties.load(new File("${properties.confDir}/datasets.properties"))
		// load bind.properties containing DB2 BIND PACKAGE parameters used by Mortgage Application build			
		if ( new File("${properties.confDir}/bind.${opts.o}.properties").exists() )
			properties.load(new File("${properties.confDir}/bind.${opts.o}.properties"))
		else if ( new File("${properties.confDir}/bind.properties").exists() )
			properties.load(new File("${properties.confDir}/bind.properties"))
		// load bindlinkEditScanner.properties containing Link Edit scanning options used by Mortgage Application build
		if ( new File("${properties.confDir}/linkEditScanner.${opts.o}.properties").exists() )
			properties.load(new File("${properties.confDir}/linkEditScanner.${opts.o}.properties"))
		else if ( new File("${properties.confDir}/linkEditScanner.properties").exists() )
			properties.load(new File("${properties.confDir}/linkEditScanner.properties"))
	} else {
		// load datasets.properties containing system specific PDS names used by Mortgage Application build
		properties.load(new File("${properties.confDir}/datasets.properties"))
		// load bind.properties containing DB2 BIND PACKAGE parameters used by Mortgage Application build
		if ( new File("${properties.confDir}/bind.properties").exists() )
			properties.load(new File("${properties.confDir}/bind.properties"))    
		// load bindlinkEditScanner.properties containing Link Edit scanning options used by Mortgage Application build
		if ( new File("${properties.confDir}/linkEditScanner.properties").exists() )
			properties.load(new File("${properties.confDir}/linkEditScanner.properties"))
	}

	println("** Build properties at startup:")
	println(properties.list()) 

	return properties                                 
}

def validateRequiredProperties(List<String> props) {
    def properties = BuildProperties.getInstance()
    props.each { prop ->
        // handle password special case i.e. can have either password or passwordFile
    	if (prop.equals("password")) {
    		if (!(properties.'dbb.RepositoryClient.password' || properties.'dbb.RepositoryClient.passwordFile')) {
		     	assert properties.'dbb.RepositoryClient.password' : "Missing property 'dbb.RepositoryClient.password'"
		      	assert properties.'dbb.RepositoryClient.passwordFile' : "Missing property 'dbb.RepositoryClient.passwordFile'"
	       }
    	}
    	else {
    		assert properties."$prop" : "Missing property $prop"
    	}
    }
}


def getBuildList(List<String> args) {
    def properties = BuildProperties.getInstance()
    def files = []
    
	// Set the buildFile or buildList property
	if (args) {
		def buildFile = args[0]
	    if (buildFile.endsWith(".txt")) {
			if (buildFile.startsWith("/"))
				properties.buildListFile = buildFile
		      else
				properties.buildListFile = "$properties.sourceDir/$buildFile".toString()
	  	}
	    else {
			properties.buildFile = buildFile
	    }
	}    

	// check to see if a build file was passed in
	if (properties.buildFile) {
		println("** Building file $properties.buildFile")
		files = [properties.buildFile]
	}
	// else check to see if a build list file was passed in
	else if (properties.buildListFile) { 
		println("** Building files listed in $properties.buildListFile")
	    files = new File(properties.buildListFile) as List<String>
	}   
	// build the entire Mortgage Application listed in files.txt   
	else { 
	    println("** Building files listed in ${properties.confDir}/files.txt")
	    files = new File("${properties.confDir}/files.txt") as List<String>
	}	
	return files
}

def createDatasets() {
    def properties = BuildProperties.getInstance()
	def srcOptions = "cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)"
	def loadOptions = "cyl space(1,1) dsorg(PO) recfm(U) blksize(32760) dsntype(library) msg(1)" 
	def srcDatasets = ["COBOL", "COPYBOOK", "OBJ", "BMS", "DBRM", "LINK", "MFS"]
	def loadDatasets = ["LOAD", "TFORMAT"]

	srcDatasets.each { dataset ->
		new CreatePDS().dataset("${properties.hlq}.$dataset").options(srcOptions).create()
	}

	loadDatasets.each { dataset ->
		new CreatePDS().dataset("${properties.hlq}.$dataset").options(loadOptions).create()
	}
	
	if (properties.errPrefix) {
	    def xmlOptions = "tracks space(200,40) dsorg(PS) blksize(27998) lrecl(16383) recfm(v,b) new"
    	new CreatePDS().dataset("${properties.hlq}.${properties.errPrefix}.SYSXMLSD.XML").options(xmlOptions).create()
	}

}

def getDefaultRepositoryClient() {
    def properties = BuildProperties.getInstance()
	def repositoryClient = new RepositoryClient().forceSSLTrusted(true)
	return repositoryClient
}

def initializeBuildArtifacts() {
    BuildReportFactory.createDefaultReport()
    def properties = BuildProperties.getInstance()
    if (!properties.userBuild) {
        def repo = getDefaultRepositoryClient()
        properties.buildGroup = "${properties.collection}" as String
        properties.buildLabel = "build.${properties.startTime}" as String
        def buildResult = repo.createBuildResult(properties.buildGroup, properties.buildLabel) 
        buildResult.setState(buildResult.PROCESSING)
        if (properties.buildHash)
            buildResult.setProperty("buildHash", properties.buildHash)
        buildResult.save()
        println("** Build result created at ${buildResult.getUrl()}")
    }
}

def getBuildResult() {
    def properties = BuildProperties.getInstance()
    def buildResult = null
    if (!properties.userBuild) {
        def repo = getDefaultRepositoryClient()
        buildResult = repo.getBuildResult(properties.buildGroup, properties.buildLabel)           
    }
    return buildResult
}

def generateBuildReport() {
    def properties = BuildProperties.getInstance()
    def jsonOutputFile = new File("${properties.workDir}/BuildReport.json")
    def htmlOutputFile = new File("${properties.workDir}/BuildReport.html")

	// create build report data file
	def buildReportEncoding = "UTF-8"
	def buildReport = BuildReportFactory.getBuildReport()
	buildReport.save(jsonOutputFile, buildReportEncoding)

	// create build report html file
	def htmlTemplate = null  // Use default HTML template.
	def css = null       // Use default theme.
	def renderScript = null  // Use default rendering.                       
	def transformer = HtmlTransformer.getInstance()
	transformer.transform(jsonOutputFile, htmlTemplate, css, renderScript, htmlOutputFile, buildReportEncoding)   
	
	return [ jsonOutputFile, htmlOutputFile ]                      
}

def getDefaultDependencyResolver(String file) {
    def properties = BuildProperties.getInstance()
	def path = new DependencyPath().sourceDir(properties.sourceDir).directory("${properties.copyPath}")
	def rule = new ResolutionRule().library("SYSLIB").path(path)
    def resolver = new DependencyResolver().sourceDir(properties.sourceDir).file(file).rule(rule)
    if (properties.userBuild)
    	resolver.setScanner(new DependencyScanner())
    else {
        path.setCollection(properties.collection)
        resolver.setCollection(properties.collection)
        resolver.setRepositoryClient(getDefaultRepositoryClient())
    }
    return resolver
}

def getDefaultImpactResolver(String file) {
	def properties = BuildProperties.getInstance()
	
	// Add rule to assocuate changes to copybooks with the need to recompile programs that reference them.
   	def cpypath = new DependencyPath().collection(properties.collection).sourceDir(properties.sourceDir).directory("${properties.copyPath}")
   	def cpyrule = new ResolutionRule().library("SYSLIB").path(cpypath)
	   
	// Add rule to associate changes to BMS maps with need to recompile programs that use the generated copybook.
   	def bmspath = new DependencyPath().collection(properties.collection).sourceDir(properties.sourceDir).directory("${properties.bmsPath}")
   	def bmsrule = new ResolutionRule().library("SYSLIB").path(bmspath)
	   
	// Add rule to associate changes to COBOL source with the need to linkedit .lnk files that reference them.
   	def cobpath = new DependencyPath().collection(properties.collection).sourceDir(properties.sourceDir).directory("${properties.cobolPath}")
   	def coblnkrule = new ResolutionRule().category("LINK").path(cobpath)
	def cobrule = new ResolutionRule().category("COB").path(cobpath)
	def testrule = new ResolutionRule().category("CALL").path(cobpath)
	   
   	def resolver = new ImpactResolver().repositoryClient(getDefaultRepositoryClient())
	   .collection(properties.collection)
	   .collection("${properties.collection}_outputs")  // Name must agree with outputs collection used in LinkEdit.groovy and CobolCompile.groovy
	   .rule(cpyrule)
	   .rule(bmsrule)
	   .rule(cobrule)
	   .rule(coblnkrule)
	   .rule(testrule)
	   .file(file)
   	return resolver
}

def updateBuildResult(Map args) {
    def properties = BuildProperties.getInstance()
    def error = args.rc > args.maxRC
    def errorMsg = null
    if (error) {
        errorMsg = "*! The return code (${args.rc}) for ${args.file} exceeded the maximum return code allowed (${args.maxRC})"
    	println(errorMsg)
    	properties.error = "true"
    }
    	
    if (!properties.userBuild) {
    	def buildResult = getBuildResult()
    	def member =  CopyToPDS.createMemberName(args.file)
		if (error) {
			buildResult.setStatus(buildResult.ERROR)
			buildResult.addProperty("error", errorMsg)
			if (args.log != null)
				buildResult.addAttachment("${member}.log", new FileInputStream(args.log))
		}
		buildResult.save()   
	}                                      
}

def finalizeBuildResult(Map args) {
	def properties = BuildProperties.getInstance()
	if (!properties.userBuild) {
		def buildResult = getBuildResult()
		buildResult.setBuildReport(new FileInputStream(args.htmlReport))
		buildResult.setBuildReportData(new FileInputStream(args.jsonReport))
		buildResult.setProperty("filesProcessed", String.valueOf(args.filesProcessed))
		buildResult.setState(buildResult.COMPLETE)
		buildResult.save() 
	}
}

// Takes a DBB build report JSON file and an deployType string
// Returns all module names with this deployType as a list
def getAllOfType(String buildReport, String checkDeployType) {
	def jsonSlurper = new JsonSlurper()
	def parsedReport = jsonSlurper.parseText(buildReport)
	returnList = []

	for (record in parsedReport.records) {
		if (record.outputs != null) {
			for (output in record.outputs) {
				if (output.dataset != null && output.deployType != null) {
					if (output.deployType == checkDeployType) {
						member = output.dataset.replaceAll(/.*\((.*)\)/, "\$1")
						returnList.add(member)
					}
				}
			}
		}
	}

	return returnList
}

