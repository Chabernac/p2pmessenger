package chabernac.application;

public class Event {
  private String description = "";
  
  public Event(String aDescription){
    description = aDescription;
  }
  
  public String getDescription(){
    return description;
  }

}
