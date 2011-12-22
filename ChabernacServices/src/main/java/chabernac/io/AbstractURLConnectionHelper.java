package chabernac.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public abstract class AbstractURLConnectionHelper {
  protected static Logger LOGGER = Logger.getLogger(AbstractURLConnectionHelper.class);
  protected final URL myURL;
  
  public AbstractURLConnectionHelper(URL anUrL, boolean isResolveURL){
    if(isResolveURL && !isLocal(anUrL)){
      try{
        InetAddress theInetAddress = InetAddress.getByName(anUrL.getHost());
        anUrL = new URL(new URL("http://" + theInetAddress.getHostAddress()), anUrL.getPath());
      }catch(Exception e){
        LOGGER.error("Unable to resolve url '"  + anUrL  + "' using url as it is");
      }
    }
    myURL = anUrL;
  }
  
  public URL getURL() {
    return myURL;
  }

  private static boolean isLocal(URL aURL){
    if(aURL.getHost().toLowerCase().contains("localhost")) return true;
    return false;
  }

  public void connectInputOutput() throws IOException{
    connect( true, true );
  }

  public void connectOutput() throws IOException{
    connect( false, true );
  }

  public abstract void scheduleClose(int aTimeout, TimeUnit aTimeUnit);
  
  public abstract void connect(boolean isDoInput, boolean isDoOutput) throws IOException;
  
  public abstract void endInput() throws IOException;
  
  public abstract String readLine() throws IOException;

  public abstract void write(String aKey, String aValue) throws IOException;
  
  public abstract void close();
}
