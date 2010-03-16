package chabernac.protocol.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.UnkwownPeerException;
import chabernac.tools.IOTools;
import chabernac.tools.iTransferListener;

public class FileTransferProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(FileTransferProtocol.class);

  public static enum Command {FILE};
  public static enum Response {ACCEPTED, REFUSED, UNKNOWN_COMMAND};

  private iFileHandler myFileHandler = null;
  private PipeProtocol myPipeProtocol = null;

  private Map<String, File> myMapping = Collections.synchronizedMap( new HashMap< String, File >() );

  public FileTransferProtocol(PipeProtocol aPipeProtocol) {
    super("FTP");
    myPipeProtocol = aPipeProtocol;
    myPipeProtocol.addPipeListener( new FilePipeListener() );
  }

  @Override
  public String getDescription() {
    return "File Transfer Protocol";
  }

  @Override
  protected String handleCommand(long aSessionId, String anInput) {
    if(anInput.startsWith( Command.FILE.name() )){
      String theFileName = anInput.substring( Command.FILE.name().length() + 1 );

      if(myFileHandler == null){
        return Response.REFUSED.name();
      }

      File theFile = myFileHandler.acceptFile( theFileName );

      if(theFile == null){
        return Response.REFUSED.name();
      }

      myMapping.put( theFileName, theFile );

      return Response.ACCEPTED.name();            
    }

    return Response.UNKNOWN_COMMAND.name();
  }

  public void sendFile(File aFile, Peer aPeer) throws UnknownHostException, IOException, FileRefusedExcpetion, UnkwownPeerException{
    String theResponse = aPeer.send( createMessage( Command.FILE.name() + " " + aFile.getName()));
    if(Response.REFUSED.name().equalsIgnoreCase( theResponse )){
      throw new FileRefusedExcpetion("The file: '" + aFile.getName() + "' was refused by peer: '" + aPeer.getPeerId() + "'");
    } else {
      Pipe thePipe = new Pipe(aPeer);
      thePipe.setPipeDescription( "FILE:" + aFile.getName() + ":" + aFile.length() );
      myPipeProtocol.openPipe( thePipe );
      FileInputStream theInputStream = null;
      try{
        theInputStream =  new FileInputStream(aFile);
        IOTools.copyStream( theInputStream, thePipe.getSocket().getOutputStream());
      }finally{
        if(theInputStream != null){
          theInputStream.close();
        }
      }
    }
  }

  @Override
  protected void stopProtocol() {
    // TODO Auto-generated method stub
  }

  public iFileHandler getFileHandler() {
    return myFileHandler;
  }

  public void setFileHandler( iFileHandler anFileHandler ) {
    myFileHandler = anFileHandler;
  }

  private class FilePipeListener implements IPipeListener{

    @Override
    public void incomingPipe( Pipe aPipe ) {
      if(aPipe.getPipeDescription().startsWith( "FILE:" )){
        String[] theAttributes = aPipe.getPipeDescription().split( ":" );

        String theFileName = theAttributes[1];
        long theLength = Long.parseLong( theAttributes[2] );

        if(!myMapping.containsKey( theFileName )){
          LOGGER.error( "The file '" + theFileName + "' is not stored in file mapping");
          try{
            aPipe.getSocket().close();
          }catch(Exception e){}
        } else {

          File theFile = myMapping.get( theFileName );
          FileOutputStream theOut = null;
          try{
            theOut = new FileOutputStream(theFile);
            IOTools.copyStream( aPipe.getSocket().getInputStream(), theOut, new FileTransferHandler(theFile, theLength) );
            //the file is received, close the socket
            theOut.flush();
            theOut.close();
            
            myFileHandler.fileSaved( theFile );
            aPipe.getSocket().close();
          }catch(Exception e){
            if(theFile != null){
              myFileHandler.fileTransferInterrupted( theFile );
            }
            
            if(theOut != null){
              try{
                theOut.close();
              }catch(Exception e2){}
            }
            LOGGER.error( "An error occured while receiving file", e );
            if(theFile != null && theFile.exists()){
              theFile.delete();
            }
          } finally {
            if(theOut != null){
              try{
                theOut.close();
              }catch(Exception e3){}
            }
            try{
              aPipe.getSocket().close();
            }catch(Exception e){}
          }
        }
      }
    }
  }

  private class FileTransferHandler implements iTransferListener{
    private File myFile = null;
    private long myLength;

    public FileTransferHandler(File aFile, long aLength){
      myFile = aFile;
      myLength = aLength;
    }

    @Override
    public void bytesTransfered( long aNumberOfBytes ) {
      myFileHandler.fileTransfer( myFile, aNumberOfBytes, myLength );
    }
  }
}
