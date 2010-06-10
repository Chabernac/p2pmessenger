package chabernac.messengerservice;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chabernac.chat.Message;

public interface iMessengerClientService extends Remote{
  public void userChanged(MessengerUser aUser) throws RemoteException;
  public MessengerUser getUser() throws RemoteException;
  public void removeUser(MessengerUser aUser) throws RemoteException;
  public void acceptMessage(Message aMessage) throws RemoteException;
}
