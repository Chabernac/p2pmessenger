/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import chabernac.io.ClassPathResource;

public class FileDataPacketPersisterProviderTest extends TestCase {

  public void testProviderPersister() throws IOException{
    File theTempFile = new File("temp.jpg");
    InputStream theInput1 = null;
    InputStream theInput2 = null;
    try{
      ClassPathResource theFile = new ClassPathResource( "chabernac/protocol/asyncfiletransfer/mars_1k_color.jpg" );
      FileDataPacketProvider theProvider = new FileDataPacketProvider( theFile.getFile(), 24 );

      FileDataPacketPersister thePersister = new FileDataPacketPersister( theTempFile, theProvider.getPacketSize(), theProvider.getNrOfPackets() );

      assertEquals( theProvider.getNrOfPackets(), thePersister.listMissingPackets().size());
      int thePacketsWritten = 0;
      while(theProvider.hasNextPacket()){
        thePersister.persistDataPacket( theProvider.getNextPacket() );
        thePacketsWritten++;
        assertEquals( theProvider.getNrOfPackets() - thePacketsWritten, thePersister.listMissingPackets().size());
      }

      thePersister.close();
      theProvider.close();
      
      assertEquals( 0, thePersister.listMissingPackets().size());

      //test if the files are equal
      theInput1 = new FileInputStream( theFile.getFile() );
      theInput2 = new FileInputStream( theTempFile );

      int theByte;
      while((theByte = theInput1.read()) != -1){
        assertEquals( theByte, theInput2.read() );
      }
    }finally{
      theTempFile.delete();
      if(theInput1 != null) theInput1.close();
      if(theInput2 != null) theInput2.close();
    }
  }
}
