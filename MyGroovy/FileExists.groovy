import com.ibm.dbb.build.*


myDsn  = "NLOPEZ.DAT.JCL"
myDsn  = "NLOPEZ.BOA.POC.U4652913.SYSXMLSD.XML???" 
//myDsn  = "NLOPEZ.DAT.JCL(ASM)"

if ( fileExists(myDsn) ) 
		println " Found $myDsn"
else 
		println " File Not found or other allocation error (enq?)"

		
System.exit(0)


//* My utils 
def fileExists( dsn ) {
	// rc=0 means file not found.   rc>0 means file found
	def br14 = new MVSExec().pgm("IEFBR14")
    br14.dd(new DDStatement().name("DUMMY").dsn(dsn).options("shr"))
	
	//rc = br14.execute()
	try {
		rc = br14.execute()
		rc = 1
	}
	catch(ex) { 
		rc = 0 
		println 'Opps' + ex.message 
		println 'Opps' + ex.stackTrace.toString().replaceAll(",","\n")   
		ex.printStackTrace() 
	}
	
	return rc  
} 

