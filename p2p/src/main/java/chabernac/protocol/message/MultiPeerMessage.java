/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/*
 * Immutable class which represents a message to multiple peers
 */

public class MultiPeerMessage implements Serializable{
  private static final long serialVersionUID = 7296768914758056021L;
  
  private final Set< String > myDestinations ;
  private final String mySource;
  private final String myMessage;
  private final List<MessageIndicator> myIndicators;
  private final Date myCreationTime = new Date();
  private final UUID myUniqueId;
  private final UUID myConversationId;
  private final boolean isLoopBack;
  
  public MultiPeerMessage ( Set< String > anDestinations , String anSource , String anMessage , List< MessageIndicator > anIndicators, UUID aUniqueId, UUID aConversationId, boolean isLoopBack) {
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
    
    if(aConversationId == null){
      myConversationId = UUID.randomUUID();
    } else {
      myConversationId = aConversationId;
    }
    
    this.isLoopBack = isLoopBack;
  }

  public Set< String > getDestinations() {
    if(myDestinations == null) return new HashSet< String >();
    return Collections.unmodifiableSet( myDestinations );
  }

  public String getSource() {
    return mySource;
  }

  public String getMessage() {
    return myMessage;
  }

  
  public boolean isLoopBack() {
    return isLoopBack;
  }

  public List< MessageIndicator > getIndicators() {
    if(myIndicators == null) return new ArrayList< MessageIndicator >();
    return Collections.unmodifiableList( myIndicators );
  }
  
  public static MultiPeerMessage createMessage(String aMessage){
    return new MultiPeerMessage(null, null, aMessage, null, null, null, false);
  }
  
  public static MultiPeerMessage createMessage(String aSource, String aMessage){
    return new MultiPeerMessage(null, aSource, aMessage, null, null, null, false);
  }
  
  public MultiPeerMessage setSource(String aSource){
    return new MultiPeerMessage(getDestinations(), aSource, getMessage(), getIndicators(), getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage addDestination(String aDestinationPeer){
    Set<String> theDestinations = new HashSet< String >(getDestinations());
    theDestinations.add( aDestinationPeer);
    return new MultiPeerMessage(theDestinations, getSource(), getMessage(), getIndicators(), getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage removeDestination(String aDestinationPeer){
    Set<String> theDestinations = new HashSet< String >(getDestinations());
    theDestinations.remove( aDestinationPeer);
    return new MultiPeerMessage(theDestinations, getSource(), getMessage(), getIndicators(), getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage setDestinations(Set<String> aDestinations){
    return new MultiPeerMessage(aDestinations, getSource(), getMessage(), getIndicators(), getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage addMessageIndicator(MessageIndicator anIndicator){
    List<MessageIndicator> theMessageIndicators = new ArrayList< MessageIndicator >(getIndicators());
    theMessageIndicators.add( anIndicator );
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), theMessageIndicators, getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage removeMessageIndicator(MessageIndicator anIndicator){
    List<MessageIndicator> theMessageIndicators = new ArrayList< MessageIndicator >(getIndicators());
    theMessageIndicators.remove( anIndicator );
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), theMessageIndicators, getUniqueId(), getConversationId(), false);
  }
  
  public boolean containsIndicator(MessageIndicator anIndicator){
    if(myIndicators == null){
      return false;
    }
    return myIndicators.contains( anIndicator );
  }
  
  
  public MultiPeerMessage setMessage(String aMessage){
    return new MultiPeerMessage(getDestinations(), getSource(), aMessage, getIndicators(), getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage setMessageIndicators(List<MessageIndicator> anIndicators){
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), anIndicators, getUniqueId(), getConversationId(), false);
  }
  
  public MultiPeerMessage setLoopBack(boolean isLoopBack){
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), getIndicators(), getUniqueId(), getConversationId(), isLoopBack);
  }
  
  public Date getCreationTime(){
    return myCreationTime;
  }
  
  public UUID getUniqueId() {
    return myUniqueId;
  }
  
  public UUID getConversationId() {
    return myConversationId;
  }
  
  public MultiPeerMessage setConversationId(UUID aConversationId){
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), getIndicators(), getUniqueId(), aConversationId, isLoopBack);
  }

  public int hashCode(){
    return myUniqueId.clockSequence();
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
    .setMessageIndicators( getIndicators() )
    .setConversationId( getConversationId() );
  }
  
  public MultiPeerMessage replyAll(){
    return MultiPeerMessage.createMessage( "" )
    .setDestinations( getDestinations() )
    .addDestination( getSource() )
    .setConversationId( getConversationId() );
  }
  
}
