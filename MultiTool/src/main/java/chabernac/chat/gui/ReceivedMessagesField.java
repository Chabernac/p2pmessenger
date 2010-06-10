/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.GPanel;
import chabernac.messengerservice.event.ReceivedMessagesUpdatedEvent;
import chabernac.util.Tools;

public class ReceivedMessagesField extends GPanel {
  private static Logger logger = Logger.getLogger(ReceivedMessagesField.class);
  private static final String SEND_FONT = "style='font-family:arial;font-size:10;color:#0000AA;'";
  private static final String RECEIVE_FONT = "style='font-family:arial;font-size:10;color:#000000;'";
  private static final String ERROR_FONT = "style='font-family:arial;font-size:10;color:#AA0022;'";
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

  private ChatMediator myMediator = null;
  private JEditorPane myPane = null;

  public ReceivedMessagesField(ChatMediator aMediator){
    myMediator = aMediator;
    init();
    buildGUI();
    addListener();
  }

  private void init(){
    myPane = new JEditorPane();
    myPane.setEditable(false);
    myPane.setContentType("text/html");
    myPane.setDragEnabled(true);
    myPane.addHyperlinkListener(new HyperlinkActivator());
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    JScrollPane theScrollPane = new JScrollPane(myPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    theScrollPane.getVerticalScrollBar().setUnitIncrement(50);
    add(theScrollPane, BorderLayout.CENTER);
    new ReceivedMessagesPopup(this, myMediator.getMessageArchive());
  }


  private void addListener(){
    ApplicationEventDispatcher.addListener(new ReceveivedMessagesUpdatedEventListener(), ReceivedMessagesUpdatedEvent.class);
    //myMediator.getMessageArchive().addObserver(new MessageObserver());
  }

  public JEditorPane getPane(){
    return myPane;
  }

  private class ReceveivedMessagesUpdatedEventListener implements iEventListener{

    public void eventFired(Event anEvt) {
     
      try{
        //if( arg.equals(MessageArchive.MESSAGE_RECEIVED) || arg.equals(MessageArchive.RECEIVED_MESSAGES_CLEARED) || o instanceof DefaultObservable){
          ArrayList theList = myMediator.getMessageArchive().getReceivedMessages();
          String theText = "";
          boolean send = false;
          theText += "<html>";
          theText += "<head><style type=\"text/css\">p {   padding-left: 5em;  } p:first-letter  {   margin-left: -5em;  }</style></head>";
          theText += "<table border='0' cellpadding='0' cellspacing='0'>";
          for(int i=0;i<theList.size();i++){
            Message theMessage= (Message)theList.get(i);
            if(!theMessage.isTechnicalMessage()){
              send = theMessage.getFrom().equals(myMediator.getMessengerClientService().getUser().getUserName());
              theText += "<tr "+ (send ? SEND_FONT : RECEIVE_FONT) + " >";
              theText += "<td><i>" +  TIME_FORMAT.format(new Date(theMessage.getSendTime())) + " " + Tools.getEnvelop(myMediator.getUserList(), theMessage) + ":</i> ";
              theText += theMessage.getMessage();
              if(theMessage.getAttachments() != null){
                for(Iterator j=theMessage.getAttachments().iterator();j.hasNext();){
                  Object theObject = j.next();
                  if(theObject instanceof File){
                    File theFile = (File)theObject;
                    String theURL = "file:///" + theFile.getAbsolutePath().replace('\\', '/');
                    theText += "<br><a href='" + theURL + "'>";
                    if(Tools.isImage(theFile)){
                      theText += "<img border='0' src='" + theURL + "'>";
                    } else {
                      theText +=  theFile.getAbsolutePath();
                    }
                    theText += "</a>";
                  }
                }
              }
              theText += "</td>";
              theText += "</tr>";
            }
          }

//          if(theMessage.getFrom().equalsIgnoreCase(myMediator.getChatModel().whoAmI())){
//          theMessage.addObserver(this);
//          }
          //}

          /*
          if( o instanceof DefaultObservable ){
            Message theMessage = (Message)((DefaultObservable)o).getTarget();
            theText += "<tr " + ERROR_FONT + "><td>Uw bericht van ";
            theText += TIME_FORMAT.format(new Date(theMessage.getSendTime()));
            theText += " kon niet afgeleverd worden aan ";
            ArrayList to = theMessage.getTo();
            for(int i=0;i<to.size();i++){
              String who = (String)to.get(i);
              if(theMessage.getStatus(who).equalsIgnoreCase(Message.FAILED)){
                theText += myMediator.getMessengerClientService().getUser().getUserName() + ", ";
              }
            }
            theText +="</td></tr>";
          }
          */

          theText += "</table></html>";
          //Logger.log(this,"Html text: " + theText);
          myPane.setText(theText);
          myPane.setCaretPosition(myPane.getDocument().getLength() > 0 ? myPane.getDocument().getLength() - 1 : 0);
      } catch(RemoteException e){
        logger.error("An error occured while creating table", e);
      }

    }

 
  }

  public class HyperlinkActivator implements HyperlinkListener{ 
    public void hyperlinkUpdate(HyperlinkEvent e){ 
      if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
        String theURL = e.getURL().toString().replaceAll("file:/", "");
        logger.debug("URL: " + theURL);
        try{
          String theCMD = "";
          if(theURL.startsWith("http")){
            theCMD = "cmd /c start " + theURL;  
          } else {
            theCMD = "cmd /c \"" + theURL + "\"";
          }
          logger.debug("Cmd: " + theCMD);
          Runtime.getRuntime().exec(theCMD);
        }catch(IOException f){
          logger.error("Could not show url", f);
        }
      } 
    } 
  } 
}
