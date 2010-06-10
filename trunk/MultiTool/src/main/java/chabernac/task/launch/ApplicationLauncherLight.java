package chabernac.task.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.application.RefBase;
import chabernac.chat.Message;
import chabernac.chat.gui.UserNameDialog;
import chabernac.chat.gui.event.UserAlreadyLoggedOnEvent;
import chabernac.chat.gui.light.ChatFrame;
import chabernac.distributionservice.DistributionService;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.iMessengerClientService;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.event.ApplicationCloseEvent;
import chabernac.updater.Updater;
import chabernac.updater.iApplication;
import chabernac.util.OptionPaneStatusDispatcher;
import chabernac.util.Tools;
import chabernac.utils.ServiceTools;

public class ApplicationLauncherLight implements iEventListener, Runnable, iApplication{
  private static Logger logger = Logger.getLogger(ApplicationLauncherLight.class);

  private boolean stop = false;
  private boolean runAsService = true;
  private String host = "s01ap094";
  private int port = 2099;

  private JFrame myMainFrame = null;

  public void addParameter(String aKey, String aParam){
    if(aKey.equals("service")) runAsService = new Boolean(aParam).booleanValue();
    else if(aKey.equalsIgnoreCase("host")) host = aParam;
    else if(aKey.equalsIgnoreCase("port")) port = Integer.parseInt(aParam);
  }

  public void runApplication(){
    launch(runAsService);
    startThread();
  }


  private void launch(boolean runAsService){
    MessengerClientService theClientService = null;
    DistributionService theDistributionService = null;
    try{
      Tools.initLog4j(new File("log4j.properties"));
      logger.debug("Booting light application launcher");
      ApplicationRefBase.putObject(ApplicationRefBase.VERSION, Updater.getVersion());
      //redirectSystem();
      Locale.setDefault(new Locale("nl", "BE"));
      Properties theProperties = ApplicationPreferences.getInstance();
      if(theProperties.containsKey("lookAndFeel")){
        try {
          UIManager.setLookAndFeel(theProperties.getProperty("lookAndFeel"));
        } catch (ClassNotFoundException e) {
          logger.error("System look and feel class not found", e);
        } catch (InstantiationException e) {
          logger.error("Could not instantiate System look and feel class", e);
        } catch (IllegalAccessException e) {
          logger.error("Could not instantiate System look and feel class", e);
        } catch (UnsupportedLookAndFeelException e) {
          logger.error("Could not instantiate System look and feel class", e);
        }
      }

      getUserName();


      RefBase theRefBase = ApplicationRefBase.getInstance();

      MessengerUser theUser = new MessengerUser();
      theUser.setFirstName(theProperties.getProperty("user.firstname"));
      theUser.setLastName(theProperties.getProperty("user.lastname"));
      theUser.setHost(Tools.getLocalInetAddress().getHostAddress());
      theUser.setUserName(theProperties.getProperty("user.userid"));
      theUser.setRmiPort(Tools.findUnusedLocalPort());
      theUser.setVersion((String)ApplicationRefBase.getObject(ApplicationRefBase.VERSION));
      theUser.setStatus(MessengerUser.ONLINE);

      LocateRegistry.createRegistry(theUser.getRmiPort());


      theClientService = new MessengerClientService(theUser, host, port);
      theDistributionService = new DistributionService("distributionlist/list.txt", theUser.getHost(), theUser.getRmiPort());

//    theClientService = new MessengerClientService(theUser, "x20d1148", 2099);
//    loadFilter(theChatModel);
      theRefBase.put(ApplicationRefBase.MESSENGERSERVICE, theClientService); 

      ApplicationEventDispatcher.addListener(this, new Class[]{ApplicationCloseEvent.class, UserAlreadyLoggedOnEvent.class});

      logger.debug("Active threads: " + Thread.activeCount());
      //printThreads();

      myMainFrame = new ChatFrame();

      if(ServiceTools.getRegistryRunKey( "Sheduler" ) == null){
        ServiceTools.addRun2Startup( new File("service.cmd") );
      }

      final DistributionService theDistService = theDistributionService;
      final MessengerClientService theClService = theClientService;

      new Thread(new Runnable(){
        public void run(){
          try{
            theDistService.register();
            theClService.register();
          } catch (Exception e) {
            logger.error("Error occured during startup", e);
          }
        }
      }).start();


      new OptionPaneStatusDispatcher(myMainFrame);
      if(!runAsService) myMainFrame.setVisible(true);
    }catch(Exception e){
      if(theClientService != null){
        try {
          theClientService.unregister();
        } catch (Exception e1) {
          logger.error("An error occured when unregistering user", e);
        }
      }
      logger.error("Error occured during startup", e);
      JOptionPane.showMessageDialog(null, "Er is een fout opgetreden tijdens het starten: " + e);
      System.exit(-1);
    }
  }

  private void getUserName(){
    Properties theProperties = ApplicationPreferences.getInstance();

    UserNameDialog theDialog = new UserNameDialog();

    theProperties.setProperty("user.firstname", Tools.makeFirstLetterUpper(theProperties.getProperty("user.firstname", "")));
    theProperties.setProperty("user.lastname", Tools.makeFirstLetterUpper(theProperties.getProperty("user.lastname", "")));
    theProperties.setProperty("user.userid", theProperties.getProperty("user.userid", "").toLowerCase());

//  TODO uncomment
    theDialog.setFirstName(theProperties.getProperty("user.firstname", ""));
    theDialog.setLastName(theProperties.getProperty("user.lastname", ""));
    String theUserId = theProperties.getProperty("user.userid", "");
    String theSystemUserId = System.getProperty("user.name").toLowerCase();

    if(!theUserId.equals(theSystemUserId) || !theDialog.validateInput()){
      theDialog.show();
//    TODO uncomment
      theProperties.setProperty("user.userid", theSystemUserId);

      
      while(theDialog.getState() == UserNameDialog.VALIDATION_FAILED){ 
        theDialog.show();
      }
      
      if(theDialog.getState() == UserNameDialog.CANCEL_PRESSED){
        System.exit(0);
      }
      
      theProperties.setProperty("user.firstname", Tools.makeFirstLetterUpper(theDialog.getFirstName()));
      theProperties.setProperty("user.lastname", Tools.makeFirstLetterUpper(theDialog.getLastName()));
      //TODO remove
//    theProperties.setProperty("user.userid", theDialog.getFirstName() + theDialog.getLastName());
    }
  }


  /*
  private void loadFilter(ChatModel aModel){
    try {
      Class theClass = Class.forName("CustomFilter");
      iMessageFilter theFilter = (iMessageFilter)theClass.newInstance();
      aModel.setFilter(theFilter);
    } catch (ClassNotFoundException e) {
      Logger.log(this,"No custom filter found");
    } catch (InstantiationException e) {
      Logger.log(this,"Could not instantiate filter");
    } catch (IllegalAccessException e) {
      Logger.log(this,"Could not instantiate filter");
    }
  }
   */

  private boolean activateUser(MessengerUser aUser){

    ObjectOutputStream theStream = null;
    try{
      if(aUser.getHost().equals(InetAddress.getLocalHost().getHostAddress()) ){
        Message theMessage = new Message();
        theMessage.setTechnicalMessage(true);
        theMessage.setMessage("activate");
        theMessage.setFrom(aUser.getId());
        theMessage.addTo(aUser.getId());

        String theServiceURL = "rmi://"  + aUser.getHost() + ":" + aUser.getRmiPort() + "/MessengerClientService";
        iMessengerClientService theService = (iMessengerClientService)Naming.lookup(theServiceURL);
        theService.acceptMessage(theMessage);
        return true;
      }
    }catch(UnknownHostException e){
      logger.error("Unknown local host", e);
    } catch (NumberFormatException e) {
      logger.error("Bad port number", e);
    } catch (IOException e) {
      logger.error("Could not create socket", e);
    } catch (NotBoundException e) {
      logger.error("could not contact local rmi server", e);
    } finally{
      if(theStream != null){
        try {
          theStream.flush();
          theStream.close();
        } catch (IOException e) {
          logger.error("Could not close stream", e);
        }
      }
    }
    return false;
  }

  /*
	private void printThreads(){
		ThreadGroup theGroup = Thread.currentThread().getThreadGroup();
		Thread[] theThreadList = new Thread[theGroup.activeCount()];
		theGroup.enumerate(theThreadList);
		for(int i=0;i<theThreadList.length;i++){
			Logger.log(ApplicationLauncherLight.class, i + ": " + theThreadList[i].getName()+ " " + theThreadList[i].toString());
		}
	}
   */

  private void redirectSystem(){
    try {
      System.setOut(new PrintStream(new FileOutputStream("out.log")));
      System.setErr(new PrintStream(new FileOutputStream("err.log")));
    } catch (FileNotFoundException e) {
      logger.error("Could not redirect output stream", e);
    } 
  }

  private void startThread(){
    new Thread(this).start();
  }

  public void save(){
    ApplicationPreferences.save();
  }

  public void run(){
    try{
      while(!stop){
        Thread.sleep(900000);
        save();
      }
    }catch(Exception e){
      logger.error("Could not sleep",  e);
    }
  }

  public void eventFired(Event evt) {
    if(evt instanceof ApplicationCloseEvent){
      myMainFrame.setVisible(false);
      stop = true;
      save();
      MessengerClientService theModel = (MessengerClientService)ApplicationRefBase.getObject(ApplicationRefBase.MESSENGERSERVICE);
      try {
        theModel.unregister();
      } catch (Exception e) {
        logger.error("An error occured when unregistering", e);
      }
      System.exit(0);
    } else if(evt instanceof UserAlreadyLoggedOnEvent){
      logger.debug("Trying to activate existing light application");
      MessengerUser theUser = ((UserAlreadyLoggedOnEvent)evt).getUser();
      if(!activateUser(theUser)){
        JOptionPane.showMessageDialog(null, "Er is reeds iemand aangelogd met de user: " + theUser + "\nMogelijks heeft u de toepassing reeds opgestart?", "Opgepast", JOptionPane.WARNING_MESSAGE);
      }
      System.exit(10);
    }
  }


  public static void main(String[] args) {
    ApplicationLauncherLight theLauncher = new ApplicationLauncherLight();
    theLauncher.addParameter("service", args[0]);
    if(args.length >= 2) theLauncher.addParameter("host", args[1]);
    if(args.length >= 3) theLauncher.addParameter("port", args[2]);
    theLauncher.runApplication();
  }
}

