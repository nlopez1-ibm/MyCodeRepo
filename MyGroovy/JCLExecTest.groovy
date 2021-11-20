/* Class to test JCL Exec   
 * Notes: ./conf is needed to point to the jobstatus rexx exec
 * the API can use the envin var confDir instead of setting it as shown below 
 * that short exec rusn the tso status cmd.  My guess its waiting for the job to end
 * to then pickup the dd's for processing
 * The API calls teh jzos api MvsJobSubmitter which uses the jes internal reader for 
 * job submission (pretty cool). 
 *
 * see jzos https://www.ibm.com/support/knowledgecenter/SSYKE2_8.0.0/com.ibm.java.zsecurity.api.80.doc/com.ibm.jzos/com/ibm/jzos/MvsJobSubmitter.html 
 * see dbb api  https://github.ibm.com/IBMDBB/DBB-Toolkit/blob/development/com.ibm.dbb.toolkit/src/com/ibm/dbb/build/JCLExec.java
 *   
 */   
import com.ibm.dbb.build.*

mem='BR14'
pds='NLOPEZ.DAT.JCL'
 
boolean noasa = 1


println " has a debug in getAll..."


JCLExec exec = new JCLExec()
exec.confDir('/u/nlopez/IBM/dbb/conf')
def rc = exec.dataset(pds).member(mem).execute()
println "Job '${exec.getSubmittedJobId()}' was submitted successfully with maxRC = ${exec.maxRC}"
exec.getAllDDNames().each({ ddName ->
    println "DD Name: $ddName"
    def file = new File("/u/nlopez/jclexec/${ddName}.output")
    // exec.saveOutput(ddName, file, null)    
    		exec.saveOutput(ddName, file, "true"  as boolean)
})
	