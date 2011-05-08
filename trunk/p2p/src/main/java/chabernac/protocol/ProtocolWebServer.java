package chabernac.protocol;

import java.net.URL;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.ajp.Ajp13SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometServlet;
import chabernac.p2p.web.ProtocolServlet;

public class ProtocolWebServer implements iP2PServer {
  private final static Logger LOGGER = Logger.getLogger(ProtocolWebServer.class);

  private final ProtocolContainer myProtocolContainer;
  private final URL myURL;
  private final int myPort;
  private Integer myAJPort = null;
  private Server myServer = null;

  public ProtocolWebServer(ProtocolContainer anProtocolContainer, int aPort, URL anURL) {
    super();
    myProtocolContainer = anProtocolContainer;
    myPort = aPort;
    myURL = anURL;
  }

  public Integer getAJPort() {
    return myAJPort;
  }

  public void setAJPort(Integer anPort) {
    myAJPort = anPort;
  }

  @Override
  public boolean isStarted() {
    return myServer.isStarted();
  }

  @Override
  public boolean start() {
    if(isStarted()){
      stop();
    }
    
    try{
      myServer = new Server(myPort);

      if(myAJPort != null){
        Ajp13SocketConnector theAJPConnector = new Ajp13SocketConnector();
        theAJPConnector.setPort(myAJPort.intValue());
        myServer.addConnector(theAJPConnector);
      }

      Context root = new Context(myServer,"/p2p",Context.SESSIONS);

      CometServlet theCometServlet = new CometServlet();
      ServletHolder theCometHolder = new ServletHolder(theCometServlet);
      theCometHolder.setInitOrder( 1 );
      root.addServlet(theCometHolder, "/comet");

      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      theProtocolServlet.getServletContext().setAttribute("ProtocolContainer", myProtocolContainer);
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      root.addServlet(theProtocolHolder, "/protocol");

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
