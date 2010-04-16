package chabernac.event;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

public class ApplicationEventDispatcherTest extends TestCase{
  
  static{
    BasicConfigurator.configure();
  }
  
	public void testApplicationEventDispatcher(){
	  
	  for(int i=0;i<400;i++){
	    ApplicationEventDispatcher.fireEvent( new Event("test event: " + i) );
	  }
	}

}
