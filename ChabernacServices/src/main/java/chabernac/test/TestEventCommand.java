package chabernac.test;

import chabernac.command.Command;
import chabernac.command.EventCommandRegistry;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.CommandEvent;

public class TestEventCommand {
  public static void main(String args[]){
    EventCommandRegistry.register("test0", new Command(){ 
      public void execute(){
        System.out.println("Hello world");
      }
    });
    
    EventCommandRegistry.register("test1", new Command(){ 
      public void execute(){
        System.out.println("Hello world 2!");
      }
    });
    
    for(int i=0;i<10;i++){  
      ApplicationEventDispatcher.fireEvent(new CommandEvent("test" + i % 2));
    }
  }
  
   
}
