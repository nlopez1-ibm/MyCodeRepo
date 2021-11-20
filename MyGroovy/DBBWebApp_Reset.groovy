@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*
import com.ibm.dbb.build.report.*
import com.ibm.dbb.build.html.*

def client = new RepositoryClient()
//client.setUrl("HTTPS://dbbdev.rtp.raleigh.ibm.com:9443/dbb/")
client.setUrl("HTTPS://9.77.144.69:9443/dbb/")

client.setUserId("ADMIN")
client.setPassword("ADMIN")
client.forceSSLTrusted(true)

collections =['poc-app-Dev-1-test-branch-impact-build','poc-app-nlopez59-master-patch-31657','poc-app-nlopez59-master-patch-05091','Mortgage-App-rel3','Mortgage-App-rel4' ]			
collections.each { collection ->

		println("** Reset option selected for $collection")
			client.deleteCollection(collection)					
			client.deleteBuildResults(collection)			
}
