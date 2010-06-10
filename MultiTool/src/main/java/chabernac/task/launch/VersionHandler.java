/*
 * Created on 24-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.task.launch;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.chat.AttachmentHandler;
import chabernac.chat.Message;
import chabernac.distributionservice.iDistributionService;
import chabernac.io.DataFile;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;


public class VersionHandler extends AttachmentHandler {
  private static Logger logger = Logger.getLogger(VersionHandler.class);

  private static final String JAR_FILE_NAME = "sheduler.jar";
  private String myLatestVersion;


  public VersionHandler(MessengerClientService aModel) {
    super(aModel);
    //myModel.addObserver(new MapObserver());

    //myLatestVersion = extractNumber((String)ApplicationRefBase.getObject(ApplicationRefBase.VERSION));
    myLatestVersion = (String)ApplicationRefBase.getObject(ApplicationRefBase.VERSION);
    findHigherVersion();
  }

  private void findHigherVersion(){
    String theHighestVersion = myLatestVersion;
    MessengerUser theHighestVersionUser = null;

    for(Iterator i=myModel.getUsers().values().iterator();i.hasNext();){
      MessengerUser theUser = (MessengerUser)i.next();
      String theRemoteVersion = theUser.getVersion();

      if(compareVersion(theRemoteVersion, theHighestVersion) > 0){
        theHighestVersion = theRemoteVersion;
        theHighestVersionUser = theUser;
      }
    }


    if(compareVersion(theHighestVersion, myLatestVersion) > 0 && theHighestVersionUser != null){
      try{
        //Sleep for 10 seconds, the other application might still be starting up....
        Thread.sleep(10000);
        String theDistributionService = "rmi://"  + theHighestVersionUser.getHost() + ":" + theHighestVersionUser.getRmiPort() + "/DistributionService";
        logger.debug("Trying to locate distribution service at: " + theDistributionService);
        iDistributionService theService = (iDistributionService)Naming.lookup(theDistributionService);
        theService.getDistributionCommand().execute();
      }catch(InterruptedException e){
        logger.error("Could not sleep", e);
      } catch (MalformedURLException e) {
        logger.error("Error occured on receiving update", e);
      } catch (RemoteException e) {
        logger.error("Error occured on receiving update", e);
      } catch (NotBoundException e) {
        logger.error("Error occured on receiving update", e);
      }
      
      
      try{
        Message theMessage = new Message();
        theMessage.setFrom(myModel.getUser().getId());
        theMessage.addTo(theHighestVersionUser.getId());
        theMessage.setMessage("send file " + JAR_FILE_NAME);
        theMessage.setTechnicalMessage(true);
        myModel.sendMessage(theMessage);
        myLatestVersion = theHighestVersion;
      } catch(RemoteException e){
        logger.error("An error occured while sending technical message", e);
      }
    }



  }

  private class MapObserver implements Observer{

    public synchronized void update(Observable o, Object arg) {
//      if(arg.equals(MessengerClientService.USER_CHANGED)){
//        findHigherVersion();
//      }
    }
  }

  protected File extractLocalPath(DataFile aFile){
    if(aFile.getFileName().equals(JAR_FILE_NAME)){
      //return new File(aFile.getFileName());
      return new File(JAR_FILE_NAME + ".new");
    }
    return super.extractLocalPath(aFile);
  }

  private static int compareVersion(String aVersion1, String aVersion2){
    String[] theVersion1 = explode(aVersion1, ".");
    String[] theVersion2 = explode(aVersion2, ".");
    if(theVersion1.length != theVersion2.length) return 0;

    for(int i=0;i<theVersion1.length;i++){  
      if(Integer.parseInt(theVersion1[i]) > Integer.parseInt(theVersion2[i])) return 1;
      else if(Integer.parseInt(theVersion1[i]) < Integer.parseInt(theVersion2[i])) return -1;
    }
    return 0;
  }

  private static String[] explode(String aString, String aSeperator){
    StringTokenizer theTokenizer = new StringTokenizer(aString, aSeperator);
    String[] theTokens = new String[theTokenizer.countTokens()];
    int i=0;
    while(theTokenizer.hasMoreTokens()){
      theTokens[i++] = theTokenizer.nextToken();
    }
    return theTokens;
  }

  public static void main(String args[]){
    System.out.println(compareVersion("1.0.0", "1.0.1"));
    System.out.println(compareVersion("1.0.2", "1.0.1"));
    System.out.println(compareVersion("1.1.1", "1.0.10"));
    System.out.println(compareVersion("2.1.1", "1.20.10"));
  }

}

