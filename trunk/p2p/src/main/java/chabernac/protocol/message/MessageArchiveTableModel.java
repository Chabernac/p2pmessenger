/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import chabernac.tools.StringTools;

public class MessageArchiveTableModel implements TableModel {
  private final MessageArchive myMessageArchive;
  private List< TableModelListener > myListeners = new ArrayList< TableModelListener >();
  private static SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
  
  public MessageArchiveTableModel ( MessageArchive anMessageArchive ) {
    super();
    myMessageArchive = anMessageArchive;
  }

  @Override
  public void addTableModelListener( TableModelListener anL ) {
    myListeners.add(anL);
  }

  @Override
  public Class< ? > getColumnClass( int anColumnIndex ) {
    return String.class;
  }

  @Override
  public int getColumnCount() {
    return 7;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "ID";
    if(anColumnIndex == 1) return "Time";
    if(anColumnIndex == 2) return "From";
    if(anColumnIndex == 3) return "To";
    if(anColumnIndex == 4) return "Message";
    if(anColumnIndex == 5) return "Indicators";
    if(anColumnIndex == 6) return "Status";
    return "";
  }

  @Override
  public int getRowCount() {
    Set< MultiPeerMessage > theMessages = myMessageArchive.getAllMessages();
    return theMessages.size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    MultiPeerMessage theMessage = getMessageAtRow(anRowIndex);
    if(anColumnIndex == 0) return StringTools.convertToLocalUniqueId(theMessage.getUniqueId().toString());
    if(anColumnIndex == 1) return FORMAT.format( theMessage.getCreationTime());
    if(anColumnIndex == 2) return StringTools.convertToLocalUniqueId(theMessage.getSource());
    if(anColumnIndex == 3) return StringTools.convertToLocalUniqueId( theMessage.getDestinations().toArray(new String[]{}) );
    if(anColumnIndex == 4) return theMessage.getMessage();
    if(anColumnIndex == 5) return theMessage.getIndicators();
    if(anColumnIndex == 6) return createStatus(theMessage);
    return null;
  }
  
  private String createStatus(MultiPeerMessage aMessage){
    StringBuilder theBuilder = new StringBuilder();
    Map<String, DeliveryReport> theReports = myMessageArchive.getDeliveryReports().get( aMessage );
    if(theReports == null) return "";
    for(String thePeer : theReports.keySet()){
      theBuilder.append(StringTools.convertToLocalUniqueId( thePeer ));
      theBuilder.append("=");
      theBuilder.append(theReports.get( thePeer ).getDeliveryStatus().name());
      theBuilder.append(";");
    }
    return theBuilder.toString();
  }

  @Override
  public boolean isCellEditable( int anRowIndex, int anColumnIndex ) {
    return false;
  }

  @Override
  public void removeTableModelListener( TableModelListener anL ) {
    myListeners.remove( anL );
  }

  @Override
  public void setValueAt( Object aValue, int anRowIndex, int anColumnIndex ) {
  }
  
  public void refresh(){
    for(TableModelListener theListener : myListeners){
      theListener.tableChanged( new TableModelEvent(this) );
    }
  }

  public MultiPeerMessage getMessageAtRow( int anRow ) {
    List<MultiPeerMessage> theMessages = new ArrayList< MultiPeerMessage>( myMessageArchive.getAllMessages());
    return theMessages.get( anRow );
  }
}
