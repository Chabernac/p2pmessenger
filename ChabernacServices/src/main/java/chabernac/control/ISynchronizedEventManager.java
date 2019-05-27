package chabernac.control;

public interface ISynchronizedEventManager {

    void addSyncronizedEventListener( iSynchronizedEvent synchronizedKeyCommand );

    void removeSyncronizedEventListener( iSynchronizedEvent synchronizedKeyCommand );

}
