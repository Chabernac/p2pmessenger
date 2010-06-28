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
  private UserInfo myUserInfo = new UserInfo();
  
  public AXALDAPUserInfoProvider(){
    myUserInfo.setStatus( Status.ONLINE );
  }

  public UserInfo getUserInfo() throws UserInfoException{
    try {
      NamingEnumeration theResult = AXALDapTools.getInstance().searchUser("axauid", System.getProperty( "user.name" ));
      while(theResult.hasMoreElements()){
        SearchResult theRes = (SearchResult)theResult.nextElement();
        Attributes theAttribs = theRes.getAttributes();
        for(NamingEnumeration theEnum = theAttribs.getAll(); theEnum.hasMoreElements();){
          BasicAttribute theAttribute = (BasicAttribute)theEnum.next();
          examinAttribute(theAttribute);
        }
        System.out.println();
      }
      return myUserInfo;
    } catch ( NamingException e ) {
      throw new UserInfoException("Could not retrieve user info from ldap", e);
    }
  }

  private void examinAttribute( BasicAttribute anAttribute ) throws NamingException {
    String theId = anAttribute.getID();
    Object theValue = anAttribute.get();
    if(theId.equalsIgnoreCase( "mail" )) myUserInfo.setEMail( theValue.toString() );
    if(theId.equalsIgnoreCase( "cn" )) myUserInfo.setName(  theValue.toString() );
    if(theId.equalsIgnoreCase( "telephoneNumber" )) myUserInfo.setTelNr( theValue.toString() );
    if(theId.equalsIgnoreCase( "axauid" )) myUserInfo.setId(  theValue.toString() );
  }

}
