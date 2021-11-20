// Run a sample Rexx Exec on MVS
// To run this enter groovyz RexxTest.groovy
// Ouptut is in the log file
//************************************************
import com.ibm.dbb.build.*
import groovy.util.*


// Mvs Exec Lib and member
def Rexx = new TSOExec().command("EX 'NLOPEZ.DAT.EXEC(TEST)'")


// required DD
Rexx.dd(new DDStatement().name("CMDSCP").dsn("NLOPEZ.ISPFGWY.EXEC").options("shr"))

// A conf file needed
Rexx.confDir(System.getenv("DBB_HOME")+"/conf")

// Sysout
Rexx.logFile(new File("/u/nlopez/Rexx/sysprint.log"))
rc = Rexx.execute()

println("Rexx exec RC = " + rc)
