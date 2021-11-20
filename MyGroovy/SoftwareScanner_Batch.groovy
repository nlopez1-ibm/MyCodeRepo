/* Class to scan cobol using adata and cob2  
 * NJL - initial draft 
 */   

/*
import groovy.transform.Field
import com.ibm.dbb.build.*
import com.ibm.dbb.dependency.*
import groovy.json.JsonSlurper
def boolean v = true		// Verbose 
*/ 

import com.ibm.dbb.build.*

/* cant use ciob2 not isntalled on tvt6031!! - use igy
 * 
 */

def subs 	 	= [:]			
/* data struct
 *   subs = [ d??? yn:[], main:[stat],   stat:[stat2] ]
 *   COMPILE EXEC IGYWC,
 *   
 */
		
// PARM.COBOL='LIST,MAP,RENT,FLAG(I,I),XREF,ADATA'
//SYSPRINT DD PATH='/u/userid/cobol/demo.lst',    (1)
// PATHOPTS=(OWRONLY,OCREAT,OTRUNC),              (2)
// PATHMODE=SIRWXU,                               (3)
// FILEDATA=TEXT                                  (4)
//SYSLIN DD PATH='/u/userid/cobol/demo.o',
// PATHOPTS=(OWRONLY,OCREAT,OTRUNC),
// PATHMODE=SIRWXU
//SYSADATA DD PATH='/u/userid/cobol/demo.adt',
// PATHOPTS=(OWRONLY,OCREAT,OTRUNC),
// PATHMODE=SIRWXU
//SYSIN DD PATH='/u/userid/cobol/demo.cbl',
// PATHOPTS=ORDONLY,
// FILEDATA=TEXT,
// RECFM=F
 
// compile.dd(new DDStatement().name("SYSLIB").path("/u/nlopez/tmp/bid-assist/pli").options("PATHOPTS(ORDONLY)"))
def compile = new MVSExec().pgm("IGYCRCTL").parm("ADATA,NOOBJ,NODECK")
compile.dd(new DDStatement().name("SYSIN").path("/u/nlopez/DAT-Demo-Workspace/Mortgage-SA-DAT/cobol/datmort.cbl").options("PATHOPTS(ORDONLY) FILEDATA(TEXT) RECFM(F)") )
//compile.dd(new DDStatement().name("SYSPRINT").path("/u/nlopez/tmp/scan.sysprint").options("PATHOPTS(OWRONLY,OCREAT			,OTRUNC) PATHMODE(SIRWXU)") ) 
compile.dd(new DDStatement().name("SYSPRINT").path("/u/nlopez/tmp/scan.sysprint").options("PATHOPTS(OWRONLY,OCREAT,OTRUNC) FILEDATA(TEXT)") )
compile.dd(new DDStatement().name("SYSADATA").path("/u/nlopez/tmp/scan.sysadata").options("PATHOPTS(OWRONLY,OCREAT,OTRUNC) FILEDATA(TEXT)") )
(1..17).toList().each { num ->
compile.dd(new DDStatement().name("SYSUT$num").options("cyl space(5,5) unit(vio) blksize(80) lrecl(80) recfm(f		,b) new"))
}
//compile.dd(new DDStatement().name("TASKLIB").dsn("IGY.V6R1M0.SIGYCOMP").options("shr"))
compile.dd(new DDStatement().name("SYSMDECK").options("DUMMY")) 		
def rc = compile.execute()

if (rc > 4)
    println("Compile failed!  RC=$rc")
else
    println("Compile successful!  RC=$rc")
		