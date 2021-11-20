@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*
import groovy.transform.*

//@njl - custom version for Garanti Sept 2019 -  v106a
//--------------------------------------------------------------
@Field BuildProperties props = BuildProperties.getInstance()
@Field def buildUtils= loadScript(new File("${props.zAppBuildDir}/utilities/BuildUtilities.groovy"))
@Field def impactUtils= loadScript(new File("${props.zAppBuildDir}/utilities/ImpactUtilities.groovy"))
@Field def bindUtils= loadScript(new File("${props.zAppBuildDir}/utilities/BindUtilities.groovy"))
@Field RepositoryClient repositoryClient

println("** Building files mapped to ${this.class.getName()}.groovy script   v106a ")
 
// verify required build properties
buildUtils.assertBuildProperties(props.cobol_requiredBuildProperties)

// sort the build list based on build file rank if provided
List<String> sortedList = buildUtils.sortBuildList(argMap.buildList, 'cobol_fileBuildRank')

// main loop - build each file in the sorted buildList
sortedList.each { buildFile ->
	println "\n*** Building file $buildFile"
	
	
	
	//** TEST SSI / HASH 
	
	
	//**** 
	// copy build file and dependency files to PDS (future Enhance - avoid copySource and compile from path)
	String rules = props.getFileProperty('cobol_resolutionRules', buildFile)
	DependencyResolver dependencyResolver = buildUtils.createDependencyResolver(buildFile, rules)
    buildUtils.copySourceFiles(buildFile, props.cobol_srcPDS, props.cobol_cpyPDS, dependencyResolver)
	
	// create Logical file and log file 
	LogicalFile logicalFile = dependencyResolver.getLogicalFile()
	String member 			= CopyToPDS.createMemberName(buildFile)
	
	File logFile 			= new File("${props.buildOutDir}/${member}.cobol.log")
	if (logFile.exists())
		logFile.delete()
	
	File db2_logFile 	= new File("${props.buildOutDir}/${member}.db2.log")
	if (db2_logFile.exists())
		db2_logFile.delete()
		

    // Init build parms by program type using the member name   
	if (!createCobolParms(buildFile, logicalFile, member)) {
    	println "***! Build stopped. BAD Application-conf/cobol.properties config. Missing Compile Parms for $member"
		props.error = "true"
    	System.exit(12)
    }

	// Allocate the DDnames and Build programs like the compiler ...
	MVSExec precompiler  = createPreCompileCommand(buildFile, logicalFile, member, db2_logFile)
	MVSExec compile      = createCompileCommand(buildFile, logicalFile, member, logFile)
    MVSExec linkEdit     = createLinkEditCommand(buildFile, logicalFile, member, logFile)
	MVSJob job 			 = new MVSJob()
	job.start()

    // println("** Skipping Bind option in cobol.groovy (njl")
	boolean bindFlag =true
	bindFlag 		 =false

	boolean skipcompile = false
	boolean skiplink 	= false
	//* if DB2 prog (isSQL), run the precompiler. If the precompiler fails > 4, then skip the compile, link and bind
		
 
	if (buildUtils.isSQL(logicalFile))  {
    	println "** Running DB2 Precomplier Step"	
					
		int pcRC = precompiler.execute()
		if  (pcRC > 4) {
			skipcompile	=true
			skiplink	=true
			bindFlag 	=false
			String errorMsg = "*! The DB2 Precompiler return code ($pcRC) for $buildFile exceeded the maximum return code of 4"
			println(errorMsg)
			props.error = "true"
			buildUtils.updateBuildResult(errorMsg:errorMsg,logs:["${member}.log":logFile],client:getRepositoryClient())
        }
    }

    
	if (!skipcompile) {
    	int rc    = compile.execute()
		int maxRC = props.getFileProperty('cobol_compileMaxRC', buildFile).toInteger()	
        if (rc > maxRC) {
			skiplink	=true
			bindFlag 	=false
			String errorMsg = "*! The compile return code ($rc) for $buildFile exceeded the maximum return code allowed ($maxRC)"
			println(errorMsg)
			props.error = "true"
			buildUtils.updateBuildResult(errorMsg:errorMsg,logs:["${member}.log":logFile],client:getRepositoryClient())
	        }
    }

	//* needsLinking is a property to skip linkedit - garanti always link so we may pull this out
	String needsLinking = props.getFileProperty('cobol_linkEdit', buildFile)
	if (!skiplink && needsLinking.toBoolean()) {
		rc    = linkEdit.execute()
		maxRC = props.getFileProperty('cobol_linkEditMaxRC', buildFile).toInteger()
		
	
		if (rc > maxRC) {
			bindFlag = false
       		String errorMsg = "*! The link edit return code ($rc) for $buildFile exceeded the maximum return code allowed ($maxRC)"
        	println(errorMsg)
	        props.error = "true"
		    buildUtils.updateBuildResult(errorMsg:errorMsg,logs:["${member}.log":logFile],client:getRepositoryClient())
       	} else {
	       	// only scan the load module if load module scanning turned on for file
    		// scans for static dependancies
		    String scanLoadModule = props.getFileProperty('cobol_scanLoadModule', buildFile)
		    if (scanLoadModule && scanLoadModule.toBoolean() && getRepositoryClient())
				impactUtils.saveStaticLinkDependencies(buildFile, "$syslmod", logicalFile, repositoryClient)
       		}
        }
	
	//@NJL  pending reconfig/review ...	
	if (bindFlag && logicalFile.isSQL() && props.RUN_DB2_BIND && props.RUN_DB2_BIND.toBoolean() ) {
		int bindMaxRC = props.getFileProperty('bind_maxRC', buildFile).toInteger()
		def owner = ( props.userBuild || ! props.OWNER ) ? System.getProperty("user.name") : props.OWNER
		
		def (bindRc, bindLogFile) = bindUtils.bindPackage(buildFile, props.cobol_dbrmPDS, props.buildOutDir, props.CONFDIR,
				props.SUBSYS, props.COLLID, owner, props.QUAL, props.verbose && props.verbose.toBoolean());
		if ( bindRc > bindMaxRC) {
			String errorMsg = "*! The bind package return code ($bindRc) for $buildFile exceeded the maximum return code allowed ($props.bind_maxRC)"
			println(errorMsg)
			props.error = "true"
			buildUtils.updateBuildResult(errorMsg:errorMsg,logs:["${member}_bind.log":bindLogFile],client:getRepositoryClient())
		}
	}
	
	// clean up passed DD statements
	job.stop()
}
// end of script


//********************************************************************
//* Method definitions
//********************************************************************

/*
 * createCobolParms - Builds up the parameter list from property file
 */
def createCobolParms(String buildFile, LogicalFile logicalFile, String member) {
    //get the first byte of the program name to use for getting compile properties processor groups
	props.pType=member[0]

	// Use the application-conf/application.property model_subID to test for matching Server Managers prefix
	// Note - test for SrvMgr first as the 1 char of Action Blocks may have the same letter as a Server 
    if (member.startsWith(props.model_subID))
    	props.pType="SrvMgr"
	 
	def parms  = props.getFileProperty('cobol_'+props.pType+'_CompParms', buildFile) ?: ""
	if (!parms) 		
			props.pType==null	
     
	def cics  = props.getFileProperty('cobol_compileCICSParms', buildFile) ?: ""
	

	//	def sql   = props.getFileProperty('cobol_compileSQLParms', buildFile) ?: ""
	//	def parms = props.getFileProperty('cobol_compileParms', buildFile) ?: ""
		

    //NJL   fix this 	
	//if (buildUtils.isCICS(logicalFile))
	//	parms = "$parms,$cics"
		
	//if (buildUtils.isSQL(logicalFile))
	//	parms = "$parms,$sql"
	

	// add debug options -future enhancement for Garanti
        //	if (props.debug)  {
	//	def compileDebugParms = props.getFileProperty('cobol_compileDebugParms', buildFile)
	//	parms = "$parms,$compileDebugParms"
	//}
		
	//if (parms.startsWith(','))
	//	parms = parms.drop(1)
	
		
	if (props.verbose) 
		println "** Using compiler parms<$parms &cics> for $buildFile"
	

	return parms
}

/*
 * createPreCompileCommand - creates a MVSExec command for DB2 PreCompiler (njl)
 */
def createPreCompileCommand(String buildFile, LogicalFile logicalFile, String member, File db2_logFile) {
	String preCompiler = props.getFileProperty('cobol_preCompiler', buildFile)
	String parms       = props.getFileProperty('cobol_'+props.pType+'_PreCompParms', buildFile) ?: ""
		
	MVSExec precompiler = new MVSExec().file(buildFile).pgm(preCompiler).parm(parms)
	precompiler.dd(new DDStatement().name("TASKLIB").dsn(props."SDSNLOAD").options("shr"))
	
	precompiler.dd(new DDStatement().name("SYSIN").dsn("${props.cobol_srcPDS}($member)").options('shr').report(true))
    precompiler.dd(new DDStatement().name("SYSLIB").dsn(props.cobol_cpyPDS).options("shr"))	
	precompiler.dd(new DDStatement().name("SYSPRINT").options(props.cobol_printTempOptions))
	precompiler.copy(new CopyToHFS().ddName("SYSPRINT").file(db2_logFile).hfsEncoding(props.logEncoding).append(true))
	

	precompiler.dd(new DDStatement().name("DBRMLIB").dsn("$props.cobol_dbrmPDS($member)").options('shr').output(true).deployType('DBRM'))
	//put translated source in same CPY for passign to compiler 
    precompiler.dd(new DDStatement().name("SYSCIN").dsn("$props.cobol_cpyPDS($member)").options('shr'))
	precompiler.dd(new DDStatement().name("SYSUT1").options(props.cobol_tempOptions))
	precompiler.dd(new DDStatement().name("SYSUT2").options(props.cobol_tempOptions))	

	return precompiler
}


/*
 * createCompileCommand - creates a MVSExec command for compiling the COBOL program (buildFile)
   make sure the compiler parms do not inclde the SQL option which we cause a pre-compile and
   conflicts with the seperate preCompile step
 */
def createCompileCommand(String buildFile, LogicalFile logicalFile, String member, File logFile) {
	String compiler  = props.getFileProperty('cobol_compiler', buildFile)
	String parms     = props.getFileProperty('cobol_'+props.pType+'_CompParms', buildFile) ?: ""
	def cics         = props.getFileProperty('cobol_compileCICSParms', buildFile) ?: ""

	if (buildUtils.isCICS(logicalFile))
		parms = "$parms,$cics"
	//println "<<trace COMPILE >> $parms"


	// define the MVSExec command to compile the program
	MVSExec compile = new MVSExec().file(buildFile).pgm(compiler).parm(parms)
	
	if (buildUtils.isSQL(logicalFile))			
		compile.dd(new DDStatement().name("SYSIN").dsn("${props.cobol_cpyPDS}($member)").options('shr').report(true))
	else   
		compile.dd(new DDStatement().name("SYSIN").dsn("${props.cobol_srcPDS}($member)").options('shr').report(true))
		
	compile.dd(new DDStatement().name("SYSMDECK").options(props.cobol_tempOptions))
    compile.dd(new DDStatement().name("SYSPRINT").options(props.cobol_printTempOptions))
    compile.copy(new CopyToHFS().ddName("SYSPRINT").file(logFile).hfsEncoding(props.logEncoding).append(true))
	(1..17).toList().each { num ->
		compile.dd(new DDStatement().name("SYSUT$num").options(props.cobol_tempOptions))
	}
	

	//add sdeck import & libray !!?? 

	
	String doLinkEdit = props.getFileProperty('cobol_linkEdit', buildFile)
	String linkEditStream = props.getFileProperty('cobol_linkEditStream', buildFile)

	compile.dd(new DDStatement().name("SYSLIN").dsn("${props.cobol_objPDS}($member)").options('shr').output(true))			
	compile.dd(new DDStatement().name("SYSLIB").dsn(props.cobol_cpyPDS).options("shr"))
		//always concat system libs - careful with prod vs dvl versions 
		//* app Libs 
		compile.dd(new DDStatement().dsn(props.bms_cpyPDS).options("shr"))
		compile.dd(new DDStatement().dsn(props.cobol_BMS_PDS).options("shr"))
		//* Sys libs 
		compile.dd(new DDStatement().dsn(props.SDFHCOB).options("shr"))
		compile.dd(new DDStatement().dsn(props.SCSQCOBC).options("shr"))
		
		
		
		
	//** njl old way keep for ref	
	//if (props.bms_cpyPDS)
		//compile.dd(new DDStatement().dsn(props.bms_cpyPDS).options("shr"))
	//if(props.team)
		//compile.dd(new DDStatement().dsn(props.cobol_BMS_PDS).options("shr"))
	//if (buildUtils.isCICS(logicalFile))
		//compile.dd(new DDStatement().dsn(props.SDFHCOB).options("shr"))
    //String isMQ = props.getFileProperty('cobol_isMQ', buildFile)
	//if (isMQ && isMQ.toBoolean())
		//compile.dd(new DDStatement().dsn(props.SCSQCOBC).options("shr"))


	// add a tasklib to the compile command with optional CICS, DB2, and IDz concatenations
	String compilerVer = props.getFileProperty('cobol_compilerVersion', buildFile)
	compile.dd(new DDStatement().name("TASKLIB").dsn(props."SIGYCOMP_$compilerVer").options("shr"))
		compile.dd(new DDStatement().dsn(props."SCEERUN").options("shr"))
		compile.dd(new DDStatement().dsn(props."SCEERUN2").options("shr"))
	
    
		//@njl - simpler ver just add everything  db2,cics,mq,.coolgen ...
		if (buildUtils.isCICS(logicalFile))
			compile.dd(new DDStatement().dsn(props.SDFHLOAD).options("shr"))
		if (buildUtils.isSQL(logicalFile))
			compile.dd(new DDStatement().dsn(props.SDSNLOAD).options("shr"))

    //njl suppressed for Garanti	
	//if (props.SFELLOAD)
	//	compile.dd(new DDStatement().dsn(props.SFELLOAD).options("shr"))
	// add optional DBRMLIB if build file contains DB2 code
	//      if (buildUtils.isSQL(logicalFile))
	//	compile.dd(new DDStatement().name("DBRMLIB").dsn("$props.cobol_dbrmPDS($member)").options('shr').output(true).deployType('DBRM'))

	return compile
}


/*
 * createLinkEditCommand - creates a MVSExec xommand for link editing the COBOL object module produced by the compile
 *
 * note the link parms are driven by file naming conventions like C* are action blocks ...
 * todo - add the logic here and in the linkedit folder for the main Server manager
 * SYSLIN is passed as a TEMPOBJ file
 *
 */
def createLinkEditCommand(String buildFile, LogicalFile logicalFile, String member, File logFile) {
	String linker = props.getFileProperty('cobol_linkEditor', buildFile)	
	String parms  = props.getFileProperty('cobol_'+props.pType+'_LinkParms', buildFile) ?: ""		
	MVSExec linkedit = new MVSExec().file(buildFile).pgm(linker).parm(parms)
	
	
	
	
	
	
	
	
	//dir = buildUtils.getAbsolutePath(buildFile)
	
	//public String path() {
		
	//dir = buildFile.substring(buildFile.lastIndexOf("/"));
	String dir = buildFile.split('/')
	dir = dir[dir.size-1];
	println "<<  HASH >> Dir $dir" 
	//if (gitUtils.isGitDir(dir)) {
	//	String hash = gitUtils.getCurrentGitHash(dir)
	//	
	//	String key = "$hashPrefix${buildUtils.relativizePath(dir)}"
	//	buildResult.setProperty(key, hash)
	//	if (props.verbose) println "** Setting property $key : $hash"
	//}
	//else {
	//	if (props.verbose) println "**! Directory $dir is not a Git repository"
	//}
	
	
	
	
	
	//println "<<TRACE linkEdit Parms in effect>> $parms"
	
	switch (props.pType) {
		case "SrvMgr": 
				syslmod=props.cobol_loadPDS
				//testing import cards hard coded 
				linkedit.dd(new DDStatement().name("SYSIN").dsn("NLOPEZ.GAR.IMPORTS(QQ)").options('shr'))
				break;		
		case "I":
				syslmod=props.cobol_loadPDS
				break;		
		default:
				syslmod=props.cobol_NCAL_loadPDS;				
	}
	linkedit.dd(new DDStatement().name("SYSLMOD").dsn("$syslmod($member)").options('shr').output(true).deployType('LOAD'))
	
	
			
	// Generate the syslin cards - needs more work - like hotfix with replace options ...
	String LinkCard= props.getFileProperty('cobol_'+props.pType+'_LinkCard', buildFile) ?: ""
	if (LinkCard) {
		def linkFile = new File("${props.buildOutDir}/linkCard.lnk")
		if (linkFile.exists())
			linkFile.delete()				
		linkFile << "  " + LinkCard.replace("\\n","\n").replace('@{member}',member)
		println("## Copying ${props.buildOutDir}/linkCard.lnk to $props.cobol_srcPDS(#LINKCRD)")
		new CopyToPDS().file(linkFile).dataset(props.cobol_srcPDS).member("#LINKCRD").execute()
		
		linkedit.dd(new DDStatement().name("SYSLIN").dsn("${props.cobol_objPDS}($member)").options('shr'))
		linkedit.dd(new DDStatement().dsn("${props.cobol_srcPDS}(#LINKCRD)").options("shr"))		
	} else {
		println("**! Error: Missing LinkCards for $member   See App-Conf/Cobol.prop.")
		props.error = "true"
	}
	
	
	
	linkedit.dd(new DDStatement().name("SYSUT1").options(props.cobol_tempOptions))
	linkedit.dd(new DDStatement().name("SYSPRINT").options(props.cobol_printTempOptions))
	linkedit.copy(new CopyToHFS().ddName("SYSPRINT").file(logFile).hfsEncoding(props.logEncoding).append(true))
	

	//#import/library for DYN DLL Interfaces -example
	//linkedit.dd(new DDStatement().name("SYSDEFSD").dsn(props.cobol_Imports).options("shr"))
	// dont use side
	linkedit.dd(new DDStatement().name("SYSDEFSD").dsn("NULLFILE"))
	
	//## Gar never uses obj for autocall
	//#linkedit.dd(new DDStatement().name("SYSLIB").dsn(props.cobol_imports).options("shr"))
	
	linkedit.dd(new DDStatement().name("SYSLIB").dsn(props.cobol_loadPDS).options("shr"))
		linkedit.dd(new DDStatement().dsn(props.SCEELKED).options("shr"))
		linkedit.dd(new DDStatement().dsn(props.SDFHLOAD).options("shr"))
		linkedit.dd(new DDStatement().dsn(props.SCSQLOAD).options("shr"))
		linkedit.dd(new DDStatement().dsn(props.SDSNLOAD).options("shr"))
		
		//#library for static action blocks  DVL 
		linkedit.dd(new DDStatement().dsn(props.cobol_NCAL_loadPDS).options("shr"))

		//#library for Prod Libs - test when ready - do a delta chg and pick up from prod
		//linkedit.dd(new DDStatement().dsn(props.cobol_PROD_NCAL_Load).options("shr"))
		//linkedit.dd(new DDStatement().dsn(props.cobol_PROD_Load).options("shr"))
		
	//######
	//# either Concat like above or qualify ... above is easier - pending full testing  	
	//#linkedit.dd(new DDStatement().dsn(props.SCEELKED).options("shr"))
    //#if (buildUtils.isCICS(logicalFile))
	//#	linkedit.dd(new DDStatement().dsn(props.SDFHLOAD).options("shr"))
		
	//#String isMQ = props.getFileProperty('cobol_isMQ', buildFile)
    //#if (isMQ && isMQ.toBoolean())
    //#	linkedit.dd(new DDStatement().dsn(props.SCSQLOAD).options("shr"))
	//#if (isSQ: ** isSQL.toBoolean())
	//#	linkedit.dd(new DDStatement().dsn(props.SDSNLOAD).options("shr"))
	//############
			
	
	// add RESLIB if needed    - needs reseach
	//if ( props.RESLIB ) {
	//	linkedit.dd(new DDStatement().name("RESLIB").dsn(props.RESLIB).options("shr"))
	//}

    return linkedit
}


def getRepositoryClient() {
	if (!repositoryClient && props."dbb.RepositoryClient.url")
		repositoryClient = new RepositoryClient().forceSSLTrusted(true)	
	return repositoryClient
}