/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class FileTransferHandler extends TransferHandler {
  private static final long serialVersionUID = -7401483816439989501L;
  private static final Logger LOGGER = Logger.getLogger(FileTransferHandler.class);
  private final String myPeerId;
  private final P2PFacade myFacade;

  public FileTransferHandler( P2PFacade aFacade, String aPeerId ) {
    super();
    myPeerId = aPeerId;
    myFacade = aFacade;
  }
  
  public boolean importData(JComponent comp, Transferable t) {
    DataFlavor[] theDataFlavors = t.getTransferDataFlavors();
    try {
      for(int i=0;i<theDataFlavors.length;i++){
        if(theDataFlavors[i].equals(DataFlavor.javaFileListFlavor)){

          List theList = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
          for(int j=0;j<theList.size();j++){
            try {
              myFacade.sendFileAsync( (File)theList.get(j), myPeerId);
              myFacade.showFileTransferOverView();
            } catch ( P2PFacadeException e ) {
              LOGGER.error("could not send file", e);
            }
          }
        } 
      }
    } catch (UnsupportedFlavorException e) {
      LOGGER.error("Invalid drop item", e);
      return false;
    } catch (IOException e) {
      LOGGER.error("Invalid drop item", e);
      return false;
    }
    return false;
  }

  public boolean canImport(JComponent comp,
                           DataFlavor[] transferFlavors) {
    for(int i=0;i<transferFlavors.length;i++){
      LOGGER.debug(transferFlavors[i].getClass().toString());
      if(transferFlavors[i].equals(DataFlavor.javaFileListFlavor)) {
        return true;
      }
    }

    return (getFlavor(transferFlavors) != null);
  }

  public int getSourceActions(JComponent c) {
    return NONE;
  }

  private DataFlavor getFlavor(DataFlavor[] flavors) {
    if (flavors != null) {
      for (int counter = 0; counter < flavors.length; counter++) {
        if (flavors[counter].equals(DataFlavor.stringFlavor)) {
          return flavors[counter];
        }
      }
    }
    return null;
  }
}
