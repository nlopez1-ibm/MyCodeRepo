not need 


/* test class fto simulate wesdtfiled jcl  
 * 
 *       //XREFIN   DD  DSN=&&XREFIN,
 *       //             DISP=(NEW,PASS,DELETE),
 *       //             UNIT=SYSDA,
 *       //             SPACE=(TRK,(30,15)),
 *       //             DCB=(BLKSIZE=6020,RECFM=FB)
 *       //SORTIN   DD  DSN=&&XREFIN,
 *       //             DISP=(OLD,PASS),
 *       //             VOL=REF=*.XREFIN
 *       
 *       a solution to reuse a temp dd in a step can be done in rexx-only becuase bpxwdyn return the vol and dsn rtvol...
 *       this is not avialable in non-rex -- java!!!
 *       
 *       this is the example that works on rexx but not java 
 *       ZFile.bpxwdyn("alloc da(temp) fi(in) unit(sysda) cyl space(1,1) lrecl(80) dsorg(PS) recfm(F,B)  new keep rtvol(rv) ") 
 *       https://www.ibm.com/support/knowledgecenter/en/SSLTBW_2.1.0/com.ibm.zos.v2r1.bpxb600/rda.htm
 *       
 *       alternative . use th cat !
 */   

import com.ibm.dbb.build.*
//import com.ibm.jzos.FileFactory
//import com.ibm.jzos.ZFile
//println "System Default code page=" + FileFactory.getDefaultZFileEncoding()

DCB="cyl space(1,1) lrecl(80) dsorg(Ps) recfm(F,B)  new keep "

MVSJob job = new MVSJob()
job.start()

TSOExec exec = new TSOExec()
exec.setCommand("ex 'NLOPEZ.DAT.EXEC(write)'")
exec.confDir('/u/nlopez/IBM/dbb/conf')					// points to the runIspf.sh interface 
exec.logFile(new File('/u/nlopez/tmp/TSOExec.log'))  	// has log fo ispf gateway interface	
exec.keepCommandScript(true)  							// does not delete the file below. Goo for tracing errors
exec.addDDStatement("CMDSCP", "NLOPEZ.ISPFGWY.EXEC", "RECFM(F,B) LRECL(80) TRACKS SPACE(1,1) DSORG(PS)", false);

	//exec.addDDStatement("IN",  "NLOPEZ.DAT.COBOL(CMINC)", "SHR", false);
	exec.dd(new DDStatement().name("fileio").dsn('nlopez.xrefin').options("cyl space(1,5) unit(vio) blksize(80) lrecl(80) recfm(f,b) new catalog") ) 

int rc = exec.execute()


job.stop()
job.start()
/*
def gener = new MVSExec().pgm("IEBGENER")
gener.dd(new DDStatement().name("SYSUT2").dsn("&&TEMP").options("cyl space(1,5) unit(vio) blksize(80) lrecl(80) recfm(f,b) new").pass(true) )
gener.dd(new DDStatement().name("SYSPRINT").options('DUMMY') )
gener.dd(new DDStatement().name("SYSIN").options('DUMMY') )

def records = '''RECORD1
RECORD2
RECORD3'''
new DDStatement().name("SYSIN").instreamData(records)
gener.execute() 


// uncomment these liens to avoid the x410 ddname unavailable
job.stop()
job.start()
*/