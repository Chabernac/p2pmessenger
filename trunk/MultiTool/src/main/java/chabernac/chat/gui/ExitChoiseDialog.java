package chabernac.chat.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;

public class ExitChoiseDialog extends JDialog {
	public static class Choices{
		public static final int EXIT = 1;
		public static final int HIDE = 2;
		public static final int BACK = 3;
	}
	
  private int myTimeout = 2;
  private JRadioButton myExitButton = null;
  private JRadioButton myHideButton = null;
  private JRadioButton myBackButton = null;
  private JLabel myTimeoutLabel = null;
  
  public ExitChoiseDialog(JFrame aFrame, int timeOut){
    super(aFrame, "Afsluit keuze", true);
    setLocation(aFrame.getX() + 10, aFrame.getY() + 10);
    myTimeout = timeOut;
    init();
    buildGUI();
    startTime();
  }
  
  private void startTime(){
    new Thread(new Runnable(){
      public void run(){
        try{
          while(myTimeout > 0){
            Thread.sleep(1000);
            myTimeout -= 1;
            fillTimeoutLabel();
          }
          setVisible(false);
        }catch(InterruptedException e){}
      }
    }).start();
  }
  
  private void init(){
    myExitButton = new JRadioButton("De toepassing volledig afsluiten");
    myHideButton = new JRadioButton("De toepassing verbergen");
    myBackButton = new JRadioButton("Terug naar de toepassing");
    myHideButton.setSelected(true);
    ButtonGroup theGroup = new ButtonGroup();
    theGroup.add(myHideButton);
    theGroup.add(myExitButton);
    theGroup.add(myBackButton);
    myTimeoutLabel = new JLabel();
  }
  
  private void fillTimeoutLabel(){
    myTimeoutLabel.setText("Deze dialoog wordt afgesloten in " + myTimeout + " seconden");
  }
  
  public int getChoice(){
  	if(myExitButton.isSelected()){
  		return Choices.EXIT;
  	} else if(myBackButton.isSelected()){
  		return Choices.BACK;
  	} else if(myHideButton.isSelected()){
  		return Choices.HIDE;
  	}
  	return 0;
  }
  
  private void buildGUI(){
    Container theContentPane = getContentPane();
    theContentPane.setLayout(new BorderLayout());
    JPanel theOptionPanel = new JPanel(new GridLayout(-1,1));
    theOptionPanel.add(new JLabel("Wat wilt u doen?"));
    theOptionPanel.add(myHideButton);
    theOptionPanel.add(myExitButton);
    theOptionPanel.add(myBackButton);
    fillTimeoutLabel();
    theOptionPanel.add(myTimeoutLabel);
    theContentPane.add(theOptionPanel, BorderLayout.CENTER);
    
    JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    theButtonPanel.add(new CommandButton(new AbstractCommand(){
      public String getName() { return "Ok"; }
      public boolean isEnabled() { return true; }
      public void execute() { setVisible(false);   }
    }));
    theContentPane.add(theButtonPanel, BorderLayout.SOUTH);
    pack();
  }
  
  public static int exitApplication(JFrame aFrame, int aTimout){
    ExitChoiseDialog theDialog = new ExitChoiseDialog(aFrame, aTimout);
    theDialog.setVisible(true);
    return theDialog.getChoice();

  }

}
