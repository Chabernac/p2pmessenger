package chabernac.protocol.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.routing.WhoIsRunningTableModel.Entry.State;

public class WhoIsRunningTableModel implements iWhoIsRunningListener, TableModel {
  private final int myPortFrom;
  private final int myRange;
  private final List<Entry> myPeerIdsAtPort = new ArrayList<Entry>();
  private final List<TableModelListener> myListeners = new ArrayList<TableModelListener>();
  private final iObjectStringConverter< AbstractPeer> myPeerConverter = new Base64ObjectStringConverter< AbstractPeer >();

  public WhoIsRunningTableModel(int aPortFrom, int aPortEnd){
    myPortFrom = aPortFrom;
    myRange = aPortEnd - aPortFrom + 1;
    init();
  }

  private void init(){
    for(int i=0;i<myRange;i++){
      myPeerIdsAtPort.add(new Entry("", myPortFrom + i, "", State.DOWN));
    }
  }


  @Override
  public void noPeerAt(String aHost, int aPort) {
    int theRow = aPort - myPortFrom;
    myPeerIdsAtPort.set(theRow, new Entry(aHost, aPort, "", State.DOWN));
    notifyListeners(theRow);
  }

  @Override
  public void peerDetected(String aHost, int aPort, String aPeerId) {
    try{
      AbstractPeer thePeer = myPeerConverter.getObject( aPeerId );
      int theRow = aPort - myPortFrom;
      myPeerIdsAtPort.set(theRow, new Entry(aHost, aPort, thePeer.getPeerId(), State.RUNNING));
      notifyListeners(theRow);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  private void notifyListeners(int aRow){
    for(TableModelListener theListener : myListeners){
      theListener.tableChanged(new TableModelEvent(this, aRow));
    }
  }

  public static class Entry{
    public final String myHost;
    public final int myPort;
    public final String myPeer;
    public final State myState;

    public static enum State{RUNNING, DOWN}

    public Entry(String anHost, int anPort, String anPeer, State anState) {
      super();
      myHost = anHost;
      myPort = anPort;
      myPeer = anPeer;
      myState = anState;
    };


  }

  @Override
  public void addTableModelListener(TableModelListener anL) {
    myListeners.add(anL);
  }


  @Override
  public Class<?> getColumnClass(int anColumnIndex) {
    return String.class;
  }


  @Override
  public int getColumnCount() {
    return 3;
  }


  @Override
  public String getColumnName(int anColumnIndex) {
    if(anColumnIndex == 0) return "Port";
    if(anColumnIndex == 1) return "Peer Id";
    if(anColumnIndex == 2) return "State";
    return "";
  }


  @Override
  public int getRowCount() {
    return myRange;
  }


  @Override
  public Object getValueAt(int anRowIndex, int anColumnIndex) {
    Entry theEntry = myPeerIdsAtPort.get(anRowIndex);
    if(anColumnIndex == 0) return theEntry.myPort;
    if(anColumnIndex == 1) return theEntry.myPeer;
    if(anColumnIndex == 2) return theEntry.myState.name();
    return null;
  }


  @Override
  public boolean isCellEditable(int anRowIndex, int anColumnIndex) {
    return false;
  }


  @Override
  public void removeTableModelListener(TableModelListener anL) {
    myListeners.remove(anL);
  }


  @Override
  public void setValueAt(Object aValue, int anRowIndex, int anColumnIndex) {
  }

  public List<Entry> getPeerIdsAtPort() {
    return Collections.unmodifiableList(myPeerIdsAtPort);
  }



}
