/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.p2pclient.gui;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.chat.gui.event.SelectUsersEvent;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.messengerservice.event.MessageSelectedEvent;
import chabernac.messengerservice.event.MessageSendEvent;


public class MessageField extends JTextArea{
  private static Logger logger = Logger.getLogger(MessageField.class);

  private ChatMediator myMediator = null;
  private ArrayList myAttachments = null;
  private TitledBorder myBorder = null;

  public MessageField(ChatMediator aMediator){
    myMediator = aMediator;
    createInputMap();
    buildGUI();
    initDropTarget();
    addListener();
  }

  private void createInputMap(){
    InputMap theInputMap = getInputMap();

    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "previous");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "next");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.CTRL_DOWN_MASK), "unlock");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.ALT_DOWN_MASK), "last");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_DOWN_MASK), "first");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.ALT_DOWN_MASK), "delete");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK), "nextrow");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK), "replyall");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_DOWN_MASK), "reply");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.SHIFT_DOWN_MASK), "clearusers");
    ActionMap theActionMap = getActionMap();
    theActionMap.put("previous", new PreviousAction());
    theActionMap.put("next", new NextAction());
    theActionMap.put("clear", new ClearAction());
    theActionMap.put("clearusers", new ClearUsersAction());
    theActionMap.put("send", new SendAction());
    theActionMap.put("nextrow", new NextRowAction());
    theActionMap.put("first", new FirstAction());
    theActionMap.put("last", new LastAction());
    theActionMap.put("delete", new DeleteAction());
    theActionMap.put("reply", new ReplyAction());
    theActionMap.put("replyall", new ReplyAllAction());
    theActionMap.put("unlock", new UnlockAction());

  }

  private void buildGUI(){
    setWrapStyleWord(true);
    setLineWrap(true);
    myBorder = new TitledBorder("Nieuw bericht");
    setBorder(myBorder);
  }

  private void initDropTarget(){
    setDragEnabled(true);
    setTransferHandler(new NewFileTransferHandler());
  }

  private void showAttachment(String aFileName){
    setText(getText() + " [" + aFileName + "] ");
  }

  private void addAttachment(File aFile){
    if(myAttachments == null) myAttachments = new ArrayList();
    //DataFile theDataFile = DataFile.loadFromFile(aFile); 
    myAttachments.add(aFile);
    showAttachment(aFile.getName());
  }

  public ArrayList getAttachments(){
    return myAttachments;
  }

  public void clear(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        setText("");
        myBorder.setTitle("Nieuw bericht");
        myAttachments = null;
        repaint();
      }
    });
    
//    requestFocus();
//    System.out.println(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
  }

  private void addListener(){
    //myMediator.getMessageArchive().addObserver(new MessageObserver());
    ApplicationEventDispatcher.addListener(new MessageSendListener(), MessageSendEvent.class);
    ApplicationEventDispatcher.addListener(new MessageSelectedListener(), MessageSelectedEvent.class);
    addMouseWheelListener(new MyMouseWheelListener());
  }
  
  private class MessageSendListener implements iEventListener{
    public void eventFired(Event anEvt) {
      clear();
    }
    
  }

  private class PreviousAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      previous();
    }
  }
  
  private void previous(){
    myMediator.save();
    myMediator.getMessageArchive().previous();
  }

  private class NextAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      next();
    }
  }
  
  private void next(){
    myMediator.save();
    myMediator.getMessageArchive().next();
    if(myMediator.getMessageArchive().getSelectedMessage() == null){
      selectUsersFromLastMessage();
    }
  }
  
  private void selectUsersFromLastMessage(){
    ArrayList theSendMessages = myMediator.getMessageArchive().getSendMessages();
    if(theSendMessages.size() > 0){
      Message theMessage = (Message)theSendMessages.get(theSendMessages.size() - 1);
      ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(theMessage.getTo()));
    }
  }

  private class FirstAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.save();
      myMediator.getMessageArchive().setSelectedMessage(0);
    }
  }

  private class LastAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.save();
      myMediator.getMessageArchive().setSelectedMessage(myMediator.getMessageArchive().getLastMessageSend());
      if(myMediator.getMessageArchive().getSelectedMessage() == null){
        selectUsersFromLastMessage();
      }
    }
  }

  private class DeleteAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.getMessageArchive().removeMessage(myMediator.getMessageArchive().getSelectedMessage());      
    }
  }

  private class ClearAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      if(getText().trim().equals("")){
        myMediator.getMessageArchive().setSelectedMessage(null);
        //myMediator.getUserListPanel().clear();
      } else {
        clear();
      }
    }
  }

  private class ClearUsersAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.getUserListPanel().clear();
    }
  }

  private class SendAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      try {
        myMediator.send();
      } catch (RemoteException e) {
        logger.error("An error occured while sending message", e);
      }
    }
  }

  private class NextRowAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      setText(getText() + "<br>\n");
    }
  }

  private class ReplyAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      try{
        Message theLastMessage = myMediator.getMessageArchive().getLastMessageReceivedFromOther();
        if(theLastMessage != null){
          ArrayList replyTo = new ArrayList();
          replyTo.add(theLastMessage.getFrom());
          ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(replyTo));
        }
      }catch(RemoteException e){
        logger.error("An error occured in reply all action", e);
      }

    }
  }

  private class ReplyAllAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      try{
        Message theLastMessage = myMediator.getMessageArchive().getLastMessageReceivedFromOther();
        if(theLastMessage != null){
          Message theReplyMessage = theLastMessage.replyAll(myMediator.getMessengerClientService().getUser().getUserName());
          ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(theReplyMessage.getTo()));
        }
      }catch(RemoteException e){
        logger.error("An error occured in reply all action", e);
      }
    }
  }
  
  private class UnlockAction extends AbstractAction{
	  public void actionPerformed(ActionEvent evt){
		 EventQueue theEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
	  }
  }
  
  private class MyMouseWheelListener implements MouseWheelListener{

    public void mouseWheelMoved(MouseWheelEvent anEvent) {
      int theNumber = anEvent.getWheelRotation();
      while(theNumber != 0){
        if(theNumber < 0) {
          previous();
          theNumber++;
        }
        if(theNumber > 0) {
          next();
          theNumber --;
        }
      }
    }
    
  }


  private class MessageSelectedListener implements iEventListener{
    

    public void eventFired(Event anEvt) {
        Message theMessage = ((MessageSelectedEvent)anEvt).getMessage();
        if(theMessage != null){
          setText(theMessage.getMessage());
          myAttachments = theMessage.getAttachments();
          String theTitle = (myMediator.getMessageArchive().getSendMessages().indexOf(theMessage) + 1) + "/" + myMediator.getMessageArchive().getSendMessages().size();
          theTitle += theMessage.isSend() ? " - verstuurd" : " - niet verstuurd";
          myBorder.setTitle(theTitle);
        } else {
          clear();
          //setText("");
          //myBorder.setTitle("Nieuw bericht");
        }
        repaint();
      }
  }

  private class NewFileTransferHandler extends TransferHandler implements  UIResource {
    public void exportToClipboard(JComponent comp, Clipboard clipboard,
        int action) {
      if (comp instanceof JTextComponent) {
        JTextComponent text = (JTextComponent)comp;
        int p0 = text.getSelectionStart();
        int p1 = text.getSelectionEnd();
        if (p0 != p1) {
          try {
            Document doc = text.getDocument();
            String srcData = doc.getText(p0, p1 - p0);
            StringSelection contents =new StringSelection(srcData);
            clipboard.setContents(contents, null);
            if (action == TransferHandler.MOVE) {
              doc.remove(p0, p1 - p0);
            }
          } catch (BadLocationException ble) {}
        }
      }
    }
    public boolean importData(JComponent comp, Transferable t) {
      if (comp instanceof JTextComponent) {
        DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

        if (flavor != null) {
          InputContext ic = comp.getInputContext();
          if (ic != null) {
            ic.endComposition();
          }
          try {
            String data = (String)t.getTransferData(flavor);

            ((JTextComponent)comp).replaceSelection(data);
            return true;
          } catch (UnsupportedFlavorException ufe) {
          } catch (IOException ioe) {
          }
        }
      }

      DataFlavor[] theDataFlavors = t.getTransferDataFlavors();
      try {
        for(int i=0;i<theDataFlavors.length;i++){
          if(theDataFlavors[i].equals(DataFlavor.javaFileListFlavor)){

            List theList = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
            for(int j=0;j<theList.size();j++){
              addAttachment((File)theList.get(j));
            }

          } 
        }
      } catch (UnsupportedFlavorException e) {
        logger.error("Invalid drop item", e);
        return false;
      } catch (IOException e) {
        logger.error("Invalid drop item", e);
        return false;
      }



      return false;
    }

    public boolean canImport(JComponent comp,
        DataFlavor[] transferFlavors) {
      JTextComponent c = (JTextComponent)comp;
      if (!(c.isEditable() && c.isEnabled())) {
        return false;
      }

      for(int i=0;i<transferFlavors.length;i++){
        logger.debug(transferFlavors[i].getClass().toString());
        if(transferFlavors[i].equals(DataFlavor.javaFileListFlavor)) {
          return true;
        }
      }

      return (getFlavor(transferFlavors) != null);
    }

    public int getSourceActions(JComponent c) {
      return NONE;
    }

    private DataFlavor getFlavor(DataFlavor[] flavors) {
      if (flavors != null) {
        for (int counter = 0; counter < flavors.length; counter++) {
          if (flavors[counter].equals(DataFlavor.stringFlavor)) {
            return flavors[counter];
          }
        }
      }
      return null;
    }

  }


}
