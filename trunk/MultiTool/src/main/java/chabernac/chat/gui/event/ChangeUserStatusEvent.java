package chabernac.chat.gui.event;

import chabernac.event.Event;

public class ChangeUserStatusEvent extends Event {
	private int myOldStatus = -1;
	private int myNewStatus = -1;
	
	public ChangeUserStatusEvent(int aNewStatus){
		this(-1, aNewStatus);
	}

	public ChangeUserStatusEvent(int anOldStatus, int aNewStatus) {
		super("Status changed event");
		myOldStatus = anOldStatus;
		myNewStatus = aNewStatus;
	}

	public int getOldStatus() {
		return myOldStatus;
	}

	public void setOldStatus(int anOldStatus) {
		myOldStatus = anOldStatus;
	}

	public int getNewStatus() {
		return myNewStatus;
	}

	public void setNewStatus(int anNewStatus) {
		myNewStatus = anNewStatus;
	}
}
