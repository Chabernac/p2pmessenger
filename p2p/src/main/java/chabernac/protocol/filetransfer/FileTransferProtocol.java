package chabernac.protocol.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.iPeerSender;
import chabernac.tools.IOTools;
import chabernac.tools.iTransferListener;

/**
 * 
 */
public class FileTransferProtocol extends Protocol {
  public static final String ID = "FTP";
  private static Logger LOGGER = Logger.getLogger(FileTransferProtocol.class);

  public static enum Command {FILE, WAIT_FOR_FILE};
  public static enum Response {ACCEPTED, REFUSED, UNKNOWN_COMMAND, FILE_OK, FILE_NOK, BAD_FILE_SIZE};

  private iFileHandler myFileHandler = null;

  private Map<UUID, FileStatus> myMapping = Collections.synchronizedMap( new HashMap< UUID, FileStatus >() );
  private FilePipeListener myFilePipeListener = new FilePipeListener();

  public FileTransferProtocol() {
    super(ID);
  }

  @Override
  public String getDescription() {
    return "File Transfer Protocol";
  }

  @Override
  public String handleCommand(String aSessionId, String anInput) {
    if(anInput.startsWith( Command.FILE.name() )){
      //just get the pipe protocol to make sure it is there and to add the listener to it
      try{
        getPipeProtocol();

        String theFileName = anInput.substring( Command.FILE.name().length() + 1 );

        if(myFileHandler == null){
          return Response.REFUSED.name();
        }

        File theFile = myFileHandler.acceptFile( theFileName );

        if(theFile == null){
          return Response.REFUSED.name();
        }

        FileStatus theStatus = new FileStatus(theFileName, theFile);

        UUID theUID = UUID.randomUUID();
        myMapping.put( theUID, theStatus );

        return Response.ACCEPTED.name() + " " + theUID.toString();
      }catch(ProtocolException e){
        return ProtocolContainer.Response.UNKNOWN_PROTOCOL.name();
      }
    }else  if(anInput.startsWith( Command.WAIT_FOR_FILE.name() )){
      String[] theFileAttributes = anInput.split( " " );
      String theFileId = theFileAttributes[1];
      long theFileSize = Long.parseLong( theFileAttributes[2] );
      UUID theFileUUID = UUID.fromString( theFileId );
      if(!myMapping.containsKey( theFileUUID )){
        return Response.FILE_NOK.name();
      } else {
        try{
          FileStatus theFileStatus = myMapping.get( theFileUUID );
          FileStatus.Status theStatus =  theFileStatus.waitForStatus();
          if(theStatus == FileStatus.Status.OK){
            //check the file size
            long theRealLength = theFileStatus.getFile().length();
            if(theRealLength == theFileSize){
              return Response.FILE_OK.name();
            } else {
              return Response.BAD_FILE_SIZE.name();
            }
          } else {
            return Response.FILE_NOK.name();
          }
        }catch(InterruptedException e){
          return Response.FILE_NOK.name();
        }
      }
    }

    return Response.UNKNOWN_COMMAND.name();
  }

  private PipeProtocol getPipeProtocol() throws ProtocolException{
    PipeProtocol thePipeProtocol = (PipeProtocol)findProtocolContainer().getProtocol( PipeProtocol.ID );
    thePipeProtocol.addPipeListener( myFilePipeListener );
    return thePipeProtocol;
  }
  
  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }
  
  public iPeerSender getPeerSender() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getPeerSender();
  }

  public void sendFile(File aFile, String aPeerid) throws FileTransferException{
    String theResponse = null;

    ProtocolContainer theProtocolContainer = findProtocolContainer();
    AbstractPeer thePeer = null;
    try{
      thePeer = getRoutingTable().getEntryForPeer( aPeerid ).getPeer();
      MessageProtocol theMessageProtocol = (MessageProtocol)theProtocolContainer.getProtocol( "MSG" );
      Message theMessage = new Message();
      theMessage.setDestination( thePeer );
      theMessage.setMessage( createMessage( Command.FILE.name() + " " + aFile.getName()) );
      theMessage.setProtocolMessage( true );
      theMessage.setMessageTimeoutInSeconds( -1 );
      theResponse = theMessageProtocol.sendMessage( theMessage );
    }catch(ProtocolException e){
      throw new FileTransferException("Could not transfer file because message protocol is not known", e);
    } catch ( MessageException e ) {
      throw new FileTransferException("Could not transfer file because message could not be delivered", e);
    } catch ( UnknownPeerException e ) {
      throw new FileTransferException("Could not transfer file because given peer id is not known", e);
    }

    if(!theResponse.startsWith( Response.ACCEPTED.name() )){
      throw new FileTransferException("The file: '" + aFile.getName() + "' was refused by peer: '" + thePeer.getPeerId() + "' with response '" + theResponse + "'");
    } else {
      String theFileId = theResponse.split( " " )[1];
      //TODO we probably should not just cast to SocketPeer
      Pipe thePipe = new Pipe((SocketPeer)thePeer);
      thePipe.setPipeDescription( "FILE:" + theFileId + ":" + aFile.length() );
      FileInputStream theInputStream = null;
      try{
        PipeProtocol thePipeProtocol = getPipeProtocol();
        thePipeProtocol.openPipe( thePipe );
        theInputStream =  new FileInputStream(aFile);
        IOTools.copyStream( theInputStream, thePipe.getSocket().getOutputStream());
        thePipeProtocol.closePipe( thePipe );

        theResponse = getPeerSender().send( thePeer, createMessage( Command.WAIT_FOR_FILE.name() + " " + theFileId + " " + aFile.length()));

        if(theResponse.equalsIgnoreCase( Response.BAD_FILE_SIZE.name() )){
          throw new FileTransferException("Received file had bad file size");
        }

        if(theResponse.equalsIgnoreCase( Response.FILE_NOK.name() )){
          throw new FileTransferException("Sending file failed");
        }
      } catch ( Exception e ) {
        throw new FileTransferException("Transferring file to peer: '" + thePeer.getPeerId() + "' could not be initiated", e);
      }finally{
        if(theInputStream != null){
          try {
            theInputStream.close();
          } catch ( IOException e ) {
          }
        }
      }
    }

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

        UUID theFileUUID = UUID.fromString( theAttributes[1] );

        long theLength = Long.parseLong( theAttributes[2] );

        if(!myMapping.containsKey( theFileUUID )){
          LOGGER.error( "The file for file id'" + theAttributes[1] + "' is not stored in file mapping");
          try{
            aPipe.getSocket().close();
          }catch(Exception e){}
        } else {

          FileStatus theFileStatus = myMapping.get( theFileUUID );
          File theFile = theFileStatus.getFile();
          FileOutputStream theOut = null;
          try{
            theOut = new FileOutputStream(theFileStatus.getFile());
            IOTools.copyStream( aPipe.getSocket().getInputStream(), theOut, new FileTransferHandler(theFile, theLength) );
            //the file is received, close the socket
            theOut.flush();
            theOut.close();

            myFileHandler.fileSaved( theFile );
            aPipe.getSocket().close();
            theFileStatus.setStatus( FileStatus.Status.OK );
          }catch(Exception e){
            theFileStatus.setStatus( FileStatus.Status.NOK );
            myMapping.remove(theFileUUID);
            if(theFileStatus.getFile() != null){
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

  private static class FileStatus{
    public static enum Status {OK, NOK};

    private String mySendFile = null;
    private File myFile = null;
    private Status myStatus = null;

    public FileStatus(String aSendFile, File aFile){
      mySendFile = aSendFile;
      myFile = aFile;
    }

    public synchronized void setStatus( Status aStatus){
      myStatus = aStatus;
      notifyAll();
    }

    public synchronized Status getStatus(){
      return myStatus;
    }

    public synchronized Status waitForStatus() throws InterruptedException{
      while(myStatus == null){
        wait();
      }
      return myStatus;
    }

    public File getFile() {
      return myFile;
    }

    public String getSendFile() {
      return mySendFile;
    }
  }

  @Override
  public void stop() {
    myMapping.clear();
  }
}
