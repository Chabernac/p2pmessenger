/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.text.Document;

public class Artifact<T> implements Serializable{
  private static final long serialVersionUID = 1188911172494141555L;

  public static enum Attribute{Name, Location, Author, CreationDate, ModificationDate, LastModifier};
  
  private Map<Attribute, String> myAttributes = new HashMap<Attribute, String>();
  private Set<DocumentationBase> myDocumentationBase = new HashSet<DocumentationBase>();
  private T myContent;
  private final UUID myArtifactId = UUID.randomUUID();
  
  private transient List<iArtifactListener> myListeners = new ArrayList<iArtifactListener>();
  
  public Artifact(){
   setAttribute( Attribute.Name, "new artifact" ); 
  }
  
  public void setAttribute(Attribute anAttribute, String aValue){
    myAttributes.put(anAttribute, aValue);
    for(iArtifactListener theListener : myListeners) theListener.attributeChanged( this, anAttribute );
  }
  
  public String getAttribute(Attribute anAttribute){
    return myAttributes.get(anAttribute);
  }
  
  public void addDocumentationBase(DocumentationBase aDocumentationBase){
    myDocumentationBase.add( aDocumentationBase );
  }
  
  public void removeDocumentationBase(DocumentationBase aDocumentationBase){
    myDocumentationBase.remove( aDocumentationBase );
  }
  
  public Set<DocumentationBase> getDocumentationBase(){
    return (Set<DocumentationBase>)Collections.unmodifiableSet( myDocumentationBase );
  }
  
  public void clearDocumantationBase() {
   myDocumentationBase.clear(); 
  }

  public T getContent() {
    return myContent;
  }

  public void setContent( T aContent ) {
    myContent = aContent;
  }

  public static Artifact<Document> getDocumentInstance(){
    return new Artifact<Document>();
  }
  
  public UUID getArtifactId() {
    return myArtifactId;
  }

  public static Artifact loadFromStream(InputStream anInputStream) throws IOException, ClassNotFoundException{
    ObjectInputStream theObjectInputStream = null;
    try{
      theObjectInputStream = new ObjectInputStream( anInputStream );
      return (Artifact) theObjectInputStream.readObject();
    } finally {
      if(theObjectInputStream != null){
        theObjectInputStream.close();
      }
    }
  }
  
  public void save(OutputStream anOutputStream) throws IOException{
    ObjectOutputStream theOutputStream = null;
    try{
      theOutputStream = new ObjectOutputStream(anOutputStream);
      theOutputStream.writeObject( this );
      theOutputStream.flush();
    } finally {
      if(theOutputStream != null){
        theOutputStream.close();
      }
    }
  }
  
  public void addArtifactListener(iArtifactListener aListener){
    if(myListeners == null) myListeners = new ArrayList<iArtifactListener>();
    myListeners.add(aListener);
  }
  
  public void removeArtifactListener(iArtifactListener aListener){
    myListeners.remove(aListener);
  }
}
