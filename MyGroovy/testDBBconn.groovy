
import com.ibm.dbb.build.*
import com.ibm.dbb.build.report.*
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import groovy.time.*
//Create a collection to store scanned dependency data

def client = new RepositoryClient().url("HTTPS://dbbdev.rtp.raleigh.ibm.com:9443/dbb/").userId("ADMIN").password("ADMIN")
client.setForceSSLTrusted(true) 


def collectionName = "LDAP-TEST-NELSON-0664"

if (!client.collectionExists(collectionName)){
   client.createCollection(collectionName,'DBBAdmins','TEAMxyz',0664) // 436 dec - 664 oct
   println "Coll created " + collectionName
}
   
 System.exit(0)
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
//Add or update a logical file to a collection
def sourceDir = "/u/nlopez/DAT-Demo-Workspace/Mortgage-SA-DAT/cobol"
def file = "datmort.cbl"
def logicalFile = new DependencyScanner().scan(file,sourceDir)
client.saveLogicalFile(collectionName, logicalFile)
