package chabernac.task.launch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.application.RefBase;
import chabernac.backup.BackupFile;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.ShutDownDialog;
import chabernac.io.ClassPathResource;
import chabernac.object.Iterable;
import chabernac.object.ListConvertor;
import chabernac.object.ObjectPool;
import chabernac.p2pstatusconnector.P2PStatusConnector;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.TaskObjectIdConvertor;
import chabernac.task.TaskTools;
import chabernac.task.event.ApplicationCloseEvent;
import chabernac.task.gui.MainFrame;
import chabernac.updater.Updater;
import chabernac.updater.iApplication;
import chabernac.util.Tools;
import chabernac.utils.IOTools;
import chabernac.utils.ServiceTools;


public class ApplicationLauncher implements iEventListener, iApplication{
	private static Logger logger = Logger.getLogger(ApplicationLauncher.class);
	private File ipFile = null;
	//private static final File todoFile = new File("todo.bin");
	private boolean stop = false;
	private ListConvertor myConvertor = null;

	public void runApplication(){
		launch();
		startTimers();
	}

	public void addParameter(String aKey, String aParam){
	}


	private void launch(){
		try{
			Tools.initLog4j(new ClassPathResource("log4j.properties"));
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

			MainFrame theMainFrame = new MainFrame();
			theRefBase.put(ApplicationRefBase.MAINFRAME, theMainFrame);

			ApplicationEventDispatcher.addListener(this, new Class[]{ApplicationCloseEvent.class});

 	    addToStartup();
//			chabernac.utils.Tools.addRun2Registry("Sheduler", new File("heavy.cmd"));

			new P2PStatusConnector( System.getProperty( "user.name" ) );
			
			theMainFrame.setVisible(true);

			//Logger.log(ApplicationLauncher.class, "Active threads: " + Thread.activeCount());
			//printThreads();
		}catch(Exception e){
			logger.error("Error occured during startup", e);
			JOptionPane.showMessageDialog(null, "Er is een fout opgetreden tijdens het starten: " + e);
			System.exit(-1);
		}
	}
	
	private void addToStartup(){
	  try {
      ServiceTools.addRun2Startup( new File("tasksheduler.cmd") );
    } catch ( IOException e ) {
      logger.error("Unable to add tasksheduler.cmd to startup");
    }	  
	}

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
        createIpFileBackup();
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

	private void createIpFileBackup(){
	  BackupFile theBackupFile = new BackupFile();
	  theBackupFile.setBackupLocation( new File("c:\\temp") );
	  theBackupFile.setFile( ipFile );
	  theBackupFile.run();
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
				createIpFileBackup();
				theDialog.setMessage("Exiting...");
				theDialog.setVisible(false);
			} catch (Exception e) {
				logger.error("Could not unregister", e);
			}
			System.exit(0);
		}
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
		theLauncher.runApplication();
	}
}
