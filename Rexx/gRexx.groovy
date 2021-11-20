// Run a sample Rexx Exec on MVS
// To run this enter groovyz gRexx.groovy
// Ouptut is cat'ed  
//************************************************
import com.ibm.dbb.build.*   	// need the DBB TSOExec API 

// sample MVS Exec Lib and member:
// def Rexx = new TSOExec().command("EX 'NLOPEZ.DAT.EXEC(TEST)'")
// def Rexx = new TSOExec().command("DSN");
def Rexx = new TSOExec().command("LISTC");

// required DD - it gets created for you 
Rexx.dd(new DDStatement().name("CMDSCP").dsn("NLOPEZ.ISPFGWY.EXEC").options("shr"))

// show my conf file path from .profile  "DBB_CONF=$DBB_HOME/conf"   
println "Conf file ->  " + System.getenv("DBB_CONF") 

isplog = "/u/nlopez/MyCodeRepo/Rexx/sysprint.log"
Rexx.logFile(new File(isplog))
rc = Rexx.execute()
println "cat $isplog ".execute().text  + "\n LastCC = $rc"


/*  Misc var for future testing *  
 *   Rexx.procedureName("GENERAL")
 *   Rexx.logLevel(1)
 *   Rexx.confDir(System.getenv("DBB_HOME")+"/conf")
 *   Rexx.confDir("/u/nlopez/IBM/ispf")
 */