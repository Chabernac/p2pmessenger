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

import chabernac.protocol.ProtocolFactory;
import chabernac.tools.StringTools;

public class MessageModel implements TableModel {

  private final MessageProtocol myProtocol;
  private final List< TableModelListener > myListeners = new ArrayList< TableModelListener >();
  
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

  public MessageModel ( MessageProtocol anProtocol ) {
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
    return 8;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "Time";
    if(anColumnIndex == 1) return "UID";
    if(anColumnIndex == 2) return "From";
    if(anColumnIndex == 3) return "To";
    if(anColumnIndex == 4) return "TTL";
    if(anColumnIndex == 5) return "Protocol";
    if(anColumnIndex == 6) return "Message";
    if(anColumnIndex == 7) return "Response";
    return "";
  }

  @Override
  public int getRowCount() {
    return myProtocol.getHistory().size();
  }
  
  public MessageAndResponse getMessageAtRow(int aRow){
    return myProtocol.getHistory().get( aRow );
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
    if(anColumnIndex == 5) return theMessage.getMessage().getMessage().substring( 0,3 );
    if(anColumnIndex == 6) return theMessage.getMessage().getMessage().substring( 3 );
    if(anColumnIndex == 7) return theMessage.getResponse();
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
  
  private class MessageListener implements iMessageListener {

    @Override
    public void messageReceived( Message aMessage ) {
      for(TableModelListener theListener : myListeners){
        theListener.tableChanged( new TableModelEvent(MessageModel.this) );
      }
    }

  }

}
