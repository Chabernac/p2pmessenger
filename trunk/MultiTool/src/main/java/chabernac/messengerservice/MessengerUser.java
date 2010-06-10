package chabernac.messengerservice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessengerUser implements Serializable{
  private static final long serialVersionUID = -4479996926169839785L;

  public static final int OFFLINE = 0;
  public static final int ONLINE = 1;
  public static final int AWAY = 2;
  public static final int BUSSY = 3;

  private String userName = "";
  private String firstName = "";
  private String lastName = "";
  private String host = "";
  private String version = "";
  private int status = OFFLINE;
  private int rmiPort;
  private transient Date lastActiveDateTime = null;

  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getHost() {
    return host;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getShortName(){
    String theLastName = getLastName();
    char theLChar = theLastName.length() > 0 ? theLastName.charAt(0) : ' ';
    return getFirstName() + " " + theLChar;
  }

  public int getRmiPort() {
    return rmiPort;
  }
  public void setRmiPort(int rmiPort) {
    this.rmiPort = rmiPort;
  }
  public int getStatus() {
    return status;
  }
  public void setStatus(int status) {
    this.status = status;
  }
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public Date getLastActiveDateTime() {
    return lastActiveDateTime;
  }
  public void setLastActiveDateTime(Date anLastActiveDateTime) {
    lastActiveDateTime = anLastActiveDateTime;
  }
  
  public String getId(){
//    return userName + "@" + host;
    return userName;
  }
    
  public boolean equals(Object anObject){
    if(!(anObject instanceof MessengerUser)) return false;
    MessengerUser theUser = (MessengerUser)anObject;
    if(getStatus() != theUser.getStatus()) return false;
    if(!getUserName().equals(theUser.getUserName())) return false;
    if(!getFirstName().equals(theUser.getFirstName())) return false;
    if(!getLastName().equals(theUser.getLastName())) return false;
    if(!getVersion().equals(theUser.getVersion())) return false;
    if(!getHost().equals(theUser.getHost())) return false;
    if(getRmiPort() != theUser.getRmiPort())  return false;
    return true;
  }

  public String toString(){
    SimpleDateFormat theFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    
    return getFirstName() + " " + 
    getLastName() + " " + 
    " (" + getUserName() + ") " + 
    " Host: " + getHost() + " " + 
    " Port: " + getRmiPort() + " " +
    " Version: " + getVersion() + " " + 
    " Last update: " + (getLastActiveDateTime() == null ? "" : theFormat.format(getLastActiveDateTime()));
  }



}
