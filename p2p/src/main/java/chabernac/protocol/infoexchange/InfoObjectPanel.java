/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.infoexchange;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class InfoObjectPanel extends JPanel {
  private static final long serialVersionUID = -5309082469848814691L;
  private final InfoExchangeProtocol<InfoObject> myInfoExchangeProtocol;
  private JTabbedPane myCurrentPane = null;
  
  public InfoObjectPanel(InfoExchangeProtocol<InfoObject> anInfoExchangeProtocol){
    myInfoExchangeProtocol = anInfoExchangeProtocol;
    anInfoExchangeProtocol.addInfoListener( new MyInfoExchangeListener() );
    rebuild();
  }
  
  private void rebuild(){
    SwingUtilities.invokeLater( new Runnable(){
      public void run(){
        Map<String, InfoObject> theInfoObject = myInfoExchangeProtocol.getInfoMap();
        
        if(myCurrentPane != null){
          remove( myCurrentPane );
        }
        myCurrentPane = new JTabbedPane();
        
        for(String thePeer : theInfoObject.keySet()){
          InfoObject theObject = theInfoObject.get( thePeer );
          JTextArea theArea = new JTextArea();
          theArea.setEditable( false );
          theArea.setText( theObject.toString() );
          myCurrentPane.addTab( thePeer,  new JScrollPane(theArea));
        }
        
        setLayout( new BorderLayout() );
        add(myCurrentPane, BorderLayout.CENTER);
      }
    });
  }
  
  public class MyInfoExchangeListener implements iInfoListener< InfoObject > {
    @Override
    public void infoChanged( String aPeerId, Map< String, InfoObject > aInfoMap ) {
      rebuild();
    }
  }
}
