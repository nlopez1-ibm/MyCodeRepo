import com.ibm.dbb.build.*
//* NJL - sample - trans a IMS  pgm  
// Debug RC=77824 (one case member not found - see syslog)
// - https://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/topics/dfhp3_transl_options_intro.html


sysin="NLOPEZ.DAT.COBOL(IMSSAMP)"
syspunch="NLOPEZ.DAT.COBOL"
sysprint= new File("/u/nlopez/tmp/IMSBuild.sysprint")

String parms = "XOPTS(SOURCE,DLI,COBOL3,NOSEQ),FLAG(I)"

//use the cics translator for IMS EXEC DLI src  (NOTE: Need to escape the $) 
def compile = new MVSExec().pgm("DFHECP1\$").parm(parms)
compile.dd(new DDStatement().name("SYSIN").dsn(sysin).options("shr"))
compile.dd(new DDStatement().name("SYSPUNCH").dsn(sysin).options("shr"))

// a copy stmt in the code needs a syslib or the compiler fails with no sysprint/sysout 
(1..2).toList().each { num -> compile.dd(new DDStatement().name("SYSUT$num").options("cyl space(1,5) unit(vio) new"))}



compile.dd(new DDStatement().name("TASKLIB").dsn("DFH.V5R1M0.CICS.SDFHLOAD").options("shr"))
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(1,1) unit(sysda)  new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(sysprint) )
rc = compile.execute()
println sysprint.text
print "\nv1   RC=" + rc 

