/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Immutable class which represents a message to multiple peers
 */

public class MultiPeerMessage {
  private final List< String > myDestinations ;
  private final String mySource;
  private final String myMessage;
  private final List<MessageIndicator> myIndicators;
  
  public MultiPeerMessage ( List< String > anDestinations , String anSource , String anMessage , List< MessageIndicator > anIndicators ) {
    super();
    myDestinations = anDestinations;
    mySource = anSource;
    myMessage = anMessage;
    myIndicators = anIndicators;
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

  public static MultiPeerMessage createMessage(String aSource, String aMessage){
    return new MultiPeerMessage(null, aSource, aMessage, null);
  }
  
  public MultiPeerMessage addDestination(String aDestinationPeer){
    List<String> theDestinations = new ArrayList< String >(getDestinations());
    theDestinations.add( aDestinationPeer);
    return new MultiPeerMessage(theDestinations, getSource(), getMessage(), getIndicators());
  }
  
  public MultiPeerMessage addMessageIndicator(MessageIndicator anIndicator){
    List<MessageIndicator> theMessageIndicators = new ArrayList< MessageIndicator >(getIndicators());
    theMessageIndicators.add( anIndicator );
    return new MultiPeerMessage(getDestinations(), getSource(), getMessage(), theMessageIndicators);
  }
  
  
}
