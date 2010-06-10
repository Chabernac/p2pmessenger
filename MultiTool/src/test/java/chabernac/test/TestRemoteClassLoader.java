package chabernac.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class TestRemoteClassLoader {

  /**
   * @param args
   */
  public static void main(String[] args) {
    new Thread(new Runnable(){
      public void run(){
        try{
          URLClassLoader theClassLoader = new URLClassLoader(new URL[]{new URL("http://www.dev.axa.be/ARCHIEF_APPLET/applet/immo/mss.jar")});
          Class theClass = theClassLoader.loadClass("chabernac.messengerservice.MessengerService");
          Method theMainMethod = theClass.getMethod("main", new Class[]{String[].class});
          theMainMethod.invoke(null, new Object[]{new String[]{"2099"}});    
        }catch(Exception e){

        }
      }
    }).start();
  }

}
