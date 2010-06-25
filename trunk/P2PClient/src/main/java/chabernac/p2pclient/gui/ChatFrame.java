/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.p2pclient.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import chabernac.gui.ReceivedMessagesField;
import chabernac.gui.SavedFrame;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class ChatFrame extends SavedFrame implements iTitleProvider, isShowDialogProvider{
  private static final long serialVersionUID = 8845601746540726343L;
  private static Logger logger = Logger.getLogger(ChatFrame.class);
  private ChatMediator myMediator = null;
  private P2PFacade myP2PFacade = null;
  private JSplitPane mySplitPane = null;
  
  public ChatFrame(P2PFacade aFacade) throws P2PFacadeException{
    super(ApplicationPreferences.getInstance().getProperty("frame.light.title","Chatterke"), new Rectangle(300,300,500,250));
    myP2PFacade = aFacade;
    init();
    loadIcon();
    buildGUI();
    addWindowListener();
  }
  
  private void addWindowListener() {
    addWindowListener(new MyWindowListener());
  }

  private void loadIcon(){
    String theIconLocation = ApplicationPreferences.getInstance().getProperty("frame.light.icon");
    if(theIconLocation != null){
      try {
        setIconImage(ImageIO.read(new File(theIconLocation)));
      } catch (IOException e) {
        logger.error("Could not load icon from location: " + theIconLocation, e);
      }
    }
  }
  
  private void init() throws P2PFacadeException{
    myMediator = new ChatMediator(myP2PFacade);
  }
  
  private void buildGUI() throws P2PFacadeException{
    setLayout(new BorderLayout());

    mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    MessageField theMessageField = new MessageField(myMediator);
    UserPanel theUserPanel = new UserPanel(myMediator);
    ReceivedMessagesField theReceivedField = new ReceivedMessagesField(myMediator);
    
    myMediator.setMessageProvider( theMessageField );
    myMediator.setReceivedMessagesProvider( theReceivedField );
    myMediator.setUserSelectionProvider( theUserPanel );
    myMediator.setTitleProvider( this );
    myMediator.setIsShowDialogProvider( this );
    
    mySplitPane.setTopComponent(new JScrollPane(theMessageField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    mySplitPane.setBottomComponent(theReceivedField);
    mySplitPane.setDividerSize(1);
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    mySplitPane.setDividerLocation(Integer.parseInt(thePreferences.getProperty("chat.light.dividerlocation", "80")));

    add(mySplitPane, BorderLayout.CENTER);
    add(theUserPanel, BorderLayout.SOUTH);
  }
  
  protected String getFrameName() {
    return "light";
  }
  
  private void savePreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    thePreferences.setProperty("chat.light.dividerlocation", Integer.toString(mySplitPane.getDividerLocation()));
  }
  
  public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing( WindowEvent anEvent ) {
      setVisible( false );
      myMediator.getP2PFacade().stop();
      savePreferences();
      ApplicationPreferences.getInstance().save();
      System.exit( 0 );
    }
  }

  @Override
  public boolean isShowDialog() {
    return !isActive();
  }

}
