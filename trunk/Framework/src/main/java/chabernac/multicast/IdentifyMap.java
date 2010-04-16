/*
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package chabernac.multicast;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import chabernac.log.Logger;

public class IdentifyMap extends Hashtable implements Runnable{
    private long myTimeout;
    private boolean stop;
    private MyObservable myObserveable = null;
    
    public IdentifyMap(long aTimeout){
        super();
        myTimeout = aTimeout;
        myObserveable = new MyObservable();
        startTimer();
    }
    
    public void alive(String aUser, String aHost){
        IdentifyItem theItem = getItem(aUser);
        theItem.setHost(aHost);
        theItem.setDate(System.currentTimeMillis());
        theItem.setStatus(IdentifyItem.ALIVE);
        myObserveable.notifyObs();
    }
    
    public void dead(String aUser){
        IdentifyItem theItem = getItem(aUser);
        theItem.setDate(System.currentTimeMillis());
        theItem.setStatus(IdentifyItem.DEAD);
        myObserveable.notifyObs();
    }
    
    public void addObserver(Observer anObserver){
      myObserveable.addObserver(anObserver);
    }
    
    public void deleteObserver(Observer anObserver){
      myObserveable.deleteObserver(anObserver);
    }

    
    private IdentifyItem getItem(String aUser){
        if(!containsKey(aUser)){
            put(aUser, new IdentifyItem(aUser));
        }
        return (IdentifyItem)get(aUser);
    }
    
    public String findUser(String aUser){
        if(!containsKey(aUser)) return null;
        IdentifyItem theItem = getItem(aUser);
        if(theItem.getStatus() == IdentifyItem.DEAD) return null;
        return ((IdentifyItem)get(aUser)).getHost();
    }
    
    private void startTimer(){
        stop = false;
        new Thread(this).start();       
    }
    
    public void run(){
        while(!stop){
            try {
                Thread.sleep(myTimeout);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cleanUp();
        }
    }
    
    private void cleanUp(){
        Logger.log(this, "Cleaning up expired items");
        Enumeration theEnumeration = elements();
        IdentifyItem theItem = null;
        long theEndTime = System.currentTimeMillis() - myTimeout;
        while(theEnumeration.hasMoreElements()){
            theItem = (IdentifyItem)theEnumeration.nextElement();
            if(theItem.getDate() < theEndTime){
                Logger.log(this, "Item of user: " + theItem.getUser() + " has been expired, removing...");
                remove(theItem.getUser());
            }
        }
    }
    
    public void stop(){
        stop = true;
    }
    
    public void setExpireTime(long aTimeout){
        myTimeout = aTimeout;
    }
    
    public Hashtable getAllAlive(){
      Hashtable alive = new Hashtable();
      Enumeration theEnumeration = elements();
      IdentifyItem theItem = null;
      while(theEnumeration.hasMoreElements()){
        theItem = (IdentifyItem)theEnumeration.nextElement();
        if(theItem.getStatus() == IdentifyItem.ALIVE){
          alive.put(theItem.getUser(), theItem.getHost());
        }
      }
      return alive;
    }
    
    private class IdentifyItem{
        public static final int DEAD = 1;
        public static final int ALIVE = 2;
        
        public String user = null;
        public String host = null;
        public long date;
        public int status;
        
        public IdentifyItem(String aUser){
            user = aUser;
        }
                
        public long getDate() {
            return date;
        }
        public void setDate(long date) {
            this.date = date;
        }
        public String getHost() {
            return host;
        }
        public void setHost(String host) {
            this.host = host;
        }
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
        public String getUser() {
            return user;
        }
        public void setUser(String user) {
            this.user = user;
        }
    }
    
    private class MyObservable extends Observable{
      public void notifyObs(){
        setChanged();
        notifyObservers();
      }
    }
}
