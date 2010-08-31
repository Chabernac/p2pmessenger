/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MessageModel implements TableModel {

  private final MessageProtocol myProtocol;
  private final List< TableModelListener > myListeners = new ArrayList< TableModelListener >();

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
    return 4;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "From";
    if(anColumnIndex == 1) return "To";
    if(anColumnIndex == 2) return "TTL";
    if(anColumnIndex == 3) return "Message";
    return "";
  }

  @Override
  public int getRowCount() {
    return myProtocol.getHistory().size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    Message theMessage = myProtocol.getHistory().get( anRowIndex );
    if(anColumnIndex == 0) return theMessage.getSource().getPeerId();
    if(anColumnIndex == 1) return theMessage.getDestination().getPeerId();
    if(anColumnIndex == 2) return theMessage.getTTL();
    if(anColumnIndex == 3) return theMessage.getMessage();
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
