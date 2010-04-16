package chabernac.utils;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

public class ThreadTools {
	public static void invokeLaterIfNotEventDispatchingThread(Runnable aRunnable){
		if(!EventQueue.isDispatchThread()){
			SwingUtilities.invokeLater(aRunnable);
		} else {
			aRunnable.run();
		}
	}

}
