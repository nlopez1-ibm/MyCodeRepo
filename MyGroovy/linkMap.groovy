/* Class to resolve complex static link dependencies. 
 *   See tail for Doc 
 */   
import groovy.transform.Field
import com.ibm.dbb.build.*
import com.ibm.dbb.dependency.*
import groovy.json.JsonSlurper
def boolean v = true		// Verbose 
 

//libs = ['NLOPEZ.GAR.ORT.QQ.P092258.LOAD', 'NLOPEZ.GAR.ORT.QQ.PROD.LOAD']
// see tso pds .dat.cobol(demo*)   main->stat  main->*dyn  main->*dll   stat->sta2   
def libs 		= ['NLOPEZ.DAT.LOAD']  	// the starting lib   
@Field mainMods 	= [:]					// list of map of main modules found in libs 
def subs 	 	= [:] 
/* data struct
 *   subs = [ dyn:[], main:[stat],   stat:[stat2] ]
 */
			
//          
// init the mpa with main modules 
libs.each {
		lib ->
			def members = getMems(lib)
			members.each { name ->  mainMods << [(name):[LIB:(lib)]] }
}			

// scan each module for static sub-modules -- how deep?  Depth is
println 'Initial mainMods Map ' + mainMods
keepScanning = true 
while( keepScanning ) {
	keepScanning = false		// assume no more static calls 
	
	mainMods.each { name, val ->		
			println 'Scanning -> ' +  name  + ' ' + val.LIB
			if (getStaticCalls(val.LIB, name) ) {				
					println 'found subs ' + mainMods
					keepScanning = true
					
					
					very close ... this do loop recurses over mainMod which is a global and appended with subs for each 
					itertation..  howeve, need logic to 
					bypassed prior scanned items ....?
			} 
	}
	//if( done ) break
	
}

//println mainMods.DEMOMAIN.LIB



System.exit(1)


			
// waste 2

// Scan lib(s) members.
def linkMap = [:]

libs.each { 
	lib ->
	if (v) {println "\nScanning Lib: $lib"}
	def members	= getMems(lib)
	
	members.each {
		member ->
			if (v) {println  "Member: $member" }			
			scanReport 	= new LinkEditScanner().scan("dummy", lib, member).toString()	 				 
			buildLinkMap(scanReport, "LINK", member, linkMap, subLibs)
	}
}
println 'pass1 linkMap: ' + linkMap
// pass 2 - scan subLibs 
subLibs.each {
	lib ->
		if (v) {println "\nScanning Lib: $lib"}
		def members	= getMems(lib)
		members.each {
			member ->
			if (v) {println  "Member: $member" }
			scanReport 	= new LinkEditScanner().scan("dummy", lib, member).toString()
			//println scanReport
			buildLinkMap(scanReport, "LINK", member, linkMap, subLibs)
		}
}

println 'pass2 linkMap: ' + linkMap
System.exit(0)




//* util Methods
// __________________________________________________________


//*** Pull the Static dependancies  
//***********************************

// Pull the Scanner's JSON result by DeployType and append to linkMap entried      
def getStaticCalls(lib, mod) {
	scanReport 		= new LinkEditScanner().scan("dummy", lib, mod).toString()			
	parsedReport 	= new JsonSlurper().parseText(scanReport)
	subsFound		= false 
		
	//println scanReport 
	 
	filterMods		= '^DFH|^CEE|^IGZ|^DSN'    
	 		
	if (parsedReport.logicalDependencies?.size) {
		parsedReport.logicalDependencies.each {
			if (it.category=='LINK' && !(it.lname =~ filterMods) && !(it.lname == mod)) {				
				//lib = it.library				
				/* 
				 * idx = libs.findIndexValues { it ==lib }
				 * if (!idx) {						
				 *		libs << lib
				 *		idx=0
				 * }
				 */
							
				//println "Found static mod $it.lname  with lib $it.library"
				mainMods << [(it.lname):[LIB:(it.library)]]
				subsFound = true
				
				
				//linkMap << [(it.lname):[main:[(mainMod)], lib:[(it.library)]] ]
				//linkMap << [(it.lname):[main:[(mainMod)], lib:(idx)] ]	 
			}
		}
	}
	return subsFound  
}

//* Run TSOExec to list mwembers of a PDS(e)
 //def getMainMods(libemListFile){
 def getMems(lib) {
	 def file 		= "dummy"
	 def dType 		= "LINK"
	 def memListFile	= "/var/ispf/WORKAREA/NLOPEZ.last"
	 def gw 			= "NLOPEZ.ISPFGWY.EXEC"
	 
	 Listds= new TSOExec().command("listds '$lib' m")
	 Listds.dd(new DDStatement().name("CMDSCP").dsn(gw).options("shr"))
	 rc = Listds.execute()
	 if (!rc) {
		 foundMem = false
		 memList = []
		 new File(memListFile).eachLine {
			 line ->
				 if (foundMem && line.substring(0,3) == "   ")
					 memList.add line.replaceAll("\\s","")
				 if (line == " --MEMBERS--")
					 foundMem = true
		 }
	 }
	 else {
		 println "Error getting list of members from $lib"
		 System.exit(1)
	 }
	 
	 return memList
 }




//* waste land
// ________________________________
/* 
	println parsedReport.logicalDependencies

	for (record in parsedReport.records) {
		println parseReport.size
	
	
	if (record.outputs != null) {
		for (output in record.outputs) {
			if (output.dataset != null && output.deployType != null) {
	println "Slup dt &output.deployType"
				if (output.deployType == DeployType) {
					member = output.dataset.replaceAll(/.*\((.*)\)/, "\$1")
					returnList.add(member)
				}
			}
		}
	}
	}
	
	
	
	println ">"+subMod.getClass() + "="  +mainMod.getClass() +"<"
	
	-- good code save area 
	SubMods.each { 
			subMod -> 					
				if (!mainMod.equals(subMod)) {					
					println "   Calls:$subMod"
					mainMod.add subMod
				}
				
		}
	
	
*/




// ***************** NOTES 
// ** Current
/* for some reason QQ has all componebts of the chain ... ? in the scanner and amblist???
 * current test chain is
 * 			qq|> C-121 |> C-131     |> C-130
 * 			  |        |> I-31(Dyn)
 *            |> D-a1
 *
 *
 *
 *
 * -- class design
 *  new build-map new (load prop file into class
 * 	get-mem - use to
 *  set-mem
 *  scan-lib lib
 *  delete-mem, mem, lib
 *  gen-relink cars, mem,lib?
 *
 *  always in impact mode? not full build?  do I need it?
 */


//*** pull the members from the loadlib
//*** order of concat is preserved. If dup mod found across loadLibs, first one is processed
//*** Submods not found within the loadLib scope are excluded from chain like system moudels (CEE..)
//*** SIDE NOTE - use replace for all static
//********************************************************************************************************
// loadLib is the scope of search for static.  can be concatenated (todo),
// issue: how to  track new items in a build.  This should be run in Full mode at least once and increm for each final build phase?
//   but- a new static item (sub that n-deep) is created in a new build, its caller(s) must also be build to include it.  if during that build we dont
//   get the order right this wont work either !!!   chicken/egg.   for ex, if dbb doesn;y know the rank of the caller or callee, it can build the caller first
//   and link it to what???  ideally, test the rc of the link, if not zero ???  not to elangant...
//   so this tool can see what was built but not whats about to be built !!!
//  its etiher dbb build main first or sub first.  if main, its link will fail- sub not found but th esubsquent sub link can trigger a relink of main
//                                                 if sub first, no prob but its unpredicable without a rank
// * more.. since the scanner returns the full chain (ie pg, 131 in qq), then the logic can be simply to find all modules that have no
//   sub first? no  becuase head of call will need sub firsts..  so the opposiute? find all heads as a kinjed list and
// work backwards
//

// PREREQIUE - COB 6.1 TEST, PDSE, BINDER (SAME AS SAYING NOT LINKEDIT WITH PDS)
// NOT SURE IF THIS ALSO WORK FOR NON -DLL (THESE ARE NCAL STATIC AND NCAL IMPORT/LIB) TEST PENDING
// the initlai scan libs cant be chg'd during impact builds.
// can concat dvl, prod ... hierarchy


/* work area linked list
 * BINDER COLLECTS ALL EXTERNS AND SCANNER LISTS THEM ALL
 * the process is to build )init scan_ ot auto=scan a simple input map  key=main:value-sub,level=1,lib=pdse ...
 * the absence of an entry assumes its end-of-chain or simple stand-alone
 * ...
 * current logical map
 * 			qq|> C121 |> C131     |> C130
 * 			  |       |> I31(Dyn)
 *            |> D1
 *
 * whats the structure !!!
 *   loop thru each main mod
 *   get its calls - all inclusive and get the subs calls until end of chain
 *
 *   scanmap                   qq: subs[c121,c131,c140,d1] the real pgm object
 *
 *   however we can see that a sub may also be a main
 *   so call the scanner for each sub until end of chain
 *
 *   scan s1 map 				s1: subs[s11, s22]
 *   end of chain (eoc)			s2, s3, s11, s22
 *
 *   result: build all eoc by rank
 *
 *
 *   goal - build oder by tail of chains dervided
 *
 *
 *
 *
 *   note an alt approach is to save all objects and have one massive bind? how is that better/worst than creating loads?
 *   I like loads as other mains can reuse them without need of another lib/file to persist
 *
 *
 *
 *   for new items - sca nthe compile output for external cross-references to resovle -  not that I* are indeterminate
 *
 *   test run no include iof main static 131? result is ...
 *   link cards used
 *   	IEW2278I B352 INVOCATION PARAMETERS - SIZE=(512K,128K),LIST,MAP,XREF,RENT,REUS,DYNAM(DLL),MODMAP(LOAD)
 *		IEW2322I 1220  1      IMPORT CODE,'IQQ1A031','IQQ1A031'
 *		IEW2322I 1220  2      LIBRARY *(IQQ1A031)
 *		IEW2322I 1220  3      ENTRY QQ1C0021
 *		IEW2322I 1220  4      NAME  QQ1C0021(R)
 *
 *   MainMod:QQ1C0021
 * 		{"dli":false,"lname":"QQ1C0021","file":"dummy","cics":false,"logicalDependencies":[
 * 		{"lname":"QQ1C0021","library"OPEZ.GAR.ORT.QQ.P092258.OBJ","category":"LINK"},
 *		{"lname":"IGZXBS61","library":"CEE.SCEELKED","category":"LINK"},
 *		{"lname":"CEESG004","library":"CEE.SCEELKED","category":"LINK"},
 *		{"lname":"CEESG003","library":"CEE.SCEELKED","catego"LINK"},
 *		{"lname":"IGZXTREN","library":"CEE.SCEELKED","category":"LINK"},
 *		{"lname":"IGZXRTN","library":"CEE.SCEELK"category":"LINK"},
 *		{"lname":"DFHEI1","library":"DFH.V5R1M0.CICS.SDFHLOAD","category":"LINK"},
 *		{"lname":"DQQM00A1"brary":"NLOPEZ.GAR.ORT.QQ.P092258.NCAL.LOAD",category":"LINK"},
 *		{"lname":"CQQ1A121","library":"NLOPEZ.GAR.ORT.QQ2258.NCAL.LOAD","category":"LINK"},
 *		{"lname":"CQQ1A131","library":"NLOPEZ.GAR.ORT.QQ.P092258.NCAL.LOAD","categoryINK"},
 *		{"lname":"CQQ1A130","library":"NLOPEZ.GAR.ORT.QQ.P092258.NCAL.LOAD","category":"LINK"},
 *		{"lname":"CEETLOC",rary":"CEE.SCEELKED","category":"LINK"},
 *		{"lname":"CEETGTFN","library":"CEE.SCEELKED","category":"LINK"}
 *		],"langua"ZBND","sql":false}
 *
 *
 *  for new items use the sysprint section
 *  Defined   Cross-reference of procedures   References
 *
 *     24   000000-CONTROLPP 5655-EC6 IBM Enterprise COBOL for z/OS  6.1.0 P190213       QQ1C0021  Date 12/21/2019  Time 21:56:38   Page     6
 *
 * Defined   Cross-reference of programs     References
 *
 *EXTERNAL   CQQ1A121 . . . . . . . . . . .  26
 *EXTERNAL   DFHEI1 . . . . . . . . . . . .  32
 *EXTERNAL   DQQM00A1 . . . . . . . . . . .  27
 *      8   QQ1C0021PP 5655-EC6 IBM Enterprise COBOL for z/OS  6.1.0 P190213       QQ1C0021  Date 12/21/2019  Time 21:56:38   Page     7
 *	Data Division Map
 *
 */




/* 12/16/19 test shows how ncal is not part of intermediate mod 121??? 
 * 
 *
 * 
*NLOPEZ:/u/nlopez/MyCodeRepo/MyGroovy #>groovyz linkMap.groovy
*
*Scanning Lib: NLOPEZ.GAR.ORT.QQ.P092258.LOAD
*Member: IQQ1A031

*Scanning Lib: NLOPEZ.GAR.ORT.QQ.PROD.LOAD
*Member: IQQ1A031
*Member: QQ1C0021
*adding scanned sub DQQM00A1  with lib index 0
*adding scanned sub CQQ1A121  with lib index [0]
*adding scanned sub CQQ1A131  with lib index [0]
*adding scanned sub CQQ1A130  with lib index [0]
**pass1 linkMap: [DQQM00A1:[main:[QQ1C0021], lib:0], CQQ1A121:[main:[QQ1C0021], lib:[0]], CQQ1A131:[main:[QQ1C0021], lib:[0]], CQQ1A130:[main:[QQ1C0021], lib:[0]]]
*
*Scanning Lib: NLOPEZ.GAR.ORT.QQ.P092258.NCAL.LOAD
*Member: CQQ1A121
*{"dli":false,"lname":"CQQ1A121","file":"dummy","cics":false,"logicalDependencies":[{"lname":"CQQ1A121","library":"NLOPEZ.GAR.ORT.QQ.P092258.OBJ","category":"LINK"}],"language":"ZBND","sql":false}
*Member: CQQ1A130
*{"dli":false,"lname":"CQQ1A130","file":"dummy","cics":false,"logicalDependencies":[{"lname":"CQQ1A130","library":"NLOPEZ.GAR.ORT.QQ.P092258.OBJ","category":"LINK"}],"language":"ZBND","sql":false}
*Member: CQQ1A131
*{"dli":false,"lname":"CQQ1A131","file":"dummy","cics":false,"logicalDependencies":[{"lname":"CQQ1A131","library":"NLOPEZ.GAR.ORT.QQ.P092258.OBJ","category":"LINK"}],"language":"ZBND","sql":false}
*Member: DQQM00A1
*{"dli":false,"lname":"DQQM00A1","file":"dummy","cics":false,"logicalDependencies":[{"lname":"DQQM00A1","library":"NLOPEZ.GAR.ORT.QQ.P092258.OBJ","category":"LINK"},{"lname":"IGZXBS61","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEESG004","library":"CEE.SCEELKED","category":"LINK"},{"lname":"IGZXRT1","library":"CEE.SCEELKED","category":"LINK"},{"lname":"IGZXTREN","library":"CEE.SCEELKED","category":"LINK"},{"lname":"IGZXRTN","library":"CEE.SCEELKED","category":"LINK"},{"lname":"IGZXCMSG","library":"CEE.SCEELKED","category":"LINK"},{"lname":"IGZXPRS","library":"CEE.SCEELKED","category":"LINK"},{"lname":"DSNHLI","library":"DFH.V5R1M0.CICS.SDFHLOAD","category":"LINK"},{"lname":"DSNHADD2","library":"DSN.V11R1M0.SDSNLOAD","category":"LINK"},{"lname":"DSNHADDR","library":"DSN.V11R1M0.SDSNLOAD","category":"LINK"},{"lname":"IGZXBST2","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEBETBL","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEESTART","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEBPUBT","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEBTRM","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEBLLST","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEBINT","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEARLU","library":"CEE.SCEELKED","category":"LINK"},{"lname":"CEEINT","library":"CEE.SCEELKED","category":"LINK"}],"language":"ZBND","sql":false}
*pass2 linkMap: [DQQM00A1:[main:[QQ1C0021], lib:0], CQQ1A121:[main:[QQ1C0021], lib:[0]], CQQ1A131:[main:[QQ1C0021], lib:[0]], CQQ1A130:[main:[QQ1C0021], lib:[0]]]
*** Build finished
*/



// ---
//			put add what the

/*
if (linkMap.containsKey(sub)){
	//append
	linkMap[(sub)].add  member
}
else {
	linkMap[(sub)] = '[$member]'
	linkMap[(sub)].put 'A'
}
  
//linkMap[(m)] ?  linkMap.[(m)].add member : "na"
	//linkMap[(m)] = linkMap[(m)] + member
}
//linkMap.[(member)] = linkMap.[(member)] +  'test'
}
}
println linkMap
System.exit(1)

println "pass1: " + buildMap




// ??  not sure what Im doign here. ... bye bye


// pass 2 - buildList by SubMods  found in pass1
if (v) {println  "\n\n pass2 Scanning subs "}
buildMap.each {
mainMod, subMods ->
if (v) {println "Main $mainMod"}
subMods.each {
subMod, lib ->
if (v) {println "Sub $subMod"}
if (v) {println  "Lib $lib"}

scanReport = new LinkEditScanner().scan("dummy", lib, subMod).toString()
subMods = getSubModChain(scanReport, "LINK", subMod)
//subMods.removeAll{ it == mainMod}
buildMap["$subMod"] =  subMods
}
}


println "pass2: " + buildMap



def thead = subs.find { it == pgm }
		
		

// BBMM - above is good start get main from one load,,, need sub of each?


println "\n - simo ---"
System.exit(1)




/* impact senarios
* 	1 - chg 130:
* 		- impacts: 131,121,qq
* 		- build 130 and link as ncal
* 		-
* 		- link 121 & qq? replacing 130 or just QQ.  will do all dep heads (since I cant tell whcih is the head... maybe a little recursuion fun)
* 	2 - cgh 130 & 131
*/
  
// S1  if pgm chg'd get head- if head is in Buildist- nop,  else gen relink card for chg'd pgm





//assertTrue(map.find{it.value == "New York"}.key == "city")
			
//println x.getClass().name
/* see tail for doc - notes
 * test case
 * 			qq |-> C121 |-> C131     |-> C130
 * 			   |        |-> I31(Dyn)
 *              |-> Da1
 *
 *  notice if ncal is on, the sub(s) are not in load lib or intermeddiate mod. ex 121 does not haev 131..????
 *  interesting config.  very effiecint as we only want subs in one main qq!!!
 */
 
// **************** WASTE ABOVE 


/* *********************
* Doc:
*   The goal is to generate a rank property tHAT forceS a build TO start from the lowest level root node up to
*   its  main node for any source file within the application scope.
*
* NCAL Notes:
*   The design at Garnati uses NCAL for external static and Dynam calls.  They are all resolved within the main.
*   This avoids a long chain and simplifies the link.  This code is used for traditional static calls or reusable static
*   routines that have chains.
*
* Usage:
*   First run in scan mode to gen a prop file called linkMap saved app-conf.
*   Input to the scan is a prod? load lib(s) to get a current(initial state) of static call dependancies.
*   The scan captures an inital sate of the applications call tree which will be updated during each sucessful build (???somehow).
*
*   Then, add a new 'build' prop "AutoGenRank=true" to manage the build/link rank using the linkMap.  Note that currently
*   rank is at the language level.  This should now be a build level as prgm's of different lanaguages can call
*   each other in any order.
*
*  zAppBuild Code changes:
*   Build.groovy has new code to implement this new feature.  It will
*   	- use the auto-generate rank prop for sorting the buildList
*   	- update LinkMap dependancies at finalize (???)
*
*   Methods:
*   	new() 					- init application linkMap.property (cache)
*   	scan(*mem, syslib(s)) 	- create a linkMap.property file and produce a linkMap.log
*   	get(mod)  				- return the callers of any mapped mpodule
*   	set(mod,caller)  		- update and persist the caller list of a mod
*   	genLink(mod)			- generate linkCards for a root module for link phase
*   	list 					- create a linkMap.log
*
*  TODO:
*  	alias 					- how to manage them
*  	entry names				- alternative link edit directive
*  
*  
*  
*  lang lacks abilty to grow a map in a closure loop - ConCurrentModificationExceptions!
*  was plannign on using that for recursively navigating the static call tree to build up a tuple based hierarcharcy map.
*  
*   
*  
*  
*/
