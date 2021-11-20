/* DBB Groovy Class to demo how to convert a file with Rexx using DBB's TSOExec  api (NLOPEZ)   
 *
 *  runs a rexx exec to convert cobol code with changeman "-INC  xxx" in col 1 to normal cobol "COPY xxx" in col 8
 *  uses 2 pds's and a rexx exec on MVS
 *  PDS DD=in  source with potential -INC
 *  PDS DD=out converted members PDS (prealloacted on 3.2) 
 *  RC=0 nothing found 
 *  RC=2 conversions made and output to DD=out       
 */   
import com.ibm.dbb.build.* 

TSOExec exec = new TSOExec()
	exec.setCommand("ex 'NLOPEZ.DAT.EXEC(CMMIG)'")
	exec.confDir('/u/nlopez/IBM/dbb/conf')					// points to the runIspf.sh interface 
	
	exec.logFile(new File('/u/nlopez/tmp/TSOExec.log'))  	// has log fo ispf gateway interface	
	exec.keepCommandScript(true)  							// does not delete the file below. Goo for tracing errors
    exec.addDDStatement("CMDSCP", "NLOPEZ.ISPFGWY.EXEC", "RECFM(F,B) LRECL(80) TRACKS SPACE(1,1) DSORG(PS)", false);

 	exec.addDDStatement("IN",  "NLOPEZ.DAT.COBOL(CMINC)", "SHR", false);
 	exec.addDDStatement("OUT", "NLOPEZ.DAT.COBOL2(CMINC)", "SHR", false);
 
	int rc = exec.execute()
			
if (rc == 2) println "**! Member Converted. See OUT DD PDS"
	
println "RC = $rc"
System.exit(rc)
	 
 
 
/* Sample rexx code to be copied to a pds om MVS and used above
*   need to all the enclosing slash asterisk to the comments and remove the leading asterisks
*   
*      
* REXX TO MIGRATE CM CBL FILE WITH -INC  (NLOPEZ) `                
* SAVE; TSO EX 'NLOPEZ.DAT.EXEC(CMMIG)'                            
* look for "-INC" in col 1 and convert to standard cobol copy stmt 
* COPY must start in margin A (col 8)                              
* See groovy script to dd allocs                                   
*                                                                    
*	say "dbb post migration processor "                                 
*	"execio * diskr in (stem l.)"                                       
*	newFile.0= null                                                     
*	nfx = 1                                                             
*                                                                    
*	rewrite=no                                                          
*	do x = 1 to l.0                                                     
*    	if left(l.x,4) = '-INC' then do                                 
*       		parse var l.x . copymem   .                                   
*       		new = "       COPY " copymem"."                                 
*       		newFile.nfx = new                                            
*       		nfx = nfx + 1                                                
*       		new = "      * Converted old text in col 1="l.x              
*       		newFile.nfx = new                                            
*       		nfx = nfx + 1                                                
*        rewrite = yes                               
*       end                                         
*    else do                                        
*       newFile.nfx = l.x                           
*       nfx = nfx+1                                 
*       end                                         
*	end                                                
*	"execio 0 diskr in (FINIS"                         
*                                                   
* if rewrite=yes then do                             
*	say "Rewriting to cmfixed"                      
*    	"execio * diskw out (finis stem newFile.)"      
*    	exit 2                                          
* end                                                
* exit 0                                             
*/
 