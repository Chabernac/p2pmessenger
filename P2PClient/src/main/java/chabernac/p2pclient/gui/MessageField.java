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
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;
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

import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.p2pclient.gui.action.ActionDecorator;


public class MessageField extends JTextArea implements iMessageProvider, iAttachementProvider{
  private static final long serialVersionUID = 8085069557786155534L;

  private static Logger logger = Logger.getLogger(MessageField.class);

  private ChatMediator myMediator = null;
  private List<File> myAttachments = null;
  private TitledBorder myBorder = null;

  public MessageField(ChatMediator aMediator){
    myMediator = aMediator;
    createInputMap();
    buildGUI();
    initDropTarget();
  }

  private void createInputMap(){
    new ActionDecorator(this, myMediator).decorate(JComponent.WHEN_FOCUSED);
    
    InputMap theInputMap = getInputMap();

    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK), "nextrow");
    ActionMap theActionMap = getActionMap();
    theActionMap.put("nextrow", new NextRowAction());
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
    if(myAttachments == null) myAttachments = new ArrayList<File>();
    //DataFile theDataFile = DataFile.loadFromFile(aFile); 
    myAttachments.add(aFile);
    showAttachment(aFile.getName());
  }

  public List<File> getAttachments(){
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

  }

  private class MessageSendListener implements iEventListener{
    public void eventFired(Event anEvt) {
      clear();
    }

  }

  private class PreviousAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.restorePreviousMessage();
    }
  }


  private class NextAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.restoreNextMesssage();
    }
  }

  private class FirstAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.restoreFirstMessage();
    }
  }

  private class LastAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.restoreLastMessage();
    }
  }

  private class DeleteAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.deleteCurrentMessage();      
    }
  }

  private class ClearAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.clear();
    }
  }

  private class ClearUsersAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.clearAll();
    }
  }

  private class SendAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.send();
    }
  }

  private class NextRowAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      setText(getText() + "<br>\n");
    }
  }

  private class ReplyAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.selectReplyUsers();
    }
  }

  private class ReplyAllAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.selectReplyAllUsers();
    }
  }

  private class UnlockAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      EventQueue theEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    }
  }
  
  private class PauseAction extends AbstractAction{
    public void actionPerformed(ActionEvent evt){
      myMediator.setShowDialog(!myMediator.isShowDialog());
    }
  }

  private class MyMouseWheelListener implements MouseWheelListener{

    public void mouseWheelMoved(MouseWheelEvent anEvent) {
      int theNumber = anEvent.getWheelRotation();
      while(theNumber != 0){
        if(theNumber < 0) {
          myMediator.restorePreviousMessage();
          theNumber++;
        }
        if(theNumber > 0) {
          myMediator.restoreNextMesssage();
          theNumber --;
        }
      }
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

  @Override
  public String getMessage() {
    return getText();
  }

  @Override
  public String getMessageTitle() {
    return myBorder.getTitle();
  }

  @Override
  public void setMessage( String aMessage ) {
    setText( aMessage );
  }

  @Override
  public void setMessageTitle( String aMessage ) {
    myBorder.setTitle( aMessage );
    repaint();
  }
}
