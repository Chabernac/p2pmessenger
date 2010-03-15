package chabernac.protocol.pipe;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.MasterProtocol;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.UnkwownPeerException;
import chabernac.tools.IOTools;
import junit.framework.TestCase;

public class PipeProtocolTest extends TestCase {
  
  static{
    BasicConfigurator.configure();
  }
  
  public void testPipeProtocol() throws InterruptedException, IOException, UnkwownPeerException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3

    RoutingTable theRoutingTable1 = new RoutingTable("1");
    MasterProtocol theProtocol1 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable1, 10, false);
    theProtocol1.addSubProtocol( theRoutingProtocol1 );
    PipeProtocol thePipeProtocol1 = new PipeProtocol(theRoutingTable1, 5);
    theProtocol1.addSubProtocol( thePipeProtocol1 );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);


    RoutingTable theRoutingTable2 = new RoutingTable("2");
    MasterProtocol theProtocol2 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, 10, false);
    theProtocol2.addSubProtocol( theRoutingProtocol2 );
    PipeProtocol thePipeProtocol2 = new PipeProtocol(theRoutingTable2, 5);
    theProtocol2.addSubProtocol( thePipeProtocol2 );

    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingTable theRoutingTable3 = new RoutingTable("3");
    MasterProtocol theProtocol3 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol3 = new RoutingProtocol(theRoutingTable3, 10, false);
    theProtocol3.addSubProtocol( theRoutingProtocol3 );
    PipeProtocol thePipeProtocol3 = new PipeProtocol(theRoutingTable3, 5);
    //add an echo pipe listener to this pipe protocol
    EchoPipeListener thePipeListener = new EchoPipeListener();
    thePipeProtocol3.setPipeListener(thePipeListener);
    theProtocol3.addSubProtocol( thePipeProtocol3 );

    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);

    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      Thread.sleep( 5000 );

      //open a pipe from peer 1 to peer 3, it should traverse peer 2
      Pipe thePipe = new Pipe(theRoutingProtocol1.getRoutingTable().getEntryForPeer("3").getPeer());
      thePipe.setPipeDescription("Test pipe description");
      
      thePipeProtocol1.openPipe(thePipe);

      for(int i=0;i<100;i++){
        thePipe.getSocket().getOutputStream().write(i);
        thePipe.getSocket().getOutputStream().flush();
        assertEquals(i, thePipe.getSocket().getInputStream().read());
      }
      
      assertEquals("1", thePipeListener.getPipe().getPeer().getPeerId());
      assertEquals("Test pipe description", thePipeListener.getPipe().getPipeDescription());

      //now lets try to send bytes over the pipe
    }finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }

  }

  private class EchoPipeListener implements IPipeListener{
    private Pipe myPipe = null;
    
    
    @Override
    public void incomingPipe(final Pipe aPipe) {
      myPipe = aPipe;
      new Thread(new Runnable(){
        public void run(){
          try {
            IOTools.copyStream(aPipe.getSocket().getInputStream(), aPipe.getSocket().getOutputStream());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
    
    public Pipe getPipe(){
      return myPipe;
    }
  }
}
