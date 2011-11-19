package chabernac.protocol.routing;

import java.io.Serializable;

public class Event implements Serializable{
  private static final long serialVersionUID = -6405120129459523172L;
  private final String myId;
  private final String myData;
  
  public Event(String anId, String anData) {
    super();
    myId = anId;
    myData = anData;
  }
  public String getId() {
    return myId;
  }
  public String getData() {
    return myData;
  }
}
