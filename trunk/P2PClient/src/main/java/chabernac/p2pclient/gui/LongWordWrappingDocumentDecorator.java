/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

public class LongWordWrappingDocumentDecorator implements StyledDocument {
  
  private final StyledDocument myDocument;
  
  public LongWordWrappingDocumentDecorator( StyledDocument aDocument ) {
    super();
    myDocument = aDocument;
  }

  public void addDocumentListener( DocumentListener aListener ) {
    myDocument.addDocumentListener( aListener );
  }

  public Style addStyle( String aNm, Style aParent ) {
    return myDocument.addStyle( aNm, aParent );
  }

  public void addUndoableEditListener( UndoableEditListener aListener ) {
    myDocument.addUndoableEditListener( aListener );
  }

  public Position createPosition( int aOffs ) throws BadLocationException {
    return myDocument.createPosition( aOffs );
  }

  public Color getBackground( AttributeSet aAttr ) {
    return myDocument.getBackground( aAttr );
  }

  public Element getCharacterElement( int aPos ) {
    return myDocument.getCharacterElement( aPos );
  }

  public Element getDefaultRootElement() {
    return myDocument.getDefaultRootElement();
  }

  public Position getEndPosition() {
    return myDocument.getEndPosition();
  }

  public Font getFont( AttributeSet aAttr ) {
    return myDocument.getFont( aAttr );
  }

  public Color getForeground( AttributeSet aAttr ) {
    return myDocument.getForeground( aAttr );
  }

  public int getLength() {
    return myDocument.getLength();
  }

  public Style getLogicalStyle( int aP ) {
    return myDocument.getLogicalStyle( aP );
  }

  public Element getParagraphElement( int aPos ) {
    return myDocument.getParagraphElement( aPos );
  }

  public Object getProperty( Object aKey ) {
    return myDocument.getProperty( aKey );
  }

  public Element[] getRootElements() {
    return myDocument.getRootElements();
  }

  public Position getStartPosition() {
    return myDocument.getStartPosition();
  }

  public Style getStyle( String aNm ) {
    return myDocument.getStyle( aNm );
  }

  public void getText( int aOffset, int aLength, Segment aTxt ) throws BadLocationException {
    myDocument.getText( aOffset, aLength, aTxt );
  }

  public String getText( int aOffset, int aLength ) throws BadLocationException {
    return myDocument.getText( aOffset, aLength );
  }

  public void insertString( int aOffset, String aStr, AttributeSet aA ) throws BadLocationException {
    myDocument.insertString( aOffset, aStr, aA );
  }

  public void putProperty( Object aKey, Object aValue ) {
    myDocument.putProperty( aKey, aValue );
  }

  public void remove( int aOffs, int aLen ) throws BadLocationException {
    myDocument.remove( aOffs, aLen );
  }

  public void removeDocumentListener( DocumentListener aListener ) {
    myDocument.removeDocumentListener( aListener );
  }

  public void removeStyle( String aNm ) {
    myDocument.removeStyle( aNm );
  }

  public void removeUndoableEditListener( UndoableEditListener aListener ) {
    myDocument.removeUndoableEditListener( aListener );
  }

  public void render( Runnable aR ) {
    myDocument.render( aR );
  }

  public void setCharacterAttributes( int aOffset, int aLength, AttributeSet aS, boolean aReplace ) {
    myDocument.setCharacterAttributes( aOffset, aLength, aS, aReplace );
  }

  public void setLogicalStyle( int aPos, Style aS ) {
    myDocument.setLogicalStyle( aPos, aS );
  }

  public void setParagraphAttributes( int aOffset, int aLength, AttributeSet aS, boolean aReplace ) {
    myDocument.setParagraphAttributes( aOffset, aLength, aS, aReplace );
  }
}
