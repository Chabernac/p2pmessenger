package chabernac.test;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import chabernac.chat.Message;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;
import chabernac.util.Tools;

public class TestMessengerService {
  public static void main(String args[]){
    MessengerClientService theServ = null;
    try{
    Thread.sleep(4000);
    MessengerUser theUser = new MessengerUser();
    theUser.setFirstName("Jefke");
    theUser.setLastName("chabernac");
    theUser.setHost(InetAddress.getLocalHost().getHostAddress());
    theUser.setRmiPort(Tools.findUnusedLocalPort());
    theUser.setUserName("ABC123");
    theUser.setVersion("1.0.0");
    
    LocateRegistry.createRegistry(theUser.getRmiPort());
    
    final MessengerClientService theService = new MessengerClientService(theUser, "s01ap094", 2099);
    theServ = theService;
//    theService.unregister();
    //theService.register();
    
    
    for(int i=0;i<1;i++){
      final Message theMessage = new Message();
      theMessage.setFrom("ABC123");
      theMessage.addTo("dgch804");
      theMessage.setMessage("toedeloe " + i);
      new Thread(new Runnable(){
         public void run(){
           try {
            theService.sendMessage(theMessage);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
         }
      }).start();
    }
    
    }catch(Exception e){
      e.printStackTrace();
    } finally {
      if(theServ != null){
        try{
          System.out.println("Unregistering");
          theServ.unregister();
        }catch(Exception e){
          e.printStackTrace();
        }
      }
      System.exit(0);
    }
    
  }
}
