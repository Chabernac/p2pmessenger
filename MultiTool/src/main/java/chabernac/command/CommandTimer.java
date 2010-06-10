/*
 * Created on 12-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.command;


import org.apache.log4j.Logger;

public class CommandTimer implements Runnable{
  private static Logger logger = Logger.getLogger(CommandTimer.class);
	private Command myCommand = null;
	private long myTimeout;
	
	public CommandTimer(Command aCommand, long aTimeout){
		myCommand = aCommand;
		myTimeout = aTimeout;
		start();
	}
	
	private void start(){
		new Thread(this).start();
	}
	
	public void run(){
		try{
			Thread.sleep(myTimeout);
			myCommand.execute();
		}catch(InterruptedException e){
			logger.debug("could not sleep", e);
		}
		
	}

}
