/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * Immutable class which represents a message to multiple peers
 */

public class MultiPeerMessage implements Serializable{
  private static final long serialVersionUID = 7296768914758056021L;
  
  private final List< String > myDestinations ;
  private final String mySource;
  private final String myMessage;
  private final List<MessageIndicator> myIndicators;
  private final Date myCreationTime = new Date();
  private final UUID myUniqueId;
  
  public MultiPeerMessage ( List< String > anDestinations , String anSource , String anMessage , List< MessageIndicator > anIndicators, UUID aUniqueId ) {
    super();
    myDestinations = anDestinations;
    mySource = anSource;
    myMessage = anMessage;
    myIndicators = anIndicators;
    if(aUniqueId == null){
      myUniqueId = UUID.randomUUID();
    } else {
      myUniqueId = aUniqueId;
    }
  }

  public List< String > getDestinations() {
    if(myDestinations == null) return new ArrayList< String >();
    return Collections.unmodifiableList( myDestinations );
  }

  public String getSource() {
    return mySource;
  }

  public String getMessage() {
    return myMessage;
  }

  public List< MessageIndicator > getIndicators() {
    if(myIndicators == null) return new ArrayList< MessageIndicator >();
    return Collections.unmodifiableList( myIndicators );
  }

  public static MultiPeerMessage createMessage(String aMessage){
    return new MultiPeerMessage(null, null, aMessage, null, null);
  }
  
  public static MultiPeerMessage createMessage(String aSource, String aMessage){
    return new MultiPeerMessage(null, aSource, aMessage, null, null);
  }
  
  public MultiPeerMessage setSource(String aSource){
    return new MultiPeerMessage(getDestinations(), aSource, getMessage(), getIndicators(), getUniqueId());
  }
  
  public MultiPeerMessage addDestination(String aDestinationPeer){
    List<String> theDestinations = new ArrayList< String >(getDestinations());
    theDestinations.add( aDestinationPeer);
    return new MultiPeerMessage(theDestinations, getSource(), getMessage(), getIndicators(), getUniqueId());
  }
  
  public MultiPeerMessage removeDestination(String aDestinationPeer){
    List<String> theDestinations = new ArrayList< String >(getDestinations());
    theDestinations.remove( aDestinationPeer);
    return new MultiPeerMessage(theDestinations, getSource(), getMessage(), getIndicators(), getUniqueId());
  }
  
  public MultiPeerMessage setDestinations(List<String> aDestinations){
    return new MultiPeerMessage(aDestinations, getSource(), getMessage(), getIndicators(), getUniqueId());
  }
  
  public MultiPeerMessage addMessageIndicator(MessageIndicator anIndicator){
    List<MessageIndicator> theMessageIndicators = new ArrayList< MessageIndicator >(getIndicators());
    theMessageIndicators.add( anIndicator );
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), theMessageIndicators, getUniqueId());
  }
  
  public MultiPeerMessage setMessage(String aMessage){
    return new MultiPeerMessage(getDestinations(), getSource(), aMessage, getIndicators(), getUniqueId());
  }
  
  public MultiPeerMessage setMessageIndicators(List<MessageIndicator> anIndicators){
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), anIndicators, getUniqueId());
  }
  
  public Date getCreationTime(){
    return myCreationTime;
  }
  
  public UUID getUniqueId() {
    return myUniqueId;
  }

  public boolean equals(Object anObject){
    if(!(anObject instanceof MultiPeerMessage)){
      return false;
    }
    MultiPeerMessage theMessage = (MultiPeerMessage)anObject;
    if(!getDestinations().equals( theMessage.getDestinations() )) return false;
    if(!mySource.equals( theMessage.getSource() )) return false;
    if(!myMessage.equals( theMessage.getMessage() )) return false;
    if(!getIndicators().equals( theMessage.getIndicators() )) return false;
    if(!getCreationTime().equals(theMessage.getCreationTime())) return false;
    return true;
  }
  
  public MultiPeerMessage reply(){
    return MultiPeerMessage.createMessage( "" )
    .addDestination( getSource() )
    .setMessageIndicators( getIndicators() );
  }
  
  public MultiPeerMessage replyAll(){
    return MultiPeerMessage.createMessage( "" )
    .setDestinations( getDestinations() )
    .addDestination( getSource() );
  }
  
}
