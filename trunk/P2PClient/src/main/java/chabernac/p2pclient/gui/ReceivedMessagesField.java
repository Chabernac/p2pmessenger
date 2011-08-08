/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.p2pclient.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

import chabernac.gui.GPanel;
import chabernac.protocol.asyncfiletransfer.AcceptFileResponse;
import chabernac.protocol.asyncfiletransfer.AcceptFileResponse.Response;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iDeliverReportListener;
import chabernac.protocol.message.iMultiPeerMessageListener;
import chabernac.tools.HTMLTools;
import chabernac.tools.SmileyTools;
import chabernac.tools.Tools;

public class ReceivedMessagesField extends GPanel implements iReceivedMessagesProvider{
  private static final long serialVersionUID = 6978782838453325145L;
  private static Logger logger = Logger.getLogger(ReceivedMessagesField.class);
  private static final String SEND_FONT = "style='font-family:arial;font-size:10;color:#0000AA;'";
  private static final String RECEIVE_FONT = "style='font-family:arial;font-size:10;color:#000000;'";
  private static final String ERROR_FONT = "style='font-family:arial;font-size:10;color:#AA0022;'";
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

  private ChatMediator myMediator = null;
  private JTextPane myPane = null;
  private String myHTML = "";

  public ReceivedMessagesField(ChatMediator aMediator) throws P2PFacadeException{
    myMediator = aMediator;
    init();
    buildGUI();
    addListener();
  }

  private void addListener() throws P2PFacadeException {
    MessageChangeListener theListener = new MessageChangeListener();
    myMediator.getP2PFacade().addMessageListener( theListener );
    myMediator.getP2PFacade().addDeliveryReportListener(  theListener );
  }

  private void init(){
    myPane = new JTextPane();
    myPane.setEditable(false);
    myPane.setContentType("text/html");
    myPane.setDragEnabled(true);
    myPane.addHyperlinkListener(new HyperlinkActivator());
    //    myPane.setDocument( new LongWordWrappingDocumentDecorator( (StyledDocument)myPane.getDocument() ) );
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    JScrollPane theScrollPane = new JScrollPane(myPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    theScrollPane.getVerticalScrollBar().setUnitIncrement(50);
    add(theScrollPane, BorderLayout.CENTER);
    new ReceivedMessagesPopup(this, myMediator);
  }


  public JEditorPane getPane(){
    return myPane;
  }

  public class HyperlinkActivator implements HyperlinkListener{ 
    public void hyperlinkUpdate(HyperlinkEvent e){ 
      if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
        if(e.getDescription().startsWith( "download:")){
          activateDownload(e.getDescription());
        } else {
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

    private void activateDownload(String aURL){
      String[] theParts = aURL.split( ":" );
      String theTransferId = theParts[1];
      String theFileName = theParts[2];

      JFileChooser theChooser = new JFileChooser(theFileName);
      int theReturn = theChooser.showOpenDialog( ReceivedMessagesField.this );
      try {
        if(theReturn == JFileChooser.APPROVE_OPTION){
          myMediator.getP2PFacade().showFileTransferOverView();
          myMediator.getP2PFacade().getAsyncFileTransferController().setFileTransferResponse( new AcceptFileResponse( theTransferId, Response.ACCEPT, theChooser.getSelectedFile() ) );
        } else {
          myMediator.getP2PFacade().getAsyncFileTransferController().setFileTransferResponse( new AcceptFileResponse( theTransferId, Response.REFUSED, null ) );
        }
      } catch ( Exception e ) {
        logger.error( "An error occured while setting accept file response", e);
      }
    }
  }

  private void createHTML() {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        try{
          Set< MultiPeerMessage > theList = myMediator.getP2PFacade().getMessageArchive().getAllMessages();
          myHTML = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">";
          boolean send = false;
          myHTML += "<html>";
          myHTML += "<head><style type=\"text/css\">p {   padding-left: 5em;  } p:first-letter  {   margin-left: -5em;  }</style><title>messages</title></head>";
          myHTML += "<body>";
          myHTML += "<table border='0' cellpadding='0' cellspacing='0'>";
          for(MultiPeerMessage theMessage : theList){
            send = theMessage.getSource().equals(myMediator.getP2PFacade().getPeerId());
            myHTML += "<tr "+ (send ? SEND_FONT : RECEIVE_FONT) + " >";
            myHTML += "<td><i>" +  TIME_FORMAT.format(theMessage.getCreationTime()) + " " + Tools.getEnvelop(myMediator.getP2PFacade(), theMessage) + ":</i> ";
            myHTML += HTMLTools.detectLinksInTextAndMakeHref(SmileyTools.replaceSmileys(theMessage.getMessage()));
            myHTML += "</td>";
            myHTML += "</tr>";
          }
          myHTML += "</table></body></html>";
          System.out.println(myHTML);
          myPane.setText(myHTML);
          myPane.setCaretPosition(myPane.getDocument().getLength() > 0 ? myPane.getDocument().getLength() - 1 : 0);
        }catch(P2PFacadeException e){
          logger.error( "Could not create received text", e );
        } 
      }
    });

  }


  private class MessageChangeListener implements iMultiPeerMessageListener, iDeliverReportListener {
    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      createHTML();
    }

    @Override
    public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
      createHTML();
    }
  }

  @Override
  public void clear() {
    myPane.setText( "" );
  }

  @Override
  public String getHTML() {
    return myHTML;
  }
}
