/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.io.CachingSocketPool;
import chabernac.io.SocketPoolFactory;
import chabernac.io.SocketPoolPanel;
import chabernac.io.SocketProxy;
import chabernac.io.iSocketPool;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ProtocolMessagePanel;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.infoexchange.InfoExchangeProtocol;
import chabernac.protocol.infoexchange.InfoObject;
import chabernac.protocol.infoexchange.InfoObjectPanel;
import chabernac.protocol.message.MessageArchivePanel;
import chabernac.protocol.message.MessagePanel;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.MultiPeerMessageProtocol;
import chabernac.protocol.pominfoexchange.POMInfo;
import chabernac.protocol.userinfo.UserInfoPanel;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.tools.PropertyMap;
import chabernac.tools.SuperNodesDataSource;
import chabernac.util.concurrent.MonitorPanel;

public class RoutingFrame extends JFrame {
  private static final long serialVersionUID = 6198311092838546976L;
  private static Logger LOGGER = Logger.getLogger(RoutingFrame.class);

  private ProtocolContainer myProtocolContainer = null;
  private ProtocolServer myProtocolServer = null;
  private boolean isStopOnClose;

  public RoutingFrame(ProtocolServer aServer, ProtocolContainer aContainer, boolean isStopOnClose) throws ProtocolException{
    myProtocolContainer = aContainer;
    myProtocolServer = aServer;
    this.isStopOnClose = isStopOnClose;
    addListeners();
    buildGUI();
//    addWebPeer();
  }

  private void addWebPeer(){
    try{
      WebPeer theWebPeer = new WebPeer("", new URL("http://x22p0212:8080/p2pwebnode"));
      RoutingTableEntry theEntry = new RoutingTableEntry(theWebPeer, 1, theWebPeer, System.currentTimeMillis());
      getRoutingTable().addEntry( theEntry );
    }catch(Exception e){
      LOGGER.error("Unable to add web peer", e);
    }
  }

  public void setVisible(boolean isVisible){
    super.setVisible( isVisible );
    if(isVisible){
      try {
        init();
      } catch ( ProtocolException e ) {
      }
    }
  }

  private void init() throws ProtocolException{
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE);
    getRoutingTable().setKeepHistory(true);
    SocketProxy.setTraceEnabled(true);
    ((MessageProtocol)myProtocolContainer.getProtocol( MessageProtocol.ID )).setKeepHistory( true );
    ((PeerSender)PeerSenderHolder.getPeerSender()).setKeepHistory(true);
  }

  private void addListeners(){
    addWindowListener( new MyWindowListener() );
  }

  private void buildGUI() throws ProtocolException {
    RoutingProtocol theRoutingProtocl = (RoutingProtocol)myProtocolContainer.getProtocol( RoutingProtocol.ID );
    setTitle( theRoutingProtocl.getLocalPeerId() );

    JTabbedPane thePane = new JTabbedPane();
    thePane.add("Routing Table",  new RoutingPanel(theRoutingProtocl) );
    thePane.add("Sockets", new SocketPoolPanel() );

    thePane.add("User Info", new UserInfoPanel((UserInfoProtocol)myProtocolContainer.getProtocol( UserInfoProtocol.ID )));
    MonitorPanel theMonitorPanel = new MonitorPanel();
    myProtocolServer.setRunnableListener( theMonitorPanel );
    thePane.add("Threads", theMonitorPanel);

    MessageProtocol theMessageProtocol = (MessageProtocol)myProtocolContainer.getProtocol( MessageProtocol.ID );
    thePane.add("Messages", new MessagePanel(theMessageProtocol));
    thePane.add("Protocol", new ProtocolMessagePanel(myProtocolContainer));
    
    InfoExchangeProtocol<InfoObject> theInfoExchangeProtocol = (InfoExchangeProtocol< InfoObject >)myProtocolContainer.getProtocol( InfoExchangeProtocol.ID );
    thePane.add("Info", new InfoObjectPanel(theInfoExchangeProtocol));
    
    thePane.add("PeerMessages", new PeerMessagePanel(theRoutingProtocl.getPeerSender()));
    
    thePane.add("Message Archive", new MessageArchivePanel((MultiPeerMessageProtocol)myProtocolContainer.getProtocol( MultiPeerMessageProtocol.ID )));


    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add( thePane, BorderLayout.CENTER );


    setSize( 1200, 700 );
  }

  private RoutingTable getRoutingTable(){
    try {
      return ((RoutingProtocol)myProtocolContainer.getProtocol( RoutingProtocol.ID )).getRoutingTable();
    } catch (ProtocolException e) {
      return null;
    }
  }

  public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing( WindowEvent anE ) {
      getRoutingTable().setKeepHistory(false);
      getRoutingTable().clearHistory();
      SocketProxy.setTraceEnabled(false);
      ((PeerSender)PeerSenderHolder.getPeerSender()).setKeepHistory(true);
      try {
        ((MessageProtocol)myProtocolContainer.getProtocol( MessageProtocol.ID )).setKeepHistory( false );
      } catch ( ProtocolException e ) {
      }

      if(isStopOnClose){
        myProtocolServer.stop();
        System.exit( 0 );
      }
    }
  }

  public static void main(String args[]) throws ProtocolException, InterruptedException, IOException{
    //    PropertyConfigurator.configure( "log4j.properties" );
    System.getProperties().put("http.proxyHost", "iproxy.axa.be");
    System.getProperties().put("http.proxyPort", "8080");

    
    BasicConfigurator.configure();
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", "60");
    theProperties.setProperty("routingprotocol.persist", "true");
    theProperties.setProperty("peerid", UUID.randomUUID().toString());
    theProperties.setProperty( "routingprotocol.supernodes", new SuperNodesDataSource("10.240.221.37"
                                                                                      ,"10.240.222.204"
                                                                                      ,"10.240.220.190"
                                                                                      ,"10.240.223.56"
                                                                                      ,"10.240.223.46"
                                                                                      ,"10.240.222.101"
                                                                                      ,"10.240.221.112"
                                                                                      ,"10.240.221.73"
                                                                                      ,"http://x22p0212:8080/p2p/"
                                                                                      ,"http://peertopeerwebnode.appspot.com/") );
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    ProtocolContainer theContainer = new ProtocolContainer(theFactory);
    ProtocolServer theServer1 = new ProtocolServer(theContainer, RoutingProtocol.START_PORT, 20, true);
    
    RoutingFrame theFrame = new RoutingFrame(theServer1, theContainer, true);
    theServer1.start();
    
    
    Thread.sleep( 2000 );
    
    theContainer.getProtocol( RoutingProtocol.ID );
    theContainer.getProtocol( UserInfoProtocol.ID );
    ((InfoExchangeProtocol< InfoObject >)theContainer.getProtocol( InfoExchangeProtocol.ID )).getInfoObject().put( "pom.info", new POMInfo() );
//    theContainer.getProtocol( VersionProtocol.ID );
//    theContainer.getProtocol( WebPeerProtocol.ID );
    
    theFrame.setVisible( true );


    iSocketPool theSocketPool = SocketPoolFactory.getSocketPool();
    if(theSocketPool instanceof CachingSocketPool){
      ((CachingSocketPool)theSocketPool).setCleanUpTimeInSeconds( 30 );
    }
  }


}
