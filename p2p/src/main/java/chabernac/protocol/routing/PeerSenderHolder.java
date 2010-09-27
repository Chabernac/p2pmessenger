package chabernac.protocol.routing;

public class PeerSenderHolder {
  private static iPeerSender myPeerSender = new PeerSender();

  public static iPeerSender getPeerSender() {
    return myPeerSender;
  }

  public static void setPeerSender(iPeerSender anMyPeerSender) {
    myPeerSender = anMyPeerSender;
  }
}
