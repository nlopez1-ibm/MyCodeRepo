under dev  


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import sun.misc.BASE64Encoder;
/**
* default imports io, lang, net,util  
*  
*/

Map paramNameToValue = new HashMap(); // parameter name to value map
String URL_BASE = "https://"
String method = "POST"
String userName = "ADMIN"
String password = "ADMIN"
String authentication = userName + ':' + password
String host = "dbbdev.192.168.44.1:9443/dbb/rest/logicalFile"
String port = "9443";
final String HTTP_MODE_POST = "POST";


String xmlFile = "CreateNewProject.xml";
String command = "create";

// construct URL
StringBuffer params = new StringBuffer();
if (paramNameToValue.keySet().size() > 0) {
	boolean isFirstParam = true;
	for (Iterator paramIter = paramNameToValue.keySet().iterator();paramIter.hasNext();) {
			String paramStr = (String)paramIter.next();
			if (isFirstParam) {
				params.append("?" + paramStr);
				isFirstParam = false;
			} else {
				params.append("&" + paramStr);
			}
			params.append("=" +
				URLEncoder.encode((String)paramNameToValue.get(paramStr),"UTF-8"));
	}
}

URL url = null;
if (method.equals(HTTP_MODE_POST))
url = new URL(URL_BASE + host + ':' + port + "/InformationAnalyzer/" + command);
else
url = new URL(URL_BASE + host + ':' + port +
"/InformationAnalyzer/" + command + params.toString());
// open HTTPS connection
HttpURLConnection connection = null;
connection = (HttpsURLConnection)url.openConnection();
((HttpsURLConnection) connection).setHostnameVerifier(new MyHostnameVerifier());
connection.setRequestProperty("Content-Type", "text/plain; charset=\"utf8\"");
connection.setRequestMethod(method);
BASE64Encoder encoder = new BASE64Encoder();
String encoded = encoder.encode((authentication).getBytes("UTF-8"));
connection.setRequestProperty("Authorization", "Basic " + encoded);
// insert XML file
if (xmlFile != null)
{
connection.setDoOutput(true);
OutputStream out = connection.getOutputStream();
FileInputStream fileIn = new FileInputStream(xmlFile);
byte[] buffer = new byte[1024];
int nbRead;
do
{
nbRead = fileIn.read(buffer);
if (nbRead>0) {
out.write(buffer, 0, nbRead);
}
} while (nbRead>=0);
out.close();
}
// execute HTTPS request
int returnCode = connection.getResponseCode();
InputStream connectionIn = null;
if (returnCode==200)
connectionIn = connection.getInputStream();
else
connectionIn = connection.getErrorStream();
// print resulting stream
BufferedReader buffer = new BufferedReader(new InputStreamReader(connectionIn));
String inputLine;
while ((inputLine = buffer.readLine()) != null)
System.out.println(inputLine);
buffer.close();
}
}
