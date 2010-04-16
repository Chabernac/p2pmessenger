package chabernac.utils;

import javax.swing.Timer;
import java.awt.event.*;

public class ThreadTimer implements ActionListener
{
  private Timer timer = null;
  private boolean timedOut = false;
  private ThreadTimerListener listener = null;
  private Thread thread = null;

  public ThreadTimer(int timeout,ThreadTimerListener listener, Thread thread)
  {
    timer = new Timer(timeout,this);
    this.thread = thread;
    this.listener = listener;
  }

  public void start()
  {
    timedOut = false;
    timer.start();
  }

  public void stop()
  {
	  timer.stop();
  }

/*
  public boolean stillRunning()
  {
	  //restarts the timer and returns whether the timer timed out.
    timer.restart();
    return timedOut;
  }
  */

  public void reset()
  {
    timedOut = false;
  }

  public boolean isTimedOut()
  {
	  if(!timedOut)
	  {
	  timer.restart();
  	  }
	  return timedOut;
  }

  public void actionPerformed(ActionEvent evt)
  {
	  if(!timedOut)
	  {
		//thread.interrupt();
		//Debug.log(this,"Thread destroyed");
    	timedOut = true;
    	timer.stop();
    	listener.timeOutOccured();
	  }
  }
}

