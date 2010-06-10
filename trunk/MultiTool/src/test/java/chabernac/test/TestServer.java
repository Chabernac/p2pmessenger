package chabernac.test;

import chabernac.messengerservice.MessengerService;

public class TestServer {
  public static void main(String args[]){
    try {
      MessengerService theService = new MessengerService(2099);
      theService.startService();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
