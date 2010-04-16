package chabernac.utils;

import org.apache.log4j.Logger;


public class TimeTracker {
	private static Logger LOGGER = Logger.getLogger(TimeTracker.class);
	
	private static long startTime = 0;
	private static boolean isEnabled = false;
	private static int depth = -1;
	private static long[] times = new long[20];
	
	static{
		for(int i=0;i<times.length;i++){
			times[i] = 0;
		}
	}
	
	public static long start(){
		if(isEnabled) {
			//long theSystemTime = System.nanoTime();
      long theSystemTime = System.currentTimeMillis();
			long theTime = theSystemTime - startTime;
			
			startTime = theSystemTime;
			
			
			depth++;
			times[depth] = 0;
			for(int i=0;i<depth;i++){
				times[i] += theTime;
				System.out.println("start times " + i + " " + times[i] / 1000000);
			}
			
			return startTime;
		}
		return 0;
	}
	
	public static long stop(){
		if(isEnabled){
			//long theTime = System.nanoTime() - startTime;
      long theTime = System.currentTimeMillis() - startTime;
			
			for(int i=0;i<=depth;i++){
				times[i] += theTime;
				System.out.println("stop times " + i + " " + times[i] / 1000000);
			}
			
			return times[depth--];
		}
		return 0;
	}     



  
  public static long logTime(String aDescription, long t){
    return 0L;
  }
	
	public static long logTime(String aDescription){
		logTime(aDescription, true);
    return 0L;
	}
	
	public static void logAndStopTime(String aDescription){
		logTime(aDescription, false);
	}
	
	public static void logTime(String aDescription, boolean restart){
		if(isEnabled){
			long theTime = stop();
			
			System.out.println(aDescription + " took " + theTime / 1000000 + " ms");
			LOGGER.debug(aDescription + " took " + theTime / 1000000 + " ms");
			//start after the debug statement, we don't want to take the time of the debug statement into account
			if(restart) start();
		}
	}
	
	public static void setEnabled(boolean isEnabled){
		TimeTracker.isEnabled = isEnabled;
	}
	
	public static void main(String args[]){
		try{
			TimeTracker.setEnabled(true);
			TimeTracker.start();
			Thread.sleep(100);
			TimeTracker.start();
			Thread.sleep(200);
			TimeTracker.logAndStopTime("200 ms");
			TimeTracker.logAndStopTime("300 ms");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
