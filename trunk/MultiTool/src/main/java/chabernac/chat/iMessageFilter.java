package chabernac.chat;

import chabernac.messengerservice.MessengerClientService;

public interface iMessageFilter {
  public boolean messageReceived(Message aMessage, MessengerClientService aMessengerClientService);
  public boolean messageSend(Message aMessage, MessengerClientService aMessengerClientService);
}
