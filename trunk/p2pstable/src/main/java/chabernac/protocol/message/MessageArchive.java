/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class will collect send and received messages
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 10-jun-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      10-jun-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac</a>
 */
public class MessageArchive implements iDeliverReportListener, iMultiPeerMessageListener {
  private List< MultiPeerMessage > myReceivedMessages = Collections.synchronizedList( new ArrayList< MultiPeerMessage >());
  
  //this collection contains the delivery reports
  //the delivery reports are grouped per MultiPeerMessage
  //per MultiPeerMessage a Map is stored which contain the delivery reports
  //the key of the map are peer id's the values are the delivery reports
  private Map< MultiPeerMessage, Map<String, DeliveryReport> > myDeliveryReports = Collections.synchronizedMap( new LinkedHashMap< MultiPeerMessage, Map<String, DeliveryReport> >() ); 
  
  public MessageArchive(MultiPeerMessageProtocol aMultiPeerMessageProtocol){
    aMultiPeerMessageProtocol.addDeliveryReportListener( this );
    aMultiPeerMessageProtocol.addMultiPeerMessageListener( this );
  }
  
  private Set< MultiPeerMessage > myAllMessages = Collections.synchronizedSet(new LinkedHashSet< MultiPeerMessage >() );

  @Override
  public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
    if(!myDeliveryReports.containsKey( aDeliverReport.getMultiPeerMessage() )){
      Map<String, DeliveryReport> theDeliveryreportsForMultiPeerMessage = new HashMap< String, DeliveryReport >();
      myDeliveryReports.put( aDeliverReport.getMultiPeerMessage(), theDeliveryreportsForMultiPeerMessage );
    }
    Map<String, DeliveryReport> theDeliveryreportsForMultiPeerMessage = myDeliveryReports.get( aDeliverReport.getMultiPeerMessage() );
    theDeliveryreportsForMultiPeerMessage.put( aDeliverReport.getMessage().getDestination().getPeerId(), aDeliverReport );
    myAllMessages.add( aDeliverReport.getMultiPeerMessage() );
  }

  @Override
  public void messageReceived( MultiPeerMessage aMessage ) {
    myReceivedMessages.add(aMessage);
    myAllMessages.add( aMessage );
  }

  public List< MultiPeerMessage > getReceivedMessages() {
    return Collections.unmodifiableList( myReceivedMessages );
  }

  public Map< MultiPeerMessage, Map< String, DeliveryReport >> getDeliveryReports() {
    //TODO the internal map with delivery reports is not unmodifiable, make a change so that it is.
    return Collections.unmodifiableMap( myDeliveryReports );
  }
  
  public Map<String, DeliveryReport> getDeliveryReportsForMultiPeerMessage(MultiPeerMessage aMessage){
    Map<String, DeliveryReport> theDeliveryReports = myDeliveryReports.get(aMessage);
    if(theDeliveryReports == null) return new HashMap< String, DeliveryReport >();
    return Collections.unmodifiableMap( theDeliveryReports );
  }

  public Set< MultiPeerMessage > getAllMessages() {
    return Collections.unmodifiableSet( new LinkedHashSet< MultiPeerMessage >(myAllMessages) );
  }

  public void clear(){
    myDeliveryReports.clear();
    myAllMessages.clear();
  }
  
}
