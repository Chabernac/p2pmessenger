/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MultiPeerMessage;

public class ChatMediatorTest extends TestCase {
  public void testMediator() throws P2PFacadeException, InterruptedException, ExecutionException{

    BasicConfigurator.configure();

    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new AXALDAPUserInfoProvider() )
    .start( 5 );
    
    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new AXALDAPUserInfoProvider() )
    .start( 5 );
    
    P2PFacade theFacade3 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new AXALDAPUserInfoProvider() )
    .start( 5 );
    
    Thread.sleep( 2000 );

    DummyMessageProvider theMessageProvider = new DummyMessageProvider();
    DummyUserSelectionProvider theUserSelectionProvider = new DummyUserSelectionProvider();

    ChatMediator theMediator = new ChatMediator(theFacade1);
    theMediator.setMessageProvider( theMessageProvider );
    theMediator.setUserSelectionProvider( theUserSelectionProvider );

    List<MultiPeerMessage> theSendMessages = new ArrayList< MultiPeerMessage >();
    int theNumber = 5;
    for(int i=0;i<theNumber;i++){
      theMessageProvider.setMessage( "test" + i );
      Set<String> theSelectedUsers = new HashSet<String>();
      if(i%2 == 0){
        theSelectedUsers.add( theFacade3.getPeerId() );
      } else {
        theSelectedUsers.add( theFacade2.getPeerId() );
      }
      theUserSelectionProvider.setSelectedUsers( theSelectedUsers );
  
      Future<MultiPeerMessage> theFuture = theMediator.send();
      theSendMessages.add(theFuture.get());
    }
    
    Thread.sleep( 1000 );
    
    assertEquals( theNumber, theFacade1.getMessageArchive().getDeliveryReports().size() );
    
    //at this point no users nor text must be selected
    
    assertTrue( theMessageProvider.getMessage().isEmpty() );
    assertTrue( theUserSelectionProvider.getSelectedUsers().isEmpty() );
    
    //now we go one message back
    
    theMediator.restorePreviousMessage();
    
    //this must be the last message send
    assertEquals( theSendMessages.get( theSendMessages.size() - 1 ).getMessage(), theMessageProvider.getMessage());
    assertEquals( theSendMessages.get( theSendMessages.size() - 1 ).getDestinations(), theUserSelectionProvider.getSelectedUsers());
    
    //restore another message
    theMediator.restorePreviousMessage();
    assertEquals( theSendMessages.get( theSendMessages.size() - 2 ).getMessage(), theMessageProvider.getMessage());
    assertEquals( theSendMessages.get( theSendMessages.size() - 2 ).getDestinations(), theUserSelectionProvider.getSelectedUsers());
    
    //restore more than message are available
    for(int i=0;i<theNumber;i++){
      theMediator.restorePreviousMessage();
    }
    
    //this must be the first message
    assertEquals( theSendMessages.get( 0 ).getMessage(), theMessageProvider.getMessage());
    assertEquals( theSendMessages.get( 0 ).getDestinations(), theUserSelectionProvider.getSelectedUsers());
    
    //go to last again
    
    for(int i=0;i<theNumber + 2;i++){
      theMediator.restoreNextMesssage();
    }
    
    assertTrue( theMessageProvider.getMessage().isEmpty() );
    assertTrue( theUserSelectionProvider.getSelectedUsers().isEmpty() );
    
    //set some text and user selection but do not send it, it should be saved as a concept
    theMessageProvider.setMessage( "concept" );
    Set<String> theSelectedUsers = new HashSet<String>();
    theSelectedUsers.add( theFacade3.getPeerId() );
    theUserSelectionProvider.setSelectedUsers( theSelectedUsers );
    
    theMediator.restorePreviousMessage();
    
    //this is the last message again
    assertEquals( theSendMessages.get( theSendMessages.size() - 1 ).getMessage(), theMessageProvider.getMessage());
    assertEquals( theSendMessages.get( theSendMessages.size() - 1 ).getDestinations(), theUserSelectionProvider.getSelectedUsers());
    
    theMediator.restoreNextMesssage();
    //this must be the concept again
    assertEquals( "concept", theMessageProvider.getMessage());
    assertEquals( theSelectedUsers, theUserSelectionProvider.getSelectedUsers());

  }
}
