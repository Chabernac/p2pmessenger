/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.debug;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import chabernac.chart.Data;
import chabernac.chart.LineChart;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class ChartsPanel extends JPanel {
  private static final long serialVersionUID = 8719296534435190678L;
  private static final Logger LOGGER = Logger.getLogger(ChartsPanel.class);
  private final ProtocolContainer myProtocolContainer;


  private final Map<String, Data> myPeerData = new HashMap<String, Data>();

  public ChartsPanel( ProtocolContainer aProtocolContainer ) {
    super();
    myProtocolContainer = aProtocolContainer;
    init();
  }

  private void init(){
    addListeners();
    startExecutor();
  }

  private void addListeners(){
    ChartsListener theListener = new ChartsListener();
    try{
      getRoutingTable().addRoutingTableListener( theListener );
    }catch(Exception e){
      LOGGER.error("Error occured while adding listener", e);
    }
  }
  
  private void startExecutor(){
    Executors.newScheduledThreadPool( 1 ).scheduleAtFixedRate( new Runnable(){
      public void run(){
        refresh();
      }
    }, 30, 30, TimeUnit.SECONDS);
  }

  public void paint(Graphics g){
    LineChart theLineChart = new LineChart(getWidth(), getHeight());

    for(Data theData : myPeerData.values()){
      theLineChart.addData( theData );
    }
    theLineChart.paint( g );
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)myProtocolContainer.getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  private void addPeerSnapshot(){
    try{
      RoutingTable theTable = getRoutingTable();
      for(RoutingTableEntry theEntry : theTable.getEntries()){
        if(!myPeerData.containsKey( theEntry.getPeer().getPeerId() )){
          myPeerData.put( theEntry.getPeer().getPeerId(), new Data(theEntry.getPeer().getPeerId()) );
        }

        Data thePeerData = myPeerData.get( theEntry.getPeer().getPeerId() );
        thePeerData.addValue( System.currentTimeMillis(), theEntry.getHopDistance() );
      }
    }catch(Exception e){
      LOGGER.error("An error occured while adding peer snapshot", e);
    }
  }
  
  private void refresh(){
    addPeerSnapshot();
    repaint();
  }


  public class ChartsListener implements IRoutingTableListener {

    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      refresh();
    }

    @Override
    public void routingTableEntryRemoved( RoutingTableEntry anEntry ) {
      refresh();
    }
  }


}
