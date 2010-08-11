/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.ldapuserinfoprovider;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchResult;

import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.protocol.userinfo.UserInfo.Status;

public class AXALDAPUserInfoProvider implements iUserInfoProvider{
  
  public void fillUserInfo( UserInfo aUserInfo ) throws UserInfoException {
    if(aUserInfo.getStatus() == Status.OFFLINE){
      aUserInfo.setStatus( Status.ONLINE );
    }
    try {
      NamingEnumeration theResult = AXALDapTools.getInstance().searchUser("axauid", System.getProperty( "user.name" ));
      while(theResult.hasMoreElements()){
        SearchResult theRes = (SearchResult)theResult.nextElement();
        Attributes theAttribs = theRes.getAttributes();
        for(NamingEnumeration theEnum = theAttribs.getAll(); theEnum.hasMoreElements();){
          BasicAttribute theAttribute = (BasicAttribute)theEnum.next();
          examinAttribute(theAttribute, aUserInfo);
        }
      }
    } catch ( NamingException e ) {
      throw new UserInfoException("Could not retrieve user info from ldap", e);
    }
  }

  private void examinAttribute( BasicAttribute anAttribute, UserInfo aUserInfo ) throws NamingException {
    String theId = anAttribute.getID();
    Object theValue = anAttribute.get();
    if(theId.equalsIgnoreCase( "mail" )) aUserInfo.setEMail( theValue.toString() );
    if(theId.equalsIgnoreCase( "cn" )) aUserInfo.setName(  theValue.toString() );
    if(theId.equalsIgnoreCase( "telephoneNumber" )) aUserInfo.setTelNr( theValue.toString() );
    if(theId.equalsIgnoreCase( "axauid" )) aUserInfo.setId(  theValue.toString() );
  }
}
