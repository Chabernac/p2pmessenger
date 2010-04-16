package chabernac.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;

import chabernac.command.AbstractCommand;

public class CommandButton extends JButton implements Observer{
  private AbstractCommand myCommand;
  private int myWidth = -1;
  
  public CommandButton(AbstractCommand aCommand){
    this(aCommand, -1);
  }
  
  public CommandButton(AbstractCommand aCommand, int aWidth){
    super(aCommand.getName());
    setMnemonic(aCommand.getMnemonic());
    myCommand = aCommand;
    myWidth = aWidth;
    layoutButton();
    aCommand.addObserver(this);
    addActionListener(new MyActionListener());
    addKeyListener();
  }
  
  private void addKeyListener(){
	  addKeyListener(
			  new KeyAdapter(){
				  public void keyPressed(KeyEvent evt){
					  if(evt.getKeyCode() == KeyEvent.VK_ENTER){
						  myCommand.execute();
					  }
				  }
			  }
			 );
  }
  
  private void layoutButton(){
    setText(myCommand.getName());
    setEnabled(myCommand.isEnabled());
    setMnemonic(myCommand.getMnemonic());
  }
  
  private class MyActionListener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
      myCommand.execute();
    }
  }

  public void update(Observable o, Object arg) {
    layoutButton();
  }
  
  
  public Dimension getPreferredSize(){
    Dimension theDimension = super.getPreferredSize();
    if(myWidth != -1) theDimension.width = myWidth;
    theDimension.height = getHeight();
    return theDimension;
  }
  
  
  public int getHeight(){
    return 20;
  }
  
  public int getWidth(){
    if(myWidth == -1) return super.getWidth();
    return myWidth;
  }

}
