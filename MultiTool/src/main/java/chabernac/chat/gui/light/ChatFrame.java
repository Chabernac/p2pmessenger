/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.light;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.chat.gui.ChatMediator;
import chabernac.chat.gui.ExitChoiseDialog;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.gui.SavedFrame;
import chabernac.messengerservice.MessengerClientService;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.event.ApplicationCloseEvent;
import chabernac.task.event.ApplicationSaveEvent;

public class ChatFrame extends SavedFrame{
  private static Logger logger = Logger.getLogger(ChatFrame.class);
  private ChatMediator myMediator = null;
  
  public ChatFrame(){
    super(ApplicationPreferences.getInstance().getProperty("frame.light.title","Chatterke"), new Rectangle(300,300,500,250));
    loadIcon();
    init();
    buildGUI();
    addListeners();
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
  
  private void init(){
    myMediator = new LightChatMediator((MessengerClientService)ApplicationRefBase.getObject(ApplicationRefBase.MESSENGERSERVICE), this);
  }
  
  private void buildGUI(){
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(myMediator.getChatPanel(), BorderLayout.CENTER);
  }
  
  private void addListeners(){
    addWindowListener(new MyWindowListener());
  }
  
  protected String getFrameName() {
    return "light";
  }
  
  private class MyWindowListener extends WindowAdapter{
    public synchronized void windowClosing(WindowEvent e) {
      ApplicationEventDispatcher.fireEvent(new ApplicationSaveEvent());
      int theChoice = ExitChoiseDialog.exitApplication(ChatFrame.this, 5);
      if(theChoice == ExitChoiseDialog.Choices.EXIT){
        ApplicationEventDispatcher.fireEvent(new ApplicationCloseEvent());
      } else if(theChoice == ExitChoiseDialog.Choices.HIDE){
        setVisible(false);
      }
    }
  }
}
