// Utility to list all collections with a give logical file (NJL - 2021) 
// dependencies: Rocket Curl, DBB Server  
// Run as:  groovy impactScan.groovy someLogicalFileName 

lname = this.args[0]
//lname ="DATNBRPM"
		
webApp  = "--user ADMIN:ADMIN https://9.65.242.173:9443/dbb/rest"
command = "curl --insecure $webApp/collection/"

apiOut 	= command.execute().text 
def collections	= new XmlSlurper().parseText(apiOut)
found=false 

println "** scanning all collections with a logical file named-> $lname <-"
collections.collection.each { collection ->
  	collection.logicalFiles.logicalFile.each { lfile -> 
  	if (lfile.lname == lname) { 
  			println "* $collection.name"
  			found=true
  	}
  }
}	
if (!found) { println "file not found in any collection" } 
System.exit(0)