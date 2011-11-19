/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.io;

import java.net.Socket;

import javax.swing.JOptionPane;

public class AXAFirewallTest {

  public static void main(String args[]){
    try{
      new Socket("10.240.223.8", 12700);
      System.out.println("ok!");
      JOptionPane.showMessageDialog( null, "ok" );
    }catch(Exception e){
      JOptionPane.showMessageDialog( null, e.toString() );
    }
  }
}
