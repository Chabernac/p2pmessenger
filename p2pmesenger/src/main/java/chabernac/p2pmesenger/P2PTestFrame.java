/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pmesenger;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JList;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.exception.PeerGroupException;

public class P2PTestFrame extends JFrame implements DiscoveryListener{

  private static final long serialVersionUID = -5981517914719146626L;

  private JList myList = null;
  private Set<Object> myPeerList = new HashSet<Object>();
  private String myTitle = null;
  
  public P2PTestFrame() throws PeerGroupException, IOException{
    buildGUI();
    initPeerDiscovery();
  }

  private void buildGUI() {
    getContentPane().setLayout(  new BorderLayout() );
    myList = new JList();
    getContentPane().add( myList, BorderLayout.CENTER );
    myTitle = System.getProperty( "user.name" ) + " " + System.currentTimeMillis();
    setTitle( myTitle );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
  }

  private void initPeerDiscovery() throws PeerGroupException, IOException {
    PeerDiscovery theDiscovery = new PeerDiscovery(myTitle);
    theDiscovery.addDiscoveryListener( this );
  }

  @Override
  public void discoveryEvent( DiscoveryEvent anEvent ) {
    myPeerList.add(anEvent.getSource());
    myList.setListData( myPeerList.toArray() );
  }

  public static void main(String args[]){
    try{
      P2PTestFrame theFrame = new P2PTestFrame();
      theFrame.setSize( 200, 200 );
      theFrame.setVisible( true );
    }catch(Exception e){
      e.printStackTrace();
    }
  }


}
