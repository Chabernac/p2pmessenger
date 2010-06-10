  package chabernac.test;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

import chabernac.chat.Message;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.event.MessageReceivedEvent;
import chabernac.util.Tools;

public class RegisterUser {

  /**
   * @param args
   */
  public static void main(String[] args) {
    try{
      long time = System.currentTimeMillis();

      final MessengerUser theUser = new MessengerUser();
      theUser.setFirstName("" + time);
      theUser.setLastName("");
      theUser.setHost(InetAddress.getLocalHost().getHostAddress());
      theUser.setRmiPort(Tools.findUnusedLocalPort());
      theUser.setUserName("User" + time);
      theUser.setVersion("1.0.0");
      theUser.setStatus(MessengerUser.ONLINE);

      System.out.println("Registering rmi service at: " + theUser.getRmiPort());


      LocateRegistry.createRegistry(theUser.getRmiPort());

      Thread.sleep(1000);

      final MessengerClientService theService = new MessengerClientService(theUser, "localhost", 54213);
      theService.setSendLocal(false);

      theService.register();

      ApplicationEventDispatcher.addListener(new iEventListener(){

        public void eventFired(Event anEvent) {
          try{
            if(anEvent instanceof MessageReceivedEvent){
              Message theMessage = ((MessageReceivedEvent)anEvent).getMessage();
              System.out.println("Message received: " + theMessage.getMessage());
              Message theNewMessage = theMessage.reply(theUser.getUserName());
              System.out.println("Envelop: " + theNewMessage.getEnvelop());
              theNewMessage.setMessage("helo! I'm not a real person");
              Thread.sleep(1000);
              theService.sendMessage(theNewMessage);
              
              
              theNewMessage = theMessage.reply(theUser.getUserName());
              theNewMessage.setMessage("helo again! I'm not a real person");
              Thread.sleep(500);
              theService.sendMessage(theNewMessage);
              
              
              theNewMessage = theMessage.reply(theUser.getUserName());
              theNewMessage.setMessage("helo again to! I'm not a real person");
              Thread.sleep(500);
              theService.sendMessage(theNewMessage);
              
              theNewMessage = theMessage.reply(theUser.getUserName());
              theNewMessage.setMessage("helo again you too! I'm not a real person");
              Thread.sleep(500);
              theService.sendMessage(theNewMessage);
              
              
              /*
              for(int i=0;i<10;i++){
	              theNewMessage = theMessage.reply(theUser.getUserName());
	              theNewMessage.setMessage("easteregg matrix fullscreen");
	              Thread.sleep(2000);
	              theService.sendMessage(theNewMessage);
              }
              */
              
              
            }
          }catch(Exception e){
            e.printStackTrace();
          }
        }

      }, MessageReceivedEvent.class);

      Thread.sleep(1000);

      Map theMap = theService.getUsers();

      System.out.println("Users: " + theMap.size());

      Thread.sleep(100000);

      theService.unregister();

      Thread.sleep(50000);
      
    }catch(Exception e){
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

}
