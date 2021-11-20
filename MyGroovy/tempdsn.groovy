//testing temp datase passing and reuse 
import com.ibm.dbb.build.*

//tempOptions=cyl space(1,5) unit(vio) blksize(80) lrecl(80) recfm(f,b) new

MVSJob job = new MVSJob()
job.start()

def gener = new MVSExec().pgm("IEBGENER")
	gener.dd(new DDStatement().name("SYSUT2").dsn("NLOPEZ.TESTP").options("cyl space(1,5) unit(sysda) blksize(80) lrecl(80) recfm(f,b) new").pass(true) )
	//gener.dd(new DDStatement().name("SYSPRINT").options('DUMMY') )
	gener.dd(new DDStatement().name("SYSPRINT").options('SYSOUT(H)   DIAG(STDOUT)') )  //see sdsf O
	gener.dd(new DDStatement().name("SYSIN").options('DUMMY') )

	def records = '''RECORD1
	RECORD2
	RECORD3'''
	new DDStatement().name("SYSUT1").instreamData(records)
	gener.execute() 

job.stop()
job.start()
System.exit(0)

/*

def gener2 = new MVSExec().pgm("IEBGENER")
gener2.dd(new DDStatement().name("SYSUT2").dsn("&&TEMP2").options("cyl space(1,5) unit(vio) blksize(80) lrecl(80) recfm(f,b) new").pass(true) )
gener2.dd(new DDStatement().name("SYSPRINT").options('DUMMY') )
gener2.dd(new DDStatement().name("SYSIN").options('DUMMY') )

new DDStatement().name("SYSIN").instreamData(records) 
gener.execute()
job.stop()

*/