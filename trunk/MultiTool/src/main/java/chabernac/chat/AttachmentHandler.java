package chabernac.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.io.DataFile;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.event.MessageReceivedEvent;

public class AttachmentHandler{
  protected MessengerClientService myModel = null;
  private static Logger logger = Logger.getLogger(AttachmentHandler.class);
  
  public AttachmentHandler(MessengerClientService aModel){
    myModel = aModel;
    ApplicationEventDispatcher.addListener(new MessageReceivedEventListener(), MessageReceivedEvent.class);
  }
  
  private void handleAttachments(Message aMessage){
	ArrayList theHeavy = aMessage.getHeavyWeightAttachemnts();
    for(int i=0;i<theHeavy.size();i++){
    	Object theHeavyObject = theHeavy.get(i);
    	Serializable theLightObject = handle(theHeavyObject);
    	if(theLightObject != null){
    	      //If handle return an object, this object is a more lightweight representation of the previous object
    	      //the object is replace to save memory e.g. DataFile is replaced with File
    		theHeavy.remove(theHeavyObject);
    		aMessage.addAttachment(theLightObject);
    	}
    }
  }
  
  private class MessageReceivedEventListener implements iEventListener{

    public void eventFired(Event anEvt) {
      MessageReceivedEvent theEvent = (MessageReceivedEvent)anEvt;
      Message theMessage = theEvent.getMessage();
      if(theMessage.getHeavyWeightAttachemnts() != null){
        handleAttachments(theMessage);
      }
    }
    
  }
  
  /*
  public synchronized void update(Observable o, Object arg) {
    if( ((String)arg).equals(MessengerClientService.MESSAGE_RECEIVED) ){
      Message theMessage = myModel.getLastMessageReceived(); 
      if(theMessage.getHeavyWeightAttachemnts() != null){
        handleAttachments(theMessage);
      }
    }
  }
  */

  private Serializable handle(Object anObject) {
    if(anObject instanceof DataFile){
      DataFile theFile = (DataFile)anObject;
      logger.debug("Data file received: " + theFile.getFileName());
      FileOutputStream theStream = null;
      try{
        File theLocalFile = extractLocalPath(theFile);
        File theParentFile = theLocalFile.getParentFile();
        if(theParentFile != null) theParentFile.mkdir();
        theStream = new FileOutputStream(theLocalFile);
        theStream.write(theFile.getData());
        //theFile.finalize();
        System.gc();
        return theLocalFile;
      } catch (FileNotFoundException e) {
        logger.error("Could not write to file: " + theFile.getFileName(), e);
      } catch (IOException e) {
        logger.error("Could not write to file: " + theFile.getFileName(), e);
      } finally {
        if(theStream != null){
          try {
            theStream.flush();
            theStream.close();
          } catch (IOException e) {
            logger.error("Could not close file", e);
          }
        }
      }
    }
    return null;
  }
  
  protected File extractLocalPath(DataFile aFile){
	  return aFile.getFile();
  }



}
