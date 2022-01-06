@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*
import com.ibm.dbb.build.report.*
import com.ibm.dbb.build.html.*
// groovyz /u/nlopez/MyCodeRepo/MyGroovy/DBBWebApp_Reset.groovy


def client = new RepositoryClient()
//client.setUrl("HTTPS://dbbdev.rtp.raleigh.ibm.com:9443/dbb/")
//
client.setUrl("https://192.168.88.113:9443/dbb/")

//curl --insecure  --user ADMIN:ADMIN https://192.168.88.113:9443

//client.setUrl("http://192.168.44.1:8181/dbb/")
    

client.setUserId("ADMIN")
client.setPassword("ADMIN")

//client.setSslProtocol("TLSv1")


client.forceSSLTrusted(true)
//client.forceSSLTrusted(false)

//collections =['poc-app-Dev-1-test-branch-impact-build','poc-app-nlopez59-master-patch-31657','poc-app-nlopez59-master-patch-05091','Mortgage-App-rel3','Mortgage-App-rel4' ]			
collections =['poc-app-master' ]
collections.each { collection ->

		println("** Reset option selected for $collection")
			client.deleteCollection(collection)					
			client.deleteBuildResults(collection)			
}
