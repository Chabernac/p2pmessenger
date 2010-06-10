package chabernac.chat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;

public class UserNameDialog extends JDialog {
  public static final int VALIDATION_FAILED = 1;
  public static final int VALIDATION_OK = 2;
  public static final int CANCEL_PRESSED = 3;
  
  private JTextField myFirstName = null;
  private JTextField myLastName = null;
  private int myState = VALIDATION_FAILED;
  
  public UserNameDialog(){
    super((JFrame)null,"Naam", true);
    init();
    buildGUI();
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
  }
  
  private void init(){
    myFirstName = new JTextField();
    myFirstName.setColumns(10);
    myLastName = new JTextField();
    myLastName.setColumns(20);
  }
  
  private void buildGUI(){
    getContentPane().setLayout(new BorderLayout());
    JPanel theFormPanel = new JPanel(new BorderLayout());
    theFormPanel.setBorder(new EmptyBorder(new Insets(5,5,5,5)));
    GridLayout theGridLayout1 = new GridLayout(-1,1);
    theGridLayout1.setVgap(4);
    JPanel theLabelPanel = new JPanel(theGridLayout1);
    GridLayout theGridLayout2 = new GridLayout(-1,1);
    theGridLayout2.setVgap(4);
    JPanel theInputPanel = new JPanel(theGridLayout2);
    theFormPanel.add(theLabelPanel, BorderLayout.WEST);
    theFormPanel.add(theInputPanel, BorderLayout.CENTER);
    JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(theFormPanel, BorderLayout.NORTH);
    getContentPane().add(theButtonPanel, BorderLayout.SOUTH);
    
    theLabelPanel.add(new JLabel("Voornaam: "));
    theLabelPanel.add(new JLabel("Achternaam: "));
    
    theInputPanel.add(myFirstName);
    theInputPanel.add(myLastName);
    
    theButtonPanel.add(new CommandButton(new OKCommand()));
    theButtonPanel.add(new CommandButton(new CancelCommand()));
    pack();
    setResizable(false);
    Toolkit theToolkit = Toolkit.getDefaultToolkit();
    Dimension theSize = theToolkit.getScreenSize();
    setLocation(theSize.width / 2 - getWidth() / 2, theSize.height / 2 - getHeight() / 2);
  }
  
  public void setFirstName(String aFirstName){
    myFirstName.setText(aFirstName);
  }
  
  public void setLastName(String aLastName){
    myLastName.setText(aLastName);
  }
  
  public String getFirstName(){
    return myFirstName.getText();
  }
  
  public String getLastName(){
    return myLastName.getText();
  }
  
  public boolean validateInput(){
    myState = VALIDATION_FAILED;
    if(myFirstName.getText().length() < 2 || myFirstName.getText().length() > 10 || myFirstName.getText().indexOf(' ') != -1){
      myFirstName.requestFocus();
      return false;
    }
    
    if(myLastName.getText().length() < 2 || myLastName.getText().length() > 20){
      myLastName.requestFocus();
      return false;
    }
    myState = VALIDATION_OK;
    return true;
  }
  
  public int getState(){
    return myState;
  }
  
  public int isValidInput(){
    show();
    return myState;
  }
  
  private class OKCommand extends AbstractCommand{

    public String getName() {
      return "Ok";
    }

    public boolean isEnabled() {
      return true;
    }

    public void execute() {
      if(!validateInput()){
        return;
      }
      
      setVisible(false);
    }
    
  }
  
  private class CancelCommand extends AbstractCommand{

    public String getName() {
      return "Cancel";
    }

    public boolean isEnabled() {
      return true;
    }

    public void execute() {
      myState = CANCEL_PRESSED;
      setVisible(false);
    }
    
  }
  
  public static void main(String args[]){
    UserNameDialog theDialog = new UserNameDialog();
    theDialog.show();
  }

}
