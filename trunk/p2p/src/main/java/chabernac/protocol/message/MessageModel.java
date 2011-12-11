/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import chabernac.tools.StringTools;

public class MessageModel implements TableModel {

  private final AbstractMessageProtocol myProtocol;
  private final List< TableModelListener > myListeners = new ArrayList< TableModelListener >();

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

  public MessageModel ( AbstractMessageProtocol anProtocol ) {
    super();
    myProtocol = anProtocol;
    anProtocol.addMessageHistoryListener( new MessageListener() );
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
    return 13;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "Time";
    if(anColumnIndex == 1) return "UID";
    if(anColumnIndex == 2) return "From";
    if(anColumnIndex == 3) return "To";
    if(anColumnIndex == 4) return "TTL";
    if(anColumnIndex == 5) return "Protocol";
    if(anColumnIndex == 6) return "Indicators";
    if(anColumnIndex == 7) return "Message";
    if(anColumnIndex == 8) return "Response";
    if(anColumnIndex == 9) return "Header Type";
    if(anColumnIndex == 10) return "Message id";
    if(anColumnIndex == 11) return "Status";
    if(anColumnIndex == 12) return "Response Time";
    return "";
  }

  @Override
  public int getRowCount() {
    return myProtocol.getHistory().size();
  }

  public MessageAndResponse getMessageAtRow(int aRow){
    return myProtocol.getHistory().get(aRow);
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    MessageAndResponse theMessage = getMessageAtRow( anRowIndex );
    if(anColumnIndex == 0) {
      Date theDate = new Date();
      theDate.setTime( theMessage.getMessage().getCreationTime() );
      return FORMAT.format( theDate );
    }
    if(anColumnIndex == 1) return StringTools.convertToLocalUniqueId(theMessage.getMessage().getMessageId().toString());
    if(anColumnIndex == 2) return theMessage.getMessage().getSource().getPeerId();
    if(anColumnIndex == 3) return theMessage.getMessage().getDestination().getPeerId();
    if(anColumnIndex == 4) return theMessage.getMessage().getTTL();
    if(anColumnIndex == 6) {
      return theMessage.getMessage().getIndicators().toString();
    }
    if(theMessage.getMessage() != null && theMessage.getMessage().getMessage() != null && !theMessage.getMessage().getIndicators().contains(MessageIndicator.ENCRYPTED)){
      if(anColumnIndex == 5) return theMessage.getMessage().getMessage().substring( 0,3 );
      if(anColumnIndex == 7) return theMessage.getMessage().getMessage().substring( 3 );
    }
    if(anColumnIndex == 8) return theMessage.getResponse();
    if(anColumnIndex == 9 && theMessage.getMessage().containsHeader("TYPE")) return theMessage.getMessage().getHeader("TYPE");
    if(anColumnIndex == 10 && theMessage.getMessage().containsHeader("MESSAGE-ID")) return StringTools.convertToLocalUniqueId(theMessage.getMessage().getHeader("MESSAGE-ID"));
    if(anColumnIndex == 11 && theMessage.getMessage().containsHeader("STATUS")) return theMessage.getMessage().getHeader("STATUS");
    if(anColumnIndex == 12) return theMessage.getResponseTime();
    
    return null;
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

  private void fireTableModelEvent(){
    for(TableModelListener theListener : myListeners){
      theListener.tableChanged( new TableModelEvent(MessageModel.this) );
    }

  }

  private class MessageListener implements iMessageListener {

    @Override
    public void messageReceived( Message aMessage ) {
      fireTableModelEvent();
    }

    @Override
    public void messageUpdated( Message aMessage ) {
      fireTableModelEvent();
    }

  }

}
