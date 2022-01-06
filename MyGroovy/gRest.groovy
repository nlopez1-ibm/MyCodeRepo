// Util to list DBB WebApp Collection > than a certain age (see cutOff var)
// Use Mode to control report vs real-delete
// Needs Curl from Rocket on USS
// TODO - fix / escape in coll name 


import groovy.json.JsonSlurper
import groovy.time.TimeCategory

def js 		= new JsonSlurper()

//mode ="Reset"
mode ="Report"

// use - 0 to purge all
Date cutOff	= new Date() - 0
webApp      = "--user ADMIN:ADMIN https://192.168.44.1:9443/dbb/rest"
println "*** DBB WebApp $mode Collection(s) where lastUpdated > $cutOff ***"

command = "curl --insecure $webApp/collection/minimal"

// cli ver 
// curl --insecure --user ADMIN:ADMIN https://192.168.88.113:9443/dbb/rest/collection/minimal



apiOut 	= command.execute().text
def collections	= js.parseText(command.execute().text)
collections.each { collection ->
	Date pDate= Date.parse("yyyy-MM-dd", collection.lastUpdated)
        if (pDate < cutOff) {
        	println "** " + collection.name.padRight(55,".") + " lastUpdated=" +collection.lastUpdated.take(10)
        
        	if (mode == "Reset")    {
        		reset	= "curl --insecure  -X DELETE $webApp/collection/"+collection.name.replaceAll(/([^a-zA-Z0-9])/, '\\\\$1' )
        		resetOut= reset.execute().text        					
        	}
        }
}




System.exit(0)



//cDate="2020-02-09T08:23:26.197-04:00"
//Date pDate= Date.parse("yyyy-MM-dd", cDate)

//println "CollDate:" + pDate
//if(pDate < old) println "DELETE Me " + pDate



//r.each { v->
//println "** " + v
// }



//println command.execute().text  + "\n LastCC = $rc"
//def proc = command.execute()
//proc.waitFor()

// Obtain status and output
//println "return code: ${ proc.exitValue()}"
//println "stderr: ${proc.err.text}"
//println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy

//def command = "ls"
