package chabernac.synchro;

import chabernac.event.Event;

public abstract class SynchronizedEvent extends Event implements iSynchronizableEvent {
  private boolean isRemoteEvent = false;

  public SynchronizedEvent() {
    super("Synchronized event");
  }

  public boolean isRemoteEvent() {
    return isRemoteEvent;
  }

  public void setRemoteEvent(boolean anIsRemoteEvent) {
    isRemoteEvent = anIsRemoteEvent;
  }
}
