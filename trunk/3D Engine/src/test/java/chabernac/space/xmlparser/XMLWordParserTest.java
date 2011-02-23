/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.xmlparser;

import java.awt.Dimension;
import java.io.IOException;

import junit.framework.TestCase;
import chabernac.io.ClassPathResource;
import chabernac.space.World;
import chabernac.worlds.WorldFrame;

public class XMLWordParserTest extends TestCase {
  public void testReadWorld() throws XMLWordParserException, IOException, InterruptedException{
    World theWorld = new World();
    XMLWordParser theParser = new XMLWordParser( theWorld );
    theParser.read( new ClassPathResource( "/maps/world.xml" ).getInputStream() );
    
    new WorldFrame( theParser.getWorld(), theParser.getCamera(), new Dimension(400,400) );
    
    Thread.sleep( 10000 );
    
  }
  
  public static void main(String args[]) throws XMLWordParserException, IOException{
    World theWorld = new World();
    XMLWordParser theParser = new XMLWordParser( theWorld );
    theParser.read( new ClassPathResource( "/maps/world.xml" ).getInputStream() );
    
    new WorldFrame( theParser.getWorld(), theParser.getCamera(), new Dimension(400,400) );
  }
}
