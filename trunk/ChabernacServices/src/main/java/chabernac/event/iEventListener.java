
package chabernac.event;

public interface iEventListener<T extends Event> {
    public void eventFired( T evt );
}
