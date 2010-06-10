package chabernac.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import chabernac.chat.gui.TrueDialog;
import chabernac.queue.ArrayQueue;
import chabernac.queue.TriggeredQueueReader;
import chabernac.queue.TriggeringQueue;
import chabernac.queue.iObjectProcessor;

public class JDialogTest extends TrueDialog implements iObjectProcessor{
  private TriggeringQueue myQueue = null;
  
  public JDialogTest(){
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    setTitle("1");
    buildGUI();
    pack();
    //setKeepInFront(true);
    show();
    init();
  }
  
  private void init(){
    myQueue = new TriggeringQueue(new ArrayQueue(10));
    TriggeredQueueReader theReader = new TriggeredQueueReader(myQueue, this);
    theReader.setThreads(1);
  }
  
  public void add(String aMessage){
    myQueue.put(aMessage);
  }
  
  public static void main(String args[]){
	  
	  try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    JDialogTest theTest = new JDialogTest();
    for(int i=0;i<10;i++){
      theTest.add("i=" + i);
    }
  }
  
  private void buildGUI(){
    getContentPane().setLayout(new BorderLayout());
    
    JButton theButton = new JButton("Ok");
    theButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        System.out.println(evt);
      }
    });
    
    getContentPane().add(theButton, BorderLayout.CENTER);
    
  }

  public void processObject(Object anArg0) {
    System.out.println("Object: " + anArg0);
    System.out.println("1");
    setTitle((String)anArg0);
    System.out.println("2");
    setVisible(true);
    System.out.println("3");
    forceToFront();
    //setKeepInFront(true);
    System.out.println("4");
    waitForBack();
    System.out.println("5");
    setVisible(false);
    //setKeepInFront(false);
    System.out.println("6");
  }


}
