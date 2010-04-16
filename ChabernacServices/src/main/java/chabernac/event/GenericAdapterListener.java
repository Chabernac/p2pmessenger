/**
 * Copyright (c) 2009 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.event;

public class GenericAdapterListener<T extends Event> implements iEventListener {
    private iEventListener< T > myListener = null;

    public GenericAdapterListener ( iEventListener< T > anEventListener ) {
        myListener = anEventListener;
    }

    /* (non-Javadoc)
     * @see chabernac.event.iEventListener#eventFired(chabernac.event.Event)
     */
    @Override
    public void eventFired( Event aEvt ) {
        myListener.eventFired( (T) aEvt );
    }

}
