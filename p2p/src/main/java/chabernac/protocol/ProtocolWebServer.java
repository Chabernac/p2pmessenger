package chabernac.protocol;

import java.net.URL;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.ajp.Ajp13SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;
import chabernac.comet.CometServlet;
import chabernac.p2p.web.ProtocolServlet;

public class ProtocolWebServer implements iP2PServer {
  private final static Logger LOGGER = Logger.getLogger(ProtocolWebServer.class);

  private final ProtocolContainer myProtocolContainer;
  private final URL myURL;
  private final int myPort;
  private Integer myAJPPort = null;
  private Server myServer = null;
  
  public static String CONTEXT = "/pp";
  public static String COMET = "/com";
  public static String PROTOCOL = "/prot";
  
  public static String CONTEXT_PROTOCOL = "pp/prot";
  public static String CONTEXT_COMET = "pp/com";

  public ProtocolWebServer(ProtocolContainer anProtocolContainer, int aPort, URL anURL) {
    super();
    myProtocolContainer = anProtocolContainer;
    myPort = aPort;
    myURL = anURL;
  }

  public Integer getAJPPort() {
    return myAJPPort;
  }

  public void setAJPPort(Integer anPort) {
    myAJPPort = anPort;
  }

  @Override
  public boolean isStarted() {
    return myServer != null && myServer.isStarted();
  }

  @Override
  public boolean start() {
    if(isStarted()){
      stop();
    }
    
    try{
      myServer = new Server(myPort);
      myServer.setThreadPool(new QueuedThreadPool(20));

      if(myAJPPort != null){
        Ajp13SocketConnector theAJPConnector = new Ajp13SocketConnector();
        theAJPConnector.setPort(myAJPPort.intValue());
        myServer.addConnector(theAJPConnector);
      }

      Context root = new Context(myServer,CONTEXT,Context.SESSIONS);
      root.getServletContext().setAttribute("ProtocolContainer", myProtocolContainer);

      CometServlet theCometServlet = new CometServlet();
      ServletHolder theCometHolder = new ServletHolder(theCometServlet);
      theCometHolder.setInitOrder( 1 );
      root.addServlet(theCometHolder, COMET);
      
      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      root.addServlet(theProtocolHolder, PROTOCOL);

      theProtocolHolder.setInitParameter( "serverurl", myURL.toString() );
      
      myServer.start();

      return true;
    }catch(Exception e){
      LOGGER.error("Could not start protocol webserver", e);
      return false;
    }
  }


  @Override
  public void stop() {
    try{
      myServer.stop();
    }catch(Exception e){
      LOGGER.error("Error occured while stopping webserver", e);
    }
    myServer = null;
  }
}
