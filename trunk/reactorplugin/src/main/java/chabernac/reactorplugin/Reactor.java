/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class Reactor implements iMultiPeerMessageListener {
  private static Logger LOGGER = Logger.getLogger(Reactor.class);

  private final P2PFacade myP2PFacade;
  private final ReactorSettings myReactorSettings = new ReactorSettings();

  private Set<String> myParticipants = new HashSet<String>();

  private ExecutorService mySendService = Executors.newSingleThreadExecutor();
  private ScheduledExecutorService myScheduledExecutorService;

  private StatementFactoryComposite myFactory = null;

  private AbstractStatement myLastStatement = null;
  private MultiPeerMessage myLastMessage = null;

  public Reactor( P2PFacade aP2pFacade ) {
    super();
    myP2PFacade = aP2pFacade;
  }

  public void start() throws ReactorException{
    try {
      myFactory = new StatementFactoryComposite( myReactorSettings.getQuestionsPerRound() );

      myP2PFacade.addMessageListener( this );
      myScheduledExecutorService = Executors.newScheduledThreadPool( 1 );
      myScheduledExecutorService.schedule( new ReactorMessageSender(), myReactorSettings.getTimeout(), myReactorSettings.getTimeUnit());
    } catch ( P2PFacadeException e ) {
      LOGGER.error("An error occured while starting reactor", e);
      throw new ReactorException("Unable to start reactor", e);
    }
  }

  public void stop(){
    try {
      myP2PFacade.removeMessageListener( this );
    } catch ( P2PFacadeException e ) {
      LOGGER.error("An error occured while stopping reactor", e);
    }
  }

  public ReactorSettings getReactorSettings() {
    return myReactorSettings;
  }

  public void togglePlayer(String aPlayer){
    if(myParticipants.contains( aPlayer )){
      myParticipants.remove(aPlayer);
    } else {
      myParticipants.add(aPlayer);
    }
  }

  @Override
  public void messageReceived( MultiPeerMessage aMessage ) {
    if(aMessage.getMessage().equalsIgnoreCase( "reactor" )){
      togglePlayer( aMessage.getSource() );
    } else if(aMessage.getMessage().equalsIgnoreCase( "reactor aan" )){
      myParticipants.add( aMessage.getSource() );
    } else if(aMessage.getMessage().equalsIgnoreCase( "reactor uit" )){
      myParticipants.remove( aMessage.getSource() );
    } else if(myLastMessage != null && myLastMessage.getConversationId().equals( aMessage.getConversationId() )){
      //this is a reply to the reactor message
      if(myLastMessage.getMessage().length() > 0){
        boolean theReplyBoolean = "j".equalsIgnoreCase( myLastMessage.getMessage().substring( 0,1 ) );
        if(myLastStatement.isTrue() == theReplyBoolean){
          
        }
      }
    }
  }

  private class ReactorMessageSender implements Runnable{

    @Override
    public void run() {
      if(myFactory.hasMoreStatements()){
        myLastStatement = myFactory.createStatement();
        MultiPeerMessage theMessage = MultiPeerMessage.createMessage( myLastStatement.getStatement() );
        for(String theDestination : myParticipants){
          theMessage.addDestination( theDestination );
        }
        theMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
        theMessage.addMessageIndicator( MessageIndicator.CLOSED_ENVELOPPE);

        try{
          if(myP2PFacade.sendMessage( theMessage, mySendService ).get()){
            myLastMessage = theMessage;
          }
        }catch(Exception e){
          LOGGER.error("Unable to send reactor message", e);
        }
      }
    }
  }
}
