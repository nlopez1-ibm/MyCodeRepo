import com.ibm.dbb.build.*
import groovy.json.* 
//* NJL - sample - build a C pgm  

sysin="NLOPEZ.DAT.COBOL(CPGM)"   
obj="NLOPEZ.DAT.OBJ(CPGM)"     
sysout_dcb="cyl space(5,5) unit(vio) blksize(133) lrecl(133) recfm(f,b) new   "

sysprint= new File("/u/nlopez/tmp/CBuild.sysprint")
sysout  = new File("/u/nlopez/tmp/CobBuild.sysout")
syscprt = new File("/u/nlopez/tmp/CobBuild.syscprt")


println("Compiling . . .")
def compile = new MVSExec().pgm("CCNDRVR").parm('list,source') 
//def compile = new MVSExec().pgm("CCNDRVR")
compile.dd(new DDStatement().name("SYSIN").dsn(sysin).options("shr"))

//compile.dd(new DDStatement().name("SYSLIN").dsn("C151808.CPS.OBJ(HELLOC)").options("shr"))
compile.dd(new DDStatement().name("SYSLIN").dsn(obj).options("shr"))

compile.dd(new DDStatement().name("SYSUT5").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT6").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT7").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT8").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT9").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT10").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT14").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT16").options("cyl space(5,5) unit(sysda)"))
compile.dd(new DDStatement().name("SYSUT17").options("cyl space(5,5) unit(sysda)"))

// compile.dd(new DDStatement().name("TASKLIB").dsn("CEE.SCEERUN2").options("shr"))
//compile.dd(new DDStatement().dsn("CBC.SCCNCMP").options("shr"))
//compile.dd(new DDStatement().dsn("CEE.SCEERUN").options("shr"))


compile.dd(new DDStatement().name("SYSPRINT").options(sysout_dcb)) 
compile.dd(new DDStatement().name("SYSOUT").options(sysout_dcb))
compile.dd(new DDStatement().name("SYSCPRT").options(sysout_dcb))

 
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(sysprint)  )
compile.copy(new CopyToHFS().ddName("SYSCPRT").file(syscprt)  )
compile.copy(new CopyToHFS().ddName("SYSOUT").file(sysout)  )

//compile.copy(new CopyToHFS().ddName("SYSOUT").file(new File("/u/C151808/CPSBilling/work/helloworldcso.log")))
//compile.copy(new CopyToHFS().ddName("SYSCPRT").file(new File("/u/C151808/CPSBilling/work/helloworldcscp.log")))
def rc = compile.execute()

println("Compile  RC=$rc")
    
// rc = compile.execute()
println "\n sysprint:---------------------------------------------------"
println sysprint.text
println "\n syscprt:---------------------------------------------------"
println syscprt.text
println "\n sysout:---------------------------------------------------"
println sysout.text
println "\n\n END SYSOUT***"
println "\n\n***Start Diag MVSExec object dump***"
println new JsonBuilder(compile).toPrettyString()


