/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class URLConnectionHelper extends AbstractURLConnectionHelper{
  private static Logger LOGGER = Logger.getLogger( URLConnectionHelper.class );


  private BufferedReader myInputStream = null;
  private OutputStreamWriter myOutputStream = null;
  private URLConnection myConnection = null;
  private boolean isFirstKey = true;

  private ScheduledExecutorService myClosingService = null;

  public URLConnectionHelper( String aURL ) throws MalformedURLException {
    super(new URL(aURL), false);
  }

  public URLConnectionHelper( URL aURL) throws MalformedURLException {
    super(aURL, false);
  }

  public URLConnectionHelper( URL aBaseURL, boolean isResolveURL) throws MalformedURLException {
    super(aBaseURL, isResolveURL);
  }
  
  public void scheduleClose(int aTimeout, TimeUnit aTimeUnit){
    if(myClosingService != null){
      myClosingService.shutdownNow();
    }
    myClosingService = Executors.newScheduledThreadPool( 1 );
    myClosingService.schedule( new CloseCommand(), aTimeout, aTimeUnit );
  }

  public void connect(boolean isDoInput, boolean isDoOutput) throws IOException{
    myConnection = myURL.openConnection();
    myConnection.setDoInput( isDoInput );
    myConnection.setDoOutput( isDoOutput );
  }
  
  public void endInput(){
    if(myOutputStream != null) {
      try {
        myOutputStream.flush();
        myOutputStream.close();
      } catch (IOException e) {
        LOGGER.error("Could not close input stream", e);
      }
    }
  }

  private BufferedReader getReader() throws IOException{
    if(myInputStream == null){
      myInputStream = new BufferedReader( new InputStreamReader( myConnection.getInputStream() ));
    }
    return myInputStream;
  }

  private OutputStreamWriter getWriter() throws IOException{
    if(myOutputStream == null){
      myOutputStream = new OutputStreamWriter( myConnection.getOutputStream() );
    }
    return myOutputStream;
  }

  public String readLine() throws IOException{
    if(myConnection == null) throw new IOException("Can not read line when not connected!");
    try{
      return getReader().readLine();
    }catch(IOException e){
      handleIOException( e );
      throw e;
    }
  }

  public void write(String aKey, String aValue) throws IOException{
    long t1 = System.currentTimeMillis();
    try{
      if(!isFirstKey) {
        getWriter().write( "&" );
      }
      getWriter().write( aKey + "=" + aValue );
      isFirstKey = false;
      long t2 = System.currentTimeMillis();
      LOGGER.debug("Writing key value '" + aKey  + "'='" + aValue + "' took " + (t2 - t1) + " ms");
    }catch(IOException e){
      handleIOException( e );
      throw e;
    } 
  }
  
  public void write(String aText) throws IOException{
    try{
      getWriter().write( aText );
    }catch(IOException e){
      handleIOException( e );
      throw e;
    }
  }
  
  public void endLine() throws IOException{
    write("\r\n");
  }

  public void flush() throws IOException{
    try{
      myOutputStream.flush();
    }catch(IOException e){
      handleIOException( e );
      throw e;
    }
  }

  private void handleIOException(IOException e){
    try{
      int theRespCode = ((HttpURLConnection)myConnection).getResponseCode();
      LOGGER.error("Error occured while connecting to '" + myURL + "' Error code '" + theRespCode + "'");
      BufferedReader theErrorReader = null;
      try{
        theErrorReader = new BufferedReader( new InputStreamReader( ((HttpURLConnection)myConnection).getErrorStream()));
        String theLine = null;
        while( (theLine = theErrorReader.readLine()) != null){
          LOGGER.error(theLine);
        }
      } finally {
        if(theErrorReader != null){
          theErrorReader.close();
        }
      }
    }catch(IOException e1){
      //LOGGER.error( "Could not get response code when handling error on connecting to '" + myURL + "'", e1 );
      LOGGER.error( "Could not get response code when handling error on connecting to '" + myURL + "'" );
    }

  }

  public void close(){
    if(myClosingService != null){
      myClosingService.shutdownNow();
    }

    if(myInputStream != null){
      try {
        myInputStream.close();
      } catch ( IOException e ) {
        LOGGER.error( "Could not close inputstream", e );
      }
    }

    /*
    if(myOutputStream != null){
      try{
        myOutputStream.flush();
      }catch(IOException e){
        LOGGER.error( "Could not flush outputstream", e );
      }

      try{
        myOutputStream.close();
      }catch(IOException e){
        LOGGER.error( "Could not close outputstream", e );
      }
    }
    */

    //only if the input stream is null we connect the url connection
    //otherwise a deadlock might arise on waiting for the input stream
//    if(myInputStream == null){
//      ((HttpURLConnection)myConnection).disconnect();
//    }
  }

  public void disconnect(){
    if(myConnection != null){
      ((HttpURLConnection)myConnection).disconnect();
    }
  }

  public class CloseCommand implements Runnable {
    @Override
    public void run() {
      close();
    }
  }
}
