package chabernac.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static boolean isDebug = false;
    private static SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
    
    public static void log(Object anObject, String aMessage){
        if(isDebug == false) return;
        
        System.out.println(getTime() + ":" + anObject.toString() + ": " + aMessage);
    }
    
    public static void log(Class aClass, String aMessage){
        if(isDebug == false) return;
        
        System.out.println(getTime() + ":" + aClass.toString() + ": " + aMessage);
    }
    
    public static void log(Object anObject, String aMessage, Throwable e){
        //if(isDebug == false) return;
        
        Logger.log(anObject.getClass(), aMessage, e);
    }
    
    public static void log(Class aClass, String aMessage, Throwable e){
        //if(isDebug == false) return;
        
        System.err.println(getTime() + ":" + aClass.toString() + ": " + aMessage);
        e.printStackTrace(System.err);
    }
    
    public static void setDebug(boolean isDebug){
        Logger.isDebug = isDebug;
    }
    
    public static String getTime(){
        return myDateFormat.format(new Date(System.currentTimeMillis())); 
    }
}
