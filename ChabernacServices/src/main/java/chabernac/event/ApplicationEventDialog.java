package chabernac.event;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import chabernac.queue.TriggeringQueue;
import chabernac.queue.iQueueListener;

public class ApplicationEventDialog extends JDialog implements iQueueListener, Runnable{
	private static final long serialVersionUID = -1102353645593383542L;
	private JLabel myLabel = null;
	private TriggeringQueue myApplicationEventQueue = null;
	private boolean isRunning = false;
	
	public ApplicationEventDialog(JFrame aFrame){
		super(aFrame, "Shutting down", false );
		setLocation(aFrame.getX() + aFrame.getWidth() / 2, aFrame.getY() + aFrame.getHeight() / 2);
		init();
		buildGUI();
	}
	
	private void init(){
		myApplicationEventQueue = ApplicationEventDispatcher.getApplicationEventQueue();
		myApplicationEventQueue.addQueueListener(this, 1);
		myLabel = new JLabel();
	}
	
	private void buildGUI(){
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(myLabel, BorderLayout.CENTER);
		setSize(200, 100);
	}
	
	public void run(){
		setVisible(true);
		isRunning = true;
		while(myApplicationEventQueue.size() > 0){
			Object theObject = myApplicationEventQueue.peek();
			if(theObject != null){
				myLabel.setText( ((Event)theObject).getDescription() );
				repaint();
				pack();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isRunning = false;
		setVisible(false);
	}

	public void trigger() {
		if(!isRunning){
			new Thread(this).start();
		}
		// TODO Auto-generated method stub
		
	}
}
