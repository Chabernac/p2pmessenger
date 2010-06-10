package chabernac.chat;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.io.DataFile;
import chabernac.messengerservice.event.MessageStatusEvent;
import chabernac.util.DefaultObservable;

public class Message implements Serializable{
	private static final long serialVersionUID = 6836476327261035871L;
	
	public static final String NOT_SEND = "NOT_SEND";
	public static final String SEND = "SEND";
	public static final String FAILED = "FAILED";
	public static final String SEND_IN_PROGRES="SEND_IN_PROGRESS";
	
	private String from = null;
	private ArrayList to = null;
	private String message = null;
	private ArrayList attachements = null;
	private ArrayList heavyWeightAttachements = null;
	private transient HashMap statuses = null;
	private boolean technicalMessage = false;
	private boolean isSend = false;
	private boolean isHiddenTo = false;
	private long sendTime = 0;
	private transient DefaultObservable myObservable = null;
  private boolean isRead = false;
	
	public Message(){
		to = new ArrayList();
		statuses = new HashMap();
		myObservable = new DefaultObservable(this);
	}
	
	public void addTo(String aUser){
		if(!to.contains(aUser))	to.add(aUser);
	}
	
	public void removeTo(String aUser){
		to.remove(aUser);
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void setTo(ArrayList to){
		this.to = to;
	}
	
	public void setMessage(String aMessage){
		this.message = aMessage;
	}
	
	public String getMessage(){
		return message;
	}
	
	public ArrayList getTo(){
		return to;
	}
	
	public boolean isTechnicalMessage() {
		return technicalMessage;
	}
	
	public void setTechnicalMessage(boolean technicalMessage) {
		this.technicalMessage = technicalMessage;
	}
	
	public boolean isSend() {
		return isSend;
	}
	
	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}
	
	public boolean isHiddenTo() {
		return isHiddenTo;
	}
	
	public void setHiddenTo(boolean isHiddenTo) {
		this.isHiddenTo = isHiddenTo;
	}
	
	public long getSendTime() {
		return sendTime;
	}
	
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	
	public boolean isRead() {
    return isRead;
  }

  public void setRead(boolean anIsRead) {
    isRead = anIsRead;
  }

  public void addAttachment(Serializable anAttachment){
		if(attachements == null) attachements = new ArrayList();
		attachements.add(anAttachment);
	}
	
	public void removeAttachment(Serializable anAttachment){
		if(attachements != null) {
			attachements.remove(anAttachment);
			if(attachements.size() == 0) attachements = null;
		}
	}
	
	public ArrayList getAttachments(){
		return attachements;
	}
	
	public void setAttachments(ArrayList anAttachments){
		attachements = anAttachments;
	}
	
	public String toString(){
		String theMessage = getEnvelop();
		theMessage += ": ";
		theMessage += message;
		return theMessage;
	}
	
	public String getEnvelop(){
		String envelop = "[" + from;
		envelop += "-->";
		if(!isHiddenTo){
			for(int i=0;i<to.size();i++){
				envelop += to.get(i);
				if(i < to.size() - 1) envelop += ",";
			}
		} else {
			envelop += "verborgen";
		}
		envelop += "]";
		return envelop;
	}
	
	public Message reply(String from){
		Message theMessage = new Message();
		theMessage.setFrom(from);
		theMessage.addTo(getFrom());
		theMessage.setTechnicalMessage(isTechnicalMessage());
		theMessage.setHiddenTo(isHiddenTo);
		return theMessage;
	}
	
	public Message replyAll(String from){
		Message theMessage = new Message();
		theMessage.setTo((ArrayList)getTo().clone());
		theMessage.addTo(getFrom());
		theMessage.setFrom(from);
		theMessage.removeTo(from);
		theMessage.setTechnicalMessage(isTechnicalMessage());
		theMessage.setHiddenTo(isHiddenTo);
		return theMessage;
	}
	
	
	public void addObserver(Observer anObserver){
		myObservable.addObserver(anObserver);
	}
	
	public void removeObserver(Observer anObserver){
		myObservable.deleteObserver(anObserver);
	}
	
	public synchronized void setStatus(String aUser, String aStatus){
    String theOldStatus = (String)statuses.get(aUser);
		statuses.put(aUser, aStatus);
		myObservable.notifyObs(aUser);
    ApplicationEventDispatcher.fireEvent(new MessageStatusEvent(this, aUser, theOldStatus, aStatus));
	}
	
	public String getStatus(String aUser){
		if(!statuses.containsKey(aUser)) return NOT_SEND;
		return (String)statuses.get(aUser);
	}
	
	public void clearStatuses(){
		statuses.clear();
	}
	
	public Object clone(){
		Message theMessage = new Message();
		theMessage.setFrom(getFrom());
		theMessage.setTo((ArrayList)getTo().clone());
		theMessage.setMessage(getMessage());
		for(int i=0;i<to.size();i++){
			String who = (String)to.get(i);
			theMessage.setStatus(who, getStatus(who));
		}
		return theMessage;
	}
	
	public boolean equals(Object anObject){
    if(anObject == null) return false;
		if(!(anObject instanceof Message)) return false;
		Message theMessage = (Message)anObject;
		if(!getFrom().equals(theMessage.getFrom())) return false;
		if(getTo().size() != theMessage.getTo().size()) return false;
		for(int i=0;i<getTo().size();i++){
			if(!getTo().get(i).equals(theMessage.getTo().get(i))) return false;
		}
		if(!getMessage().equals(theMessage.getMessage())) return false;
		//if(isSend() != theMessage.isSend()) return false;
		if( (getAttachments() == null) != (theMessage.getAttachments() == null)) return false;
		if( getAttachments() != null && theMessage.getAttachments() != null){
			for(int i=0;i<getAttachments().size();i++){
				if(!getAttachments().get(i).equals(theMessage.getAttachments().get(i)) ) return false;
			}
		}
		return true;
	}
	
	/**
	 * Replace instances of File by DataFile, in this way the content of the file is serialized as well
	 * @param aMessage
	 */
	public void prepareAttachments(){
		if(attachements != null){
			if(heavyWeightAttachements == null){
				heavyWeightAttachements = new ArrayList();
			}
			heavyWeightAttachements.clear();
			for(int i=0;i<attachements.size();i++){
				if(attachements.get(i) instanceof File){
					heavyWeightAttachements.add( DataFile.loadFromFile( (File)attachements.get(i) ) );
					attachements.remove(i);
				}
				
//				else {
//					heavyWeightAttachements.add( attachements.get(i) );
//				}
			}
		}
	}
	
	public void cleanHeavyWeightAttachments(){
		if(heavyWeightAttachements != null){
			heavyWeightAttachements.clear();
		}
	}
	
	public ArrayList getHeavyWeightAttachemnts(){
		return heavyWeightAttachements;
	}
	
	
}

