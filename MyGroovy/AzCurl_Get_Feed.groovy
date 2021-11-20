// test an azure rest api to get feed for UCD with Rocket curl
// api str has my pat for testing 
// see https://docs.microsoft.com/en-us/rest/api/azure/devops/artifacts/artifact%20%20details/get%20package%20version?view=azure-devops-rest-6.0
//import groovy.json.JsonOutput.*

import groovy.json.* 
api = "curl --insecure -u Nelson.Lopez1@ibm.com:tgeauojcsuaor6mn7yxoipw3rz532dev2a534sedyrivyxxqwaxa --insecure   -X GET https://feeds.dev.azure.com/Azure-Repo-DBB/DBB-Azure-Release/_apis/packaging/Feeds/test_feed/packages?api-version=6.0-preview.1"
def result =  api.execute().toJson()
println new JsonBuilder(result).toPrettyString()  

System.exit(0)

def pretty = JsonOutput.prettyPrint(result)
println (pretty)
System.exit(0)

//def jso = new JsonOuputr()
def results= JsonOutput.toJson(api.execute().text)
println JsonOutput.prettyPrint(results)  
System.exit(0)
/*
println "Feed Name=${apiout.value.name}"  
println apiout.value.url     
println apiout.prettyPrint() //.stripIndent()
System.exit(0)
*/





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


// list feeds
api1         = "curl --insecure -u Nelson.Lopez1@ibm.com:tgeauojcsuaor6mn7yxoipw3rz532dev2a534sedyrivyxxqwaxa --insecure   -X GET https://feeds.dev.azure.com/Azure-Repo-DBB/DBB-Azure-Release/_apis/packaging/feeds?api-version=6.0-preview.1"




// orig >curl -u Nelson.Lopez1@ibm.com:tgeauojcsuaor6mn7yxoipw3rz532dev2a534sedyrivyxxqwaxa --insecure   -X GET https://feeds.dev.azure.com/Azure-Repo-DBB/DBB-Azure-Release/_apis/packaging/feeds?api-version=6.0-preview.1


//println apiout["value"."name"]
//#packages= js.parseText(api.execute().text)

//println new JsonBuilder(apiOut).toPrettyString()

 
