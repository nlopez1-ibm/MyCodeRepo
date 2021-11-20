/* sample script to invoke BR14  (Nlopez)
 * Add this to your home dir and cal lit br14.groovy 
 * then run it   "groovyz  br14.groovy" 
 * then for extra credit uncommnet the dd line and rerun 
 * and find the bug
 */

import com.ibm.dbb.build.*
pdsDCB="cyl space(1,1) lrecl(80) dsorg(PO) refm(F,B) dsntype(library) new catalog"    


def runPgm = new MVSExec().pgm("IEFBR14")
runPgm.dd(new DDStatement().name("MYDD").dsn("NLOPEZ.BR14.TEST").options(pdsDCB))

println("\n\n** Calling IEFBR14")
int rc = runPgm.execute()
println "RC: $rc \n\n"	

