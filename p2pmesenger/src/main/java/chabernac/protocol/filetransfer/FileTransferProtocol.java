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
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.Peer;
import chabernac.tools.IOTools;
import chabernac.tools.iTransferListener;

/**
 * 
 * TODO improve performance
 */
public class FileTransferProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(FileTransferProtocol.class);

  public static enum Command {FILE, WAIT_FOR_FILE};
  public static enum Response {ACCEPTED, REFUSED, UNKNOWN_COMMAND, FILE_OK, FILE_NOK, BAD_FILE_SIZE};

  private iFileHandler myFileHandler = null;
  private PipeProtocol myPipeProtocol = null;

  private Map<UUID, FileStatus> myMapping = Collections.synchronizedMap( new HashMap< UUID, FileStatus >() );

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
  public String handleCommand(long aSessionId, String anInput) {
    if(anInput.startsWith( Command.FILE.name() )){
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

  public void sendFile(File aFile, Peer aPeer) throws FileTransferException{
    String theResponse = null;
    try{
      theResponse = aPeer.send( createMessage( Command.FILE.name() + " " + aFile.getName()));
    }catch(IOException e){
      throw new FileTransferException("File transfer message could not be send to peer: '" + aPeer.getPeerId() + "'", e);
    }
    
    if(Response.REFUSED.name().equalsIgnoreCase( theResponse )){
      throw new FileTransferException("The file: '" + aFile.getName() + "' was refused by peer: '" + aPeer.getPeerId() + "'");
    } else {
      String theFileId = theResponse.split( " " )[1];
      Pipe thePipe = new Pipe(aPeer);
      thePipe.setPipeDescription( "FILE:" + theFileId + ":" + aFile.length() );
      FileInputStream theInputStream = null;
      try{
        myPipeProtocol.openPipe( thePipe );
        theInputStream =  new FileInputStream(aFile);
        IOTools.copyStream( theInputStream, thePipe.getSocket().getOutputStream());
        myPipeProtocol.closePipe( thePipe );

        theResponse = aPeer.send( createMessage( Command.WAIT_FOR_FILE.name() + " " + theFileId + " " + aFile.length()));

        if(theResponse.equalsIgnoreCase( Response.BAD_FILE_SIZE.name() )){
          throw new FileTransferException("Received file had bad file size");
        }

        if(theResponse.equalsIgnoreCase( Response.FILE_NOK.name() )){
          throw new FileTransferException("Sending file failed");
        }
      } catch ( Exception e ) {
        throw new FileTransferException("Transferring file to peer: '" + aPeer.getPeerId() + "' could not be initiated", e);
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

  @Override
  protected void stopProtocol() {
    myMapping.clear();
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

    public Status getStatus(){
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
}
