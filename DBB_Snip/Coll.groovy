// sample code to learn DBB Dep/Coll logic 
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import groovy.json.JsonSlurper
import groovy.transform.*


//Unlike the build result, a dependency collection is a simple repository object containing just a list of logical files. As such there is no dedicated interface for it. All collection APIs are located in the RepositoryClient utility class.
// Create a collection to store scanned dependency data
def collectionName = "Nelson_Test.master"
def client = new RepositoryClient().url("https://dbbdev.rtp.raleigh.ibm.com:9443/dbb/").userId("ADMIN").password("ADMIN") 
client.setForceSSLTrusted(true)
if (!client.collectionExists(collectionName))
   client.createCollection(collectionName) 

// Add or update a logical file to a collection
def sourceDir = "/u/nlopez/DAT-Demo-Workspace"
def file = "Mortgage-SA-DAT/cobol/datmort.cbl"
def logicalFile = new DependencyScanner().scan(file, sourceDir)
client.saveLogicalFile(collectionName, logicalFile)

println "\nLF: $logicalFile"
println "\n" 


//Find all logical files that have a dependency reference that resides in DD library MYLIB
logicalDependency = new LogicalDependency(null, "SYSLIB", null)
lfiles = client.getAllLogicalFiles(collectionName, logicalDependency)
println "\nLF SYSLIB: $lfiles"
println "\n" 

//Find all logical files that have a dependency reference that resides in DD library MYLIB
logicalDependency = new LogicalDependency(null, null, "CALL")
lfiles = client.getAllLogicalFiles(collectionName, logicalDependency)
println "\nLF CALLS: $lfiles"
println "\n" 

// Retrieve a logical file from a collection
//  logicalFile = client.getLogicalFile(collectionName, "MortgageApplication/cobol/epsnbrvl.cbl")

// Delete a logical file from a collection
//  client.deleteLogicalFile(collectionName, "MortgageApplication/cobol/epsnbrvl.cbl")

