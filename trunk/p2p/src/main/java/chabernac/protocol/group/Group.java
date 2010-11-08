/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.group;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import chabernac.protocol.routing.AbstractPeer;

public class Group {
  private Set<AbstractPeer> myMembers = Collections.synchronizedSet( new HashSet<AbstractPeer>() );
  
  public Group addMember(AbstractPeer aPeer){
   myMembers.add(aPeer);
   return this;
  }
  
  public Group removeMember(AbstractPeer aPeer){
    myMembers.remove( aPeer );
    return this;
  }
}
