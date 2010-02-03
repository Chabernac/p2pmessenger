/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pmesenger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.exception.PeerGroupException;

public class P2PTestFrame extends JFrame implements DiscoveryListener, iChatListener{

  private static final long serialVersionUID = -5981517914719146626L;

  private JList myList = null;
  private List<Object> myPeerList = new ArrayList<Object>();
  private String myTitle = null;
  private JTextArea myReceived = null;
  private JTextArea mySend = null;
  private PeerDiscovery myPeerDiscovery = null;
  
  public P2PTestFrame() throws PeerGroupException, IOException, NoSuchAlgorithmException{
    buildGUI();
    initPeerDiscovery();
  }

  private void buildGUI() {
    myTitle = System.getProperty( "user.name" ) + " " + System.currentTimeMillis();
    setTitle( myTitle );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    
    getContentPane().setLayout(  new BorderLayout() );
    myList = new JList();
    myList.setBorder( new TitledBorder("Peers") );
    getContentPane().add( myList, BorderLayout.NORTH);
    myReceived = new JTextArea();
    myReceived.setBorder( new TitledBorder("Received") );
    mySend = new JTextArea();
    mySend.setBorder( new TitledBorder("Send") );
    
    getContentPane().add(new JScrollPane(myReceived), BorderLayout.CENTER);
    
    JPanel theSouthPanel = new JPanel(new BorderLayout());
    theSouthPanel.add(mySend, BorderLayout.CENTER);
    
    JButton theSendButton = new JButton("Send");
    theSendButton.addActionListener( new SendAction() );
    theSouthPanel.add(theSendButton, BorderLayout.EAST);
    
    getContentPane().add( theSouthPanel, BorderLayout.SOUTH );
  }
  
  private class SendAction implements ActionListener{
    @Override
    public void actionPerformed( ActionEvent anE ) {
      String theSelectedPeer = myPeerList.get( myList.getSelectedIndex() ).toString();
      
      try{
        myPeerDiscovery.sendMessage( mySend.getText(), theSelectedPeer );
        mySend.setText( "" );
      }catch(Exception e){
        e.printStackTrace();
      }
    }
    
   
  }

  private void initPeerDiscovery() throws PeerGroupException, IOException, NoSuchAlgorithmException {
    myPeerDiscovery = new PeerDiscovery(myTitle);
    myPeerDiscovery.addDiscoveryListener( this );
    setTitle( myTitle + "-" + myPeerDiscovery.getPeerId() );
    myPeerDiscovery.addChatListener( this );
  }

  @Override
  public void discoveryEvent( DiscoveryEvent anEvent ) {
    int theSelectedIndex = myList.getSelectedIndex();
    if(!myPeerList.contains( anEvent.getSource() )){
      myPeerList.add(anEvent.getSource());
    }
    myList.setListData( myPeerList.toArray() );
    myList.setSelectedIndex( theSelectedIndex );
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

  @Override
  public void messageReceived( String aMessage ) {
    myReceived.setText( myReceived.getText() + "\r\n" + aMessage );
  }


}
