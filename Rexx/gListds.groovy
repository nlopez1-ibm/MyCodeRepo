// TSO LISTDS 
@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.build.*
import com.ibm.dbb.dependency.*
import groovy.util.*
import groovy.transform.*

@Field def buildUtils= loadScript(new File("BuildUtilities.groovy"))


loadLib = 'NLOPEZ.GAR.ORT.QQ.P092258.LOAD'
def Listds= new TSOExec().command("listds '$loadLib' m")		
		.logFile(new File("/u/nlopez/MyCodeRepo/Rexx/listds.log"))		
		.keepCommandScript(true)
Listds.dd(new DDStatement().name("CMDSCP").dsn("NLOPEZ.ISPFGWY.EXEC").options("shr"))
def rc = Listds.execute()

if (!rc) {
	def mainList = []
	def SubMap   = [:]
		
	foundMem = false	
	new File("/var/ispf/WORKAREA/NLOPEZ.last").eachLine {
		   line ->
		   if (foundMem && line.substring(0,3) == "   ") {			   			   
			   mainList.add line 
			   
			   // Scan a single program object
			   def file = "dummy"
			   def loadPDS = "${properties.hlq}.LOAD"
			   def member = line.replaceAll("\\s","") 
			   println "Added $member"
			   def logicalFile = new LinkEditScanner().scan(file, loadLib, member)
			   println logicalFile 
		   }		   
		   if (line == " --MEMBERS--") {
		   		foundMem = true
				println "start of mems"
		   }
	}
}
else
	println "Error getting list of members for $loadLib"
		   
/* data structure.
 * l1 = list of members in pds
 * l2 = subs with recursive use count 
 */