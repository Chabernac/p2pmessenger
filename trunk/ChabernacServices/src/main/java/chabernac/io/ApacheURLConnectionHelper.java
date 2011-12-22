package chabernac.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

public class ApacheURLConnectionHelper extends AbstractURLConnectionHelper {
  private HttpPost myPost = null;
  private HttpGet myGet = null;
  private DefaultHttpClient myClient = null;
  private List<NameValuePair> myParams = null;
  private HttpResponse myResponse = null;
  private BufferedReader myReader = null;
  private static HttpHost myProxy = null;

  static{
    detectProxy();
  }

  public ApacheURLConnectionHelper(URL anUrL, boolean isResolveURL) {
    super(anUrL, isResolveURL);
  }

  private static void detectProxy(){
    String theProxy = "iproxy.axa.be";
    try{
      InetAddress.getByName(theProxy);
      myProxy = new HttpHost(theProxy,8080);
      LOGGER.debug("Proxy found '" + myProxy.toHostString()  + "'");
    }catch(Exception e){
      LOGGER.error("Unable to resolve proxy url '"  + theProxy  + "' ");
    }
  }

  @Override
  public void scheduleClose(int aTimeout, TimeUnit aTimeUnit) {
    // TODO Auto-generated method stub

  }

  @Override
  public void connect(boolean isDoInput, boolean isDoOutput) throws IOException {
    HttpParams theHttpParams = new BasicHttpParams();

    if(myProxy != null) {
      theHttpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, myProxy);
      LOGGER.debug("Using proxy '" + myProxy + "'");
    }
    theHttpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    myClient = new DefaultHttpClient(theHttpParams);

    if(myProxy != null && System.getProperty( "user.name" ).equalsIgnoreCase("dgch804")){
      Credentials theCredentials = new NTCredentials("dgch804", "8411D02k", "X22P0212", "AXA-BE");
//      Credentials theCredentials = new NTCredentials("NTLM TlRMTVNTUAADAAAAGAAYAHIAAAAYABgAigAAAAwADABIAAAADgAOAFQAAAAQABAAYgAAAAAAAACiAAAABYKIogUBKAoAAAAPQQBYAEEALQBCAEUARABHAEMASAA4ADAANABYADIAMgBQADAAMgAxADIAVOhpvoDAX+sAAAAAAAAAAAAAAAAAAAAALa/tdDv3Lposo8Y5B0rM7v9ePxFjLMTG");
      LOGGER.debug("Setting proxy authentication");
      myClient.getCredentialsProvider().setCredentials(AuthScope.ANY, theCredentials);


//      List<String> authtypes = new ArrayList<String>();
//      authtypes.add(AuthPolicy.NTLM);
//      authtypes.add(AuthPolicy.BASIC);
//      authtypes.add(AuthPolicy.DIGEST);
//      myClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF,
//              authtypes);
//      myClient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF,
//              authtypes);
    }


    try {
      if(isDoInput){
        myPost = new HttpPost(myURL.toURI());
      } else {
        myGet = new HttpGet( myURL.toURI());
      }
    } catch (URISyntaxException e) {
      throw new IOException("Could not create post or get", e);
    }
  }

  @Override
  public void endInput() throws IOException{
    if(myParams != null && myPost != null){
      myPost.setEntity(new UrlEncodedFormEntity(myParams, "UTF-8"));
    }
  }

  private void execute() throws ClientProtocolException, IOException{
    if(myResponse == null){
      if(myPost != null) myResponse = myClient.execute(myPost);
      else if(myGet != null) myResponse = myClient.execute(myGet);
    }
  }

  @Override
  public String readLine() throws IOException {
    execute();
    if(myReader == null){
      myReader = new BufferedReader(new InputStreamReader(myResponse.getEntity().getContent()));
    }
    return myReader.readLine();
  }

  @Override
  public void write(String aKey, String aValue) throws IOException {
    if(myParams == null) myParams = new ArrayList<NameValuePair>();
    myParams.add(new BasicNameValuePair(aKey, aValue));
  }

  @Override
  public void close() {
  }
}
