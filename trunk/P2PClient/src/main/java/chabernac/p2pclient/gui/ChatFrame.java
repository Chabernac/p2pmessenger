/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.p2pclient.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import chabernac.events.EventDispatcher;
import chabernac.gui.SavedFrame;
import chabernac.gui.event.FocusGainedEvent;
import chabernac.gui.event.FocusLostEvent;
import chabernac.p2pclient.gui.action.ActionDecorator;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;
import chabernac.preference.iApplicationPreferenceListener;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.tools.Tools;

public class ChatFrame extends SavedFrame implements iTitleProvider, isShowDialogProvider, iChatFrame{
  private static final long serialVersionUID = 8845601746540726343L;
  private static Logger logger = Logger.getLogger(ChatFrame.class);
  private ChatMediator myMediator = null;
  private JSplitPane mySplitPane = null;
  private MessageField myMessageField = null;
  
  public ChatFrame(ChatMediator aMediator) throws P2PFacadeException{
    super(ApplicationPreferences.getInstance().getProperty("frame.light.title","Chatterke"), new Rectangle(300,300,500,250));
    myMediator = aMediator;
    loadIcon();
    buildGUI();
    addListeners();
    createInputMap();
  }
  
  private void createInputMap(){
    new ActionDecorator(mySplitPane, myMediator).decorate(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  private void addListeners() throws P2PFacadeException {
    MyWindowListener theWindowListener = new MyWindowListener();
    addWindowListener(theWindowListener);
    addWindowFocusListener(theWindowListener);
    addFocusListener(new MyFocusListener());
    myMediator.getUserSelectionProvider().addSelectionChangedListener( new UserSelectionChangedListener() );
    ApplicationPreferences.getInstance().addApplicationPreferenceListener( new ApplicationPreferenceListener() );
    myMediator.getP2PFacade().getPersonalInfo().addObserver( new UserInfoObserver() );
    
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
  
  public ChatMediator getMediator(){
    return myMediator;
  }
  
  private void buildGUI() throws P2PFacadeException{
    setLayout(new BorderLayout());

    mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    myMessageField = new MessageField(myMediator);
    UserPanel theUserPanel = new UserPanel(myMediator);
    ReceivedMessagesField theReceivedField = new ReceivedMessagesField(myMediator);
    
    myMediator.setMessageProvider( myMessageField );
    myMediator.setAttachementProvider( myMessageField );
    myMediator.setReceivedMessagesProvider( theReceivedField );
    myMediator.setUserSelectionProvider( theUserPanel );
    myMediator.setTitleProvider( this );
    myMediator.setIsShowDialogProvider( this );
    myMediator.setChatFrame( this );
//    myMediator.setTitle();
    myMediator.setPopupMessage();
    
    mySplitPane.setTopComponent(new JScrollPane(myMessageField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
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
  
  private void setTitle(){
    Set<String> theUsers = myMediator.getUserSelectionProvider().getSelectedUsers();
    String theTitle = ApplicationPreferences.getInstance().getProperty("frame.light.title","Chatterke");
    try{
      theTitle += " [" + myMediator.getP2PFacade().getPersonalInfo().getStatus().name();
      
      if(myMediator.getP2PFacade().getPersonalInfo().getStatusMessage() != null && !myMediator.getP2PFacade().getPersonalInfo().getStatusMessage().equals( "" )){
        theTitle += " " + myMediator.getP2PFacade().getPersonalInfo().getStatusMessage();
      }
      
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP )){
        theTitle += " - popup geblokkeerd";
      } else if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.CLOSED )){
        theTitle += " - ontvang gesloten";
      }
      theTitle += "]";
      if(theUsers.size() == 1){
        theTitle += " - sc " + Tools.getShortNameForUser( myMediator.getP2PFacade().getUserInfo().get( theUsers.iterator().next() )); 
      } else if(theUsers.size() > 1){
        theTitle += " - mc ";
      }
    }catch(P2PFacadeException e){
      logger.error( "Could not set title", e );
    }
    setTitle( theTitle );
  }
  
  public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing( WindowEvent anEvent ) {
      setVisible( false );
      savePreferences();
      ApplicationPreferences.getInstance().save();
//      myMediator.getP2PFacade().stop();
//      ApplicationPreferences.getInstance().save();
//      System.exit( 0 );
    }
    
    public void windowGainedFocus(WindowEvent aWindowEvent){
      EventDispatcher.getInstance(FocusGainedEvent.class).fireEvent(new FocusGainedEvent(ChatFrame.this));
    }
  } 

  @Override
  public boolean isShowDialog() {
    return !isActive();
  }
  
  public void requestFocus(){
    myMessageField.requestFocus();
  }
  
  public void showFrame(){
    SwingUtilities.invokeLater( new Runnable(){
      public void run(){
        setVisible( true );
        setState( Frame.NORMAL );
        toFront();
        requestFocus();  
      }
    });
      
    NewMessageDialog5.getInstance( getMediator() ).cancelPendingTasks();
    NewMessageDialog5.getInstance( getMediator() ).setVisible( false );
  }
  
  public class MyFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent anArg0) {
      EventDispatcher.getInstance(FocusGainedEvent.class).fireEvent(new FocusGainedEvent(ChatFrame.this));
    }

    @Override
    public void focusLost(FocusEvent anArg0) {
      EventDispatcher.getInstance(FocusLostEvent.class).fireEvent(new FocusLostEvent(ChatFrame.this));
    }
  }
  
  public class UserSelectionChangedListener implements iSelectionChangedListener {
    @Override
    public void selectionChanged() {
      setTitle();
    }
  }
  
  public class ApplicationPreferenceListener implements iApplicationPreferenceListener {
    @Override
    public void applicationPreferenceChanged( String aKey, String aValue ) {
      setTitle();
    }

    @Override
    public void applicationPreferenceChanged( Enum anEnumValue ) {
      setTitle();
    }
  }
  
  public class UserInfoObserver implements Observer {
    @Override
    public void update( Observable anO, Object anArg ) {
      setTitle();
    }

  }
}
