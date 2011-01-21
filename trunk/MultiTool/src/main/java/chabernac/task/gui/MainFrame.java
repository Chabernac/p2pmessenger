package chabernac.task.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.application.RefBase;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.gui.SavedFrame;
import chabernac.gui.StatusBar;
import chabernac.task.Task;
import chabernac.task.TaskException;
import chabernac.task.command.DefaultActivityCommand;
import chabernac.task.event.ApplicationCloseEvent;
import chabernac.task.event.ApplicationSaveEvent;
import chabernac.todo.gui.ToDoPanel;

public class MainFrame extends SavedFrame{
  private static Logger logger = Logger.getLogger(MainFrame.class);
  
	private JTabbedPane myTabbedPane = null;
	private JPanel myChatPanel = null;
	
	public MainFrame(){
		super("Activity logger - " + ApplicationRefBase.getObject(ApplicationRefBase.VERSION), new Rectangle(0,0,700,500) );
		buildGUI();
		addListeners();
		ApplicationRefBase.putObject(ApplicationRefBase.MAINFRAME, this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	private void buildGUI(){
		getContentPane().setLayout(new GridBagLayout());
    
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.insets = new Insets(2,2,2,2);
    theCons.fill = GridBagConstraints.BOTH;
    theCons.weightx = 1;
    theCons.weighty = 1;
    theCons.gridx = 0;
    theCons.gridy = 0;
    
		myTabbedPane = new JTabbedPane();
		myTabbedPane.add("Activities", new MainPanel());
		myTabbedPane.add("Todo", new ToDoPanel());
		myTabbedPane.add("Periods", new PeriodOverviewPanel());
		
		getContentPane().add(myTabbedPane, theCons);
    
    theCons.gridy++;
    theCons.fill = GridBagConstraints.HORIZONTAL;
    theCons.weighty = 0;
    
    getContentPane().add(new StatusBar(), theCons);
		loadIcon();
	}
	
	private void addListeners(){
		addWindowListener(new MyWindowListener());
	}
	
	private void loadIcon(){
		try{
			setIconImage(ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("images/calendar2.gif")));
		}catch(IOException e){
			logger.error("could not load icon", e);
		}
	}
	
	private class MyWindowListener extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			if(closeRunningTask()){
        ApplicationEventDispatcher.fireEvent(new ApplicationSaveEvent());
        ApplicationEventDispatcher.fireEvent(new ApplicationCloseEvent());
			}
		}
	}
	
	private boolean closeRunningTask(){
		RefBase theRefBase = ApplicationRefBase.getInstance();
		Task theRunningTask = ((DefaultActivityCommand)theRefBase.get(ApplicationRefBase.DEFAULTTASKCOMMAND)).getRunningTask();
		if(theRunningTask != null){
			int theChoice = JOptionPane.showConfirmDialog((JFrame)theRefBase.get(ApplicationRefBase.MAINFRAME), "U staat op het punt de applicatie te verlaten maar er is nog één activiteit actief.  Dient deze activiteit gestopt te worden?");
			if(theChoice == JOptionPane.YES_OPTION){
				try{
					theRunningTask.stop();
				}catch(TaskException e){
					logger.error("Could not stop current task", e);
				}
				return true;
			}
			if(theChoice == JOptionPane.CANCEL_OPTION){
				return false;
			}
		}
		return true;
	}
	
	
	public void focusOnTab(JComponent aComponent){
		myTabbedPane.setSelectedComponent(aComponent);
	}
	
	public void focusOnChatTab() {
		focusOnTab(myChatPanel);
	}

	protected String getFrameName() {
		return "heavy";
	}
}
