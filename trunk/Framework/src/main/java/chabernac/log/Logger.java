/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package chabernac.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author D1DAB1L
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Logger {
    private static boolean isDebug = false;
    private static SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
    
    public static void log(Object anObject, String aMessage){
        if(isDebug == false) return;
        
        Logger.log(anObject.getClass(), aMessage);
    }
    
    public static void log(Class aClass, String aMessage){
        if(isDebug == false) return;
        
        System.out.println(getTime() + ":" + aClass.toString() + ": " + aMessage);
    }
    
    public static void log(Object anObject, String aMessage, Throwable e){
        if(isDebug == false) return;
        
        Logger.log(anObject.getClass(), aMessage, e);
    }
    
    public static void log(Class aClass, String aMessage, Throwable e){
        if(isDebug == false) return;
        
        Logger.log(aClass.toString(), aMessage);
        e.printStackTrace(System.out);
    }
    
    public static void setDebug(boolean isDebug){
        Logger.isDebug = isDebug;
    }
    
    public static String getTime(){
        return myDateFormat.format(new Date(System.currentTimeMillis())); 
    }
}
