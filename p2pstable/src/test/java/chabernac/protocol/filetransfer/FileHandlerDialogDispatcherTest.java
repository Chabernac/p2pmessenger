/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.testingutils.DeliveryReportCollector;
import chabernac.testingutils.FileTools;

public class FileHandlerDialogDispatcherTest extends TestCase {
  public void testFileHandlerDialogDispatcher() throws P2PFacadeException, InterruptedException, IOException, ExecutionException{
    if(true) return;
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );

    int theNrOfFiles = 5;
    File[] theFiles = new File[theNrOfFiles];
    for(int i=0;i<theNrOfFiles;i++){
      theFiles[i] = new File("test" + i + ".txt");
      FileTools.createFile( theFiles[i], (i + 5) * 10000000 );
    }

    try{
      iFileHandler theFilehandler = new FileHandlerDialogDispatcher();
      theFacade2.setFileHandler( theFilehandler );
      
      ExecutorService theExecutorService = Executors.newFixedThreadPool( 5 );
      List< Future<Boolean> > theFutureList = new ArrayList< Future< Boolean > >();
      for(int i=0;i<theFiles.length;i++){
        theFutureList.add( theFacade1.sendFile( theFiles[i], theFacade2.getPeerId(), theExecutorService) );
        Thread.sleep( 5000 );
      }
      
      for(Future<Boolean> theFuture : theFutureList){
        assertTrue( theFuture.get() );
      }
      
      Thread.sleep(5000);
      
    } finally{
      theFacade1.stop();
      theFacade2.stop();
      for(int i=0;i<theFiles.length;i++){
        theFiles[i].delete();
      }
    }
  }
  
  

}
