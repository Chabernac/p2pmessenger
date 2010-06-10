/*
 * Created on 13-jan-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.test;

import java.rmi.Naming;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.iMessengerClientService;

public class TestMessage {
  private static Logger logger = Logger.getLogger(TestMessage.class);

  public static void main(String[] args) {
    MessengerUser theUser = new MessengerUser();
    theUser.setHost("localhost");
    theUser.setRmiPort(4733);
    String theServiceURL = "rmi://"  + theUser.getHost() + ":" + theUser.getRmiPort() + "/MessengerClientService";
    try {
      final iMessengerClientService theClientService = (iMessengerClientService)Naming.lookup(theServiceURL);
      
      Thread.sleep(2000);
      
      for(int i=0;i<10;i++){
        new Thread(new Runnable(){
          public void run(){
            try{
              Message theMessage = new Message();
              theMessage.setSendTime(System.currentTimeMillis());
              theMessage.setFrom("dtge719");
              theMessage.addTo("dgch804");
              theMessage.addTo("abcdefg qlsmfjd qsmldfj qlmf qslmdf ");
              theMessage.setMessage("test: " + System.currentTimeMillis());
              //theMessage.addAttachment(new File("C:\\data\\Projects\\Tasksheduler2\\images\\calendar2.gif"));
              theMessage.prepareAttachments();
              theClientService.acceptMessage(theMessage);
              System.out.println("message received");
            }catch(Exception e){
              e.printStackTrace();
            }
          }
        }).start();
        Thread.sleep(500);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }


  }


}
