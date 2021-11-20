import com.ibm.dbb.build.*
//* NJL - sample - build a pgm  

sysin="NLOPEZ.DAT.COBOL(SAMPCOB)"      // bad Run - Copy MEMBER NOT FOUND
//sysin="NLOPEZ.DAT.COBOL(SAMPCOB2)"   // Good Run - no copy stmt   

sysprint= new File("/u/nlopez/tmp/CobBuild.sysprint")
sysout= new File("/u/nlopez/tmp/CobBuild.sysout")
parms='CICS,MDECK(NOC),ADATA,EXIT(ADEXIT(ELAXMGUX),XOPTS(DLI)'

def compile = new MVSExec().pgm("IGYCRCTL").parm(parms)
compile.dd(new DDStatement().name("SYSIN").dsn(sysin).options("shr"))

// a copy stmt in the code needs a syslib or the compiler fails with no sysprint/sysout 
compile.dd(new DDStatement().name("SYSLIB").dsn("NLOPEZ.DAT.COBOL").options("shr"))
compile.dd(new DDStatement().name("SYSLIN").options("DUMMY"))
compile.dd(new DDStatement().name("WSEDSF1").options("DUMMY"))
compile.dd(new DDStatement().name("SYSXMLSD").dsn("NLOPEZ.SYSXMLSD.XML").options("shr"))
 

(1..17).toList().each { num -> compile.dd(new DDStatement().name("SYSUT$num").options("cyl space(1,5) unit(vio) new"))}

compile.dd(new DDStatement().name("SYSMDECK").dsn("NLOPEZ.DAT.COBOL(SAMPDECK").options("shr"))
compile.dd(new DDStatement().name("SYSADATA").options("cyl space(1,1) unit(sysda)  new"))

compile.dd(new DDStatement().name("TASKLIB").dsn("IGY.V6R1M0.SIGYCOMP").options("shr"))
compile.dd(new DDStatement().dsn("DFH.V5R1M0.CICS.SDFHLOAD").options("shr"))
compile.dd(new DDStatement().dsn("DSN.V11R1M0.SDSNLOAD").options("shr"))
compile.dd(new DDStatement().dsn("FEL.V14R1M2.SFELLOAD").options("shr"))

compile.dd(new DDStatement().name("SYSOUT").options("cyl space(1,1) unit(sysda)  new"))
compile.copy(new CopyToHFS().ddName("SYSOUT").file(sysout) )
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(1,1) unit(sysda)  new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(sysprint) )
rc = compile.execute()
println sysprint.text
println sysout.text
print "RC=" + rc 

