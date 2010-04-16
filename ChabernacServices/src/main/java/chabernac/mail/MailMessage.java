/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.mail;

import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import chabernac.io.ObjectDataSource;



/**
 * Class for easily sending a mail with attachments.  The Attachments are given with a file name and 
 * a String. The file name is just the name the attachment should have in the mail, it is NOT the 
 * physical location of the file on the application server.  In fact the content of the attachment is just
 * given as a string and is never stored in a file on the application server. 
 *
 *
 * @version v1.0.0      3-feb-2004
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 3-feb-2004 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class MailMessage {
  private static final String CHARACATER_SET = "iso-8859-1";
    
  //private static  Logger          logger           = Logger.getLogger(WMMailMessage.class);
  private String myHost        = null;
  private String myFrom        = null;
  private String[] myReplyTo     = null;
  private String[] myTo        = null;
  private String[] myCc        = null;
  private String[] myBcc        = null;
  private String mySubject     = "";
  private Vector myAttachments = null;
  private String myText        = null;
  private String myContentType = null;//"text/plain";
  private String myMultipartType = "mixed";
  
  
  public MailMessage(String host, String from, String[] to){
    this(host, from, to, null, "");
  }

  public MailMessage(String host, String from, String[] to, String[] cc){
    this(host, from, to, cc, "");
  }
  
  public MailMessage(String host, String from, String[] to, String[] cc, String aSubject){
    myHost = host;
    myFrom = from;
    myTo = to; 
    myCc = cc;
    mySubject = aSubject;
    myAttachments = new Vector();
  }
  
  public void send() throws MessagingException{
    Properties props = System.getProperties();
    props.put("mail.smtp.host", myHost);
    Session session = Session.getDefaultInstance(props, null);
    MimeMessage theMessage = new MimeMessage(session);
    theMessage.setFrom(new InternetAddress(myFrom));
    for(int i=0;i<myTo.length;i++){
      theMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(myTo[i]));
    }
    if(myCc != null){
      for(int i=0;i<myCc.length;i++){
        theMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(myCc[i]));
      }
    }
    if(myBcc != null){
      for(int i=0;i<myBcc.length;i++){
        theMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(myBcc[i]));
      }
    }
    if(myReplyTo != null){
      InternetAddress[] theReplyTo = new InternetAddress[myReplyTo.length];
      for(int i=0;i<myReplyTo.length;i++){
        theReplyTo[i] = new InternetAddress(myReplyTo[i]);
      }
      theMessage.setReplyTo(theReplyTo);
    }

    theMessage.setSubject(mySubject);
    
    MimeMultipart theMultipart = new MimeMultipart(myMultipartType);
    
    if(myText != null){
      MimeBodyPart theBodyPart = new MimeBodyPart();
      if(myContentType != null)theBodyPart.setContent(myText, myContentType);       
      else theBodyPart.setText(myText, CHARACATER_SET);
      theMultipart.addBodyPart(theBodyPart);
    }
     
    for(int i=0;i<myAttachments.size();i++){
      Attachment theAttachment = (Attachment)myAttachments.elementAt(i);
      MimeBodyPart theBodyPart = new MimeBodyPart();
      if(theAttachment.getFile() != null) theBodyPart.setFileName(theAttachment.getFile());
      switch(theAttachment.getType()){
        case Attachment.VIRTUALFILE:{
          if(theAttachment.getContentType() != null){
            theBodyPart.setContent(theAttachment.getContent(), theAttachment.getContentType());
          } else {
            theBodyPart.setText(theAttachment.getContent(), CHARACATER_SET);
          }
          break;  
        }
        case Attachment.LOCALFILE:{
          DataSource source = new FileDataSource(theAttachment.getContent());
          theBodyPart.setDataHandler(new DataHandler(source));
          break;  
        }
        case Attachment.VIRTUALOBJECTFILE:{
          DataSource source = new ObjectDataSource(theAttachment.getObject());
          theBodyPart.setDataHandler(new DataHandler(source));
          break;  
        }
      }
      theMultipart.addBodyPart(theBodyPart);
    }
    
    theMessage.setContent(theMultipart);
    Transport.send(theMessage);
    
  }
  
  /**
   * Declare an attachment for this mail.  
   * The given filename is the name the attachment will haven in the mail.
   * So it is not the physical location of the file.
   * 
   * @param aFileName Name that the attachment will have in the mail.
   * @param aContent content of the attachment.
   */
  public void addAttachemnt(String aFileName, String aContent){
    myAttachments.addElement(new Attachment(aFileName, aContent, Attachment.VIRTUALFILE));
  }
  
  /**
   * Declare an attachment for this mail.  
   * The given filename is the name the attachment will haven in the mail.
   * aFileName can be NULL
   * So it is not the physical location of the file.
   * 
   * @param aFileName Name that the attachment will have in the mail.
   * @param aContent content of the attachment.
   * @param aContentType content type of the given text, e.g. text/plain, text/html, ...
   */
  
  public void addAttachemnt(String aFileName, String aContent, String aContentType){
      myAttachments.addElement(new Attachment(aFileName, aContent, Attachment.VIRTUALFILE, aContentType));
  }
  
  /**
   * Declare an attachment for this mail.
   * The given filename is the name the attachment will haven in the mail.
   * The content is the name of file on local system.
   * 
   * 
   * @param aFileName Name that the attachment will have in the mail.
   * @param aContent Name of the file on the local system.
   */
  public void addSystemAttachemnt(String aFileName, String aContent){
    myAttachments.addElement(new Attachment(aFileName, aContent, Attachment.LOCALFILE));
  }
  
  /**
   * Declare an attachment for this mail.
   * The given filename is the name the attachment will haven in the mail.
   * The content is the url to the nfs file
   * 
   * 
   * @param aFileName Name that the attachment will have in the mail.
   * @param aContent url to the nfs file
   * @param aNfsUid 
   * @param aNfsGuid 
   */
  public void addNfsAttachemnt(String aFileName, String aContent, int aNfsUid, int aNfsGuid){
    myAttachments.addElement(new Attachment(aFileName, aContent, aNfsUid, aNfsGuid));
  }
  
  /**
   * Declare an attachment for this mail.
   * The given filename is the name the attachment will haven in the mail.
   * The content an object that will be serialized
   * 
   * 
   * @param aFileName Name that the attachment will have in the mail.
   * @param anObject the object that will be serialized and attached to the mail
   */
  public void addAttachemnt(String aFileName, Object anObject){
    myAttachments.addElement(new Attachment(aFileName, anObject));
  }
  
  /**
   * set the content of the mail.
   * 
   * @param aText content of the mail. 
   */
  public void setText(String aText){ myText = aText; }
  public String getText(){ return myText; }
  public void setSubject(String aSubject){ mySubject = aSubject; }
  public String getSubject(){ return mySubject; }
  public void setContentType(String aContentType){ myContentType = aContentType; }
  public String getContentType(){ return myContentType; }
  public void setMultipartType(String aContentType){ myMultipartType = aContentType; }
  public String getMultipartType(){ return myMultipartType; }
  public void setReplyTo(String[] aReplyTo){ myReplyTo = aReplyTo; }
  public String[] getReplyTo(){ return myReplyTo; }
  public void setBcc(String[] aBcc){ myBcc = aBcc; }
  public String[] getBcc(){ return myBcc; }
  

  public String[] getCc() {
    return myCc;
  }

  public void setCc(String[] anCc) {
    myCc = anCc;
  }

  public String getFrom() {
    return myFrom;
  }

  public void setFrom(String anFrom) {
    myFrom = anFrom;
  }

  public String getHost() {
    return myHost;
  }

  public void setHost(String anHost) {
    myHost = anHost;
  }

  public String[] getTo() {
    return myTo;
  }

  public void setTo(String[] anTo) {
    myTo = anTo;
  }
  
  
  /**
   * Inner class for storing attachment information
   * 3 Attachment types exist:
   * 
   * VIRUTALFILE: The file is not stored anywhere
   * LOCALFILE: The file is stored on the local system (application server)
   * NFSFILE: The file is stored on a NFS server. 
   *   
   */
  private class Attachment{
    public static final int VIRTUALFILE = 0;
    public static final int LOCALFILE   = 1;
    public static final int NFSFILE     = 2;
    public static final int VIRTUALOBJECTFILE     = 3;
    
    
    private String myFile = null;
    private String myContent = null;
    private String myContentType = null;
    private int myType;
    private int myNfsUid = -1;
    private int myNfsGuid = -1;
    private Object myObject = null;
    
    public Attachment(String aFile, String aContent, int aNfsUid, int aNfsGuid){
      this(aFile, aContent, NFSFILE);
      myNfsUid = aNfsUid;
      myNfsGuid = aNfsGuid;
    }
    
    public Attachment(String aFile, String aContent, int aType){
      this(aFile, aContent, aType, null);
    }
    
    public Attachment(String aFile, Object anObject){
      myType = VIRTUALOBJECTFILE;
      myFile = aFile;
      myObject = anObject;
    }
    
    public Attachment(String aFile, String aContent, int aType, String aContentType){
      myFile = aFile;
      myContent = aContent;
      myType = aType;
      myContentType = aContentType;
    }
    
    public String getContent(){ return myContent; }
    public String getContentType(){ return myContentType; }
    public String getFile(){ return myFile; }
    public int getType(){ return myType; }
    public int getNfsUid(){ return myNfsUid; }
    public int getNfsGuid(){ return myNfsGuid; }
    public Object getObject() { return myObject; }
    public void setObject(Object anObject) { myObject = anObject; }
    
  }


}
