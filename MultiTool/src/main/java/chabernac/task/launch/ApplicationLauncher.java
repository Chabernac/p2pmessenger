package chabernac.task.launch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.application.RefBase;
import chabernac.chat.AttachmentHandler;
import chabernac.chat.Message;
import chabernac.chat.gui.UserNameDialog;
import chabernac.chat.gui.event.UserAlreadyLoggedOnEvent;
import chabernac.distributionservice.DistributionService;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.ShutDownDialog;
import chabernac.mail.MailMessage;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.iMessengerClientService;
import chabernac.object.Iterable;
import chabernac.object.ListConvertor;
import chabernac.object.ObjectPool;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.TaskObjectIdConvertor;
import chabernac.task.TaskTools;
import chabernac.task.event.ApplicationCloseEvent;
import chabernac.task.gui.MainFrame;
import chabernac.updater.Updater;
import chabernac.updater.iApplication;
import chabernac.util.StatusDispatcher;
import chabernac.util.Tools;
import chabernac.utils.IOTools;
import chabernac.utils.ServiceTools;


public class ApplicationLauncher implements iEventListener, iApplication{
	private static Logger logger = Logger.getLogger(ApplicationLauncher.class);
	private File ipFile = null;
	//private static final File todoFile = new File("todo.bin");
	private boolean stop = false;
	private ListConvertor myConvertor = null;

	private String host = "s01ap094";
	private int port = 2099;

	public void runApplication(){
		launch();
		startTimers();
	}

	public void addParameter(String aKey, String aParam){
		if(aKey.equalsIgnoreCase("host")) host = aParam;
		else if(aKey.equalsIgnoreCase("port")) port = Integer.parseInt(aParam);
	}


	private void launch(){
		MessengerClientService theClientService = null;
		DistributionService theDistributionService = null;
		try{
			Tools.initLog4j(new File("log4j.properties"));
			logger.debug("Booting heavy application launcher");

			ApplicationRefBase.putObject(ApplicationRefBase.VERSION, Updater.getVersion());
//			redirectSystem();
			Locale.setDefault(new Locale("nl", "BE"));
			final Properties theProperties = ApplicationPreferences.getInstance();
			ipFile = new File(theProperties.getProperty("task.file","ip.bin"));
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


			/*
      Color theBGColor = new Color(230,230,230);

      UIManager.put( "Panel.background", theBGColor);
      UIManager.put( "Button.background", theBGColor);
      UIManager.put( "CheckBox.background", theBGColor);
      UIManager.put( "ScrollBar.background", theBGColor);
      UIManager.put( "TabbedPane.background", theBGColor);
      UIManager.put( "Table.background", theBGColor);
			 */


			getUserName();

//			String theName = theProperties.getProperty("user.name", "");
//			while(theName.equals("") || theName.length() > 40 || theName.indexOf(" ") == -1){
//			theName = JOptionPane.showInputDialog(null, "Wat is uw volledige naam?").trim();
//			}
//			theProperties.setProperty("user.name", theName);
//			StringTokenizer theTokenizer = new StringTokenizer(theName);
//			theProperties.setProperty("user.firstname", theTokenizer.nextToken());
//			theProperties.setProperty("user.lastname", theTokenizer.nextToken());

			RefBase theRefBase = ApplicationRefBase.getInstance();
			theRefBase.put(ApplicationRefBase.ROOTTASK, TaskTools.loadTask(ipFile));

			ObjectPool thePool = new ObjectPool(new TaskObjectIdConvertor());
			thePool.addIterable((Iterable)ApplicationRefBase.getObject(ApplicationRefBase.ROOTTASK));
			myConvertor = new ListConvertor(thePool);

			theRefBase.put(ApplicationRefBase.TODO, myConvertor.convertList2Objects(TaskTools.loadToDo(ipFile)));

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
//			theClientService = new MessengerClientService(theUser, "X20D1148", 2099);
			theDistributionService = new DistributionService("distributionlist/list.txt", theUser.getHost(), theUser.getRmiPort());

			//loadFilter(theChatModel);
			theRefBase.put(ApplicationRefBase.MESSENGERSERVICE, theClientService);

			MainFrame theMainFrame = new MainFrame();
			new AttachmentHandler(theClientService);
			theRefBase.put(ApplicationRefBase.MAINFRAME, theMainFrame);

			ApplicationEventDispatcher.addListener(this, new Class[]{ApplicationCloseEvent.class, UserAlreadyLoggedOnEvent.class});

			if(ServiceTools.getRegistryRunKey( "Sheduler" ) == null){
			  ServiceTools.addRun2Startup( new File("heavy.cmd") );
			}
//			chabernac.utils.Tools.addRun2Registry("Sheduler", new File("heavy.cmd"));

			theMainFrame.setVisible(true);

			final DistributionService theDistService = theDistributionService;
			final MessengerClientService theClService = theClientService;

			new Thread(new Runnable(){
				public void run(){
					try{
						theDistService.register();
						theClService.register();
					} catch (Exception e) {
						StatusDispatcher.showError("Er is een fout opgetreden tijdens het connecteren met de server");
						logger.error("Er is een fout opgetreden tijdens het connecteren met de server", e);
					}

					//StatusDispatcher.showMessage("U bent aangelogd als " + theProperties.getProperty("user.firstname") + " " + theProperties.getProperty("user.lastname"));
				}
			}).start();


			//Logger.log(ApplicationLauncher.class, "Active threads: " + Thread.activeCount());
			//printThreads();
		}catch(Exception e){
			if(theClientService != null) {
				try {
					theClientService.unregister();
				} catch (Exception e1) {
					logger.error("Could not unregister", e);
				}
			}
			if(theDistributionService != null) {
				try {
					theDistributionService.unregister();
				} catch (Exception e1) {
					logger.error("Could not unregister", e);
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

//		TODO uncomment
		theDialog.setFirstName(theProperties.getProperty("user.firstname", ""));
		theDialog.setLastName(theProperties.getProperty("user.lastname", ""));
		String theUserId = theProperties.getProperty("user.userid", "");
		String theSystemUserId = System.getProperty("user.name").toLowerCase();

		if(!theUserId.equals(theSystemUserId) || !theDialog.validateInput()){
			theDialog.show();
//			TODO uncomment
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
//			theProperties.setProperty("user.userid", theDialog.getFirstName() + theDialog.getLastName());
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

	/*
  private void printThreads(){
    ThreadGroup theGroup = Thread.currentThread().getThreadGroup();
    Thread[] theThreadList = new Thread[theGroup.activeCount()];
    theGroup.enumerate(theThreadList);
    for(int i=0;i<theThreadList.length;i++){
      Logger.log(ApplicationLauncher.class, i + ": " + theThreadList[i].getName()+ " " + theThreadList[i].toString());
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

	private void startTimers(){
	  ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
	  new Timer(Integer.parseInt(thePreferences.getProperty("save.timeout", "900000")), new ActionListener(){
	    public void actionPerformed(ActionEvent evt){
	      save();
	    }
	  }).start();
	  new Timer(Integer.parseInt(thePreferences.getProperty("backup.mail.timeout", "14400000")), new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        sendBackupMail();
      }
	  }).start();
		
	}

	public void save(){
		HashMap theMap = new HashMap();
		theMap.put(ApplicationRefBase.ROOTTASK, ApplicationRefBase.getObject(ApplicationRefBase.ROOTTASK));
		theMap.put(ApplicationRefBase.TODO, myConvertor.convertList2Ids((List)ApplicationRefBase.getObject(ApplicationRefBase.TODO)));
		try{
			IOTools.saveObject(theMap, ipFile);
		}catch(IOException e){
			logger.error("Could not save object", e);
		}
		ApplicationPreferences.save();
	}

	private void sendBackupMail(){
		Properties theProperties = ApplicationPreferences.getInstance(); 
		String theBackupMailTo = theProperties.getProperty("backup.mail.to", "");
		String theBackupMailFrom = theProperties.getProperty("backup.mail.from", theBackupMailTo);
		String theHost = theProperties.getProperty("mail.host", "smtpint.axa.be");

		if(!"".equals(theBackupMailTo) && !"".equals(theBackupMailFrom) && !"".equals(theHost)){
			//
			MailMessage theMessage = new MailMessage(theHost, theBackupMailFrom, new String[]{theBackupMailTo});
			theMessage.setSubject("tasksheduler backup");
			theMessage.addSystemAttachemnt(ipFile.getName(), ipFile.getAbsolutePath());
			try {
				theMessage.send();
			} catch (MessagingException e) {
				logger.error("An error occured while sending backup mail", e);
			}
		}
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
			logger.debug("Application close event received");
			try{
				JFrame theMainFrame = ((JFrame)ApplicationRefBase.getObject(ApplicationRefBase.MAINFRAME));
				theMainFrame.setVisible(false);
				ShutDownDialog theDialog = new ShutDownDialog(theMainFrame);
				theDialog.setVisible(true);

				stop = true;
				theDialog.setMessage("Saving...");
				save();
				sendBackupMail();
				MessengerClientService theModel = (MessengerClientService)ApplicationRefBase.getObject(ApplicationRefBase.MESSENGERSERVICE);
				theDialog.setMessage("Stopping messenger...");
				theModel.unregister();
				theDialog.setMessage("Exiting...");
				theDialog.setVisible(false);
			} catch (Exception e) {
				logger.error("Could not unregister", e);
			}
			System.exit(0);
		} else if(evt instanceof UserAlreadyLoggedOnEvent){
		  logger.debug("Trying to activate existing heavy application");
      MessengerUser theUser = ((UserAlreadyLoggedOnEvent)evt).getUser();
      if(!activateUser(theUser)){
        JOptionPane.showMessageDialog(null, "Er is reeds iemand aangelogd met de user: " + theUser + "\nMogelijks heeft u de toepassing reeds opgestart?", "Opgepast", JOptionPane.WARNING_MESSAGE);
      }
      System.exit(10);
		  
//			MessengerUser theUser = ((UserAlreadyLoggedOnEvent)evt).getUser();
//			StatusDispatcher.showError("Er is reeds iemand aangelogd met de user: " + theUser + "\nMogelijks heeft u de toepassing reeds opgestart?");
//			logger.error("Er is reeds iemand aangelogd met de user: " +  theUser);
//			System.exit(-1);
		}
	}
	
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


	public static void main(String[] args) {
		/*
     Task theRootTask = TaskTools.loadTask(file);
     ArrayList thePeriods = theRootTask.getAllPeriods();
     for(int i=0;i<thePeriods.size();i++) System.out.println(((Period)thePeriods.get(i)).getStartTime());
     Collections.sort(thePeriods);
     System.out.println("--------------SORTED-------------");
     for(int i=0;i<thePeriods.size();i++) System.out.println(((Period)thePeriods.get(i)).getStartTime());
		 */
//		String theVersion = "4.0.0";
//		if(args.length > 0) theVersion = args[0];
		ApplicationLauncher theLauncher = new ApplicationLauncher();
		if(args.length >= 1) theLauncher.addParameter("host", args[0]);
		if(args.length >= 2) theLauncher.addParameter("port", args[1]);
		theLauncher.runApplication();
	}
}
