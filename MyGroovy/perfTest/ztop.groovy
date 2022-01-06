orig moved to home nov 2021 


// USSMON: NLopez 2020 
// Desc:  TOP for USS  OMVS resources. Output in csv format.                 
// Arg:   Jobname filter  ie, nlop* for all jobs starting nlop

/* Notes: This sample shows how you could use the ZOAU and ISF Java Api's to 
 *        access SDSF for Job Info and OPERCMDS like DISPLAY OMVS,L
 *        For details on ZOAU 
 *        				User guide: https://www.ibm.com/support/knowledgecenter/SSKFYE_1.0.1/welcome_zoautil.html
 *        				Javadoc - https://www.ibm.com/support/knowledgecenter/SSKFYE_1.0.1/javadoc_zoautil/index.html?view=embed
 *        For ISF: 
 *        				User guide: https://www.ibm.com/support/knowledgecenter/SSLTBW_2.4.0/com.ibm.zos.v2r4.isfa600/javapl.htm
 */

// zoau api
import com.ibm.zoautil.*
import com.ibm.zoautil.types.*

// isf api (SDSF)
import com.ibm.zos.sdsf.core.ISFException
import com.ibm.zos.sdsf.core.ISFRequestSettings
import com.ibm.zos.sdsf.core.ISFBaseRunner
import com.ibm.zos.sdsf.core.ISFActiveRunner	// DA panel runner - all Jobs and processes
import com.ibm.zos.sdsf.core.ISFProcessRunner 	// PS panel runner - OMVS processes

// 
arg = args.length ? args[0] : System.getenv("USER")+ "*"
sysWideStuff = SDSF_DA_JOBS(arg)
OperCmd_Display_Syslog("display", "omvs,l",sysWideStuff)

System.exit(0)

//*****
//* use COLSHELP IN SDSF DA for list of columns names used in filter and getters
def SDSF_DA_JOBS(String arg) {	
	if (arg.length() == 1 | arg.length() > 8 ) arg = "*"   	//suppress glob of * as input for all jobs 
	
	DA_settings = new ISFRequestSettings()
	DA_settings.addNoModify()									// performance trick
	DA_settings.addISFLineLim(100)
	DA_runner = new ISFActiveRunner(DA_settings)				// SDSF "DA" API
	
	PS_settings = new ISFRequestSettings()
	PS_settings.addNoModify()									// performance trick 
	PS_settings.addISFLineLim(9000)
	
	//* stdout header csv format 
	 println "JNAME,"+
			 "Start-Date," +
			 "Start-Time," +
			 "PID," +
			 "ASIDX," +
			 "CPU-Exec-Time," +
			 "State," +
			 "SH_CMD,"+
			 "OWNER," +
			 "cpu%," +
			 "real," +
			 "excp," +
			 "Active-Files"
			 
	 sysCPU 	= "NA"
	 sysName 	= "NA"
		 
	// get all OMVS processes by jobname passed in arg 
	PS_settings.addISFFilter("jname eq $arg")   				// Filter requested job
	PS_runner = new ISFProcessRunner(PS_settings)				// SDSF "PS" API -aka Runner
	def PS  = PS_runner.exec()

	PS.each { ps ->
		ps_JNAME	= ps.getValue("jname")
		ps_stDate 	= ps.getValue("datee")
		ps_stTime 	= ps.getValue("timee")
		ps_actFiles = ps.getValue("actfiles")
		ps_command 	= ps.getValue("command")
		ps_PID  	= ps.getValue("pid")
		ps_ASIDX	= ps.getValue("asidx")
		ps_CPU		= ps.getValue("cpu")
		ps_State	= ps.getValue("state")							// multple coded value - an R means it running 
		ps_OWNERID	= ps.getValue("ownerid")
		
		// if the process is an ASIDX,  lookup the DA status  
		if (ps_ASIDX) {	
			DA_settings.addISFFilter("ASIDX eq $ps_ASIDX") 
			def da = DA_runner.exec()								// DA Runner
					 
			if (da.size() == 1) {
				sysCPU	= da[0].getValue("scpu")					// system wide CPU Util
				da_real = da[0].getValue("real")
				da_excp = da[0].getValue("excp")
				da_cpu 	= da[0].getValue("cpupr")
				sysName = da[0].getValue("sysname")
			}
			
			// stdout is in csv format 
			println ps_JNAME 	+"," +
					ps_stDate	+"," +
					ps_stTime	+"," +	
					ps_PID  	+"," +
					ps_ASIDX	+"," +
					ps_CPU		+"," +
					ps_State 	+"," +
					ps_command  +"," +
					ps_OWNERID	+"," +
					da_cpu		+"," + 
					da_real 	+"," + 
					da_excp		+"," +
					ps_actFiles
		}
	}
	
	return "${sysCPU},${sysName}"								// return the last occurance of system level metrics 
}

//*****************************************************
//* ZOAU OPERCMD to Get Real-Time OMVS resource util   
def OperCmd_Display_Syslog(String cmd, String opts, String sysStuff) {
	proc  = OperatorCmd.execute(cmd,opts)						// OperatorCmd see ZOAU doc
	 
	if  (!proc.getReturnCode()) {
		sysout=proc.getOutput().split("\n")				
		sysout.each {   
			values=it.split(" +")
			if (it.indexOf('MAXPROCSYS') > 0)  	MAXPROCSYS = (values[2].toInteger() / values[4].toInteger() *100) 
			if (it.indexOf('MAXUIDS') > 0)  	MAXUIDS = (values[2].toInteger() / values[4].toInteger() *100)
		} 
	}
	
	println "@MaxProcSys_Util%,MaxUIDS_UTIL%,System_CPU_UTIL%,SysName "
	println MAXPROCSYS +"," + MAXUIDS +"," + sysStuff
}