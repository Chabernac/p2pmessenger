package chabernac.multicast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

import chabernac.log.Logger;
import chabernac.record.Record;

/**
 * @author D1DAB1L
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IdentifyProtocol implements DatagramProtocol, Runnable {
    private MulticastIO myIO = null;
    private String myHostAddress = null;
    private IdentifyMap myMappings = null;
    private String myUser = null;
    private long myWaitTime = 500;
    private int myRetries = 2;
    private long myExpireTime;
    private boolean continueAnnouncements = true;
    private boolean stop = false;
    
    public IdentifyProtocol(String aUser){
        this(aUser, 1000 * 60 * 10);
    }
   
    public IdentifyProtocol(String aUser, long anExpireTime){
        myUser = aUser.toLowerCase();
        myMappings = new IdentifyMap(anExpireTime);
        try {
            myHostAddress = InetAddress.getLocalHost().getHostName().toLowerCase();
            myIO = new MulticastIO(2011, InetAddress.getByName("224.0.0.10"), 24, this);
            myIO.startIO();
            myExpireTime = anExpireTime;
            sendAnnouncement();
            refreshAll();
            if(continueAnnouncements) new Thread(this).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
    }
    
    public void stop(){
        stop = true;
        IdentifyRecord theRecord = new IdentifyRecord();
        theRecord.setValue("TYPE", IdentifyRecord.DEAD);
        theRecord.setValue("USER", myUser);
        sendRecord(theRecord);
        myIO.stopIO();
    }
    
    public void run(){
        while(!stop && continueAnnouncements){
            try{
                Thread.sleep(myExpireTime);
                sendAnnouncement();
            }catch(InterruptedException e){
                Logger.log(this,"Could not sleep", e);
            }
        }
    }

    public void handle(byte[] theContent, MulticastIO anIO) {
        IdentifyRecord theRecord = new IdentifyRecord();
        theRecord.setContent(theContent);
        if(theRecord.getLongValue("TYPE") == IdentifyRecord.REQUEST){
            Logger.log(this, "Request packet received");
            String who = theRecord.getStringValue("USER"); 
            if(who.equals(myUser) || who.equalsIgnoreCase("ALL")){
                Logger.log(this, "Sending announcement packet");
                sendAnnouncement();  
            }
        } else if(theRecord.getLongValue("TYPE") == IdentifyRecord.ANNOUNCEMENT){
            String theUser = theRecord.getStringValue("USER"); 
            if(!theUser.equals(myUser)){
                Logger.log(this,"Announcement packet received: " + new String(theRecord.getContent()));
                myMappings.alive(theUser, theRecord.getStringValue("HOST"));
                Logger.log(this, "Current mapppings: " + myMappings.toString());
                synchronized(theUser.intern()){
                    theUser.intern().notifyAll();
                }
            }
        } else if(theRecord.getLongValue("TYPE") == IdentifyRecord.DEAD){
            Logger.log(this,"Dead packet received: " + theRecord.getStringValue("USER"));
            myMappings.dead(theRecord.getStringValue("USER"));
        }
    }
    
    private void sendRecord(Record aRecord){
        myIO.sendDatagramPacket(aRecord.getContent());
    }
    
    public void sendAnnouncement(){
        IdentifyRecord theRecord = new IdentifyRecord();
        theRecord.setValue("TYPE", IdentifyRecord.ANNOUNCEMENT);
        theRecord.setValue("HOST", myHostAddress);
        theRecord.setValue("USER", myUser);
        sendRecord(theRecord);
    }
    
    public void setExpireTime(long aTimeout){ myMappings.setExpireTime(aTimeout); }
    public void setWaitTime(long aTimeout){ myWaitTime = aTimeout; }
    public void setRetries(int retries){ myRetries = retries; }
    public void setContinueAnnouncement(boolean continueAnn){ continueAnnouncements = continueAnn; }
    
    public void refreshAll(){
      IdentifyRecord theRecord = new IdentifyRecord();
      theRecord.setValue("TYPE", IdentifyRecord.REQUEST);
      theRecord.setValue("USER", "ALL");
      sendRecord(theRecord);
    }
    
    public String whoHas(String aUserName){
        return whoHas(aUserName, false);
    }
    
    public String whoHas(String aUserName, boolean forceLookup){
        String theUserName = aUserName.toLowerCase();
        if(forceLookup && myMappings.containsKey(theUserName)) myMappings.remove(theUserName);
        if(theUserName.equals(myUser)) return myHostAddress;
        int counter = 0;
        while(!myMappings.containsKey(theUserName)){
            if(counter >= myRetries) {
                myMappings.dead(theUserName);
                return null;
            }
            counter++;
            Logger.log(this, "User: " + theUserName + " not found after " + counter + " attempts.");
            IdentifyRecord theRecord = new IdentifyRecord();
            theRecord.setValue("TYPE", IdentifyRecord.REQUEST);
            theRecord.setValue("USER", theUserName);
            sendRecord(theRecord);
            try {
                synchronized(theUserName.intern()){
                    theUserName.intern().wait(myWaitTime);
                }
            } catch (InterruptedException e) {
                Logger.log(this,"Could not wait", e);
            }
        }
        return myMappings.findUser(theUserName);
    }
    
    public Hashtable whoHas(Vector userNames){
        final Hashtable theHosts = new Hashtable();
        for(int i=0;i<userNames.size();i++){
            final String theUserName = (String)userNames.elementAt(i);
            new Thread(){
                public void run(){
                    String host = whoHas(theUserName);
                    if(host == null) host = "Not Found";
                    theHosts.put(theUserName, host);
                    synchronized(theHosts){
                        theHosts.notifyAll();
                    }
                }
            }.start();
        }
        while(theHosts.size() < userNames.size()){
            synchronized(theHosts){
                try{
                    theHosts.wait();
                }catch(InterruptedException e){}
            }
        }
        return theHosts;
    }
    
    public IdentifyMap getIdentifyMap(){
      return myMappings;
    }
    
    
    private class IdentifyRecord extends Record{
        public static final int ANNOUNCEMENT = 1;
        public static final int REQUEST = 2;
        public static final int DEAD = 3;
        
        public void defineFields(){
            setField("TYPE", 2, NUMERIC);
            setField("HOST", 10, ALPHANUMERIC);
            setField("USER", 10, ALPHANUMERIC);
            setField("FILLER", 2, ALPHANUMERIC);
        }
    }
    
    
    
}
;